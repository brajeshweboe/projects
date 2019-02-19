package com.ofbiz.importinterface.services.supplier;

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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.net.URL;
import org.apache.ofbiz.security.Security;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import java.util.Locale;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
//Changed lang -> lang3 for eclipse only
/*import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;*/
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
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import com.ofbiz.importinterface.constants.CategoryXML;
import com.ofbiz.importinterface.exception.CategoryImportException;
import com.ofbiz.utility.ImportUtility;
import com.ofbiz.utility.DateTimeUtility;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ImportSupplierServices {
	public static final String module = ImportSupplierServices.class.getName();
	private static Map<String, String> categoryMap;
	private static List<Map<String, String>> categoryMapList;
	private static List<Map<String, String>> categoryItemMapList;
	private static Map<String, String> failedCategoryMap;
	private static Delegator delegator;
	private static String warehouse, storeId;
	private static String categoryExtractInputPath;
	private static String categoryExtractSuccessPath;
	private static String categoryExtractOutputPath;
	private static String categoryExtractInputErrorPath;
	private static String categoryExtractXMLSuccessPath;
	private static String dateTimeFormatWCS;
	private static boolean calculateEstimateShipDate;
	private static String userLoginId = "";
	private static String nowTimestamp = "";
	private static String BLANK = "";

	public static Map<String, Object> prepareAndImportCategoryXML(DispatchContext ctx, Map<String, ?> context)
			throws CategoryImportException {
		Debug.logInfo(" service prepareAndImportCategoryXML starts", module);
		failedCategoryMap = new HashMap<String, String>();
		delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		userLoginId = userLogin.getString("userLoginId");
		nowTimestamp = UtilDateTime.toDateString(UtilDateTime.nowTimestamp(), "yyyy-MM-dd HH:mm:ss");
		

		validateConfigurablePath(context);
		readFolder();
		try {
			generateErrorStatus();
		} catch (IOException e) {
			throw new CategoryImportException(e.getMessage());
		}
		//Reverting release
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("dumpDirPath", categoryExtractOutputPath);
		resultMap.put("success", "success");
		Debug.logInfo(" service prepareAndImportCategoryXML ends", module);
		return resultMap;
	}

	private static void validateConfigurablePath(Map<String, ?> context) throws CategoryImportException {
		Debug.logInfo(" check validateConfigurablePath starts", module);
		if(UtilValidate.isEmpty(categoryExtractInputPath)){
		categoryExtractInputPath = UtilProperties.getPropertyValue("gaadizo.properties", "categoryextract-input-path");
		}
		Debug.logInfo("categoryExtractInputPath : " +categoryExtractInputPath, module);
		if (!new File(categoryExtractInputPath).exists()) {
			throw new CategoryImportException("Category Import Path is not configured");
		}

		categoryExtractOutputPath = FlexibleStringExpander
				.expandString(UtilProperties.getPropertyValue("gaadizo.properties", "categoryextract-output-path"), context);
		Debug.logInfo("categoryExtractOutputPath : " +categoryExtractOutputPath, module);
		if (StringUtils.isBlank(categoryExtractOutputPath)) {
			throw new CategoryImportException("Category import output path is not configured");
		}
		if (!new File(categoryExtractOutputPath).exists()) {
			new File(categoryExtractOutputPath).mkdirs();
		}

		categoryExtractInputErrorPath = FlexibleStringExpander.expandString(
				UtilProperties.getPropertyValue("gaadizo.properties", "categoryextract-input-error-path"), context);
		Debug.logInfo("categoryExtractInputErrorPath : " +categoryExtractInputErrorPath, module);
		if (StringUtils.isBlank(categoryExtractInputErrorPath)) {
			throw new CategoryImportException("Category import input error path is not configured");
		}
		if (!new File(categoryExtractInputErrorPath).exists()) {
			new File(categoryExtractInputErrorPath).mkdirs();
		}
		categoryExtractSuccessPath = FlexibleStringExpander
				.expandString(UtilProperties.getPropertyValue("gaadizo.properties", "categoryextract-success-path"), context);
		Debug.logInfo("categoryExtractSuccessPath : " +categoryExtractSuccessPath, module);
		if (StringUtils.isBlank(categoryExtractSuccessPath)) {
			throw new CategoryImportException("Category import success error path is not configured");
		}
		if (!new File(categoryExtractSuccessPath).exists()) {
			new File(categoryExtractSuccessPath).mkdirs();
		}

		warehouse = UtilProperties.getPropertyValue("gaadizo.properties", "warehouse");
		if (StringUtils.isBlank(warehouse)) {
			throw new CategoryImportException("Facility id is not configured");
		}
		storeId = UtilProperties.getPropertyValue("gaadizo.properties", "storeid");
		if (StringUtils.isBlank(storeId)) {
			throw new CategoryImportException("store-id is not configured");
		}
		dateTimeFormatWCS = UtilProperties.getPropertyValue("gaadizo.properties", "datetime-format-from-wcs");
		if (StringUtils.isBlank(dateTimeFormatWCS)) {
			throw new CategoryImportException("dateTimeFormatWCS is not configured");
		}
		Debug.logInfo(" check validateConfigurablePath ends", module);
	}

	private static void generateErrorStatus() throws IOException {
		final String FILE_HEADER = "categoryId,Status,Comments";
		if (failedCategoryMap != null && !failedCategoryMap.isEmpty()) {
			FileWriter fileWriter = null;
			String filePath = categoryExtractInputErrorPath + "error_.csv";
			try {
				fileWriter = new FileWriter(filePath);
				fileWriter.append(FILE_HEADER);
				fileWriter.append("\n");
				for (Map.Entry<String, String> failedCategory : failedCategoryMap.entrySet()) {
					fileWriter.append(failedCategory.getKey());
					fileWriter.append(",");
					fileWriter.append("Error");
					fileWriter.append(",");
					fileWriter.append(failedCategory.getValue());
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

	private static void readFolder() throws CategoryImportException {
		try {
			Debug.logInfo(" reading files starts", module);
			File folder = new File(categoryExtractInputPath);
			File[] listOfFiles = null;
			if (folder != null) {
				listOfFiles = folder.listFiles();
				if (listOfFiles.length > 0) {
					for (final File file : listOfFiles) {

						if (file.isFile()
								&& StringUtils.equalsIgnoreCase("csv", FilenameUtils.getExtension(file.getName()))) {
							Debug.logInfo("satrt reading file--" + file.getName(), module);
							readCSVAndConvertToCategoryXML(file);
							Debug.logInfo("end reading file--" + file.getName(), module);
						}
						try {
							File destDir = new File(categoryExtractSuccessPath);
							Debug.logInfo("copying --" + file.getName() + " to category extract success path", module);
							FileUtils.copyFileToDirectory(file, destDir);
							Debug.logInfo("delete --" + file.getName() + " from category extract input path", module);
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
			throw new CategoryImportException(e.getMessage());

		}
	}

	public static void readCSVAndConvertToCategoryXML(File file) throws IOException, CategoryImportException {
		// String csvFile ="F:\\Sample_data\\input\\Quancious_CategoryExtract_2017-12-29_11_58.csv";
		Debug.logInfo("satrt reading csv line by line--", module);
		String line = "";
		String csvSplitBy = ",";
		categoryMapList = new ArrayList();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
			int count = 0;
			while ((line = br.readLine()) != null) {
				if (count == 0) {
					count++;
					continue;
				}
				String[] categoryLine = line.split(csvSplitBy);
				categoryMapList.add(buildDataRows(categoryLine, file));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CategoryImportException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				br.close();
			}
		}
		Debug.logInfo("end reading csv line by line--", module);
		//createCategory(categoryMapList, file.getName());
		parseCategoryFromMapList(file.getName());
		
	}

	private static Map<String, String> buildDataRows(String[] s, File file) throws CategoryImportException {
		Map<String, String> data = new HashMap<String, String>();
		try {
			for (CategoryXML categoryXMLs : CategoryXML.values()) {
				data.put(categoryXMLs.name(), ("null".equalsIgnoreCase(s[categoryXMLs.getCode()]) ? " "
						: StringUtils.replace(s[categoryXMLs.getCode()], "~", ",")));
				// changed ~ to ',' because if csv identifies , in any of its
				// value then column get shifted to other
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			try {
				File destDir = new File(categoryExtractInputErrorPath);
				FileUtils.copyFileToDirectory(file, destDir);
				//file.delete();
			} catch (IOException e1) {
				throw new CategoryImportException(e1.getMessage());
			}
		}
		return data;
	}

/*	private static void createCategory(List<Map<String, String>> categoryList, String fileName) throws IOException {
		Debug.logInfo("create map for similar category from csv start--", module);
		int size = categoryList.size();
		for (int i = 0; i < size; i++) {
			try {
				generateCategoryAndRollup(categoryList.get(i), fileName);
			} catch (CategoryImportException e) {
				failedCategoryMap.put(categoryMap.get(CategoryXML.Category_Id.name()), e.getMessage());
			}
		}
		Debug.logInfo("create map for similar category from csv end--", module);
	}

	private static void generateCategoryAndRollup(Map<String, String> pCSVCategoryPerLine,
			String fileName) throws IOException, CategoryImportException {
		categoryMap = new HashMap<String, String>();
		categoryMap.put(CategoryXML.Category_Id.name(), pCSVCategoryPerLine.get(CategoryXML.Category_Id.name()));
		categoryMap.put(CategoryXML.Catgory_Type.name(), pCSVCategoryPerLine.get(CategoryXML.Catgory_Type.name()));
		categoryMap.put(CategoryXML.Parent_Category_ID.name(), pCSVCategoryPerLine.get(CategoryXML.Parent_Category_ID.name()));
		categoryMap.put(CategoryXML.Category_Name.name(), pCSVCategoryPerLine.get(CategoryXML.Category_Name.name()));
	    categoryMap.put(CategoryXML.Description.name(), pCSVCategoryPerLine.get(CategoryXML.Description.name()));
		categoryMap.put(CategoryXML.Long_Description.name(), pCSVCategoryPerLine.get(CategoryXML.Long_Description.name()));
		categoryMap.put(CategoryXML.Category_Image_URL.name(), pCSVCategoryPerLine.get(CategoryXML.Category_Image_URL.name()));
		if (!isExist("ProductCategory", UtilMisc.toMap("productCategoryId", categoryMap.get(CategoryXML.Category_Id.name())))) {
			//parseCategoryFromMapList(fileName);
		}
	}*/

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static boolean isExist(String entityName, Map fields) {
		List<GenericValue> categoryList;
		try {
			categoryList = delegator.findByAnd(entityName, fields,null,false);
			Debug.logInfo("from isExist() categoryList--" + categoryList, module);
			
			/*if (UtilValidate.isEmpty(categoryList)) {
				return false;
			} else {
				return true;
			}*/
		} catch (GenericEntityException e) {
			Debug.logError(e.getMessage(), module);
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private static void parseCategoryFromMapList(String fileName) throws IOException, CategoryImportException {
		Debug.logInfo("start parseCategoryFromMapList--", module);
		List<Element> contents = new ArrayList<Element>();
		
		Debug.logInfo("parsing category from map start--", module);
		int size = categoryMapList.size();
		for (int i = 0; i < size; i++) {
			try {
				categoryMap = categoryMapList.get(i);
				if (!isExist("ProductCategory", UtilMisc.toMap("productCategoryId", categoryMap.get(CategoryXML.Category_Id.name())))) {
					contents.addAll(getProductCategoryAndRollup());
				}
			} catch (CategoryImportException e) {
				failedCategoryMap.put(categoryMap.get(CategoryXML.Category_Id.name()), e.getMessage());
			}
		}
		Debug.logInfo("parsing category from map end--", module);
		
		Element category = new Element("entity-engine-xml");
		category.addContent(contents);
		writeXMLToFile(category);
		Debug.logInfo("start parseCategoryFromMapList--", module);
	}

	private static void writeXMLToFile(Element category) throws IOException {
		XMLOutputter xmlOutput = new XMLOutputter();
		// display nice
		xmlOutput.setFormat(Format.getPrettyFormat());
		Document doc = new Document(category);
		doc.setRootElement(category);
		OutputStreamWriter writer = null;
		try {
			Date dNow = new Date();
		    SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs");
		    String datetime = ft.format(dNow);
			String filePath = categoryExtractOutputPath + "category_" +datetime+ ".xml";
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


	private static List<Element> getProductCategoryAndRollup() throws CategoryImportException {
	    List<Element> productCategoryAndRollup = new ArrayList<>();
	    /*Debug.logInfo("start getProductCategory method for Category Id--" + categoryMap.get(CategoryXML.Category_Id.name()), module);
		Element productCategory = new Element("ProductCategory");
		//element.setAttribute(new Attribute("productCategoryTypeId", "SALES_ORDER"));
		productCategory.setAttribute(new Attribute(CategoryXML.Category_Id.getOfbizColName(), categoryMap.get(CategoryXML.Category_Id.name())));
		productCategory.setAttribute(new Attribute(CategoryXML.Catgory_Type.getOfbizColName(), categoryMap.get(CategoryXML.Catgory_Type.name())));
		if(UtilValidate.isNotEmpty(categoryMap.get(CategoryXML.Parent_Category_ID.name())))
			productCategory.setAttribute(new Attribute(CategoryXML.Parent_Category_ID.getOfbizColName(), categoryMap.get(CategoryXML.Parent_Category_ID.name())));
		else
			productCategory.setAttribute(new Attribute(CategoryXML.Parent_Category_ID.getOfbizColName(), BLANK));
		if(UtilValidate.isNotEmpty(categoryMap.get(CategoryXML.Category_Name.name())))
			productCategory.setAttribute(new Attribute(CategoryXML.Category_Name.getOfbizColName(), categoryMap.get(CategoryXML.Category_Name.name())));
		else
			productCategory.setAttribute(new Attribute(CategoryXML.Category_Name.getOfbizColName(), BLANK));
		if(UtilValidate.isNotEmpty(categoryMap.get(CategoryXML.Long_Description.name())))
			productCategory.setAttribute(new Attribute(CategoryXML.Long_Description.getOfbizColName(), categoryMap.get(CategoryXML.Long_Description.name())));
		else
			productCategory.setAttribute(new Attribute(CategoryXML.Long_Description.getOfbizColName(), BLANK));
		if(UtilValidate.isNotEmpty(categoryMap.get(CategoryXML.Category_Image_URL.name())))
			productCategory.setAttribute(new Attribute(CategoryXML.Category_Image_URL.getOfbizColName(), categoryMap.get(CategoryXML.Category_Image_URL.name())));
		else
			productCategory.setAttribute(new Attribute(CategoryXML.Category_Image_URL.getOfbizColName(), BLANK));
		
		List<Element> productCategoryAndRollup = new ArrayList<>();
		productCategoryAndRollup.add(productCategory);
		
		if(UtilValidate.isNotEmpty(categoryMap.get(CategoryXML.Category_Id.name())) && UtilValidate.isNotEmpty(categoryMap.get(CategoryXML.Parent_Category_ID.name()))) {
			Element productCategoryRollup = new Element("ProductCategoryRollup");
			productCategoryRollup.setAttribute(new Attribute(CategoryXML.Category_Id.getOfbizColName(), categoryMap.get(CategoryXML.Category_Id.name())));
			productCategoryRollup.setAttribute(new Attribute(CategoryXML.Parent_Product_Category_Id.getOfbizColName(), categoryMap.get(CategoryXML.Parent_Category_ID.name())));
			productCategoryRollup.setAttribute(new Attribute(CategoryXML.From_Date.getOfbizColName(), UtilDateTime.nowTimestamp().toString()));
			productCategoryAndRollup.add(productCategoryRollup);
		}

		Debug.logInfo("end getProductCategory method for Category Id--" + categoryMap.get(CategoryXML.Category_Id.name()), module);
*/		return productCategoryAndRollup;
	}


	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, Object> importCategoryInERPFromCsv(DispatchContext ctx, Map<String, ?> context)
			throws Exception {
		Debug.logInfo("start service importCategoryInERPFromCsv", module);
		delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		userLoginId = userLogin.getString("userLoginId");
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
		            	categoryExtractInputPath = (String) resultMap.get("uploadFilePath");
					}
				} catch(Exception eeeee){
				
				}
			}
		}
		
		categoryExtractXMLSuccessPath = FlexibleStringExpander.expandString(
				UtilProperties.getPropertyValue("gaadizo.properties", "categoryextract-success-xml-path"), context);
		if (StringUtils.isBlank(categoryExtractXMLSuccessPath)) {
			throw new CategoryImportException("Category extract xml success path is not cofigured");
		}
		if (!new File(categoryExtractXMLSuccessPath).exists()) {
			new File(categoryExtractXMLSuccessPath).mkdirs();
		}
		try {
			Debug.logInfo("calling  service prepareAndImportCategoryXML", module);
			Map prepareAndImportCategoryXMLResult = dispatcher.runSync("prepareAndImportCategoryXML",
					UtilMisc.toMap("userLogin", context.get("userLogin")));
			List<String> messages = new ArrayList<>();
			Debug.logInfo(" Method insertIntoDB starts", module);

			String outputXmlFilePath = (String) prepareAndImportCategoryXMLResult.get("dumpDirPath");
			Debug.logInfo("calling  service entityImportDirectoryFOrERP outputXmlFilePath : "+outputXmlFilePath, module);

			if (!ServiceUtil.isError(prepareAndImportCategoryXMLResult)) {
				Debug.logInfo("calling  service entityImportDirectory to import the xml", module);
				Map entityImportDirParams = UtilMisc.toMap("path", outputXmlFilePath, "userLogin",
						context.get("userLogin"));

				Map result = dispatcher.runSync("entityImportDirectoryForERP", entityImportDirParams);

				List<String> serviceMsg = (List) result.get("messages");
				for (String msg : serviceMsg) {
					messages.add(msg);
				}
				
				Debug.logInfo("Moving XMLFilesFromDir starts messages : "+messages, module);

				if (!messages.isEmpty() && messages.contains("SUCCESS")) {
					ImportUtility.moveXMLFilesFromDir(outputXmlFilePath, categoryExtractXMLSuccessPath);
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
