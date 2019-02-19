package com.ofbiz.importinterface.services.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import com.ofbiz.importinterface.constants.CategoryXML;
import com.ofbiz.importinterface.exception.CategoryImportException;
import com.ofbiz.utility.DateTimeUtility;


public class ImportServices {
	public static final String module = ImportServices.class.getName();

	public static Map<String, Object> entityImportDirectoryForERP(DispatchContext dctx, Map<String, ? extends Object> context) {
		Debug.logInfo("Method entityImportDirectoryForERP starts ", module);
		GenericValue userLogin = (GenericValue) context.get("userLogin");
        LocalDispatcher dispatcher = dctx.getDispatcher();

        List<String> messages = new ArrayList<String>();

        String path = (String) context.get("path");
        String mostlyInserts = (String) context.get("mostlyInserts");
        String maintainTimeStamps = (String) context.get("maintainTimeStamps");
        String createDummyFks = (String) context.get("createDummyFks");
        boolean deleteFiles = (String) context.get("deleteFiles") != null;
        String checkDataOnly = (String) context.get("checkDataOnly");

        Integer txTimeout = (Integer)context.get("txTimeout");
        Long filePause = (Long)context.get("filePause");

        if (txTimeout == null) {
            txTimeout = Integer.valueOf(7200);
        }
        if (filePause == null) {
            filePause = Long.valueOf(0);
        }

        if (UtilValidate.isNotEmpty(path)) {
            long pauseLong = filePause != null ? filePause.longValue() : 0;
            File baseDir = new File(path);

            if (baseDir.isDirectory() && baseDir.canRead()) {
                File[] fileArray = baseDir.listFiles();
                List<File> files = new ArrayList<File>();
                for (File file: fileArray) {
                    if (file.getName().toUpperCase().endsWith("XML")) {
                        files.add(file);
                    }
                }

                int passes=0;
                int initialListSize = files.size();
                int lastUnprocessedFilesCount = 0;
                List<File> unprocessedFiles = new ArrayList<File>();
                while (files.size()>0 &&
                        files.size() != lastUnprocessedFilesCount) {
                    lastUnprocessedFilesCount = files.size();
                    unprocessedFiles = new ArrayList<File>();
                    for (File f: files) {
                        Map<String, Object> parseEntityXmlFileArgs = UtilMisc.toMap("mostlyInserts", mostlyInserts,
                                "createDummyFks", createDummyFks,
                                "checkDataOnly", checkDataOnly,
                                "maintainTimeStamps", maintainTimeStamps,
                                "txTimeout", txTimeout,
                                "userLogin", userLogin);

                        try {
                            URL furl = f.toURI().toURL();
                            parseEntityXmlFileArgs.put("url", furl);
                            Map<String, Object> outputMap = dispatcher.runSync("parseEntityXmlFileForERP", parseEntityXmlFileArgs);
                            Long numberRead = (Long) outputMap.get("rowProcessed");
                            messages.add("Got " + numberRead.longValue() + " entities from " + f);
                            if (deleteFiles) {
                                messages.add("Deleting " + f);
                                f.delete();
                            }
                        } catch (Exception e) {
                            unprocessedFiles.add(f);
                            messages.add("Failed " + f + " adding to retry list for next pass");
                        }
                        // pause in between files
                        if (pauseLong > 0) {
                            Debug.logInfo("Pausing for [" + pauseLong + "] seconds - " + UtilDateTime.nowTimestamp(), module);
                            try {
                                Thread.sleep((pauseLong * 1000));
                            } catch (InterruptedException ie) {
                                Debug.logInfo("Pause finished - " + UtilDateTime.nowTimestamp(), module);
                            }
                        }
                    }
                    files = unprocessedFiles;
                    passes++;
                    messages.add("Pass " + passes + " complete");
                    Debug.logInfo("Pass " + passes + " complete", module);
                }
                lastUnprocessedFilesCount=unprocessedFiles.size();
                messages.add("---------------------------------------");
                messages.add("Succeeded: " + (initialListSize-lastUnprocessedFilesCount) + " of " + initialListSize);
                messages.add("Failed:    " + lastUnprocessedFilesCount + " of " + initialListSize);
                messages.add("---------------------------------------");
                messages.add("Failed Files:");
                if(lastUnprocessedFilesCount == 0) 
                {
                    messages.add("SUCCESS");
                } 
                else 
                {
                    messages.add("ERROR");
                }
                for (File file: unprocessedFiles) {
                    messages.add(file.toString());
                }
            } else {
                messages.add("path not found or can't be read");
            }
        } else {
            messages.add("No path specified, doing nothing.");
        }
        // send the notification
        Map<String, Object> resp = UtilMisc.toMap("messages", (Object) messages);
        Debug.logInfo("Method entityImportDirectoryForERP ends ", module);
        return resp;
    }

  
    public static Map<String, Object> parseEntityXmlFileForERP(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();

        URL url = (URL) context.get("url");
        String xmltext = (String) context.get("xmltext");

        if (url == null && xmltext == null) {
            return ServiceUtil.returnError("No entity xml file or text specified");
        }
        boolean mostlyInserts = (String) context.get("mostlyInserts") != null;
        boolean maintainTimeStamps = (String) context.get("maintainTimeStamps") != null;
        boolean createDummyFks = (String) context.get("createDummyFks") != null;
        boolean checkDataOnly = (String) context.get("checkDataOnly") != null;
        Integer txTimeout = (Integer) context.get("txTimeout");

        if (txTimeout == null) {
            txTimeout = Integer.valueOf(7200);
        }

        long rowProcessed = 0;
        try {
            EntitySaxReader reader = new EntitySaxReader(delegator);
            reader.setUseTryInsertMethod(mostlyInserts);
            reader.setMaintainTxStamps(maintainTimeStamps);
            reader.setTransactionTimeout(txTimeout.intValue());
            reader.setCreateDummyFks(createDummyFks);
            reader.setCheckDataOnly(checkDataOnly);

            long numberRead = (url != null ? reader.parse(url) : reader.parse(xmltext));
            rowProcessed = numberRead;
        } catch (Exception ex) {
            return ServiceUtil.returnError("Error parsing entity xml file: " + ex.toString());
        }
        // send the notification
        Map<String, Object> resp = UtilMisc.<String, Object>toMap("rowProcessed", rowProcessed);
        return resp;
    }

    /** Performs an entity maintenance security check. Returns hasPermission=true
     * if the user has the ENTITY_MAINT permission.
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> entityMaintPermCheckForVishal(DispatchContext dctx, Map<String, ? extends Object> context) {
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
Delegator delegator = dctx.getDelegator();
if(UtilValidate.isEmpty(userLogin)) {
                try {
                    
                    userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "admin"),false);
                }
                catch(Exception exu)
                {
                    Debug.log("======exu===========error in getting userlogin=========================");
                }
            }
        Security security = dctx.getSecurity();
        Map<String, Object> resultMap = null;
        if (security.hasPermission("ENTITY_MAINT", userLogin)) {
            resultMap = ServiceUtil.returnSuccess();
            resultMap.put("hasPermission", true);
        } else {
            resultMap = ServiceUtil.returnFailure(UtilProperties.getMessage("WebtoolsUiLabels", "WebtoolsPermissionError", locale));
            resultMap.put("hasPermission", false);
        }
        return resultMap;
    }
}
