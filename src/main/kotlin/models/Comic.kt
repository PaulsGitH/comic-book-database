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
    //Issue with loading occurred using the Notes 5.0 as a base for my assignment, an issue that did not occur
    //in my final notes app for assignment 2, I would assume this is related to the extra fields and complications
    //related to different types(boolean, MutableSet) and the amount of fields. Researching the issue I discovered
    //the problem is fixed from the use of a secondary constructor,so that Jackson could work with all fields.
    //I have no idea how this works, it's not called, it doesn't appear to be used anywhere, but the saved YAML
    //file loads perfectly with no errors, and a list appears perfectly with what was saved prior. There were many
    //resources for this, but this was most relevant in the general sense, allowing it to load as it did in my notes
    //notes app for assignment 2.
    //https://stackoverflow.com/questions/23003634/using-this-in-constructor
    constructor() : this(0, "", "", "", "", false, mutableSetOf())

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