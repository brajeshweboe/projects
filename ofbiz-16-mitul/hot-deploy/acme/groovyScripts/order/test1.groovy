import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

//URL url = new URL ("http://localhost:8080/job/test/buildWithParameters"); 
URL url = new URL("http://13.127.206.24:8000/booking_start_list"); //new URL(redirectUrl);
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
      }