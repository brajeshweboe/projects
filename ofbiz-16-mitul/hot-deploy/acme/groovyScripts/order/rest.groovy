import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

import java.io.InputStreamReader;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigDecimal;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


import java.net.URLConnection;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
//import net.sf.json.JSONException;
//import net.sf.json.JSONObject;
import java.net.URLEncoder;
import java.io.BufferedReader;


//InputStream in = null;
        HttpURLConnection connection = null;
        try {
            // connect to the gateway
			Map<String, Object> params = new HashMap<String, Object>();
		params.put("username", "Erp");
		params.put("password", "Team");
		StringBuilder postData = new StringBuilder();
		for (Map.Entry<String, Object> param : params.entrySet()) {
			if (postData.length() != 0)
				postData.append('&');
			postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
			postData.append('=');
			postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
			System.out.println("Making paramters "+param.getValue());
		}
		byte[] postDataBytes = postData.toString().getBytes("UTF-8");

		try {
			url = new URL("http://13.127.206.24:8000/booking_start_list");
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
			conn.setDoOutput(true);
			 OutputStream out  = conn.getOutputStream().write(postDataBytes);


			 
			 
			 Writer wout = new OutputStreamWriter(out);
            wout.write(request.toXml());
			 System.out.println("JSON object "+request);
            wout.flush();
            wout.close();

            inputSt = connection.getInputStream();
            response = new GatewayResponse(inputSt, request);
		   System.out.println("JSON object "+response);


		} catch (Exception e) {
			Debug.logError("Error in payment  : " + e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		/*StringBuilder postData = new StringBuilder();
		for (Map.Entry<String, Object> param : params.entrySet()) {
			if (postData.length() != 0)
				postData.append('&');
			postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
			postData.append('=');
			postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
			System.out.println("Making paramters "+param.getValue());
		}
		   byte[] postDataBytes = postData.toString().getBytes("UTF-8");
			
            url = new URL("http://13.127.206.24:8000/booking_start_list");
			
        
        
        try {
            // connect to the gateway
            URL u = new URL(url);
            connection = (HttpURLConnection)(u.openConnection());
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");            
            connection.setConnectTimeout(timeout*1000);            
            
            OutputStream out = connection.getOutputStream();
            Writer wout = new OutputStreamWriter(out);
            wout.write(request.toXml());
            wout.flush();
            wout.close();

            i3e3n = connection.getInputStream();
            response = new GatewayResponse(i3e3n, request);
            
        } 
        catch (Exception e) {
            // rethrow exception so that the caller learns what went wrong
            Debug.logError(e, e.getMessage(), module);
            throw e;
        } */
			/*
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

			String readAPIResponse = " ";
			StringBuilder jsonString = new StringBuilder();
			while ((readAPIResponse = in.readLine()) != null) {
				jsonString.append(readAPIResponse);
			}

			jsonObj = new JSONObject().fromObject(jsonString.toString());
			*/
		   System.out.println("JSON object "+jsonObj);
        } 
        catch (Exception e) {
            // rethrow exception so that the caller learns what went wrong
            Debug.logError(e, e.getMessage(), module);
            throw e;
        }