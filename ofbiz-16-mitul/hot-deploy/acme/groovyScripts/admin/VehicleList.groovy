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

module = "VehicleList.groovy"

//get the Vehicle Details
if(UtilValidate.isNotEmpty(parameters.vehicleId) || UtilValidate.isNotEmpty(parameters.searchName)){
   System.out.println("vehicleId========================================"+parameters.vehicleId);
   System.out.println("searchName========================================"+parameters.searchName);
   vehicleId = parameters.vehicleId;
   searchName = parameters.searchName;
   
    vehicleCond = []
    if (vehicleId) {
        vehicleCond.add(EntityCondition.makeCondition("vehicleId", EntityOperator.LIKE,  "%" + vehicleId + "%"))
    }
    if (searchName) {
        vehicleCond.add(EntityCondition.makeCondition("searchName", EntityOperator.LIKE, "%" + searchName + "%"));
    }
    System.out.println("vehicleCond========================================"+vehicleCond);
    vehicleList = from("Vehicle").where(vehicleCond).orderBy("vehicleId","modelId").queryList()
} else {
	vehicleList = from("Vehicle").orderBy("vehicleId","modelId").queryList()
}
 
System.out.println("vehicleList========================================"+vehicleList);
context.vehicleList = vehicleList;
