import java.util.*
import java.sql.Timestamp
import org.apache.ofbiz.entity.*
import org.apache.ofbiz.base.util.*
import org.apache.ofbiz.entity.util.*
import org.apache.ofbiz.order.order.OrderReadHelper;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.service.*
import org.apache.ofbiz.entity.*
import org.apache.ofbiz.entity.condition.*
import org.apache.ofbiz.entity.util.*
import org.apache.ofbiz.base.util.*
import com.gaadizo.data.*;
import java.text.SimpleDateFormat;

shoppingCart = session.getAttribute("shoppingCart")
if(UtilValidate.isEmpty(shoppingCart)){
    shoppingCart = ShoppingCartEvents.getCartObject(request);
}
module = "JobCardList.groovy"
orderHeader = null;
orderHeaderAndJobCardGV = null;
println("======jobCard detila===12121212====="+parameters.jobCardId+"==="+parameters.orderId+"========");
if(UtilValidate.isNotEmpty(parameters.orderId)) {
	orderHeaderAndJobCardList = from("OrderHeaderAndJobCard").where("orderId", parameters.orderId).queryList()
			if(UtilValidate.isNotEmpty(orderHeaderAndJobCardList)) {
				orderHeaderAndJobCardGV = EntityUtil.getFirst(orderHeaderAndJobCardList);
				parameters.jobCardId = orderHeaderAndJobCardGV.jobCardId;
			}
}
jobCardId = parameters.jobCardId?parameters.jobCardId:shoppingCart.getJobCardId();
orderId = parameters.orderId;
println("======jobCard detila===12121212====="+parameters.jobCardId+"======"+jobCardId+"=====");
if(UtilValidate.isNotEmpty(jobCardId)) {
    
    println("======jobCard detila===28282828======orderId=========="+orderId);

    jobCardList = null;
    //get the order types
    if(UtilValidate.isEmpty(orderHeaderAndJobCardGV)){
	    if(UtilValidate.isNotEmpty(orderId)) {
	        jobCardList = from("OrderHeaderAndJobCard").where("jobCardId", jobCardId,"productStoreId",userLogin.ownerId).queryList()
	        		println("==353535====jobCard detila===12121212================");
	        orderHeaderAndJobCardGV = EntityUtil.getFirst(jobCardList);
	    } else {
	    	println("====38383838383==jobCard detila===12121212================");
	    	orderHeaderAndJobCardGV = from("JobCard").where("jobCardId", jobCardId,"serviceProviderId",userLogin.ownerId).queryOne()
	    }
    }
    orderHeaderAndjobCardGV = orderHeaderAndJobCardGV;
    println("======jobCardId=====181818====="+orderHeaderAndjobCardGV+"===========");
    vehicledList = from("Vehicle").queryList();
    println("======vehicledList=====535353181818====="+vehicledList.size()+"===========");
    context.vehicledList=vehicledList;    
    context.orderHeaderAndjobCardGV = orderHeaderAndjobCardGV;
    context.orderHeaderAndJobCard = orderHeaderAndjobCardGV;
	    
	vehicleDetail = from("Vehicle").where("vehicleId", orderHeaderAndjobCardGV.vehicleId).queryOne()
	context.vehicleDetail=vehicleDetail;
	    
    if(UtilValidate.isNotEmpty(orderHeaderAndjobCardGV)) {
   		 
    	context.personGV = from("Person").where("partyId", orderHeaderAndjobCardGV.customerId).cache(true).queryOne()
    	orderHeader = from("OrderHeader").where("orderId", orderHeaderAndjobCardGV.orderId).queryOne()
    	if(UtilValidate.isNotEmpty(orderHeaderAndjobCardGV.orderId)){
	   	 //get the order items
	   	 jobCardDetails = from("OrderItem").where("orderId", orderHeaderAndjobCardGV.orderId,"statusId","ITEM_APPROVED").orderBy("orderItemSeqId").queryList()
	  	  context.jobCardDetails = jobCardDetails
	   	 jobCardOrderAdjustmentList = from("OrderAdjustment").where("orderId", orderHeaderAndjobCardGV.orderId).queryList()
	     	OrderReadHelper orh = new OrderReadHelper(orderHeader,jobCardOrderAdjustmentList,jobCardDetails);
	        
	        println("=======orh.getOrderGrandTotal()==========="+orh.getOrderGrandTotal()+"===================");
	        context.orh = orh;
	   			 orderAdjustmentList = from("OrderAdjustment").where("orderId", orderHeaderAndjobCardGV.orderId).queryList();
	   			 context.orderAdjustmentList = orderAdjustmentList;
	    
	    
	    		println("======orderHeaderAndjobCardGV.orderId=========="+orderHeaderAndjobCardGV.orderId+"===========");
	    		orderCustomerId = orderHeaderAndjobCardGV.customerId;
	    		println("==808080808====orderHeaderAndjobCardGV.orderId=========="+orderHeaderAndjobCardGV.customerId+"===========");
	    if(UtilValidate.isNotEmpty(orderCustomerId)){
		    partyContactDetailByPurposeList = from("PartyContactDetailByPurpose").where("partyId", orderCustomerId).queryList();
		    //partyContactDetailByPurposeList = from("PartyContactMechPurpose").where("partyId", orderRoleGV.partyId).queryList();
		
		    partyEmail = null;
		    partyAddress = null;
		    //partyAndPostalAddressGV = EntityUtil.getFirst(partyContactDetailByPurposeList);
		    partyAndPostalAddressGV = from("PartyAndPostalAddress").where("partyId", orderHeaderAndjobCardGV.customerId,"contactMechTypeId","POSTAL_ADDRESS").queryFirst();
		    context.partyAndPostalAddressGV=partyAndPostalAddressGV;
		    
		    partyContactDetailByPurposeList.each
		    { partyContactDetailByPurpose ->
		        if("PRIMARY_EMAIL".equals(partyContactDetailByPurpose.contactMechPurposeTypeId)) {
		        	context.partyEmail = partyContactDetailByPurpose.infoString;
		        	println("===1===orderRoleGV======"+context.partyEmail+"===========");
		        }
		        if("BILLING_LOCATION".equals(partyContactDetailByPurpose.contactMechPurposeTypeId)) {
		        	context.partyAddress = partyContactDetailByPurpose;
		        	println("===2===orderRoleGV==========="+context.partyAddress+"======");
		        }
		        if("SHIPPING_LOCATION".equals(partyContactDetailByPurpose.contactMechPurposeTypeId)) {
                    partySAddress = partyContactDetailByPurpose;
                    context.partySAddress = partySAddress;
                    println("==3====orderRoleGV=================");
                }
		    }
	    }
	
	    vehicleList = from("Vehicle").where("vehicleId", orderHeaderAndjobCardGV.vehicleId).queryList();
	
	    vehicleGV = EntityUtil.getFirst(vehicleList);
	    context.vehicleGV=vehicleGV;
	
	    
	
	    // get the order statuses
	    orderStatuses = from("StatusItem").where("statusTypeId", "ORDER_STATUS").orderBy("sequenceId", "description").queryList()
	    context.orderStatuses = orderStatuses
    }
    }
    context.orderHeader = orderHeader;
    vehicles = from("Vehicle").orderBy("manufacturerName").cache(true).queryList()
    vehiclesMYear = from("Vehicle").orderBy("year").cache(true).queryList()

    vehicleList = [];
    vehicleMNameList = [];
	vehicleModelNameList = [];
    vehicleVariantList = [];
    vehicleList = [];
    vehicleModelIdList = [];
    vehicleFList=[];
    vehicleYList=[];
    if(UtilValidate.isNotEmpty(vehicles)){
        vehicles.each { vehicle ->
            if(!vehicleMNameList.contains(vehicle.manufacturerName)){
                vehicleMNameList.add(vehicle.manufacturerName);
                vehicleList.add(vehicle)
            }
			if(!vehicleModelNameList.contains(vehicle.modelName)){
                vehicleModelNameList.add(vehicle.modelName);
                vehicleList.add(vehicle)
            }
            if(!vehicleVariantList.contains(vehicle.variant)){
                vehicleVariantList.add(vehicle.variant);
                vehicleList.add(vehicle)
            }
            if(!vehicleModelIdList.contains(vehicle.modelId)){
                vehicleModelIdList.add(vehicle.modelId);
                vehicleList.add(vehicle)
            }
        }
    }
    if(UtilValidate.isNotEmpty(vehiclesMYear)){
        vehiclesMYear.each { vehicleMYear ->
            if(!vehicleYList.contains(vehicleMYear.year)){
                vehicleYList.add(vehicleMYear.year);
            }
        }
    }

 //   context.jobCardHistoryList = from("OrderHeaderAndJobCard").where("customerId", orderHeaderAndjobCardGV.customerId,"statusId","ORDER_COMPLETED").queryList()
    println("=======jobCardHistoryList===================="+context.jobCardHistoryList+"==================");
    
    
    context.vehicleList = vehicles
    context.vehicleFList=vehicleFList;
    context.vehicleModelIdList=vehicleModelIdList;
    context.vehicleMNameList=vehicleMNameList;
    context.vehicleVariantList=vehicleVariantList;
    context.vehicleYList=vehicleYList;
	context.vehicleModelNameList=vehicleModelNameList;
	
}