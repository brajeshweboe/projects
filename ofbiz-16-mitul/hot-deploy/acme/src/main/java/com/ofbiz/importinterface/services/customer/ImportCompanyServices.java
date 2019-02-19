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

public class ImportCompanyServices {
	public static final String module = ImportCompanyServices.class.getName();
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
	private static String orderExtractOutputPath = null;
	private static String companyextractoutputpath = null;
	
	public static Map<String, Object> importCompanyInERPFromCsv(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map result = new HashMap<String, Object>();
        orderExtractOutputPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("gaadizo.properties", "orderextract-output-path"), context);
        companyextractoutputpath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("gaadizo.properties", "companyextract-output-path"), context);
        String companyextractsuccessxmlpath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("gaadizo.properties", "companyextract-success-xml-path"), context);
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
                            	if(UtilValidate.isNotEmpty(row.getCell(0))){
                            		companInfoMap.put("COMPANYID", row.getCell(0).getRichStringCellValue().toString());
                            		if(UtilValidate.isNotEmpty(row.getCell(1))){
                            			companInfoMap.put("COMPANY_NAME", row.getCell(1).getRichStringCellValue().toString());
                            		}
                                	if(UtilValidate.isNotEmpty(row.getCell(2))){
                                		row.getCell(2).setCellType(HSSFCell.CELL_TYPE_STRING);
                                		companInfoMap.put("CONTACTNUMBER", row.getCell(2).getRichStringCellValue().toString());
                                    	}
                                	if(UtilValidate.isNotEmpty(row.getCell(3))){
                                		companInfoMap.put("ADDRESS1", row.getCell(3).getRichStringCellValue().toString());
                                    	}
                                	if(UtilValidate.isNotEmpty(row.getCell(4))){
                                		companInfoMap.put("ADDRESS2", row.getCell(4).getRichStringCellValue().toString());
                                    	}
                                	if(UtilValidate.isNotEmpty(row.getCell(5))){
                                		companInfoMap.put("CITY", row.getCell(5).getRichStringCellValue().toString());
                                    	}
                                	if(UtilValidate.isNotEmpty(row.getCell(6))){
                                		row.getCell(6).setCellType(HSSFCell.CELL_TYPE_STRING);
                                		companInfoMap.put("STATE", row.getCell(6).getRichStringCellValue().toString());
                                    	}
                                	if(UtilValidate.isNotEmpty(row.getCell(7))){
                                		row.getCell(7).setCellType(HSSFCell.CELL_TYPE_STRING);
                                		companInfoMap.put("POSTALCODE", row.getCell(7).getRichStringCellValue().toString());
                                    	}
                                	if(UtilValidate.isNotEmpty(row.getCell(8))){
                                		row.getCell(8).setCellType(HSSFCell.CELL_TYPE_STRING);
                                		companInfoMap.put("JOBCARD_PREFIX", row.getCell(8).getRichStringCellValue().toString());
                                    	}
                                	if(UtilValidate.isNotEmpty(row.getCell(9))){
                                		row.getCell(8).setCellType(HSSFCell.CELL_TYPE_STRING);
                                		companInfoMap.put("JOBCARD_SEQ", row.getCell(9).toString());
                                    	}
                                	if(UtilValidate.isNotEmpty(row.getCell(10))){
                                		row.getCell(9).setCellType(HSSFCell.CELL_TYPE_STRING);
                                		companInfoMap.put("INVOICE_PREFIX", row.getCell(10).getRichStringCellValue().toString());
                                    	}
                                	if(UtilValidate.isNotEmpty(row.getCell(11))){
                                		row.getCell(8).setCellType(HSSFCell.CELL_TYPE_STRING);
                                		companInfoMap.put("INVOICE_SEQ", row.getCell(11).toString());
                                    	}
                                	if(UtilValidate.isNotEmpty(row.getCell(12))){
                                		row.getCell(10).setCellType(HSSFCell.CELL_TYPE_STRING);
                                		companInfoMap.put("LOGOURL", row.getCell(12).getRichStringCellValue().toString());
                                    	}
                                	if(UtilValidate.isNotEmpty(row.getCell(13))){
                                		row.getCell(11).setCellType(HSSFCell.CELL_TYPE_STRING);
                                		companInfoMap.put("GSTNNO", row.getCell(13).getRichStringCellValue().toString());
                                    	}
                                	}
                            	companInfoMapListMap.put(row.getCell(0).getRichStringCellValue().toString(), companInfoMap);
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
            		if(UtilValidate.isNotEmpty(companyPartyId)){
            			GenericValue partyExisting = EntityQuery.use(delegator).from("Party").where("partyId",companyPartyId).queryOne();
            			if(UtilValidate.isNotEmpty(partyExisting)){
            				System.out.println("==275====partyExisting========================"+partyExisting+"=======================");
            				continue;
            			} else {
            				if(UtilValidate.isNotEmpty(companyPartyId)){
            					System.out.println("==279====companyPartyId======================"+companyPartyId+"=======================");
            					Map<String, String> companInfoMapWithValue = companInfoMapListMap.get(companyPartyId);
                           	     Element partyElement = new Element("Party");
                           	 	 partyElement.setAttribute(new Attribute("partyId", companyPartyId));
                           	 	 partyElement.setAttribute(new Attribute("partyTypeId", "PARTY_GROUP"));
                           	     partyElement.setAttribute(new Attribute("statusId", "PARTY_ENABLED"));
                           	     partyElement.setAttribute(new Attribute("createdDate", UtilDateTime.nowTimestamp().toString()));
                           	 	 partyElement.setAttribute(new Attribute("preferredCurrencyUomId", "INR"));
                           	 	companyList.add(partyElement);
                           	 Element partyGroupElement = new Element("PartyGroup");
                     	 	   partyGroupElement.setAttribute(new Attribute("partyId", companyPartyId));
                     	       partyGroupElement.setAttribute(new Attribute("groupName", companInfoMapWithValue.get("COMPANY_NAME")));
                     	       if(UtilValidate.isNotEmpty(companInfoMapWithValue.get("LOGOIMAGEURL"))){
                     	      partyGroupElement.setAttribute(new Attribute("logoImageUrl", companInfoMapWithValue.get("LOGOIMAGEURL")));
                     	       }
                  	 	       companyList.add(partyGroupElement);
                           	 	
                           	    Element partyRole1Element = new Element("PartyRole");
                           	 partyRole1Element.setAttribute(new Attribute("partyId", companyPartyId));
                           	partyRole1Element.setAttribute(new Attribute("roleTypeId", "_NA_"));
                       	 	    companyList.add(partyRole1Element);
                       	 	    
                       	 	Element partyRole2Element = new Element("PartyRole");
                       	 partyRole2Element.setAttribute(new Attribute("partyId", companyPartyId));
                       	partyRole2Element.setAttribute(new Attribute("roleTypeId", "INTERNAL_ORGANIZATIO"));
                   	 	    companyList.add(partyRole2Element);
                   	 	Element partyRole3Element = new Element("PartyRole");
                   	 partyRole3Element.setAttribute(new Attribute("partyId", companyPartyId));
                   	partyRole3Element.setAttribute(new Attribute("roleTypeId", "BILL_FROM_VENDOR"));
               	 	    companyList.add(partyRole3Element);
               	 	Element partyRole4Element = new Element("PartyRole");
               	 partyRole4Element.setAttribute(new Attribute("partyId", companyPartyId));
               	partyRole4Element.setAttribute(new Attribute("roleTypeId", "BILL_TO_CUSTOMER"));
           	 	    companyList.add(partyRole4Element);
                       	 	   Element partyStatusElement = new Element("PartyStatus");
                       	 	   partyStatusElement.setAttribute(new Attribute("partyId", companyPartyId));
                       	 	   partyStatusElement.setAttribute(new Attribute("statusId", "PARTY_ENABLED"));
                       	 	   partyStatusElement.setAttribute(new Attribute("statusDate", UtilDateTime.nowTimestamp().toString()));
                       	 	   companyList.add(partyStatusElement);
                    	 	   /*
    
                    	 	    */
                       	 	   
                    	 	  //<GlJournal organizationPartyId="Company" glJournalId="ERROR_JOURNAL" glJournalName="Suspense transactions"/>
                    	 	   /*
                    	 	    
<entity-engine-xml>
    <!-- General Ledger Setup for the organization -->
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="100000" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="111000" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="111100" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="112000" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="120000" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="125000" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="121800" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="122000" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="122100" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="122200" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="122300" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="122500" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="140000" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="141000" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="142000" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="210000" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="213000" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="213200" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="213300" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="213500" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="214000" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="215000" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="221100" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="224000" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="224100" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="224106" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="224140" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="224151" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="224153" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="224209" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="336000" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="400000" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="401000" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="409000" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="410000" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="421000" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="422000" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="423000" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="424000" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="500000" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="510000" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="514000" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="515000" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="516100" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="600000" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="601000" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="601100" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="601200" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="601300" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="601400" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="602100" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="602200" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="603100" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="603200" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="604000" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="605000" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="625000" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="650000" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="804000" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="810000" fromDate="${fromDate}"/>
    <GlAccountOrganization organizationPartyId="${orgPartyId}" glAccountId="900000" fromDate="${fromDate}"/>
    
    <!-- Default mapping between account types and account ids -->
    <GlAccountTypeDefault organizationPartyId="${orgPartyId}" glAccountTypeId="ACCOUNTS_RECEIVABLE" glAccountId="120000"/>
    <GlAccountTypeDefault organizationPartyId="${orgPartyId}" glAccountTypeId="INTRSTINC_RECEIVABLE" glAccountId="121800"/>
    <GlAccountTypeDefault organizationPartyId="${orgPartyId}" glAccountTypeId="INVENTORY_XFER_OUT" glAccountId="125000"/>
    <GlAccountTypeDefault organizationPartyId="${orgPartyId}" glAccountTypeId="INVENTORY_ACCOUNT" glAccountId="140000"/>
    <GlAccountTypeDefault organizationPartyId="${orgPartyId}" glAccountTypeId="RAWMAT_INVENTORY" glAccountId="141000"/>
    <GlAccountTypeDefault organizationPartyId="${orgPartyId}" glAccountTypeId="WIP_INVENTORY" glAccountId="142000"/>
    <GlAccountTypeDefault organizationPartyId="${orgPartyId}" glAccountTypeId="PREPAID_EXPENSES" glAccountId="150000"/>
    <GlAccountTypeDefault organizationPartyId="${orgPartyId}" glAccountTypeId="ACCOUNTS_PAYABLE" glAccountId="210000"/>
    <GlAccountTypeDefault organizationPartyId="${orgPartyId}" glAccountTypeId="CUSTOMER_CREDIT" glAccountId="213000"/>
    <GlAccountTypeDefault organizationPartyId="${orgPartyId}" glAccountTypeId="CUSTOMER_DEPOSIT" glAccountId="213300"/>
    <GlAccountTypeDefault organizationPartyId="${orgPartyId}" glAccountTypeId="UNINVOICED_SHIP_RCPT" glAccountId="214000"/>
    <GlAccountTypeDefault organizationPartyId="${orgPartyId}" glAccountTypeId="INVENTORY_XFER_IN" glAccountId="215000"/>
    <GlAccountTypeDefault organizationPartyId="${orgPartyId}" glAccountTypeId="COMMISSIONS_PAYABLE" glAccountId="221100"/>
    <GlAccountTypeDefault organizationPartyId="${orgPartyId}" glAccountTypeId="RETAINED_EARNINGS" glAccountId="336000"/>
    <GlAccountTypeDefault organizationPartyId="${orgPartyId}" glAccountTypeId="SALES_ACCOUNT" glAccountId="400000"/>
    <GlAccountTypeDefault organizationPartyId="${orgPartyId}" glAccountTypeId="COGS_ACCOUNT" glAccountId="500000"/>
    <GlAccountTypeDefault organizationPartyId="${orgPartyId}" glAccountTypeId="INV_ADJ_VAL" glAccountId="515000"/>
    <GlAccountTypeDefault organizationPartyId="${orgPartyId}" glAccountTypeId="OPERATING_EXPENSE" glAccountId="600000"/>
    <GlAccountTypeDefault organizationPartyId="${orgPartyId}" glAccountTypeId="TAX_ACCOUNT" glAccountId="900000"/>
    
    <!-- mappings for payments -->
    <PaymentGlAccountTypeMap organizationPartyId="${orgPartyId}" paymentTypeId="CUSTOMER_REFUND" glAccountTypeId="CUSTOMER_CREDIT"/>
    <!-- NOTE: do not change this -->
    <PaymentGlAccountTypeMap organizationPartyId="${orgPartyId}" paymentTypeId="VENDOR_PAYMENT" glAccountTypeId="ACCOUNTS_PAYABLE"/>
    <PaymentGlAccountTypeMap organizationPartyId="${orgPartyId}" paymentTypeId="VENDOR_PREPAY" glAccountTypeId="PREPAID_EXPENSES"/>
    <PaymentGlAccountTypeMap organizationPartyId="${orgPartyId}" paymentTypeId="COMMISSION_PAYMENT" glAccountTypeId="COMMISSIONS_PAYABLE"/>
    <PaymentGlAccountTypeMap organizationPartyId="${orgPartyId}" paymentTypeId="PAY_CHECK" glAccountTypeId="CUSTOMER_CREDIT"/>
    <PaymentGlAccountTypeMap organizationPartyId="${orgPartyId}" paymentTypeId="GC_WITHDRAWAL" glAccountTypeId="CUSTOMER_CREDIT"/>
    <PaymentGlAccountTypeMap organizationPartyId="${orgPartyId}" paymentTypeId="SALES_TAX_PAYMENT" glAccountTypeId="TAX_ACCOUNT"/>
    <PaymentGlAccountTypeMap organizationPartyId="${orgPartyId}" paymentTypeId="PAYROL_PAYMENT" glAccountTypeId="OPERATING_EXPENSE"/>
    <PaymentGlAccountTypeMap organizationPartyId="${orgPartyId}" paymentTypeId="PAYROLL_TAX_PAYMENT" glAccountTypeId="TAX_ACCOUNT"/>
    <PaymentGlAccountTypeMap organizationPartyId="${orgPartyId}" paymentTypeId="INCOME_TAX_PAYMENT" glAccountTypeId="TAX_ACCOUNT"/>
    
    <!-- NOTE: do not change this -->
    <PaymentGlAccountTypeMap organizationPartyId="${orgPartyId}" paymentTypeId="CUSTOMER_PAYMENT" glAccountTypeId="ACCOUNTS_RECEIVABLE"/>
    <PaymentGlAccountTypeMap organizationPartyId="${orgPartyId}" paymentTypeId="CUSTOMER_DEPOSIT" glAccountTypeId="CUSTOMER_DEPOSIT"/>
    <PaymentGlAccountTypeMap organizationPartyId="${orgPartyId}" paymentTypeId="INTEREST_RECEIPT" glAccountTypeId="INTRSTINC_RECEIVABLE"/>
    <PaymentGlAccountTypeMap organizationPartyId="${orgPartyId}" paymentTypeId="GC_DEPOSIT" glAccountTypeId="CUSTOMER_DEPOSIT"/>
    
    <PaymentMethodTypeGlAccount organizationPartyId="${orgPartyId}" paymentMethodTypeId="GIFT_CERTIFICATE" glAccountId="120000"/>
    <PaymentMethodTypeGlAccount organizationPartyId="${orgPartyId}" paymentMethodTypeId="CASH" glAccountId="112000"/>
    <PaymentMethodTypeGlAccount organizationPartyId="${orgPartyId}" paymentMethodTypeId="EFT_ACCOUNT" glAccountId="111100"/>
    <PaymentMethodTypeGlAccount organizationPartyId="${orgPartyId}" paymentMethodTypeId="FIN_ACCOUNT" glAccountId="111100"/>
    <PaymentMethodTypeGlAccount organizationPartyId="${orgPartyId}" paymentMethodTypeId="PERSONAL_CHECK" glAccountId="112000"/>
    <PaymentMethodTypeGlAccount organizationPartyId="${orgPartyId}" paymentMethodTypeId="COMPANY_CHECK" glAccountId="112000"/>
    <PaymentMethodTypeGlAccount organizationPartyId="${orgPartyId}" paymentMethodTypeId="CERTIFIED_CHECK" glAccountId="112000"/>
    <PaymentMethodTypeGlAccount organizationPartyId="${orgPartyId}" paymentMethodTypeId="MONEY_ORDER" glAccountId="112000"/>
    <PaymentMethodTypeGlAccount organizationPartyId="${orgPartyId}" paymentMethodTypeId="COMPANY_ACCOUNT" glAccountId="111100"/>
    <PaymentMethodTypeGlAccount organizationPartyId="${orgPartyId}" paymentMethodTypeId="EXT_BILLACT" glAccountId="213000"/>
    <PaymentMethodTypeGlAccount organizationPartyId="${orgPartyId}" paymentMethodTypeId="EXT_COD" glAccountId="112000"/>
    <PaymentMethodTypeGlAccount organizationPartyId="${orgPartyId}" paymentMethodTypeId="EXT_EBAY" glAccountId="112000"/>
    <PaymentMethodTypeGlAccount organizationPartyId="${orgPartyId}" paymentMethodTypeId="EXT_OFFLINE" glAccountId="112000"/>
    <PaymentMethodTypeGlAccount organizationPartyId="${orgPartyId}" paymentMethodTypeId="EXT_PAYPAL" glAccountId="122500"/>
    <PaymentMethodTypeGlAccount organizationPartyId="${orgPartyId}" paymentMethodTypeId="EXT_WORLDPAY" glAccountId="122500"/>
    <CreditCardTypeGlAccount organizationPartyId="${orgPartyId}" cardType="CCT_AMERICANEXPRESS" glAccountId="122100"/>
    <CreditCardTypeGlAccount organizationPartyId="${orgPartyId}" cardType="CCT_DINERSCLUB" glAccountId="122100"/>
    <CreditCardTypeGlAccount organizationPartyId="${orgPartyId}" cardType="CCT_DISCOVER" glAccountId="122200"/>
    <CreditCardTypeGlAccount organizationPartyId="${orgPartyId}" cardType="CCT_VISA" glAccountId="122300"/>
    <CreditCardTypeGlAccount organizationPartyId="${orgPartyId}" cardType="CCT_MASTERCARD" glAccountId="122300"/>
    
    <FinAccountTypeGlAccount organizationPartyId="${orgPartyId}" finAccountTypeId="GIFTCERT_ACCOUNT" glAccountId="213200"/>
    <FinAccountTypeGlAccount organizationPartyId="${orgPartyId}" finAccountTypeId="DEPOSIT_ACCOUNT" glAccountId="213500"/>
    <FinAccountTypeGlAccount organizationPartyId="${orgPartyId}" finAccountTypeId="BANK_ACCOUNT" glAccountId="213500"/>
    <FinAccountTypeGlAccount organizationPartyId="${orgPartyId}" finAccountTypeId="INVESTMENT_ACCOUNT" glAccountId="213500"/>
    
    <VarianceReasonGlAccount organizationPartyId="${orgPartyId}" varianceReasonId="VAR_LOST" glAccountId="514000"/>
    <VarianceReasonGlAccount organizationPartyId="${orgPartyId}" varianceReasonId="VAR_STOLEN" glAccountId="514000"/>
    <VarianceReasonGlAccount organizationPartyId="${orgPartyId}" varianceReasonId="VAR_FOUND" glAccountId="514000"/>
    <VarianceReasonGlAccount organizationPartyId="${orgPartyId}" varianceReasonId="VAR_DAMAGED" glAccountId="514000"/>
    <VarianceReasonGlAccount organizationPartyId="${orgPartyId}" varianceReasonId="VAR_INTEGR" glAccountId="514000"/>
    <VarianceReasonGlAccount organizationPartyId="${orgPartyId}" varianceReasonId="VAR_SAMPLE" glAccountId="625000"/>

</entity-engine-xml>

                    	 	    */
                    	 	   
                    	 	   
                       	 	   //======================================================================================================
                       	 	//<Facility facilityId="GAADIZO_COMPANY" facilityTypeId="WAREHOUSE" ownerPartyId="GAADIZO_COMPANY" defaultInventoryItemTypeId="NON_SERIAL_INV_ITEM" facilityName="Warehouse"/>
                       	 	Element facilityElement = new Element("Facility");
                       	    facilityElement.setAttribute(new Attribute("facilityId", companyPartyId));
                       	    facilityElement.setAttribute(new Attribute("facilityTypeId", "WAREHOUSE"));
                       	    facilityElement.setAttribute(new Attribute("ownerPartyId", companyPartyId));
                       	    facilityElement.setAttribute(new Attribute("defaultInventoryItemTypeId", "NON_SERIAL_INV_ITEM"));
                       	    facilityElement.setAttribute(new Attribute("facilityName", "WAREHOUSE"));
                	 	    companyList.add(facilityElement);
                       	// <ContactMech contactMechId="VA_1003" contactMechTypeId="POSTAL_ADDRESS"/>
                       	Element contactMechElement = new Element("ContactMech");
                       	String contactMechId = "VA_"+companyPartyId;
                       	contactMechElement.setAttribute(new Attribute("contactMechId", contactMechId));
                       	contactMechElement.setAttribute(new Attribute("contactMechTypeId", "POSTAL_ADDRESS"));
               	 	    companyList.add(contactMechElement);
                       	 // <ContactMech contactMechId="VA_1005" contactMechTypeId="EMAIL_ADDRESS" infoString="bpatel@gmail.com"/>
                       	//Element contactMechEmailElement = new Element("ContactMech");
                    	//contactMechEmailElement.setAttribute(new Attribute("contactMechId", "VAE_"+companyPartyId));
                   	    //contactMechEmailElement.setAttribute(new Attribute("contactMechTypeId", "EMAIL_ADDRESS"));
               	 	    //contactMechEmailElement.setAttribute(new Attribute("infoString", companInfoMapWithValue.get("EMAIL_ADDRESS")));
               	 	    //companyList.add(contactMechEmailElement);
                       //<PostalAddress contactMechId="VA_1003" toName="GAADIZO_COMPANY Warehouse" attnName="GAADIZO_COMPANY Warehouse" address1="2003 Warehouse Blvd" city="GAADIZO_COMPANY City" 
               	 	   // postalCode="11530" countryGeoId="USA" stateProvinceGeoId="NY"/>
               	 	    
               	 	    /*
               	 	     
    <PartyContactMech partyId="Company" contactMechId="9000" fromDate="2000-01-01 00:00:00.000" allowSolicitation="Y"/>
    <PostalAddress contactMechId="9000" toName="Company XYZ" address1="2003 Open Blvd" city="Open City" postalCode="999999" countryGeoId="USA" stateProvinceGeoId="CA" geoPointId="9000"/>
    <ContactMech contactMechId="Company" contactMechTypeId="EMAIL_ADDRESS" infoString="ofbiztest@yahoo.com"/>
    <PartyContactMech partyId="Company" contactMechId="Company" fromDate="2000-01-01 00:00:00.000" allowSolicitation="Y"/>
    <PartyContactMechPurpose contactMechPurposeTypeId="PRIMARY_EMAIL" partyId="Company" contactMechId="Company" fromDate="2003-01-01 00:00:00.000"/>
    <PartyContactMechPurpose partyId="Company" contactMechId="9000" contactMechPurposeTypeId="BILLING_LOCATION" fromDate="2000-01-01 00:00:00.000"/>
    <PartyContactMechPurpose partyId="Company" contactMechId="9000" contactMechPurposeTypeId="GENERAL_LOCATION" fromDate="2000-01-01 00:00:00.000"/>
    <PartyContactMechPurpose partyId="Company" contactMechId="9000" contactMechPurposeTypeId="PAYMENT_LOCATION" fromDate="2000-01-01 00:00:00.000"/>
    <PartyGeoPoint partyId="Company" geoPointId="9000" fromDate="2009-01-09 00:00:00.000"/>
               	 	     */
               	 	Element partyContactMechElement = new Element("PartyContactMech");
               	 	partyContactMechElement.setAttribute(new Attribute("partyId", companyPartyId));
               	 	partyContactMechElement.setAttribute(new Attribute("contactMechId", contactMechId));
               	    partyContactMechElement.setAttribute(new Attribute("allowSolicitation", "Y"));
               	 	partyContactMechElement.setAttribute(new Attribute("fromDate", UtilDateTime.nowTimestamp().toString()));
           	 	    companyList.add(partyContactMechElement);
           	 	    
               	 	Element partyContactMechPurpose1Element = new Element("PartyContactMechPurpose");
               	 partyContactMechPurpose1Element.setAttribute(new Attribute("partyId", companyPartyId));
               	partyContactMechPurpose1Element.setAttribute(new Attribute("contactMechId", contactMechId));
               	partyContactMechPurpose1Element.setAttribute(new Attribute("contactMechPurposeTypeId", "BILLING_LOCATION"));
               	partyContactMechPurpose1Element.setAttribute(new Attribute("fromDate", UtilDateTime.nowTimestamp().toString()));
           	 	    companyList.add(partyContactMechPurpose1Element);
           	 	Element partyContactMechPurpose2Element = new Element("PartyContactMechPurpose");
           	 partyContactMechPurpose2Element.setAttribute(new Attribute("partyId", companyPartyId));
           	partyContactMechPurpose2Element.setAttribute(new Attribute("contactMechId", contactMechId));
           	partyContactMechPurpose2Element.setAttribute(new Attribute("contactMechPurposeTypeId", "GENERAL_LOCATION"));
           	partyContactMechPurpose2Element.setAttribute(new Attribute("fromDate", UtilDateTime.nowTimestamp().toString()));
          	 	    companyList.add(partyContactMechPurpose2Element);
          	 	 Element partyContactMechPurpose3Element = new Element("PartyContactMechPurpose");
          	 	partyContactMechPurpose3Element.setAttribute(new Attribute("partyId", companyPartyId));
          	 	partyContactMechPurpose3Element.setAttribute(new Attribute("contactMechId", contactMechId));
          	 	partyContactMechPurpose3Element.setAttribute(new Attribute("contactMechPurposeTypeId", "PAYMENT_LOCATION"));
          	 	partyContactMechPurpose3Element.setAttribute(new Attribute("fromDate", UtilDateTime.nowTimestamp().toString()));
           	 	    companyList.add(partyContactMechPurpose3Element);
               	 	    
                       	Element postalAddressElement = new Element("PostalAddress");
                       	postalAddressElement.setAttribute(new Attribute("contactMechId", contactMechId));
                       	postalAddressElement.setAttribute(new Attribute("toName", companInfoMapWithValue.get("COMPANY_NAME")));
                    	postalAddressElement.setAttribute(new Attribute("contactNumber", companInfoMapWithValue.get("CONTACTNUMBER")));
                       	postalAddressElement.setAttribute(new Attribute("attnName", companInfoMapWithValue.get("COMPANY_NAME")));
                       	postalAddressElement.setAttribute(new Attribute("address1", companInfoMapWithValue.get("ADDRESS1")));
                       	if(UtilValidate.isNotEmpty(companInfoMapWithValue.get("ADDRESS2"))){
                       	    postalAddressElement.setAttribute(new Attribute("address2", companInfoMapWithValue.get("ADDRESS2")));
                       	}
                       	postalAddressElement.setAttribute(new Attribute("city", companInfoMapWithValue.get("CITY")));
                       	postalAddressElement.setAttribute(new Attribute("countryGeoId", "IND"));
                       	if(UtilValidate.isNotEmpty(companInfoMapWithValue.get("COMPANY_NAME"))){
                       		List<GenericValue> geoGVs = EntityQuery.use(delegator).from("Geo").where("geoTypeId", "STATE", "geoName", companInfoMapWithValue.get("STATE")).queryList();
                       		if(UtilValidate.isNotEmpty(geoGVs)){
                       			GenericValue geoGV = EntityUtil.getFirst(geoGVs);
                       			postalAddressElement.setAttribute(new Attribute("stateProvinceGeoId", geoGV.get("geoId").toString()));
                       		}
                       	}
               	 	    companyList.add(postalAddressElement);
               	 	    
                       	// <FacilityContactMech facilityId="GAADIZO_COMPANY" contactMechId="VA_1003" fromDate="2001-05-13 12:00:00.0"/>
                       	Element facilityContactMechElement = new Element("FacilityContactMech");
                       	facilityContactMechElement.setAttribute(new Attribute("facilityId", companyPartyId));
                       	facilityContactMechElement.setAttribute(new Attribute("contactMechId", contactMechId));
                       	facilityContactMechElement.setAttribute(new Attribute("fromDate", UtilDateTime.nowTimestamp().toString()));
               	 	    companyList.add(facilityContactMechElement);
               	 	    
                       	// <FacilityContactMech facilityId="GAADIZO_COMPANY" contactMechId="VA_1004" fromDate="2001-05-13 12:00:00.0"/>
                       	//Element facilityContactMechE = new Element("FacilityContactMech");
                       //	facilityContactMechE.setAttribute(new Attribute("partyId", companyPartyId));
                      // 	facilityContactMechE.setAttribute(new Attribute("roleTypeId", "_NA_"));
               	 	   //facilityContactMechE.setAttribute(new Attribute("fromDate", UtilDateTime.nowTimestamp()));
               	 	  //  companyList.add(facilityContactMechE);
                       	// <FacilityContactMech facilityId="GAADIZO_COMPANY" contactMechId="VA_1005" fromDate="2001-05-13 12:00:00.0"/>
                    /*   	Element partyRoleElement = new Element("PartyRole");
                    	partyRoleElement.setAttribute(new Attribute("partyId", companyPartyId));
                   	    partyRoleElement.setAttribute(new Attribute("roleTypeId", "_NA_"));
               	 	    companyList.add(partyRoleElement);*/
                       	 //<FacilityContactMechPurpose facilityId="GAADIZO_COMPANY" contactMechId="VA_1003" contactMechPurposeTypeId="SHIPPING_LOCATION" fromDate="2001-05-13 12:00:00.0"/>
                       	Element facilityContactMechPurposeElement = new Element("FacilityContactMechPurpose");
                       	facilityContactMechPurposeElement.setAttribute(new Attribute("facilityId", companyPartyId));
                       	facilityContactMechPurposeElement.setAttribute(new Attribute("contactMechId", contactMechId));
                       	facilityContactMechPurposeElement.setAttribute(new Attribute("contactMechPurposeTypeId", "SHIPPING_LOCATION"));
                       	facilityContactMechPurposeElement.setAttribute(new Attribute("fromDate", UtilDateTime.nowTimestamp().toString()));
               	 	    companyList.add(facilityContactMechPurposeElement);
               	 	    Element facilityContactMechPurposeEElement = new Element("FacilityContactMechPurpose");
               	  		facilityContactMechPurposeEElement.setAttribute(new Attribute("facilityId", companyPartyId));
               	  		facilityContactMechPurposeEElement.setAttribute(new Attribute("contactMechId", contactMechId));
               	  		facilityContactMechPurposeEElement.setAttribute(new Attribute("contactMechPurposeTypeId", "SHIP_ORIG_LOCATION"));
               	  		facilityContactMechPurposeEElement.setAttribute(new Attribute("fromDate", UtilDateTime.nowTimestamp().toString()));
               	  		companyList.add(facilityContactMechPurposeEElement);
               	  		
               	  	Element SequenceValueItemEElement = new Element("SequenceValueItem");
               	  	SequenceValueItemEElement.setAttribute(new Attribute("ownerId", companyPartyId));
               	  	SequenceValueItemEElement.setAttribute(new Attribute("prefix", companInfoMapWithValue.get("INVOICE_PREFIX")));
               	  	SequenceValueItemEElement.setAttribute(new Attribute("prefixType", "INVOICE"));//Todo
               	  	SequenceValueItemEElement.setAttribute(new Attribute("seqId", companInfoMapWithValue.get("INVOICE_SEQ")));
               	  	SequenceValueItemEElement.setAttribute(new Attribute("seqName","INVOICE"+companyPartyId));
           	  		companyList.add(SequenceValueItemEElement);
           	  		
           	  	Element SequenceValueItemJCElement = new Element("SequenceValueItem");
           	  	SequenceValueItemJCElement.setAttribute(new Attribute("ownerId", companyPartyId));
           	  	SequenceValueItemJCElement.setAttribute(new Attribute("prefix", companInfoMapWithValue.get("JOBCARD_PREFIX")));
           	  	SequenceValueItemJCElement.setAttribute(new Attribute("prefixType", "JOBCARD"));
           	  	SequenceValueItemJCElement.setAttribute(new Attribute("seqId", companInfoMapWithValue.get("JOBCARD_SEQ")));//Todo
           	  	SequenceValueItemJCElement.setAttribute(new Attribute("seqName", "JOBCARD"+companyPartyId));
       	  		companyList.add(SequenceValueItemJCElement);
       	  		
       	  	Element SequenceValueItemORDERElement = new Element("SequenceValueItem");
       	  	SequenceValueItemORDERElement.setAttribute(new Attribute("ownerId", companyPartyId));
       	  	SequenceValueItemORDERElement.setAttribute(new Attribute("prefix", companInfoMapWithValue.get("INVOICE_PREFIX")));
       	  	SequenceValueItemORDERElement.setAttribute(new Attribute("prefixType", "ORDER"));
       	  	SequenceValueItemORDERElement.setAttribute(new Attribute("seqId", companInfoMapWithValue.get("INVOICE_SEQ")));//Todo
       	  	SequenceValueItemORDERElement.setAttribute(new Attribute("seqName", "ORDER"+companyPartyId));
      	  		companyList.add(SequenceValueItemORDERElement);
       	  		
      	  	Element SequenceValueItemPARTYElement = new Element("SequenceValueItem");
       	  	SequenceValueItemPARTYElement.setAttribute(new Attribute("ownerId", companyPartyId));
       	  	SequenceValueItemPARTYElement.setAttribute(new Attribute("prefix", companInfoMapWithValue.get("JOBCARD_PREFIX")));
       	  	SequenceValueItemPARTYElement.setAttribute(new Attribute("prefixType", "PARTY"));
       	  	SequenceValueItemPARTYElement.setAttribute(new Attribute("seqId", companInfoMapWithValue.get("INVOICE_SEQ")));//Todo
       	  	SequenceValueItemPARTYElement.setAttribute(new Attribute("seqName", "PARTY"+companyPartyId));
      	  		companyList.add(SequenceValueItemPARTYElement);
      	  		
      	  
      	   
       	  		
                       	 //<FacilityContactMechPurpose facilityId="GAADIZO_COMPANY" contactMechId="VA_1005" contactMechPurposeTypeId="PRIMARY_EMAIL" fromDate="2001-05-13 12:00:00.0"/>
                       	/*Element partyRoleElement = new Element("PartyRole");
                    	partyRoleElement.setAttribute(new Attribute("partyId", companyPartyId));
                   	    partyRoleElement.setAttribute(new Attribute("roleTypeId", "_NA_"));
               	 	    companyList.add(partyRoleElement); */
                       	 /*//<FacilityLocation facilityId="GAADIZO_COMPANY" locationSeqId="TLTLTLLL01" locationTypeEnumId="FLT_PICKLOC" areaId="TL" aisleId="TL" sectionId="TL" levelId="LL" positionId="01"/>
                       	Element partyRoleElement = new Element("PartyRole");
                    	partyRoleElement.setAttribute(new Attribute("partyId", companyPartyId));
                   	    partyRoleElement.setAttribute(new Attribute("roleTypeId", "_NA_"));
               	 	    companyList.add(partyRoleElement);
                       	 //<FacilityLocation facilityId="GAADIZO_COMPANY" locationSeqId="TLTLTLUL01" locationTypeEnumId="FLT_BULK" areaId="TL" aisleId="TL" sectionId="TL" levelId="UL" positionId="01"/>
                       	Element partyRoleElement = new Element("PartyRole");
                    	partyRoleElement.setAttribute(new Attribute("partyId", companyPartyId));
                   	    partyRoleElement.setAttribute(new Attribute("roleTypeId", "_NA_"));
               	 	    companyList.add(partyRoleElement);
                       	// <FacilityLocation facilityId="GAADIZO_COMPANY" locationSeqId="TLTLTLLL02" locationTypeEnumId="FLT_PICKLOC" areaId="TL" aisleId="TL" sectionId="TL" levelId="LL" positionId="02"/>
                       	Element partyRoleElement = new Element("PartyRole");
                    	partyRoleElement.setAttribute(new Attribute("partyId", companyPartyId));
                   	    partyRoleElement.setAttribute(new Attribute("roleTypeId", "_NA_"));
               	 	    companyList.add(partyRoleElement);
                       	// <FacilityLocation facilityId="GAADIZO_COMPANY" locationSeqId="TLTLTLUL02" locationTypeEnumId="FLT_BULK" areaId="TL" aisleId="TL" sectionId="TL" levelId="UL" positionId="02"/>
                       	Element partyRoleElement = new Element("PartyRole");
                    	partyRoleElement.setAttribute(new Attribute("partyId", companyPartyId));
                   	    partyRoleElement.setAttribute(new Attribute("roleTypeId", "_NA_"));
               	 	    companyList.add(partyRoleElement);
                       	 //<FacilityLocation facilityId="GAADIZO_COMPANY" locationSeqId="TLTLTLLL03" locationTypeEnumId="FLT_PICKLOC" areaId="TL" aisleId="TL" sectionId="TL" levelId="LL" positionId="03"/>
                       	Element partyRoleElement = new Element("PartyRole");
                    	partyRoleElement.setAttribute(new Attribute("partyId", companyPartyId));
                   	    partyRoleElement.setAttribute(new Attribute("roleTypeId", "_NA_"));
               	 	    companyList.add(partyRoleElement);
                       	// <FacilityLocation facilityId="GAADIZO_COMPANY" locationSeqId="TLTLTLUL03" locationTypeEnumId="FLT_BULK" areaId="TL" aisleId="TL" sectionId="TL" levelId="UL" positionId="03"/>
                       	Element partyRoleElement = new Element("PartyRole");
                    	partyRoleElement.setAttribute(new Attribute("partyId", companyPartyId));
                   	    partyRoleElement.setAttribute(new Attribute("roleTypeId", "_NA_"));
               	 	    companyList.add(partyRoleElement);
                       	// <FacilityLocation facilityId="GAADIZO_COMPANY" locationSeqId="TLTLTLLL04" locationTypeEnumId="FLT_PICKLOC" areaId="TL" aisleId="TL" sectionId="TL" levelId="LL" positionId="04"/>
                       	Element partyRoleElement = new Element("PartyRole");
                    	partyRoleElement.setAttribute(new Attribute("partyId", companyPartyId));
                   	    partyRoleElement.setAttribute(new Attribute("roleTypeId", "_NA_"));
               	 	    companyList.add(partyRoleElement);
                       	// <FacilityLocation facilityId="GAADIZO_COMPANY" locationSeqId="TLTLTLUL04" locationTypeEnumId="FLT_BULK" areaId="TL" aisleId="TL" sectionId="TL" levelId="UL" positionId="04"/>
                       	Element partyRoleElement = new Element("PartyRole");
                    	partyRoleElement.setAttribute(new Attribute("partyId", companyPartyId));
                   	    partyRoleElement.setAttribute(new Attribute("roleTypeId", "_NA_"));
               	 	    companyList.add(partyRoleElement);*/
                       //=========================================================
               	  	   Element productStoreElement = new Element("ProductStore");
               	  productStoreElement.setAttribute(new Attribute("productStoreId",companyPartyId)); 
               	  productStoreElement.setAttribute(new Attribute("storeName",(String) companInfoMapWithValue.get("COMPANY_NAME"))); 
               	productStoreElement.setAttribute(new Attribute("companyName","Open For Business")); 
               	productStoreElement.setAttribute(new Attribute("title","ERP System"));
               	productStoreElement.setAttribute(new Attribute("subtitle", companInfoMapWithValue.get("COMPANY_NAME"))); 
               	productStoreElement.setAttribute(new Attribute("payToPartyId",companyPartyId)); 
               	productStoreElement.setAttribute(new Attribute("daysToCancelNonPay","30")); 
               	productStoreElement.setAttribute(new Attribute("prorateShipping","Y")); 
               	productStoreElement.setAttribute(new Attribute("prorateTaxes","Y"));
               	productStoreElement.setAttribute(new Attribute("inventoryFacilityId", companyPartyId));
               	productStoreElement.setAttribute(new Attribute("oneInventoryFacility","Y")); 
               	productStoreElement.setAttribute(new Attribute("checkInventory","Y")); 
               	productStoreElement.setAttribute(new Attribute("reserveInventory","Y"));
                productStoreElement.setAttribute(new Attribute("balanceResOnOrderCreation","Y")); 
				productStoreElement.setAttribute(new Attribute("reserveOrderEnumId","INVRO_FIFO_REC")); 
				productStoreElement.setAttribute(new Attribute("requireInventory","N"));
				productStoreElement.setAttribute(new Attribute("defaultLocaleString","en_US")); 
				productStoreElement.setAttribute(new Attribute("defaultCurrencyUomId","INR"));
				productStoreElement.setAttribute(new Attribute("defaultSalesChannelEnumId","WEB_SALES_CHANNEL")); 
				productStoreElement.setAttribute(new Attribute("allowPassword","Y")); 
				productStoreElement.setAttribute(new Attribute("explodeOrderItems","N")); 
				productStoreElement.setAttribute(new Attribute("retryFailedAuths","Y")); 
				productStoreElement.setAttribute(new Attribute("reqReturnInventoryReceive","N"));
				productStoreElement.setAttribute(new Attribute("headerApprovedStatus","ORDER_APPROVED"));
				productStoreElement.setAttribute(new Attribute("itemApprovedStatus","ITEM_APPROVED"));
				productStoreElement.setAttribute(new Attribute("digitalItemApprovedStatus","ITEM_APPROVED"));
				productStoreElement.setAttribute(new Attribute("headerDeclinedStatus","ORDER_REJECTED"));
				productStoreElement.setAttribute(new Attribute("itemDeclinedStatus","ITEM_REJECTED"));
				productStoreElement.setAttribute(new Attribute("headerCancelStatus","ORDER_CANCELLED"));
				productStoreElement.setAttribute(new Attribute("itemCancelStatus","ITEM_CANCELLED"));
				productStoreElement.setAttribute(new Attribute("orderNumberPrefix","WS"));
				productStoreElement.setAttribute(new Attribute("authDeclinedMessage","There has been a problem with your method of payment. Please try a different method or call customer service."));
				productStoreElement.setAttribute(new Attribute("authFraudMessage","Your order has been rejected and your account has been disabled due to fraud."));
				productStoreElement.setAttribute(new Attribute("authErrorMessage","Problem connecting to payment processor; we will continue to retry and notify you by email."));
				productStoreElement.setAttribute(new Attribute("storeCreditValidDays","90"));
				productStoreElement.setAttribute(new Attribute("storeCreditAccountEnumId","FIN_ACCOUNT"));
				productStoreElement.setAttribute(new Attribute("visualThemeId","EC_DEFAULT"));
				productStoreElement.setAttribute(new Attribute("prodSearchExcludeVariants","Y"));
				productStoreElement.setAttribute(new Attribute("autoApproveInvoice","Y"));
				productStoreElement.setAttribute(new Attribute("shipIfCaptureFails","Y"));
				productStoreElement.setAttribute(new Attribute("autoApproveOrder","Y"));
				productStoreElement.setAttribute(new Attribute("showOutOfStockProducts","Y"));
				companyList.add(productStoreElement);
				
				Element productStoreFacilityElement = new Element("ProductStoreFacility");
				productStoreFacilityElement.setAttribute(new Attribute("productStoreId",companyPartyId));
				productStoreFacilityElement.setAttribute(new Attribute("facilityId",companyPartyId));
				productStoreFacilityElement.setAttribute(new Attribute("fromDate", UtilDateTime.nowTimestamp().toString()));
				companyList.add(productStoreFacilityElement);
                            }
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
    				Map entityImportDirParams = UtilMisc.toMap("path", companyextractoutputpath, "userLogin", context.get("userLogin"));
    System.out.println("====entityImportDirParams======"+entityImportDirParams+"==90909090==="+uploadedFilepath);
    				Map entityImportDirectoryForERPResult = dispatcher.runSync("entityImportDirectoryForERP", entityImportDirParams);

    				if (!ServiceUtil.isError(entityImportDirectoryForERPResult)) {
    					moveXMLFilesFromDir(companyextractoutputpath, companyextractsuccessxmlpath);
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
			file.delete();
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
		
		String finalPath =  companyextractoutputpath ;
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
	
	/*private static Map<String, String> buildDataRows(String[] s, File file) {
		Map<String, String> data = new HashMap<String, String>();
		try {
			for (OrderCSV orderCSVs : OrderCSV.values()) {
				System.out.println("258 OrderXML.values() "+OrderCSV.values());
				data.put(orderCSVs.name(), ("null".equalsIgnoreCase(s[orderCSVs.getCode()]) ? " "
						: StringUtils.replace(s[orderCSVs.getCode()], "~", ",")));
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
	}*/
	
	
	/*public static Map<String, Object> prepareAndImportCategoryXML(DispatchContext ctx, Map<String, ?> context)
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
		categoryExtractInputPath = UtilProperties.getPropertyValue("gaadizo.properties", "categoryextract-input-path");
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
	}*/

	/*public static void readCSVAndConvertToCategoryXML(File file) throws IOException, CategoryImportException {
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
		
	}*/

	/*private static Map<String, String> buildDataRows(String[] s, File file) throws CategoryImportException {
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
	}*/

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

	/*@SuppressWarnings("unchecked")
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
	}*/


	


	/*@SuppressWarnings({ "unchecked", "rawtypes" })
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
	}*/

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
	            //error_list.add(UtilProperties.getMessage("OSafeAdminUiLabels", "BlankUploadFileError", locale));
	           /* tmp = new MessageString("incorrect file","_uploadedFile_fileName",true);
	            error_list.add(tmp);*/
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
	 /*public static List buildDataRows(List headerCols,HSSFSheet s) 
	    {
			List dataRows = new ArrayList();
			try 
			{
	            for (int rowCount = 1 ; rowCount < s.getRows()-1 ; rowCount++) 
	            {
	            	Cell[] row = s.getRow(rowCount);
	                if (row.length > 0) 
	                {
	            	    Map mRows = new HashMap();
	                    for (int colCount = 0; colCount < headerCols.size(); colCount++) 
	                    {
	                	    String colContent=null;
	                	    try 
	                	    {
	                		    colContent=row[colCount].getContents();
	                	    }
	                	    catch (Exception e) 
	                	    {
	                		    colContent="";
	                   	    }
	                        mRows.put(headerCols.get(colCount), StringUtil.replaceString(colContent,"\"","'"));
	                    }
	                    dataRows.add(mRows);
	                }
	            }
	    	}
	      	catch (Exception e) 
	      	{
	   	    }
	      	return dataRows;
	    }
*/

	 /*private static void readFolder(GenericValue userLogin, LocalDispatcher dispatcher) throws OrderImportException {
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
								readCSVAndConvertToCompanyXML(file, userLogin, dispatcher);
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
		}*/

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
			                	
			                	
			                	
			                	/*
			                    // read productId from first column "sheet column index
			                    // starts from 0"
			                    HSSFCell cell2 = row.getCell(2);
			                    cell2.setCellType(HSSFCell.CELL_TYPE_STRING);
			                    String productId = cell2.getRichStringCellValue().toString();
			                    // read QOH from ninth column
			                    HSSFCell cell5 = row.getCell(5);
			                    BigDecimal quantityOnHand = BigDecimal.ZERO;
			                    if (cell5 != null && cell5.getCellType() == HSSFCell.CELL_TYPE_NUMERIC)
			                        quantityOnHand = new BigDecimal(cell5.getNumericCellValue());

			                    // check productId if null then skip creating inventory item
			                    // too.
			                    boolean productExists = ImportProductHelper.checkProductExists(productId, delegator);

			                    if (productId != null && !productId.trim().equalsIgnoreCase("") && !productExists) {
			                        products.add(ImportProductHelper.prepareProduct(productId));
			                        if (quantityOnHand.compareTo(BigDecimal.ZERO) >= 0)
			                            inventoryItems.add(ImportProductHelper.prepareInventoryItem(productId, quantityOnHand,
			                                    delegator.getNextSeqId("InventoryItem")));
			                        else
			                            inventoryItems.add(ImportProductHelper.prepareInventoryItem(productId, BigDecimal.ZERO, delegator
			                                    .getNextSeqId("InventoryItem")));
			                    }
			                    int rowNum = row.getRowNum() + 1;
			                    if (row.toString() != null && !row.toString().trim().equalsIgnoreCase("") && productExists) {
			                        Debug.logWarning("Row number " + rowNum + " not imported from " + item.getName(), module);
			                    }
			                */}
			            }
					
					//orderList.add(buildDataRows(orderLine, file));
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
				/*try {
					File destDir = new File(orderExtractInputErrorPath);
					FileUtils.copyFileToDirectory(file, destDir);
					file.delete();
				} catch (IOException e1) {
					throw new OrderImportException(e1.getMessage());
				}*/
			}
			return data;
		}

		public static Map<String, Object> importCategryMasterCSV(DispatchContext dctx, Map<String, ? extends Object> context) {
	        Delegator delegator = dctx.getDelegator();
	        Locale locale = (Locale) context.get("locale");
	        LocalDispatcher dispatcher = dctx.getDispatcher();
	        Map result = new HashMap<String, Object>();
	        orderExtractOutputPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("gaadizo.properties", "orderextract-output-path"), context);
	        companyextractoutputpath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("gaadizo.properties", "companyextract-output-path"), context);
	        String companyextractsuccessxmlpath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("gaadizo.properties", "companyextract-success-xml-path"), context);
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
	                            	if(UtilValidate.isNotEmpty(row.getCell(0))){
	                            		companInfoMap.put("COMPANYID", row.getCell(0).getRichStringCellValue().toString());
	                            		if(UtilValidate.isNotEmpty(row.getCell(1))){
	                            			companInfoMap.put("COMPANY_NAME", row.getCell(1).getRichStringCellValue().toString());
	                            		}
	                                	if(UtilValidate.isNotEmpty(row.getCell(2))){
	                                		row.getCell(2).setCellType(HSSFCell.CELL_TYPE_STRING);
	                                		companInfoMap.put("CONTACTNUMBER", row.getCell(2).getRichStringCellValue().toString());
	                                    	}
	                                	if(UtilValidate.isNotEmpty(row.getCell(3))){
	                                		companInfoMap.put("ADDRESS1", row.getCell(3).getRichStringCellValue().toString());
	                                    	}
	                                	if(UtilValidate.isNotEmpty(row.getCell(4))){
	                                		companInfoMap.put("ADDRESS2", row.getCell(4).getRichStringCellValue().toString());
	                                    	}
	                                	if(UtilValidate.isNotEmpty(row.getCell(5))){
	                                		companInfoMap.put("CITY", row.getCell(5).getRichStringCellValue().toString());
	                                    	}
	                                	if(UtilValidate.isNotEmpty(row.getCell(6))){
	                                		row.getCell(6).setCellType(HSSFCell.CELL_TYPE_STRING);
	                                		companInfoMap.put("STATE", row.getCell(6).getRichStringCellValue().toString());
	                                    	}
	                                	if(UtilValidate.isNotEmpty(row.getCell(7))){
	                                		row.getCell(7).setCellType(HSSFCell.CELL_TYPE_STRING);
	                                		companInfoMap.put("POSTALCODE", row.getCell(7).getRichStringCellValue().toString());
	                                    	}
	                                	if(UtilValidate.isNotEmpty(row.getCell(8))){
	                                		row.getCell(8).setCellType(HSSFCell.CELL_TYPE_STRING);
	                                		companInfoMap.put("JOBCARD_PREFIX", row.getCell(8).getRichStringCellValue().toString());
	                                    	}
	                                	if(UtilValidate.isNotEmpty(row.getCell(9))){
	                                		row.getCell(9).setCellType(HSSFCell.CELL_TYPE_STRING);
	                                		companInfoMap.put("INVOICE_PREFIX", row.getCell(9).getRichStringCellValue().toString());
	                                    	}
	                                	if(UtilValidate.isNotEmpty(row.getCell(10))){
	                                		row.getCell(10).setCellType(HSSFCell.CELL_TYPE_STRING);
	                                		companInfoMap.put("LOGOURL", row.getCell(10).getRichStringCellValue().toString());
	                                    	}
	                                	if(UtilValidate.isNotEmpty(row.getCell(11))){
	                                		row.getCell(11).setCellType(HSSFCell.CELL_TYPE_STRING);
	                                		companInfoMap.put("GSTNNO", row.getCell(11).getRichStringCellValue().toString());
	                                    	}
	                                	}
	                            	companInfoMapListMap.put(row.getCell(0).getRichStringCellValue().toString(), companInfoMap);
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
	            		if(UtilValidate.isNotEmpty(companyPartyId)){
	            			GenericValue partyExisting = EntityQuery.use(delegator).from("Party").where("partyId",companyPartyId).queryOne();
	            			if(UtilValidate.isNotEmpty(partyExisting)){
	            				continue;
	            			} else {
	            				if(UtilValidate.isNotEmpty(companyPartyId)){
	            					
	            					Map<String, String> companInfoMapWithValue = companInfoMapListMap.get(companyPartyId);
	                           	     Element partyElement = new Element("Party");
	                           	 	 partyElement.setAttribute(new Attribute("partyId", companyPartyId));
	                           	 	 partyElement.setAttribute(new Attribute("partyTypeId", "PARTY_GROUP"));
	                           	     partyElement.setAttribute(new Attribute("statusId", "PARTY_ENABLED"));
	                           	     partyElement.setAttribute(new Attribute("createdDate", UtilDateTime.nowTimestamp().toString()));
	                           	 	 partyElement.setAttribute(new Attribute("preferredCurrencyUomId", "INR"));
	                           	 	companyList.add(partyElement);
	                           	 Element partyGroupElement = new Element("PartyGroup");
	                     	 	   partyGroupElement.setAttribute(new Attribute("partyId", companyPartyId));
	                     	       partyGroupElement.setAttribute(new Attribute("groupName", companInfoMapWithValue.get("COMPANY_NAME")));
	                     	       if(UtilValidate.isNotEmpty(companInfoMapWithValue.get("LOGOIMAGEURL"))){
	                     	      partyGroupElement.setAttribute(new Attribute("logoImageUrl", companInfoMapWithValue.get("LOGOIMAGEURL")));
	                     	       }
	                  	 	       companyList.add(partyGroupElement);
	                           	 	
	                           	    Element partyRole1Element = new Element("PartyRole");
	                           	 partyRole1Element.setAttribute(new Attribute("partyId", companyPartyId));
	                           	partyRole1Element.setAttribute(new Attribute("roleTypeId", "_NA_"));
	                       	 	    companyList.add(partyRole1Element);
	                       	 	    
	                       	 	Element partyRole2Element = new Element("PartyRole");
	                       	 partyRole2Element.setAttribute(new Attribute("partyId", companyPartyId));
	                       	partyRole2Element.setAttribute(new Attribute("roleTypeId", "INTERNAL_ORGANIZATIO"));
	                   	 	    companyList.add(partyRole2Element);
	                   	 	Element partyRole3Element = new Element("PartyRole");
	                   	 partyRole3Element.setAttribute(new Attribute("partyId", companyPartyId));
	                   	partyRole3Element.setAttribute(new Attribute("roleTypeId", "BILL_FROM_VENDOR"));
	               	 	    companyList.add(partyRole3Element);
	               	 	Element partyRole4Element = new Element("PartyRole");
	               	 partyRole4Element.setAttribute(new Attribute("partyId", companyPartyId));
	               	partyRole4Element.setAttribute(new Attribute("roleTypeId", "BILL_TO_CUSTOMER"));
	           	 	    companyList.add(partyRole4Element);
	                       	 	   Element partyStatusElement = new Element("PartyStatus");
	                       	 	   partyStatusElement.setAttribute(new Attribute("partyId", companyPartyId));
	                       	 	   partyStatusElement.setAttribute(new Attribute("statusId", "PARTY_ENABLED"));
	                       	 	   partyStatusElement.setAttribute(new Attribute("statusDate", UtilDateTime.nowTimestamp().toString()));
	                       	 	   companyList.add(partyStatusElement);
	                    	 	  
	                    	 	  //<GlJournal organizationPartyId="Company" glJournalId="ERROR_JOURNAL" glJournalName="Suspense transactions"/>
	                       	 	   //======================================================================================================
	                       	 	//<Facility facilityId="GAADIZO_COMPANY" facilityTypeId="WAREHOUSE" ownerPartyId="GAADIZO_COMPANY" defaultInventoryItemTypeId="NON_SERIAL_INV_ITEM" facilityName="Warehouse"/>
	                       	 	Element facilityElement = new Element("Facility");
	                       	    facilityElement.setAttribute(new Attribute("facilityId", companyPartyId));
	                       	    facilityElement.setAttribute(new Attribute("facilityTypeId", "WAREHOUSE"));
	                       	    facilityElement.setAttribute(new Attribute("ownerPartyId", companyPartyId));
	                       	    facilityElement.setAttribute(new Attribute("defaultInventoryItemTypeId", "NON_SERIAL_INV_ITEM"));
	                       	    facilityElement.setAttribute(new Attribute("facilityName", "WAREHOUSE"));
	                	 	    companyList.add(facilityElement);
	                       	// <ContactMech contactMechId="VA_1003" contactMechTypeId="POSTAL_ADDRESS"/>
	                       	Element contactMechElement = new Element("ContactMech");
	                       	String contactMechId = "VA_"+companyPartyId;
	                       	contactMechElement.setAttribute(new Attribute("contactMechId", contactMechId));
	                       	contactMechElement.setAttribute(new Attribute("contactMechTypeId", "POSTAL_ADDRESS"));
	               	 	    companyList.add(contactMechElement);
	               	 	Element partyContactMechElement = new Element("PartyContactMech");
	               	 	partyContactMechElement.setAttribute(new Attribute("partyId", companyPartyId));
	               	 	partyContactMechElement.setAttribute(new Attribute("contactMechId", contactMechId));
	               	    partyContactMechElement.setAttribute(new Attribute("allowSolicitation", "Y"));
	               	 	partyContactMechElement.setAttribute(new Attribute("fromDate", UtilDateTime.nowTimestamp().toString()));
	           	 	    companyList.add(partyContactMechElement);
	           	 	    
	               	 	Element partyContactMechPurpose1Element = new Element("PartyContactMechPurpose");
	               	 partyContactMechPurpose1Element.setAttribute(new Attribute("partyId", companyPartyId));
	               	partyContactMechPurpose1Element.setAttribute(new Attribute("contactMechId", contactMechId));
	               	partyContactMechPurpose1Element.setAttribute(new Attribute("contactMechPurposeTypeId", "BILLING_LOCATION"));
	               	partyContactMechPurpose1Element.setAttribute(new Attribute("fromDate", UtilDateTime.nowTimestamp().toString()));
	           	 	    companyList.add(partyContactMechPurpose1Element);
	           	 	Element partyContactMechPurpose2Element = new Element("PartyContactMechPurpose");
	           	 partyContactMechPurpose2Element.setAttribute(new Attribute("partyId", companyPartyId));
	           	partyContactMechPurpose2Element.setAttribute(new Attribute("contactMechId", contactMechId));
	           	partyContactMechPurpose2Element.setAttribute(new Attribute("contactMechPurposeTypeId", "GENERAL_LOCATION"));
	           	partyContactMechPurpose2Element.setAttribute(new Attribute("fromDate", UtilDateTime.nowTimestamp().toString()));
	          	 	    companyList.add(partyContactMechPurpose2Element);
	          	 	 Element partyContactMechPurpose3Element = new Element("PartyContactMechPurpose");
	          	 	partyContactMechPurpose3Element.setAttribute(new Attribute("partyId", companyPartyId));
	          	 	partyContactMechPurpose3Element.setAttribute(new Attribute("contactMechId", contactMechId));
	          	 	partyContactMechPurpose3Element.setAttribute(new Attribute("contactMechPurposeTypeId", "PAYMENT_LOCATION"));
	          	 	partyContactMechPurpose3Element.setAttribute(new Attribute("fromDate", UtilDateTime.nowTimestamp().toString()));
	           	 	    companyList.add(partyContactMechPurpose3Element);
	               	 	    
	                       	Element postalAddressElement = new Element("PostalAddress");
	                       	postalAddressElement.setAttribute(new Attribute("contactMechId", contactMechId));
	                       	postalAddressElement.setAttribute(new Attribute("toName", companInfoMapWithValue.get("COMPANY_NAME")));
	                    	postalAddressElement.setAttribute(new Attribute("contactNumber", companInfoMapWithValue.get("CONTACTNUMBER")));
	                       	postalAddressElement.setAttribute(new Attribute("attnName", companInfoMapWithValue.get("COMPANY_NAME")));
	                       	postalAddressElement.setAttribute(new Attribute("address1", companInfoMapWithValue.get("ADDRESS1")));
	                       	if(UtilValidate.isNotEmpty(companInfoMapWithValue.get("ADDRESS2"))){
	                       	    postalAddressElement.setAttribute(new Attribute("address2", companInfoMapWithValue.get("ADDRESS2")));
	                       	}
	                       	postalAddressElement.setAttribute(new Attribute("city", companInfoMapWithValue.get("CITY")));
	                       	postalAddressElement.setAttribute(new Attribute("countryGeoId", "IND"));
	                       	if(UtilValidate.isNotEmpty(companInfoMapWithValue.get("COMPANY_NAME"))){
	                       		List<GenericValue> geoGVs = EntityQuery.use(delegator).from("Geo").where("geoTypeId", "STATE", "geoName", companInfoMapWithValue.get("STATE")).queryList();
	                       		if(UtilValidate.isNotEmpty(geoGVs)){
	                       			GenericValue geoGV = EntityUtil.getFirst(geoGVs);
	                       			postalAddressElement.setAttribute(new Attribute("stateProvinceGeoId", geoGV.get("geoId").toString()));
	                       		}
	                       	}
	               	 	    companyList.add(postalAddressElement);
	               	 	    
	                       	// <FacilityContactMech facilityId="GAADIZO_COMPANY" contactMechId="VA_1003" fromDate="2001-05-13 12:00:00.0"/>
	                       	Element facilityContactMechElement = new Element("FacilityContactMech");
	                       	facilityContactMechElement.setAttribute(new Attribute("facilityId", companyPartyId));
	                       	facilityContactMechElement.setAttribute(new Attribute("contactMechId", contactMechId));
	                       	facilityContactMechElement.setAttribute(new Attribute("fromDate", UtilDateTime.nowTimestamp().toString()));
	               	 	    companyList.add(facilityContactMechElement);
	               	 	    
	                       	// <FacilityContactMech facilityId="GAADIZO_COMPANY" contactMechId="VA_1004" fromDate="2001-05-13 12:00:00.0"/>
	                       	//Element facilityContactMechE = new Element("FacilityContactMech");
	                       //	facilityContactMechE.setAttribute(new Attribute("partyId", companyPartyId));
	                      // 	facilityContactMechE.setAttribute(new Attribute("roleTypeId", "_NA_"));
	               	 	   //facilityContactMechE.setAttribute(new Attribute("fromDate", UtilDateTime.nowTimestamp()));
	               	 	  //  companyList.add(facilityContactMechE);
	                       	// <FacilityContactMech facilityId="GAADIZO_COMPANY" contactMechId="VA_1005" fromDate="2001-05-13 12:00:00.0"/>
	                    /*   	Element partyRoleElement = new Element("PartyRole");
	                    	partyRoleElement.setAttribute(new Attribute("partyId", companyPartyId));
	                   	    partyRoleElement.setAttribute(new Attribute("roleTypeId", "_NA_"));
	               	 	    companyList.add(partyRoleElement);*/
	                       	 //<FacilityContactMechPurpose facilityId="GAADIZO_COMPANY" contactMechId="VA_1003" contactMechPurposeTypeId="SHIPPING_LOCATION" fromDate="2001-05-13 12:00:00.0"/>
	                       	Element facilityContactMechPurposeElement = new Element("FacilityContactMechPurpose");
	                       	facilityContactMechPurposeElement.setAttribute(new Attribute("facilityId", companyPartyId));
	                       	facilityContactMechPurposeElement.setAttribute(new Attribute("contactMechId", contactMechId));
	                       	facilityContactMechPurposeElement.setAttribute(new Attribute("contactMechPurposeTypeId", "SHIPPING_LOCATION"));
	                       	facilityContactMechPurposeElement.setAttribute(new Attribute("fromDate", UtilDateTime.nowTimestamp().toString()));
	               	 	    companyList.add(facilityContactMechPurposeElement);
	               	 	    Element facilityContactMechPurposeEElement = new Element("FacilityContactMechPurpose");
	               	  		facilityContactMechPurposeEElement.setAttribute(new Attribute("facilityId", companyPartyId));
	               	  		facilityContactMechPurposeEElement.setAttribute(new Attribute("contactMechId", contactMechId));
	               	  		facilityContactMechPurposeEElement.setAttribute(new Attribute("contactMechPurposeTypeId", "SHIP_ORIG_LOCATION"));
	               	  		facilityContactMechPurposeEElement.setAttribute(new Attribute("fromDate", UtilDateTime.nowTimestamp().toString()));
	               	  		companyList.add(facilityContactMechPurposeEElement);
	               	  		
	               	  	Element SequenceValueItemEElement = new Element("SequenceValueItem");
	               	  	SequenceValueItemEElement.setAttribute(new Attribute("ownerId", companyPartyId));
	               	  	SequenceValueItemEElement.setAttribute(new Attribute("prefix", companInfoMapWithValue.get("INVOICE_PREFIX")));
	               	  	SequenceValueItemEElement.setAttribute(new Attribute("prefixType", "INVOICE"));//Todo
	               	  	SequenceValueItemEElement.setAttribute(new Attribute("seqId", "10000"));
	               	  	SequenceValueItemEElement.setAttribute(new Attribute("seqName","INVOICE"+companyPartyId));
	           	  		companyList.add(SequenceValueItemEElement);
	           	  		
	           	  	Element SequenceValueItemJCElement = new Element("SequenceValueItem");
	           	  	SequenceValueItemJCElement.setAttribute(new Attribute("ownerId", companyPartyId));
	           	  	SequenceValueItemJCElement.setAttribute(new Attribute("prefix", companInfoMapWithValue.get("JOBCARD_PREFIX")));
	           	  	SequenceValueItemJCElement.setAttribute(new Attribute("prefixType", "JOBCARD"));
	           	  	SequenceValueItemJCElement.setAttribute(new Attribute("seqId", "10000"));//Todo
	           	  	SequenceValueItemJCElement.setAttribute(new Attribute("seqName", "JOBCARD"+companyPartyId));
	       	  		companyList.add(SequenceValueItemJCElement);
	       	  		
	       	  	Element SequenceValueItemORDERElement = new Element("SequenceValueItem");
	       	  	SequenceValueItemORDERElement.setAttribute(new Attribute("ownerId", companyPartyId));
	       	  	SequenceValueItemORDERElement.setAttribute(new Attribute("prefix", companInfoMapWithValue.get("INVOICE_PREFIX")));
	       	  	SequenceValueItemORDERElement.setAttribute(new Attribute("prefixType", "ORDER"));
	       	  	SequenceValueItemORDERElement.setAttribute(new Attribute("seqId", "10000"));//Todo
	       	  	SequenceValueItemORDERElement.setAttribute(new Attribute("seqName", "ORDER"+companyPartyId));
	      	  		companyList.add(SequenceValueItemORDERElement);
	       	  		
	      	  	Element SequenceValueItemPARTYElement = new Element("SequenceValueItem");
	       	  	SequenceValueItemPARTYElement.setAttribute(new Attribute("ownerId", companyPartyId));
	       	  	SequenceValueItemPARTYElement.setAttribute(new Attribute("prefix", companInfoMapWithValue.get("JOBCARD_PREFIX")));
	       	  	SequenceValueItemPARTYElement.setAttribute(new Attribute("prefixType", "PARTY"));
	       	  	SequenceValueItemPARTYElement.setAttribute(new Attribute("seqId", "10000"));//Todo
	       	  	SequenceValueItemPARTYElement.setAttribute(new Attribute("seqName", "PARTY"+companyPartyId));
	      	  		companyList.add(SequenceValueItemPARTYElement);
	      	  		
	      	  
	      	   
	       	  		
	                       	 //<FacilityContactMechPurpose facilityId="GAADIZO_COMPANY" contactMechId="VA_1005" contactMechPurposeTypeId="PRIMARY_EMAIL" fromDate="2001-05-13 12:00:00.0"/>
	                       	/*Element partyRoleElement = new Element("PartyRole");
	                    	partyRoleElement.setAttribute(new Attribute("partyId", companyPartyId));
	                   	    partyRoleElement.setAttribute(new Attribute("roleTypeId", "_NA_"));
	               	 	    companyList.add(partyRoleElement); */
	                       	 /*//<FacilityLocation facilityId="GAADIZO_COMPANY" locationSeqId="TLTLTLLL01" locationTypeEnumId="FLT_PICKLOC" areaId="TL" aisleId="TL" sectionId="TL" levelId="LL" positionId="01"/>
	                       	Element partyRoleElement = new Element("PartyRole");
	                    	partyRoleElement.setAttribute(new Attribute("partyId", companyPartyId));
	                   	    partyRoleElement.setAttribute(new Attribute("roleTypeId", "_NA_"));
	               	 	    companyList.add(partyRoleElement);
	                       	 //<FacilityLocation facilityId="GAADIZO_COMPANY" locationSeqId="TLTLTLUL01" locationTypeEnumId="FLT_BULK" areaId="TL" aisleId="TL" sectionId="TL" levelId="UL" positionId="01"/>
	                       	Element partyRoleElement = new Element("PartyRole");
	                    	partyRoleElement.setAttribute(new Attribute("partyId", companyPartyId));
	                   	    partyRoleElement.setAttribute(new Attribute("roleTypeId", "_NA_"));
	               	 	    companyList.add(partyRoleElement);
	                       	// <FacilityLocation facilityId="GAADIZO_COMPANY" locationSeqId="TLTLTLLL02" locationTypeEnumId="FLT_PICKLOC" areaId="TL" aisleId="TL" sectionId="TL" levelId="LL" positionId="02"/>
	                       	Element partyRoleElement = new Element("PartyRole");
	                    	partyRoleElement.setAttribute(new Attribute("partyId", companyPartyId));
	                   	    partyRoleElement.setAttribute(new Attribute("roleTypeId", "_NA_"));
	               	 	    companyList.add(partyRoleElement);
	                       	// <FacilityLocation facilityId="GAADIZO_COMPANY" locationSeqId="TLTLTLUL02" locationTypeEnumId="FLT_BULK" areaId="TL" aisleId="TL" sectionId="TL" levelId="UL" positionId="02"/>
	                       	Element partyRoleElement = new Element("PartyRole");
	                    	partyRoleElement.setAttribute(new Attribute("partyId", companyPartyId));
	                   	    partyRoleElement.setAttribute(new Attribute("roleTypeId", "_NA_"));
	               	 	    companyList.add(partyRoleElement);
	                       	 //<FacilityLocation facilityId="GAADIZO_COMPANY" locationSeqId="TLTLTLLL03" locationTypeEnumId="FLT_PICKLOC" areaId="TL" aisleId="TL" sectionId="TL" levelId="LL" positionId="03"/>
	                       	Element partyRoleElement = new Element("PartyRole");
	                    	partyRoleElement.setAttribute(new Attribute("partyId", companyPartyId));
	                   	    partyRoleElement.setAttribute(new Attribute("roleTypeId", "_NA_"));
	               	 	    companyList.add(partyRoleElement);
	                       	// <FacilityLocation facilityId="GAADIZO_COMPANY" locationSeqId="TLTLTLUL03" locationTypeEnumId="FLT_BULK" areaId="TL" aisleId="TL" sectionId="TL" levelId="UL" positionId="03"/>
	                       	Element partyRoleElement = new Element("PartyRole");
	                    	partyRoleElement.setAttribute(new Attribute("partyId", companyPartyId));
	                   	    partyRoleElement.setAttribute(new Attribute("roleTypeId", "_NA_"));
	               	 	    companyList.add(partyRoleElement);
	                       	// <FacilityLocation facilityId="GAADIZO_COMPANY" locationSeqId="TLTLTLLL04" locationTypeEnumId="FLT_PICKLOC" areaId="TL" aisleId="TL" sectionId="TL" levelId="LL" positionId="04"/>
	                       	Element partyRoleElement = new Element("PartyRole");
	                    	partyRoleElement.setAttribute(new Attribute("partyId", companyPartyId));
	                   	    partyRoleElement.setAttribute(new Attribute("roleTypeId", "_NA_"));
	               	 	    companyList.add(partyRoleElement);
	                       	// <FacilityLocation facilityId="GAADIZO_COMPANY" locationSeqId="TLTLTLUL04" locationTypeEnumId="FLT_BULK" areaId="TL" aisleId="TL" sectionId="TL" levelId="UL" positionId="04"/>
	                       	Element partyRoleElement = new Element("PartyRole");
	                    	partyRoleElement.setAttribute(new Attribute("partyId", companyPartyId));
	                   	    partyRoleElement.setAttribute(new Attribute("roleTypeId", "_NA_"));
	               	 	    companyList.add(partyRoleElement);*/
	                       //=========================================================
	               	  	   Element productStoreElement = new Element("ProductStore");
	               	  productStoreElement.setAttribute(new Attribute("productStoreId",companyPartyId)); 
	               	  productStoreElement.setAttribute(new Attribute("storeName",(String) companInfoMapWithValue.get("COMPANY_NAME"))); 
	               	productStoreElement.setAttribute(new Attribute("companyName","Open For Business")); 
	               	productStoreElement.setAttribute(new Attribute("title","ERP System"));
	               	productStoreElement.setAttribute(new Attribute("subtitle", companInfoMapWithValue.get("COMPANY_NAME"))); 
	               	productStoreElement.setAttribute(new Attribute("payToPartyId",companyPartyId)); 
	               	productStoreElement.setAttribute(new Attribute("daysToCancelNonPay","30")); 
	               	productStoreElement.setAttribute(new Attribute("prorateShipping","Y")); 
	               	productStoreElement.setAttribute(new Attribute("prorateTaxes","Y"));
	               	productStoreElement.setAttribute(new Attribute("inventoryFacilityId", companyPartyId));
	               	productStoreElement.setAttribute(new Attribute("oneInventoryFacility","Y")); 
	               	productStoreElement.setAttribute(new Attribute("checkInventory","Y")); 
	               	productStoreElement.setAttribute(new Attribute("reserveInventory","Y"));
	                productStoreElement.setAttribute(new Attribute("balanceResOnOrderCreation","Y")); 
					productStoreElement.setAttribute(new Attribute("reserveOrderEnumId","INVRO_FIFO_REC")); 
					productStoreElement.setAttribute(new Attribute("requireInventory","N"));
					productStoreElement.setAttribute(new Attribute("defaultLocaleString","en_US")); 
					productStoreElement.setAttribute(new Attribute("defaultCurrencyUomId","INR"));
					productStoreElement.setAttribute(new Attribute("defaultSalesChannelEnumId","WEB_SALES_CHANNEL")); 
					productStoreElement.setAttribute(new Attribute("allowPassword","Y")); 
					productStoreElement.setAttribute(new Attribute("explodeOrderItems","N")); 
					productStoreElement.setAttribute(new Attribute("retryFailedAuths","Y")); 
					productStoreElement.setAttribute(new Attribute("reqReturnInventoryReceive","N"));
					productStoreElement.setAttribute(new Attribute("headerApprovedStatus","ORDER_APPROVED"));
					productStoreElement.setAttribute(new Attribute("itemApprovedStatus","ITEM_APPROVED"));
					productStoreElement.setAttribute(new Attribute("digitalItemApprovedStatus","ITEM_APPROVED"));
					productStoreElement.setAttribute(new Attribute("headerDeclinedStatus","ORDER_REJECTED"));
					productStoreElement.setAttribute(new Attribute("itemDeclinedStatus","ITEM_REJECTED"));
					productStoreElement.setAttribute(new Attribute("headerCancelStatus","ORDER_CANCELLED"));
					productStoreElement.setAttribute(new Attribute("itemCancelStatus","ITEM_CANCELLED"));
					productStoreElement.setAttribute(new Attribute("orderNumberPrefix","WS"));
					productStoreElement.setAttribute(new Attribute("authDeclinedMessage","There has been a problem with your method of payment. Please try a different method or call customer service."));
					productStoreElement.setAttribute(new Attribute("authFraudMessage","Your order has been rejected and your account has been disabled due to fraud."));
					productStoreElement.setAttribute(new Attribute("authErrorMessage","Problem connecting to payment processor; we will continue to retry and notify you by email."));
					productStoreElement.setAttribute(new Attribute("storeCreditValidDays","90"));
					productStoreElement.setAttribute(new Attribute("storeCreditAccountEnumId","FIN_ACCOUNT"));
					productStoreElement.setAttribute(new Attribute("visualThemeId","EC_DEFAULT"));
					productStoreElement.setAttribute(new Attribute("prodSearchExcludeVariants","Y"));
					productStoreElement.setAttribute(new Attribute("autoApproveInvoice","Y"));
					productStoreElement.setAttribute(new Attribute("shipIfCaptureFails","Y"));
					productStoreElement.setAttribute(new Attribute("autoApproveOrder","Y"));
					productStoreElement.setAttribute(new Attribute("showOutOfStockProducts","Y"));
					companyList.add(productStoreElement);
					
	                            }
	            			}
	            			
	            		}
	            	}
	            	contents.addAll(companyList);
	            	//===========================
	            	Element order = new Element("entity-engine-xml");
	        		order.addContent(contents);

	        		writeXMLToFile(order,uploadedFilepath);
	        		try
	        		{
	        		//if (!ServiceUtil.isError(prepareAndImportOrderXMLResult)) {
	    				Debug.logInfo("calling  service entityImportDirectory to import the xml", module);
	    				Map entityImportDirParams = UtilMisc.toMap("path", companyextractoutputpath, "userLogin", context.get("userLogin"));
	    System.out.println("====entityImportDirParams======"+entityImportDirParams+"==90909090==="+uploadedFilepath);
	    				Map entityImportDirectoryForERPResult = dispatcher.runSync("entityImportDirectoryForERP", entityImportDirParams);

	    				if (!ServiceUtil.isError(entityImportDirectoryForERPResult)) {
	    					moveXMLFilesFromDir(companyextractoutputpath, companyextractsuccessxmlpath);
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


		public static Map<String, Object> importCompanyAccountsInERPFromCsv(DispatchContext dctx, Map<String, ? extends Object> context) {
	        Delegator delegator = dctx.getDelegator();
	        Locale locale = (Locale) context.get("locale");
	        LocalDispatcher dispatcher = dctx.getDispatcher();
	        Map result = new HashMap<String, Object>();
	        orderExtractOutputPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("gaadizo.properties", "companyaccountextract-output-path"), context);
	        companyextractoutputpath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("gaadizo.properties", "companyaccountextract-output-path"), context);
	        String companyextractsuccessxmlpath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("gaadizo.properties", "companyaccountextract-success-xml-path"), context);
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
	                            	if(UtilValidate.isNotEmpty(row.getCell(0))){
	                            		companInfoMap.put("COMPANYID", row.getCell(0).getRichStringCellValue().toString());
	                            			companInfoMap.put("ACCOUNT_TYPE", row.getCell(1).getRichStringCellValue().toString());
	                                		companInfoMap.put("CONTACTNUMBER", row.getCell(2).getRichStringCellValue().toString());
	                                }
	                            	companInfoMapListMap.put(row.getCell(0).getRichStringCellValue().toString(), companInfoMap);
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
	            		if(UtilValidate.isNotEmpty(companyPartyId)){
	            			GenericValue partyExisting = EntityQuery.use(delegator).from("Party").where("partyId",companyPartyId).queryOne();
	            			if(UtilValidate.isNotEmpty(partyExisting)){
	            				System.out.println("==275====partyExisting========================"+partyExisting+"=======================");
	            				continue;
	            			} else {
	            				if(UtilValidate.isNotEmpty(companyPartyId)){
	            					System.out.println("==279====companyPartyId======================"+companyPartyId+"=======================");
	            					Map<String, String> companInfoMapWithValue = companInfoMapListMap.get(companyPartyId);
	                           	     Element partyElement = new Element("Party");
	                           	 	 partyElement.setAttribute(new Attribute("partyId", companyPartyId));
	                           	 	 partyElement.setAttribute(new Attribute("partyTypeId", "PARTY_GROUP"));
	                           	     partyElement.setAttribute(new Attribute("statusId", "PARTY_ENABLED"));
	                           	     partyElement.setAttribute(new Attribute("createdDate", UtilDateTime.nowTimestamp().toString()));
	                           	 	 partyElement.setAttribute(new Attribute("preferredCurrencyUomId", "INR"));
	                           	 	companyList.add(partyElement);
	                           	 Element partyGroupElement = new Element("PartyGroup");
	                     	 	   partyGroupElement.setAttribute(new Attribute("partyId", companyPartyId));
	                     	       partyGroupElement.setAttribute(new Attribute("groupName", companInfoMapWithValue.get("COMPANY_NAME")));
	                     	       if(UtilValidate.isNotEmpty(companInfoMapWithValue.get("LOGOIMAGEURL"))){
	                     	      partyGroupElement.setAttribute(new Attribute("logoImageUrl", companInfoMapWithValue.get("LOGOIMAGEURL")));
	                     	       }
	                  	 	       companyList.add(partyGroupElement);
	                           	 	
	                           	    Element partyRole1Element = new Element("PartyRole");
	                           	 partyRole1Element.setAttribute(new Attribute("partyId", companyPartyId));
	                           	partyRole1Element.setAttribute(new Attribute("roleTypeId", "_NA_"));
	                       	 	    companyList.add(partyRole1Element);
	                       	 	    
	                       	 	Element partyRole2Element = new Element("PartyRole");
	                       	 partyRole2Element.setAttribute(new Attribute("partyId", companyPartyId));
	                       	partyRole2Element.setAttribute(new Attribute("roleTypeId", "INTERNAL_ORGANIZATIO"));
	                   	 	    companyList.add(partyRole2Element);
	                   	 	Element partyRole3Element = new Element("PartyRole");
	                   	 partyRole3Element.setAttribute(new Attribute("partyId", companyPartyId));
	                   	partyRole3Element.setAttribute(new Attribute("roleTypeId", "BILL_FROM_VENDOR"));
	               	 	    companyList.add(partyRole3Element);
	               	 	Element partyRole4Element = new Element("PartyRole");
	               	 partyRole4Element.setAttribute(new Attribute("partyId", companyPartyId));
	               	partyRole4Element.setAttribute(new Attribute("roleTypeId", "BILL_TO_CUSTOMER"));
	           	 	    companyList.add(partyRole4Element);
	                       	 	   Element partyStatusElement = new Element("PartyStatus");
	                       	 	   partyStatusElement.setAttribute(new Attribute("partyId", companyPartyId));
	                       	 	   partyStatusElement.setAttribute(new Attribute("statusId", "PARTY_ENABLED"));
	                       	 	   partyStatusElement.setAttribute(new Attribute("statusDate", UtilDateTime.nowTimestamp().toString()));
	                       	 	   companyList.add(partyStatusElement);
	                    	 	   
	                    	 	   
	                    	 	   
	                       	 	   //======================================================================================================
	                       	 	Element facilityElement = new Element("Facility");
	                       	    facilityElement.setAttribute(new Attribute("facilityId", companyPartyId));
	                       	    facilityElement.setAttribute(new Attribute("facilityTypeId", "WAREHOUSE"));
	                       	    facilityElement.setAttribute(new Attribute("ownerPartyId", companyPartyId));
	                       	    facilityElement.setAttribute(new Attribute("defaultInventoryItemTypeId", "NON_SERIAL_INV_ITEM"));
	                       	    facilityElement.setAttribute(new Attribute("facilityName", "WAREHOUSE"));
	                	 	    companyList.add(facilityElement);
	                       	Element contactMechElement = new Element("ContactMech");
	                       	String contactMechId = "VA_"+companyPartyId;
	                       	contactMechElement.setAttribute(new Attribute("contactMechId", contactMechId));
	                       	contactMechElement.setAttribute(new Attribute("contactMechTypeId", "POSTAL_ADDRESS"));
	               	 	    companyList.add(contactMechElement);
	                       	 
	               	 	Element partyContactMechElement = new Element("PartyContactMech");
	               	 	partyContactMechElement.setAttribute(new Attribute("partyId", companyPartyId));
	               	 	partyContactMechElement.setAttribute(new Attribute("contactMechId", contactMechId));
	               	    partyContactMechElement.setAttribute(new Attribute("allowSolicitation", "Y"));
	               	 	partyContactMechElement.setAttribute(new Attribute("fromDate", UtilDateTime.nowTimestamp().toString()));
	           	 	    companyList.add(partyContactMechElement);
	           	 	    
	               	 	Element partyContactMechPurpose1Element = new Element("PartyContactMechPurpose");
	               	 partyContactMechPurpose1Element.setAttribute(new Attribute("partyId", companyPartyId));
	               	partyContactMechPurpose1Element.setAttribute(new Attribute("contactMechId", contactMechId));
	               	partyContactMechPurpose1Element.setAttribute(new Attribute("contactMechPurposeTypeId", "BILLING_LOCATION"));
	               	partyContactMechPurpose1Element.setAttribute(new Attribute("fromDate", UtilDateTime.nowTimestamp().toString()));
	           	 	    companyList.add(partyContactMechPurpose1Element);
	           	 	Element partyContactMechPurpose2Element = new Element("PartyContactMechPurpose");
	           	 partyContactMechPurpose2Element.setAttribute(new Attribute("partyId", companyPartyId));
	           	partyContactMechPurpose2Element.setAttribute(new Attribute("contactMechId", contactMechId));
	           	partyContactMechPurpose2Element.setAttribute(new Attribute("contactMechPurposeTypeId", "GENERAL_LOCATION"));
	           	partyContactMechPurpose2Element.setAttribute(new Attribute("fromDate", UtilDateTime.nowTimestamp().toString()));
	          	 	    companyList.add(partyContactMechPurpose2Element);
	          	 	 Element partyContactMechPurpose3Element = new Element("PartyContactMechPurpose");
	          	 	partyContactMechPurpose3Element.setAttribute(new Attribute("partyId", companyPartyId));
	          	 	partyContactMechPurpose3Element.setAttribute(new Attribute("contactMechId", contactMechId));
	          	 	partyContactMechPurpose3Element.setAttribute(new Attribute("contactMechPurposeTypeId", "PAYMENT_LOCATION"));
	          	 	partyContactMechPurpose3Element.setAttribute(new Attribute("fromDate", UtilDateTime.nowTimestamp().toString()));
	           	 	    companyList.add(partyContactMechPurpose3Element);
	               	 	    
	                       	Element postalAddressElement = new Element("PostalAddress");
	                       	postalAddressElement.setAttribute(new Attribute("contactMechId", contactMechId));
	                       	postalAddressElement.setAttribute(new Attribute("toName", companInfoMapWithValue.get("COMPANY_NAME")));
	                    	postalAddressElement.setAttribute(new Attribute("contactNumber", companInfoMapWithValue.get("CONTACTNUMBER")));
	                       	postalAddressElement.setAttribute(new Attribute("attnName", companInfoMapWithValue.get("COMPANY_NAME")));
	                       	postalAddressElement.setAttribute(new Attribute("address1", companInfoMapWithValue.get("ADDRESS1")));
	                       	if(UtilValidate.isNotEmpty(companInfoMapWithValue.get("ADDRESS2"))){
	                       	    postalAddressElement.setAttribute(new Attribute("address2", companInfoMapWithValue.get("ADDRESS2")));
	                       	}
	                       	postalAddressElement.setAttribute(new Attribute("city", companInfoMapWithValue.get("CITY")));
	                       	postalAddressElement.setAttribute(new Attribute("countryGeoId", "IND"));
	                       	if(UtilValidate.isNotEmpty(companInfoMapWithValue.get("COMPANY_NAME"))){
	                       		List<GenericValue> geoGVs = EntityQuery.use(delegator).from("Geo").where("geoTypeId", "STATE", "geoName", companInfoMapWithValue.get("STATE")).queryList();
	                       		if(UtilValidate.isNotEmpty(geoGVs)){
	                       			GenericValue geoGV = EntityUtil.getFirst(geoGVs);
	                       			postalAddressElement.setAttribute(new Attribute("stateProvinceGeoId", geoGV.get("geoId").toString()));
	                       		}
	                       	}
	               	 	    companyList.add(postalAddressElement);
	               	 	    
	                       	Element facilityContactMechElement = new Element("FacilityContactMech");
	                       	facilityContactMechElement.setAttribute(new Attribute("facilityId", companyPartyId));
	                       	facilityContactMechElement.setAttribute(new Attribute("contactMechId", contactMechId));
	                       	facilityContactMechElement.setAttribute(new Attribute("fromDate", UtilDateTime.nowTimestamp().toString()));
	               	 	    companyList.add(facilityContactMechElement);
	               	 	    
	                       	Element facilityContactMechPurposeElement = new Element("FacilityContactMechPurpose");
	                       	facilityContactMechPurposeElement.setAttribute(new Attribute("facilityId", companyPartyId));
	                       	facilityContactMechPurposeElement.setAttribute(new Attribute("contactMechId", contactMechId));
	                       	facilityContactMechPurposeElement.setAttribute(new Attribute("contactMechPurposeTypeId", "SHIPPING_LOCATION"));
	                       	facilityContactMechPurposeElement.setAttribute(new Attribute("fromDate", UtilDateTime.nowTimestamp().toString()));
	               	 	    companyList.add(facilityContactMechPurposeElement);
	               	 	    Element facilityContactMechPurposeEElement = new Element("FacilityContactMechPurpose");
	               	  		facilityContactMechPurposeEElement.setAttribute(new Attribute("facilityId", companyPartyId));
	               	  		facilityContactMechPurposeEElement.setAttribute(new Attribute("contactMechId", contactMechId));
	               	  		facilityContactMechPurposeEElement.setAttribute(new Attribute("contactMechPurposeTypeId", "SHIP_ORIG_LOCATION"));
	               	  		facilityContactMechPurposeEElement.setAttribute(new Attribute("fromDate", UtilDateTime.nowTimestamp().toString()));
	               	  		companyList.add(facilityContactMechPurposeEElement);
	               	  		
	               	  	Element SequenceValueItemEElement = new Element("SequenceValueItem");
	               	  	SequenceValueItemEElement.setAttribute(new Attribute("ownerId", companyPartyId));
	               	  	SequenceValueItemEElement.setAttribute(new Attribute("prefix", companInfoMapWithValue.get("INVOICE_PREFIX")));
	               	  	SequenceValueItemEElement.setAttribute(new Attribute("prefixType", "INVOICE"));//Todo
	               	  	SequenceValueItemEElement.setAttribute(new Attribute("seqId", companInfoMapWithValue.get("INVOICE_SEQ")));
	               	  	SequenceValueItemEElement.setAttribute(new Attribute("seqName","INVOICE"+companyPartyId));
	           	  		companyList.add(SequenceValueItemEElement);
	           	  		
	           	  	Element SequenceValueItemJCElement = new Element("SequenceValueItem");
	           	  	SequenceValueItemJCElement.setAttribute(new Attribute("ownerId", companyPartyId));
	           	  	SequenceValueItemJCElement.setAttribute(new Attribute("prefix", companInfoMapWithValue.get("JOBCARD_PREFIX")));
	           	  	SequenceValueItemJCElement.setAttribute(new Attribute("prefixType", "JOBCARD"));
	           	  	SequenceValueItemJCElement.setAttribute(new Attribute("seqId", companInfoMapWithValue.get("JOBCARD_SEQ")));//Todo
	           	  	SequenceValueItemJCElement.setAttribute(new Attribute("seqName", "JOBCARD"+companyPartyId));
	       	  		companyList.add(SequenceValueItemJCElement);
	       	  		
	       	  	Element SequenceValueItemORDERElement = new Element("SequenceValueItem");
	       	  	SequenceValueItemORDERElement.setAttribute(new Attribute("ownerId", companyPartyId));
	       	  	SequenceValueItemORDERElement.setAttribute(new Attribute("prefix", companInfoMapWithValue.get("INVOICE_PREFIX")));
	       	  	SequenceValueItemORDERElement.setAttribute(new Attribute("prefixType", "ORDER"));
	       	  	SequenceValueItemORDERElement.setAttribute(new Attribute("seqId", companInfoMapWithValue.get("INVOICE_SEQ")));//Todo
	       	  	SequenceValueItemORDERElement.setAttribute(new Attribute("seqName", "ORDER"+companyPartyId));
	      	  		companyList.add(SequenceValueItemORDERElement);
	       	  		
	      	  	Element SequenceValueItemPARTYElement = new Element("SequenceValueItem");
	       	  	SequenceValueItemPARTYElement.setAttribute(new Attribute("ownerId", companyPartyId));
	       	  	SequenceValueItemPARTYElement.setAttribute(new Attribute("prefix", companInfoMapWithValue.get("JOBCARD_PREFIX")));
	       	  	SequenceValueItemPARTYElement.setAttribute(new Attribute("prefixType", "PARTY"));
	       	  	SequenceValueItemPARTYElement.setAttribute(new Attribute("seqId", companInfoMapWithValue.get("INVOICE_SEQ")));//Todo
	       	  	SequenceValueItemPARTYElement.setAttribute(new Attribute("seqName", "PARTY"+companyPartyId));
	      	  		companyList.add(SequenceValueItemPARTYElement);
	      	  		
	      	  
	      	   
	       	  		
	                       	 
	                       //=========================================================
	               	  	   Element productStoreElement = new Element("ProductStore");
	               	  productStoreElement.setAttribute(new Attribute("productStoreId",companyPartyId)); 
	               	  productStoreElement.setAttribute(new Attribute("storeName",(String) companInfoMapWithValue.get("COMPANY_NAME"))); 
	               	productStoreElement.setAttribute(new Attribute("companyName","Open For Business")); 
	               	productStoreElement.setAttribute(new Attribute("title","ERP System"));
	               	productStoreElement.setAttribute(new Attribute("subtitle", companInfoMapWithValue.get("COMPANY_NAME"))); 
	               	productStoreElement.setAttribute(new Attribute("payToPartyId",companyPartyId)); 
	               	productStoreElement.setAttribute(new Attribute("daysToCancelNonPay","30")); 
	               	productStoreElement.setAttribute(new Attribute("prorateShipping","Y")); 
	               	productStoreElement.setAttribute(new Attribute("prorateTaxes","Y"));
	               	productStoreElement.setAttribute(new Attribute("inventoryFacilityId", companyPartyId));
	               	productStoreElement.setAttribute(new Attribute("oneInventoryFacility","Y")); 
	               	productStoreElement.setAttribute(new Attribute("checkInventory","Y")); 
	               	productStoreElement.setAttribute(new Attribute("reserveInventory","Y"));
	                productStoreElement.setAttribute(new Attribute("balanceResOnOrderCreation","Y")); 
					productStoreElement.setAttribute(new Attribute("reserveOrderEnumId","INVRO_FIFO_REC")); 
					productStoreElement.setAttribute(new Attribute("requireInventory","N"));
					productStoreElement.setAttribute(new Attribute("defaultLocaleString","en_US")); 
					productStoreElement.setAttribute(new Attribute("defaultCurrencyUomId","INR"));
					productStoreElement.setAttribute(new Attribute("defaultSalesChannelEnumId","WEB_SALES_CHANNEL")); 
					productStoreElement.setAttribute(new Attribute("allowPassword","Y")); 
					productStoreElement.setAttribute(new Attribute("explodeOrderItems","N")); 
					productStoreElement.setAttribute(new Attribute("retryFailedAuths","Y")); 
					productStoreElement.setAttribute(new Attribute("reqReturnInventoryReceive","N"));
					productStoreElement.setAttribute(new Attribute("headerApprovedStatus","ORDER_APPROVED"));
					productStoreElement.setAttribute(new Attribute("itemApprovedStatus","ITEM_APPROVED"));
					productStoreElement.setAttribute(new Attribute("digitalItemApprovedStatus","ITEM_APPROVED"));
					productStoreElement.setAttribute(new Attribute("headerDeclinedStatus","ORDER_REJECTED"));
					productStoreElement.setAttribute(new Attribute("itemDeclinedStatus","ITEM_REJECTED"));
					productStoreElement.setAttribute(new Attribute("headerCancelStatus","ORDER_CANCELLED"));
					productStoreElement.setAttribute(new Attribute("itemCancelStatus","ITEM_CANCELLED"));
					productStoreElement.setAttribute(new Attribute("orderNumberPrefix","WS"));
					productStoreElement.setAttribute(new Attribute("authDeclinedMessage","There has been a problem with your method of payment. Please try a different method or call customer service."));
					productStoreElement.setAttribute(new Attribute("authFraudMessage","Your order has been rejected and your account has been disabled due to fraud."));
					productStoreElement.setAttribute(new Attribute("authErrorMessage","Problem connecting to payment processor; we will continue to retry and notify you by email."));
					productStoreElement.setAttribute(new Attribute("storeCreditValidDays","90"));
					productStoreElement.setAttribute(new Attribute("storeCreditAccountEnumId","FIN_ACCOUNT"));
					productStoreElement.setAttribute(new Attribute("visualThemeId","EC_DEFAULT"));
					productStoreElement.setAttribute(new Attribute("prodSearchExcludeVariants","Y"));
					productStoreElement.setAttribute(new Attribute("autoApproveInvoice","Y"));
					productStoreElement.setAttribute(new Attribute("shipIfCaptureFails","Y"));
					productStoreElement.setAttribute(new Attribute("autoApproveOrder","Y"));
					productStoreElement.setAttribute(new Attribute("showOutOfStockProducts","Y"));
					companyList.add(productStoreElement);
					
	                            }
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
	    				Map entityImportDirParams = UtilMisc.toMap("path", companyextractoutputpath, "userLogin", context.get("userLogin"));
	    System.out.println("====entityImportDirParams======"+entityImportDirParams+"==90909090==="+uploadedFilepath);
	    				Map entityImportDirectoryForERPResult = dispatcher.runSync("entityImportDirectoryForERP", entityImportDirParams);

	    				if (!ServiceUtil.isError(entityImportDirectoryForERPResult)) {
	    					moveXMLFilesFromDir(companyextractoutputpath, companyextractsuccessxmlpath);
	    					Debug.logInfo("moved XMLFilesFromDir successfully", module);
	    				}
	    				
	    		} catch (Exception ex) {
	    			//errorDuringInsertion = true;
	    			//errorMsgs.add(ex.getMessage());
	    			Debug.logError(ex.getMessage(), module);
	    			//throw ex;
	    		}
	            	
	        		
	        		
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
}
