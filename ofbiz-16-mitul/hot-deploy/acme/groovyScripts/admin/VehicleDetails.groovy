import java.util.*
import java.sql.Timestamp;
import org.apache.ofbiz.entity.*
import org.apache.ofbiz.base.util.*
import org.apache.ofbiz.entity.util.*
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.condition.*

module = "CustomerDetails.groovy"
partyId = parameters.partyId;
orderHeaderAndJobCardGV = null;
vehicleList = null;
if(UtilValidate.isNotEmpty(partyId)) {
    
    println("======partyId=====12121212====="+partyId+"===========");

    partyAndPersonGV = null;
    userLoginGV = null;
    //get the order types
    partyAndPersonList = from("PartyAndUserLoginAndPerson").where("partyId", partyId).queryList()
    
    if(UtilValidate.isNotEmpty(partyAndPersonList)) {
	partyAndPersonGV = EntityUtil.getFirst(partyAndPersonList);
	context.partyAndPersonGV = partyAndPersonGV;

	partyContactDetailByPurposeList = from("PartyContactDetailByPurpose").where("partyId", partyId).queryList();
	shippingPCDPGV=null;
	billingPCDPGV=null;
	emailPCDPGV=null;
	phonePCDPGV=null;
	if(UtilValidate.isNotEmpty(partyContactDetailByPurposeList)) {
		for(pcdpGV in partyContactDetailByPurposeList)
		{
			if("BILLING_LOCATION".equals(pcdpGV.contactMechPurposeTypeId)){
				billingPCDPGV = pcdpGV;
			}else if("SHIPPING_LOCATION".equals(pcdpGV.contactMechPurposeTypeId)){
				shippingPCDPGV = pcdpGV;
			}else if("PRIMARY_EMAIL".equals(pcdpGV.contactMechPurposeTypeId)){
				emailPCDPGV = pcdpGV;
			}else if("PHONE_MOBILE".equals(pcdpGV.contactMechPurposeTypeId)){
				phonePCDPGV = pcdpGV;
			}
				
		}
	}
	
	context.billingPCDPGV=billingPCDPGV;
	context.shippingPCDPGV=shippingPCDPGV;
	context.emailPCDPGV=emailPCDPGV;
	context.phonePCDPGV=phonePCDPGV;
	
	orderRoleList= from("OrderRole").where("partyId", partyId, "roleTypeId", "BILL_TO_CUSTOMER").queryList();
	if(UtilValidate.isNotEmpty(orderRoleList)) {
		orderRoleGV = EntityUtil.getFirst(orderRoleList);
		println("======orderRoleGV=========="+orderRoleGV+"===========");
		orderHeaderAndJobCardList = from("OrderHeaderAndJobCard").where("orderId", orderRoleGV.orderId)orderBy("orderDate DESC", "orderId DESC").queryList();
		context.orderHeaderAndJobCardList=orderHeaderAndJobCardList;
		if(UtilValidate.isNotEmpty(orderHeaderAndJobCardList)) {
			
			orderHeaderAndJobCardGV = EntityUtil.getFirst(orderHeaderAndJobCardList);
			context.orderHeaderAndJobCardGV=orderHeaderAndJobCardGV;
			vehicleModelIds = EntityUtil.getFieldListFromEntityList(orderHeaderAndJobCardList, "vehicleModelId", true);
			//get the customer Details
			vehiclesCond = [];
			vehiclesCond.add(EntityCondition.makeCondition("modelId", EntityOperator.IN, vehicleModelIds));
			System.out.println("vehiclesCond==========%%%%%%%%%%%%%%%=============================="+vehiclesCond);
			vehicleList = from("Vehicle").where(vehiclesCond).orderBy("modelId").queryList();
			System.out.println("vehicleList==========%%%%%%%%%%%%%%%=============================="+vehicleList);
			if(UtilValidate.isNotEmpty(vehicleList)) {
				lastVehicleGV=vehicleList;
				context.lastVehicleGV=lastVehicleGV;
				println("================lastVehicleGV============="+lastVehicleGV);
				context.vehicleList=vehicleList;
			}
		}
	}
		
}
println("===========orderHeaderAndJobCardGV====="+orderHeaderAndJobCardGV+"===========");

//Few Static Values
context.insuranceCompany="HDFC";
context.paymentMode="Cash On Delivery";
context.customerType="Gaadizo";
}


//============================

