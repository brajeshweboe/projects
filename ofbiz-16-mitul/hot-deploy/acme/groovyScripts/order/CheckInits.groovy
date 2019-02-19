import org.apache.ofbiz.service.*
import org.apache.ofbiz.entity.*
import org.apache.ofbiz.entity.condition.*
import org.apache.ofbiz.entity.util.*
import org.apache.ofbiz.base.util.*
import org.apache.ofbiz.order.shoppingcart.*
import org.apache.ofbiz.party.party.PartyWorker
import org.apache.ofbiz.party.contact.ContactHelper
import org.apache.ofbiz.product.catalog.CatalogWorker
import org.apache.ofbiz.product.store.ProductStoreWorker
import org.apache.ofbiz.order.shoppingcart.product.ProductDisplayWorker
import org.apache.ofbiz.order.shoppingcart.product.ProductPromoWorker

println("===============start checkinits groovy=========");
shoppingCart = session.getAttribute("shoppingCart")
//ShoppingCartEvents.destroyCart(request,response);
//shoppingCart.clear();
ownerCompanyId = userLogin.ownerCompanyId;
productStores = from("ProductStore").where("payToPartyId", ownerCompanyId).cache(true).queryList()
productStore = null;
productStoreId=null;
if(UtilValidate.isNotEmpty(productStores)){
    productStore = EntityUtil.getFirst(productStores);
}
println("===38====productStore=="+ownerCompanyId+"====11======"+productStore+"==========");
productStore = productStore;
if(UtilValidate.isEmpty(productStore)){
    println("==41=====productStore=====22======="+productStore+"==========");
    productStore = ProductStoreWorker.getProductStore(request);
}



println("==43=====productStore======33======"+productStore+"==========");
if (productStore) {
    productStoreId=productStore.getString("productStoreId");
    context.defaultProductStore = productStore
    if (productStore.defaultSalesChannelEnumId)
        context.defaultSalesChannel = from("Enumeration").where("enumId", productStore.defaultSalesChannelEnumId).cache(true).queryOne()
}
// Get the Cart
//shoppingCart = session.getAttribute("shoppingCart")
if(UtilValidate.isEmpty(shoppingCart)){
    shoppingCart = ShoppingCartEvents.getCartObject(request);
    println("==43=====productStore============"+shoppingCart.getCurrency()+"==========");
}
println("==4777777=====productStore============"+shoppingCart.getJobCardId()+"==========");
if(UtilValidate.isNotEmpty(shoppingCart)){
    shoppingCart.setProductStoreId(productStoreId);
}

if(UtilValidate.isNotEmpty(shoppingCart)){
    parameters.partyId = shoppingCart.getPartyId();
    println("==52====userLogin.partyId===="+userLogin.partyId+"=====");
    println("==53===parameters.partyId===="+parameters.partyId+"=====");
    if(userLogin.partyId.equals(parameters.partyId)) {
        println("==5555555555555555555555555555555===");
        shoppingCart.setPartyId(null);
        shoppingCart.setPlacingCustomerPartyId(null);
        shoppingCart.setBillToCustomerPartyId(null);
        shoppingCart.setShipToCustomerPartyId(null);
        shoppingCart.setEndUserCustomerPartyId(null);
        shoppingCart.setOrderPartyId(null);
        parameters.partyId = null;
        shoppingCart.clear() 
    }
}
context.shoppingCart = shoppingCart

//salesChannels = from("Enumeration").where("enumTypeId", "ORDER_SALES_CHANNEL").orderBy("sequenceId").cache(true).queryList()
//context.salesChannels = salesChannels

productStores = from("ProductStore").orderBy("productStoreId", "storeName").cache(true).queryList()
context.productStores = productStores

suppliers = from("PartyRoleAndPartyDetail").where("roleTypeId", "SUPPLIER").orderBy("groupName", "partyId").queryList()
context.suppliers = suppliers

organizations = from("PartyAcctgPrefAndGroup").queryList()
context.organizations = organizations

// Set Shipping From the Party 
partyId = null
println("==64=====partyId======1111======"+parameters.partyId+"==========");
partyId = parameters.partyId

if(UtilValidate.isEmpty(partyId)){
    partyId = shoppingCart.getShipToCustomerPartyId();
}
println("==3434343434===22===partyId===="+partyId+"==");

if (partyId) {
    party = from("Person").where("partyId", partyId).queryOne()
    if (party) {
        contactMech = EntityUtil.getFirst(ContactHelper.getContactMech(party, "SHIPPING_LOCATION", "POSTAL_ADDRESS", false))
        if (contactMech) {
            ShoppingCart shoppingCart = ShoppingCartEvents.getCartObject(request)
            shoppingCart.setAllShippingContactMechId(contactMech.contactMechId)
        }
    }
}

vehicles = from("Vehicle").orderBy("manufacturerName").cache(true).queryList()
vehiclesMYear = from("Vehicle").orderBy("year").cache(true).queryList()

vehicleList = [];
vehicleIdList = [];
vehicleMNameList = [];
vehicleVariantList = [];
vehicleList = [];
vehicleModelIdList = [];
vehicleModelNameList=[];
vehicleFList=[];
vehicleYList=[];
vehicleOilCapacityList=[];

if(UtilValidate.isNotEmpty(vehicles)){
    vehicles.each { vehicle ->
        if(!vehicleIdList.contains(vehicle.vehicleId)){
            vehicleIdList.add(vehicle.vehicleId);
            vehicleList.add(vehicle)
        }
        if(!vehicleOilCapacityList.contains(vehicle.oilCapacity)){
            vehicleOilCapacityList.add(vehicle.oilCapacity);
            vehicleList.add(vehicle)
        }
        if(!vehicleMNameList.contains(vehicle.manufacturerName)){
            vehicleMNameList.add(vehicle.manufacturerName);
            vehicleList.add(vehicle)
        }
        if(!vehicleOilCapacityList.contains(vehicle.oilCapacity)){
            vehicleOilCapacityList.add(vehicle.oilCapacity);
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

println("======105======parameters==========="+parameters.emailAddress+"============");
emailGV = null;
/*if(UtilValidate.isNotEmpty(partyId)){
    partyAndContactMechList = from("PartyAndContactMech").where("partyId",partyId).queryList()
    println("======108======parameters==========="+partyAndContactMechList+"============");
    if(UtilValidate.isNotEmpty(partyAndContactMechList)){
        partyAndContactMechList.each { partyAndContactMechGV ->
            if(UtilValidate.isNotEmpty(partyAndContactMechGV.infoString)){
                emailGV = partyAndContactMechGV;
            }
        }
        request.setAttribute("_ERROR_MESSAGE_", "Email already exists in the system.")
        println("====110============================");
        partyAndContactMechSGV = EntityUtil.getFirst(EntityUtil.filterByDate(partyAndContactMechList))
        println("=====112===========================");
        listPartyPostalAddress = delegator.findByAnd("PartyAndPostalAddress", [partyId: partyAndContactMechSGV.partyId], null, true)
        context.partyPostalAddress = EntityUtil.getFirst(listPartyPostalAddress);
        
        jobCardGV = from("JobCard").where("jobCardId",shoppingCart.getJobCardId()).queryOne()
        parameters.emailAddress = parameters.emailAddress;
        println("===121213======"+context.partyPostalAddress+"=======================");
        context.personGV = from("Person").where("partyId", partyAndContactMechSGV.partyId).cache(true).queryOne()
        //partyAndContactMechList = from("PartyAndContactMech").where("partyId",partyId).queryList()
         //partyAndContactMechSGV = EntityUtil.getFirst(EntityUtil.filterByDate(partyAndContactMechList))
        
        //return "error";
if(jobCardGV){        
parameters.vehicleModel=jobCardGV.vehicleModel;
parameters.vehicleId=jobCardGV.vehicleId
parameters.vehicleManufacturer=jobCardGV.vehicleManufacturer
parameters.vehicleFuelType=jobCardGV.vehicleFuelType
parameters.vehicleVariant=jobCardGV.vehicleVariant
parameters.oilCapacity=jobCardGV.oilCapacity
parameters.vehicleRegNo=jobCardGV.registationNumber
parameters.ServiceDate=jobCardGV.serviceDate
parameters.DeliveryDate=jobCardGV.deliveryDate
parameters.lastServiceDate=jobCardGV.lastServiceDate
parameters.vehicleInsuranceComp=jobCardGV.vehicleInsuranceComp
parameters.InsuranceDate=jobCardGV.insuranceExpiryDate
parameters.paymentMode=jobCardGV.paymentMode
parameters.customerType=jobCardGV.customerType
}
    }
}*/

context.emailGV = emailGV
context.vehicleIdList=vehicleIdList;
context.vehicleOilCapacityList=vehicleOilCapacityList;
println("===============end checkinits groovy=========");
context.vehicleList = vehicles
context.vehicleFList=vehicleFList;
context.vehicleModelIdList=vehicleModelIdList;
context.vehicleModelNameList=vehicleModelNameList;
context.vehicleMNameList=vehicleMNameList;
context.vehicleVariantList=vehicleVariantList;
context.vehicleYList=vehicleYList;
geoAssocList = from("GeoAssocAndGeoTo").where("geoIdFrom", "IND").cache(true).queryList();
context.geoAssocList = geoAssocList;