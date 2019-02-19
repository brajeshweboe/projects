package com.ofbiz.importinterface.services.pvprice;

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
import java.net.URL;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
/*import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;*/
//Changed lang -> lang3 for eclipse only
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
import com.ofbiz.importinterface.constants.PVPriceXML;
import com.ofbiz.importinterface.exception.PVPriceImportException;
import com.ofbiz.utility.ImportUtility;


public class ImportPVPriceServices {
	

	public static final String module = ImportPVPriceServices.class.getName();
	private static Map<String, String> pvPriceMap;
	private static List<Map<String, String>> pvPriceMapList;
	private static Map<String, String> failedPVPriceMap;
	private static Delegator delegator;
	private static String pvPriceExtractInputPath;
	private static String pvPriceExtractSuccessPath;
	private static String pvPriceExtractOutputPath;
	private static String pvPriceExtractInputErrorPath;
	private static String pvPriceExtractXMLSuccessPath;
	private static String dateTimeFormatWCS;
	private static String userLoginId = "";
	private static String nowTimestamp = "";
	private static String BLANK = "";

	public static Map<String, Object> prepareAndImportPVPriceXML(DispatchContext ctx, Map<String, ?> context)
			throws PVPriceImportException {
		Debug.logInfo(" service prepareAndImportPVPriceXML starts", module);
		failedPVPriceMap = new HashMap<String, String>();
		delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		userLoginId = userLogin.getString("userLoginId");
		nowTimestamp = UtilDateTime.toDateString(UtilDateTime.nowTimestamp(), "yyyy-MM-dd HH:mm:ss");
		

		validateConfigurablePath(context);
		readFolder();
		try {
			generateErrorStatus();
		} catch (IOException e) {
			throw new PVPriceImportException(e.getMessage());
		}
		//Reverting release
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("dumpDirPath", pvPriceExtractOutputPath);
		resultMap.put("success", "success");
		Debug.logInfo(" service prepareAndImportPVPriceXML ends", module);
		return resultMap;
	}

	private static void validateConfigurablePath(Map<String, ?> context) throws PVPriceImportException {
		Debug.logInfo(" check validateConfigurablePath starts", module);
		if(UtilValidate.isEmpty(pvPriceExtractInputPath)){
		    pvPriceExtractInputPath = UtilProperties.getPropertyValue("gaadizo.properties", "pvpriceextract-input-path");
		}
		Debug.logInfo("pvPriceExtractInputPath : " +pvPriceExtractInputPath, module);
		if (!new File(pvPriceExtractInputPath).exists()) {
			throw new PVPriceImportException("PVPrice Import Path is not configured");
		}

		pvPriceExtractOutputPath = FlexibleStringExpander
				.expandString(UtilProperties.getPropertyValue("gaadizo.properties", "pvpriceextract-output-path"), context);
		Debug.logInfo("pvPriceExtractOutputPath : " +pvPriceExtractOutputPath, module);
		if (StringUtils.isBlank(pvPriceExtractOutputPath)) {
			throw new PVPriceImportException("PVPrice import output path is not configured");
		}
		if (!new File(pvPriceExtractOutputPath).exists()) {
			new File(pvPriceExtractOutputPath).mkdirs();
		}

		pvPriceExtractInputErrorPath = FlexibleStringExpander.expandString(
				UtilProperties.getPropertyValue("gaadizo.properties", "pvpriceextract-input-error-path"), context);
		Debug.logInfo("pvPriceExtractInputErrorPath : " +pvPriceExtractInputErrorPath, module);
		if (StringUtils.isBlank(pvPriceExtractInputErrorPath)) {
			throw new PVPriceImportException("PVPrice import input error path is not configured");
		}
		if (!new File(pvPriceExtractInputErrorPath).exists()) {
			new File(pvPriceExtractInputErrorPath).mkdirs();
		}
		pvPriceExtractSuccessPath = FlexibleStringExpander
				.expandString(UtilProperties.getPropertyValue("gaadizo.properties", "pvpriceextract-success-path"), context);
		Debug.logInfo("pvPriceExtractSuccessPath : " +pvPriceExtractSuccessPath, module);
		if (StringUtils.isBlank(pvPriceExtractSuccessPath)) {
			throw new PVPriceImportException("PVPrice import success error path is not configured");
		}
		if (!new File(pvPriceExtractSuccessPath).exists()) {
			new File(pvPriceExtractSuccessPath).mkdirs();
		}

		dateTimeFormatWCS = UtilProperties.getPropertyValue("gaadizo.properties", "datetime-format-from-wcs");
		if (StringUtils.isBlank(dateTimeFormatWCS)) {
			throw new PVPriceImportException("dateTimeFormatWCS is not configured");
		}
		Debug.logInfo(" check validateConfigurablePath ends", module);
	}

	private static void generateErrorStatus() throws IOException {
		final String FILE_HEADER = "productId,Status,Comments";
		if (failedPVPriceMap != null && !failedPVPriceMap.isEmpty()) {
			FileWriter fileWriter = null;
			String filePath = pvPriceExtractInputErrorPath + "error_.csv";
			try {
				fileWriter = new FileWriter(filePath);
				fileWriter.append(FILE_HEADER);
				fileWriter.append("\n");
				for (Map.Entry<String, String> failedPVPrice : failedPVPriceMap.entrySet()) {
					fileWriter.append(failedPVPrice.getKey());
					fileWriter.append(",");
					fileWriter.append("Error");
					fileWriter.append(",");
					fileWriter.append(failedPVPrice.getValue());
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

	private static void readFolder() throws PVPriceImportException {
		try {
			Debug.logInfo(" reading files starts", module);
			File folder = new File(pvPriceExtractInputPath);
			File[] listOfFiles = null;
			if (folder != null) {
				listOfFiles = folder.listFiles();
				if (listOfFiles.length > 0) {
					for (final File file : listOfFiles) {

						if (file.isFile()
								&& StringUtils.equalsIgnoreCase("csv", FilenameUtils.getExtension(file.getName()))) {
							Debug.logInfo("satrt reading file--" + file.getName(), module);
							readCSVAndConvertToPVPriceXML(file);
							Debug.logInfo("end reading file--" + file.getName(), module);
						}
						try {
							File destDir = new File(pvPriceExtractSuccessPath);
							Debug.logInfo("copying --" + file.getName() + " to pvPrice extract success path", module);
							FileUtils.copyFileToDirectory(file, destDir);
							Debug.logInfo("delete --" + file.getName() + " from pvPrice extract input path", module);
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
			throw new PVPriceImportException(e.getMessage());

		}
	}

	public static void readCSVAndConvertToPVPriceXML(File file) throws IOException, PVPriceImportException {
		// String csvFile ="F:\\Sample_data\\input\\Quancious_PVPriceExtract_2017-12-29_11_58.csv";
		Debug.logInfo("satrt reading csv line by line--", module);
		String line = "";
		String csvSplitBy = ",";
		pvPriceMapList = new ArrayList();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
			int count = 0;
			while ((line = br.readLine()) != null) {
				if (count == 0) {
					count++;
					continue;
				}
				String[] pvPriceLine = line.split(csvSplitBy);
				pvPriceMapList.add(buildDataRows(pvPriceLine, file));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (PVPriceImportException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				br.close();
			}
		}
		Debug.logInfo("end reading csv line by line--", module);
		//createPVPrice(pvPriceMapList, file.getName());
		parsePVPriceFromMapList(file.getName());
		
	}

	private static Map<String, String> buildDataRows(String[] s, File file) throws PVPriceImportException {
		Map<String, String> data = new HashMap<String, String>();
		try {
			for (PVPriceXML pvPriceXMLs : PVPriceXML.values()) {
				data.put(pvPriceXMLs.name(), ("null".equalsIgnoreCase(s[pvPriceXMLs.getCode()]) ? " "
						: StringUtils.replace(s[pvPriceXMLs.getCode()], "~", ",")));
				// changed ~ to ',' because if csv identifies , in any of its
				// value then column get shifted to other
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			try {
				File destDir = new File(pvPriceExtractInputErrorPath);
				FileUtils.copyFileToDirectory(file, destDir);
				//file.delete();
			} catch (IOException e1) {
				throw new PVPriceImportException(e1.getMessage());
			}
		}
		return data;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static boolean isExist(String entityName, Map fields) {
		List<GenericValue> pvPriceList;
		try {
			pvPriceList = delegator.findByAnd(entityName, fields,null,false);
			Debug.logInfo("from isExist() pvPriceList--" + pvPriceList, module);
			
			if (UtilValidate.isEmpty(pvPriceList)) {
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
	private static void parsePVPriceFromMapList(String fileName) throws IOException, PVPriceImportException {
		Debug.logInfo("start parsePVPriceFromMapList--", module);
		List<Element> contents = new ArrayList<Element>();
		
		Debug.logInfo("parsing pvPrice from map start--", module);
		int size = pvPriceMapList.size();
		for (int i = 0; i < size; i++) {
			try {
				pvPriceMap = pvPriceMapList.get(i);
				BigDecimal pvPrice;
				if(UtilValidate.isNotEmpty(pvPriceMap.get(PVPriceXML.GaadiZo_Price.name())))
				{
					pvPrice = new BigDecimal(pvPriceMap.get(PVPriceXML.GaadiZo_Price.name()));
				}else{
					pvPrice = new BigDecimal(0);
				}
				if (!isExist("ProductVehiclePrice", UtilMisc.toMap("productId", pvPriceMap.get(PVPriceXML.Spare_Part_Number.name()),
						PVPriceXML.Vehicle_Id.getOfbizColName(), pvPriceMap.get(PVPriceXML.Vehicle_Id.name()),
						PVPriceXML.Service_Center_Id.getOfbizColName(), pvPriceMap.get(PVPriceXML.Service_Center_Id.name()),
						PVPriceXML.Facility_Id.getOfbizColName(), pvPriceMap.get(PVPriceXML.Facility_Id.name()),
						PVPriceXML.GaadiZo_Price.getOfbizColName(), pvPrice))) {
					contents.addAll(getPVPrice());
					contents.addAll(getProductPrice());
				}
			} catch (PVPriceImportException e) {
				failedPVPriceMap.put(pvPriceMap.get(PVPriceXML.Spare_Part_Number.name()), e.getMessage());
			}
		}
		Debug.logInfo("parsing pvPrice from map end--", module);
		
		Element pvPrice = new Element("entity-engine-xml");
		pvPrice.addContent(contents);
		writeXMLToFile(pvPrice);
		Debug.logInfo("start parsePVPriceFromMapList--", module);
	}

	private static void writeXMLToFile(Element pvPrice) throws IOException {
		XMLOutputter xmlOutput = new XMLOutputter();
		// display nice
		xmlOutput.setFormat(Format.getPrettyFormat());
		Document doc = new Document(pvPrice);
		doc.setRootElement(pvPrice);
		OutputStreamWriter writer = null;
		try {
			Date dNow = new Date();
		    SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs");
		    String datetime = ft.format(dNow);
			String filePath = pvPriceExtractOutputPath + "pvPrice_" +datetime+ ".xml";
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

	private static List<Element> getPVPrice() throws PVPriceImportException {
		Debug.logInfo("start getPVPrice method for PVPrice Id--" + pvPriceMap.get(PVPriceXML.Spare_Part_Number.name()), module);
		Element pvPriceElement = new Element("ProductVehiclePrice");
		//Required Columns
		pvPriceElement.setAttribute(new Attribute(PVPriceXML.Spare_Part_Number.getOfbizColName(), pvPriceMap.get(PVPriceXML.Spare_Part_Number.name())));
		pvPriceElement.setAttribute(new Attribute(PVPriceXML.Vehicle_Id.getOfbizColName(), pvPriceMap.get(PVPriceXML.Vehicle_Id.name())));
		pvPriceElement.setAttribute(new Attribute("fromDate", nowTimestamp));
		pvPriceElement.setAttribute(new Attribute(PVPriceXML.Service_Center_Id.getOfbizColName(), pvPriceMap.get(PVPriceXML.Service_Center_Id.name())));
		pvPriceElement.setAttribute(new Attribute(PVPriceXML.Facility_Id.getOfbizColName(), pvPriceMap.get(PVPriceXML.Facility_Id.name())));
		//Optional Columns
		if(UtilValidate.isNotEmpty(pvPriceMap.get(PVPriceXML.GaadiZo_Price.name())))
			pvPriceElement.setAttribute(new Attribute(PVPriceXML.GaadiZo_Price.getOfbizColName(), pvPriceMap.get(PVPriceXML.GaadiZo_Price.name())));
		else
			pvPriceElement.setAttribute(new Attribute(PVPriceXML.GaadiZo_Price.getOfbizColName(), BLANK));
		if(UtilValidate.isNotEmpty(pvPriceMap.get(PVPriceXML.Package_Discount.name())))
			pvPriceElement.setAttribute(new Attribute(PVPriceXML.Package_Discount.getOfbizColName(), pvPriceMap.get(PVPriceXML.Package_Discount.name())));
		else
			pvPriceElement.setAttribute(new Attribute(PVPriceXML.Package_Discount.getOfbizColName(), BLANK));
		if(UtilValidate.isNotEmpty(pvPriceMap.get(PVPriceXML.WorkShop_Price.name())))
			pvPriceElement.setAttribute(new Attribute(PVPriceXML.WorkShop_Price.getOfbizColName(), pvPriceMap.get(PVPriceXML.WorkShop_Price.name())));
		else
			pvPriceElement.setAttribute(new Attribute(PVPriceXML.WorkShop_Price.getOfbizColName(), BLANK));
		if(UtilValidate.isNotEmpty(pvPriceMap.get(PVPriceXML.Tax_Rate_Percentage.name())))
			pvPriceElement.setAttribute(new Attribute(PVPriceXML.Tax_Rate_Percentage.getOfbizColName(), pvPriceMap.get(PVPriceXML.Tax_Rate_Percentage.name())));
		else
			pvPriceElement.setAttribute(new Attribute(PVPriceXML.Tax_Rate_Percentage.getOfbizColName(), BLANK));
		if(UtilValidate.isNotEmpty(pvPriceMap.get(PVPriceXML.Description.name())))
			pvPriceElement.setAttribute(new Attribute(PVPriceXML.Description.getOfbizColName(), pvPriceMap.get(PVPriceXML.Description.name())));
		else
			pvPriceElement.setAttribute(new Attribute(PVPriceXML.Description.getOfbizColName(), BLANK));
		List<Element> allPVPrice= new ArrayList<>();
		allPVPrice.add(pvPriceElement);
		

		Debug.logInfo("end getPVPrice method for PVPrice Id--" + pvPriceMap.get(PVPriceXML.Spare_Part_Number.name()), module);
		return allPVPrice;
	}

	private static List<Element> getProductPrice() throws PVPriceImportException {
		Debug.logInfo("start getProductPrice method for PVPrice Id--" + pvPriceMap.get(PVPriceXML.Spare_Part_Number.name()), module);
		Element productPriceList = new Element("ProductPrice");
		//Required Columns
		productPriceList.setAttribute(new Attribute(PVPriceXML.Spare_Part_Number.getOfbizColName(), pvPriceMap.get(PVPriceXML.Spare_Part_Number.name())));
		productPriceList.setAttribute(new Attribute("productPriceTypeId", "LIST_PRICE"));
		productPriceList.setAttribute(new Attribute("productPricePurposeId", "PURCHASE"));
		productPriceList.setAttribute(new Attribute("currencyUomId", "INR"));
		productPriceList.setAttribute(new Attribute("productStoreGroupId", "_NA_"));
		productPriceList.setAttribute(new Attribute("fromDate", nowTimestamp));
		//Optional Columns
		if(UtilValidate.isNotEmpty(pvPriceMap.get(PVPriceXML.WorkShop_Price.name())))
			productPriceList.setAttribute(new Attribute("price", pvPriceMap.get(PVPriceXML.WorkShop_Price.name())));
		else
			productPriceList.setAttribute(new Attribute("price", BLANK));
		if(UtilValidate.isNotEmpty(pvPriceMap.get(PVPriceXML.Tax_Rate_Percentage.name())))
			productPriceList.setAttribute(new Attribute(PVPriceXML.Tax_Rate_Percentage.getOfbizColName(), pvPriceMap.get(PVPriceXML.Tax_Rate_Percentage.name())));
		else
			productPriceList.setAttribute(new Attribute(PVPriceXML.Tax_Rate_Percentage.getOfbizColName(), BLANK));
		
		Element productPriceDefault = new Element("ProductPrice");
		//Required Columns
		productPriceDefault.setAttribute(new Attribute(PVPriceXML.Spare_Part_Number.getOfbizColName(), pvPriceMap.get(PVPriceXML.Spare_Part_Number.name())));
		productPriceDefault.setAttribute(new Attribute("productPriceTypeId", "DEFAULT_PRICE"));
		productPriceDefault.setAttribute(new Attribute("productPricePurposeId", "PURCHASE"));
		productPriceDefault.setAttribute(new Attribute("currencyUomId", "INR"));
		productPriceDefault.setAttribute(new Attribute("productStoreGroupId", "_NA_"));
		productPriceDefault.setAttribute(new Attribute("fromDate", nowTimestamp));
		//Optional Columns
		if(UtilValidate.isNotEmpty(pvPriceMap.get(PVPriceXML.GaadiZo_Price.name())))
			productPriceDefault.setAttribute(new Attribute("price", pvPriceMap.get(PVPriceXML.GaadiZo_Price.name())));
		else
			productPriceDefault.setAttribute(new Attribute("price", BLANK));
		if(UtilValidate.isNotEmpty(pvPriceMap.get(PVPriceXML.Tax_Rate_Percentage.name())))
			productPriceDefault.setAttribute(new Attribute(PVPriceXML.Tax_Rate_Percentage.getOfbizColName(), pvPriceMap.get(PVPriceXML.Tax_Rate_Percentage.name())));
		else
			productPriceDefault.setAttribute(new Attribute(PVPriceXML.Tax_Rate_Percentage.getOfbizColName(), BLANK));
		
		Element productPriceMRP = new Element("ProductPrice");
		//Required Columns
		productPriceMRP.setAttribute(new Attribute(PVPriceXML.Spare_Part_Number.getOfbizColName(), pvPriceMap.get(PVPriceXML.Spare_Part_Number.name())));
		productPriceMRP.setAttribute(new Attribute("productPriceTypeId", "MAXIMUM_PRICE"));
		productPriceMRP.setAttribute(new Attribute("productPricePurposeId", "PURCHASE"));
		productPriceMRP.setAttribute(new Attribute("currencyUomId", "INR"));
		productPriceMRP.setAttribute(new Attribute("productStoreGroupId", "_NA_"));
		productPriceMRP.setAttribute(new Attribute("fromDate", nowTimestamp));
		//Optional Columns
		if(UtilValidate.isNotEmpty(pvPriceMap.get(PVPriceXML.MRP.name())))
			productPriceMRP.setAttribute(new Attribute("price", pvPriceMap.get(PVPriceXML.MRP.name())));
		else
			productPriceMRP.setAttribute(new Attribute("price", BLANK));
		if(UtilValidate.isNotEmpty(pvPriceMap.get(PVPriceXML.Tax_Rate_Percentage.name())))
			productPriceMRP.setAttribute(new Attribute(PVPriceXML.Tax_Rate_Percentage.getOfbizColName(), pvPriceMap.get(PVPriceXML.Tax_Rate_Percentage.name())));
		else
			productPriceMRP.setAttribute(new Attribute(PVPriceXML.Tax_Rate_Percentage.getOfbizColName(), BLANK));
		
		Element productPriceDiscount = new Element("ProductPrice");
		//Required Columns
		productPriceDiscount.setAttribute(new Attribute(PVPriceXML.Spare_Part_Number.getOfbizColName(), pvPriceMap.get(PVPriceXML.Spare_Part_Number.name())));
		productPriceDiscount.setAttribute(new Attribute("productPriceTypeId", "DISCOUNT"));
		productPriceDiscount.setAttribute(new Attribute("productPricePurposeId", "PURCHASE"));
		productPriceDiscount.setAttribute(new Attribute("currencyUomId", "INR"));
		productPriceDiscount.setAttribute(new Attribute("productStoreGroupId", "_NA_"));
		productPriceDiscount.setAttribute(new Attribute("fromDate", nowTimestamp));
		//Optional Columns
		if(UtilValidate.isNotEmpty(pvPriceMap.get(PVPriceXML.Package_Discount.name())))
			productPriceDiscount.setAttribute(new Attribute("price", pvPriceMap.get(PVPriceXML.Package_Discount.name())));
		else
			productPriceDiscount.setAttribute(new Attribute("price", BLANK));
		if(UtilValidate.isNotEmpty(pvPriceMap.get(PVPriceXML.Tax_Rate_Percentage.name())))
			productPriceDiscount.setAttribute(new Attribute(PVPriceXML.Tax_Rate_Percentage.getOfbizColName(), pvPriceMap.get(PVPriceXML.Tax_Rate_Percentage.name())));
		else
			productPriceDiscount.setAttribute(new Attribute(PVPriceXML.Tax_Rate_Percentage.getOfbizColName(), BLANK));
		
		List<Element> allPVPrice= new ArrayList<>();
		allPVPrice.add(productPriceList);
		allPVPrice.add(productPriceDefault);
		allPVPrice.add(productPriceMRP);
		allPVPrice.add(productPriceDiscount);
		

		Debug.logInfo("end getProductPrice method for ProductPrice Id--" + pvPriceMap.get(PVPriceXML.Spare_Part_Number.name()), module);
		return allPVPrice;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, Object> importPVPriceInERPFromCsv(DispatchContext ctx, Map<String, ?> context)
			throws Exception {
		Debug.logInfo("start service importPVPriceInERPFromCsv", module);
		delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		userLoginId = userLogin.getString("userLoginId");
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
		            	pvPriceExtractInputPath = (String) resultMap.get("uploadFilePath");
					}
				} catch(Exception eeeee){
				
				}
			}
		}
		
		
		pvPriceExtractXMLSuccessPath = FlexibleStringExpander.expandString(
				UtilProperties.getPropertyValue("gaadizo.properties", "pvpriceextract-success-xml-path"), context);
		if (StringUtils.isBlank(pvPriceExtractXMLSuccessPath)) {
			throw new PVPriceImportException("PVPrice extract xml success path is not cofigured");
		}
		if (!new File(pvPriceExtractXMLSuccessPath).exists()) {
			new File(pvPriceExtractXMLSuccessPath).mkdirs();
		}
		try {
			Debug.logInfo("calling  service prepareAndImportPVPriceXML", module);
			Map prepareAndImportPVPriceXMLResult = dispatcher.runSync("prepareAndImportPVPriceXML",
					UtilMisc.toMap("userLogin", context.get("userLogin")));
			List<String> messages = new ArrayList<>();
			Debug.logInfo(" Method insertIntoDB starts", module);

			String outputXmlFilePath = (String) prepareAndImportPVPriceXMLResult.get("dumpDirPath");
			Debug.logInfo("----- prepareAndImportPVPriceXMLResult : "+prepareAndImportPVPriceXMLResult+" -----outputXmlFilePath : "+outputXmlFilePath, module);

			if (!ServiceUtil.isError(prepareAndImportPVPriceXMLResult)) {
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
					ImportUtility.moveXMLFilesFromDir(outputXmlFilePath, pvPriceExtractXMLSuccessPath);
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
