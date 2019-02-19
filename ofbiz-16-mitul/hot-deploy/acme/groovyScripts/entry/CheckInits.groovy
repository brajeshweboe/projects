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

println("==3434343434==1====CheckInit.groovy=======");

productStore = ProductStoreWorker.getProductStore(request)
if (productStore) {
    context.defaultProductStore = productStore
    if (productStore.defaultSalesChannelEnumId)
        context.defaultSalesChannel = from("Enumeration").where("enumId", productStore.defaultSalesChannelEnumId).cache(true).queryOne()
}
// Get the Cart



shoppingCart = session.getAttribute("shoppingCart")
context.shoppingCart = shoppingCart

salesChannels = from("Enumeration").where("enumTypeId", "ORDER_SALES_CHANNEL").orderBy("sequenceId").cache(true).queryList()
context.salesChannels = salesChannels

productStores = from("ProductStore").orderBy("productStoreId", "storeName").cache(true).queryList()
context.productStores = productStores

suppliers = from("PartyRoleAndPartyDetail").where("roleTypeId", "SUPPLIER").orderBy("groupName", "partyId").queryList()
context.suppliers = suppliers

organizations = from("PartyAcctgPrefAndGroup").queryList()
context.organizations = organizations

// Set Shipping From the Party 
partyId = null

partyId = parameters.partyId

println("==3434343434===22===partyId===="+partyId+"==");
println("==3434343434==33====partyId===="+shoppingCart.getShipToCustomerPartyId()+"==");
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
vehicleMNameList = [];
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


context.vehicleList = vehicles
context.vehicleFList=vehicleFList;
context.vehicleModelIdList=vehicleModelIdList;
context.vehicleMNameList=vehicleMNameList;
context.vehicleVariantList=vehicleVariantList;
context.vehicleYList=vehicleYList;
