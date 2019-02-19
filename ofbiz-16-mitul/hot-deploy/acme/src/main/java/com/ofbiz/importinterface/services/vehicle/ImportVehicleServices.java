package com.ofbiz.importinterface.services.vehicle;

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
import com.ofbiz.importinterface.constants.VehicleXML;
import com.ofbiz.importinterface.exception.VehicleImportException;
import com.ofbiz.utility.ImportUtility;


public class ImportVehicleServices {
	

	public static final String module = ImportVehicleServices.class.getName();
	private static Map<String, String> vehicleMap;
	private static List<Map<String, String>> vehicleMapList;
	private static Map<String, String> failedVehicleMap;
	private static Delegator delegator;
	private static String vehicleExtractInputPath;
	private static String vehicleExtractSuccessPath;
	private static String vehicleExtractOutputPath;
	private static String vehicleExtractInputErrorPath;
	private static String vehicleExtractXMLSuccessPath;
	private static String dateTimeFormatWCS;
	private static String userLoginId = "";
	private static String nowTimestamp = "";
	private static String BLANK = "";

	public static Map<String, Object> prepareAndImportVehicleXML(DispatchContext ctx, Map<String, ?> context)
			throws VehicleImportException {
		Debug.logInfo(" service prepareAndImportVehicleXML starts", module);
		failedVehicleMap = new HashMap<String, String>();
		delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		userLoginId = userLogin.getString("userLoginId");
		nowTimestamp = UtilDateTime.toDateString(UtilDateTime.nowTimestamp(), "yyyy-MM-dd HH:mm:ss");
		

		validateConfigurablePath(context);
		readFolder();
		try {
			generateErrorStatus();
		} catch (IOException e) {
			throw new VehicleImportException(e.getMessage());
		}
		//Reverting release
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("dumpDirPath", vehicleExtractOutputPath);
		resultMap.put("success", "success");
		Debug.logInfo(" service prepareAndImportVehicleXML ends", module);
		return resultMap;
	}

	private static void validateConfigurablePath(Map<String, ?> context) throws VehicleImportException {
		Debug.logInfo(" check validateConfigurablePath starts", module);
		if(UtilValidate.isEmpty(vehicleExtractInputPath)){
			vehicleExtractInputPath = UtilProperties.getPropertyValue("gaadizo.properties", "vehicleextract-input-path");
		}
		Debug.logInfo("vehicleExtractInputPath : " +vehicleExtractInputPath, module);
		if (!new File(vehicleExtractInputPath).exists()) {
			throw new VehicleImportException("Vehicle Import Path is not configured");
		}

		vehicleExtractOutputPath = FlexibleStringExpander
				.expandString(UtilProperties.getPropertyValue("gaadizo.properties", "vehicleextract-output-path"), context);
		Debug.logInfo("vehicleExtractOutputPath : " +vehicleExtractOutputPath, module);
		if (StringUtils.isBlank(vehicleExtractOutputPath)) {
			throw new VehicleImportException("Vehicle import output path is not configured");
		}
		if (!new File(vehicleExtractOutputPath).exists()) {
			new File(vehicleExtractOutputPath).mkdirs();
		}

		vehicleExtractInputErrorPath = FlexibleStringExpander.expandString(
				UtilProperties.getPropertyValue("gaadizo.properties", "vehicleextract-input-error-path"), context);
		Debug.logInfo("vehicleExtractInputErrorPath : " +vehicleExtractInputErrorPath, module);
		if (StringUtils.isBlank(vehicleExtractInputErrorPath)) {
			throw new VehicleImportException("Vehicle import input error path is not configured");
		}
		if (!new File(vehicleExtractInputErrorPath).exists()) {
			new File(vehicleExtractInputErrorPath).mkdirs();
		}
		vehicleExtractSuccessPath = FlexibleStringExpander
				.expandString(UtilProperties.getPropertyValue("gaadizo.properties", "vehicleextract-success-path"), context);
		Debug.logInfo("vehicleExtractSuccessPath : " +vehicleExtractSuccessPath, module);
		if (StringUtils.isBlank(vehicleExtractSuccessPath)) {
			throw new VehicleImportException("Vehicle import success error path is not configured");
		}
		if (!new File(vehicleExtractSuccessPath).exists()) {
			new File(vehicleExtractSuccessPath).mkdirs();
		}

		dateTimeFormatWCS = UtilProperties.getPropertyValue("gaadizo.properties", "datetime-format-from-wcs");
		if (StringUtils.isBlank(dateTimeFormatWCS)) {
			throw new VehicleImportException("dateTimeFormatWCS is not configured");
		}
		Debug.logInfo(" check validateConfigurablePath ends", module);
	}

	private static void generateErrorStatus() throws IOException {
		final String FILE_HEADER = "vehicleId,Status,Comments";
		if (failedVehicleMap != null && !failedVehicleMap.isEmpty()) {
			FileWriter fileWriter = null;
			String filePath = vehicleExtractInputErrorPath + "error_.csv";
			try {
				fileWriter = new FileWriter(filePath);
				fileWriter.append(FILE_HEADER);
				fileWriter.append("\n");
				for (Map.Entry<String, String> failedVehicle : failedVehicleMap.entrySet()) {
					fileWriter.append(failedVehicle.getKey());
					fileWriter.append(",");
					fileWriter.append("Error");
					fileWriter.append(",");
					fileWriter.append(failedVehicle.getValue());
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

	private static void readFolder() throws VehicleImportException {
		try {
			Debug.logInfo(" reading files starts", module);
			File folder = new File(vehicleExtractInputPath);
			File[] listOfFiles = null;
			if (folder != null) {
				listOfFiles = folder.listFiles();
				if (listOfFiles.length > 0) {
					for (final File file : listOfFiles) {

						if (file.isFile()
								&& StringUtils.equalsIgnoreCase("csv", FilenameUtils.getExtension(file.getName()))) {
							Debug.logInfo("satrt reading file--" + file.getName(), module);
							readCSVAndConvertToVehicleXML(file);
							Debug.logInfo("end reading file--" + file.getName(), module);
						}
						try {
							File destDir = new File(vehicleExtractSuccessPath);
							Debug.logInfo("copying --" + file.getName() + " to vehicle extract success path", module);
							FileUtils.copyFileToDirectory(file, destDir);
							Debug.logInfo("delete --" + file.getName() + " from vehicle extract input path", module);
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
			throw new VehicleImportException(e.getMessage());

		}
	}

	public static void readCSVAndConvertToVehicleXML(File file) throws IOException, VehicleImportException {
		// String csvFile ="F:\\Sample_data\\input\\Quancious_VehicleExtract_2017-12-29_11_58.csv";
		Debug.logInfo("satrt reading csv line by line--", module);
		String line = "";
		String csvSplitBy = ",";
		vehicleMapList = new ArrayList();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
			int count = 0;
			while ((line = br.readLine()) != null) {
				if (count == 0) {
					count++;
					continue;
				}
				String[] vehicleLine = line.split(csvSplitBy);
				vehicleMapList.add(buildDataRows(vehicleLine, file));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (VehicleImportException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				br.close();
			}
		}
		Debug.logInfo("end reading csv line by line--", module);
		//createVehicle(vehicleMapList, file.getName());
		parseVehicleFromMapList(file.getName());
		
	}

	private static Map<String, String> buildDataRows(String[] s, File file) throws VehicleImportException {
		Map<String, String> data = new HashMap<String, String>();
		try {
			for (VehicleXML vehicleXMLs : VehicleXML.values()) {
				data.put(vehicleXMLs.name(), ("null".equalsIgnoreCase(s[vehicleXMLs.getCode()]) ? " "
						: StringUtils.replace(s[vehicleXMLs.getCode()], "~", ",")));
				// changed ~ to ',' because if csv identifies , in any of its
				// value then column get shifted to other
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			try {
				File destDir = new File(vehicleExtractInputErrorPath);
				FileUtils.copyFileToDirectory(file, destDir);
				//file.delete();
			} catch (IOException e1) {
				throw new VehicleImportException(e1.getMessage());
			}
		}
		return data;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static boolean isExist(String entityName, Map fields) {
		List<GenericValue> vehicleList;
		try {
			vehicleList = delegator.findByAnd(entityName, fields,null,false);
			Debug.logInfo("from isExist() vehicleList--" + vehicleList, module);
			
			if (UtilValidate.isEmpty(vehicleList)) {
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
	private static void parseVehicleFromMapList(String fileName) throws IOException, VehicleImportException {
		Debug.logInfo("start parseVehicleFromMapList--", module);
		List<Element> contents = new ArrayList<Element>();
		
		Debug.logInfo("parsing vehicle from map start--", module);
		int size = vehicleMapList.size();
		for (int i = 0; i < size; i++) {
			try {
				vehicleMap = vehicleMapList.get(i);
				if (!isExist("Vehicle", UtilMisc.toMap("vehicleId", vehicleMap.get(VehicleXML.Vehicle_Id.name())))) {
					contents.addAll(getVehicle());
				}
			} catch (VehicleImportException e) {
				failedVehicleMap.put(vehicleMap.get(VehicleXML.Vehicle_Id.name()), e.getMessage());
			}
		}
		Debug.logInfo("parsing vehicle from map end--", module);
		
		Element vehicle = new Element("entity-engine-xml");
		vehicle.addContent(contents);
		writeXMLToFile(vehicle);
		Debug.logInfo("start parseVehicleFromMapList--", module);
	}

	private static void writeXMLToFile(Element vehicle) throws IOException {
		XMLOutputter xmlOutput = new XMLOutputter();
		// display nice
		xmlOutput.setFormat(Format.getPrettyFormat());
		Document doc = new Document(vehicle);
		doc.setRootElement(vehicle);
		OutputStreamWriter writer = null;
		try {
			Date dNow = new Date();
		    SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs");
		    String datetime = ft.format(dNow);
			String filePath = vehicleExtractOutputPath + "vehicle_" +datetime+ ".xml";
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


	private static List<Element> getVehicle() throws VehicleImportException {
		Debug.logInfo("start getVehicle method for Vehicle Id--" + vehicleMap.get(VehicleXML.Vehicle_Id.name()), module);
		Element vehicleElement = new Element("Vehicle");
		vehicleElement.setAttribute(new Attribute(VehicleXML.Vehicle_Id.getOfbizColName(), vehicleMap.get(VehicleXML.Vehicle_Id.name())));
		vehicleElement.setAttribute(new Attribute(VehicleXML.Vehicle_Model_Id.getOfbizColName(), vehicleMap.get(VehicleXML.Vehicle_Model_Id.name())));
		vehicleElement.setAttribute(new Attribute(VehicleXML.Vehicle_Manufacturer_Id.getOfbizColName(), vehicleMap.get(VehicleXML.Vehicle_Manufacturer_Id.name())));
		if(UtilValidate.isNotEmpty(vehicleMap.get(VehicleXML.Vehicle_Manufacturer_Code.name())))
			vehicleElement.setAttribute(new Attribute(VehicleXML.Vehicle_Manufacturer_Code.getOfbizColName(), vehicleMap.get(VehicleXML.Vehicle_Manufacturer_Code.name())));
		else
			vehicleElement.setAttribute(new Attribute(VehicleXML.Vehicle_Manufacturer_Code.getOfbizColName(), BLANK));
		if(UtilValidate.isNotEmpty(vehicleMap.get(VehicleXML.Category_Id.name())))
			vehicleElement.setAttribute(new Attribute(VehicleXML.Category_Id.getOfbizColName(), vehicleMap.get(VehicleXML.Category_Id.name())));
		else
			vehicleElement.setAttribute(new Attribute(VehicleXML.Category_Id.getOfbizColName(), BLANK));
		if(UtilValidate.isNotEmpty(vehicleMap.get(VehicleXML.Vehicle_Model_Name.name())))
			vehicleElement.setAttribute(new Attribute(VehicleXML.Vehicle_Model_Name.getOfbizColName(), vehicleMap.get(VehicleXML.Vehicle_Model_Name.name())));
		else
			vehicleElement.setAttribute(new Attribute(VehicleXML.Vehicle_Model_Name.getOfbizColName(), BLANK));
		if(UtilValidate.isNotEmpty(vehicleMap.get(VehicleXML.Vehicle_Manufacturer.name())))
			vehicleElement.setAttribute(new Attribute(VehicleXML.Vehicle_Manufacturer.getOfbizColName(), vehicleMap.get(VehicleXML.Vehicle_Manufacturer.name())));
		else
			vehicleElement.setAttribute(new Attribute(VehicleXML.Vehicle_Manufacturer.getOfbizColName(), BLANK));
		if(UtilValidate.isNotEmpty(vehicleMap.get(VehicleXML.Search_Name.name())))
			vehicleElement.setAttribute(new Attribute(VehicleXML.Search_Name.getOfbizColName(), vehicleMap.get(VehicleXML.Search_Name.name())));
		else
			vehicleElement.setAttribute(new Attribute(VehicleXML.Search_Name.getOfbizColName(), BLANK));
		if(UtilValidate.isNotEmpty(vehicleMap.get(VehicleXML.Year.name())))
			vehicleElement.setAttribute(new Attribute(VehicleXML.Year.getOfbizColName(), vehicleMap.get(VehicleXML.Year.name())));
		else
			vehicleElement.setAttribute(new Attribute(VehicleXML.Year.getOfbizColName(), BLANK));
		if(UtilValidate.isNotEmpty(vehicleMap.get(VehicleXML.Variant.name())))
			vehicleElement.setAttribute(new Attribute(VehicleXML.Variant.getOfbizColName(), vehicleMap.get(VehicleXML.Variant.name())));
		else
			vehicleElement.setAttribute(new Attribute(VehicleXML.Variant.getOfbizColName(), BLANK));
		if(UtilValidate.isNotEmpty(vehicleMap.get(VehicleXML.Power.name())))
			vehicleElement.setAttribute(new Attribute(VehicleXML.Power.getOfbizColName(), vehicleMap.get(VehicleXML.Power.name())));
		else
			vehicleElement.setAttribute(new Attribute(VehicleXML.Power.getOfbizColName(), BLANK));
		if(UtilValidate.isNotEmpty(vehicleMap.get(VehicleXML.Type.name())))
			vehicleElement.setAttribute(new Attribute(VehicleXML.Type.getOfbizColName(), vehicleMap.get(VehicleXML.Type.name())));
		else
			vehicleElement.setAttribute(new Attribute(VehicleXML.Type.getOfbizColName(), BLANK));
		if(UtilValidate.isNotEmpty(vehicleMap.get(VehicleXML.Fuel_Type.name())))
			vehicleElement.setAttribute(new Attribute(VehicleXML.Fuel_Type.getOfbizColName(), vehicleMap.get(VehicleXML.Fuel_Type.name())));
		else
			vehicleElement.setAttribute(new Attribute(VehicleXML.Fuel_Type.getOfbizColName(), BLANK));
		if(UtilValidate.isNotEmpty(vehicleMap.get(VehicleXML.Oil_Capacity.name())))
			vehicleElement.setAttribute(new Attribute(VehicleXML.Oil_Capacity.getOfbizColName(), vehicleMap.get(VehicleXML.Oil_Capacity.name())));
		else
			vehicleElement.setAttribute(new Attribute(VehicleXML.Oil_Capacity.getOfbizColName(), BLANK));
		
		List<Element> allVehicle= new ArrayList<>();
		allVehicle.add(vehicleElement);
		

		Debug.logInfo("end getVehicle method for Vehicle Id--" + vehicleMap.get(VehicleXML.Vehicle_Id.name()), module);
		return allVehicle;
	}


	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, Object> importVehicleInERPFromCsv(DispatchContext ctx, Map<String, ?> context)
			throws Exception {
		Debug.logInfo("start service importVehicleInERPFromCsv", module);
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
		            	vehicleExtractInputPath = (String) resultMap.get("uploadFilePath");
					}
				} catch(Exception eeeee){
				
				}
			}
		}
		
		vehicleExtractXMLSuccessPath = FlexibleStringExpander.expandString(
				UtilProperties.getPropertyValue("gaadizo.properties", "vehicleextract-success-xml-path"), context);
		if (StringUtils.isBlank(vehicleExtractXMLSuccessPath)) {
			throw new VehicleImportException("Vehicle extract xml success path is not cofigured");
		}
		if (!new File(vehicleExtractXMLSuccessPath).exists()) {
			new File(vehicleExtractXMLSuccessPath).mkdirs();
		}
		try {
			Debug.logInfo("calling  service prepareAndImportVehicleXML", module);
			Map prepareAndImportVehicleXMLResult = dispatcher.runSync("prepareAndImportVehicleXML",
					UtilMisc.toMap("userLogin", context.get("userLogin")));
			List<String> messages = new ArrayList<>();
			Debug.logInfo(" Method insertIntoDB starts", module);

			String outputXmlFilePath = (String) prepareAndImportVehicleXMLResult.get("dumpDirPath");
			Debug.logInfo("----- prepareAndImportVehicleXMLResult : "+prepareAndImportVehicleXMLResult+" -----outputXmlFilePath : "+outputXmlFilePath, module);

			if (!ServiceUtil.isError(prepareAndImportVehicleXMLResult)) {
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
					ImportUtility.moveXMLFilesFromDir(outputXmlFilePath, vehicleExtractXMLSuccessPath);
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
