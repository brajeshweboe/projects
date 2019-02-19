import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.service.*
import org.apache.ofbiz.entity.*
import org.apache.ofbiz.entity.condition.*
import org.apache.ofbiz.entity.util.*
import org.apache.ofbiz.base.util.*
import com.gaadizo.data.*;
import java.text.SimpleDateFormat;

//Customer section
customerName = parameters.customerName;
emailAddress = parameters.emailAddress;
address = parameters.address;
contactNumber = parameters.contactNumber;
city = parameters.city;
postalCode = parameters.postalCode;
customerType = parameters.customerType;
vehicleRegNo = parameters.vehicleRegNo;

//Vehicle section
vehicleModel = parameters.vehicleModel;
vehicleManufacturer = parameters.vehicleManufacturer;
vehicleId = parameters.vehicleId;
vehicleFuelType = parameters.vehicleFuelType;
vehicleVariant = parameters.vehicleVariant;
oilCapacity = parameters.oilCapacity;
vehicleRegYear = parameters.vehicleRegYear;
serviceDate = parameters.serviceDate;
deliveryDate= parameters.deliveryDate;
lastServiceDate = parameters.lastServiceDate;
vehicleInsuranceComp = parameters.vehicleInsuranceComp;
insuranceExpDate = parameters.insuranceExpDate;
paymentMode = parameters.paymentMode;

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
        println("======emailAddress======="+emailAddress+"===================");
        partyAndContactMechList = from("PartyAndPostalAddress").where("infoString",emailAddress,"ownerId",userLogin.ownerCompanyId).cache(true).queryList()
        partyAndContactMechList = from("PartyAndContactMech").where("infoString",emailAddress,"ownerId",userLogin.ownerCompanyId).cache(true).queryList()
        		
        if(UtilValidate.isNotEmpty(partyAndContactMechList)){
            request.setAttribute("_ERROR_MESSAGE_", "Email already exists in the system.")
            println("================================");
            partyAndContactMechSGV = EntityUtil.getFirst(EntityUtil.filterByDate(partyAndContactMechList))
            println("================================");
            listPartyPostalAddress = delegator.findByAnd("PartyAndPostalAddress", [partyId: partyAndContactMechSGV.partyId], null, true)
            context.partyPostalAddress = EntityUtil.getFirst(listPartyPostalAddress);
            println("===121213======"+context.partyPostalAddress+"=======================");
            context.personGV = from("Person").where("partyId", partyAndContactMechSGV.partyId).cache(true).queryOne()
            if(UtilValidate.isNotEmpty(shoppingCart) && UtilValidate.isNotEmpty(partyAndContactMechSGV) && UtilValidate.isNotEmpty(partyAndContactMechSGV.partyId)){
                shoppingCart.setShipToCustomerPartyId(partyAndContactMechSGV.partyId);
            }
            String shoppingCartJobCardId = shoppingCart.getJobCardId();
             println("===64==shoppingCartJobCardId===="+shoppingCartJobCardId+"===121212===="+parameters.jobCardId+"==1345======="+shoppingCart.jobCardId+"=======");
             if(UtilValidate.isEmpty(shoppingCartJobCardId)){
            //jobCardHistoryList
            /*
                String customerName = (String)context.get("customerName");
		        String serviceDate = (String)context.get("serviceDate");
		        String pickup = (String)context.get("pickup");
		        String vehicleId = (String)par.get("vehicleId");
		        String vehicleModel = (String)context.get("vehicleModel");
		        String vehicleManufacturer = (String)context.get("vehicleManufacturer");
		        String fuelType = (String)context.get("fuelType");
		        String oilCapacity = (String)context.get("oilCapacity");
		        String vehicleVariant = (String)context.get("vehicleVariant");
		        String VehicleRegNo = (String)context.get("VehicleRegNo");
		        String VehicleRegYear = (String)context.get("VehicleRegYear");
		        String lastServiceDate = (String)context.get("LastServiceDate");
		        String deliveryDate = (String)context.get("deliveryDate");
		        String vehicleInsuranceComp = (String)context.get("vehicleInsuranceComp");
		        String paymentMode = (String)context.get("paymentMode");
		        String InsuranceExpiryDate = (String)context.get("InsuranceDate");
		        String customerType = (String)context.get("customerType"); */
		        
		        /* createExistingCustomerJCCtx = 
    ["userLogin" : userLogin,vehicleManufacturer:vehicleManufacturer,vehicleModel:vehicleModel,oilType:fuelType,oilCapacity:oilCapacity,
     customerType:customerType,paymentMode:paymentMode,insuranceExpiryDate:InsuranceExpiryDate,pickup:pickup,
     vehicleInsuranceComp:vehicleInsuranceComp,serviceDate:serviceDate,registationNumber:VehicleRegNo,
     lastServiceDate:lastServiceDate,vehicleId:vehicleId,customerId:partyAndContactMechSGV.partyId,deliveryDate:deliveryDate]; */
     
    createExistingCustomerJCCtx = ["firstName" : customerName,"vehicleFuelType":vehicleFuelType,
    "userLogin" : userLogin,"vehicleManufacturer":vehicleManufacturer,"vehicleModel":vehicleModel,"oilCapacity":oilCapacity,
     "vehicleVariant":vehicleVariant,"customerType":customerType,"paymentMode":paymentMode,"insuranceExpiryDate":InsuranceDate,"deliveryDate":DeliveryDate,
     "vehicleInsuranceComp":vehicleInsuranceComp,"serviceDate":ServiceDate,"registationNumber":VehicleRegNo,
     "lastServiceDate":LastServiceDate,"vehicleId":vehicleId,"customerId":partyAndContactMechSGV.partyId];
            resultMap = runService('createJobCard', createExistingCustomerJCCtx);
             if(UtilValidate.isNotEmpty(resultMap.jobCardId)){
    			parameters.jobCardId = resultMap.jobCardId;
    			context.jobCardId = resultMap.jobCardId;
    }
            return "customeExists";
            }
        }
    }
}
if(UtilValidate.isEmpty(partyAndContactMechList)){
if(UtilValidate.isEmpty(address)){
    request.setAttribute("_ERROR_MESSAGE_", "Please enter valid address.")
}
if(UtilValidate.isEmpty(contactNumber)){
    request.setAttribute("_ERROR_MESSAGE_", "Please enter valid contact number.")
} 
if(UtilValidate.isEmpty(city)){
    request.setAttribute("_ERROR_MESSAGE_", "Please enter valid city.")
}
if(UtilValidate.isEmpty(postalCode)){
    request.setAttribute("_ERROR_MESSAGE_", "Please enter valid postal code.")
}
//Map inputFieldsMap = new HashMap();

//inputFieldsMap.putAll(parameters)
createCustomerAndVehicleFromJCCtx = 
    ["customerName" : customerName,"city":city,"postalCode":postalCode, "emailAddress" : emailAddress,"address":address,"vehicleFuelType":vehicleFuelType,
     "contactNumber":contactNumber, "userLogin" : userLogin,"vehicleManufacturer":vehicleManufacturer,"vehicleModel":vehicleModel,"oilCapacity":oilCapacity,
     "vehicleVariant":vehicleVariant,"customerType":customerType,"paymentMode":paymentMode,"insuranceExpiryDate":InsuranceDate,"deliveryDate":DeliveryDate,
     "vehicleInsuranceComp":vehicleInsuranceComp,"serviceDate":ServiceDate,"vehicleRegNo":VehicleRegNo,"vehicleRegYear":VehicleRegYear,
     "lastServiceDate":LastServiceDate,"vehicleId":vehicleId];
//if(UtilValidate.isEmpty(parameters.partyId)){
//    resultMap = runService('createCustomerFromJC', ["customerName" : customerName,"city":city,"postalCode":postalCode, "emailAddress" : emailAddress,"address":address,"contactNumber":contactNumber, "userLogin" : userLogin])
//}
//inputFieldsMap.put("userLogin", userLogin);
//if(UtilValidate.isNotEmpty(parameters.partyId)){
println("===909090=====createCustomerAndVehicleFromJCCtx======"+createCustomerAndVehicleFromJCCtx+"===============");
    resultMap = runService('createCustomerAndVehicleFromJC', createCustomerAndVehicleFromJCCtx);
    if(UtilValidate.isNotEmpty(resultMap.jobCardId)){
    	parameters.jobCardId = resultMap.jobCardId;
    	context.jobCardId = resultMap.jobCardId;
    }
//}
println("===76767676=====parameters======"+resultMap+"===============");
if(UtilValidate.isNotEmpty(shoppingCart) && UtilValidate.isNotEmpty(resultMap) && UtilValidate.isNotEmpty(resultMap.partyId)){
    println("===76767676=====parameters======"+resultMap+"===============");
    shoppingCart.setShipToCustomerPartyId(null);
    println("=1111==76767676=====parameters======"+shoppingCart.getShipToCustomerPartyId()+"===============");
    shoppingCart.setShipToCustomerPartyId(resultMap.partyId);
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



return "error";
}