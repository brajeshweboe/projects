package com.ofbiz.importinterface.services.inventory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.jdom.Attribute;
import org.jdom.Document;
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
import org.apache.ofbiz.entity.condition.EntityCondition;
import org.apache.ofbiz.entity.condition.EntityOperator;
import org.apache.ofbiz.entity.util.EntityUtil;
import com.ofbiz.importinterface.constants.InventoryXML;
import com.ofbiz.importinterface.exception.ProductInventoryImportException;
import com.ofbiz.utility.ImportUtility;


public class ImportProductInventoryServices {
	

	public static final String module = ImportProductInventoryServices.class.getName();
	private static Map<String, String> inventoryMap;
	private static List<Map<String, String>> inventoryMapList;
	private static Map<String, String> failedProductInventoryMap;
	private static Delegator delegator;
	private static String inventoryExtractInputPath;
	private static String inventoryExtractSuccessPath;
	private static String inventoryExtractOutputPath;
	private static String inventoryExtractInputErrorPath;
	private static String inventoryExtractXMLSuccessPath;
	private static String nowTimestamp = "";
	private static String BLANK = "";

	public static Map<String, Object> prepareAndImportProductInventoryXML(DispatchContext ctx, Map<String, ?> context)
			throws ProductInventoryImportException {
		Debug.logInfo(" service prepareAndImportProductInventoryXML starts", module);
		failedProductInventoryMap = new HashMap<String, String>();
		delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		nowTimestamp = UtilDateTime.toDateString(UtilDateTime.nowTimestamp(), "yyyy-MM-dd HH:mm:ss");
		

		validateConfigurablePath(context);
		readFolder();
		try {
			generateErrorStatus();
		} catch (IOException e) {
			throw new ProductInventoryImportException(e.getMessage());
		}
		//Reverting release
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("dumpDirPath", inventoryExtractOutputPath);
		resultMap.put("success", "success");
		Debug.logInfo(" service prepareAndImportProductInventoryXML ends", module);
		return resultMap;
	}

	private static void validateConfigurablePath(Map<String, ?> context) throws ProductInventoryImportException {
		Debug.logInfo(" check validateConfigurablePath starts", module);
		if(UtilValidate.isEmpty(inventoryExtractInputPath)) {
			inventoryExtractInputPath = UtilProperties.getPropertyValue("gaadizo.properties", "inventoryextract-input-path");
		}
		Debug.logInfo("inventoryExtractInputPath : " +inventoryExtractInputPath, module);
		if (!new File(inventoryExtractInputPath).exists()) {
			throw new ProductInventoryImportException("ProductInventory Import Path is not configured");
		}

		inventoryExtractOutputPath = FlexibleStringExpander
				.expandString(UtilProperties.getPropertyValue("gaadizo.properties", "inventoryextract-output-path"), context);
		Debug.logInfo("inventoryExtractOutputPath : " +inventoryExtractOutputPath, module);
		if (StringUtils.isBlank(inventoryExtractOutputPath)) {
			throw new ProductInventoryImportException("ProductInventory import output path is not configured");
		}
		if (!new File(inventoryExtractOutputPath).exists()) {
			new File(inventoryExtractOutputPath).mkdirs();
		}

		inventoryExtractInputErrorPath = FlexibleStringExpander.expandString(
				UtilProperties.getPropertyValue("gaadizo.properties", "inventoryextract-input-error-path"), context);
		Debug.logInfo("inventoryExtractInputErrorPath : " +inventoryExtractInputErrorPath, module);
		if (StringUtils.isBlank(inventoryExtractInputErrorPath)) {
			throw new ProductInventoryImportException("ProductInventory import input error path is not configured");
		}
		if (!new File(inventoryExtractInputErrorPath).exists()) {
			new File(inventoryExtractInputErrorPath).mkdirs();
		}
		inventoryExtractSuccessPath = FlexibleStringExpander
				.expandString(UtilProperties.getPropertyValue("gaadizo.properties", "inventoryextract-success-path"), context);
		Debug.logInfo("inventoryExtractSuccessPath : " +inventoryExtractSuccessPath, module);
		if (StringUtils.isBlank(inventoryExtractSuccessPath)) {
			throw new ProductInventoryImportException("ProductInventory import success error path is not configured");
		}
		if (!new File(inventoryExtractSuccessPath).exists()) {
			new File(inventoryExtractSuccessPath).mkdirs();
		}

		Debug.logInfo(" check validateConfigurablePath ends", module);
	}

	private static void generateErrorStatus() throws IOException {
		final String FILE_HEADER = "productId,Status,Comments";
		if (failedProductInventoryMap != null && !failedProductInventoryMap.isEmpty()) {
			FileWriter fileWriter = null;
			String filePath = inventoryExtractInputErrorPath + "error_.csv";
			try {
				fileWriter = new FileWriter(filePath);
				fileWriter.append(FILE_HEADER);
				fileWriter.append("\n");
				for (Map.Entry<String, String> failedProductInventory : failedProductInventoryMap.entrySet()) {
					fileWriter.append(failedProductInventory.getKey());
					fileWriter.append(",");
					fileWriter.append("Error");
					fileWriter.append(",");
					fileWriter.append(failedProductInventory.getValue());
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

	private static void readFolder() throws ProductInventoryImportException {
		try {
			Debug.logInfo(" reading files starts", module);
			File folder = new File(inventoryExtractInputPath);
			File[] listOfFiles = null;
			if (folder != null) {
				listOfFiles = folder.listFiles();
				if (listOfFiles.length > 0) {
					for (final File file : listOfFiles) {

						if (file.isFile()
								&& StringUtils.equalsIgnoreCase("csv", FilenameUtils.getExtension(file.getName()))) {
							Debug.logInfo("satrt reading file--" + file.getName(), module);
							readCSVAndConvertToProductInventoryXML(file);
							Debug.logInfo("end reading file--" + file.getName(), module);
						}
						try {
							File destDir = new File(inventoryExtractSuccessPath);
							Debug.logInfo("copying --" + file.getName() + " to inventory extract success path", module);
							FileUtils.copyFileToDirectory(file, destDir);
							Debug.logInfo("delete --" + file.getName() + " from inventory extract input path", module);
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
			throw new ProductInventoryImportException(e.getMessage());

		}
	}

	public static void readCSVAndConvertToProductInventoryXML(File file) throws IOException, ProductInventoryImportException {
		// String csvFile ="F:\\Sample_data\\input\\Quancious_ProductInventoryExtract_2017-12-29_11_58.csv";
		Debug.logInfo("satrt reading csv line by line--", module);
		String line = "";
		String csvSplitBy = ",";
		inventoryMapList = new ArrayList();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
			int count = 0;
			while ((line = br.readLine()) != null) {
				if (count == 0) {
					count++;
					continue;
				}
				String[] inventoryLine = line.split(csvSplitBy);
				inventoryMapList.add(buildDataRows(inventoryLine, file));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ProductInventoryImportException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				br.close();
			}
		}
		Debug.logInfo("end reading csv line by line--", module);
		//createProductInventory(inventoryMapList, file.getName());
		parseProductInventoryFromMapList(file.getName());
		
	}

	private static Map<String, String> buildDataRows(String[] s, File file) throws ProductInventoryImportException {
		Map<String, String> data = new HashMap<String, String>();
		try {
			for (InventoryXML inventoryXMLs : InventoryXML.values()) {
				data.put(inventoryXMLs.name(), ("null".equalsIgnoreCase(s[inventoryXMLs.getCode()]) ? " "
						: StringUtils.replace(s[inventoryXMLs.getCode()], "~", ",")));
				// changed ~ to ',' because if csv identifies , in any of its
				// value then column get shifted to other
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			try {
				File destDir = new File(inventoryExtractInputErrorPath);
				FileUtils.copyFileToDirectory(file, destDir);
				//file.delete();
			} catch (IOException e1) {
				throw new ProductInventoryImportException(e1.getMessage());
			}
		}
		return data;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static boolean isExist(String entityName, Map fields) {
		List<GenericValue> inventoryList;
		try {
			inventoryList = delegator.findByAnd(entityName, fields,null,false);
			Debug.logInfo("from isExist() inventoryList--" + inventoryList, module);
			
			if (UtilValidate.isEmpty(inventoryList)) {
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
	private static void parseProductInventoryFromMapList(String fileName) throws IOException, ProductInventoryImportException {
		Debug.logInfo("start parseProductInventoryFromMapList--", module);
		List<Element> contents = new ArrayList<Element>();
		
		Debug.logInfo("parsing inventory from map start--", module);
		int size = inventoryMapList.size();
		for (int i = 0; i < size; i++) {
			try {
				inventoryMap = inventoryMapList.get(i);
				if (isExist("Product", UtilMisc.toMap("productId", inventoryMap.get(InventoryXML.Product_Id.name()))) && isExist("Facility", UtilMisc.toMap("facilityId", inventoryMap.get(InventoryXML.Service_Center_id.name())))) {
					contents.addAll(getProductFacility());
					contents.addAll(getProductAttribute());
					contents.addAll(getFacilityLocation());
					contents.addAll(getProductFacilityLocation());
					contents.addAll(getInventoryItemAndDetail());
					
				}
			} catch (ProductInventoryImportException e) {
				failedProductInventoryMap.put(inventoryMap.get(InventoryXML.Product_Id.name()), e.getMessage());
			}
		}
		Debug.logInfo("parsing inventory from map end--", module);
		
		Element inventory = new Element("entity-engine-xml");
		inventory.addContent(contents);
		writeXMLToFile(inventory);
		Debug.logInfo("start parseProductInventoryFromMapList--", module);
	}

	private static void writeXMLToFile(Element inventory) throws IOException {
		XMLOutputter xmlOutput = new XMLOutputter();
		// display nice
		xmlOutput.setFormat(Format.getPrettyFormat());
		Document doc = new Document(inventory);
		doc.setRootElement(inventory);
		OutputStreamWriter writer = null;
		try {
			Date dNow = new Date();
		    SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs");
		    String datetime = ft.format(dNow);
			String filePath = inventoryExtractOutputPath + "inventory_" +datetime+ ".xml";
			Debug.logInfo("from writeXMLToFile method writting file on filePath--" + filePath, module);
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

	private static List<Element> getProductFacility() throws ProductInventoryImportException {
		Debug.logInfo("start getProductFacility method for ProductInventory Id--" + inventoryMap.get(InventoryXML.Product_Id.name()), module);
		Element inventoryElement = new Element("ProductFacility");
		//Required Columns
		inventoryElement.setAttribute(new Attribute(InventoryXML.Product_Id.getOfbizColName(), inventoryMap.get(InventoryXML.Product_Id.name())));
		inventoryElement.setAttribute(new Attribute("facilityId", inventoryMap.get(InventoryXML.Service_Center_id.name()).toUpperCase()));
		//inventoryElement.setAttribute(new Attribute(InventoryXML.Inventroy_Quantity.getOfbizColName(), inventoryMap.get(InventoryXML.Inventroy_Quantity.name())));
		inventoryElement.setAttribute(new Attribute("productAssocTypeId", "PRODUCT_CONF"));
		inventoryElement.setAttribute(new Attribute("minimumStock", "1"));
		inventoryElement.setAttribute(new Attribute("reorderQuantity", "1"));
		inventoryElement.setAttribute(new Attribute("daysToShip", "1"));
		
		List<Element> allProductInventory= new ArrayList<>();
		allProductInventory.add(inventoryElement);
		Debug.logInfo("end getProductFacility method for ProductInventory Id--" + inventoryMap.get(InventoryXML.Product_Id.name()), module);
		return allProductInventory;
	}
	
	private static List<Element> getProductAttribute() throws ProductInventoryImportException {
		Debug.logInfo("start getProductFacility method for ProductInventory Id--" + inventoryMap.get(InventoryXML.Product_Id.name()), module);
		Element inventoryElement1 = new Element("ProductAttribute");
		Element inventoryElement2 = new Element("ProductAttribute");
		//Required Columns
		inventoryElement1.setAttribute(new Attribute(InventoryXML.Product_Id.getOfbizColName(), inventoryMap.get(InventoryXML.Product_Id.name())));
		inventoryElement1.setAttribute(new Attribute("attrName", "BF_INVENTORY_TOT"));
		inventoryElement1.setAttribute(new Attribute("attrValue", inventoryMap.get(InventoryXML.Inventroy_Quantity.name())));
		
		inventoryElement2.setAttribute(new Attribute(InventoryXML.Product_Id.getOfbizColName(), inventoryMap.get(InventoryXML.Product_Id.name())));
		inventoryElement2.setAttribute(new Attribute("attrName", "BF_INVENTORY_WHS"));
		inventoryElement2.setAttribute(new Attribute("attrValue", inventoryMap.get(InventoryXML.Inventroy_Quantity.name())));
		
		//inventoryElement.setAttribute(new Attribute(InventoryXML.Inventroy_Quantity.getOfbizColName(), inventoryMap.get(InventoryXML.Inventroy_Quantity.name())));
		
		List<Element> allProductInventory= new ArrayList<>();
		allProductInventory.add(inventoryElement1);
		allProductInventory.add(inventoryElement2);
		Debug.logInfo("end getProductFacility method for ProductInventory Id--" + inventoryMap.get(InventoryXML.Product_Id.name()), module);
		return allProductInventory;
	}
	
	private static List<Element> getFacilityLocation() throws ProductInventoryImportException {
		Debug.logInfo("start getFacilityLocation method for ProductInventory Id--" + inventoryMap.get(InventoryXML.Product_Id.name()), module);
		Element inventoryElement1 = new Element("FacilityLocation");
		Element inventoryElement2 = new Element("FacilityLocation");
		//Required Columns
		inventoryElement1.setAttribute(new Attribute("facilityId", inventoryMap.get(InventoryXML.Service_Center_id.name()).toUpperCase()));
		inventoryElement1.setAttribute(new Attribute("locationSeqId", "TLTLTLLL01"));
		inventoryElement1.setAttribute(new Attribute("locationTypeEnumId", "FLT_PICKLOC"));
		inventoryElement1.setAttribute(new Attribute("areaId", "TL"));
		inventoryElement1.setAttribute(new Attribute("aisleId", "TL"));
		inventoryElement1.setAttribute(new Attribute("sectionId", "TL"));
		inventoryElement1.setAttribute(new Attribute("levelId", "LL"));
		inventoryElement1.setAttribute(new Attribute("positionId", "01"));
		
		inventoryElement2.setAttribute(new Attribute("facilityId", inventoryMap.get(InventoryXML.Service_Center_id.name()).toUpperCase()));
		inventoryElement2.setAttribute(new Attribute("locationSeqId", "TLTLTLUL01"));
		inventoryElement2.setAttribute(new Attribute("locationTypeEnumId", "TLTLTLUL01"));
		inventoryElement2.setAttribute(new Attribute("areaId", "TL"));
		inventoryElement2.setAttribute(new Attribute("aisleId", "TL"));
		inventoryElement2.setAttribute(new Attribute("sectionId", "TL"));
		inventoryElement2.setAttribute(new Attribute("levelId", "LL"));
		inventoryElement2.setAttribute(new Attribute("positionId", "01"));
		
		List<Element> allProductInventory= new ArrayList<>();
		allProductInventory.add(inventoryElement1);
		allProductInventory.add(inventoryElement2);
		Debug.logInfo("end getFacilityLocation method for ProductInventory Id--" + inventoryMap.get(InventoryXML.Product_Id.name()), module);
		return allProductInventory;
	}
	
	private static List<Element> getProductFacilityLocation() throws ProductInventoryImportException {
		Debug.logInfo("start getProductFacilityLocation method for ProductInventory Id--" + inventoryMap.get(InventoryXML.Product_Id.name()), module);
		Element inventoryElement = new Element("ProductFacilityLocation");
		//Required Columns
		inventoryElement.setAttribute(new Attribute(InventoryXML.Product_Id.getOfbizColName(), inventoryMap.get(InventoryXML.Product_Id.name())));
		inventoryElement.setAttribute(new Attribute("facilityId", inventoryMap.get(InventoryXML.Service_Center_id.name()).toUpperCase()));
		inventoryElement.setAttribute(new Attribute("productAssocTypeId", "PRODUCT_CONF"));
		inventoryElement.setAttribute(new Attribute("minimumStock", "1"));
		inventoryElement.setAttribute(new Attribute("locationSeqId", "TLTLTLLL01"));
		inventoryElement.setAttribute(new Attribute("moveQuantity", "1"));
		
		List<Element> allProductInventory= new ArrayList<>();
		allProductInventory.add(inventoryElement);
		Debug.logInfo("end getProductFacilityLocation method for ProductInventory Id--" + inventoryMap.get(InventoryXML.Product_Id.name()), module);
		return allProductInventory;
	}
	
	private static List<Element> getInventoryItemAndDetail() throws ProductInventoryImportException {
		Debug.logInfo("start getInventoryItemAndDetail method for ProductInventory Id--" + inventoryMap.get(InventoryXML.Product_Id.name()), module);
		List<Element> allProductInventory= new ArrayList<>();
		String productId = inventoryMap.get(InventoryXML.Product_Id.name()).toUpperCase();
		String facilityId = inventoryMap.get(InventoryXML.Service_Center_id.name()).toUpperCase();
		String newQuantityOnHandTotal = inventoryMap.get(InventoryXML.Inventroy_Quantity.name());
		String unitCost = inventoryMap.get(InventoryXML.Cost.name());
		BigDecimal unitCostBD = new BigDecimal(0);
		if(UtilValidate.isNotEmpty(unitCost)){
			unitCostBD = new BigDecimal(unitCost);
		}
		
		try {
			List  inventoryItemList = delegator.findList("InventoryItem", EntityCondition.makeCondition(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId), EntityOperator.AND, EntityCondition.makeCondition("facilityId" , EntityOperator.EQUALS, facilityId)), null, UtilMisc.toList("lastUpdatedStamp"), null, false);
	        GenericValue existedInventoryItemId = EntityUtil.getFirst(inventoryItemList);
	        
	        BigDecimal currentQuantityOnHandTotal = new BigDecimal(0);
	        BigDecimal unitCostExistingBD = new BigDecimal(0);
	        if (UtilValidate.isNotEmpty(existedInventoryItemId)){
	        	unitCostExistingBD = existedInventoryItemId.getBigDecimal("unitCost");
	        }
	        String inventoryItemId = null;
	        if (unitCostBD.compareTo(unitCostExistingBD) == 0){
	       	 inventoryItemId = existedInventoryItemId.getString("inventoryItemId");
	       	 currentQuantityOnHandTotal = existedInventoryItemId.getBigDecimal("quantityOnHandTotal");
	        }else{
	        	inventoryItemId = delegator.getNextSeqId("InventoryItem");
	        	Element inventoryItemElement = new Element("InventoryItem");
	    		//Required Columns
	    		inventoryItemElement.setAttribute(new Attribute(InventoryXML.Product_Id.getOfbizColName(), productId));
	    		inventoryItemElement.setAttribute(new Attribute("facilityId", facilityId));
	    		inventoryItemElement.setAttribute(new Attribute("inventoryItemTypeId", "NON_SERIAL_INV_ITEM"));
	    		inventoryItemElement.setAttribute(new Attribute("datetimeReceived", nowTimestamp));
	    		inventoryItemElement.setAttribute(new Attribute("currencyUomId", "INR"));
	    		inventoryItemElement.setAttribute(new Attribute("locationSeqId", "TLTLTLLL01"));
	    		inventoryItemElement.setAttribute(new Attribute("currencyUomId", "INR"));
	    		inventoryItemElement.setAttribute(new Attribute("ownerPartyId", facilityId));
	    		inventoryItemElement.setAttribute(new Attribute("quantityOnHandTotal", "0"));
	    		inventoryItemElement.setAttribute(new Attribute("availableToPromiseTotal", "0"));
	    		inventoryItemElement.setAttribute(new Attribute("accountingQuantityTotal", "0"));
	    		inventoryItemElement.setAttribute(new Attribute("inventoryItemId", inventoryItemId));
	    		inventoryItemElement.setAttribute(new Attribute("unitCost", unitCost+""));
	    		allProductInventory.add(inventoryItemElement);
	        }
	        
	        BigDecimal newQuantityOnHandTotalBD = new BigDecimal(newQuantityOnHandTotal);
	        if(UtilValidate.isNotEmpty(newQuantityOnHandTotalBD)) {
	        BigDecimal deltaQuantityHandOnTotal = newQuantityOnHandTotalBD.subtract(currentQuantityOnHandTotal);
	        //An condition where prevent zero row in db for IID
	           if(UtilValidate.isNotEmpty(deltaQuantityHandOnTotal) && deltaQuantityHandOnTotal.compareTo(BigDecimal.ZERO) != 0) {
	                String inventoryItemDetailSeqId = delegator.getNextSeqId("InventoryItemDetail");
	                Element inventoryItemDetailElement = new Element("InventoryItemDetail");
	        		//Required Columns
	        		inventoryItemDetailElement.setAttribute(new Attribute("inventoryItemId", inventoryItemId));
	        		inventoryItemDetailElement.setAttribute(new Attribute("inventoryItemDetailSeqId", inventoryItemDetailSeqId));
	        		inventoryItemDetailElement.setAttribute(new Attribute("effectiveDate", nowTimestamp));
	        		inventoryItemDetailElement.setAttribute(new Attribute("availableToPromiseDiff", deltaQuantityHandOnTotal+""));
	        		inventoryItemDetailElement.setAttribute(new Attribute("quantityOnHandDiff", deltaQuantityHandOnTotal+""));
	        		inventoryItemDetailElement.setAttribute(new Attribute("accountingQuantityDiff", "0"));
	        		inventoryItemDetailElement.setAttribute(new Attribute("unitCost", unitCost+""));
	        		allProductInventory.add(inventoryItemDetailElement);
	           }
	        }
		} catch(Exception e)
			{
				Debug.logError(e, module);
			}
		
		Debug.logInfo("end getInventoryItemAndDetail method for ProductInventory Id--" + inventoryMap.get(InventoryXML.Product_Id.name()), module);
		return allProductInventory;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, Object> importProductInventoryFromCsv(DispatchContext ctx, Map<String, ?> context)
			throws Exception {
		Debug.logInfo("start service importProductInventoryFromCsv", module);
		delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		nowTimestamp = UtilDateTime.toDateString(UtilDateTime.nowTimestamp(), "yyyy-MM-dd HH:mm:ss");
		Map<String, Object> resultMap = new HashMap<>();
		boolean errorDuringInsertion = false;
		List errorMsgs = new ArrayList();
		
		System.out.println("====38888===context===================="+context+"===========================");
	    List<String> error_list = new ArrayList<String>();
		ByteBuffer fileBytes = (ByteBuffer) context.get("uploadedFile");
		String uploadedFileName  = "uploadedFile" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) +".csv";
		String uploadedFilepath  = "";
		if(UtilValidate.isNotEmpty(uploadedFileName)) 
		{
		    if(!((uploadedFileName.toUpperCase()).endsWith("CSV") || (uploadedFileName.toUpperCase()).endsWith("XLSX"))) 
			{
		        error_list.add("Incorrect file format.");	
		    } else {
		        Map<String, Object> uploadFileCtx = new HashMap<String, Object>();
		        uploadFileCtx.put("userLogin",userLogin);
		        uploadFileCtx.put("uploadedFile",fileBytes);
		        uploadFileCtx.put("_uploadedFile_fileName",uploadedFileName);
		        try 
		        {
		        	resultMap = dispatcher.runSync("uploadFile", uploadFileCtx);
		        	System.out.println("=======result===================="+resultMap+"===========================");
		            //===========================================================
		            if(UtilValidate.isNotEmpty(resultMap.get("uploadFilePath")) && UtilValidate.isNotEmpty(resultMap.get("uploadFileName"))) 
		            {
		            	inventoryExtractInputPath = (String) resultMap.get("uploadFilePath");
					}
				} catch(Exception eeeee){
				
				}
			}
		}
		
		
		inventoryExtractXMLSuccessPath = FlexibleStringExpander.expandString(
				UtilProperties.getPropertyValue("gaadizo.properties", "inventoryextract-success-xml-path"), context);
		if (StringUtils.isBlank(inventoryExtractXMLSuccessPath)) {
			throw new ProductInventoryImportException("ProductInventory extract xml success path is not cofigured");
		}
		if (!new File(inventoryExtractXMLSuccessPath).exists()) {
			new File(inventoryExtractXMLSuccessPath).mkdirs();
		}
		try {
			Debug.logInfo("calling  service prepareAndImportProductInventoryXML", module);
			Map prepareAndImportProductInventoryXMLResult = dispatcher.runSync("prepareAndImportProductInventoryXML",
					UtilMisc.toMap("userLogin", context.get("userLogin")));
			List<String> messages = new ArrayList<>();
			Debug.logInfo(" Method insertIntoDB starts", module);

			String outputXmlFilePath = (String) prepareAndImportProductInventoryXMLResult.get("dumpDirPath");
			Debug.logInfo("----- prepareAndImportProductInventoryXMLResult : "+prepareAndImportProductInventoryXMLResult+" -----outputXmlFilePath : "+outputXmlFilePath, module);

			if (!ServiceUtil.isError(prepareAndImportProductInventoryXMLResult)) {
				Debug.logInfo("calling  service entityImportDirectoryForERP to import the xml", module);
				Map entityImportDirParams = UtilMisc.toMap("path", outputXmlFilePath, "userLogin",
						context.get("userLogin"));

				Map result = dispatcher.runSync("entityImportDirectoryForERP", entityImportDirParams);

				List<String> serviceMsg = (List) result.get("messages");
				for (String msg : serviceMsg) {
					messages.add(msg);
				}
				
				Debug.logInfo("Moving XMLFilesFromDir starts messages : "+messages, module);

				if (!messages.isEmpty() && messages.contains("SUCCESS")) {
					ImportUtility.moveXMLFilesFromDir(outputXmlFilePath, inventoryExtractXMLSuccessPath);
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


}
