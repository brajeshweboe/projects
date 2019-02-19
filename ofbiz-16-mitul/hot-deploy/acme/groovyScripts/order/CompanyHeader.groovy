 // this script is used to get the company's logo header information for orders, invoices, and returns.  It can either take order, invoice, returnHeader from
 // parameters or use orderId, invoiceId, or returnId to look them up.
 // if none of these parameters are available then fromPartyId is used or "ORGANIZATION_PARTY" from general.properties as fallback

import java.sql.Timestamp

import org.apache.ofbiz.base.util.*
import org.apache.ofbiz.entity.*
import org.apache.ofbiz.entity.util.*
import org.apache.ofbiz.party.contact.*
import org.apache.ofbiz.order.order.OrderReadHelper
import org.apache.ofbiz.party.content.PartyContentWrapper
import org.apache.ofbiz.entity.util.EntityUtilProperties
import org.apache.ofbiz.party.contact.ContactMechWorker;

jobCardId = parameters.jobCardId
orderId = parameters.orderId

fromPartyId = parameters.fromPartyId

//======================================================start Company information=====================================================================================
// the logo
partyGroup = from("PartyGroup").where("partyId", userLogin.ownerCompanyId).queryOne()
logoImageUrl = null;
if (partyGroup) {
	 if (partyGroup.logoImageUrl) {
         logoImageUrl = partyGroup.logoImageUrl
     } else{
    	 partyContentWrapper = new PartyContentWrapper(dispatcher, partyGroup, locale, EntityUtilProperties.getPropertyValue("content", "defaultMimeType", "text/html; charset=utf-8", delegator))
    	partyContent = partyContentWrapper.getFirstPartyContentByType(partyGroup.partyId , partyGroup, "LGOIMGURL", delegator)
    	if (partyContent) {
    		logoImageUrl = "/content/control/stream?contentId=" + partyContent.contentId
    	}
     }
    if(UtilValidate.isEmpty(logoImageUrl)){
    	logoImageUrl = "/erpTheme/images/logo/companylogo.png";
    }
    println("=========logoImageUrl=============="+logoImageUrl+"=======================");
    context.logoImageUrl = logoImageUrl
}
//If logoImageUrl not null then only set it to context else it will override the default value "/images/ofbiz_powered.gif"
/*if (logoImageUrl) {
    context.logoImageUrl = logoImageUrl
}
*/
// the company name
companyName = "Default Company"
if (partyGroup?.groupName) {
    companyName = partyGroup.groupName
}
context.companyName = companyName

// the address
addresses = from("PartyContactWithPurpose")
                .where("partyId", userLogin.ownerCompanyId, "contactMechPurposeTypeId", "GENERAL_LOCATION")
                .filterByDate("contactFromDate", "contactThruDate", "purposeFromDate", "purposeThruDate")
                .queryList()
address = null
if (addresses) {
    address = from("PostalAddress").where("contactMechId", addresses[0].contactMechId).queryOne()
}
if (address)    {
   // get the country name and state/province abbreviation
   country = address.getRelatedOne("CountryGeo", true)
   if (country) {
      context.countryName = country.get("geoName", locale)
   }
   stateProvince = address.getRelatedOne("StateProvinceGeo", true)
   if (stateProvince) {
       context.stateProvinceAbbr = stateProvince.abbreviation
   }
}
context.postalAddress = address

//telephone
phones = from("PartyContactWithPurpose")
             .where("partyId", userLogin.ownerCompanyId, "contactMechPurposeTypeId", "PRIMARY_PHONE")
             .filterByDate("contactFromDate", "contactThruDate", "purposeFromDate", "purposeThruDate")
             .queryList()
if (phones) {
    context.phone = from("TelecomNumber").where("contactMechId", phones[0].contactMechId).queryOne()
}

// Fax
faxNumbers = from("PartyContactWithPurpose")
                 .where("partyId", userLogin.ownerCompanyId, "contactMechPurposeTypeId", "FAX_NUMBER")
                 .filterByDate("contactFromDate", "contactThruDate", "purposeFromDate", "purposeThruDate")
                 .queryList()
if (faxNumbers) {
    context.fax = from("TelecomNumber").where("contactMechId", faxNumbers[0].contactMechId).queryOne()
}

//Email
emails = from("PartyContactWithPurpose")
             .where("partyId", userLogin.ownerCompanyId, "contactMechPurposeTypeId", "PRIMARY_EMAIL")
             .filterByDate("contactFromDate", "contactThruDate", "purposeFromDate", "purposeThruDate")
             .queryList()
if (emails) {
    context.email = from("ContactMech").where("contactMechId", emails[0].contactMechId).queryOne()
} else {    //get email address from party contact mech
    selContacts = from("PartyContactMech")
                      .where("partyId", userLogin.ownerCompanyId).filterByDate(nowTimestamp, "fromDate", "thruDate")
                      .queryList()
    if (selContacts) {
        i = selContacts.iterator()
        while (i.hasNext())    {
            email = i.next().getRelatedOne("ContactMech", false)
            if ("ELECTRONIC_ADDRESS".equals(email.contactMechTypeId))    {
                context.email = email
                break
            }
        }
    }
}
//=============================================================end Company information================================================================================================
println("=====jobCardId===================="+jobCardId+"=====================");
println("=====orderId===================="+orderId+"=====================");
println("=====userLogin.ownerCompanyId==========000000=========="+userLogin.ownerCompanyId+"=====================");

if (jobCardId && orderId && userLogin.ownerCompanyId){
println("====114===================");
    orderHeaderAndJobCardList = from("OrderHeaderAndJobCard").where("orderId", orderId,"jobCardId",jobCardId).queryList();
    GenericValue orderHeaderAndJobCard = EntityUtil.getFirst(orderHeaderAndJobCardList);
    println("====114===777orderHeaderAndJobCard====09090909090===="+orderHeaderAndJobCard.customerId+"=====");
    if(orderHeaderAndJobCard){
        partyAndPostalAddressList = from("PartyAndPostalAddress").where("partyId", orderHeaderAndJobCard.customerId).orderBy("fromDate").filterByDate().queryList()
        GenericValue partyAndPostalAddressGV = EntityUtil.getFirst(partyAndPostalAddressList);    
        context.partyAndPostalAddressGV = partyAndPostalAddressGV;
        println("====partyAndPostalAddressGV========"+partyAndPostalAddressGV+"===========");
        context.orderHeaderAndJobCard=orderHeaderAndJobCard;
        println("====orderHeaderAndJobCard======"+orderHeaderAndJobCard+"=============");
    }
    context.orderHeaderAndItems = from("OrderHeaderAndItems").where("orderId", orderId).queryList();
    
    cmvm = ContactMechWorker.getOrderContactMechValueMaps(delegator, orderId);
    context.orderContactMechValueMaps = cmvm;
    
}
context.partyGroup=partyGroup;