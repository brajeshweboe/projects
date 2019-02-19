import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.service.*
import org.apache.ofbiz.entity.*
import org.apache.ofbiz.entity.condition.*
import org.apache.ofbiz.entity.util.*
import org.apache.ofbiz.base.util.*
import com.gaadizo.data.*;
import java.text.SimpleDateFormat;


customerName = parameters.customerName;
emailAddress = parameters.emailAddress;
address = parameters.address;
contactNumber = parameters.contactNumber;
city = parameters.city;
postalCode = parameters.postalCode;

vehicleId = parameters.vehicleId;
vehicleModel = parameters.vehicleModel;
vehicleManufacturer = parameters.vehicleManufacturer;
vehicleFuelType = parameters.vehicleFuelType;
vehicleVariant = parameters.vehicleVariant;
oilCapacity = parameters.oilCapacity;
registationNumber = parameters.registationNumber;
VehicleRegYear = parameters.VehicleRegYear;
ServiceDate = parameters.ServiceDate;
DeliveryDate= parameters.DeliveryDate;
LastServiceDate = parameters.LastServiceDate;
vehicleInsuranceComp = parameters.vehicleInsuranceComp;
InsuranceDate = parameters.InsuranceDate;
customerType = parameters.customerType;
paymentMode = parameters.paymentMode;


if(UtilValidate.isEmpty(emailAddress)){
    request.setAttribute("_ERROR_MESSAGE_", "Please enter valid Email Address.")
}
if(UtilValidate.isEmpty(contactNumber)){
    request.setAttribute("_ERROR_MESSAGE_", "Please enter valid contact number.")
} 
if(UtilValidate.isEmpty(customerName)){
    request.setAttribute("_ERROR_MESSAGE_", "Please enter valid customerName.")
}
if(UtilValidate.isEmpty(postalCode)){
    request.setAttribute("_ERROR_MESSAGE_", "Please enter valid postal code.")
}

Map jcpMap = new HashMap();
Map jcMap = new HashMap();
jcpMap.put("customerName", customerName);
jcpMap.put("emailAddress", emailAddress);
jcpMap.put("address", address);
jcpMap.put("contactNumber", contactNumber);
jcpMap.put("city", city);
jcpMap.put("postalCode", postalCode);

jcMap.put("customerName", customerName);
jcMap.put("vehicleId", vehicleId);
jcMap.put("vehicleModel", vehicleModel);
jcMap.put("vehicleManufacturer", vehicleManufacturer);
jcMap.put("vehicleFuelType", vehicleFuelType);
jcMap.put("vehicleVariant", vehicleVariant);
jcMap.put("oilCapacity", oilCapacity);
jcMap.put("registationNumber", registationNumber);
jcMap.put("VehicleRegYear", VehicleRegYear);
jcMap.put("ServiceDate", ServiceDate);
jcMap.put("DeliveryDate", DeliveryDate);
jcMap.put("LastServiceDate", LastServiceDate);
jcMap.put("vehicleInsuranceComp", vehicleInsuranceComp);
jcMap.put("InsuranceDate", InsuranceDate);
jcMap.put("customerType", customerType);
jcMap.put("paymentMode", paymentMode);
partyAndPostalAddressList = null;
shoppingCart = session.getAttribute("shoppingCart")
if(UtilValidate.isEmpty(shoppingCart)){
    shoppingCart = ShoppingCartEvents.getCartObject(request);
}
println("==388888=====productStore============"+shoppingCart.getJobCardId()+"==========");
println("==26====emailAddress======="+shoppingCart.getPartyId()+"===================");
partyAndContactMechList = null;
if(UtilValidate.isEmpty(customerName)){
    request.setAttribute("_ERROR_MESSAGE_", "Please enter valid customer name.")
}
println("======emailAddress======="+emailAddress+"=========="+customerName+"=========");
if(UtilValidate.isEmpty(emailAddress)){
    request.setAttribute("_ERROR_MESSAGE_", "Please enter valid email.")
} else {
    if(UtilValidate.isNotEmpty(emailAddress) && !UtilValidate.isEmail(emailAddress)){
        request.setAttribute("_ERROR_MESSAGE_", "Please enter valid email.")
        return "error";
    } else {
        println("===92929292===emailAddress======="+emailAddress+"===================");
        //partyAndContactMechList = from("PartyAndContactMech").where("infoString",emailAddress).queryList()
        partyAndPostalAddressList = from("PartyAndPostalAddress").where("infoString",emailAddress,"ownerId",userLogin.ownerCompanyId).cache(true).queryList()
        		
        if(UtilValidate.isNotEmpty(partyAndPostalAddressList)){
        	partyAndPostalAddress = EntityUtil.getFirst(EntityUtil.filterByDate(partyAndPostalAddressList))
        	        String customerId = partyAndPostalAddress.partyId;
            request.setAttribute("_ERROR_MESSAGE_", "Email already exists in the system.")
            
            jcMap.put("customerId", customerId);
            context.personGV = from("Person").where("partyId", customerId).cache(true).queryOne()
            context.partyPostalAddress = partyAndPostalAddress;
            
            if(UtilValidate.isNotEmpty(shoppingCart) && UtilValidate.isNotEmpty(partyAndPostalAddress) && UtilValidate.isNotEmpty(customerId)){
                shoppingCart.setShipToCustomerPartyId(customerId);
            }
             //String shoppingCartJobCardId = shoppingCart.getJobCardId();
             //println("===64==shoppingCartJobCardId===="+shoppingCartJobCardId+"===121212===="+parameters.jobCardId+"==1345======="+shoppingCart.jobCardId+"=======");
            // if(UtilValidate.isEmpty(shoppingCartJobCardId)){
            //jobCardHistoryList
            createExistingCustomerJCCtx = ["firstName" : customerName,"vehicleFuelType":vehicleFuelType,
                                           "userLogin" : userLogin,"vehicleManufacturer":vehicleManufacturer,"vehicleModel":vehicleModel,"oilCapacity":oilCapacity,
                                           "vehicleVariant":vehicleVariant,"customerType":customerType,"paymentMode":paymentMode,"insuranceExpiryDate":InsuranceDate,"deliveryDate":DeliveryDate,
                                           "vehicleInsuranceComp":vehicleInsuranceComp,"serviceDate":ServiceDate,"registationNumber":registationNumber,
                                           "lastServiceDate":LastServiceDate,"vehicleId":vehicleId,"customerId":customerId];
            resultMap = runService('createJobCard', createExistingCustomerJCCtx);
            if(UtilValidate.isNotEmpty(resultMap.jobCardId)){
    			parameters.jobCardId = resultMap.jobCardId;
    			context.jobCardId = resultMap.jobCardId;
            }
            return "customeExists";
        }
    }
}
if(UtilValidate.isEmpty(partyAndPostalAddressList)){
	 println("=====1251111111111111111111==================");
//Map inputFieldsMap = new HashMap();

//inputFieldsMap.putAll(parameters)
createCustomerAndVehicleFromJCCtx = 
    ["customerName" : customerName,"city":city,"postalCode":postalCode, "emailAddress" : emailAddress,"address":address,"vehicleFuelType":vehicleFuelType,
     "contactNumber":contactNumber, "userLogin" : userLogin,"vehicleManufacturer":vehicleManufacturer,"vehicleModel":vehicleModel,"oilCapacity":oilCapacity,
     "vehicleVariant":vehicleVariant,"customerType":customerType,"paymentMode":paymentMode,"insuranceExpiryDate":InsuranceDate,"deliveryDate":DeliveryDate,
     "vehicleInsuranceComp":vehicleInsuranceComp,"serviceDate":ServiceDate,"registationNumber":registationNumber,"vehicleRegYear":VehicleRegYear,
     "lastServiceDate":LastServiceDate,"vehicleId":vehicleId];
//if(UtilValidate.isEmpty(parameters.partyId)){
//    resultMap = runService('createCustomerFromJC', ["customerName" : customerName,"city":city,"postalCode":postalCode, "emailAddress" : emailAddress,"address":address,"contactNumber":contactNumber, "userLogin" : userLogin])
//}
//inputFieldsMap.put("userLogin", userLogin);
//if(UtilValidate.isNotEmpty(parameters.partyId)){
println("===909090=====createCustomerAndVehicleFromJCCtx======"+createCustomerAndVehicleFromJCCtx+"===============");
    resultMap = runService('createCustomerAndVehicleFromJC', createCustomerAndVehicleFromJCCtx);
    println("===144444=====resultMap======"+resultMap+"===============");
    if(UtilValidate.isNotEmpty(resultMap.jobCardId)){
    	parameters.jobCardId = resultMap.jobCardId;
    	println("===76767676=====parameters.jobCardId======"+parameters.jobCardId+"===============");
    	context.jobCardId = resultMap.jobCardId;
    }
//}
println("===76767676=====parameters======"+resultMap+"===============");
if(UtilValidate.isNotEmpty(shoppingCart) && UtilValidate.isNotEmpty(resultMap) && UtilValidate.isNotEmpty(resultMap.partyId)){
    println("===76767676=====parameters======"+resultMap+"===============");
    println("=1111==76767676=====parameters======"+shoppingCart.getShipToCustomerPartyId()+"===============");
    shoppingCart.setOrderPartyId(resultMap.partyId);
    shoppingCart.setJobCardId(resultMap.jobCardId);
    shoppingCart.setAllShipmentMethodTypeId("NO_SHIPPING");

    
    //==========================================================================================
    //private String jobCardId;
    //private String orderId;
    //private String customerId;
    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss MMM d, yyyy z");
    //Timestamp sdfdata = new Timestamp(parameters.ServiceDate)
  //  java.sql.Timestamp sdfdate = new java.sql.Timestamp(sdf.parse(parameters.ServiceDate));
    //shoppingCart.setServiceDate(parameters.ServiceDate)
    shoppingCart.setVehicleId(parameters.vehicleId)
    shoppingCart.setVehicleModelId(parameters.vehicleModelId)
    shoppingCart.setVehicleModel(parameters.vehicleModel)
    shoppingCart.setRegistationNumber(parameters.registationNumber)
   // shoppingCart.setCustomerName(parameters.customerName)
    shoppingCart.setVehicleMakeId(parameters.vehicleMakeId)
    shoppingCart.setVehicleMake(parameters.vehicleMake)
     shoppingCart.setPaymentMode(parameters.paymentMode)
     
   /* newJC.setServiceDate(parameters.serviceDate)
    newJC.setServiceDate(parameters.serviceDate)
    newJC.setServiceDate(parameters.serviceDate)
    newJC.setServiceDate(parameters.serviceDate)
    newJC.setServiceDate(parameters.serviceDate)
   
    
    private String offerDetails;
    private String paymentMode;
    private String serviceTime;
    private String pickup;
    private String createdBy;
    private String gaadizoCredit;*/
    //=============================================================================================
    
    
    println("==2222=76767676=====parameters======"+shoppingCart.getShipToCustomerPartyId()+"===============");
}
context.partyId=resultMap.partyId;
context.jobCardId=parameters.jobCardId;
parameters.jobCardId=parameters.jobCardId;



return "success";
}