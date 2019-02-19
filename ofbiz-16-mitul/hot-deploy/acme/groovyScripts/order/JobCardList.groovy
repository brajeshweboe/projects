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
import org.apache.ofbiz.entity.condition.*

module = "JobCardList.groovy"
jobCardList = null;
//get the order types
//List<GenericValue> 
if(UtilValidate.isNotEmpty(parameters.jobCardId) || UtilValidate.isNotEmpty(parameters.fromDate) || UtilValidate.isNotEmpty(parameters.thruDate) || UtilValidate.isNotEmpty(parameters.CustomerName)){
   //jobCardList = from("OrderHeaderAndJobCard").where("orderId",parameters.bookingId).orderBy("orderId").queryList()
   System.out.println("fromDate========================================"+parameters.fromDate);
   System.out.println("thruDate========================================"+parameters.thruDate);
   System.out.println("bookingId========================================"+parameters.bookingId);
   System.out.println("CustomerName========================================"+parameters.CustomerName);
   System.out.println("jobCardId========================================"+parameters.jobCardId);
	fromDate = parameters.fromDate
    thruDate = parameters.thruDate
    orderId = parameters.bookingId
	jobCardId = parameters.jobCardId
	
    CustomerName = parameters.CustomerName.trim()
    orderHeaderAndJobCardCond = []
    if (jobCardId) {
        orderHeaderAndJobCardCond.add(EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("jobCardId")), EntityOperator.EQUALS, jobCardId.toUpperCase()))
    }
	if (orderId) {
        orderHeaderAndJobCardCond.add(EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("orderId")), EntityOperator.EQUALS, orderId.toUpperCase()))
    }
    if (CustomerName) {
        orderHeaderAndJobCardCond.add(EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("firstName")), EntityOperator.LIKE, "%" + CustomerName.toUpperCase() + "%"));//,EntityCondition.makeCondition("lastName", EntityOperator.LIKE, "%" + CustomerName + "%"),EntityOperator.OR])
    }
    if (fromDate) {
        orderHeaderAndJobCardCond.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, Timestamp.valueOf(fromDate)))
    }
    if (thruDate) {
        orderHeaderAndJobCardCond.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, Timestamp.valueOf(thruDate)))
    }
    orderHeaderAndJobCardCond.add(EntityCondition.makeCondition("ownerId", EntityOperator.EQUALS, userLogin.ownerCompanyId))
     System.out.println("CustomerName========================================"+orderHeaderAndJobCardCond);
    jobCardList = from("OrderHeaderAndJobCard").where(orderHeaderAndJobCardCond).queryList()
} else {
	jobCardList = from("OrderHeaderAndJobCard").where(EntityCondition.makeCondition("ownerId", EntityOperator.EQUALS, userLogin.ownerCompanyId)).orderBy("orderId").queryList()
}
System.out.println("jobCardList========================================"+jobCardList);
context.jobCardList = jobCardList


