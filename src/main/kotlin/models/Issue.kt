package models

data class Issue (var issueId: Int = 0,
                  var dateOfPublication : String,
                  var rrp: Int = 0,
                  var currentMarketValue: Int = 0,
                  var rarity: String,
                  var condition: String,
                  var isIssueDocumented: Boolean = false)
{

    override fun toString(): String {
        val status = if (isIssueDocumented) "Complete" else "TODO"
        return "$issueId: $dateOfPublication, RRP: $rrp, Current Market Value: $currentMarketValue, Rarity: $rarity, Condition: $condition ($status)"
    }

}