import java.util.*
import java.sql.Timestamp
import org.apache.ofbiz.entity.*
import org.apache.ofbiz.base.util.*
import org.apache.ofbiz.entity.util.*
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.condition.*

module = "CustomersList.groovy"

//get the order types
//List<GenericValue> 
customersList = from("Party").orderBy("partyId").queryList()

System.out.println("customersList========================================"+customersList);
context.customersList = customersList

module = "CustomerList.groovy"
partyRoles = from("PartyRole").where("roleTypeId", "SHIP_TO_CUSTOMER").orderBy("partyId").queryList()
context.partyRoles = partyRoles
partyIds = EntityUtil.getFieldListFromEntityList(partyRoles, "partyId", true);

//get the customer Details
customerCond = []
customerCond.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, partyIds));

if(UtilValidate.isNotEmpty(parameters.customerId) || UtilValidate.isNotEmpty(parameters.customerName)){
   System.out.println("customerId========================================"+parameters.customerId);
   System.out.println("customerName========================================"+parameters.customerName);
   customerId = parameters.customerId;
   customerName = parameters.customerName;
   
    if (customerId) {
        customerCond.add(EntityCondition.makeCondition("partyId", EntityOperator.LIKE, "%" + customerId + "%"))
    }
    if (customerName) {
        customerCond.add(EntityCondition.makeCondition("firstName", EntityOperator.LIKE, "%" + customerName + "%"));
    }
    
    System.out.println("customerCond========================================"+customerCond);
    partyAndPersons = from("PartyAndPerson").where(customerCond).orderBy("partyId").queryList()
} else {
	partyAndPersons = from("PartyAndPerson").where(customerCond).orderBy("partyId").queryList()
}



System.out.println("partyIds========================================"+partyIds);
context.partyAndPersons = partyAndPersons
System.out.println("partyAndPersons========================================"+partyAndPersons);











