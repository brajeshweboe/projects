import java.util.*
import java.sql.Timestamp
import org.apache.ofbiz.entity.*
import org.apache.ofbiz.base.util.*
import org.apache.ofbiz.entity.util.*
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.condition.*

System.out.println("==1====parameters========="+parameters.selectedJobCardId+"===============");
System.out.println("==2====parameters========="+parameters.selectedOrderId+"===============");
System.out.println("==3====parameters========="+parameters.selectedVehicleId+"===============");


println("===============add item in job card groovy=========");
//shoppingCart = session.getAttribute("shoppingCart")
//if(UtilValidate.isEmpty(shoppingCart)){
  //  shoppingCart = ShoppingCartEvents.getCartObject(request);
  //  println("==43=====productStore============"+shoppingCart.getCurrency()+"==========");
//}
//System.out.println("==Shopping Cart size====="+shoppingCart.items().size()+"===============");
 //shoppingCart.setJobCardId(parameters.selectedJobCardId)
//    shoppingCart.setVehicleId(parameters.selectedVehicleId)
println("==4777777=====productStore============"+shoppingCart.getJobCardId()+"==========");

if(UtilValidate.isNotEmpty(parameters.selectedOrderId)){
parameters.selectedOrderId = parameters.selectedOrderId;
    //if order not available then we have to set jobcard and vehicle in the card
} else{
    // call load card from order
}
parameters.selectedJobCardId=parameters.selectedJobCardId;
parameters.selectedVehicleId=parameters.selectedVehicleId;

vehicleList = from("Vehicle").where("ownerId", userLogin.ownerCompanyId).orderBy("modelName").queryList()
context.vehicleList=vehicleList;
fastServiceList = from("ProductFacility").where("productFacilityTypeId","FASTSERVICE","ownerId", userLogin.ownerCompanyId).orderBy("productFacilitySeqId").queryList()
if(UtilValidate.isNotEmpty(fastServiceList)){
    fastServiceIds = EntityUtil.getFieldListFromEntityList(fastServiceList,"productId",true);
    fastServiceProductList = from("Product").where(EntityCondition.makeCondition("productId", EntityOperator.IN, fastServiceIds)).queryList()
    context.fastServiceProductList = fastServiceProductList;    
    
}
if(UtilValidate.isNotEmpty(parameters.selectedVehicleId)){
parameters.selectedVehicleId = parameters.selectedVehicleId;
System.out.println("==3====parameters========="+parameters.selectedVehicleId+"===============");
System.out.println("==3====parameters========="+userLogin.ownerCompanyId+"===============");
	productVehiclePriceList = from("ProductVehiclePrice").where("productStoreId", "GAADIZO_STORE_ID","vehicleId",parameters.selectedVehicleId).queryList()
    context.productVehiclePriceList = productVehiclePriceList;
}

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

performFindListCtx = [:];
performFindListCtx.productId = parameters.productId;
performFindListCtx.vehicleId = parameters.vehicleId;
performFindListCtx.productStoreId = userLogin.owneCompanyId;
performFindListCtx.facilityId = userLogin.owneCompanyId;

Set selectedProductList = new HashSet();

resultMap = runService('performFindList', [inputFields : performFindListCtx, entityName : "ProductVehiclePrice", userLogin : userLogin])
        System.out.println("==43====resultMap========="+resultMap.list.size()+"===============");
selectedProductList.addAll(resultMap.list);
/*
<set field="entityName" value="Product"/>
            <service service-name="performFindList" result-map="result" result-map-list="list">
                <field-map field-name="inputFields" from-field="requestParameters"/>
                <field-map field-name="entityName" from-field="entityName"/>
                <field-map field-name="orderBy" from-field="parameters.sortField"/>
                <field-map field-name="viewIndex" from-field="viewIndex"/>
                <field-map field-name="viewSize" from-field="viewSize"/>
            </service> */

