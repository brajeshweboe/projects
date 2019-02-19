package com.ofbiz.importinterface.services.order;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilDateTime;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilProperties;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.base.util.string.FlexibleStringExpander;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.condition.EntityCondition;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.entity.util.EntityUtil;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.ofbiz.importinterface.constants.OrderCSV;
import com.ofbiz.importinterface.constants.OrderXML;
import com.ofbiz.importinterface.exception.OrderImportException;
import com.ofbiz.utility.OmsDateTimeUtility;


public class ImportOrderServiceInERP {
	public static final String module = ImportOrderServiceInERP.class.getName();
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
    //service 3
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, Object> importOrderInERPFromJson(DispatchContext ctx, Map<String, ?> context)
			throws Exception {
		System.out.println("=1211===orderExtractXMLSuccessPath===1193=====");
		Debug.logInfo("start service importOrderInERPFromJson", module);
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

		BookingOrderPojo bookingOrderPojodata = (BookingOrderPojo)context.get("bookingOrderPojoData");
		
		if(bookingOrderPojodata!=null && "201".equals(String.valueOf(bookingOrderPojodata.getStatus())) && "success".equals(bookingOrderPojodata.getMsg())){
			Data data  = bookingOrderPojodata.getData();
			List<String> bookingIdList = data.getBooking_id_list();
			List<Booking> bookingList = data.getBooking_list();
			Debug.logInfo("bookingIdList : "+bookingIdList, module);
			Debug.logInfo("bookingList.size() : "+bookingList.size(), module);
			
			for(Booking booking : bookingList){
				try {
					Debug.logInfo("calling  service prepareAndImportOrderXMLFromJson", module);
					Map<String, Object> createCtx = new HashMap<String, Object>();
		            createCtx.put("booking", booking);
		            createCtx.put("userLogin", userLogin);
					Map prepareAndImportOrderXMLFromJsonResult = dispatcher.runSync("prepareAndImportOrderXMLFromJson", createCtx);
					List<String> messages = new ArrayList<String>();

					String outputXmlFilePath = (String) prepareAndImportOrderXMLFromJsonResult.get("dumpDirPath");
					System.out.println("==1379=outputXmlFilePath======"+outputXmlFilePath+"=====");
					if (!ServiceUtil.isError(prepareAndImportOrderXMLFromJsonResult)) {
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
							//moveXMLFilesFromDir(outputXmlFilePath, orderExtractXMLSuccessPath);
							Debug.logInfo("moved XMLFilesFromDir successfully", module);
						}
					}
				} catch (Exception ex) {
					errorDuringInsertion = true;
					errorMsgs.add(ex.getMessage());
					Debug.logError(ex.getMessage(), module);
					throw ex;
				}
			}
		}
		Debug.logInfo(" Method importOrderInERPFromJson ends", module);
		if (errorDuringInsertion) {
			return ServiceUtil.returnError(errorMsgs);
		}
		return resultMap;
	}

	public static Map<String, Object> prepareAndImportOrderXMLFromJson(DispatchContext ctx, Map<String, ?> context)
			throws OrderImportException {
		Debug.logInfo(" service prepareAndImportOrderXMLFromJson starts", module);
		failedOrderMap = new HashMap<String, String>();
		delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		userLoginId = userLogin.getString("userLoginId");
		nowTimestamp = UtilDateTime.toDateString(UtilDateTime.nowTimestamp(), "yyyy-MM-dd HH:mm:ss.SSS");

		validateConfigurablePath(context);

		Booking booking = (Booking)context.get("booking");
		//readFolder(userLogin,dispatcher);
		try {
			if(booking!=null){
				generateOrderWithOrderItem(userLogin, dispatcher, booking);
			}
			generateErrorStatus();
		} catch (IOException e) {
			throw new OrderImportException(e.getMessage());
		}
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("dumpDirPath", orderExtractOutputPath);
		resultMap.put("success", "success");
		Debug.logInfo(" service prepareAndImportOrderXMLFromJson ends", module);
		return resultMap;
	}

	private static void validateConfigurablePath(Map<String, ?> context) throws OrderImportException {
		Debug.logInfo(" check validateConfigurablePath starts", module);
		orderExtractInputPath = UtilProperties.getPropertyValue("gaadizo.properties", "orderextract-input-path");
		Debug.log("orderExtractInputPath "+orderExtractInputPath);
		if(UtilValidate.isEmpty(orderExtractInputPath)){
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


	private static void generateOrderWithOrderItem(GenericValue userLogin, LocalDispatcher dispatcher, Booking booking) throws IOException, OrderImportException {
		orderItemMapList = new ArrayList<Map<String, String>>();

		BookingDetail booking_detail = booking.getBooking_detail();
	    CustomerDetail customer_detail = booking.getCustomer_detail();
	    VehicleDetail vehicle_detail = booking.getVehicle_detail();
	    ServiceDetail service_detail = booking.getService_detail();
	    miscellaneous miscellaneous = booking.getMiscellaneous();
		
		orderMap = new HashMap<String, String>();
		orderMap.put(OrderCSV.ORDER_ID.name(), booking_detail.getOrder_ID());
		orderMap.put(OrderCSV.Booking_ID.name(), booking_detail.getBooking_ID());
		orderMap.put(OrderXML.ORDER_STATUS.name(), "ORDER_CREATED");
		orderMap.put(OrderXML.ORDER_PLACED_DATE.name(), nowTimestamp);
		orderMap.put(OrderCSV.Order_Amount.name(), booking_detail.getOffer_Amount());
		orderMap.put(OrderXML.CURRENCY.name(), "INR");
		orderMap.put(OrderCSV.Address_1.name(), customer_detail.getAddress_1());
		orderMap.put(OrderCSV.Address_2.name(), customer_detail.getAddress_2());
		if(miscellaneous.getPick_up_Address()!=null){
			orderMap.put(OrderCSV.Pick_up_Address1.name(), miscellaneous.getPick_up_Address().getArea());
			orderMap.put(OrderCSV.Pick_up_Address2.name(), miscellaneous.getPick_up_Address().getLand_Mark());
		}
		orderMap.put(OrderCSV.CUST_EMAIL.name(), customer_detail.getCust_Email());
		orderMap.put(OrderCSV.First_Name.name(), customer_detail.getFirst_Name());
		orderMap.put(OrderCSV.Last_Name.name(), customer_detail.getLast_Name());
		orderMap.put(CUSTOMER_LOGON_ID, customer_detail.getCustomer_ID());
		//orderMap.putAll(orderLineItem);
		List<GenericValue> productAssocList = getOrderLineItem(service_detail, vehicle_detail);
		Debug.logInfo(" List<GenericValue> productAssocList : " + productAssocList, module);

		//Fixed Static values issue with dynamic values.
		storeId = service_detail.getService_Provider_ID();
		warehouse = service_detail.getService_Provider_ID();
		
		if (!isExist("OrderHeader", UtilMisc.toMap("orderId", orderMap.get(OrderCSV.ORDER_ID.name())))) {
			parseOrderFromMap(userLogin, dispatcher, booking_detail, customer_detail, vehicle_detail, service_detail, miscellaneous);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static List<GenericValue> getOrderLineItem(ServiceDetail service_detail, VehicleDetail vehicle_detail) {
		List<GenericValue> productAssocList = new ArrayList();
		//orderItemMapList setting for an order
		Map<String, String> orderItemMap;
		try {
			
			//productAssocList = delegator.findByAnd("ProductAssoc", UtilMisc.toMap("productExternalId", service_detail.getService_ID(), "vehicleModelId", vehicle_detail.getVehicle_Model_ID(), "ownerId", service_detail.getService_Provider_ID()),null,false);
			productAssocList = delegator.findByAnd("ProductAssoc", UtilMisc.toMap("productExternalId", service_detail.getService_ID(), "vehicleModelId", vehicle_detail.getVehicle_Model_ID()),null,false);
			int orderItemId = 10001;
			System.out.println("======productAssocList======"+productAssocList+"========");
			List<GenericValue> productVehiclePriceList = null;
			GenericValue productVehiclePrice = null;
			for(GenericValue productAssoc : productAssocList) {
				productVehiclePriceList = delegator.findByAnd("ProductVehiclePrice", UtilMisc.toMap("productId", productAssoc.getString("productIdTo"), "vehicleId", vehicle_detail.getVehicle_Model_ID()),null,false);
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
					if(UtilValidate.isNotEmpty(productVehiclePrice.getString("workshopPrice"))) 
						workshopPrice = productVehiclePrice.getString("workshopPrice");
					if(UtilValidate.isNotEmpty(productVehiclePrice.getString("price"))) 
						itemPrice = productVehiclePrice.getString("price");
					if(UtilValidate.isNotEmpty(productVehiclePrice.getString("description"))) 
						description = productVehiclePrice.getString("description");
				}
				orderItemMap.put(OrderXML.ITEM_PRICE.name(), workshopPrice);
				orderItemMap.put(OrderXML.ORDER_ITEM_TOTAL.name(), itemPrice);
				orderItemMap.put(OrderXML.PRODUCT_NAME.name(), description);

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
	
	@SuppressWarnings("unchecked")
	private static void parseOrderFromMap(GenericValue userLogin, LocalDispatcher dispatcher, 
			BookingDetail booking_detail, CustomerDetail customer_detail, VehicleDetail vehicle_detail, 
			ServiceDetail service_detail, miscellaneous miscellaneous) throws IOException, OrderImportException {
		Debug.logInfo("start parseOrderFromMap--", module);
		String partyId = delegator.getNextSeqId("Party");
		List<Element> contents = new ArrayList<Element>();
		
		contents.addAll(getRole(null, partyId, true));
		contents.add(getOrderHeader());
		contents.add(getJobCard(booking_detail, customer_detail, vehicle_detail, service_detail, miscellaneous));
		Map<String, Object> contactMechDetails = getContactMechList(partyId, booking_detail, customer_detail, vehicle_detail, service_detail, miscellaneous);

		contents.addAll((List<Element>) contactMechDetails.get(contactMechElementList));
		contents.addAll(getRole(orderMap.get(OrderCSV.ORDER_ID.name()), partyId, false));
		

		String orderStatus = UtilProperties.getPropertyValue("gaadizo.properties",
				"ORDER_STATUS_" + StringUtils.upperCase(orderMap.get(OrderXML.ORDER_STATUS.name())).replace(" ", "_"));
		contents.add(getOrderStatus(orderMap.get(OrderCSV.ORDER_ID.name()), null, null, orderStatus, false));
		contents.addAll(getOrderItem(contactMechDetails.get(shippingContactMechId).toString(), userLogin, dispatcher, service_detail));

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

	private static List<Element> getRole(String orderId, String partyId, boolean isPartyRole) {
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
			//person.setAttribute(new Attribute("socialSecurityNumber", orderMap.get(OrderCSV.CUST_GTSN.name())));
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
			billFromVendor.setAttribute(new Attribute("partyId", warehouse));
			billFromVendor.setAttribute(new Attribute("orderId", orderMap.get(OrderCSV.ORDER_ID.name())));
			billFromVendor.setAttribute(new Attribute("roleTypeId", "BILL_FROM_VENDOR"));
			partyWithRole.add(billFromVendor);
		}
		return partyWithRole;
	}

	private static Element getOrderHeader() throws OrderImportException {
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

		element.setAttribute(new Attribute("entryDate", orderPlacedDate));
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
		element.setAttribute(new Attribute("productStoreId", storeId));//service_detail.getService_Provider_ID();
		element.setAttribute(new Attribute("originFacilityId", warehouse));
		element.setAttribute(new Attribute("ownerId", storeId));
		element.setAttribute(new Attribute("createdByUserLogin", userLoginId));
		/*element.setAttribute(new Attribute("importedOrderCsvName", fileName));
		element.setAttribute(new Attribute("importedOrderCsvName", fileName));
		*/
		element.setAttribute(new Attribute("statusId", "ORDER_APPROVED"));
		Debug.logInfo("end getOrderHeader method for Order Id--" + orderMap.get(OrderCSV.ORDER_ID.name()), module);
		return element;
	}
	
	private static Element getJobCard(BookingDetail booking_detail, CustomerDetail customer_detail, VehicleDetail vehicle_detail, 
			ServiceDetail service_detail, miscellaneous miscellaneous) throws OrderImportException {
		Debug.logInfo("start getJobCard method for Order Id--" + booking_detail.getOrder_ID(), module);
		System.out.println("==573======orderMap========"+orderMap+"============");
		Element element = new Element("JobCard");
		element.setAttribute(new Attribute("jobCardId", booking_detail.getBooking_ID()));
		element.setAttribute(new Attribute(OrderXML.ORDER_ID.getOfbizColName(), booking_detail.getOrder_ID()));
		String orderPlacedDate = null;
		try {
			orderPlacedDate = OmsDateTimeUtility
					.parseDateToTimestamp(orderMap.get(OrderXML.ORDER_PLACED_DATE.name()), nowTimestamp)
					.toString();
			element.setAttribute(new Attribute("serviceDate", orderPlacedDate));
		} catch (ParseException e) {
			throw new OrderImportException(e.getMessage());
		}
		if(vehicle_detail.getVehicle_Make_ID()!=null)
			element.setAttribute(new Attribute("vehicleId", vehicle_detail.getVehicle_Make_ID()));
		if(vehicle_detail.getVehicle_Model_ID()!=null)
			element.setAttribute(new Attribute("vehicleModelId", vehicle_detail.getVehicle_Model_ID()));
		if(vehicle_detail.getVehicle_Model()!=null)
			element.setAttribute(new Attribute("vehicleModel", vehicle_detail.getVehicle_Model()));
		if(vehicle_detail.getVehicle_Regstn_No()!=null)
			element.setAttribute(new Attribute("registationNumber", vehicle_detail.getVehicle_Regstn_No()));
		if(vehicle_detail.getVehicle_Make_ID()!=null)
			element.setAttribute(new Attribute("vehicleMakeId", vehicle_detail.getVehicle_Make_ID()));
		if(vehicle_detail.getVehicle_Make()!=null)
			element.setAttribute(new Attribute("vehicleMake", vehicle_detail.getVehicle_Make()));
		
		element.setAttribute(new Attribute("serviceProviderId", service_detail.getService_Provider_ID()));
		if(service_detail.getService_Provider()!=null)
			element.setAttribute(new Attribute("serviceProvider", service_detail.getService_Provider()));
		String service_Availed = "";
		for(String service: service_detail.getService_Availed()){
			service_Availed = service_Availed+service;
		}
		element.setAttribute(new Attribute("serviceAvailed", service_Availed));
		if(service_detail.getService_ID()!=null)
			element.setAttribute(new Attribute("offerDetails", service_detail.getService_ID()));
		/*
		element.setAttribute(new Attribute("oilType", orderMap.get(OrderCSV.Oil_type.name())));
		element.setAttribute(new Attribute("offerCode", orderMap.get(OrderCSV.Offer_Code.name())));
		element.setAttribute(new Attribute("paymentMode", orderMap.get(OrderCSV.Payment_Mode.name())));
		element.setAttribute(new Attribute("serviceTime", orderMap.get(OrderCSV.Service_Time.name())));
		*/
		
		if(customer_detail.getCustomer_ID()!=null)
			element.setAttribute(new Attribute("customerId", customer_detail.getCustomer_ID()));
		if(customer_detail.getFirst_Name()!=null)
			element.setAttribute(new Attribute("firstName", customer_detail.getFirst_Name()));
		if(customer_detail.getLast_Name()!=null)
			element.setAttribute(new Attribute("lastName", customer_detail.getLast_Name()));
		if(customer_detail.getState_Code()!=null)
			element.setAttribute(new Attribute("status", customer_detail.getState_Code()));
		element.setAttribute(new Attribute("createdBy", userLoginId));
		if(orderMap.get(OrderCSV.Gaadizo_Credit.name())!=null)
			element.setAttribute(new Attribute("gaadizoCredit", orderMap.get(OrderCSV.Gaadizo_Credit.name())));
		if(miscellaneous.getPick_up_Address()!=null)
			element.setAttribute(new Attribute("pickup", miscellaneous.getPick_up()+"$$$"+miscellaneous.getPick_up_Address().getArea()+"$$$"+miscellaneous.getPick_up_Address().getLocality()+"$$$"+miscellaneous.getPick_up_Address().getLand_Mark()));
		Debug.logInfo("end getJobCard method for Booking_ID --" + orderMap.get(OrderCSV.Booking_ID.name()), module);
		return element;
	}

	private static Map<String, Object> getContactMechList(String partyId, BookingDetail booking_detail, CustomerDetail customer_detail, VehicleDetail vehicle_detail, 
			ServiceDetail service_detail, miscellaneous miscellaneous) {
		List<Element> contactMechList = new ArrayList<Element>();
		
		//SHIPPING_LOCATION Details
		String contachMechIdShip = delegator.getNextSeqId("ContactMech");
		contactMechList.add(getContactMech("POSTAL_ADDRESS", contachMechIdShip, null));
		/*String postalCodeGeoId = delegator.getNextSeqId("Geo");
		Element postalCodeGeo = new Element("Geo");
		postalCodeGeo.setAttribute(new Attribute("geoId", postalCodeGeoId));
		postalCodeGeo.setAttribute(new Attribute("geoTypeId", "POSTAL_CODE"));
		if(customer_detail.getState_Code()!=null)
			postalCodeGeo.setAttribute(new Attribute("geoName", customer_detail.getState_Code()));
		if(customer_detail.getState_Code()!=null)
			postalCodeGeo.setAttribute(new Attribute("geoCode", customer_detail.getState_Code()));
		if(customer_detail.getState_Code()!=null)
			postalCodeGeo.setAttribute(new Attribute("geoSecCode", customer_detail.getState_Code()));
		if(customer_detail.getState_Code()!=null)
			postalCodeGeo.setAttribute(new Attribute("abbreviation", customer_detail.getState_Code()));
		contactMechList.add(postalCodeGeo);*/
		
		Element postalAddressShip = new Element("PostalAddress");
		postalAddressShip.setAttribute(new Attribute("contactMechId", contachMechIdShip));
		if(customer_detail.getAddress_1()!=null)
			postalAddressShip.setAttribute(new Attribute("address1", customer_detail.getAddress_1()));
		if(customer_detail.getAddress_2()!=null)
			postalAddressShip.setAttribute(new Attribute("address2", customer_detail.getAddress_2()));
		if(customer_detail.getCity()!=null)
			postalAddressShip.setAttribute(new Attribute("city", customer_detail.getCity()));
		postalAddressShip.setAttribute(new Attribute("stateProvinceGeoId", customer_detail.getState_Code()));//"HR"));
		postalAddressShip.setAttribute(new Attribute("countryGeoId", "IND"));//"INDIA"));
		postalAddressShip.setAttribute(new Attribute("postalCode", "122001"));
		//postalAddressShip.setAttribute(new Attribute("directions", orderMap.get(OrderCSV.Remarks.name())));
		contactMechList.add(postalAddressShip);
		contactMechList.add(getPartyContactMech(partyId, contachMechIdShip, customer_detail.getAddress_1()+" "+customer_detail.getAddress_2()));
		contactMechList.add(getPartyContactMechPurpose(partyId, contachMechIdShip, "SHIPPING_LOCATION"));
		
		//BILLING_LOCATION Details
		String contachMechIdBill = delegator.getNextSeqId("ContactMech");
		contactMechList.add(getContactMech("POSTAL_ADDRESS", contachMechIdBill, null));
		Element postalAddressBill = new Element("PostalAddress");
		postalAddressBill.setAttribute(new Attribute("contactMechId", contachMechIdBill));
		if(miscellaneous.getPick_up_Address()!=null){
			if(miscellaneous.getPick_up_Address().getArea()!=null)
				postalAddressBill.setAttribute(new Attribute("address1", miscellaneous.getPick_up_Address().getArea()));
			if(miscellaneous.getPick_up_Address().getLand_Mark()!=null)
				postalAddressBill.setAttribute(new Attribute("address2", miscellaneous.getPick_up_Address().getLand_Mark()));
		}
		if(orderMap.get(OrderCSV.City.name())!=null)
			postalAddressBill.setAttribute(new Attribute("city", orderMap.get(OrderCSV.City.name())));
		if(orderMap.get(OrderCSV.State_Code.name())!=null)
			postalAddressBill.setAttribute(new Attribute("stateProvinceGeoId", orderMap.get(OrderCSV.State_Code.name())));
		postalAddressBill.setAttribute(new Attribute("countryGeoId", "IND"));
		postalAddressBill.setAttribute(new Attribute("postalCode", "122001"));
		if(miscellaneous.getPick_up_Address()!=null && miscellaneous.getPick_up_Address().getLocality()!=null)
			postalAddressBill.setAttribute(new Attribute("directions", miscellaneous.getPick_up_Address().getLocality()));
		contactMechList.add(postalAddressBill);
		if(miscellaneous.getPick_up_Address()!=null)
			contactMechList.add(getPartyContactMech(partyId, contachMechIdBill, miscellaneous.getPick_up_Address().getArea()+" "+miscellaneous.getPick_up_Address().getLand_Mark()));
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

	private static List<Element> getOrderItem(String shipmentContactMechId,GenericValue userLogin, LocalDispatcher dispatcher, ServiceDetail service_detail) throws OrderImportException {
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

			String orderItemStatus = "ITEM_APPROVED";
			orderItemElement.setAttribute(new Attribute(OrderXML.ORDER_ITEM_STATUS.getOfbizColName(), orderItemStatus));
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
			
			
			if(UtilValidate.isNotEmpty(service_detail.getService_Provider_ID())){
				
			    /*Map<String, Object> ctx = new HashMap<String, Object>();
	            ctx.put("productId", productId);
	            ctx.put("facilityId", orderMap.get(OrderCSV.Service_Provider_ID.name()));
	            ctx.put("orderItemSeqId", orderItemSeqId);
	            ctx.put("shipGroupSeqId", "00001");
	            ctx.put("orderId", orderMap.get(OrderCSV.ORDER_ID.name()));
	            ctx.put("quantity", orderItem.get(OrderXML.QUANTITY.name()));
	            ctx.put("requireInventory", "N");
	            ctx.put("reserveOrderEnumId", "INVRO_FIFO_REC");
	            Map<String, Object> map = dispatcher.runSync("reserveProductInventory", ctx);
			    */				
				try{
					List<EntityCondition> findBasedOnFields = new ArrayList<EntityCondition>();
					findBasedOnFields.add(EntityCondition.makeCondition("facilityId", service_detail.getService_Provider_ID()));
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
						    invItemGVCtx.setAttribute(new Attribute("ownerPartyId", service_detail.getService_Provider_ID()));
						    invItemGVCtx.setAttribute(new Attribute("facilityId", service_detail.getService_Provider_ID()));
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
		}
		Debug.logInfo("end getOrderItem method for Order Id--" + orderMap.get(OrderCSV.ORDER_ID.name()), module);
		return orderItemList;
	}

	private static List<Element> getOrderAdjustment(Map<String, String> orderItem, String orderItemSeqId,String shipGroupSeqId) {
		Debug.logInfo("start getOrderAdjustment method for Order Id--" + orderMap.get(OrderCSV.ORDER_ID.name()),
				module);

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
		
		List<Element> orderAdjustmentList = new ArrayList<Element>();
		orderAdjustmentList.add(orderAdjustmentSales);

		Debug.logInfo("end getOrderAdjustment method for Order Id--" + orderMap.get(OrderCSV.ORDER_ID.name()), module);
		return orderAdjustmentList;
	}

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

	private static List<Element> getCarrierShipmentMethodAndOrderAdjustment(Map<String, String> orderItem,
			String shipmentContactMechId, String orderItemSeqId, String productId) throws OrderImportException {
		Debug.logInfo("start getCarrierShipmentMethod", module);
		List<Element> carrierElementsList = new ArrayList<Element>();

		Element orderItemShipGroup = new Element("OrderItemShipGroup");
		orderItemShipGroup.setAttribute(
				new Attribute(OrderXML.ORDER_ID.getOfbizColName(), orderMap.get(OrderCSV.ORDER_ID.name())));
		//String releaseNumber = StringUtils.leftPad("10000", 5, "0");
		orderItemShipGroup.setAttribute(new Attribute("shipGroupSeqId", "00001"));
		orderItemShipGroup.setAttribute(new Attribute(OrderXML.SHIP_MODE.getOfbizColName(), "_NA_"));
		orderItemShipGroup.setAttribute(new Attribute("carrierPartyId", "_NA_"));
		orderItemShipGroup.setAttribute(new Attribute("carrierRoleTypeId", "CARRIER"));
		orderItemShipGroup.setAttribute(new Attribute("contactMechId", shipmentContactMechId));
		//orderItemShipGroup.setAttribute(new Attribute("vendorPartyId", userLogin.getString("ownerCompanyId")));
		carrierElementsList.add(orderItemShipGroup);

		Debug.logInfo("end getCarrierShipmentMethod", module);
		return carrierElementsList;
	}

	private static List<Element> getInventoryElement(Map<String, String> orderItem, String productId,
			String orderItemSeqId) {
		List<Element> elementList = new ArrayList<Element>();
		String inventoryItemId = delegator.getNextSeqId("InventoryItem");
		Element inventoryItem = new Element("InventoryItem");
		inventoryItem.setAttribute(new Attribute("availableToPromiseTotal", orderItem.get(OrderXML.QUANTITY.name())));
		inventoryItem.setAttribute(new Attribute("facilityId", warehouse));
		inventoryItem.setAttribute(new Attribute("inventoryItemId", inventoryItemId));
		inventoryItem.setAttribute(new Attribute("inventoryItemTypeId", "NON_SERIAL_INV_ITEM"));
		inventoryItem.setAttribute(new Attribute("productId", productId));
		inventoryItem.setAttribute(new Attribute("quantityOnHandTotal", orderItem.get(OrderXML.QUANTITY.name())));

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
}
