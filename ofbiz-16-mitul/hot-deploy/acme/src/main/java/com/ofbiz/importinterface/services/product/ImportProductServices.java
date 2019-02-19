package com.ofbiz.importinterface.services.product;

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

import com.ofbiz.importinterface.constants.CategoryXML;
import com.ofbiz.importinterface.constants.ProductXML;
import com.ofbiz.importinterface.exception.ProductImportException;
import com.ofbiz.utility.ImportUtility;


public class ImportProductServices {
	

	public static final String module = ImportProductServices.class.getName();
	private static Map<String, String> productMap;
	private static List<Map<String, String>> productMapList;
	private static Map<String, String> failedProductMap;
	private static Delegator delegator;
	private static String productExtractInputPath;
	private static String productExtractSuccessPath;
	private static String productExtractOutputPath;
	private static String productExtractInputErrorPath;
	private static String productExtractXMLSuccessPath;
	private static String dateTimeFormatWCS;
	private static String userLoginId = "";
	private static String nowTimestamp = "";
	private static String BLANK = "";
	private static String PRIMARY_PRODUCT_CATEGORY_ID = "FINISHED_GOOD";

	public static Map<String, Object> prepareAndImportProductXML(DispatchContext ctx, Map<String, ?> context)
			throws ProductImportException {
		Debug.logInfo(" service prepareAndImportProductXML starts", module);
		failedProductMap = new HashMap<String, String>();
		delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		userLoginId = userLogin.getString("userLoginId");
		nowTimestamp = UtilDateTime.toDateString(UtilDateTime.nowTimestamp(), "yyyy-MM-dd HH:mm:ss");
		

		validateConfigurablePath(context);
		readFolder();
		try {
			generateErrorStatus();
		} catch (IOException e) {
			throw new ProductImportException(e.getMessage());
		}
		//Reverting release
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("dumpDirPath", productExtractOutputPath);
		resultMap.put("success", "success");
		Debug.logInfo(" service prepareAndImportProductXML ends", module);
		return resultMap;
	}

	private static void validateConfigurablePath(Map<String, ?> context) throws ProductImportException {
		Debug.logInfo(" check validateConfigurablePath starts", module);
		if(UtilValidate.isEmpty(productExtractInputPath)){
		productExtractInputPath = UtilProperties.getPropertyValue("gaadizo.properties", "productextract-input-path");
		}
		Debug.logInfo("productExtractInputPath : " +productExtractInputPath, module);
		if (!new File(productExtractInputPath).exists()) {
			throw new ProductImportException("Product Import Path is not configured");
		}

		productExtractOutputPath = FlexibleStringExpander
				.expandString(UtilProperties.getPropertyValue("gaadizo.properties", "productextract-output-path"), context);
		Debug.logInfo("productExtractOutputPath : " +productExtractOutputPath, module);
		if (StringUtils.isBlank(productExtractOutputPath)) {
			throw new ProductImportException("Product import output path is not configured");
		}
		if (!new File(productExtractOutputPath).exists()) {
			new File(productExtractOutputPath).mkdirs();
		}

		productExtractInputErrorPath = FlexibleStringExpander.expandString(
				UtilProperties.getPropertyValue("gaadizo.properties", "productextract-input-error-path"), context);
		Debug.logInfo("productExtractInputErrorPath : " +productExtractInputErrorPath, module);
		if (StringUtils.isBlank(productExtractInputErrorPath)) {
			throw new ProductImportException("Product import input error path is not configured");
		}
		if (!new File(productExtractInputErrorPath).exists()) {
			new File(productExtractInputErrorPath).mkdirs();
		}
		productExtractSuccessPath = FlexibleStringExpander
				.expandString(UtilProperties.getPropertyValue("gaadizo.properties", "productextract-success-path"), context);
		Debug.logInfo("productExtractSuccessPath : " +productExtractSuccessPath, module);
		if (StringUtils.isBlank(productExtractSuccessPath)) {
			throw new ProductImportException("Product import success error path is not configured");
		}
		if (!new File(productExtractSuccessPath).exists()) {
			new File(productExtractSuccessPath).mkdirs();
		}

		dateTimeFormatWCS = UtilProperties.getPropertyValue("gaadizo.properties", "datetime-format-from-wcs");
		if (StringUtils.isBlank(dateTimeFormatWCS)) {
			throw new ProductImportException("dateTimeFormatWCS is not configured");
		}
		Debug.logInfo(" check validateConfigurablePath ends", module);
	}

	private static void generateErrorStatus() throws IOException {
		final String FILE_HEADER = "productId,Status,Comments";
		if (failedProductMap != null && !failedProductMap.isEmpty()) {
			FileWriter fileWriter = null;
			String filePath = productExtractInputErrorPath + "error_"+nowTimestamp+".csv";
			try {
				fileWriter = new FileWriter(filePath);
				fileWriter.append(FILE_HEADER);
				fileWriter.append("\n");
				for (Map.Entry<String, String> failedProduct : failedProductMap.entrySet()) {
					fileWriter.append(failedProduct.getKey());
					fileWriter.append(",");
					fileWriter.append("Error");
					fileWriter.append(",");
					fileWriter.append(failedProduct.getValue());
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

	private static void readFolder() throws ProductImportException {
		try {
			Debug.logInfo(" reading files starts", module);
			File folder = new File(productExtractInputPath);
			File[] listOfFiles = null;
			if (folder != null) {
				listOfFiles = folder.listFiles();
				if (listOfFiles.length > 0) {
					for (final File file : listOfFiles) {

						if (file.isFile()
								&& StringUtils.equalsIgnoreCase("csv", FilenameUtils.getExtension(file.getName()))) {
							Debug.logInfo("satrt reading file--" + file.getName(), module);
							readCSVAndConvertToProductXML(file);
							Debug.logInfo("end reading file--" + file.getName(), module);
						}
						try {
							File destDir = new File(productExtractSuccessPath);
							Debug.logInfo("copying --" + file.getName() + " to product extract success path", module);
							FileUtils.copyFileToDirectory(file, destDir);
							Debug.logInfo("delete --" + file.getName() + " from product extract input path", module);
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
			throw new ProductImportException(e.getMessage());

		}
	}

	public static void readCSVAndConvertToProductXML(File file) throws IOException, ProductImportException {
		// String csvFile ="F:\\Sample_data\\input\\Quancious_ProductExtract_2017-12-29_11_58.csv";
		Debug.logInfo("satrt reading csv line by line--", module);
		String line = "";
		String csvSplitBy = ",";
		productMapList = new ArrayList();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
			int count = 0;
			while ((line = br.readLine()) != null) {
				if (count == 0) {
					count++;
					continue;
				}
				String[] productLine = line.split(csvSplitBy);
				productMapList.add(buildDataRows(productLine, file));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ProductImportException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				br.close();
			}
		}
		Debug.logInfo("end reading csv line by line--", module);
		//createProduct(productMapList, file.getName());
		parseProductFromMapList(file.getName());
		
	}

	private static Map<String, String> buildDataRows(String[] s, File file) throws ProductImportException {
		Map<String, String> data = new HashMap<String, String>();
		try {
			for (ProductXML productXMLs : ProductXML.values()) {
				data.put(productXMLs.name(), ("null".equalsIgnoreCase(s[productXMLs.getCode()]) ? " "
						: StringUtils.replace(s[productXMLs.getCode()], "~", ",")));
				// changed ~ to ',' because if csv identifies , in any of its
				// value then column get shifted to other
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			try {
				File destDir = new File(productExtractInputErrorPath);
				FileUtils.copyFileToDirectory(file, destDir);
				//file.delete();
			} catch (IOException e1) {
				throw new ProductImportException(e1.getMessage());
			}
		}
		return data;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static boolean isExist(String entityName, Map fields) {
		List<GenericValue> productList;
		try {
			productList = delegator.findByAnd(entityName, fields,null,false);
			Debug.logInfo("from isExist() productList--" + productList, module);
			
			if (UtilValidate.isEmpty(productList)) {
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
	private static void parseProductFromMapList(String fileName) throws IOException, ProductImportException {
		Debug.logInfo("start parseProductFromMapList--", module);
		List<Element> contents = new ArrayList<Element>();
		
		Debug.logInfo("parsing product from map start--", module);
		int size = productMapList.size();
		for (int i = 0; i < size; i++) {
			try {
				productMap = productMapList.get(i);
				if (!isExist("Product", UtilMisc.toMap("productId", productMap.get(ProductXML.Spare_Part_Number.name())))) {
					contents.addAll(getProductAndInfo());
					if (!isExist("ProductCategory", UtilMisc.toMap("productCategoryId", productMap.get(ProductXML.Product_Category_ID.name())))) {
						throw new ProductImportException("Product import input error -Product_Category_ID : " + productMap.get(ProductXML.Product_Category_ID.name()) + " is not configured. Please create the Product Category from Category Import first.");
					}
					contents.addAll(getProductCategoryMember());
				}
			} catch (ProductImportException e) {
				failedProductMap.put(productMap.get(ProductXML.Spare_Part_Number.name()), e.getMessage());
			}
		}
		Debug.logInfo("parsing product from map end--", module);
		
		Element product = new Element("entity-engine-xml");
		product.addContent(contents);
		writeXMLToFile(product);
		Debug.logInfo("start parseProductFromMapList--", module);
	}

	private static void writeXMLToFile(Element product) throws IOException {
		XMLOutputter xmlOutput = new XMLOutputter();
		// display nice
		xmlOutput.setFormat(Format.getPrettyFormat());
		Document doc = new Document(product);
		doc.setRootElement(product);
		OutputStreamWriter writer = null;
		try {
			Date dNow = new Date();
		    SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs");
		    String datetime = ft.format(dNow);
			String filePath = productExtractOutputPath + "product_" +datetime+ ".xml";
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


	private static List<Element> getProductAndInfo() throws ProductImportException {
		Debug.logInfo("start getProductAndInfo method for Product Id--" + productMap.get(ProductXML.Spare_Part_Number.name()), module);
		Element productElement = new Element("Product");
		//Required Columns
		productElement.setAttribute(new Attribute(ProductXML.Spare_Part_Number.getOfbizColName(), productMap.get(ProductXML.Spare_Part_Number.name())));
		
		//Optional Columns
		productElement.setAttribute(new Attribute("productTypeId", PRIMARY_PRODUCT_CATEGORY_ID));		
		if(UtilValidate.isNotEmpty(productMap.get(ProductXML.Product_Category_ID.name())))
			productElement.setAttribute(new Attribute("primaryProductCategoryId", productMap.get(ProductXML.Product_Category_ID.name())));
		else
			productElement.setAttribute(new Attribute("primaryProductCategoryId", BLANK));
		if(UtilValidate.isNotEmpty(productMap.get(ProductXML.Product_Type.name())))
			productElement.setAttribute(new Attribute(ProductXML.Product_Type.getOfbizColName(), productMap.get(ProductXML.Product_Type.name())));
		else
			productElement.setAttribute(new Attribute(ProductXML.Product_Type.getOfbizColName(), BLANK));
		if(UtilValidate.isNotEmpty(productMap.get(ProductXML.Product_Type_Id.name())))
			productElement.setAttribute(new Attribute(ProductXML.Product_Type_Id.getOfbizColName(), PRIMARY_PRODUCT_CATEGORY_ID));
		else
			productElement.setAttribute(new Attribute(ProductXML.Product_Type_Id.getOfbizColName(), BLANK));
		
		if(UtilValidate.isNotEmpty(productMap.get(ProductXML.Brand_Name.name())))
			productElement.setAttribute(new Attribute(ProductXML.Brand_Name.getOfbizColName(), productMap.get(ProductXML.Brand_Name.name())));
		else
			productElement.setAttribute(new Attribute(ProductXML.Brand_Name.getOfbizColName(), BLANK));
		if(UtilValidate.isNotEmpty(productMap.get(ProductXML.Spare_Part_Name.name())))
			productElement.setAttribute(new Attribute(ProductXML.Spare_Part_Name.getOfbizColName(), productMap.get(ProductXML.Spare_Part_Name.name())));
		else
			productElement.setAttribute(new Attribute(ProductXML.Spare_Part_Name.getOfbizColName(), BLANK));
		if(UtilValidate.isNotEmpty(productMap.get(ProductXML.Description.name())))
			productElement.setAttribute(new Attribute(ProductXML.Description.getOfbizColName(), productMap.get(ProductXML.Description.name())));
		else
			productElement.setAttribute(new Attribute(ProductXML.Description.getOfbizColName(), BLANK));
		if(UtilValidate.isNotEmpty(productMap.get(ProductXML.Description.name())))
			productElement.setAttribute(new Attribute("longDescription", productMap.get(ProductXML.Description.name())));
		else
			productElement.setAttribute(new Attribute("longDescription", BLANK));
		
		if(UtilValidate.isNotEmpty(productMap.get(ProductXML.Inventory_Unit_of_Measure.name())))
			productElement.setAttribute(new Attribute("quantityUomId", productMap.get(ProductXML.Inventory_Unit_of_Measure.name())));
		else
			productElement.setAttribute(new Attribute("quantityUomId", BLANK));
		if(UtilValidate.isNotEmpty(productMap.get(ProductXML.Product_Length.name())))
			productElement.setAttribute(new Attribute(ProductXML.Product_Length.getOfbizColName(), productMap.get(ProductXML.Product_Length.name())));
		else
			productElement.setAttribute(new Attribute(ProductXML.Product_Length.getOfbizColName(), BLANK));
		if(UtilValidate.isNotEmpty(productMap.get(ProductXML.Product_Width.name())))
			productElement.setAttribute(new Attribute(ProductXML.Product_Width.getOfbizColName(), productMap.get(ProductXML.Product_Width.name())));
		else
			productElement.setAttribute(new Attribute(ProductXML.Product_Width.getOfbizColName(), BLANK));
		if(UtilValidate.isNotEmpty(productMap.get(ProductXML.Product_Height.name())))
			productElement.setAttribute(new Attribute(ProductXML.Product_Height.getOfbizColName(), productMap.get(ProductXML.Product_Height.name())));
		else
			productElement.setAttribute(new Attribute(ProductXML.Product_Height.getOfbizColName(), BLANK));
		
		//ProductInfo Element
		Element productInfoElement = new Element("ProductInfo");
		//Required Columns
		productInfoElement.setAttribute(new Attribute(ProductXML.Spare_Part_Number.getOfbizColName(), productMap.get(ProductXML.Spare_Part_Number.name())));
		//Optional Columns
		if(UtilValidate.isNotEmpty(productMap.get(ProductXML.Barcode.name())))
			productInfoElement.setAttribute(new Attribute(ProductXML.Barcode.getOfbizColName(), productMap.get(ProductXML.Barcode.name())));
		else
			productInfoElement.setAttribute(new Attribute(ProductXML.Barcode.getOfbizColName(), BLANK));
		if(UtilValidate.isNotEmpty(productMap.get(ProductXML.HSN_SAC_Code.name())))
			productInfoElement.setAttribute(new Attribute(ProductXML.HSN_SAC_Code.getOfbizColName(), productMap.get(ProductXML.HSN_SAC_Code.name())));
		else
			productInfoElement.setAttribute(new Attribute(ProductXML.HSN_SAC_Code.getOfbizColName(), BLANK));
		if(UtilValidate.isNotEmpty(productMap.get(ProductXML.Sales_UOM.name())))
			productInfoElement.setAttribute(new Attribute(ProductXML.Sales_UOM.getOfbizColName(), productMap.get(ProductXML.Sales_UOM.name())));
		else
			productInfoElement.setAttribute(new Attribute(ProductXML.Sales_UOM.getOfbizColName(), BLANK));
		if(UtilValidate.isNotEmpty(productMap.get(ProductXML.Purchase_UOM.name())))
			productInfoElement.setAttribute(new Attribute(ProductXML.Purchase_UOM.getOfbizColName(), productMap.get(ProductXML.Purchase_UOM.name())));
		else
			productInfoElement.setAttribute(new Attribute(ProductXML.Purchase_UOM.getOfbizColName(), BLANK));
		if(UtilValidate.isNotEmpty(productMap.get(ProductXML.Packing_Type.name())))
			productInfoElement.setAttribute(new Attribute(ProductXML.Packing_Type.getOfbizColName(), productMap.get(ProductXML.Packing_Type.name())));
		else
			productInfoElement.setAttribute(new Attribute(ProductXML.Packing_Type.getOfbizColName(), BLANK));
		if(UtilValidate.isNotEmpty(productMap.get(ProductXML.Supplier_Id.name())))
			productInfoElement.setAttribute(new Attribute(ProductXML.Supplier_Id.getOfbizColName(), productMap.get(ProductXML.Supplier_Id.name())));
		else
			productInfoElement.setAttribute(new Attribute(ProductXML.Supplier_Id.getOfbizColName(), BLANK));
		if(UtilValidate.isNotEmpty(productMap.get(ProductXML.Oil_Capacity_In_Ltrs.name())))
			productInfoElement.setAttribute(new Attribute(ProductXML.Oil_Capacity_In_Ltrs.getOfbizColName(), productMap.get(ProductXML.Oil_Capacity_In_Ltrs.name())));
		else
			productInfoElement.setAttribute(new Attribute(ProductXML.Oil_Capacity_In_Ltrs.getOfbizColName(), BLANK));
		if(UtilValidate.isNotEmpty(productMap.get(ProductXML.Pack_Size.name())))
			productInfoElement.setAttribute(new Attribute(ProductXML.Pack_Size.getOfbizColName(), productMap.get(ProductXML.Pack_Size.name())));
		else
			productInfoElement.setAttribute(new Attribute(ProductXML.Pack_Size.getOfbizColName(), BLANK));
		
		if("SERVICE".equalsIgnoreCase(productMap.get(ProductXML.Product_Type_Id.name())))
			productInfoElement.setAttribute(new Attribute(ProductXML.Is_Inventory.getOfbizColName(),"N"));
		else if(UtilValidate.isNotEmpty(productMap.get(ProductXML.Is_Inventory.name())))
			productInfoElement.setAttribute(new Attribute(ProductXML.Is_Inventory.getOfbizColName(), productMap.get(ProductXML.Is_Inventory.name()).trim()));
		else
			productInfoElement.setAttribute(new Attribute(ProductXML.Is_Inventory.getOfbizColName(), "N"));
		
		List<Element> allProduct= new ArrayList<>();
		allProduct.add(productElement);
		allProduct.add(productInfoElement);
		

		Debug.logInfo("end getProduct method for Product Id--" + productMap.get(ProductXML.Spare_Part_Number.name()), module);
		return allProduct;
	}
	
	private static List<Element> getProductCategoryMember() throws ProductImportException {
		Debug.logInfo("start getProduct method for Product Id--" + productMap.get(ProductXML.Spare_Part_Number.name()), module);
		Element productElement = new Element("ProductCategoryMember");
		//Required Columns
		productElement.setAttribute(new Attribute("productCategoryId", productMap.get(ProductXML.Product_Category_ID.name())));
		productElement.setAttribute(new Attribute(ProductXML.Spare_Part_Number.getOfbizColName(), productMap.get(ProductXML.Spare_Part_Number.name())));
		productElement.setAttribute(new Attribute("fromDate", nowTimestamp));
		//Optional Columns
		if(UtilValidate.isNotEmpty(productMap.get(ProductXML.Spare_Part_Name.name())))
			productElement.setAttribute(new Attribute("comments", productMap.get(ProductXML.Spare_Part_Name.name())));
		else
			productElement.setAttribute(new Attribute("comments", BLANK));
		List<Element> allProduct= new ArrayList<>();
		allProduct.add(productElement);
		Debug.logInfo("end getProduct method for Product Id--" + productMap.get(ProductXML.Spare_Part_Number.name()), module);
		return allProduct;
	}
	
	private static List<Element> getProductPrice() throws ProductImportException {
		Debug.logInfo("start getProduct method for Product Id--" + productMap.get(ProductXML.Spare_Part_Number.name()), module);
		Element productElement = new Element("ProductPrice");
		//Required Columns
		productElement.setAttribute(new Attribute("productCategoryId", productMap.get(ProductXML.Product_Category_ID.name())));
		productElement.setAttribute(new Attribute(ProductXML.Spare_Part_Number.getOfbizColName(), productMap.get(ProductXML.Spare_Part_Number.name())));
		productElement.setAttribute(new Attribute("fromDate", nowTimestamp));
		//Optional Columns
		if(UtilValidate.isNotEmpty(productMap.get(ProductXML.Spare_Part_Name.name())))
			productElement.setAttribute(new Attribute("comments", productMap.get(ProductXML.Spare_Part_Name.name())));
		else
			productElement.setAttribute(new Attribute("comments", BLANK));
		List<Element> allProduct= new ArrayList<>();
		allProduct.add(productElement);
		Debug.logInfo("end getProduct method for Product Id--" + productMap.get(ProductXML.Spare_Part_Number.name()), module);
		return allProduct;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, Object> importProductInERPFromCsv(DispatchContext ctx, Map<String, ?> context)
			throws Exception {
		Debug.logInfo("start service importProductInERPFromCsv", module);
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
		            	productExtractInputPath = (String) resultMap.get("uploadFilePath");
					}
				} catch(Exception eeeee){
				
				}
			}
		}
		
		productExtractXMLSuccessPath = FlexibleStringExpander.expandString(
				UtilProperties.getPropertyValue("gaadizo.properties", "productextract-success-xml-path"), context);
		if (StringUtils.isBlank(productExtractXMLSuccessPath)) {
			throw new ProductImportException("Product extract xml success path is not cofigured");
		}
		if (!new File(productExtractXMLSuccessPath).exists()) {
			new File(productExtractXMLSuccessPath).mkdirs();
		}
		try {
			Debug.logInfo("calling  service prepareAndImportProductXML", module);
			Map prepareAndImportProductXMLResult = dispatcher.runSync("prepareAndImportProductXML",
					UtilMisc.toMap("userLogin", context.get("userLogin")));
			List<String> messages = new ArrayList<>();
			Debug.logInfo(" Method insertIntoDB starts", module);

			String outputXmlFilePath = (String) prepareAndImportProductXMLResult.get("dumpDirPath");
			Debug.logInfo("----- prepareAndImportProductXMLResult : "+prepareAndImportProductXMLResult+" -----outputXmlFilePath : "+outputXmlFilePath, module);

			if (!ServiceUtil.isError(prepareAndImportProductXMLResult)) {
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
					ImportUtility.moveXMLFilesFromDir(outputXmlFilePath, productExtractXMLSuccessPath);
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
