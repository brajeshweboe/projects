import java.util.*
import java.sql.Timestamp
import org.apache.ofbiz.entity.*
import org.apache.ofbiz.base.util.*
import org.apache.ofbiz.entity.util.*
import org.apache.ofbiz.entity.condition.EntityConditionBuilder
import org.apache.ofbiz.entity.util.EntityUtil
import org.apache.ofbiz.entity.condition.EntityCondition;
import org.apache.ofbiz.entity.condition.EntityOperator;

module = "JobCardList.groovy"
orderHeader = null;
jobCardId = parameters.jobCardId;
if(UtilValidate.isNotEmpty(jobCardId)) {
    

    orderHeaderAndjobCardGV = null;
    //get the order types
    jobCardList = from("OrderHeaderAndJobCard").where("jobCardId", jobCardId).cache(true).queryList()
    if(UtilValidate.isNotEmpty(jobCardList)) {
    orderHeaderAndjobCardGV = EntityUtil.getFirst(jobCardList);

    orderHeader = from("OrderHeader").where("orderId", orderHeaderAndjobCardGV.orderId).cache(true).queryOne()
    
    if(UtilValidate.isNotEmpty(orderHeader)){
	    //orderHeader = orderHeader;
        
        
	    context.orderHeaderAndjobCardGV = orderHeaderAndjobCardGV;
	    
	    vehicleDetail = from("Vehicle").where("vehicleId", orderHeaderAndjobCardGV.vehicleId).cache(true).queryOne()
	    context.vehicleDetail=vehicleDetail;

	    
	    //get the order items
	    jobCardDetails = from("OrderItem").where("orderId", orderHeader.orderId,"statusId","ITEM_APPROVED").orderBy("orderItemSeqId").queryList()
	    
	   
	    context.jobCardDetails = jobCardDetails
	    productInventoryMap = new HashMap();
	    List<String> orderItemProductIds = EntityUtil.getFieldListFromEntityList(jobCardDetails,"productId",false);
	    if(UtilValidate.isNotEmpty(orderItemProductIds)){
	    	exprBldr = new EntityConditionBuilder()
	    	inventoryCond = exprBldr.AND() {
			    EQUALS(facilityId: userLogin.ownerId)
			    IN(productId: orderItemProductIds)
			}
	    	List<GenericValue> inventoryItems = EntityQuery.use(delegator).from("InventoryItem").where(inventoryCond).queryList();
	    	
	    	if(UtilValidate.isNotEmpty(inventoryItems)){
	    		//TODO:we have to refine this logic based on the secnario
	    		orderItemProductIds.each 
	    		{ 
	    			orderItemProductId ->
	    		    BigDecimal inventroyATP = BigDecimal.ZERO;
		    		String binLoc = null;
	    		    inventoryItems.each
				    { 
		    			inventoryItem ->
		    			    
		    			if(orderItemProductId.equals(inventoryItem.productId)){
		    				
		    				inventroyATP = inventroyATP.add(inventoryItem.availableToPromiseTotal);
		    				binLoc = inventoryItem.locationSeqId;
		    			}
				    }
		    		productInventoryMap.put(orderItemProductId, inventroyATP);
		    		productInventoryMap.put(orderItemProductId+"binLoc", binLoc);
	    		}
	    	}
	    }
	    context.productInventoryMap = productInventoryMap;		
	    orderAdjustmentList = from("OrderAdjustment").where("orderId", orderHeader.orderId).queryList();
	    context.orderAdjustmentList = orderAdjustmentList;
	    
	    
	    orderRoles = from("OrderRole").where("orderId", orderHeaderAndjobCardGV.orderId, "roleTypeId", "SHIP_TO_CUSTOMER").cache(true).queryList();
	    if(UtilValidate.isNotEmpty(orderRoles)){
	        orderRoleGV = EntityUtil.getFirst(orderRoles);
		    partyContactDetailByPurposeList = from("PartyContactDetailByPurpose").where("partyId", orderRoleGV.partyId).cache(true).queryList();
		
		    partyEmail = null;
		    partyAddress = null;
		    partyAndPostalAddressGV = EntityUtil.getFirst(partyContactDetailByPurposeList);
		    context.partyAndPostalAddressGV=partyAndPostalAddressGV;
		    
		    partyContactDetailByPurposeList.each
		    { partyContactDetailByPurpose ->
		        if("PRIMARY_EMAIL".equals(partyContactDetailByPurpose.contactMechPurposeTypeId)) {
		            partyEmail = partyContactDetailByPurpose.infoString;
		        }
		        if("GENERAL_LOCATION".equals(partyContactDetailByPurpose.contactMechPurposeTypeId)) {
		            partyAddress = partyContactDetailByPurpose.address1 + partyContactDetailByPurpose.address2 + partyContactDetailByPurpose.city + partyContactDetailByPurpose.postalCode;
		        }
		    }
		    context.partyAddress = partyAddress;
		    context.partyEmail = partyEmail;
	    }
	
	    // get the order statuses
	    orderStatuses = from("StatusItem").where("statusTypeId", "ORDER_STATUS").orderBy("sequenceId", "description").cache(true).queryList()
	    context.orderStatuses = orderStatuses
    }
    }
    context.orderHeader = orderHeader;
}