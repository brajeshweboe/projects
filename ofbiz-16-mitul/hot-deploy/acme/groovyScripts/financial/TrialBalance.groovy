import org.apache.ofbiz.entity.condition.EntityCondition;
import org.apache.ofbiz.entity.condition.EntityOperator;
import org.apache.ofbiz.party.party.PartyHelper;
import org.apache.ofbiz.base.util.UtilValidate;

if(UtilValidate.isNotEmpty(userLogin)){
    userLogin.ownerId

}
partyNameList = []
parties.each { party ->
    partyName = PartyHelper.getPartyName(party)
    partyNameList.add(partyName)
}
context.partyNameList = partyNameList

if (parameters.customTimePeriodId) {
    customTimePeriod = from("CustomTimePeriod").where("customTimePeriodId", parameters.customTimePeriodId).cache(true).queryOne()
    exprList = []
    exprList.add(EntityCondition.makeCondition('organizationPartyId', EntityOperator.IN, partyIds))
    exprList.add(EntityCondition.makeCondition('fromDate', EntityOperator.LESS_THAN, customTimePeriod.getTimestamp('thruDate')))
    exprList.add(EntityCondition.makeCondition(EntityCondition.makeCondition('thruDate', EntityOperator.GREATER_THAN_EQUAL_TO, customTimePeriod.getTimestamp('fromDate')), EntityOperator.OR, EntityCondition.makeCondition('thruDate', EntityOperator.EQUALS, null)))
    List organizationGlAccounts = from("GlAccountOrganizationAndClass").where(exprList).orderBy("accountCode").queryList()

    accountBalances = []
    postedDebitsTotal = 0
    postedCreditsTotal = 0
    organizationGlAccounts.each { organizationGlAccount ->
        accountBalance = [:]
        accountBalance = runService('computeGlAccountBalanceForTimePeriod', [organizationPartyId: organizationGlAccount.organizationPartyId, customTimePeriodId: customTimePeriod.customTimePeriodId, glAccountId: organizationGlAccount.glAccountId])
        if (accountBalance.postedDebits != 0 || accountBalance.postedCredits != 0) {
            accountBalance.glAccountId = organizationGlAccount.glAccountId
            accountBalance.accountCode = organizationGlAccount.accountCode
            accountBalance.accountName = organizationGlAccount.accountName
            postedDebitsTotal = postedDebitsTotal + accountBalance.postedDebits
            postedCreditsTotal = postedCreditsTotal + accountBalance.postedCredits
            accountBalances.add(accountBalance)
        }
    }
    context.postedDebitsTotal = postedDebitsTotal
    context.postedCreditsTotal = postedCreditsTotal
    context.accountBalances = accountBalances
}

