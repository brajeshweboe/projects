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

module = "CategoryList.groovy"

//get the Category Details
//List<GenericValue> 

if(UtilValidate.isNotEmpty(parameters.categoryId) || UtilValidate.isNotEmpty(parameters.categoryName)){
   System.out.println("categoryId========================================"+parameters.categoryId);
   System.out.println("categoryName========================================"+parameters.categoryName);
   categoryId = parameters.categoryId;
   categoryName = parameters.categoryName;
   
    productCategoryCond = []
    if (categoryId) {
        productCategoryCond.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.LIKE, "%" + categoryId + "%"))
    }
    if (categoryName) {
        productCategoryCond.add(EntityCondition.makeCondition("categoryName", EntityOperator.LIKE, "%" + categoryName + "%"));
    }
    System.out.println("productCategoryCond========================================"+productCategoryCond);
    categoryList = from("ProductCategory").where(productCategoryCond).orderBy("productCategoryId").queryList()
} else {
	categoryList = from("ProductCategory").orderBy("productCategoryId").queryList()
}

System.out.println("categoryList========================================"+categoryList);
context.categoryList = categoryList
