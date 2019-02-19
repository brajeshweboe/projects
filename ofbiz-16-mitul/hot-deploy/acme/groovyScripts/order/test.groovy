import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilDateTime;
import org.apache.ofbiz.base.util.UtilFormatOut;
import org.apache.ofbiz.base.util.UtilHttp;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilProperties;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.transaction.GenericTransactionException;
import org.apache.ofbiz.entity.transaction.TransactionUtil;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.entity.util.EntityUtilProperties;
import org.apache.ofbiz.order.order.OrderChangeHelper;
import org.apache.ofbiz.product.store.ProductStoreWorker;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ModelService;

//Map<String, Object> params = new HashMap<String, Object>();
//0
//201
//404
 //params.put("username", "Erp");
 //params.put("key", "be5ca743900f009fd4bc817fbc00c");
	//	params.put("password", "Team");
 //String str = UtilHttp.urlEncodeArgs(params);
 
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
 
            URL u = new URL("http://13.127.206.24:8000/booking_start_list"); //new URL(redirectUrl);
            URLConnection uc = u.openConnection();
			uc.setRequestProperty("key","be5ca743900f009fd4bc817fbc00c");
			uc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			uc.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
			uc.setConnectTimeout(5000);
            uc.setDoOutput(true);
			println("=====1111========ERP Verification Response:======== " + uc);
            uc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            PrintWriter pw = new PrintWriter(uc.getOutputStream());
			println("====2222=========ERP Verification Response:======== " + pw);
            //pw.println(str);
            pw.close();

            BufferedReader i1qqqqn = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            confirmResp = i1qqqqn.readLine();
            i1qqqqn.close();
            println("=====3333========ERP Verification Response:======== " + confirmResp);