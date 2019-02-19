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

import java.util.*
import java.sql.Timestamp
import org.apache.ofbiz.entity.*
import org.apache.ofbiz.base.util.*
import org.apache.ofbiz.entity.util.*
import org.apache.ofbiz.entity.GenericValue;

module = "JobCardList.groovy"

//get the order types
//List<GenericValue> 
jobCardList = from("OrderHeaderAndJobCard").orderBy("orderId").queryList()
/*//Adding static values for testing only. 
jobCardList = new ArrayList<Map>();
for(i=0;i<10;i++) {
Map jobCard = new HashMap();
jobCard.orderId = "100101";
jobCard.jobCardId = "100101";
jobCard.customerName = "100101";
jobCard.grandTotal = "999.99";
jobCard.pickup = "66";
jobCard.serviceDate = "20 March 2018";
jobCard.vehicleCode = "ZK100101";
jobCard.vehicleModel = "MAR100101";
jobCard.registationNumber = "REG100101";
jobCard.status = "CONFIRM";
jobCardList.add(jobCard);
}*/
