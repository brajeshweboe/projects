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
import java.util.Date;
import java.sql.Timestamp;
import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import org.apache.ofbiz.base.util.StringUtil; 
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilProperties;
import org.apache.ofbiz.base.util.UtilValidate;
import java.util.List;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
 
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



	// http://localhost:8080/RESTfulExample/json/product/post
	public static void main(String[] args) {

		try {

			URL url = new URL("http://13.127.206.24:8000/get_jobcard_details");
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
			while ((output = br.readLine()) != null) {
				System.out.println(output);
				output1 = output1+output;
			}
			Map<String,Object> registrationMap = new HashMap<String,Object>();
			
			 System.out.println("===105===output1====================================="+output1+"=========================");
			 JSONParser parser = new JSONParser();
			 JSONObject jsonobj = (JSONObject) parser.parse(output1);
             JSONArray langJSONArray = (JSONArray) jsonobj.get("data");
			 System.out.println("===111===jsonobj====================================="+langJSONArray+"=========================");
			 
			 Iterator langJSONArrayItr = langJSONArray.iterator();
			 while (langJSONArrayItr.hasNext()) {
Map<String,String> registrationValueMap = new HashMap<String,String>();
                JSONObject innerObj = (JSONObject) langJSONArrayItr.next();

                System.out.println("language============ "+ innerObj.get("shop_id"));
                registrationValueMap.put("vehicle_number",innerObj.get("vehicle_number"));
                registrationMap.put(innerObj.get("shop_id").toString(),registrationValueMap);
            }
            partyGroup = from("PartyGroup").where("partyId", "admin").queryOne()
			// System.out.println("===52===registrationMap====================================="+userLogin+"=========================");
			//=========================================================================================================================================
			    for (String registration : registrationMap.keySet()) {
			         Map registrationM = (Map) registrationMap.get(registration); 
						 
                        //resultMap = runService("createCustomerAndJobCardFromRegistration", ["serviceCenterId" : "GAADIZO", "registrationNumber" : registrationM.get("vehicle_number").toString(), userLogin : userLogin])
                        Map<String, Object> result1 = dispatcher.runSync("createCustomerAndJobCardFromRegistration", ["serviceCenterId" : "GAADIZO105", "registrationNumber" : registrationM.get("vehicle_number").toString(), userLogin : userLogin]);
                        }
           //=========================================================================================================================================
			
			conn.disconnect();

		} catch (MalformedURLException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();

		}


}