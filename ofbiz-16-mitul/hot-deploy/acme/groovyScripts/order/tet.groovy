import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
//import javax.json.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.GeneralException;
import org.apache.ofbiz.base.util.HttpClient;
import org.apache.ofbiz.base.util.HttpClientException;
import org.apache.ofbiz.base.util.UtilGenerics;
import org.apache.ofbiz.base.util.UtilNumber;
import org.apache.ofbiz.base.util.UtilProperties;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.base.util.UtilXml;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;
import org.w3c.dom.Document;
import java.util.HashMap;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import java.io.StringBufferInputStream;
import org.apache.ofbiz.base.util.UtilDateTime;
import org.apache.ofbiz.entity.util.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;


public class NetClientPost {

	// http://localhost:8080/RESTfulExample/json/product/post
	public static void main(String[] args) {

		try {

			URL url = new URL("http://13.127.206.24:8000/booking_start_list");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("key", "be5ca743900f009fd4bc817fbc00c");

			//JSONObject cred   = new JSONObject();
           // cred.put("username","Erp");
            //cred.put("password", "Team");
			String input = "{\"username\":\"Erp\",\"password\":\"Team\"}";
 
			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();
    
	        System.out.println(conn.getResponseCode());
			/*if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}*/

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			String output1="";
			System.out.println("Output from Server .... \n");
			/*
			{"status":201,"msg":"success","data":{"booking_list":[{"GDZAA5789":{"booking_detail":{"Order_ID":5635,"Booking_ID":"GDZAA5789","Booking_Date":"2018-10-18","Service_Date":"24 Oct 2018","Service_Time":"11:00 AM","Payable_Amount":3724,"Invoice_Amount":3724,"Order_Amount":3724,"Discount_Amount":0,"Offer_Code":0,"Offer_Amount":3724,"Offer_Detail":3724,"Oil_Type":"Semi Synthetic Oil","Status":"Service Started","Gaadizo_Creadit":0,"Prepaid_Amount":0,"Payment_Mode":"Cash On Delivery"},"customer_detail":{"Customer_ID":4773,"First_Name":"naresh","Last_Name":"","Cust_Email":"naresh@gmail.com","Address_1":"","Address_2":"","City":"","State_Code":""},"vehicle_detail":{"Vehicle_Make":"Ford","Vehicle_Make_ID":18,"Vehicle_Model":"Eco Sport Diesel","Vehicle_Model_ID":385,"Vehicle_Regstn_No":null,"Year":null},"service_detail":{"Service_ID":"1","Service_Provider_ID":394,"Service_Provider":"demo Staging station","Service_Availed":["Standard Service (Semi Synthetic Oil)"]},"miscellaneous":{"Gaadizo_Comments":""}}},{"GDZAA5791":{"booking_detail":{"Order_ID":5636,"Booking_ID":"GDZAA5791","Booking_Date":"2018-10-19","Service_Date":"24 Oct 2018","Service_Time":"03:00 PM","Payable_Amount":3998,"Invoice_Amount":3998,"Order_Amount":3998,"Discount_Amount":0,"Offer_Code":0,"Offer_Amount":3998,"Offer_Detail":3998,"Oil_Type":"Normal Oil","Status":"Service Started","Gaadizo_Creadit":0,"Prepaid_Amount":0,"Payment_Mode":"Cash On Delivery"},"customer_detail":{"Customer_ID":50015,"First_Name":"abcdexyz","Last_Name":"","Cust_Email":"abcde.xyz@gmail.com","Address_1":"","Address_2":"","City":"","State_Code":""},"vehicle_detail":{"Vehicle_Make":"Nissan","Vehicle_Make_ID":22,"Vehicle_Model":"Xtrail","Vehicle_Model_ID":197,"Vehicle_Regstn_No":"vb45ag1234","Year":null},"service_detail":{"Service_ID":"1","Service_Provider_ID":394,"Service_Provider":"demo Staging station","Service_Availed":["Standard Service (Normal Oil)"]},"miscellaneous":{"Gaadizo_Comments":""}}},{"GDZAA5793":{"booking_detail":{"Order_ID":5637,"Booking_ID":"GDZAA5793","Booking_Date":"2018-10-19","Service_Date":"24 Oct 2018","Service_Time":"04:00 PM","Payable_Amount":null,"Invoice_Amount":null,"Order_Amount":null,"Discount_Amount":0,"Offer_Code":0,"Offer_Amount":null,"Offer_Detail":null,"Oil_Type":null,"Status":"Service Started","Gaadizo_Creadit":0,"Prepaid_Amount":0,"Payment_Mode":"Cash On Delivery"},"customer_detail":{"Customer_ID":30015,"First_Name":"Vikrant Yadav","Last_Name":"","Cust_Email":"vikrant.yadav@gaadizo.com","Address_1":"","Address_2":"","City":"","State_Code":""},"vehicle_detail":{"Vehicle_Make":"Fiat","Vehicle_Make_ID":34,"Vehicle_Model":"Punto-Diesel","Vehicle_Model_ID":231,"Vehicle_Regstn_No":"Hr24ag1234","Year":2015},"service_detail":{"Service_ID":"0","Service_Provider_ID":394,"Service_Provider":"demo Staging station"},"miscellaneous":{"Gaadizo_Comments":null,"Pick_up":1,"Pick_up_Address":{"Area":"Sohna Road","Locality":"Near JMD","Land_Mark":"Haryana"}}}}],"booking_id_list":["GDZAA5789","GDZAA5791","GDZAA5793"]}}
			*/
			while ((output = br.readLine()) != null) {
			output1 = output1+output;
				System.out.println(output);
			}
//output1="{"status":201,"msg":"success","data":{"booking_list":[{"GDZAA5789":{"booking_detail":{"Order_ID":5635,"Booking_ID":"GDZAA5789","Booking_Date":"2018-10-18","Service_Date":"24 Oct 2018","Service_Time":"11:00 AM","Payable_Amount":3724,"Invoice_Amount":3724,"Order_Amount":3724,"Discount_Amount":0,"Offer_Code":0,"Offer_Amount":3724,"Offer_Detail":3724,"Oil_Type":"Semi Synthetic Oil","Status":"Service Started","Gaadizo_Creadit":0,"Prepaid_Amount":0,"Payment_Mode":"Cash On Delivery"},"customer_detail":{"Customer_ID":4773,"First_Name":"naresh","Last_Name":"","Cust_Email":"naresh@gmail.com","Address_1":"","Address_2":"","City":"","State_Code":""},"vehicle_detail":{"Vehicle_Make":"Ford","Vehicle_Make_ID":18,"Vehicle_Model":"Eco Sport Diesel","Vehicle_Model_ID":385,"Vehicle_Regstn_No":null,"Year":null},"service_detail":{"Service_ID":"1","Service_Provider_ID":394,"Service_Provider":"demo Staging station","Service_Availed":["Standard Service (Semi Synthetic Oil)"]},"miscellaneous":{"Gaadizo_Comments":""}}},{"GDZAA5791":{"booking_detail":{"Order_ID":5636,"Booking_ID":"GDZAA5791","Booking_Date":"2018-10-19","Service_Date":"24 Oct 2018","Service_Time":"03:00 PM","Payable_Amount":3998,"Invoice_Amount":3998,"Order_Amount":3998,"Discount_Amount":0,"Offer_Code":0,"Offer_Amount":3998,"Offer_Detail":3998,"Oil_Type":"Normal Oil","Status":"Service Started","Gaadizo_Creadit":0,"Prepaid_Amount":0,"Payment_Mode":"Cash On Delivery"},"customer_detail":{"Customer_ID":50015,"First_Name":"abcdexyz","Last_Name":"","Cust_Email":"abcde.xyz@gmail.com","Address_1":"","Address_2":"","City":"","State_Code":""},"vehicle_detail":{"Vehicle_Make":"Nissan","Vehicle_Make_ID":22,"Vehicle_Model":"Xtrail","Vehicle_Model_ID":197,"Vehicle_Regstn_No":"vb45ag1234","Year":null},"service_detail":{"Service_ID":"1","Service_Provider_ID":394,"Service_Provider":"demo Staging station","Service_Availed":["Standard Service (Normal Oil)"]},"miscellaneous":{"Gaadizo_Comments":""}}},{"GDZAA5793":{"booking_detail":{"Order_ID":5637,"Booking_ID":"GDZAA5793","Booking_Date":"2018-10-19","Service_Date":"24 Oct 2018","Service_Time":"04:00 PM","Payable_Amount":null,"Invoice_Amount":null,"Order_Amount":null,"Discount_Amount":0,"Offer_Code":0,"Offer_Amount":null,"Offer_Detail":null,"Oil_Type":null,"Status":"Service Started","Gaadizo_Creadit":0,"Prepaid_Amount":0,"Payment_Mode":"Cash On Delivery"},"customer_detail":{"Customer_ID":30015,"First_Name":"Vikrant Yadav","Last_Name":"","Cust_Email":"vikrant.yadav@gaadizo.com","Address_1":"","Address_2":"","City":"","State_Code":""},"vehicle_detail":{"Vehicle_Make":"Fiat","Vehicle_Make_ID":34,"Vehicle_Model":"Punto-Diesel","Vehicle_Model_ID":231,"Vehicle_Regstn_No":"Hr24ag1234","Year":2015},"service_detail":{"Service_ID":"0","Service_Provider_ID":394,"Service_Provider":"demo Staging station"},"miscellaneous":{"Gaadizo_Comments":null,"Pick_up":1,"Pick_up_Address":{"Area":"Sohna Road","Locality":"Near JMD","Land_Mark":"Haryana"}}}}],"booking_id_list":["GDZAA5789","GDZAA5791","GDZAA5793"]}}";
			/* JSONParser parser = new JSONParser();
			 JSONObject jsonobj = (JSONObject) parser.parse(output1);
             System.out.println("=11111111111=1212===jsonobj====================================="+jsonobj+"=========================");
             System.out.println("=2222222=1212===jsonobj====================================="+jsonobj+"=========================");
             JSONArray langJSONArray = (JSONArray) jsonobj.get("data");
			 System.out.println("===111===jsonobj====================================="+langJSONArray+"=========================");
			 
			 Iterator langJSONArrayItr = langJSONArray.iterator();
			 while (langJSONArrayItr.hasNext()) {
           		Map<String,String> registrationValueMap = new HashMap<String,String>();
                JSONObject innerObj = (JSONObject) langJSONArrayItr.next();

JSONObject inne1rObj = (JSONObject) innerObj.get("data");
                System.out.println("language============ "+inne1rObj);
               // registrationValueMap.put("vehicle_number",innerObj.get("vehicle_number"));
                //registrationMap.put(innerObj.get("shop_id").toString(),registrationValueMap);
            }
			

			conn.disconnect();
*/
		} catch (MalformedURLException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();

		}

	}

}