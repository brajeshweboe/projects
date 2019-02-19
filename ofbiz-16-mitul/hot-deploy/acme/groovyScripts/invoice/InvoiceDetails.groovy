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

module = "JobCardList.groovy"

//get the order types
jobCardList = from("OrderHeaderAndJobCard").orderBy("orderId").queryList()
context.jobCardList = jobCardList

//get the order types
jobCardDetails = from("OrderItem").orderBy("orderItemSeqId").queryList()
context.jobCardDetails = jobCardDetails

// get the order types
orderTypes = from("OrderType").orderBy("description").queryList()
context.orderTypes = orderTypes

// get the role types
roleTypes = from("RoleType").orderBy("description").queryList()
context.roleTypes = roleTypes

// get the order statuses
orderStatuses = from("StatusItem").where("statusTypeId", "ORDER_STATUS").orderBy("sequenceId", "description").queryList()
context.orderStatuses = orderStatuses

