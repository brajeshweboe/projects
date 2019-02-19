jobCardList = from("JobCard").where("serviceProviderId",userLogin.ownerId).orderBy("jobCardId").queryList();
context.jobCardList=jobCardList;
println("======parameters.jobCardId============="+parameters.jobCardId+"====================");
if(parameters.jobCardId){
	println("======parameters.jobCardId============="+parameters.jobCardId+"====================");
}

