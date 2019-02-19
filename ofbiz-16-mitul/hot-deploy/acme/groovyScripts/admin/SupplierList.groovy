import java.util.*
import java.sql.Timestamp
import org.apache.ofbiz.entity.*
import org.apache.ofbiz.base.util.*
import org.apache.ofbiz.entity.util.*
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.condition.*

module = "SuppliersList.groovy"

//get the order types
//List<GenericValue> 
suppliersList = from("Party").orderBy("partyId").queryList()

System.out.println("suppliersList========================================"+suppliersList);
context.suppliersList = suppliersList

module = "SupplierList.groovy"
partyRoles = from("PartyRole").where("roleTypeId", "SHIP_TO_CUSTOMER").orderBy("partyId").queryList()
context.partyRoles = partyRoles
partyIds = EntityUtil.getFieldListFromEntityList(partyRoles, "partyId", true);

//get the supplier Details
supplierCond = []
supplierCond.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, partyIds));

if(UtilValidate.isNotEmpty(parameters.supplierId) || UtilValidate.isNotEmpty(parameters.supplierName)){
   System.out.println("supplierId========================================"+parameters.supplierId);
   System.out.println("supplierName========================================"+parameters.supplierName);
   supplierId = parameters.supplierId;
   supplierName = parameters.supplierName;
   
    if (supplierId) {
        supplierCond.add(EntityCondition.makeCondition("partyId", EntityOperator.LIKE, "%" + supplierId + "%"))
    }
    if (supplierName) {
        supplierCond.add(EntityCondition.makeCondition("firstName", EntityOperator.LIKE, "%" + supplierName + "%"));
    }
    
    System.out.println("supplierCond========================================"+supplierCond);
    partyAndPersons = from("PartyAndPerson").where(supplierCond).orderBy("partyId").queryList()
} else {
	partyAndPersons = from("PartyAndPerson").where(supplierCond).orderBy("partyId").queryList()
}



System.out.println("partyIds========================================"+partyIds);
context.partyAndPersons = partyAndPersons
System.out.println("partyAndPersons========================================"+partyAndPersons);











