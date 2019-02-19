/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.sql.Timestamp

import org.apache.ofbiz.base.util.*
import org.apache.ofbiz.entity.*
import org.apache.ofbiz.entity.util.*
import org.apache.ofbiz.party.contact.*
import org.apache.ofbiz.order.order.OrderReadHelper
import org.apache.ofbiz.party.content.PartyContentWrapper
import org.apache.ofbiz.entity.util.EntityUtilProperties
import org.apache.ofbiz.party.contact.ContactMechWorker;
System.out.println("======="+parameters.shipmentId+"==============");
addresses = from("PartyAndPostalAddress").where("partyId", "Company").queryList()
GenericValue postalAddress = EntityUtil.getFirst(addresses); 
println("====================="+postalAddress);
context.postalAddress = postalAddress;
//context.orderContactMechValueMaps = ContactMechWorker.getPartyContactMechValueMaps(delegator, "Company", showOld)
//PartyAndPostalAddress
shipment = from('Shipment').where('shipmentId', parameters.shipmentId).queryOne()
println("====================="+postalAddress);
if(shipment.originContactMechId != null){
context.originContactDetail = from('PostalAddress').where('contactMechId', shipment.originContactMechId).queryOne()
}
if(shipment.destinationContactMechId != null){
context.destinationContactDetail = from('PostalAddress').where('contactMechId', shipment.destinationContactMechId).queryOne()
}
