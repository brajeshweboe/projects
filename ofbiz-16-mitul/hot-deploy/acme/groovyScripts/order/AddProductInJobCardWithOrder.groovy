import java.util.*
import java.sql.Timestamp
import org.apache.ofbiz.entity.*
import org.apache.ofbiz.base.util.*
import org.apache.ofbiz.entity.util.*
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.condition.*

System.out.println("==1====parameters========="+parameters.jobCardId+"===============");
System.out.println("==2====parameters========="+parameters.orderId+"===============");
System.out.println("==3====parameters========="+parameters.vehicleId+"===============");


println("=============add product in job card with order========");
if(UtilValidate.isNotEmpty(userLogin)){
if(UtilValidate.isNotEmpty(parameters.orderId)){
parameters.orderId = parameters.orderId;
    //if order not available then we have to set jobcard and vehicle in the card
	orderItemGVList = from("OrderItem").where("orderId",parameters.orderId).queryList()
	context.orderItemGVList = orderItemGVList;
} else{
    // call load card from order
}
parameters.jobCardId=parameters.jobCardId;
parameters.vehicleId=parameters.vehicleId;

vehicleList = from("Vehicle").where("ownerId", userLogin.ownerCompanyId).orderBy("modelName").queryList();
context.vehicleList=vehicleList;

fastServiceList = from("ProductFacility").where("productFacilityTypeId","FASTSERVICE","ownerId", userLogin.ownerCompanyId).orderBy("productFacilitySeqId").queryList()
if(UtilValidate.isNotEmpty(fastServiceList)){
    fastServiceIds = EntityUtil.getFieldListFromEntityList(fastServiceList,"productId",true);
    fastServiceProductList = from("Product").where(EntityCondition.makeCondition("productId", EntityOperator.IN, fastServiceIds)).queryList()
    context.fastServiceProductList = fastServiceProductList;    
    
}
List productFacilityInventoryItemList = new ArrayList();
Map productFacilityInventoryItemMap = new HashMap();
System.out.println("==3====parameters========="+parameters.vehicleId+"======"+userLogin.ownerCompanyId+"=============");
if(UtilValidate.isNotEmpty(parameters.vehicleId)){
	System.out.println("==3====parameters========="+userLogin.ownerCompanyId+"===============");
	//TODO:We have to 
	productVehiclePriceList = from("ProductVehiclePrice").where("productStoreId", userLogin.ownerId,"vehicleId",parameters.vehicleId).queryList()
			System.out.println("==3====productVehiclePriceList========="+productVehiclePriceList+"===============");
	productIdListForInventory = EntityUtil.getFieldListFromEntityList(productVehiclePriceList,"productId",true);
	
	List<EntityCondition> productPriceEcList = new LinkedList<EntityCondition>();
    productPriceEcList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIdListForInventory));
    productPriceEcList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, userLogin.ownerCompanyId));
    productPriceEcList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, userLogin.ownerCompanyId));
    System.out.println("==50====productPriceEcList========"+productPriceEcList+"===============");
    productFacilityInventoryItemList = from("InventoryItemAndDetail").where(EntityCondition.makeCondition(productPriceEcList, EntityOperator.AND)).queryList();
    System.out.println("==52====productFacilityInventoryItemMap========"+productFacilityInventoryItemList+"===============");
    context.productVehiclePriceList = productVehiclePriceList;
}
System.out.println("==55====productFacilityInventoryItemMap========"+productFacilityInventoryItemList+"===============");

productFacilityList = from("ProductFacility").where("ownerId", userLogin.ownerCompanyId).orderBy("productFacilitySeqId").queryList()

fastServiceList = new ArrayList<GenericValue>();
otherServiceList = new ArrayList<GenericValue>();

if(UtilValidate.isNotEmpty(productFacilityList)){
    for(GenericValue productFacility : productFacilityList) {
        if("FASTSERVICE".equals(productFacility.productFacilityTypeId)) {
            fastServiceList.add(productFacility);
        }/* else {
            otherServiceList.add(productFacility);
        } */
    }
    productFacilityList.removeAll(fastServiceList);
}

context.otherServiceList = productFacilityList;
context.fastServiceList = fastServiceList;
context.productFacilityList = fastServiceList;

fastServiceList = from("ProductFacility").where("productFacilityTypeId","FASTSERVICE","ownerId", userLogin.ownerCompanyId).orderBy("productFacilitySeqId").queryList()
context.fastServiceList = fastServiceList;
System.out.println("==43====resultMap========="+fastServiceList+"===============");
Set selectedProductList = new HashSet();
Set selectedProductSet = new HashSet();
performFindListCtx = [:];
performFindListCtx.productId = parameters.productId;
performFindListCtx.vehicleId = parameters.vehicleId;
performFindListCtx.productStoreId = userLogin.ownerCompanyId;
performFindListCtx.facilityId = userLogin.ownerCompanyId;

resultMap = runService('performFindList', [inputFields : performFindListCtx, entityName : "ProductVehiclePrice", userLogin : userLogin])
System.out.println("==76====resultMap========="+resultMap.list+"===============");
productIdListForInventory = EntityUtil.getFieldListFromEntityList(resultMap.list,"productId",true);
System.out.println("==50====productIdListForInventory========"+productIdListForInventory+"===============");
List<EntityCondition> productPriceEcList = new LinkedList<EntityCondition>();
productPriceEcList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIdListForInventory));
productPriceEcList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, userLogin.ownerCompanyId));
productPriceEcList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, userLogin.ownerCompanyId));
System.out.println("==50====productPriceEcList========"+productPriceEcList+"===============");
productFacilityInventoryItemList = from("InventoryItem").where(EntityCondition.makeCondition(productPriceEcList, EntityOperator.AND)).queryList();
System.out.println("==52====productFacilityInventoryItemMap========"+productFacilityInventoryItemList+"===============");
if(UtilValidate.isNotEmpty(productFacilityInventoryItemList)){
	System.out.println("==56====productFacilityInventoryItemMap========"+productFacilityInventoryItemMap+"===============");
	
	for(GenericValue productFacilityInventoryItem : productFacilityInventoryItemList) {
		productFacilityInventoryItemMap.put(productFacilityInventoryItem.productId,productFacilityInventoryItem);
    }
	System.out.println("==59====productFacilityInventoryItemMap========"+productFacilityInventoryItemMap+"===============");
	context.productFacilityInventoryItemMap = productFacilityInventoryItemMap;
}

Iterator productVPItr = resultMap.list.iterator()
while (productVPItr.hasNext()) {
	productVP = productVPItr.next();
	if(selectedProductList.contains(productVP.productId)){
		
	}
}
System.out.println("==43====resultMap========="+resultMap.list.size()+"===============");
selectedProductList.addAll(resultMap.list);
context.selectedProductList = selectedProductList;
/*
<set field="entityName" value="Product"/>
            <service service-name="performFindList" result-map="result" result-map-list="list">
                <field-map field-name="inputFields" from-field="requestParameters"/>
                <field-map field-name="entityName" from-field="entityName"/>
                <field-map field-name="orderBy" from-field="parameters.sortField"/>
                <field-map field-name="viewIndex" from-field="viewIndex"/>
                <field-map field-name="viewSize" from-field="viewSize"/>
            </service> */


}