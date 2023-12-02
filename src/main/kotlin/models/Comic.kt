package models

import utils.Utilities

data class Comic(
    var comicId: Int = 0,
    var comicTitle: String,
    var comicWriter: String,
    var comicArtist: String,
    var comicPublisher: String,
    var isComicSold: Boolean = false,
    var issues: MutableSet<Issue> = mutableSetOf())
{
    private var lastIssueId = 0
    private fun getIssueId() = lastIssueId++

    fun addIssue(issue: Issue) : Boolean {
        issue.issueId = getIssueId()
        return issues.add(issue)
    }

    fun numberOfIssues() = issues.size

    fun findOne(id: Int): Issue?{
        return issues.find{ issue -> issue.issueId == id }
    }

    fun delete(id: Int): Boolean {
        return issues.removeIf { issue -> issue.issueId == id}
    }

    fun update(id: Int, newIssue : Issue): Boolean {
        val foundIssue = findOne(id)

        //if the object exists, use the details passed in the newIssue parameter to
        //update the found issue in the Set
        if (foundIssue != null){
            foundIssue.dateOfPublication = newIssue.dateOfPublication
            foundIssue.rrp = newIssue.rrp
            foundIssue.currentMarketValue = newIssue.currentMarketValue
            foundIssue.rarity = newIssue.rarity
            foundIssue.condition = newIssue.condition
            foundIssue.isIssueDocumented = newIssue.isIssueDocumented
            return true
        }

        //if the object was not found, return false, indicating that the update was not successful
        return false
    }

    fun checkNoteCompletionStatus(): Boolean {
        if (issues.isNotEmpty()) {
            for (issue in issues) {
                if (!issue.isIssueDocumented) {
                    return false
               }
            }
        }
        return true //a note with empty items can be archived, or all items are complete
    }

    fun listIssues() =
         if (issues.isEmpty())  "\tNO ITEMS ADDED"
         else  Utilities.formatSetString(issues)

    override fun toString(): String {
        val soldStatus = if (isComicSold) "Sold" else "Available"
        return "$comicId: $comicTitle, Writer: $comicWriter, Artist: $comicArtist, Publisher: $comicPublisher, Status: $soldStatus \n${listIssues()}"
    }

}