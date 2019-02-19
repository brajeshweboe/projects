package com.ofbiz.importinterface.services.order;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityUtilProperties;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import com.google.gson.Gson;

public class ImportOrderServicesJson {
	public static final String module = ImportOrderServicesJson.class.getName();

	
	//service 2 that calling from main service
    public static Map<String, Object> importOrderInERPFromRestApi(DispatchContext dctx, Map<String, Object> context) throws Exception {
    	Debug.logInfo("start service importOrderInERPFromRestApi", module);
    	Map<String, Object> result = ServiceUtil.returnSuccess();
        String orderBookingJsonFromCRM = (String)context.get("orderBookingJsonFromCRM");
        System.out.println("=========orderBookingJsonFromCRM============="+orderBookingJsonFromCRM+"===============");
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {

			/*String json = "{\n    \"status\": 201,\n    \"msg\": \"success\",\n    \"data\": {\n        \"booking_list\": [\n            {\n                    \"booking_detail\": {\n                        \"Order_ID\": 5635,\n                        \"Booking_ID\": \"GDZAA5789\",\n                        \"Booking_Date\": \"2018-10-18\",\n                        \"Service_Date\": \"24 Oct 2018\",\n                        \"Service_Time\": \"11:00 AM\",\n                        \"Payable_Amount\": 3724,\n                        \"Invoice_Amount\": 3724,\n                        \"Order_Amount\": 3724,\n                        \"Discount_Amount\": 0,\n                        \"Offer_Code\": 0,\n                        \"Offer_Amount\": 3724,\n                        \"Offer_Detail\": 3724,\n                        \"Oil_Type\": \"Semi Synthetic Oil\",\n                        \"Status\": \"Service Started\",\n                        \"Gaadizo_Creadit\": 0,\n                        \"Prepaid_Amount\": 0,\n                        \"Payment_Mode\": \"Cash On Delivery\"\n                    },\n                    \"customer_detail\": {\n                        \"Customer_ID\": 4773,\n                        \"First_Name\": \"naresh\",\n                        \"Last_Name\": \"\",\n                        \"Cust_Email\": \"naresh@gmail.com\",\n                        \"Address_1\": \"\",\n                        \"Address_2\": \"\",\n                        \"City\": \"\",\n                        \"State_Code\": \"\"\n                    },\n                    \"vehicle_detail\": {\n                        \"Vehicle_Make\": \"Ford\",\n                        \"Vehicle_Make_ID\": 18,\n                        \"Vehicle_Model\": \"Eco Sport Diesel\",\n                        \"Vehicle_Model_ID\": 385,\n                        \"Vehicle_Regstn_No\": null,\n                        \"Year\": null\n                    },\n                    \"service_detail\": {\n                        \"Service_ID\": \"394\",\n                        \"Service_Provider_ID\": \"GAADIZO_FAC\",\n                        \"Service_Provider\": \"demo Staging station\",\n                        \"Service_Availed\": [\n                            \"Standard Service (Semi Synthetic Oil)\"\n                        ]\n                    },\n                    \"miscellaneous\": {\n                        \"Gaadizo_Comments\": \"\"\n                    }\n            },\n\t\t\t{\n                    \"booking_detail\": {\n                        \"Order_ID\": 5635,\n                        \"Booking_ID\": \"GDZAA5790\",\n                        \"Booking_Date\": \"2018-10-18\",\n                        \"Service_Date\": \"24 Oct 2018\",\n                        \"Service_Time\": \"11:00 AM\",\n                        \"Payable_Amount\": 3724,\n                        \"Invoice_Amount\": 3724,\n                        \"Order_Amount\": 3724,\n                        \"Discount_Amount\": 0,\n                        \"Offer_Code\": 0,\n                        \"Offer_Amount\": 3724,\n                        \"Offer_Detail\": 3724,\n                        \"Oil_Type\": \"Semi Synthetic Oil\",\n                        \"Status\": \"Service Started\",\n                        \"Gaadizo_Creadit\": 0,\n                        \"Prepaid_Amount\": 0,\n                        \"Payment_Mode\": \"Cash On Delivery\"\n                    },\n                    \"customer_detail\": {\n                        \"Customer_ID\": 4773,\n                        \"First_Name\": \"naresh\",\n                        \"Last_Name\": \"\",\n                        \"Cust_Email\": \"naresh@gmail.com\",\n                        \"Address_1\": \"\",\n                        \"Address_2\": \"\",\n                        \"City\": \"\",\n                        \"State_Code\": \"\"\n                    },\n                    \"vehicle_detail\": {\n                        \"Vehicle_Make\": \"Ford\",\n                        \"Vehicle_Make_ID\": 18,\n                        \"Vehicle_Model\": \"Eco Sport Diesel\",\n                        \"Vehicle_Model_ID\": 386,\n                        \"Vehicle_Regstn_No\": null,\n                        \"Year\": null\n                    },\n                    \"service_detail\": {\n                        \"Service_ID\": \"395\",\n                        \"Service_Provider_ID\": \"GAADIZO_FAC\",\n                        \"Service_Provider\": \"demo Staging station\",\n                        \"Service_Availed\": [\n                            \"Standard Service (Semi Synthetic Oil)\"\n                        ]\n                    },\n                    \"miscellaneous\": {\n                        \"Gaadizo_Comments\": \"\"\n                    }\n            }\n        ],\n        \"booking_id_list\": [\n            \"GDZAA5789\",\n\t\t\t\"GDZAA5790\"\n        ]\n    }\n}";
	    	// Now do the magic.
			BookingOrderPojo data = new Gson().fromJson(json, BookingOrderPojo.class);
			*/
			if(orderBookingJsonFromCRM!=null){
				BookingOrderPojo bookingOrderPojodata = new Gson().fromJson(orderBookingJsonFromCRM, BookingOrderPojo.class);
				// Show it.
				Debug.logInfo(" Msg : "+bookingOrderPojodata.getMsg()+" Status : "+bookingOrderPojodata.getStatus()+" Data : "+bookingOrderPojodata.getData(), module);
				Map<String, Object> createResp;
				try {
					Map<String, Object> createCtx = new HashMap<String, Object>();
					createCtx.put("bookingOrderPojoData", bookingOrderPojodata);
					createCtx.put("userLogin", userLogin);
					createResp = dispatcher.runSync("importOrderInERPFromJson", createCtx);
				} catch (GenericServiceException e) {
					Debug.logError(e, module);
					return ServiceUtil.returnError(e.getMessage());
				}
			}
	        
		} catch (Exception ex) {
			Debug.logError(ex.getMessage(), module);
			throw ex;
		}
		Debug.logInfo(" Method insertIntoDB ends", module);
		
        return result;
    }
    
    public static Map<String, Object> importBookingListInERPFromRestApi(DispatchContext dctx, Map<String, Object> context) throws Exception {
    	Debug.logInfo("start service importBookingListInERPFromRestApi", module);
    	Map<String, Object> result = ServiceUtil.returnSuccess();
    	LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        
        System.out.println("=========in side of============importBookingListInERPFromRestApi=============");
        try {
        	//getting value for rest
        	String restUrl = EntityUtilProperties.getPropertyValue("gaadizo", "restUrl", "http://13.127.206.24:8000/", delegator);
        	String restUserName = EntityUtilProperties.getPropertyValue("gaadizo", "restUsername", "Erp", delegator);
        	String restPassword = EntityUtilProperties.getPropertyValue("gaadizo", "restPassword", "Team", delegator);
        	String restKey = EntityUtilProperties.getPropertyValue("gaadizo", "restKey", "be5ca743900f009fd4bc817fbc00c", delegator);
			URL url = new URL(restUrl+"booking_start_list");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("key", restKey);

			String input = "{\"username\":\""+restUserName+"\",\"password\":\""+restPassword+"\"}";
			System.out.println("==restKey==="+restKey+"===input=========="+input+"===conn======"+conn+"====");
			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();
    
	        System.out.println(conn.getResponseCode());

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			String output1="";
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
			output1 = output1+output;
				System.out.println(output);
			}
			/*String json = "{\r\n    \"status\": 201,\r\n    \"msg\": \"success\",\r\n    \"data\": {\r\n        \"booking_list\": [\r\n            {\r\n                    \"booking_detail\": {\r\n                        \"Order_ID\": Test_5639,\r\n                        \"Booking_ID\": \"Test_GDZAA5791\",\r\n                        \"Booking_Date\": \"2018-10-18\",\r\n                        \"Service_Date\": \"24 Oct 2018\",\r\n                        \"Service_Time\": \"11:00 AM\",\r\n                        \"Payable_Amount\": 3724,\r\n                        \"Invoice_Amount\": 3724,\r\n                        \"Order_Amount\": 3724,\r\n                        \"Discount_Amount\": 0,\r\n                        \"Offer_Code\": 0,\r\n                        \"Offer_Amount\": 3724,\r\n                        \"Offer_Detail\": 3724,\r\n                        \"Oil_Type\": \"Semi Synthetic Oil\",\r\n                        \"Status\": \"Service Started\",\r\n                        \"Gaadizo_Creadit\": 0,\r\n                        \"Prepaid_Amount\": 0,\r\n                        \"Payment_Mode\": \"Cash On Delivery\"\r\n                    },\r\n                    \"customer_detail\": {\r\n                        \"Customer_ID\": Test_4773,\r\n                        \"First_Name\": \"naresh\",\r\n                        \"Last_Name\": \"\",\r\n                        \"Cust_Email\": \"naresh@gmail.com\",\r\n                        \"Address_1\": \"\",\r\n                        \"Address_2\": \"\",\r\n                        \"City\": \"\",\r\n                        \"State_Code\": \"\"\r\n                    },\r\n                    \"vehicle_detail\": {\r\n                        \"Vehicle_Make\": \"Ford\",\r\n                        \"Vehicle_Make_ID\": 18,\r\n                        \"Vehicle_Model\": \"Eco Sport Diesel\",\r\n                        \"Vehicle_Model_ID\": 385,\r\n                        \"Vehicle_Regstn_No\": null,\r\n                        \"Year\": null\r\n                    },\r\n                    \"service_detail\": {\r\n                        \"Service_ID\": \"394\",\r\n                        \"Service_Provider_ID\": \"GAADIZO_FAC\",\r\n                        \"Service_Provider\": \"demo Staging station\",\r\n                                                                                                                                                                                    \"Service_Availed\": [\r\n                            \"Standard Service (Semi Synthetic Oil)\"\r\n                        ]\r\n                    },\r\n                    \"miscellaneous\": {\r\n                        \"Gaadizo_Comments\": \"\"\r\n                    }\r\n            },\r\n                        {\r\n                    \"booking_detail\": {\r\n                        \"Order_ID\": Test_5640,\r\n                        \"Booking_ID\": \"Test_GDZAA5792\",\r\n                        \"Booking_Date\": \"2018-10-18\",\r\n                        \"Service_Date\": \"24 Oct 2018\",\r\n                        \"Service_Time\": \"11:00 AM\",\r\n                        \"Payable_Amount\": 3724,\r\n                        \"Invoice_Amount\": 3724,\r\n                        \"Order_Amount\": 3724,\r\n                        \"Discount_Amount\": 0,\r\n                        \"Offer_Code\": 0,\r\n                        \"Offer_Amount\": 3724,\r\n                        \"Offer_Detail\": 3724,\r\n                        \"Oil_Type\": \"Semi Synthetic Oil\",\r\n                        \"Status\": \"Service Started\",\r\n                        \"Gaadizo_Creadit\": 0,\r\n                        \"Prepaid_Amount\": 0,\r\n                        \"Payment_Mode\": \"Cash On Delivery\"\r\n                    },\r\n                    \"customer_detail\": {\r\n                        \"Customer_ID\": Test_4775,\r\n                        \"First_Name\": \"naresh\",\r\n                        \"Last_Name\": \"\",\r\n                        \"Cust_Email\": \"naresh@gmail.com\",\r\n                        \"Address_1\": \"\",\r\n                        \"Address_2\": \"\",\r\n                        \"City\": \"\",\r\n                        \"State_Code\": \"\"\r\n                    },\r\n                    \"vehicle_detail\": {\r\n                        \"Vehicle_Make\": \"Ford\",\r\n                        \"Vehicle_Make_ID\": 18,\r\n                        \"Vehicle_Model\": \"Eco Sport Diesel\",\r\n                        \"Vehicle_Model_ID\": 386,\r\n                        \"Vehicle_Regstn_No\": null,\r\n                        \"Year\": null\r\n                    },\r\n                    \"service_detail\": {\r\n                        \"Service_ID\": \"395\",\r\n                        \"Service_Provider_ID\": \"GAADIZO_FAC\",\r\n                        \"Service_Provider\": \"demo Staging station\",\r\n                        \"Service_Availed\": [\r\n                            \"Standard Service (Semi Synthetic Oil)\"\r\n                        ]\r\n                    },\r\n                    \"miscellaneous\": {\r\n                        \"Gaadizo_Comments\": \"\"\r\n                    }\r\n            }\r\n        ],\r\n        \"booking_id_list\": [\r\n            \"GDZAA5789\",\r\n                        \"GDZAA5790\"\r\n        ]\r\n    }\r\n}";
			*/
			/*String json = "{\r\n    \"status\": 201,\r\n    \"msg\": \"success\",\r\n    \"data\": {\r\n        \"booking_list\": [\r\n            {\r\n                    \"booking_detail\": {\r\n                        \"Order_ID\": Test_5650,\r\n                        \"Booking_ID\": \"Test_GDZAA5800\",\r\n                        \"Booking_Date\": \"2018-10-18\",\r\n                        \"Service_Date\": \"24 Oct 2018\",\r\n                        \"Service_Time\": \"11:00 AM\",\r\n                        \"Payable_Amount\": 3724,\r\n                        \"Invoice_Amount\": 3724,\r\n                        \"Order_Amount\": 3724,\r\n                        \"Discount_Amount\": 0,\r\n                        \"Offer_Code\": 0,\r\n                        \"Offer_Amount\": 3724,\r\n                        \"Offer_Detail\": 3724,\r\n                        \"Oil_Type\": \"Semi Synthetic Oil\",\r\n                        \"Status\": \"Service Started\",\r\n                        \"Gaadizo_Creadit\": 0,\r\n                        \"Prepaid_Amount\": 0,\r\n                        \"Payment_Mode\": \"Cash On Delivery\"\r\n                    },\r\n                    \"customer_detail\": {\r\n                        \"Customer_ID\": Test_4800,\r\n                        \"First_Name\": \"naresh\",\r\n                        \"Last_Name\": \"\",\r\n                        \"Cust_Email\": \"naresh@gmail.com\",\r\n                        \"Address_1\": \"\",\r\n                        \"Address_2\": \"\",\r\n                        \"City\": \"\",\r\n                        \"State_Code\": \"\"\r\n                    },\r\n                    \"vehicle_detail\": {\r\n                        \"Vehicle_Make\": \"Ford\",\r\n                        \"Vehicle_Make_ID\": 18,\r\n                        \"Vehicle_Model\": \"Eco Sport Diesel\",\r\n                        \"Vehicle_Model_ID\": 385,\r\n                        \"Vehicle_Regstn_No\": null,\r\n                        \"Year\": null\r\n                    },\r\n                    \"service_detail\": {\r\n                        \"Service_ID\": \"PM_ES1\",\r\n                        \"Service_Provider_ID\": \"GAADIZO_FAC\",\r\n                        \"Service_Provider\": \"demo Staging station\",\r\n                                                                                                                                                                                    \"Service_Availed\": [\r\n                            \"Standard Service (Semi Synthetic Oil)\"\r\n                        ]\r\n                    },\r\n                    \"miscellaneous\": {\r\n                        \"Gaadizo_Comments\": \"\"\r\n                    }\r\n            },\r\n                        {\r\n                    \"booking_detail\": {\r\n                        \"Order_ID\": Test_5651,\r\n                        \"Booking_ID\": \"Test_GDZAA5801\",\r\n                        \"Booking_Date\": \"2018-10-18\",\r\n                        \"Service_Date\": \"24 Oct 2018\",\r\n                        \"Service_Time\": \"11:00 AM\",\r\n                        \"Payable_Amount\": 3724,\r\n                        \"Invoice_Amount\": 3724,\r\n                        \"Order_Amount\": 3724,\r\n                        \"Discount_Amount\": 0,\r\n                        \"Offer_Code\": 0,\r\n                        \"Offer_Amount\": 3724,\r\n                        \"Offer_Detail\": 3724,\r\n                        \"Oil_Type\": \"Semi Synthetic Oil\",\r\n                        \"Status\": \"Service Started\",\r\n                        \"Gaadizo_Creadit\": 0,\r\n                        \"Prepaid_Amount\": 0,\r\n                        \"Payment_Mode\": \"Cash On Delivery\"\r\n                    },\r\n                    \"customer_detail\": {\r\n                        \"Customer_ID\": Test_4801,\r\n                        \"First_Name\": \"naresh\",\r\n                        \"Last_Name\": \"\",\r\n                        \"Cust_Email\": \"naresh@gmail.com\",\r\n                        \"Address_1\": \"\",\r\n                        \"Address_2\": \"\",\r\n                        \"City\": \"\",\r\n                        \"State_Code\": \"\"\r\n                    },\r\n                    \"vehicle_detail\": {\r\n                        \"Vehicle_Make\": \"Ford\",\r\n                        \"Vehicle_Make_ID\": 18,\r\n                        \"Vehicle_Model\": \"Eco Sport Diesel\",\r\n                        \"Vehicle_Model_ID\": 386,\r\n                        \"Vehicle_Regstn_No\": null,\r\n                        \"Year\": null\r\n                    },\r\n                    \"service_detail\": {\r\n                        \"Service_ID\": \"PM_SS1\",\r\n                        \"Service_Provider_ID\": \"GAADIZO_FAC\",\r\n                        \"Service_Provider\": \"demo Staging station\",\r\n                        \"Service_Availed\": [\r\n                            \"Standard Service (Semi Synthetic Oil)\"\r\n                        ]\r\n                    },\r\n                    \"miscellaneous\": {\r\n                        \"Gaadizo_Comments\": \"\"\r\n                    }\r\n            }\r\n        ],\r\n        \"booking_id_list\": [\r\n            \"GDZAA5789\",\r\n                        \"GDZAA5790\"\r\n        ]\r\n    }\r\n}";
			output1=json;*/
			result.put("output1", output1);
			if(UtilValidate.isNotEmpty(output1)){
				Map<String, Object> createResp;
		        try {
		        	Map<String, Object> createCtx = new HashMap<String, Object>();
		            createCtx.put("orderBookingJsonFromCRM", output1);
		            createCtx.put("userLogin", userLogin);
		            createResp = dispatcher.runSync("importOrderInERPFromRestApi", createCtx);
		        } catch (GenericServiceException e) {
		            Debug.logError(e, module);
		            return ServiceUtil.returnError(e.getMessage());
		        }
			}
		} catch (MalformedURLException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		Debug.logInfo(" Method insertIntoDB ends", module);
        return result;
    }
    
}
