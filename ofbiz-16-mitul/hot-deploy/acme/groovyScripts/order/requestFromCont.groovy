import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
System.out.println("===25252525==start===================");
String output=null;
    String endpoint,inputParam, username, password;
    inputParam= OrderId;
    try{
        endpoint = "http://192.168.0.74:8085/webtools/control/SOAPService";
        username="admin";
        password="ofbiz";
        Call call = (Call) new Service().createCall();
        call.setTargetEndpointAddress(new URL (endpoint));
        call.setOperationName(new javax.xml.namespace.QName("createBulkPaymentAndDetails"));
       // call.addParameter ("orderId", org.apache.axis.Constants.XSD_STRING,javax.xml.rpc.ParameterMode.IN);
        call.addParameter("login.username", org.apache.axis.Constants.XSD_STRING, javax.xml.rpc.ParameterMode.IN);
        call.addParameter("login.password", org.apache.axis.Constants.XSD_STRING, javax.xml.rpc.ParameterMode.IN);
        call.setReturnType(org.apache.axis.Constants.SOAP_STRING);
        Object response = call.invoke(new Object[]{inputParam,username,password});
        output = (String) response;
		System.out.println("===25252525==output====="+output+"===================");
        try{
            System.out.println(output);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }catch(Exception e){
        e.printStackTrace();
    }
   // return output;
//System.out.println("=========="+parameters+"===================");

/*
//URL url = new URL ("http://localhost:8080/job/test/buildWithParameters"); 
//URL url = new URL("http://13.127.206.24:8000/booking_start_list"); //new URL(redirectUrl);
// Jenkins URL localhost:8080, job named 'test'
      String user = "Erp"; // username
      String pass = "Team"; // password or API token
      String authStr = user +":"+  pass;
      String encoding = DatatypeConverter.printBase64Binary(authStr.getBytes("utf-8"));

      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("POST");
      connection.setDoOutput(true);
      connection.setRequestProperty("Authorization", "Basic " + encoding);

      String urlParams="paramA=123";
      byte[] postData = urlParams.getBytes("utf-8");
      try(DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
        wr.write(postData);
      }

      InputStream content = connection.getInputStream();
      BufferedReader in   =
          new BufferedReader (new InputStreamReader (content));
      String line;
      while ((line = in.readLine()) != null) {
        System.out.println(line);
      }*/