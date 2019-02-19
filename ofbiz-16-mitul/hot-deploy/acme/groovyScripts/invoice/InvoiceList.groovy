import java.util.*
import java.sql.Timestamp
import org.apache.ofbiz.entity.*
import org.apache.ofbiz.base.util.*
import org.apache.ofbiz.entity.util.*
import org.apache.ofbiz.entity.GenericValue;

module = "InvoiceList.groovy"

invoiceList = from("InvoiceDetailExport").where("partyIdFrom",userLogin.ownerCompanyId).orderBy("invoiceDate").queryList()
println("====invoiceList============"+invoiceList+"======================");
context.invoiceList = invoiceList

