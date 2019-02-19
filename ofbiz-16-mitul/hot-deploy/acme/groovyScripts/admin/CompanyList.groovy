import java.util.*
import java.sql.Timestamp
import org.apache.ofbiz.entity.*
import org.apache.ofbiz.base.util.*
import org.apache.ofbiz.entity.util.*
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.condition.*

module = "CustomersList.groovy"
compCond = [];

if(UtilValidate.isNotEmpty(parameters.partyId)){
    compCond.add(EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("partyId")), EntityOperator.EQUALS, parameters.partyId.toUpperCase()))
}
if(UtilValidate.isNotEmpty(parameters.companyName)){
    compCond.add(EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("groupName")), EntityOperator.EQUALS, parameters.companyName.toUpperCase()))
}

if(UtilValidate.isNotEmpty(parameters.companyName)){
    compCond.add(EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("groupName")), EntityOperator.EQUALS, parameters.companyName.toUpperCase()))
    if (CustomerName) {
        orderHeaderAndJobCardCond.add(EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("firstName")), EntityOperator.LIKE, "%" + CustomerName.toUpperCase() + "%"));//,EntityCondition.makeCondition("lastName", EntityOperator.LIKE, "%" + CustomerName + "%"),EntityOperator.OR])
    }
}



customersList = from("PartyRoleAndPartyDetail").where("roleTypeId","BILL_FROM_VENDOR").orderBy("partyId").queryList()


System.out.println("customersList========================================"+customersList);
context.customersList = customersList












