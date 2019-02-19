package com.ofbiz.importinterface.services.customer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.jdom.Attribute;
import org.jdom.Document;
import org.apache.ofbiz.entity.condition.EntityOperator;
import org.apache.ofbiz.entity.util.*;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

//Changed lang -> lang3 for eclipse only
/*import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;*/
import org.apache.ofbiz.entity.util.*;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.StringUtil;
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
import com.ofbiz.importinterface.constants.OrderCSV;
import com.ofbiz.importinterface.exception.CategoryImportException;
import com.ofbiz.importinterface.exception.OrderImportException;
import com.ofbiz.utility.ImportUtility;
import com.ofbiz.utility.DateTimeUtility;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilDateTime;
import org.apache.ofbiz.base.util.UtilGenerics;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilProperties;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntity;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.condition.EntityCondition;
import org.apache.ofbiz.entity.condition.EntityConditionList;
import org.apache.ofbiz.entity.condition.EntityExpr;
import org.apache.ofbiz.entity.condition.EntityFunction;
import org.apache.ofbiz.entity.condition.EntityOperator;
import org.apache.ofbiz.entity.model.DynamicViewEntity;
import org.apache.ofbiz.entity.model.ModelKeyMap;
import org.apache.ofbiz.entity.util.EntityListIterator;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.entity.util.EntityTypeUtil;
import org.apache.ofbiz.entity.util.EntityUtil;
import org.apache.ofbiz.product.spreadsheetimport.ImportProductHelper;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ModelService;
import org.apache.ofbiz.service.ServiceUtil;
import org.apache.commons.io.FileUtils;

public class ImportCompanyAccountServices {
	public static final String module = ImportCompanyAccountServices.class.getName();
	public static final String resource = "GaadizoUiLabels";
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
	private static String companyAccountExtractOutputPath = null;
	//private static String companyAccountextractoutputpath = null;
	
	public static Map<String, Object> importChartOfAccountInERPFromCsv(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map result = new HashMap<String, Object>();
        companyAccountExtractOutputPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("gaadizo.properties", "companyAccountExtract-output-path"), context);
        //companyextractoutputpath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("gaadizo.properties", "companyAccountExtract-output-path"), context);
        String companyAccountextractsuccessxmlpath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("gaadizo.properties", "companyAccountExtract-success-xml-path"), context);
        List<Map<String, String>> companInfoMapList = new ArrayList<Map<String, String>>();
        Map<String,Map<String, String>> companInfoMapListMap = new HashMap<String,Map<String, String>>();
        
        List<String> error_list = new ArrayList<String>();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        ByteBuffer fileBytes = (ByteBuffer) context.get("uploadedFile");
        String uploadedFileName  = "uploadedFile" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) +".csv";
        String uploadedFilepath  = "";
        if(UtilValidate.isNotEmpty(uploadedFileName)) 
        {
        	if(!((uploadedFileName.toUpperCase()).endsWith("CSV") || (uploadedFileName.toUpperCase()).endsWith("XLSX"))) 
        	{
        		error_list.add("Incorrect file format.");	
        	} 
        	else 
        	{
        		Map<String, Object> uploadFileCtx = new HashMap<String, Object>();
                uploadFileCtx.put("userLogin",userLogin);
                uploadFileCtx.put("uploadedFile",fileBytes);
                uploadFileCtx.put("_uploadedFile_fileName",uploadedFileName);
                
                try 
                {
        			result = dispatcher.runSync("uploadFile", uploadFileCtx);
        			System.out.println("=======result===================="+result+"===========================");
        		
                //===========================================================
                if(UtilValidate.isNotEmpty(result.get("uploadFilePath")) && UtilValidate.isNotEmpty(result.get("uploadFileName"))) 
                {
                	String line = "";
            		String cvsSplitBy = ",";
            		File file = new File(result.get("uploadFilePath")+""+result.get("uploadFileName"));
            		
            		//try 
                    //{
                        POIFSFileSystem fs = null;
                        HSSFWorkbook wb = null;
                        try {
                            fs = new POIFSFileSystem(new FileInputStream(file));
                            wb = new HSSFWorkbook(fs);
                        } catch (IOException e) {
                            Debug.logError("Unable to read or create workbook from file", module);
                            return ServiceUtil.returnError("Unable to read or create workbook from file");
                        }

                        // get first sheet
                        HSSFSheet sheet = wb.getSheetAt(0);
                        wb.close();
                        int sheetLastRowNumber = sheet.getLastRowNum();
                        String compInfoHeader = UtilProperties.getPropertyValue("gaadizo.properties", "company_info_head");
                        for (int j = 1; j <= sheetLastRowNumber; j++) {
                        	Map<String, String> companInfoMap = new HashMap<String, String>();
                            HSSFRow row = sheet.getRow(j);
                            if (row != null) {
                            	System.out.println("===1====row========================"+row.getCell(0)+"=======================");
                            	if(UtilValidate.isNotEmpty(row.getCell(0)))
                            	{
                            		companInfoMap.put("COMPANYID", row.getCell(0).getRichStringCellValue().toString());
                            		if(UtilValidate.isNotEmpty(row.getCell(1))){
                            			companInfoMap.put("ACCOUNT_TYPE", row.getCell(1).getRichStringCellValue().toString());
                            		}
                                	if(UtilValidate.isNotEmpty(row.getCell(2))){
                                		row.getCell(2).setCellType(HSSFCell.CELL_TYPE_STRING);
                                		companInfoMap.put("ACCOUNT_ID", row.getCell(2).getRichStringCellValue().toString());
                                    	}
                                	if(UtilValidate.isNotEmpty(row.getCell(3))){
                                		row.getCell(3).setCellType(HSSFCell.CELL_TYPE_STRING);
                                		companInfoMap.put("PARENT_TYPE_ID", row.getCell(3).getRichStringCellValue().toString());
                                    	}
                                	if(UtilValidate.isNotEmpty(row.getCell(4))){
                                		row.getCell(4).setCellType(HSSFCell.CELL_TYPE_STRING);
                                		companInfoMap.put("PARENT_ACCOUNT_ID", row.getCell(4).getRichStringCellValue().toString());
                                    	}
                                	

                                	}
                            	companInfoMapListMap.put(row.getCell(2).getRichStringCellValue().toString(), companInfoMap);
                            	System.out.println("===1====row========================"+companInfoMapListMap+"=======================");
                            }
                        }          		            		           	
                //=============================================================================
        	}
                
            if(UtilValidate.isNotEmpty(companInfoMapListMap.keySet().size() > 0)){
            	List<Element> contents = new ArrayList<Element>();
            	Set<String> partyIds = companInfoMapListMap.keySet();
            	List<Element> companyList = new ArrayList<Element>();
            	Iterator<String> partyIdsItr = partyIds.iterator();
            	while(partyIdsItr.hasNext()){
            		String companyPartyId = partyIdsItr.next();
            		System.out.println("=242===companyPartyId======"+companyPartyId+"==========================");
            		if(UtilValidate.isNotEmpty(companyPartyId)){
            			System.out.println("=244===companyList======"+companyPartyId+"==========================");
            			GenericValue partyExisting = EntityQuery.use(delegator).from("GlAccount").where("glAccountId",companyPartyId).queryOne();
            			Map<String, String> companInfoMapWithValue = companInfoMapListMap.get(companyPartyId);
            			System.out.println("==246====partyExisting========================"+partyExisting+"=======================");
            			if(UtilValidate.isEmpty(partyExisting)){
            				
            				GenericValue glAccountTypeExisting = EntityQuery.use(delegator).from("GlAccountType").where("glAccountTypeId",companInfoMapWithValue.get("ACCOUNT_TYPE")).queryOne();
            				System.out.println("==275====partyExisting========================"+partyExisting+"=======================");
            				if(UtilValidate.isEmpty(glAccountTypeExisting)){
            					//<GlAccountType description="Accounts Payable" glAccountTypeId="ACCOUNTS_PAYABLE" hasTable="N" parentTypeId=""/>
            					Element glAccountTypeElement = new Element("GlAccountType");
            					glAccountTypeElement.setAttribute(new Attribute("glAccountTypeId", companInfoMapWithValue.get("ACCOUNT_TYPE").toUpperCase()));
            					glAccountTypeElement.setAttribute(new Attribute("description", "PARTY_GROUP"));
            					glAccountTypeElement.setAttribute(new Attribute("hasTable", "N"));
                      	 	 	if(UtilValidate.isNotEmpty(companInfoMapWithValue.get("ACCOUNT_TYPE"))){
                      	 	 		glAccountTypeElement.setAttribute(new Attribute("parentTypeId", companInfoMapWithValue.get("ACCOUNT_TYPE").toUpperCase()));
                      	 	 	}
                      	 	 	companyList.add(glAccountTypeElement);
            				}
            				System.out.println("=262===companyList======"+companyList+"==========================");
            				/*if(UtilValidate.isEmpty(glAccountClassExisting)){
            					//<GlAccountType description="Accounts Payable" glAccountTypeId="ACCOUNTS_PAYABLE" hasTable="N" parentTypeId=""/>
            					Element glAccountTypeElement = new Element("GlAccountClass");
            					glAccountTypeElement.setAttribute(new Attribute("glAccountTypeId", companInfoMapWithValue.get("ACCOUNT_TYPE").toUpperCase()));
            					glAccountTypeElement.setAttribute(new Attribute("description", "PARTY_GROUP"));
            					glAccountTypeElement.setAttribute(new Attribute("hasTable", "N"));
                      	 	 	if(UtilValidate.isNotEmpty(companInfoMapWithValue.get("ACCOUNT_TYPE"))){
                      	 	 	glAccountTypeElement.setAttribute(new Attribute("parentTypeId", companInfoMapWithValue.get("ACCOUNT_TYPE").toUpperCase()));
                      	 	 	}
                      	 	 	companyList.add(glAccountTypeElement);
            				}*/
            				System.out.println("=274===companyList======"+companInfoMapWithValue.get("ACCOUNT_ID")+"==========================");
            				if(UtilValidate.isNotEmpty(companInfoMapWithValue.get("ACCOUNT_ID"))){
            					//<GlAccountType description="Accounts Payable" glAccountTypeId="ACCOUNTS_PAYABLE" hasTable="N" parentTypeId=""/>
            					Element glAccountElement = new Element("GlAccount");
            					if(UtilValidate.isNotEmpty(companInfoMapWithValue.get("PARENT_ACCOUNT_ID"))){
                      	 	 		GenericValue parentGlAccountExisting = EntityQuery.use(delegator).from("GlAccount").where("glAccountId",companInfoMapWithValue.get("PARENT_ACCOUNT_ID").toUpperCase()).queryOne();
                      	 	 		if(UtilValidate.isNotEmpty(parentGlAccountExisting)){
                      	 	 			glAccountElement.setAttribute(new Attribute("parentGlAccountId", companInfoMapWithValue.get("PARENT_ACCOUNT_ID").toUpperCase()));
                      	 	 		}
                      	 	 	}
            					glAccountElement.setAttribute(new Attribute("glAccountId", companInfoMapWithValue.get("ACCOUNT_ID").toUpperCase()));
            					glAccountElement.setAttribute(new Attribute("accountCode", companInfoMapWithValue.get("ACCOUNT_ID").toUpperCase()));
            					glAccountElement.setAttribute(new Attribute("glAccountTypeId", companInfoMapWithValue.get("ACCOUNT_TYPE").toUpperCase()));
            					glAccountElement.setAttribute(new Attribute("glResourceTypeId", "MONEY"));
            					glAccountElement.setAttribute(new Attribute("accountName", companInfoMapWithValue.get("ACCOUNT_TYPE").toUpperCase()));
                      	 	 	companyList.add(glAccountElement);
            				}
            				
        					Element glAccountOrgTypeElement = new Element("GlAccountOrganization");
        					glAccountOrgTypeElement.setAttribute(new Attribute("glAccountId", companInfoMapWithValue.get("ACCOUNT_ID").toUpperCase()));
        					glAccountOrgTypeElement.setAttribute(new Attribute("organizationPartyId", companInfoMapWithValue.get("COMPANYID").toUpperCase()));
        					glAccountOrgTypeElement.setAttribute(new Attribute("fromDate", UtilDateTime.nowTimestamp().toString()));
                  	 	 	companyList.add(glAccountOrgTypeElement);
            			} else {
            				Element glAccountOrgTypeElement = new Element("GlAccountOrganization");
        					glAccountOrgTypeElement.setAttribute(new Attribute("glAccountId", companInfoMapWithValue.get("ACCOUNT_ID").toUpperCase()));
        					glAccountOrgTypeElement.setAttribute(new Attribute("organizationPartyId", companInfoMapWithValue.get("COMPANYID").toUpperCase()));
        					glAccountOrgTypeElement.setAttribute(new Attribute("fromDate", UtilDateTime.nowTimestamp().toString()));
                  	 	 	companyList.add(glAccountOrgTypeElement);
            			}
            			
            		}
            	}
            	System.out.println("=711===companyList======"+companyList+"==========================");
            	contents.addAll(companyList);
            	System.out.println("==713==contents======"+contents+"=====");
            	//===========================
            	Element order = new Element("entity-engine-xml");
        		order.addContent(contents);
        		System.out.println("==713==order======"+order+"=====");
        		writeXMLToFile(order,uploadedFilepath);
        		try
        		{
        		//if (!ServiceUtil.isError(prepareAndImportOrderXMLResult)) {
    				Debug.logInfo("calling  service entityImportDirectory to import the xml", module);
    				Map entityImportDirParams = UtilMisc.toMap("path", companyAccountExtractOutputPath, "userLogin", context.get("userLogin"));
    System.out.println("====entityImportDirParams======"+entityImportDirParams+"==90909090==="+uploadedFilepath);
    				Map entityImportDirectoryForERPResult = dispatcher.runSync("entityImportDirectoryForERP", entityImportDirParams);

    				if (!ServiceUtil.isError(entityImportDirectoryForERPResult)) {
    					moveXMLFilesFromDir(companyAccountExtractOutputPath, companyAccountextractsuccessxmlpath);
    					Debug.logInfo("moved XMLFilesFromDir successfully", module);
    				}
    				/*List<String> serviceMsg = (List) entityImportDirectoryForERPResult.get("messages");
    				for (String msg : serviceMsg) {
    					messages.add(msg);
    				}

    				if (!ServiceUtil.isError(entityImportDirectoryForERPResult)) {
    					moveXMLFilesFromDir(outputXmlFilePath, companyextractoutputpath);
    					Debug.logInfo("moved XMLFilesFromDir successfully", module);
    				}*/
    			//}
    		} catch (Exception ex) {
    			//errorDuringInsertion = true;
    			//errorMsgs.add(ex.getMessage());
    			Debug.logError(ex.getMessage(), module);
    			//throw ex;
    		}
            	// try {
                 	// Map<String,Object> getNextPartyIds = new HashMap<String,Object>();
                 	 
                 	 /*getNextPartyIdContext.put("ownerId", userLogin.getString("ownerCompanyId"));
                 	 getNextPartyIdContext.put("userLogin", userLogin);
                     Map<String, Object> getNextPartyIdResult = dispatcher.runSync("getNextPartyId", getNextPartyIdContext);
                     if (ServiceUtil.isError(getNextPartyIdResult)) {
                         String errMsg = "Party sequence in corret.";
                         return ServiceUtil.returnError(errMsg, null, null, getNextPartyIdResult);
                     }
                     String partyId = (String) getNextPartyIdResult.get("partyId");*/
                     /*if(UtilValidate.isNotEmpty(partyId)){
                    	 Element postalCodeGeo = new Element("P");
                 		 postalCodeGeo.setAttribute(new Attribute("geoId", postalCodeGeoId));
                 		 postalCodeGeo.setAttribute(new Attribute("geoTypeId", "POSTAL_CODE"));
                 		 postalCodeGeo.setAttribute(new Attribute("geoName", orderMap.get(OrderCSV.Postal_Code.name())));
                 		 postalCodeGeo.setAttribute(new Attribute("geoCode", orderMap.get(OrderCSV.Postal_Code.name())));
                 		 postalCodeGeo.setAttribute(new Attribute("geoSecCode", orderMap.get(OrderCSV.Postal_Code.name())));
                 		 postalCodeGeo.setAttribute(new Attribute("abbreviation", orderMap.get(OrderCSV.Postal_Code.name())));
                 		 contactMechList.add(postalCodeGeo);
                     }*/
                     
                /* } catch (GenericServiceException e) {
                     String errMsg = "Party sequence not working.";
                     Debug.logError(e, errMsg, module);
                     return ServiceUtil.returnError(errMsg);
                 }*/
            	
            	
            	
            	
        		//SHIPPING_LOCATION Details
        		//String contachMechIdShip = delegator.getNextSeqId("Party");
        		/*if (UtilValidate.isNotEmpty(orgPartyId)) {
                    Map<String, Object> getNextPartyIdContext = new HashMap<String, Object>();
                    getNextPartyIdContext.putAll(context);
                    getNextPartyIdContext.put("partyId", orgPartyId);
                    getNextPartyIdContext.put("userLogin", userLogin);
                    if (UtilValidate.isEmpty(orderId)) {
                        try {
                        	getNextPartyIdContext = ctx.makeValidContext("getNextPartyId", "IN", getNextPartyIdContext);
                            Map<String, Object> getNextOrderIdResult = dispatcher.runSync("getNextOrderId", getNextPartyIdContext);
                            if (ServiceUtil.isError(getNextOrderIdResult)) {
                                String errMsg = UtilProperties.getMessage(resource_error, 
                                        "OrderErrorGettingNextOrderIdWhileCreatingOrder", locale);
                                return ServiceUtil.returnError(errMsg, null, null, getNextOrderIdResult);
                            }
                            orderId = (String) getNextOrderIdResult.get("orderId");
                            
                            
                        } catch (GenericServiceException e) {
                            String errMsg = UtilProperties.getMessage(resource_error, 
                                    "OrderCaughtGenericServiceExceptionWhileGettingOrderId", locale);
                            Debug.logError(e, errMsg, module);
                            return ServiceUtil.returnError(errMsg);
                        }
                    }
                }*/
        		
        		/*contactMechList.add(getContactMech("POSTAL_ADDRESS", contachMechIdShip, null));
        		String postalCodeGeoId = delegator.getNextSeqId("Geo");
        		Element postalCodeGeo = new Element("Geo");
        		postalCodeGeo.setAttribute(new Attribute("geoId", postalCodeGeoId));
        		postalCodeGeo.setAttribute(new Attribute("geoTypeId", "POSTAL_CODE"));
        		postalCodeGeo.setAttribute(new Attribute("geoName", orderMap.get(OrderCSV.Postal_Code.name())));
        		postalCodeGeo.setAttribute(new Attribute("geoCode", orderMap.get(OrderCSV.Postal_Code.name())));
        		postalCodeGeo.setAttribute(new Attribute("geoSecCode", orderMap.get(OrderCSV.Postal_Code.name())));
        		postalCodeGeo.setAttribute(new Attribute("abbreviation", orderMap.get(OrderCSV.Postal_Code.name())));
        		contactMechList.add(postalCodeGeo);
        		*/
        		
        		
            }
                } 
                catch (GenericServiceException e) 
                {
        			e.printStackTrace();
        		}catch (GenericEntityException e) 
                {
        			e.printStackTrace();
        		}catch (Exception e11) 
                {
        			e11.printStackTrace();
        		}
        }
        
        }

        return ServiceUtil.returnSuccess();
    }
	private static void moveXMLFilesFromDir(String dirPath, String destDirPath) throws IOException {

		Debug.logInfo(" Method moveXMLFilesFromDir starts", module);
		File dir = new File(dirPath);
		File destDir = new File(destDirPath);
		for (File file : dir.listFiles()) {
			FileUtils.copyFileToDirectory(file, destDir);
			//file.delete();
		}
		Debug.logInfo(" Method moveXMLFilesFromDir ends", module);
	}
	
	private static void writeXMLToFile(Element company,String finalFilePath) throws IOException {
		System.out.println("========finalFilePath============="+finalFilePath+"=====================");
		XMLOutputter xmlOutput = new XMLOutputter();
		// display nice
		xmlOutput.setFormat(Format.getPrettyFormat());
		Document doc = new Document(company);
		doc.setRootElement(company);
		OutputStreamWriter writer = null;
		
		String finalPath =  companyAccountExtractOutputPath ;
		System.out.println("========finalPath============="+finalPath+"=====================");
		finalFilePath = finalPath;
		try {
			if (!new File(finalPath).exists()) {
				new File(finalPath).mkdirs();
			}
			String filePath = finalPath + "company_" + UtilDateTime.nowTimestamp().getTime() + ".xml";
			System.out.println("====864====finalPath============="+filePath+"=====================");
			 writer = new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8);
			 System.out.println("====864====doc============="+doc+"=====================");
			xmlOutput.output(doc, writer);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

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

	 public static Map<String, Object> uploadFile(DispatchContext dctx, Map<String, ? extends Object> context) 
	    {
	        
	        Locale locale = (Locale) context.get("locale");
	        ByteBuffer uploadBytes = (ByteBuffer) context.get("uploadedFile");
	        String xlsFileName = (String)context.get("_uploadedFile_fileName");
	        //List<MessageString> error_list = new ArrayList<MessageString>();
	       // MessageString tmp = null;
	        Map result = ServiceUtil.returnSuccess();
	        if (UtilValidate.isNotEmpty(xlsFileName))
	        {
	            String uploadTempDir = System.getProperty("ofbiz.home") + "/runtime/tmp/upload/"+Long.toString(UtilDateTime.nowTimestamp().getTime())+"/";
	                
	                if (!new File(uploadTempDir).exists()) 
	                {
	                    new File(uploadTempDir).mkdirs();
	                }
	                
	                String filenameToUse = xlsFileName;
	                
	                File file = new File(uploadTempDir + filenameToUse);
	                
	                if(file.exists()) 
	                {
	                    file.delete();
	                }
	                
	                try 
	                {
	                    RandomAccessFile out = new RandomAccessFile(file, "rw");
	                    out.write(uploadBytes.array());
	                    out.close();
	                    result.put("uploadFileName",xlsFileName);
	                    result.put("uploadFilePath",uploadTempDir);
	                } 
	                catch (FileNotFoundException e) 
	                {
	                    Debug.logError(e, module);
	                    return ServiceUtil.returnError("Unable to open file for writing: " + file.getAbsolutePath());
	                } catch (IOException e) {
	                    Debug.logError(e, module);
	                    return ServiceUtil.returnError("Unable to write binary data to: " + file.getAbsolutePath());
	                } catch (Exception eeee) {
	                    Debug.logError(eeee, module);
	                    return ServiceUtil.returnError("Unable to write binary data to: " + file.getAbsolutePath());
	                }
	        } 
	        else 
	        {
	            return ServiceUtil.returnError("Unable to open file for writing");
	        }
	        
	        
	        return result;
	    }

	 public static List<String> buildCompanyHeader() 
	{
	        List<String> headerCols = new ArrayList();
	        headerCols.add("COMPANYID");
	   	    headerCols.add("COMPANY_NAME");
	   	    headerCols.add("CONTACTNUMBER");
		   	headerCols.add("ADDRESS1");
		   	headerCols.add("ADDRESS2");
		   	headerCols.add("CITY");
		   	headerCols.add("STATE");
		   	headerCols.add("POSTALCODE");
		   	headerCols.add("JOBCARD_PREFIX");
		   	headerCols.add("INVOICE_PREFIX");
		   	headerCols.add("LOGOURL");
		   	headerCols.add("GSTN_No");
	   	    return headerCols;
	 }

		public static void readCSVAndConvertToCompanyXML(File file, GenericValue userLogin, LocalDispatcher dispatcher) throws IOException, OrderImportException {
			// String csvFile =
			// "F:\\Sample_data\\input\\Quancious_OrderExtract_2017-12-29_11_58.csv";
			Debug.logInfo("satrt reading csv line by line--", module);
			String line = "";
			String cvsSplitBy = ",";
			List<Map<String, String>> orderList = new ArrayList<Map<String, String>>();
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
				int count = 0;
				while ((line = br.readLine()) != null) {
					if (count == 0) {
						count++;
						continue;
					}
					String[] orderLine = line.split(cvsSplitBy);
					System.out.println("==767====orderLine===================="+orderLine+"===========================");
					System.out.println("239 orderLine++++"+orderLine);
					
					 POIFSFileSystem fs = null;
			            HSSFWorkbook wb = null;
			            try {
			                fs = new POIFSFileSystem(new FileInputStream(file));
			                wb = new HSSFWorkbook(fs);
			            } catch (IOException e) {
			                Debug.logError("Unable to read or create workbook from file", module);
			                //return ServiceUtil.returnError("Unable to read or create workbook from file");
			            }

			            // get first sheet
			            HSSFSheet sheet = wb.getSheetAt(0);
			            wb.close();
			            int sheetLastRowNumber = sheet.getLastRowNum();
			            for (int j = 1; j <= sheetLastRowNumber; j++) {
			                HSSFRow row = sheet.getRow(j);
			                if (row != null) {
			                	
			                	
			                	
			                	}
			            }
					
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception ey) {
				ey.printStackTrace();
			} finally {
				if (br != null) {
					br.close();
				}
			}
			Debug.logInfo("end reading csv line by line--", module);
			//createOrder(orderList, file.getName(), userLogin,  dispatcher);
		}

		private static Map<String, String> buildDataRows(String[] s, File file){
			Map<String, String> data = new HashMap<String, String>();
			System.out.println("==796====file===================="+file.getName()+"===========================");
			try {
				for (OrderCSV orderCSVs : OrderCSV.values()) {
					System.out.println("258 OrderXML.values() "+OrderCSV.values());
					data.put(orderCSVs.name(), ("null".equalsIgnoreCase(s[orderCSVs.getCode()]) ? " ": StringUtils.replace(s[orderCSVs.getCode()], "~", ",")));
					// changed ~ to ',' because if csv identifies , in any of its
					// value then column get shifted to other
				}
			} catch (ArrayIndexOutOfBoundsException e) {
			}
			return data;
		}

		//public static Map<String, Object> importCategryMasterCSV(DispatchContext dctx, Map<String, ? extends Object> context) {}

}
