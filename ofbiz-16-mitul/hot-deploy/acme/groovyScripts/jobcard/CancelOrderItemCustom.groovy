orderId = parameters.orderId;
orderItemSeqId = parameters.orderItemSeqId;
jobCardId = parameters.jobCardId;
orderId = parameters.orderId;
orderId = parameters.orderId;
orderId = parameters.orderId;

println("======cancelOrderItemCustom=================8888888888888888=====================");
resultMap = runService('cancelOrderItemCustom', ["orderId" : orderId,"orderItemSeqId":orderItemSeqId,"shipGroupSeqId":"00001","jobCardId":jobCardId, "userLogin" : userLogin,"purposeFrom":"sparePartIssue"]);
println("======resultMap================="+resultMap+"=====================");
parameters.jobCardId = resultMap.jobCardId
parameters.orderId = resultMap.orderId
