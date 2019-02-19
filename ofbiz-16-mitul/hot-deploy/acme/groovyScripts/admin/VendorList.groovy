import java.util.*
import java.sql.Timestamp
import org.apache.ofbiz.entity.*
import org.apache.ofbiz.base.util.*
import org.apache.ofbiz.entity.util.*
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.condition.*

module = "VendorsList.groovy"

//get the order types
//List<GenericValue> 
vendorsList = from("Party").orderBy("partyId").queryList()

System.out.println("vendorsList========================================"+vendorsList);
context.vendorsList = vendorsList

module = "VendorList.groovy"
partyRoles = from("PartyRole").where("roleTypeId", "SHIP_TO_CUSTOMER").orderBy("partyId").queryList()
context.partyRoles = partyRoles
partyIds = EntityUtil.getFieldListFromEntityList(partyRoles, "partyId", true);

//get the vendor Details
vendorCond = []
vendorCond.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, partyIds));

if(UtilValidate.isNotEmpty(parameters.vendorId) || UtilValidate.isNotEmpty(parameters.vendorName)){
   System.out.println("vendorId========================================"+parameters.vendorId);
   System.out.println("vendorName========================================"+parameters.vendorName);
   vendorId = parameters.vendorId;
   vendorName = parameters.vendorName;
   
    if (vendorId) {
        vendorCond.add(EntityCondition.makeCondition("partyId", EntityOperator.LIKE, "%" + vendorId + "%"))
    }
    if (vendorName) {
        vendorCond.add(EntityCondition.makeCondition("firstName", EntityOperator.LIKE, "%" + vendorName + "%"));
    }
    
    System.out.println("vendorCond========================================"+vendorCond);
    partyAndPersons = from("PartyAndPerson").where(vendorCond).orderBy("partyId").queryList()
} else {
	partyAndPersons = from("PartyAndPerson").where(vendorCond).orderBy("partyId").queryList()
}



System.out.println("partyIds========================================"+partyIds);
context.partyAndPersons = partyAndPersons
System.out.println("partyAndPersons========================================"+partyAndPersons);











