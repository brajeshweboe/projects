package com.ofbiz.importinterface.services.order;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.net.URL;
import org.apache.ofbiz.security.Security;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import java.util.Locale;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.jdom.Attribute;
import org.jdom.Document;
import org.apache.ofbiz.entity.condition.EntityOperator;
import org.apache.ofbiz.entity.util.*;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilDateTime;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilProperties;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.base.util.string.FlexibleStringExpander;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import com.ofbiz.importinterface.constants.OrderCSV;
import com.ofbiz.importinterface.constants.OrderXML;
import com.ofbiz.importinterface.exception.OrderImportException;
import com.ofbiz.utility.ImportUtility;
import com.ofbiz.utility.OmsDateTimeUtility;
import org.apache.ofbiz.entity.condition.EntityCondition;
import org.apache.ofbiz.entity.condition.EntityOperator;
import org.apache.ofbiz.entity.util.EntityUtil;


public class ImportOrderServices {
	public static final String module = ImportOrderServices.class.getName();
	private static Map<String, String> orderMap;
	private static List<Map<String, String>> orderItemMapList;
	private static Map<String, String> failedOrderMap;
	private static Delegator delegator;
	private static final String billingContactMechId = "billingContactMechId",
			shippingContactMechId = "billingContactMechId", contactMechElementList = "contactMechElementList";
	private static String userLoginId = "";
	private static String nowTimestamp = "";
	private static String warehouse, storeId;
	private static String orderExtractInputPath;
	private static String orderExtractSuccessPath;
	private static String orderExtractOutputPath;
	private static String orderExtractInputErrorPath;
	private static String orderExtractXMLSuccessPath;
	private static String dateTimeFormatWCS;
	private static boolean calculateEstimateShipDate;
	private static int estimateShipDays;
	private static String CUSTOMER_LOGON_ID = "customerLogonId";

	public static Map<String, Object> prepareAndImportOrderXML(DispatchContext ctx, Map<String, ?> context)
			throws OrderImportException {
		Debug.logInfo(" service prepareAndImportOrderXML starts", module);
		failedOrderMap = new HashMap<String, String>();
		delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		userLoginId = userLogin.getString("userLoginId");
		nowTimestamp = UtilDateTime.toDateString(UtilDateTime.nowTimestamp(), "yyyy-MM-dd HH:mm:ss.SSS");

		validateConfigurablePath(context);
		readFolder(userLogin,dispatcher);
		try {
			generateErrorStatus();
		} catch (IOException e) {
			throw new OrderImportException(e.getMessage());
		}
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("dumpDirPath", orderExtractOutputPath);
		resultMap.put("success", "success");
		Debug.logInfo(" service prepareAndImportOrderXML ends", module);
		return resultMap;
	}

	private static void validateConfigurablePath(Map<String, ?> context) throws OrderImportException {
		Debug.logInfo(" check validateConfigurablePath starts", module);
		orderExtractInputPath = UtilProperties.getPropertyValue("gaadizo.properties", "orderextract-input-path");
		Debug.log("orderExtractInputPath "+orderExtractInputPath);
		if(UtilValidate.isEmpty(orderExtractInputPath)){
			//orderExtractInputPath = "C:/Extract/Images";
			orderExtractInputPath = "E:\\bigfish-project\\demotheme\\gaadizo-erp\\gaadizo-erp\\hot-deploy\\tmp\\order_import\\order";
		}
		Debug.log("orderExtractInputPath2 "+orderExtractInputPath);
		if (!new File(orderExtractInputPath).exists()) {
			Debug.log("orderExtractInputPath3 "+orderExtractInputPath);
			throw new OrderImportException("Order Import Path is not configured");
		}

orderExtractOutputPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("gaadizo.properties", "orderextract-output-path"), context);
				Debug.log("orderExtractOutputPath1 "+orderExtractInputPath);
		if (StringUtils.isBlank(orderExtractOutputPath)) {
			throw new OrderImportException("Order import output path is not configured");
		}
		if (!new File(orderExtractOutputPath).exists()) {
			new File(orderExtractOutputPath).mkdirs();
		}

		orderExtractInputErrorPath = FlexibleStringExpander.expandString(
				UtilProperties.getPropertyValue("gaadizo.properties", "orderextract-input-error-path"), context);
		if (StringUtils.isBlank(orderExtractInputErrorPath)) {
			throw new OrderImportException("Order import input error path is not configured");
		}
		if (!new File(orderExtractInputErrorPath).exists()) {
			new File(orderExtractInputErrorPath).mkdirs();
		}
		orderExtractSuccessPath = FlexibleStringExpander
				.expandString(UtilProperties.getPropertyValue("gaadizo.properties", "orderextract-success-path"), context);
		if (StringUtils.isBlank(orderExtractSuccessPath)) {
			throw new OrderImportException("Order import success error path is not configured");
		}
		if (!new File(orderExtractSuccessPath).exists()) {
			new File(orderExtractSuccessPath).mkdirs();
		}

		warehouse = UtilProperties.getPropertyValue("gaadizo.properties", "warehouse");
		if (StringUtils.isBlank(warehouse)) {
			throw new OrderImportException("Facility id is not configured");
		}
		storeId = UtilProperties.getPropertyValue("gaadizo.properties", "storeid");
		if (StringUtils.isBlank(storeId)) {
			throw new OrderImportException("store-id is not configured");
		}
		dateTimeFormatWCS = UtilProperties.getPropertyValue("gaadizo.properties", "datetime-format-from-wcs");
		if (StringUtils.isBlank(dateTimeFormatWCS)) {
			throw new OrderImportException("dateTimeFormatWCS is not configured");
		}
		calculateEstimateShipDate = BooleanUtils
				.toBoolean(UtilProperties.getPropertyValue("gaadizo.properties", "calculate_estimated_ship_date"));
		if (calculateEstimateShipDate) {
			try {
				estimateShipDays = Integer
						.valueOf(UtilProperties.getPropertyValue("gaadizo.properties", "estimated_ship_days"));
			} catch (NumberFormatException e) {
				estimateShipDays = 0;
			}
		}
		Debug.logInfo(" check validateConfigurablePath ends", module);
	}

	private static void generateErrorStatus() throws IOException {
		final String FILE_HEADER = "orderId,Status,Comments";
		if (failedOrderMap != null && !failedOrderMap.isEmpty()) {
			FileWriter fileWriter = null;
			String filePath = orderExtractInputErrorPath + "error_.csv";
			try {
				fileWriter = new FileWriter(filePath);
				fileWriter.append(FILE_HEADER);
				fileWriter.append("\n");
				for (Map.Entry<String, String> failedOrder : failedOrderMap.entrySet()) {
					fileWriter.append(failedOrder.getKey());
					fileWriter.append(",");
					fileWriter.append("Error");
					fileWriter.append(",");
					fileWriter.append(failedOrder.getValue());
					fileWriter.append("\n");
				}
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (fileWriter != null) {
					fileWriter.close();
				}
			}
		}
	}

	private static void readFolder(GenericValue userLogin, LocalDispatcher dispatcher) throws OrderImportException {
		try {
			Debug.logInfo(" reading files starts", module);
			File folder = new File(orderExtractInputPath);
			File[] listOfFiles = null;
			if (folder != null) {
				listOfFiles = folder.listFiles();
				if (listOfFiles.length > 0) {
					for (final File file : listOfFiles) {

						if (file.isFile() && StringUtils.equalsIgnoreCase("csv", FilenameUtils.getExtension(file.getName()))) {
							Debug.logInfo("satrt reading file--" + file.getName(), module);
							readCSVAndConvertToOrderXML(file, userLogin, dispatcher);
							Debug.logInfo("end reading file--" + file.getName(), module);
						}
						try {
							File destDir = new File(orderExtractSuccessPath);
							Debug.logInfo("copying --" + file.getName() + " to order extract success path", module);
							FileUtils.copyFileToDirectory(file, destDir);
							Debug.logInfo("delete --" + file.getName() + " from order extract input path", module);
							//file.delete();
						} catch (IOException e1) {
							Debug.logError(e1.getMessage(), module);
						}
					}
				} else {
					Debug.logInfo("Nothing to read from input path", module);
				}
			}
		} catch (IOException e) {
			Debug.logError(e.getMessage(), module);
			throw new OrderImportException(e.getMessage());

		}
	}

	public static void readCSVAndConvertToOrderXML(File file, GenericValue userLogin, LocalDispatcher dispatcher) throws IOException, OrderImportException {
		// String csvFile =
		// "F:\\Sample_data\\input\\Quancious_OrderExtract_2017-12-29_11_58.csv";
		Debug.logInfo("satrt reading csv line by line--", module);
		String line = "";
		String cvsSplitBy = ",";
		List<Map<String, String>> orderList = new ArrayList<Map<String, String>>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(
					   new InputStreamReader(
			                      new FileInputStream(file), StandardCharsets.UTF_8));
			int count = 0;
			while ((line = br.readLine()) != null) {
				if (count == 0) {
					count++;
					continue;
				}
				String[] orderLine = line.split(cvsSplitBy);
				System.out.println("239 orderLine++++"+orderLine);
				orderList.add(buildDataRows(orderLine, file));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (OrderImportException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				br.close();
			}
		}
		Debug.logInfo("end reading csv line by line--", module);
		createOrder(orderList, file.getName(), userLogin,  dispatcher);
	}

	private static Map<String, String> buildDataRows(String[] s, File file) throws OrderImportException {
		Map<String, String> data = new HashMap<String, String>();
		try {
			for (OrderCSV orderCSVs : OrderCSV.values()) {
				System.out.println("258 OrderXML.values() "+OrderCSV.values());
				data.put(orderCSVs.name(), ("null".equalsIgnoreCase(s[orderCSVs.getCode()]) ? " " : StringUtils.replace(s[orderCSVs.getCode()], "~", ",")));
				// changed ~ to ',' because if csv identifies , in any of its
				// value then column get shifted to other
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			try {
				File destDir = new File(orderExtractInputErrorPath);
				FileUtils.copyFileToDirectory(file, destDir);
				file.delete();
			} catch (IOException e1) {
				throw new OrderImportException(e1.getMessage());
			}
		}
		return data;
	}

	private static void createOrder(List<Map<String, String>> orderList, String fileName,GenericValue userLogin, LocalDispatcher dispatcher) throws IOException {
		Debug.logInfo("create map for similar order from csv start--", module);
		int size = orderList.size();
		for (int i = 0; i < size; i++) {
			innerLoop: for (int j = i; j < size; j++) {
				if (j + 1 != size && orderList.get(i).get(OrderCSV.ORDER_ID.name())
						.equals(orderList.get(j + 1).get(OrderCSV.ORDER_ID.name()))) {
					continue innerLoop;
				} else {
					try {
						generateOrderWithOrderItem(i, j, orderList, fileName, userLogin, dispatcher);
					} catch (OrderImportException e) {
						failedOrderMap.put(orderMap.get(OrderCSV.ORDER_ID.name()), e.getMessage());
					}
					i = j;
					break innerLoop;
				}
			}
		}
		Debug.logInfo("create map for similar order from csv end--", module);
	}

	private static void generateOrderWithOrderItem(int startCount, int endCount, List<Map<String, String>> orderList,
			String fileName, GenericValue userLogin, LocalDispatcher dispatcher) throws IOException, OrderImportException {
		orderItemMapList = new ArrayList<Map<String, String>>();
		//Map<String, String> orderLineItem = getOrderLineItem("ProductAssoc", UtilMisc.toMap("productId", orderMap.get(OrderCSV.Service_ID.name()), OrderCSV.Vehicle_Model_ID.getOfbizColName(), orderMap.get(OrderCSV.Vehicle_Model_ID.name())));
		//orderItemMapList.add(orderLineItem);
		
		orderMap = new HashMap<String, String>();
		for (int j = startCount; j <= endCount; j++) {
			Map<String, String> orderLineItem = orderList.get(j);
			System.out.println("======orderLineItem=========="+orderLineItem+"=======");
			System.out.println("=======OrderCSV.ORDER_TOTAL.name()================="+OrderCSV.Order_Amount.name()+"===orderLineItem==="+orderLineItem.get(OrderCSV.Order_Amount.name())+"==========");
			
			orderMap.put(OrderCSV.ORDER_ID.name(), orderLineItem.get(OrderCSV.ORDER_ID.name()));
			orderMap.put(OrderXML.ORDER_STATUS.name(), "ORDER_CREATED");
			orderMap.put(OrderXML.ORDER_PLACED_DATE.name(), nowTimestamp);
			orderMap.put(OrderCSV.Order_Amount.name(), orderLineItem.get(OrderCSV.Order_Amount.name()));
			orderMap.put(OrderXML.CURRENCY.name(), "INR");
			orderMap.put(OrderCSV.Address_1.name(), orderLineItem.get(OrderCSV.Address_1.name()));
			orderMap.put(OrderCSV.Address_2.name(), orderLineItem.get(OrderCSV.Address_2.name()));
			orderMap.put(OrderCSV.Pick_up_Address1.name(), orderLineItem.get(OrderCSV.Pick_up_Address1.name()));
			orderMap.put(OrderCSV.Pick_up_Address2.name(), orderLineItem.get(OrderCSV.Pick_up_Address2.name()));
			orderMap.put(OrderCSV.CUST_EMAIL.name(), orderLineItem.get(OrderCSV.CUST_EMAIL.name()));
			orderMap.put(OrderCSV.First_Name.name(), orderLineItem.get(OrderCSV.First_Name.name()));
			orderMap.put(CUSTOMER_LOGON_ID, orderLineItem.get(OrderCSV.Customer_ID.name()));
			orderMap.put(OrderCSV.CUST_GTSN.name(), orderLineItem.get(OrderCSV.CUST_GTSN.name()));
			orderMap.putAll(orderLineItem);
		}
		getOrderLineItem();

		if (!isExist("OrderHeader", UtilMisc.toMap("orderId", orderMap.get(OrderCSV.ORDER_ID.name())))) {
			parseOrderFromMap(fileName, userLogin, dispatcher);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static List<GenericValue> getOrderLineItem() {
		List<GenericValue> productAssocList = new ArrayList();
		//orderItemMapList setting for an order
		Map<String, String> orderItemMap;
		try {
			
			productAssocList = delegator.findByAnd("ProductAssoc", UtilMisc.toMap("productId", orderMap.get(OrderCSV.Service_ID.name()), OrderCSV.Vehicle_Model_ID.getOfbizColName(), orderMap.get(OrderCSV.Vehicle_Model_ID.name())),null,false);
			int orderItemId = 10001;
			System.out.println("======productAssocList======"+productAssocList+"========");
			List<GenericValue> productVehiclePriceList = null;
			GenericValue productVehiclePrice = null;
			for(GenericValue productAssoc : productAssocList) {
				productVehiclePriceList = delegator.findByAnd("ProductVehiclePrice", UtilMisc.toMap("productId", productAssoc.getString("productIdTo"), "vehicleId", orderMap.get(OrderCSV.Vehicle_Model_ID.name())),null,false);
				System.out.println("======productVehiclePriceList======"+productVehiclePriceList.size()+"========");
				productVehiclePrice = EntityUtil.getFirst(productVehiclePriceList);
				System.out.println("======productVehiclePrice======"+productVehiclePriceList.size()+"========");
				orderItemMap = new HashMap();
				orderItemMap.put(OrderXML.PRODUCT_SKU.name(), productAssoc.getString("productIdTo"));
				orderItemMap.put(OrderXML.TYPE.name(), productAssoc.getString("productItemType"));

				orderItemMap.put(OrderXML.ORDER_ITEM_ID.name(), String.valueOf(orderItemId++));
				orderItemMap.put(OrderXML.ORDER_ITEM_STATUS.name(), "ITEM_APPROVED");
				orderItemMap.put("isFromGaadizo", "Y");
				orderItemMap.put(OrderXML.QUANTITY.name(), "1");
				//
				String workshopPrice = "0";
				String itemPrice = "0";
				String description = "";
				if(UtilValidate.isNotEmpty(productVehiclePrice)){
					 workshopPrice = productVehiclePrice.getString("workshopPrice");
					 itemPrice = productVehiclePrice.getString("price");
					 description = productVehiclePrice.getString("description");
				}
				orderItemMap.put(OrderXML.ITEM_PRICE.name(), workshopPrice);
				orderItemMap.put(OrderXML.ORDER_ITEM_TOTAL.name(), itemPrice);
				orderItemMap.put(OrderXML.PRODUCT_NAME.name(), description);
				/*orderItemMap.put(OrderXML.PRODUCT_SKU.name(), productAssoc.getString("productIdTo"));
				orderItemMap.put(OrderXML.PRODUCT_SKU.name(), productAssoc.getString("productIdTo"));
				orderItemMap.put(OrderXML.PRODUCT_SKU.name(), productAssoc.getString("productIdTo"));
				orderItemMap.put(OrderXML.PRODUCT_SKU.name(), productAssoc.getString("productIdTo"));
				orderItemMap.put(OrderXML.PRODUCT_SKU.name(), productAssoc.getString("productIdTo"));
				orderItemMap.put(OrderXML.PRODUCT_SKU.name(), productAssoc.getString("productIdTo"));
				*/
				orderItemMapList.add(orderItemMap);
			}
			
			if (UtilValidate.isEmpty(productAssocList)) {
				return new ArrayList();
			} else {
				return productAssocList;
			}
		} catch (GenericEntityException e) {
			Debug.logError(e.getMessage(), module);
		}
		return productAssocList;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static boolean isExist(String entityName, Map fields) {
		List<GenericValue> orderList;
		try {
			
			//orderList = delegator.findByAnd(entityName, fields);
			orderList = delegator.findByAnd(entityName, fields,null,false);
			
			if (UtilValidate.isEmpty(orderList)) {
				return false;
			} else {
				return true;
			}
		} catch (GenericEntityException e) {
			Debug.logError(e.getMessage(), module);
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private static void parseOrderFromMap(String fileName, GenericValue userLogin, LocalDispatcher dispatcher) throws IOException, OrderImportException {
		Debug.logInfo("start parseOrderFromMap--", module);
		String partyId = delegator.getNextSeqId("Party");
		List<Element> contents = new ArrayList<Element>();
		
		/*List<Element> productElements = getProduct();
		if (productElements != null && !productElements.isEmpty()) {
			contents.addAll(getProduct());
		}*/

		contents.addAll(getRole(null, partyId, true));
		contents.add(getOrderHeader(fileName));
		contents.add(getJobCard());
		Map<String, Object> contactMechDetails = getContactMechList(partyId);

		contents.addAll((List<Element>) contactMechDetails.get(contactMechElementList));
		contents.addAll(getRole(orderMap.get(OrderCSV.ORDER_ID.name()), partyId, false));
		

		String orderStatus = UtilProperties.getPropertyValue("gaadizo.properties",
				"ORDER_STATUS_" + StringUtils.upperCase(orderMap.get(OrderXML.ORDER_STATUS.name())).replace(" ", "_"));
		contents.add(getOrderStatus(orderMap.get(OrderCSV.ORDER_ID.name()), null, null, orderStatus, false));
		contents.addAll(getOrderItem(contactMechDetails.get(shippingContactMechId).toString(), userLogin, dispatcher));

		// new XMLOutputter().output(doc, System.out);
		Element order = new Element("entity-engine-xml");
		order.addContent(contents);

		writeXMLToFile(order);
		Debug.logInfo("start parseOrderFromMap--", module);
	}

	private static void writeXMLToFile(Element order) throws IOException {
		XMLOutputter xmlOutput = new XMLOutputter();
		// display nice
		xmlOutput.setFormat(Format.getPrettyFormat());
		Document doc = new Document(order);
		doc.setRootElement(order);
		OutputStreamWriter writer = null;
		try {
			String filePath = orderExtractOutputPath + "order_" + orderMap.get(OrderCSV.ORDER_ID.name()) + ".xml";
			 writer = new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8);
			xmlOutput.output(doc, writer);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	private static Map<String, Object> getContactMechList(String partyId) {
		List<Element> contactMechList = new ArrayList<Element>();
		
		//SHIPPING_LOCATION Details
		String contachMechIdShip = delegator.getNextSeqId("ContactMech");
		contactMechList.add(getContactMech("POSTAL_ADDRESS", contachMechIdShip, null));
		String postalCodeGeoId = delegator.getNextSeqId("Geo");
		Element postalCodeGeo = new Element("Geo");
		postalCodeGeo.setAttribute(new Attribute("geoId", postalCodeGeoId));
		postalCodeGeo.setAttribute(new Attribute("geoTypeId", "POSTAL_CODE"));
		postalCodeGeo.setAttribute(new Attribute("geoName", orderMap.get(OrderCSV.Postal_Code.name())));
		postalCodeGeo.setAttribute(new Attribute("geoCode", orderMap.get(OrderCSV.Postal_Code.name())));
		postalCodeGeo.setAttribute(new Attribute("geoSecCode", orderMap.get(OrderCSV.Postal_Code.name())));
		postalCodeGeo.setAttribute(new Attribute("abbreviation", orderMap.get(OrderCSV.Postal_Code.name())));
		contactMechList.add(postalCodeGeo);
		
		Element postalAddressShip = new Element("PostalAddress");
		postalAddressShip.setAttribute(new Attribute("contactMechId", contachMechIdShip));
		postalAddressShip.setAttribute(new Attribute("address1", orderMap.get(OrderCSV.Address_1.name())));
		postalAddressShip.setAttribute(new Attribute("address2", orderMap.get(OrderCSV.Address_2.name())));
		postalAddressShip.setAttribute(new Attribute("city", orderMap.get(OrderCSV.City.name())));
		postalAddressShip.setAttribute(new Attribute("stateProvinceGeoId", orderMap.get(OrderCSV.State_Code.name())));
		postalAddressShip.setAttribute(new Attribute("countryGeoId", "INDIA"));
		postalAddressShip.setAttribute(new Attribute("postalCodeGeoId", postalCodeGeoId));
		postalAddressShip.setAttribute(new Attribute("directions", orderMap.get(OrderCSV.Remarks.name())));
		contactMechList.add(postalAddressShip);
		contactMechList.add(getPartyContactMech(partyId, contachMechIdShip, orderMap.get(OrderCSV.Address_1.name())+" "+orderMap.get(OrderCSV.Address_2.name())));
		contactMechList.add(getPartyContactMechPurpose(partyId, contachMechIdShip, "SHIPPING_LOCATION"));
		
		//BILLING_LOCATION Details
		String contachMechIdBill = delegator.getNextSeqId("ContactMech");
		contactMechList.add(getContactMech("POSTAL_ADDRESS", contachMechIdBill, null));
		Element postalAddressBill = new Element("PostalAddress");
		postalAddressBill.setAttribute(new Attribute("contactMechId", contachMechIdBill));
		postalAddressBill.setAttribute(new Attribute("address1", orderMap.get(OrderCSV.Pick_up_Address1.name())));
		postalAddressBill.setAttribute(new Attribute("address2", orderMap.get(OrderCSV.Pick_up_Address2.name())));
		postalAddressBill.setAttribute(new Attribute("city", orderMap.get(OrderCSV.City.name())));
		postalAddressBill.setAttribute(new Attribute("stateProvinceGeoId", orderMap.get(OrderCSV.State_Code.name())));
		postalAddressBill.setAttribute(new Attribute("countryGeoId", "INDIA"));
		postalAddressBill.setAttribute(new Attribute("postalCodeGeoId", postalCodeGeoId));
		postalAddressBill.setAttribute(new Attribute("directions", orderMap.get(OrderCSV.Remarks.name())));
		contactMechList.add(postalAddressBill);
		contactMechList.add(getPartyContactMech(partyId, contachMechIdBill, orderMap.get(OrderCSV.Pick_up_Address1.name())+" "+orderMap.get(OrderCSV.Pick_up_Address2.name())));
		contactMechList.add(getPartyContactMechPurpose(partyId, contachMechIdBill, "BILLING_LOCATION"));
		
		//PRIMARY_EMAIL Details
		String customerEmail = ("null".equalsIgnoreCase(orderMap.get(OrderCSV.CUST_EMAIL.name())) ? ""
				: orderMap.get(OrderCSV.CUST_EMAIL.name()));
		String emailAddressContactMechId = delegator.getNextSeqId("ContactMech");
		contactMechList.add(getContactMech("EMAIL_ADDRESS", emailAddressContactMechId, customerEmail));
		contactMechList.add(getPartyContactMech(partyId, emailAddressContactMechId, customerEmail));
		contactMechList.add(getPartyContactMechPurpose(partyId, emailAddressContactMechId, "PRIMARY_EMAIL"));

		//PHONE_MOBILE Details
		String customerPhone = ("null".equalsIgnoreCase(orderMap.get(OrderCSV.CUST_PHONE.name())) ? ""
				: orderMap.get(OrderCSV.CUST_PHONE.name()));
		String customerPhoneContactMechId = delegator.getNextSeqId("ContactMech");
		contactMechList.add(getContactMech("TELECOM_NUMBER", customerPhoneContactMechId, customerPhone));
		contactMechList.add(getPartyContactMech(partyId, customerPhoneContactMechId, customerPhone));
		contactMechList.add(getPartyContactMechPurpose(partyId, customerPhoneContactMechId, "PHONE_MOBILE"));
		
		contactMechList.addAll(getOrderContactMech(orderMap.get(OrderCSV.ORDER_ID.name()), contachMechIdShip,
				contachMechIdBill, emailAddressContactMechId, customerPhoneContactMechId));
		Map<String, Object> contactMechDetails = new HashMap<String, Object>();
		contactMechDetails.put(billingContactMechId, contachMechIdBill);
		contactMechDetails.put(shippingContactMechId, contachMechIdShip);
		contactMechDetails.put(contactMechElementList, contactMechList);

		return contactMechDetails;
	}

	private static List<Element> getOrderContactMech(String orderId, String shippingContactMechId,
			String billingContactMechId, String emailAddressContactMechId, String phoneNumberContactMechId) {

		Element orderContactMechShipping = new Element("OrderContactMech");
		orderContactMechShipping.setAttribute(new Attribute(OrderXML.ORDER_ID.getOfbizColName(), orderId));
		orderContactMechShipping.setAttribute(new Attribute("contactMechPurposeTypeId", "SHIPPING_LOCATION"));
		orderContactMechShipping.setAttribute(new Attribute("contactMechId", shippingContactMechId));

		Element orderContactMechBilling = new Element("OrderContactMech");
		orderContactMechBilling.setAttribute(new Attribute(OrderXML.ORDER_ID.getOfbizColName(), orderId));
		orderContactMechBilling.setAttribute(new Attribute("contactMechPurposeTypeId", "BILLING_LOCATION"));
		orderContactMechBilling.setAttribute(new Attribute("contactMechId", billingContactMechId));

		Element orderContactMechEmail = new Element("OrderContactMech");
		orderContactMechEmail.setAttribute(new Attribute(OrderXML.ORDER_ID.getOfbizColName(), orderId));
		orderContactMechEmail.setAttribute(new Attribute("contactMechPurposeTypeId", "ORDER_EMAIL"));
		orderContactMechEmail.setAttribute(new Attribute("contactMechId", emailAddressContactMechId));
		
		Element orderContactMechPhone = new Element("OrderContactMech");
		orderContactMechPhone.setAttribute(new Attribute(OrderXML.ORDER_ID.getOfbizColName(), orderId));
		orderContactMechPhone.setAttribute(new Attribute("contactMechPurposeTypeId", "PHONE_MOBILE"));
		orderContactMechPhone.setAttribute(new Attribute("contactMechId", phoneNumberContactMechId));

		List<Element> contactMechList = new ArrayList<Element>();
		contactMechList.add(orderContactMechShipping);
		contactMechList.add(orderContactMechBilling);
		contactMechList.add(orderContactMechEmail);
		contactMechList.add(orderContactMechPhone);
		return contactMechList;
	}

	private static Element getContactMech(String contactMechTypeId, String contactMechId, String infoString) {
		Element contactMech = new Element("ContactMech");
		contactMech.setAttribute(new Attribute("contactMechId", contactMechId));
		contactMech.setAttribute(new Attribute("contactMechTypeId", contactMechTypeId));
		if (infoString != null) {
			contactMech.setAttribute(new Attribute("infoString", infoString));
		}
		return contactMech;
	}
	
	private static Element getPartyContactMech(String partyId, String contactMechId, String infoString) {
		Element partyContactMech = new Element("PartyContactMech");
		partyContactMech.setAttribute(new Attribute("partyId", partyId));
		partyContactMech.setAttribute(new Attribute("contactMechId", contactMechId));
		partyContactMech.setAttribute(new Attribute("fromDate", nowTimestamp));
		if (infoString != null) {
			partyContactMech.setAttribute(new Attribute("comments", infoString));
		}
		return partyContactMech;
	}
	
	private static Element getPartyContactMechPurpose(String partyId, String contactMechId, String contactMechPurposeTypeId) {
		Element partyContactMechPurpose = new Element("PartyContactMechPurpose");
		partyContactMechPurpose.setAttribute(new Attribute("partyId", partyId));
		partyContactMechPurpose.setAttribute(new Attribute("contactMechId", contactMechId));
		partyContactMechPurpose.setAttribute(new Attribute("contactMechPurposeTypeId", contactMechPurposeTypeId));
		partyContactMechPurpose.setAttribute(new Attribute("fromDate", nowTimestamp));
		return partyContactMechPurpose;
	}
	

	private static List<Element> getRole(String orderId, String partyId, boolean isPartyRole) {
		/*
		 * 
		 * 
		 * <
		 */
		Element party = null, userLogin = null;
		Element person = null;
		if (isPartyRole) {
			party = new Element("Party");
			person = new Element("Person");
			party.setAttribute(new Attribute("partyId", partyId));
			party.setAttribute(new Attribute("partyTypeId", "PERSON"));
			party.setAttribute(new Attribute("createdByUserLogin", userLoginId));

			person.setAttribute(new Attribute("firstName", orderMap.get(OrderCSV.First_Name.name())));
			person.setAttribute(new Attribute("partyId", partyId));
			person.setAttribute(new Attribute("socialSecurityNumber", orderMap.get(OrderCSV.CUST_GTSN.name())));
			// <UserLogin userLoginId="BLOG_GUEST" partyId="BLOG_GUEST"/>
			userLogin = new Element("UserLogin");
			userLogin.setAttribute(new Attribute("userLoginId", orderMap.get(CUSTOMER_LOGON_ID)));
			userLogin.setAttribute(new Attribute("partyId", partyId));

		}
		String roleElement = isPartyRole ? "PartyRole" : "OrderRole";

		Element role = new Element(roleElement);
		role.setAttribute(new Attribute("partyId", partyId));
		if (!isPartyRole) {
			role.setAttribute(new Attribute("orderId", orderId));
		}

		role.setAttribute(new Attribute("roleTypeId", "PLACING_CUSTOMER"));

		Element billToCustomerRole = new Element(roleElement);
		billToCustomerRole.setAttribute(new Attribute("partyId", partyId));
		if (!isPartyRole) {
			billToCustomerRole.setAttribute(new Attribute("orderId", orderId));
		}
		billToCustomerRole.setAttribute(new Attribute("roleTypeId", "BILL_TO_CUSTOMER"));

		Element shipToCustomerRole = new Element(roleElement);
		shipToCustomerRole.setAttribute(new Attribute("partyId", partyId));
		if (!isPartyRole) {
			shipToCustomerRole.setAttribute(new Attribute("orderId", orderId));
		}
		shipToCustomerRole.setAttribute(new Attribute("roleTypeId", "SHIP_TO_CUSTOMER"));

		Element endUserCustomerRole = new Element(roleElement);
		endUserCustomerRole.setAttribute(new Attribute("partyId", partyId));
		if (!isPartyRole) {
			endUserCustomerRole.setAttribute(new Attribute("orderId", orderId));
		}
		endUserCustomerRole.setAttribute(new Attribute("roleTypeId", "END_USER_CUSTOMER"));
		
		List<Element> partyWithRole = new ArrayList<Element>();
		if (isPartyRole) {
			partyWithRole.add(party);
			partyWithRole.add(person);
			partyWithRole.add(userLogin);
		} 
			
		partyWithRole.add(role);
		partyWithRole.add(billToCustomerRole);
		partyWithRole.add(shipToCustomerRole);
		if (!isPartyRole) {
			//BILL_FROM_VENDOR is added for invoice
			Element billFromVendor = new Element("OrderRole");
			billFromVendor.setAttribute(new Attribute("partyId", "Company"));
			billFromVendor.setAttribute(new Attribute("orderId", orderMap.get(OrderCSV.ORDER_ID.name())));
			billFromVendor.setAttribute(new Attribute("roleTypeId", "BILL_FROM_VENDOR"));
			partyWithRole.add(billFromVendor);
		}
		return partyWithRole;
	}

	private static Element getOrderHeader(String fileName) throws OrderImportException {
		Debug.logInfo("start getOrderHeader method for Order Id--" + orderMap.get(OrderCSV.ORDER_ID.name()), module);
		Element element = new Element("OrderHeader");
		element.setAttribute(
				new Attribute(OrderXML.ORDER_ID.getOfbizColName(), orderMap.get(OrderCSV.ORDER_ID.name())));
		element.setAttribute(new Attribute("jobCardId", orderMap.get(OrderCSV.Booking_ID.name())));
		element.setAttribute(new Attribute("orderTypeId", "SALES_ORDER"));
		element.setAttribute(new Attribute("orderName", orderMap.get(OrderCSV.First_Name.name())));
		System.out.println("==573======orderMap========"+orderMap+"============");
		String orderPlacedDate = null;
		try {
			orderPlacedDate = OmsDateTimeUtility
					.parseDateToTimestamp(orderMap.get(OrderXML.ORDER_PLACED_DATE.name()), nowTimestamp)
					.toString();
			element.setAttribute(new Attribute(OrderXML.ORDER_PLACED_DATE.getOfbizColName(), orderPlacedDate));
		} catch (ParseException e) {
			throw new OrderImportException(e.getMessage());
		}

		element.setAttribute(new Attribute("entryDate", UtilDateTime.nowTimestamp().toString()));
		String orderStatus = UtilProperties.getPropertyValue("gaadizo.properties",
				"ORDER_STATUS_" + StringUtils.upperCase(orderMap.get(OrderXML.ORDER_STATUS.name())).replace(" ", "_"));
		element.setAttribute(new Attribute(OrderXML.ORDER_STATUS.getOfbizColName(), orderStatus));
		if ("ROW".equalsIgnoreCase(orderMap.get(OrderXML.CURRENCY.name()))) {
			element.setAttribute(new Attribute(OrderXML.CURRENCY.getOfbizColName(), UtilProperties
					.getPropertyValue("gaadizo.properties", "CURRENCY_" + orderMap.get(OrderXML.CURRENCY.name()))));

		} else {
			element.setAttribute(
					new Attribute(OrderXML.CURRENCY.getOfbizColName(), orderMap.get(OrderXML.CURRENCY.name())));
		}
		element.setAttribute(
				new Attribute(OrderXML.ORDER_TOTAL.getOfbizColName(), orderMap.get(OrderCSV.Order_Amount.name())));
		element.setAttribute(new Attribute("productStoreId", storeId));
		element.setAttribute(new Attribute("originFacilityId", warehouse));
		element.setAttribute(new Attribute("createdByUserLogin", userLoginId));
		element.setAttribute(new Attribute("importedOrderCsvName", fileName));
		element.setAttribute(new Attribute("importedOrderCsvName", fileName));
		element.setAttribute(new Attribute("statusId", "ORDER_APPROVED"));
		Debug.logInfo("end getOrderHeader method for Order Id--" + orderMap.get(OrderCSV.ORDER_ID.name()), module);
		return element;
	}
	
	private static Element getJobCard() throws OrderImportException {
		Debug.logInfo("start getJobCard method for Order Id--" + orderMap.get(OrderCSV.ORDER_ID.name()), module);
		System.out.println("==573======orderMap========"+orderMap+"============");
		Element element = new Element("JobCard");
		element.setAttribute(new Attribute("jobCardId", orderMap.get(OrderCSV.Booking_ID.name())));
		element.setAttribute(new Attribute(OrderXML.ORDER_ID.getOfbizColName(), orderMap.get(OrderCSV.ORDER_ID.name())));
		String orderPlacedDate = null;
		try {
			orderPlacedDate = OmsDateTimeUtility
					.parseDateToTimestamp(orderMap.get(OrderXML.ORDER_PLACED_DATE.name()), nowTimestamp)
					.toString();
			element.setAttribute(new Attribute("serviceDate", orderPlacedDate));
		} catch (ParseException e) {
			throw new OrderImportException(e.getMessage());
		}

		element.setAttribute(new Attribute("vehicleId", orderMap.get(OrderCSV.Vehicle_Make_Id.name())));
		element.setAttribute(new Attribute("vehicleModelId", orderMap.get(OrderCSV.Vehicle_Model_ID.name())));
		element.setAttribute(new Attribute("vehicleModel", orderMap.get(OrderCSV.Vehicle_Model.name())));
		element.setAttribute(new Attribute("registationNumber", orderMap.get(OrderCSV.Vehicle_Regstn_No.name())));
		element.setAttribute(new Attribute("vehicleMakeId", orderMap.get(OrderCSV.Vehicle_Make_Id.name())));
		element.setAttribute(new Attribute("vehicleMake", orderMap.get(OrderCSV.Vehicle_Make.name())));
		
		element.setAttribute(new Attribute("serviceProviderId", orderMap.get(OrderCSV.Service_Provider_ID.name())));
		element.setAttribute(new Attribute("serviceProvider", orderMap.get(OrderCSV.Service_Provider.name())));
		element.setAttribute(new Attribute("serviceAvailed", orderMap.get(OrderCSV.Service_Availed.name())));
		element.setAttribute(new Attribute("oilType", orderMap.get(OrderCSV.Oil_type.name())));
		element.setAttribute(new Attribute("offerCode", orderMap.get(OrderCSV.Offer_Code.name())));
		element.setAttribute(new Attribute("offerDetails", orderMap.get(OrderCSV.Offer_Details.name())));
		element.setAttribute(new Attribute("paymentMode", orderMap.get(OrderCSV.Payment_Mode.name())));
		element.setAttribute(new Attribute("serviceTime", orderMap.get(OrderCSV.Service_Time.name())));
		
		element.setAttribute(new Attribute("customerId", orderMap.get(OrderCSV.Customer_ID.name())));
		element.setAttribute(new Attribute("firstName", orderMap.get(OrderCSV.First_Name.name())));
		element.setAttribute(new Attribute("lastName", orderMap.get(OrderCSV.Last_Name.name())));
		element.setAttribute(new Attribute("status", orderMap.get(OrderCSV.Status.name())));
		element.setAttribute(new Attribute("createdBy", userLoginId));
		element.setAttribute(new Attribute("gaadizoCredit", orderMap.get(OrderCSV.Gaadizo_Credit.name())));
		element.setAttribute(new Attribute("pickup", orderMap.get(OrderCSV.Pick_up.name())));
		Debug.logInfo("end getJobCard method for Booking_ID --" + orderMap.get(OrderCSV.Booking_ID.name()), module);
		return element;
	}

	// OrderItem orderId="30411648517"orderItemSeqId="320001
	// " orderItemTypeId="PRODUCT_ORDER_ITEM" productId="GZ-1001" isPromo="N"
	// quantity="1.0" selectedAmount="0.0" unitPrice="12.0"
	// unitListPrice="25.99" isModifiedPrice="Y" itemDescription="Nan Gizmo"
	// correspondingPoId="" statusId="ITEM_APPROVED"/>

	private static List<Element> getOrderItem(String shipmentContactMechId,GenericValue userLogin, LocalDispatcher dispatcher) throws OrderImportException {
		List<Element> orderItemList = new ArrayList<Element>();
		Debug.logInfo("start getOrderItem method for Order Id--" + orderMap.get(OrderCSV.ORDER_ID.name()), module);
		for (Map<String, String> orderItem : orderItemMapList) {
			String orderItemSeqId = delegator.getNextSeqId("OrderItem");
			Element orderItemElement = new Element("OrderItem");
			orderItemElement.setAttribute(
					new Attribute(OrderXML.ORDER_ID.getOfbizColName(), orderMap.get(OrderCSV.ORDER_ID.name())));
			orderItemElement.setAttribute("orderItemSeqId", orderItemSeqId);
			orderItemElement.setAttribute("orderItemTypeId", "PRODUCT_ORDER_ITEM");
			orderItemElement.setAttribute("shipGroupSeqId", "00001");
			orderItemElement.setAttribute("statusId", "ITEM_APPROVED");
			String productId = "";
				productId = orderItem.get(OrderXML.PRODUCT_SKU.name());
			orderItemElement.setAttribute(new Attribute(OrderXML.PRODUCT_SKU.getOfbizColName(), productId));

			orderItemElement.setAttribute(new Attribute(OrderXML.ORDER_ITEM_ID.getOfbizColName(),
					orderItem.get(OrderXML.ORDER_ITEM_ID.name())));
			/*if (StringUtils.isBlank(orderItem.get(OrderXML.RELEASE_NUMBER.name()))) {
				throw new OrderImportException("Release number is missing");
			}
			orderItemElement.setAttribute(new Attribute(OrderXML.RELEASE_NUMBER.getOfbizColName(),
					orderItem.get(OrderXML.RELEASE_NUMBER.name())));*/

			/*String orderItemStatus = UtilProperties.getPropertyValue("gaadizo.properties", "ORDER_ITEM_STATUS_"
					+ StringUtils.upperCase(orderItem.get(OrderXML.ORDER_ITEM_STATUS.name())).replace(" ", "_"));

			orderItemElement.setAttribute(new Attribute(OrderXML.ORDER_ITEM_STATUS.getOfbizColName(), orderItemStatus));*/
			String orderItemStatus = "ITEM_APPROVED";
			orderItemElement.setAttribute(
					new Attribute("statusId", orderItemStatus));
			orderItemElement.setAttribute(
					new Attribute(OrderXML.QUANTITY.getOfbizColName(), orderItem.get(OrderXML.QUANTITY.name())));

			orderItemElement.setAttribute(
					new Attribute(OrderXML.ITEM_PRICE.getOfbizColName(), orderItem.get(OrderXML.ITEM_PRICE.name())));
			orderItemElement.setAttribute(new Attribute(OrderXML.ORDER_ITEM_TOTAL.getOfbizColName(),
					orderItem.get(OrderXML.ORDER_ITEM_TOTAL.name())));
			orderItemElement.setAttribute(new Attribute("itemDescription", orderItem.get(OrderXML.PRODUCT_NAME.name())));

			orderItemElement.setAttribute(new Attribute("productTypeTypeId", orderItem.get(OrderXML.TYPE.name()).toUpperCase()));

			orderItemList.add(orderItemElement);
			orderItemList.add(getOrderStatus(orderMap.get(OrderCSV.ORDER_ID.name()), orderItemSeqId, null,
					orderItemStatus, true));
			orderItemList.addAll(getCarrierShipmentMethodAndOrderAdjustment(null, shipmentContactMechId, null, null));
			
			Element orderItemShipGroupAssocM = new Element("OrderItemShipGroupAssoc");
			orderItemShipGroupAssocM.setAttribute(
					new Attribute(OrderXML.ORDER_ID.getOfbizColName(), orderMap.get(OrderXML.ORDER_ID.name())));
			orderItemShipGroupAssocM.setAttribute(new Attribute("orderItemSeqId", orderItemSeqId));
			orderItemShipGroupAssocM.setAttribute(new Attribute("shipGroupSeqId", "00001"));
			orderItemShipGroupAssocM.setAttribute(
					new Attribute(OrderXML.QUANTITY.getOfbizColName(), orderItem.get(OrderXML.QUANTITY.name())));
			orderItemList.add(orderItemShipGroupAssocM);
			
			
			if(UtilValidate.isNotEmpty(orderMap.get(OrderCSV.Service_Provider_ID.name()))){
				
			    /*Map<String, Object> ctx = new HashMap<String, Object>();
	            ctx.put("productId", productId);
	            ctx.put("facilityId", orderMap.get(OrderCSV.Service_Provider_ID.name()));
	            ctx.put("orderItemSeqId", orderItemSeqId);
	            ctx.put("shipGroupSeqId", "00001");
	            ctx.put("orderId", orderMap.get(OrderCSV.ORDER_ID.name()));
	            ctx.put("quantity", orderItem.get(OrderXML.QUANTITY.name()));
	            ctx.put("requireInventory", "N");
	            ctx.put("reserveOrderEnumId", "INVRO_FIFO_REC");
	          //  Map<String, Object> map = dispatcher.runSync("reserveProductInventory", ctx);
*/				try{
					List<EntityCondition> findBasedOnFields = new ArrayList<EntityCondition>();
					findBasedOnFields.add(EntityCondition.makeCondition("facilityId", orderMap.get(OrderCSV.Service_Provider_ID.name())));
					findBasedOnFields.add(EntityCondition.makeCondition("productId", productId));
					List<GenericValue> inventoryItems = EntityQuery.use(delegator).from("InventoryItem").where(findBasedOnFields).orderBy("-datetimeReceived").queryList();
					Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
					if(UtilValidate.isNotEmpty(inventoryItems) && inventoryItems.size() > 0){
						BigDecimal itemAQtyTotalInServiceCenter = BigDecimal.ZERO;
						for(GenericValue inventoryItem : inventoryItems){
							itemAQtyTotalInServiceCenter.add(inventoryItem.getBigDecimal("availableToPromiseTotal"));
						}
							/*if(itemAQtyTotalInServiceCenter.compare(orderItem.get(OrderXML.QUANTITY.name())) < 0){
								//if inventory 0 for selected product and SC, create reservation in the system for selected product with SC 
								//if(inventoryItem.getBigDecimal("availableToPromiseTotal").compare(remainingQty) >= 0){
									
								    ModelService modelService = dispatcher.getDispatchContext().getModelService("createInventoryItemDetail");
								    Map<String, Object> ctx = modelService.makeValid(context, "IN");
						           // ctx.put("inventoryItemId", inventoryItem.getString("inventoryItemId"));
						            ctx.put("effectiveDate", UtilDateTime.nowTimestamp());
						            ctx.put("orderId", orderMap.get(OrderCSV.ORDER_ID.name()));
						            ctx.put("availableToPromiseDiff", remainingQty.negate());
						            ctx.put("quantityOnHandDiff", BigDecimal.ZERO);
						            Map<String, Object> map = dispatcher.runSync("createInventoryItemDetail", ctx);
						            remainingQty = remainingQty.subtract(inventoryItem.getBigDecimal("availableToPromiseTotal"));
								//} 
							} else {*/
								BigDecimal remainingQty = new BigDecimal(orderItem.get(OrderXML.QUANTITY.name()));
								for(GenericValue inventoryItem : inventoryItems){
									
									//remainingQty = orderItem.get(OrderXML.QUANTITY.name());
									if(remainingQty.compareTo((BigDecimal)inventoryItem.get("availableToPromiseTotal")) >= 0){
									   // ModelService modelService = dispatcher.getDispatchContext().getModelService("createInventoryItemDetail");
									    /*Map<String, Object> ctxVG =new HashMap<String, Object>();
									    ctxVG.put("inventoryItemId", inventoryItem.getString("inventoryItemId"));
									    ctxVG.put("effectiveDate", UtilDateTime.nowTimestamp());
									    ctxVG.put("orderId", orderMap.get(OrderCSV.ORDER_ID.name()));
									    ctxVG.put("availableToPromiseDiff", remainingQty.negate());
									    ctxVG.put("quantityOnHandDiff", BigDecimal.ZERO);
									    orderItemList.add(orderItemShipGroupAssocM);*/
									    Element invItemDetailCtx = new Element("InventoryItemDetail");
									    String inventoryItemDetailSeqId = delegator.getNextSeqId("InventoryItemDetail");
									    invItemDetailCtx.setAttribute(new Attribute("inventoryItemDetailSeqId", inventoryItemDetailSeqId));
									    invItemDetailCtx.setAttribute(new Attribute("inventoryItemId", inventoryItem.getString("inventoryItemId")));
									    invItemDetailCtx.setAttribute(new Attribute("effectiveDate", nowTimestamp.toString()));
									    invItemDetailCtx.setAttribute(new Attribute("orderId", orderMap.get(OrderCSV.ORDER_ID.name())));
									    invItemDetailCtx.setAttribute(new Attribute("availableToPromiseDiff", remainingQty.negate().toString()));
									    invItemDetailCtx.setAttribute(new Attribute("quantityOnHandDiff", BigDecimal.ZERO.toString()));
									    orderItemList.add(invItemDetailCtx);
										
							            //Map<String, Object> map = dispatcher.runSync("createInventoryItemDetail", ctxVG);
							            remainingQty = remainingQty.subtract(inventoryItem.getBigDecimal("availableToPromiseTotal"));
									} else {
										if(inventoryItem.getBigDecimal("availableToPromiseTotal").compareTo(BigDecimal.ZERO) > 0){
											/*remainingQty = remainingQty.subtract(inventoryItem.getBigDecimal("availableToPromiseTotal"));
										    Map<String, Object> ctxGV = new HashMap<String, Object>();
										    ctxGV.put("inventoryItemId", inventoryItem.getString("inventoryItemId"));
										    ctxGV.put("effectiveDate", UtilDateTime.nowTimestamp());
										    ctxGV.put("orderId", orderMap.get(OrderCSV.ORDER_ID.name()));
								            
										    ctxGV.put("availableToPromiseDiff", inventoryItem.getBigDecimal("availableToPromiseTotal").negate());
										    ctxGV.put("quantityOnHandDiff", BigDecimal.ZERO);
								            Map<String, Object> map = dispatcher.runSync("createInventoryItemDetail", ctxGV);*/
											 Element invItemDetailCtx = new Element("InventoryItemDetail");
											 String inventoryItemDetailSeqNId = delegator.getNextSeqId("InventoryItemDetail");
											 
											 invItemDetailCtx.setAttribute(new Attribute("inventoryItemDetailSeqId", inventoryItemDetailSeqNId));
											    invItemDetailCtx.setAttribute(new Attribute("inventoryItemId", inventoryItem.getString("inventoryItemId")));
											    invItemDetailCtx.setAttribute(new Attribute("effectiveDate", nowTimestamp.toString()));
											    invItemDetailCtx.setAttribute(new Attribute("orderId", orderMap.get(OrderCSV.ORDER_ID.name())));
											    invItemDetailCtx.setAttribute(new Attribute("availableToPromiseDiff", inventoryItem.getBigDecimal("availableToPromiseTotal").negate().toString()));
											    invItemDetailCtx.setAttribute(new Attribute("quantityOnHandDiff", BigDecimal.ZERO.toString()));
											    orderItemList.add(invItemDetailCtx);
									    } 
									}
									Element orderItemShipGrpInvResGV = new Element("OrderItemShipGrpInvRes");
								    orderItemShipGrpInvResGV.setAttribute(
											new Attribute("orderId", orderItem.get(OrderXML.ORDER_ID.name())));
								    orderItemShipGrpInvResGV.setAttribute(new Attribute("shipGroupSeqId","00001"));
								    orderItemShipGrpInvResGV.setAttribute(new Attribute("orderItemSeqId", orderItemSeqId));

								    orderItemShipGrpInvResGV.setAttribute(new Attribute("inventoryItemId", inventoryItem.getString("inventoryItemId")));
								    orderItemShipGrpInvResGV.setAttribute(new Attribute("reserveOrderEnumId", "INVRO_FIFO_REC"));
								    orderItemShipGrpInvResGV.setAttribute(new Attribute("quantity", orderItem.get(OrderXML.QUANTITY.name())));
								    orderItemShipGrpInvResGV.setAttribute(new Attribute("reservedDatetime", nowTimestamp.toString()));
								    orderItemShipGrpInvResGV.setAttribute(new Attribute("createdDatetime", nowTimestamp.toString()));
								    orderItemShipGrpInvResGV.setAttribute(new Attribute("promisedDatetime", nowTimestamp.toString()));
								    orderItemShipGrpInvResGV.setAttribute(new Attribute("priority", "2"));
								    orderItemList.add(orderItemShipGrpInvResGV);
									
								}
							//}
					} else {
						//if inventory 0 for selected product and SC, create reservation in the system for selected product with SC 
						    String newInventoryItemId = delegator.getNextSeqId("InventoryItem");
						    Element invItemGVCtx = new Element("InventoryItem");
						    invItemGVCtx.setAttribute(new Attribute("inventoryItemId", newInventoryItemId));
						    invItemGVCtx.setAttribute(new Attribute("inventoryItemTypeId", "NON_SERIAL_INV_ITEM"));
						    invItemGVCtx.setAttribute(new Attribute("productId", productId));
						    invItemGVCtx.setAttribute(new Attribute("ownerPartyId", orderMap.get(OrderCSV.Service_Provider_ID.name())));
						    invItemGVCtx.setAttribute(new Attribute("facilityId", orderMap.get(OrderCSV.Service_Provider_ID.name())));
						    invItemGVCtx.setAttribute(new Attribute("quantityOnHandTotal", BigDecimal.ZERO.toString()));
						    invItemGVCtx.setAttribute(new Attribute("availableToPromiseTotal", BigDecimal.ZERO.toString()));
						    invItemGVCtx.setAttribute(new Attribute("datetimeReceived", UtilDateTime.nowTimestamp().toString()));
						    orderItemList.add(invItemGVCtx);
						    
						    Element invItemDetailGVCtx = new Element("InventoryItemDetail");
						    String inventoryItemDetailSeqId = delegator.getNextSeqId("InventoryItemDetail");
						    invItemDetailGVCtx.setAttribute(new Attribute("inventoryItemDetailSeqId", inventoryItemDetailSeqId));
						    invItemDetailGVCtx.setAttribute(new Attribute("inventoryItemId", newInventoryItemId));
						    invItemDetailGVCtx.setAttribute(new Attribute("effectiveDate", UtilDateTime.nowTimestamp().toString()));
						    invItemDetailGVCtx.setAttribute(new Attribute("orderId", orderMap.get(OrderCSV.ORDER_ID.name())));
						    invItemDetailGVCtx.setAttribute(new Attribute("availableToPromiseDiff", new BigDecimal(orderItem.get(OrderXML.QUANTITY.name())).negate().toString()));
						    invItemDetailGVCtx.setAttribute(new Attribute("quantityOnHandDiff", BigDecimal.ZERO.toString()));
						    orderItemList.add(invItemDetailGVCtx);
						    
						    System.out.println("==573======orderMap========949==========");
						    Element orderItemShipGrpInvResGV1 = new Element("OrderItemShipGrpInvRes");
						    orderItemShipGrpInvResGV1.setAttribute(new Attribute("orderId", orderMap.get(OrderCSV.ORDER_ID.name())));
						    orderItemShipGrpInvResGV1.setAttribute(new Attribute("shipGroupSeqId","00001"));
						    orderItemShipGrpInvResGV1.setAttribute(new Attribute("orderItemSeqId", orderItemSeqId));
						    orderItemShipGrpInvResGV1.setAttribute(new Attribute("inventoryItemId", newInventoryItemId));
						    orderItemShipGrpInvResGV1.setAttribute(new Attribute("reserveOrderEnumId", "INVRO_FIFO_REC"));
						    orderItemShipGrpInvResGV1.setAttribute(new Attribute("quantity", orderItem.get(OrderXML.QUANTITY.name())));
						    orderItemShipGrpInvResGV1.setAttribute(new Attribute("quantityNotAvailable", orderItem.get(OrderXML.QUANTITY.name())));
						    orderItemShipGrpInvResGV1.setAttribute(new Attribute("reservedDatetime", nowTimestamp.toString()));
						    orderItemShipGrpInvResGV1.setAttribute(new Attribute("createdDatetime", nowTimestamp.toString()));
						    orderItemShipGrpInvResGV1.setAttribute(new Attribute("promisedDatetime", nowTimestamp.toString()));
						    orderItemShipGrpInvResGV1.setAttribute(new Attribute("priority", "2"));
						    System.out.println("==962=====orderItemShipGrpInvResGV1========"+orderItemShipGrpInvResGV1+"==========");
						    orderItemList.add(orderItemShipGrpInvResGV1);
					}
				} catch (Exception ex) {
					Debug.logError(ex.getMessage(), module);
				}
			}
			
			
			
			/*orderItemList.addAll(getProductFeatureElements(orderItem, "OrderItemAttribute", orderItemSeqId,
					orderItem.get(OrderXML.PRODUCT_SKU), null, false));*/
		}
		//orderItemList.addAll(getCarrierShipmentMethodAndOrderAdjustment(null, shipmentContactMechId, null, null));
		Debug.logInfo("end getOrderItem method for Order Id--" + orderMap.get(OrderCSV.ORDER_ID.name()), module);
		return orderItemList;
	}

	// <OrderAdjustment orderAdjustmentId="Demo1234_01"
	// orderAdjustmentTypeId="SHIPPING_CHARGES" orderId="30411648517"
	// orderItemSeqId="_NA_" shipGroupSeqId="_NA_" amount="5"
	// createdDate="2009-12-01 9:00:00.000" createdByUserLogin="admin"/>
	private static List<Element> getOrderAdjustment(Map<String, String> orderItem, String orderItemSeqId,String shipGroupSeqId) {
		Debug.logInfo("start getOrderAdjustment method for Order Id--" + orderMap.get(OrderCSV.ORDER_ID.name()),
				module);
		/*Element orderAdjustmentShipping = new Element("OrderAdjustment");
		String orderAdjustmentId = delegator.getNextSeqId("OrderAdjustment");

		orderAdjustmentShipping.setAttribute(new Attribute("orderAdjustmentId", orderAdjustmentId));
		orderAdjustmentShipping.setAttribute(new Attribute("orderAdjustmentTypeId", "SHIPPING_CHARGES"));
		orderAdjustmentShipping.setAttribute(new Attribute("orderId", orderMap.get(OrderCSV.ORDER_ID.name())));
		orderAdjustmentShipping.setAttribute(new Attribute("orderItemSeqId", orderItemSeqId));
		orderAdjustmentShipping.setAttribute(new Attribute("shipGroupSeqId", shipGroupSeqId));
		orderAdjustmentShipping.setAttribute(new Attribute("amount", orderItem.get(OrderXML.SHIPPING_CHARGES.name())));
		orderAdjustmentShipping.setAttribute(new Attribute("createdDate", nowTimestamp));
		orderAdjustmentShipping.setAttribute(new Attribute("createdByUserLogin", userLoginId));*/

		String orderAdjustmentIdForTax = delegator.getNextSeqId("OrderAdjustment");
		Element orderAdjustmentSales = new Element("OrderAdjustment");
		orderAdjustmentSales.setAttribute(new Attribute("orderAdjustmentId", orderAdjustmentIdForTax));
		orderAdjustmentSales.setAttribute(new Attribute("orderAdjustmentTypeId", "SALES_TAX"));
		orderAdjustmentSales.setAttribute(new Attribute("orderId", orderMap.get(OrderCSV.ORDER_ID.name())));
		orderAdjustmentSales.setAttribute(new Attribute("orderItemSeqId", orderItemSeqId));
		orderAdjustmentSales.setAttribute(new Attribute("shipGroupSeqId", shipGroupSeqId));
		orderAdjustmentSales.setAttribute(new Attribute("amount", "0.00"));
		//orderAdjustmentSales.setAttribute(new Attribute("amount", orderItem.get(OrderXML.TAXES_DUTIES.name())));
		orderAdjustmentSales.setAttribute(new Attribute("createdDate", nowTimestamp));
		orderAdjustmentSales.setAttribute(new Attribute("createdByUserLogin", userLoginId));
		
		/*String orderAdjustmentIdForPromotion = delegator.getNextSeqId("OrderAdjustment");
		Element orderAdjustmentPromotion = new Element("OrderAdjustment");
		orderAdjustmentPromotion.setAttribute(new Attribute("orderAdjustmentId", orderAdjustmentIdForPromotion));
		orderAdjustmentPromotion.setAttribute(new Attribute("orderAdjustmentTypeId", "PROMOTION_ADJUSTMENT"));
		orderAdjustmentPromotion.setAttribute(new Attribute("orderId", orderMap.get(OrderCSV.ORDER_ID.name())));
		orderAdjustmentPromotion.setAttribute(new Attribute("orderItemSeqId", orderItemSeqId));
		orderAdjustmentPromotion.setAttribute(new Attribute("shipGroupSeqId", shipGroupSeqId));
		orderAdjustmentPromotion.setAttribute(new Attribute("amount", orderItem.get(OrderXML.DISCOUNT.name())));
		orderAdjustmentPromotion.setAttribute(new Attribute("createdDate", nowTimestamp));
		orderAdjustmentPromotion.setAttribute(new Attribute("createdByUserLogin", userLoginId));*/

		List<Element> orderAdjustmentList = new ArrayList<Element>();
		//orderAdjustmentList.add(orderAdjustmentShipping);
		orderAdjustmentList.add(orderAdjustmentSales);
		//orderAdjustmentList.add(orderAdjustmentPromotion);
		Debug.logInfo("end getOrderAdjustment method for Order Id--" + orderMap.get(OrderCSV.ORDER_ID.name()), module);
		return orderAdjustmentList;
	}

	// <OrderStatus orderStatusId="Demo1234_03" statusId="ORDER_APPROVED"
	// orderId="30411648517" orderPaymentPreferenceId="9000"
	// statusDatetime="2009-12-01 9:00:00.000" statusUserLogin="admin"/>

	// <OrderStatus orderStatusId="Demo1234_02" statusId="ITEM_APPROVED"
	// orderId="30411648517" orderItemSeqId="320001" statusDatetime="2009-12-01
	// 9:00:00.000" statusUserLogin="admin"/>

	private static Element getOrderStatus(String orderID, String orderItemSeqId, String orderPaymentPreferenceId,
			String status, boolean isOrderItem) {
		String orderStatusSeqId = delegator.getNextSeqId("OrderStatus");
		Element orderStatus = new Element("OrderStatus");
		if (isOrderItem) {
			orderStatus.setAttribute(new Attribute("orderStatusId", orderStatusSeqId));
			orderStatus.setAttribute(new Attribute("statusId", status));
			orderStatus.setAttribute(new Attribute("orderId", orderID));
			orderStatus.setAttribute(new Attribute("orderItemSeqId", orderItemSeqId));
			orderStatus.setAttribute(new Attribute("statusDatetime", nowTimestamp));
			orderStatus.setAttribute(new Attribute("statusUserLogin", userLoginId));
		} else {
			orderStatus.setAttribute(new Attribute("orderStatusId", orderStatusSeqId));
			orderStatus.setAttribute(new Attribute("statusId", status));
			orderStatus.setAttribute(new Attribute("orderId", orderID));
			orderStatus.setAttribute(new Attribute("orderPaymentPreferenceId", ""));// TODO
																					// payment
																					// preference
			orderStatus.setAttribute(new Attribute("statusDatetime", nowTimestamp));
			orderStatus.setAttribute(new Attribute("statusUserLogin", userLoginId));
		}
		return orderStatus;

	}

	private static List<Element> getProduct() throws OrderImportException {
		// <Product productId="SV-1000" productTypeId="FINISHED_GOODS"
		// primaryProductCategoryId="SERV-001" productName="Service product"
		// internalName="Service type product" description="Service type product
		// isVirtual="N" isVariant="N"
		// createdDate="2008-12-02 12:00:00.0" createdByUserLogin="admin"

		// <ProductCategory categoryName="Demo Browse Root" from category_name
		// longDescription="Demo Catalog Primary Browse Root Category" same
		// productCategoryId="CATALOG1" upper category_name remove space with
		// under
		// productCategoryTypeId="CATALOG_CATEGORY" hardcode/>
		Debug.logInfo("start getProduct--", module);
		List<Element> elementList = new ArrayList<Element>();
		for (Map<String, String> orderItem : orderItemMapList) {
			String productId = "";
				productId = orderItem.get(OrderXML.PRODUCT_SKU.name());
			if (isExist("Product", UtilMisc.toMap("productId", productId))) {
				continue;
			}
			Element productCategory = new Element("ProductCategory");

			productCategory.setAttribute(new Attribute("categoryName", orderItem.get(OrderXML.CATEGORY_NAME.name())));
			productCategory
					.setAttribute(new Attribute("longDescription", orderItem.get(OrderXML.CATEGORY_NAME.name())));
			String categoryName = orderItem.get(OrderXML.CATEGORY_NAME.name()).toUpperCase().replaceAll(" ", "_");
			productCategory.setAttribute(new Attribute("productCategoryId", categoryName));
			productCategory.setAttribute(new Attribute("productCategoryTypeId", "CATALOG_CATEGORY"));

			Element product = new Element("Product");
			String designFeature = orderItem.get(OrderXML.TYPE.name()).toUpperCase();

			if (StringUtils.isBlank(designFeature)) {
				throw new OrderImportException(OrderXML.TYPE.name() + " can not be null");
			}

			product.setAttribute(new Attribute(OrderXML.PRODUCT_SKU.getOfbizColName(), productId));
			product.setAttribute("primaryProductCategoryId", categoryName);
			product.setAttribute(new Attribute("productTypeId", "FINISHED_GOOD"));
			product.setAttribute(new Attribute("productTypeTypeId", designFeature));
			product.setAttribute(new Attribute("productName", orderItem.get(OrderXML.PRODUCT_NAME.name())));
			product.setAttribute(new Attribute("internalName", orderItem.get(OrderXML.PRODUCT_NAME.name())));
			product.setAttribute(new Attribute("description", orderItem.get(OrderXML.PRODUCT_NAME.name())));

			product.setAttribute(new Attribute("isVirtual", "N"));
			product.setAttribute(new Attribute("isVariant", "N"));
			product.setAttribute(new Attribute("createdDate", nowTimestamp));
			product.setAttribute(new Attribute("createdByUserLogin", userLoginId));
			elementList.add(productCategory);
			elementList.add(product);
			//elementList.addAll(getProductRelatedElements(orderItem, designFeature, productId));

		}
		Debug.logInfo("end getProduct--", module);
		return elementList;

	}

	private static List<Element> getProductRelatedElements(Map<String, String> orderItemMap, String designFeature,
			String productId) {
		// <ProductFeatureCategory productFeatureCategoryId="9000"
		// description="Widget Features"/>
		Debug.logInfo("start getProductRelatedElements", module);
		List<Element> elementList = new ArrayList<Element>();
		Element productFeatureCategory = new Element("ProductFeatureCategory");
		String productFeatureCategoryId = delegator.getNextSeqId("ProductFeatureCategory");
		productFeatureCategory.setAttribute(new Attribute("productFeatureCategoryId", productFeatureCategoryId));
		productFeatureCategory.setAttribute(new Attribute("description", designFeature));
		elementList.add(productFeatureCategory);
		elementList.addAll(getProductFeatureElements(orderItemMap, "ProductFeature", productFeatureCategoryId,
				productId, designFeature, true));
		Debug.logInfo("start getProductRelatedElements", module);
		return elementList;
	}

	// <ProductFeature productFeatureId="8003"
	// productFeatureCategoryId="8000" productFeatureTypeId="DESIGN/FASHION/KIT"
	// description="MIT"/>
	// <OrderItemAttribute ORDERID="36702667686" orderItemSeqId="365022"
	// attrName="SIZE_TYPE" ATTRVALUE="VALUE FROM CSV OF SPECIFIC COLUMN" />
	private static List<Element> getProductFeatureElements(Map<String, String> orderItemMap, String elementName,
			String seqId, String productId, String designFeature, boolean isProductFeature) {
		List<Element> elementFeatureList = new ArrayList<Element>();

		String productFeatureId = "";
		for (OrderXML orderXML : OrderXML.values()) {
			if ((OrderXML.WEIGHT == orderXML || OrderXML.PRODUCT_IMAGE == orderXML)
					|| (orderXML.getCode() >= 32 && orderXML.getCode() <= 97)) {
				if ("null".equalsIgnoreCase(orderItemMap.get(orderXML.name()))
						|| StringUtils.isBlank(orderItemMap.get(orderXML.name()))
						|| "NA".equalsIgnoreCase(orderItemMap.get(orderXML.name()))) {
					continue;
				} else {
					if (isProductFeature && OrderXML.WEIGHT != orderXML && OrderXML.PRODUCT_IMAGE != orderXML
							&& OrderXML.SIZE_TYPE != orderXML
							&& OrderXML.SIZE != orderXML) {
						Element elementFeature = new Element(elementName);
						elementFeature.setAttribute(new Attribute("productFeatureId", orderXML.name()));
						productFeatureId = orderXML.name();
						elementFeature.setAttribute("productFeatureCategoryId", seqId);
						elementFeature.setAttribute("productFeatureTypeId", designFeature);
						elementFeature.setAttribute(new Attribute("description", orderItemMap.get(orderXML.name())));
						elementFeatureList.add(elementFeature);
						elementFeatureList.add(getProductFeatureAppl(orderItemMap, productFeatureId, productId));
					} else if (!isProductFeature) {
						Element elementFeature = new Element(elementName);
						elementFeature.setAttribute(new Attribute(OrderXML.ORDER_ID.getOfbizColName(),
								orderItemMap.get(OrderXML.ORDER_ID.name())));
						elementFeature.setAttribute(new Attribute("orderItemSeqId", seqId));
						elementFeature.setAttribute(new Attribute("attrName", orderXML.name()));
						elementFeature.setAttribute(new Attribute("attrValue", orderItemMap.get(orderXML.name())));
						elementFeatureList.add(elementFeature);
					}
				}
			}
		}
		return elementFeatureList;
	}

	// <ProductFeatureAppl productId="GZ-1006" productFeatureId="8000"
	// productFeatureApplTypeId="SELECTABLE_FEATURE" fromDate="2001-05-13
	// 12:00:00.0" sequenceNum="1"/>
	//
	private static Element getProductFeatureAppl(Map<String, String> orderItemMap, String productFeatureId,
			String productId) {
		Element productFeatureAppl = new Element("ProductFeatureAppl");
		productFeatureAppl.setAttribute(new Attribute("productId", productId));
		productFeatureAppl.setAttribute(new Attribute("productFeatureId", productFeatureId));
		productFeatureAppl.setAttribute(new Attribute("productFeatureApplTypeId", "STANDARD_FEATURE"));
		productFeatureAppl.setAttribute(new Attribute("fromDate", nowTimestamp));
		return productFeatureAppl;
	}

	private static List<Element> getCarrierShipmentMethodAndOrderAdjustment(Map<String, String> orderItem,
			String shipmentContactMechId, String orderItemSeqId, String productId) throws OrderImportException {
		Debug.logInfo("start getCarrierShipmentMethod", module);
		List<Element> carrierElementsList = new ArrayList<Element>();

		/*String shipmentPartyId = StringUtils.isNotBlank(orderItem.get(OrderXML.SHIP_PROVIDER.name()))
				? orderItem.get(OrderXML.SHIP_PROVIDER.name()).toUpperCase()
				: orderItem.get(OrderXML.SHIP_PROVIDER.name());

		String shipMode = orderItem.get(OrderXML.SHIP_MODE.name());
		String shipmentMethodTypeId = StringUtils.isNotBlank(shipMode) ? shipMode.replaceAll(" ", "_").toUpperCase()
				: shipMode;

		if (!isExist("Party", UtilMisc.toMap(OrderXML.SHIP_PROVIDER.getOfbizColName(), shipmentPartyId))) {
			Element shipmentparty = new Element("Party");
			shipmentparty.setAttribute(new Attribute(OrderXML.SHIP_PROVIDER.getOfbizColName(), shipmentPartyId));
			carrierElementsList.add(shipmentparty);

			Element shipmentPartyRole = new Element("PartyRole");
			shipmentPartyRole.setAttribute(new Attribute(OrderXML.SHIP_PROVIDER.getOfbizColName(), shipmentPartyId));
			shipmentPartyRole.setAttribute(new Attribute("roleTypeId", "CARRIER"));
			carrierElementsList.add(shipmentPartyRole);
		} 
		if (!isExist("CarrierShipmentMethod", UtilMisc.toMap(OrderXML.SHIP_PROVIDER.getOfbizColName(),
				shipmentPartyId, OrderXML.SHIP_MODE.getOfbizColName(), shipmentMethodTypeId))) {

			Element shipmentMethodType = new Element("ShipmentMethodType");
			shipmentMethodType.setAttribute(new Attribute("description", shipMode));
			shipmentMethodType.setAttribute(new Attribute(OrderXML.SHIP_MODE.getOfbizColName(), shipmentMethodTypeId));

			Element carrierShipmentMethod = new Element("CarrierShipmentMethod");
			carrierShipmentMethod
					.setAttribute(new Attribute(OrderXML.SHIP_PROVIDER.getOfbizColName(), shipmentPartyId));
			carrierShipmentMethod.setAttribute(new Attribute("roleTypeId", "CARRIER"));
			carrierShipmentMethod
					.setAttribute(new Attribute(OrderXML.SHIP_MODE.getOfbizColName(), shipmentMethodTypeId));
			carrierShipmentMethod.setAttribute(new Attribute("sequenceNumber", "000001"));
			carrierShipmentMethod.setAttribute(new Attribute("carrierServiceCode", "000001"));

			Element shipmentCostEstimate = new Element("ShipmentCostEstimate");
			String shipmentCostEstimateId = delegator.getNextSeqId("ShipmentCostEstimate");
			shipmentCostEstimate.setAttribute(new Attribute("shipmentCostEstimateId", shipmentCostEstimateId));
			shipmentCostEstimate.setAttribute(new Attribute("shipmentMethodTypeId", shipmentMethodTypeId));
			shipmentCostEstimate.setAttribute(new Attribute("carrierPartyId", shipmentPartyId));
			shipmentCostEstimate.setAttribute(new Attribute("carrierRoleTypeId", "CARRIER"));

			shipmentCostEstimate.setAttribute(new Attribute("productStoreId", storeId));
			shipmentCostEstimate
					.setAttribute(new Attribute("orderFlatPrice", orderItem.get(OrderXML.SHIPPING_CHARGES.name())));
			
			//prepare data for product store shipment meth for add item line 
			Element productStoreShipmentMeth = new Element("ProductStoreShipmentMeth");
			String productStoreShipmentMethId = delegator.getNextSeqId("ProductStoreShipmentMeth");
			productStoreShipmentMeth.setAttribute(new Attribute("productStoreShipMethId", productStoreShipmentMethId));
			productStoreShipmentMeth.setAttribute(new Attribute("productStoreId", storeId));
			productStoreShipmentMeth.setAttribute(new Attribute("shipmentMethodTypeId", shipmentMethodTypeId));
			productStoreShipmentMeth.setAttribute(new Attribute("partyId", shipmentPartyId));
			productStoreShipmentMeth.setAttribute(new Attribute("roleTypeId", "CARRIER"));
			
			carrierElementsList.add(shipmentMethodType);
			carrierElementsList.add(carrierShipmentMethod);
			carrierElementsList.add(shipmentCostEstimate);
			carrierElementsList.add(productStoreShipmentMeth);

		}*/

		// <CarrierShipmentMethod partyId="UPS" roleTypeId="CARRIER"
		// shipmentMethodTypeId="NEXT_DAY" sequenceNumber="1"
		// carrierServiceCode="01"/>

		Element orderItemShipGroup = new Element("OrderItemShipGroup");
		orderItemShipGroup.setAttribute(
				new Attribute(OrderXML.ORDER_ID.getOfbizColName(), orderMap.get(OrderCSV.ORDER_ID.name())));
		//String releaseNumber = StringUtils.leftPad("10000", 5, "0");
		orderItemShipGroup.setAttribute(new Attribute("shipGroupSeqId", "00001"));
		orderItemShipGroup.setAttribute(new Attribute(OrderXML.SHIP_MODE.getOfbizColName(), "STANDARD"));
		orderItemShipGroup.setAttribute(new Attribute("carrierPartyId", "_NA_"));
		orderItemShipGroup.setAttribute(new Attribute("carrierRoleTypeId", "CARRIER"));
		orderItemShipGroup.setAttribute(new Attribute("contactMechId", shipmentContactMechId));
		//orderItemShipGroup.setAttribute(new Attribute("vendorPartyId", userLogin.getString("ownerCompanyId")));
		carrierElementsList.add(orderItemShipGroup);
		/*Element orderItemShipGroupAssoc = new Element("OrderItemShipGroupAssoc");
		orderItemShipGroupAssoc.setAttribute(
				new Attribute(OrderXML.ORDER_ID.getOfbizColName(), orderMap.get(OrderXML.ORDER_ID.name())));
		orderItemShipGroupAssoc.setAttribute(new Attribute("orderItemSeqId", orderItemSeqId));
		orderItemShipGroupAssoc.setAttribute(new Attribute("shipGroupSeqId", "00001"));
		orderItemShipGroupAssoc.setAttribute(
				new Attribute(OrderXML.QUANTITY.getOfbizColName(), orderItem.get(OrderXML.QUANTITY.name())));
		String estimatedShipDate = null;
		

		carrierElementsList.add(orderItemShipGroup);
		carrierElementsList.add(orderItemShipGroupAssoc);*/
		//carrierElementsList.addAll(getInventoryElement(orderItem, productId, orderItemSeqId));
		//carrierElementsList.addAll(getOrderAdjustment(orderItem, orderItemSeqId, releaseNumber));
		Debug.logInfo("end getCarrierShipmentMethod", module);
		return carrierElementsList;
	}

	private static List<Element> getInventoryElement(Map<String, String> orderItem, String productId,
			String orderItemSeqId) {
		List<Element> elementList = new ArrayList<Element>();
		// <InventoryItem availableToPromiseTotal="1450" facilityId="FMO_FAC"
		// inventoryItemId="9001" inventoryItemTypeId="NON_SERIAL_INV_ITEM"
		// productId="12.00063.0050.00FBPB45"
		// quantityOnHandTotal="1450.000000"/>
		String inventoryItemId = delegator.getNextSeqId("InventoryItem");
		Element inventoryItem = new Element("InventoryItem");
		inventoryItem.setAttribute(new Attribute("availableToPromiseTotal", orderItem.get(OrderXML.QUANTITY.name())));
		inventoryItem.setAttribute(new Attribute("facilityId", warehouse));
		inventoryItem.setAttribute(new Attribute("inventoryItemId", inventoryItemId));
		inventoryItem.setAttribute(new Attribute("inventoryItemTypeId", "NON_SERIAL_INV_ITEM"));
		inventoryItem.setAttribute(new Attribute("productId", productId));
		inventoryItem.setAttribute(new Attribute("quantityOnHandTotal", orderItem.get(OrderXML.QUANTITY.name())));

		// <OrderItemShipGrpInvRes orderId="DEMO_B2C_AU01"
		// shipGroupSeqId="00001" orderItemSeqId="00001" inventoryItemId="9001"
		// reserveOrderEnumId="INVRO_FIFO_REC" quantity="1.0"
		// reservedDatetime="2009-12-01 9:00:00.000" createdDatetime="2009-12-01
		// 9:00:00.000" promisedDatetime="2009-12-01 9:00:00.000" priority="2"/>
		Element orderItemShipGroup = new Element("OrderItemShipGrpInvRes");
		orderItemShipGroup.setAttribute(
				new Attribute(OrderXML.ORDER_ID.getOfbizColName(), orderItem.get(OrderXML.ORDER_ID.name())));
		orderItemShipGroup.setAttribute(new Attribute("shipGroupSeqId",
				StringUtils.leftPad(orderItem.get(OrderXML.RELEASE_NUMBER.name()), 5, "0")));
		orderItemShipGroup.setAttribute(new Attribute("orderItemSeqId", orderItemSeqId));

		orderItemShipGroup.setAttribute(new Attribute("inventoryItemId", inventoryItemId));
		orderItemShipGroup.setAttribute(new Attribute("reserveOrderEnumId", "INVRO_FIFO_REC"));
		orderItemShipGroup.setAttribute(
				new Attribute(OrderXML.QUANTITY.getOfbizColName(), orderItem.get(OrderXML.QUANTITY.name())));
		orderItemShipGroup.setAttribute(new Attribute("reservedDatetime", nowTimestamp));
		orderItemShipGroup.setAttribute(new Attribute("createdDatetime", nowTimestamp));
		orderItemShipGroup.setAttribute(new Attribute("promisedDatetime", nowTimestamp));
		orderItemShipGroup.setAttribute(new Attribute("priority", "2"));

		elementList.add(inventoryItem);
		elementList.add(orderItemShipGroup);
		return elementList;
	}
	
	/*private static List<Element> getOrderPaymentPreference(){
		return null;
	}*/
	
	/*private static Element getPaymentMethod(){
		
	}*/

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, Object> importOrderInERPService(DispatchContext ctx, Map<String, ?> context)
			throws Exception {
		System.out.println("=1211===orderExtractXMLSuccessPath===1193=====");
		Debug.logInfo("start service importOrderInERPService", module);
		delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		userLoginId = userLogin.getString("userLoginId");
		nowTimestamp = UtilDateTime.toDateString(UtilDateTime.nowTimestamp(), "yyyy-MM-dd HH:mm:ss");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean errorDuringInsertion = false;
		List errorMsgs = new ArrayList();
		System.out.println("=1211===orderExtractXMLSuccessPath===1193=====");
		orderExtractXMLSuccessPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("gaadizo.properties", "orderextract-success-xml-path"), context);
		if (StringUtils.isBlank(orderExtractXMLSuccessPath)) {
			throw new OrderImportException("Order extract xml success path is not cofigured");
		}
		if (!new File(orderExtractXMLSuccessPath).exists()) {
			new File(orderExtractXMLSuccessPath).mkdirs();
		}
		System.out.println("=1211===orderExtractXMLSuccessPath======"+orderExtractXMLSuccessPath+"=====");
		
		System.out.println("==1212==context======"+context+"=====");
		try {
			Debug.logInfo("calling  service prepareAndImportOrderXML", module);
			Map prepareAndImportOrderXMLResult = dispatcher.runSync("prepareAndImportOrderXML",
					UtilMisc.toMap("userLogin", context.get("userLogin")));
			List<String> messages = new ArrayList<String>();
			Debug.logInfo(" Method insertIntoDB starts", module);

			String outputXmlFilePath = (String) prepareAndImportOrderXMLResult.get("dumpDirPath");
			System.out.println("==1379=outputXmlFilePath======"+outputXmlFilePath+"=====");
			if (!ServiceUtil.isError(prepareAndImportOrderXMLResult)) {
				Debug.logInfo("calling  service entityImportDirectory to import the xml", module);
				Map entityImportDirParams = UtilMisc.toMap("path", outputXmlFilePath, "userLogin",
						context.get("userLogin"));
System.out.println("====entityImportDirParams======"+entityImportDirParams+"=====");
				Map result = dispatcher.runSync("entityImportDirectoryForERP", entityImportDirParams);

				List<String> serviceMsg = (List) result.get("messages");
				for (String msg : serviceMsg) {
					messages.add(msg);
				}

				if (messages.size() > 0 && messages.contains("SUCCESS")) {
					moveXMLFilesFromDir(outputXmlFilePath, orderExtractXMLSuccessPath);
					Debug.logInfo("moved XMLFilesFromDir successfully", module);
				}
			}
		} catch (Exception ex) {
			errorDuringInsertion = true;
			errorMsgs.add(ex.getMessage());
			Debug.logError(ex.getMessage(), module);
			throw ex;
		}
		Debug.logInfo(" Method insertIntoDB ends", module);
		if (errorDuringInsertion) {
			return ServiceUtil.returnError(errorMsgs);
		}
		return resultMap;
	}

	private static void moveXMLFilesFromDir(String dirPath, String destDirPath) throws IOException {

		Debug.logInfo(" Method moveXMLFilesFromDir starts", module);
		File dir = new File(dirPath);
		File destDir = new File(destDirPath);
		for (File file : dir.listFiles()) {
			FileUtils.copyFileToDirectory(file, destDir);
			file.delete();
		}
		Debug.logInfo(" Method moveXMLFilesFromDir ends", module);
	}

     public static Map<String, Object> importOrderInERPServiceFromCRM(DispatchContext dctx, Map<String, Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        String stringXml = (String) context.get("orderInERPServiceFromCRM");
        System.out.println("=========orderInERPServiceFromCRM============="+stringXml+"===============");
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        
        try {
            //Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(stringXml)));
			//System.out.println("==1446=======doc============="+doc+"===============");
			
            //Element engineDocElement = UtilXml.firstChildElement(doc.getDocumentElement(), "receivables");

            //List<? extends Element> messageElementList = UtilXml.childElementList(doc.getDocumentElement(), "receivable");
         //System.out.println("==1446=======messageElementList============="+messageElementList+"===============");
            // if (UtilValidate.isNotEmpty(messageElementList)) {
            //     for (Iterator<? extends Element> i = messageElementList.iterator(); i.hasNext();) {
                     
                      
              //   }
            // }
         } catch (Exception nfe) {
             Debug.logError("Error parsing message severity: " + nfe.getMessage(), module);
         }
        return result;
    }
	
     public static Map<String, Object> importOrderFromRestAPI(DispatchContext dctx, Map<String, Object> context) {
         Map<String, Object> result = ServiceUtil.returnSuccess();
         System.out.println("===1468======context=========="+context+"==============");
        
         Delegator delegator = dctx.getDelegator();
         LocalDispatcher dispatcher = dctx.getDispatcher();
         try {
        	 System.out.println("=========orderInERPServiceFromCRM========================");
          } catch (Exception nfe) {
              Debug.logError("Error parsing message severity: " + nfe.getMessage(), module);
          }
         return result;
     }
          
}
