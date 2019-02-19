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

module = "ProductList.groovy"

//get the Product Details
if(UtilValidate.isNotEmpty(parameters.productId) || UtilValidate.isNotEmpty(parameters.productName)){
   System.out.println("productId========================================"+parameters.productId);
   System.out.println("productName========================================"+parameters.productName);
   productId = parameters.productId;
   productName = parameters.productName;
   
    productCond = []
    if (productId) {
        productCond.add(EntityCondition.makeCondition("productId", EntityOperator.LIKE, "%" + productId + "%"))
    }
    if (productName) {
        productCond.add(EntityCondition.makeCondition("productName", EntityOperator.LIKE, "%" + productName + "%"));
    }
    System.out.println("productCond========================================"+productCond);
    productList = from("ProductAndInfo").where(productCond).orderBy("productId").queryList()
} else {
	productList = from("ProductAndInfo").orderBy("productId").queryList()
}
 
System.out.println("productList========================================"+productList);
context.productList = productList