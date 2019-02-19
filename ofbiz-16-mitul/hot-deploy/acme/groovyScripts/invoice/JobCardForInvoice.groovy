
module = "JobCardForInvoice.groovy"

jobCardList = from("OrderHaderAndBin").where("ownerId",userLogin.ownerId,"statusId","ORDER_APPROVED").orderBy("jobCardId").queryList()
if (jobCardList) {
	//orderIds = EntityUtil.getFieldListFromEntityList(jobCardList, "orderId", true)
}
//jobCardList = from("OrderHeaderAndJobCard").where("serviceProviderId",userLogin.ownerId).orderBy("orderId").queryList()

System.out.println("jobCardList========================================"+jobCardList);
context.jobCardList = jobCardList