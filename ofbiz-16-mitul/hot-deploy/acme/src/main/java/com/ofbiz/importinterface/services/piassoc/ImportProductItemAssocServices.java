package com.ofbiz.importinterface.services.piassoc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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

import com.ofbiz.importinterface.constants.ProductItemAssocXML;
import com.ofbiz.importinterface.exception.ProductItemAssocImportException;
import com.ofbiz.utility.ImportUtility;


public class ImportProductItemAssocServices {
	

	public static final String module = ImportProductItemAssocServices.class.getName();
	private static Map<String, String> piAssocMap;
	private static List<Map<String, String>> piAssocMapList;
	private static Map<String, String> failedProductItemAssocMap;
	private static Delegator delegator;
	private static String piAssocExtractInputPath;
	private static String piAssocExtractSuccessPath;
	private static String piAssocExtractOutputPath;
	private static String piAssocExtractInputErrorPath;
	private static String piAssocExtractXMLSuccessPath;
	private static String dateTimeFormatWCS;
	private static String nowTimestamp = "";
	private static String BLANK = "";

	public static Map<String, Object> prepareAndImportProductItemAssocXML(DispatchContext ctx, Map<String, ?> context)
			throws ProductItemAssocImportException {
		Debug.logInfo(" service prepareAndImportProductItemAssocXML starts", module);
		failedProductItemAssocMap = new HashMap<String, String>();
		delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		nowTimestamp = UtilDateTime.toDateString(UtilDateTime.nowTimestamp(), "yyyy-MM-dd HH:mm:ss");
		

		validateConfigurablePath(context);
		readFolder();
		try {
			generateErrorStatus();
		} catch (IOException e) {
			throw new ProductItemAssocImportException(e.getMessage());
		}
		//Reverting release
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("dumpDirPath", piAssocExtractOutputPath);
		resultMap.put("success", "success");
		Debug.logInfo(" service prepareAndImportProductItemAssocXML ends", module);
		return resultMap;
	}

	private static void validateConfigurablePath(Map<String, ?> context) throws ProductItemAssocImportException {
		Debug.logInfo(" check validateConfigurablePath starts", module);
		if(UtilValidate.isEmpty(piAssocExtractInputPath)) {
		piAssocExtractInputPath = UtilProperties.getPropertyValue("gaadizo.properties", "piassocextract-input-path");
		}
		Debug.logInfo("piAssocExtractInputPath : " +piAssocExtractInputPath, module);
		if (!new File(piAssocExtractInputPath).exists()) {
			throw new ProductItemAssocImportException("ProductItemAssoc Import Path is not configured");
		}

		piAssocExtractOutputPath = FlexibleStringExpander
				.expandString(UtilProperties.getPropertyValue("gaadizo.properties", "piassocextract-output-path"), context);
		Debug.logInfo("piAssocExtractOutputPath : " +piAssocExtractOutputPath, module);
		if (StringUtils.isBlank(piAssocExtractOutputPath)) {
			throw new ProductItemAssocImportException("ProductItemAssoc import output path is not configured");
		}
		if (!new File(piAssocExtractOutputPath).exists()) {
			new File(piAssocExtractOutputPath).mkdirs();
		}

		piAssocExtractInputErrorPath = FlexibleStringExpander.expandString(
				UtilProperties.getPropertyValue("gaadizo.properties", "piassocextract-input-error-path"), context);
		Debug.logInfo("piAssocExtractInputErrorPath : " +piAssocExtractInputErrorPath, module);
		if (StringUtils.isBlank(piAssocExtractInputErrorPath)) {
			throw new ProductItemAssocImportException("ProductItemAssoc import input error path is not configured");
		}
		if (!new File(piAssocExtractInputErrorPath).exists()) {
			new File(piAssocExtractInputErrorPath).mkdirs();
		}
		piAssocExtractSuccessPath = FlexibleStringExpander
				.expandString(UtilProperties.getPropertyValue("gaadizo.properties", "piassocextract-success-path"), context);
		Debug.logInfo("piAssocExtractSuccessPath : " +piAssocExtractSuccessPath, module);
		if (StringUtils.isBlank(piAssocExtractSuccessPath)) {
			throw new ProductItemAssocImportException("ProductItemAssoc import success error path is not configured");
		}
		if (!new File(piAssocExtractSuccessPath).exists()) {
			new File(piAssocExtractSuccessPath).mkdirs();
		}

		dateTimeFormatWCS = UtilProperties.getPropertyValue("gaadizo.properties", "datetime-format-from-wcs");
		if (StringUtils.isBlank(dateTimeFormatWCS)) {
			throw new ProductItemAssocImportException("dateTimeFormatWCS is not configured");
		}
		Debug.logInfo(" check validateConfigurablePath ends", module);
	}

	private static void generateErrorStatus() throws IOException {
		final String FILE_HEADER = "productId,Status,Comments";
		if (failedProductItemAssocMap != null && !failedProductItemAssocMap.isEmpty()) {
			FileWriter fileWriter = null;
			String filePath = piAssocExtractInputErrorPath + "error_.csv";
			try {
				fileWriter = new FileWriter(filePath);
				fileWriter.append(FILE_HEADER);
				fileWriter.append("\n");
				for (Map.Entry<String, String> failedProductItemAssoc : failedProductItemAssocMap.entrySet()) {
					fileWriter.append(failedProductItemAssoc.getKey());
					fileWriter.append(",");
					fileWriter.append("Error");
					fileWriter.append(",");
					fileWriter.append(failedProductItemAssoc.getValue());
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

	private static void readFolder() throws ProductItemAssocImportException {
		try {
			Debug.logInfo(" reading files starts", module);
			File folder = new File(piAssocExtractInputPath);
			File[] listOfFiles = null;
			if (folder != null) {
				listOfFiles = folder.listFiles();
				if (listOfFiles.length > 0) {
					for (final File file : listOfFiles) {

						if (file.isFile()
								&& StringUtils.equalsIgnoreCase("csv", FilenameUtils.getExtension(file.getName()))) {
							Debug.logInfo("satrt reading file--" + file.getName(), module);
							readCSVAndConvertToProductItemAssocXML(file);
							Debug.logInfo("end reading file--" + file.getName(), module);
						}
						try {
							File destDir = new File(piAssocExtractSuccessPath);
							Debug.logInfo("copying --" + file.getName() + " to piAssoc extract success path", module);
							FileUtils.copyFileToDirectory(file, destDir);
							Debug.logInfo("delete --" + file.getName() + " from piAssoc extract input path", module);
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
			throw new ProductItemAssocImportException(e.getMessage());

		}
	}

	public static void readCSVAndConvertToProductItemAssocXML(File file) throws IOException, ProductItemAssocImportException {
		// String csvFile ="F:\\Sample_data\\input\\Quancious_ProductItemAssocExtract_2017-12-29_11_58.csv";
		Debug.logInfo("satrt reading csv line by line--", module);
		String line = "";
		String csvSplitBy = ",";
		piAssocMapList = new ArrayList();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
			int count = 0;
			while ((line = br.readLine()) != null) {
				if (count == 0) {
					count++;
					continue;
				}
				String[] piAssocLine = line.split(csvSplitBy);
				piAssocMapList.add(buildDataRows(piAssocLine, file));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ProductItemAssocImportException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				br.close();
			}
		}
		Debug.logInfo("end reading csv line by line--", module);
		//createProductItemAssoc(piAssocMapList, file.getName());
		parseProductItemAssocFromMapList(file.getName());
		
	}

	private static Map<String, String> buildDataRows(String[] s, File file) throws ProductItemAssocImportException {
		Map<String, String> data = new HashMap<String, String>();
		try {
			for (ProductItemAssocXML piAssocXMLs : ProductItemAssocXML.values()) {
				data.put(piAssocXMLs.name(), ("null".equalsIgnoreCase(s[piAssocXMLs.getCode()]) ? " "
						: StringUtils.replace(s[piAssocXMLs.getCode()], "~", ",")));
				// changed ~ to ',' because if csv identifies , in any of its
				// value then column get shifted to other
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			try {
				File destDir = new File(piAssocExtractInputErrorPath);
				FileUtils.copyFileToDirectory(file, destDir);
				//file.delete();
			} catch (IOException e1) {
				throw new ProductItemAssocImportException(e1.getMessage());
			}
		}
		return data;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static boolean isExist(String entityName, Map fields) {
		List<GenericValue> piAssocList;
		try {
			piAssocList = delegator.findByAnd(entityName, fields,null,false);
			Debug.logInfo("from isExist() piAssocList--" + piAssocList, module);
			
			if (UtilValidate.isEmpty(piAssocList)) {
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
	private static void parseProductItemAssocFromMapList(String fileName) throws IOException, ProductItemAssocImportException {
		Debug.logInfo("start parseProductItemAssocFromMapList--", module);
		List<Element> contents = new ArrayList<Element>();
		
		Debug.logInfo("parsing piAssoc from map start--", module);
		int size = piAssocMapList.size();
		for (int i = 0; i < size; i++) {
			try {
				piAssocMap = piAssocMapList.get(i);
				if (isExist("Product", UtilMisc.toMap("productId", piAssocMap.get(ProductItemAssocXML.Service_Product_Id.name()))) && isExist("Product", UtilMisc.toMap("productId", piAssocMap.get(ProductItemAssocXML.Service_Item_Id.name())))) {
					contents.addAll(getProductAssoc());
					contents.addAll(getProductFacility());
				}
			} catch (ProductItemAssocImportException e) {
				failedProductItemAssocMap.put(piAssocMap.get(ProductItemAssocXML.Service_Item_Id.name()), e.getMessage());
			}
		}
		Debug.logInfo("parsing piAssoc from map end--", module);
		
		Element piAssoc = new Element("entity-engine-xml");
		piAssoc.addContent(contents);
		writeXMLToFile(piAssoc);
		Debug.logInfo("start parseProductItemAssocFromMapList--", module);
	}

	private static void writeXMLToFile(Element piAssoc) throws IOException {
		XMLOutputter xmlOutput = new XMLOutputter();
		// display nice
		xmlOutput.setFormat(Format.getPrettyFormat());
		Document doc = new Document(piAssoc);
		doc.setRootElement(piAssoc);
		OutputStreamWriter writer = null;
		try {
			Date dNow = new Date();
		    SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs");
		    String datetime = ft.format(dNow);
			String filePath = piAssocExtractOutputPath + "piAssoc_" +datetime+ ".xml";
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

	private static List<Element> getProductAssoc() throws ProductItemAssocImportException {
		Debug.logInfo("start getProductAssoc method for ProductItemAssoc Id--" + piAssocMap.get(ProductItemAssocXML.Service_Product_Id.name()), module);
		Element piAssocElement = new Element("ProductAssoc");
		//Required Columns
		piAssocElement.setAttribute(new Attribute(ProductItemAssocXML.Service_Product_Id.getOfbizColName(), piAssocMap.get(ProductItemAssocXML.Service_Product_Id.name())));
		piAssocElement.setAttribute(new Attribute(ProductItemAssocXML.Vehicle_Model_Id.getOfbizColName(), piAssocMap.get(ProductItemAssocXML.Vehicle_Model_Id.name())));
		piAssocElement.setAttribute(new Attribute(ProductItemAssocXML.Service_Item_Id.getOfbizColName(), piAssocMap.get(ProductItemAssocXML.Service_Item_Id.name())));
		if (isExist("Party", UtilMisc.toMap("partyId", piAssocMap.get(ProductItemAssocXML.Service_Provider_Id.name()))))
			piAssocElement.setAttribute(new Attribute(ProductItemAssocXML.Service_Provider_Id.getOfbizColName(), piAssocMap.get(ProductItemAssocXML.Service_Provider_Id.name())));
		else
			throw new ProductItemAssocImportException("Service_Provider_Id is not configued in system as Party. Please with a correct Service_Provider_Id.");
		piAssocElement.setAttribute(new Attribute("productAssocTypeId", "PRODUCT_CONF"));
		if(UtilValidate.isNotEmpty(piAssocMap.get(ProductItemAssocXML.From_Date.name())))
			piAssocElement.setAttribute(new Attribute(ProductItemAssocXML.From_Date.getOfbizColName(), nowTimestamp));
		else
			piAssocElement.setAttribute(new Attribute(ProductItemAssocXML.From_Date.getOfbizColName(), nowTimestamp));
		if(UtilValidate.isNotEmpty(piAssocMap.get(ProductItemAssocXML.Thru_Date.name())))
			piAssocElement.setAttribute(new Attribute(ProductItemAssocXML.Thru_Date.getOfbizColName(), nowTimestamp));
		else
			piAssocElement.setAttribute(new Attribute(ProductItemAssocXML.Thru_Date.getOfbizColName(), nowTimestamp));
		
		piAssocElement.setAttribute(new Attribute("sequenceNum", "1"));
		if(UtilValidate.isNotEmpty(piAssocMap.get(ProductItemAssocXML.Quantity.name())))
			piAssocElement.setAttribute(new Attribute(ProductItemAssocXML.Quantity.getOfbizColName(), piAssocMap.get(ProductItemAssocXML.Quantity.name())));
		else
			piAssocElement.setAttribute(new Attribute(ProductItemAssocXML.Quantity.getOfbizColName(), ""));
		if(UtilValidate.isNotEmpty(piAssocMap.get(ProductItemAssocXML.Service_External_Id.name())))
			piAssocElement.setAttribute(new Attribute(ProductItemAssocXML.Service_External_Id.getOfbizColName(), piAssocMap.get(ProductItemAssocXML.Service_External_Id.name())));
		else
			piAssocElement.setAttribute(new Attribute(ProductItemAssocXML.Service_External_Id.getOfbizColName(), ""));
		if(UtilValidate.isNotEmpty(piAssocMap.get(ProductItemAssocXML.Service_Item_Type.name())))
			piAssocElement.setAttribute(new Attribute(ProductItemAssocXML.Service_Item_Type.getOfbizColName(), piAssocMap.get(ProductItemAssocXML.Service_Item_Type.name())));
		else
			piAssocElement.setAttribute(new Attribute(ProductItemAssocXML.Service_Item_Type.getOfbizColName(), ""));
		if(UtilValidate.isNotEmpty(piAssocMap.get(ProductItemAssocXML.Oil_Type.name())))
			piAssocElement.setAttribute(new Attribute(ProductItemAssocXML.Oil_Type.getOfbizColName(), piAssocMap.get(ProductItemAssocXML.Oil_Type.name())));
		else
			piAssocElement.setAttribute(new Attribute(ProductItemAssocXML.Oil_Type.getOfbizColName(), ""));
		
		List<Element> allProductItemAssoc= new ArrayList<>();
		allProductItemAssoc.add(piAssocElement);
		Debug.logInfo("end getProductAssoc method for ProductItemAssoc Id--" + piAssocMap.get(ProductItemAssocXML.Service_Item_Id.name()), module);
		return allProductItemAssoc;
	}
	
	private static List<Element> getProductFacility() throws ProductItemAssocImportException {
		Debug.logInfo("start getProductFacility method for Service_Product_Id--" + piAssocMap.get(ProductItemAssocXML.Service_Product_Id.name()), module);
		Element pFacilityElement = new Element("ProductFacility");
		//Required Columns
		pFacilityElement.setAttribute(new Attribute(ProductItemAssocXML.Service_Product_Id.getOfbizColName(), piAssocMap.get(ProductItemAssocXML.Service_Product_Id.name())));
		pFacilityElement.setAttribute(new Attribute("productFacilityTypeId", "FASTSERVICE"));
		pFacilityElement.setAttribute(new Attribute("facilityId", piAssocMap.get(ProductItemAssocXML.Service_Provider_Id.name())));
		pFacilityElement.setAttribute(new Attribute("ownerId", piAssocMap.get(ProductItemAssocXML.Service_Provider_Id.name())));
		pFacilityElement.setAttribute(new Attribute("productFacilitySeqId", "3"));
		pFacilityElement.setAttribute(new Attribute("lastInventoryCount", "1000.000000"));
		
		List<Element> allProductItemAssoc= new ArrayList<>();
		allProductItemAssoc.add(pFacilityElement);
		Debug.logInfo("end getProductFacility method for Service_Product_Id Id--" + piAssocMap.get(ProductItemAssocXML.Service_Product_Id.name()), module);
		return allProductItemAssoc;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, Object> importProductItemAssocFromCsv(DispatchContext ctx, Map<String, ?> context)
			throws Exception {
		Debug.logInfo("start service importProductItemAssocFromCsv", module);
		delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		nowTimestamp = UtilDateTime.toDateString(UtilDateTime.nowTimestamp(), "yyyy-MM-dd HH:mm:ss");
		Map<String, Object> resultMap = new HashMap<>();
		boolean errorDuringInsertion = false;
		List errorMsgs = new ArrayList();
		
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
		            //===========================================================
		            if(UtilValidate.isNotEmpty(resultMap.get("uploadFilePath")) && UtilValidate.isNotEmpty(resultMap.get("uploadFileName"))) 
		            {
		            	piAssocExtractInputPath = (String) resultMap.get("uploadFilePath");
					}
				} catch(Exception eeeee){
				
				}
			}
		}
		
		piAssocExtractXMLSuccessPath = FlexibleStringExpander.expandString(
				UtilProperties.getPropertyValue("gaadizo.properties", "piassocextract-success-xml-path"), context);
		if (StringUtils.isBlank(piAssocExtractXMLSuccessPath)) {
			throw new ProductItemAssocImportException("ProductItemAssoc extract xml success path is not cofigured");
		}
		if (!new File(piAssocExtractXMLSuccessPath).exists()) {
			new File(piAssocExtractXMLSuccessPath).mkdirs();
		}
		try {
			Debug.logInfo("calling  service prepareAndImportProductItemAssocXML", module);
			Map prepareAndImportProductItemAssocXMLResult = dispatcher.runSync("prepareAndImportProductItemAssocXML",
					UtilMisc.toMap("userLogin", context.get("userLogin")));
			List<String> messages = new ArrayList<>();
			Debug.logInfo(" Method insertIntoDB starts", module);

			String outputXmlFilePath = (String) prepareAndImportProductItemAssocXMLResult.get("dumpDirPath");
			Debug.logInfo("----- prepareAndImportProductItemAssocXMLResult : "+prepareAndImportProductItemAssocXMLResult+" -----outputXmlFilePath : "+outputXmlFilePath, module);

			if (!ServiceUtil.isError(prepareAndImportProductItemAssocXMLResult)) {
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
					ImportUtility.moveXMLFilesFromDir(outputXmlFilePath, piAssocExtractXMLSuccessPath);
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
