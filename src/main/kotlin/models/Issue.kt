package models

data class Issue (var issueId: Int = 0,
                  var issueNo: Int = 0,
                  var dateOfPublication : String,
                  var rrp: Int = 0,
                  var currentMarketValue: Int = 0,
                  var rarity: String,
                  var condition: String,
                  var isIssueDocumented: Boolean = false)
{
    //Added so YAML can save and load variables from Issue along with Comic
    //details and one source for adding a constructor(): this is found in Comic
    constructor() : this(0,0, "", 0, 0, "", "", false)
    override fun toString(): String {
        val status = if (isIssueDocumented) "Fully Documented" else "Documentation in Progress"
        return "$issueId: Issue No: $issueNo, $dateOfPublication, RRP: $rrp, Current Market Value: $currentMarketValue, Rarity: $rarity, Condition: $condition ($status)"
    }

}