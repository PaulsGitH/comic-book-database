import controllers.ComicAPI
import models.Issue
import models.Comic
import utils.ScannerInput.readNextChar
import utils.ScannerInput.readNextInt
import utils.ScannerInput.readNextLine
import kotlin.system.exitProcess

private val comicAPI = ComicAPI()

fun main() = runMenu()

fun runMenu() {
    do {
        when (val option = mainMenu()) {
            1 -> addComic()
            2 -> listComics()
            3 -> updateComic()
            4 -> deleteComic()
            5 -> archiveComic()
            6 -> addItemToComic()
            7 -> updateItemContentsInComic()
            8 -> deleteAnItem()
            9 -> markItemStatus()
            10 -> searchComics()
            15 -> searchItems()
            16 -> listToDoItems()
            0 -> exitApp()
            else -> println("Invalid menu choice: $option")
        }
    } while (true)
}

fun mainMenu() = readNextInt(
        """ 
         > -----------------------------------------------------  
         > |                  NOTE KEEPER APP                  |
         > -----------------------------------------------------  
         > | NOTE MENU                                         |
         > |   1) Add a note                                   |
         > |   2) List notes                                   |
         > |   3) Update a note                                |
         > |   4) Delete a note                                |
         > |   5) Archive a note                               |
         > -----------------------------------------------------  
         > | ITEM MENU                                         | 
         > |   6) Add item to a note                           |
         > |   7) Update item contents on a note               |
         > |   8) Delete item from a note                      |
         > |   9) Mark item as complete/todo                   | 
         > -----------------------------------------------------  
         > | REPORT MENU FOR NOTES                             | 
         > |   10) Search for all notes (by note title)        |
         > |   11) .....                                       |
         > |   12) .....                                       |
         > |   13) .....                                       |
         > |   14) .....                                       |
         > -----------------------------------------------------  
         > | REPORT MENU FOR ITEMS                             |                                
         > |   15) Search for all items (by item description)  |
         > |   16) List TODO Items                             |
         > |   17) .....                                       |
         > |   18) .....                                       |
         > |   19) .....                                       |
         > -----------------------------------------------------  
         > |   0) Exit                                         |
         > -----------------------------------------------------  
         > ==>> """.trimMargin(">")
    )

//------------------------------------
//NOTE MENU
//------------------------------------
fun addComic() {
    val comicTitle = readNextLine("Enter a title for the comic: ")
    val comicPriority = readNextInt("Enter a priority (1-low, 2, 3, 4, 5-high): ")
    val comicCategory = readNextLine("Enter a category for the comic: ")
    val isAdded = comicAPI.add(Comic(comicTitle = comicTitle, comicPriority = comicPriority, comicCategory = comicCategory))

    if (isAdded) {
        println("Added Successfully")
    } else {
        println("Add Failed")
    }
}

fun listComics() {
    if (comicAPI.numberOfComics() > 0) {
        val option = readNextInt(
            """
                  > --------------------------------
                  > |   1) View ALL notes          |
                  > |   2) View ACTIVE notes       |
                  > |   3) View ARCHIVED notes     |
                  > --------------------------------
         > ==>> """.trimMargin(">")
        )

        when (option) {
            1 -> listAllComics()
            2 -> listAvailableComics()
            3 -> listSoldComics()
            else -> println("Invalid option entered: $option")
        }
    } else {
        println("Option Invalid - No notes stored")
    }
}

fun listAllComics() = println(comicAPI.listAllComics())
fun listAvailableComics() = println(comicAPI.listAvailableComics())
fun listSoldComics() = println(comicAPI.listSoldComics())

fun updateComic() {
    listComics()
    if (comicAPI.numberOfComics() > 0) {
        // only ask the user to choose the note if notes exist
        val id = readNextInt("Enter the id of the note to update: ")
        if (comicAPI.findComic(id) != null) {
            val comicTitle = readNextLine("Enter a title for the note: ")
            val comicPriority = readNextInt("Enter a priority (1-low, 2, 3, 4, 5-high): ")
            val comicCategory = readNextLine("Enter a category for the note: ")

            // pass the index of the note and the new note details to NoteAPI for updating and check for success.
            if (comicAPI.update(id, Comic(0, comicTitle, comicPriority, comicCategory, false))){
                println("Update Successful")
            } else {
                println("Update Failed")
            }
        } else {
            println("There is no comic for this index number")
        }
    }
}

fun deleteComic() {
    listComics()
    if (comicAPI.numberOfComics() > 0) {
        // only ask the user to choose the comic to delete if comic exists
        val id = readNextInt("Enter the id of the comic to delete: ")
        // pass the index of the note to NoteAPI for deleting and check for success.
        val comicToDelete = comicAPI.delete(id)
        if (comicToDelete) {
            println("Delete Successful!")
        } else {
            println("Delete NOT Successful")
        }
    }
}

fun soldComic() {
    listAvailableComics()
    if (comicAPI.numberOfAvailableComics() > 0) {
        // only ask the user to choose the note to archive if active notes exist
        val id = readNextInt("Enter the id of the sold comic to be archived: ")
        // pass the index of the comic to comicAPI for archiving and check for success.
        if (comicAPI.soldComic(id)) {
            println("Archiving of Sold Comic Successful!")
        } else {
            println("Archive NOT Successful")
        }
    }
}

//-------------------------------------------
//ISSUE MENU (only available for available comics)
//-------------------------------------------
private fun addIssueToComic() {
    val comic: Comic? = askUserToChooseAvailableComic()
    if (comic != null) {
        val dateOfPublication = readNextLine("\t Date of Publication: ")
        val rrp = readNextInt("\t RRP: ")
        val currentMarketValue = readNextInt("\t Current Market Value: ")
        val rarity = readNextLine("\t Rarity: ")
        val condition = readNextLine("\t Condition: ")

        val newIssue = Issue(
            dateOfPublication = dateOfPublication,
            rrp = rrp,
            currentMarketValue = currentMarketValue,
            rarity = rarity,
            condition = condition
        )

        if (comic.addIssue(newIssue)) {
            println("Add Successful!")
        } else {
            println("Add NOT Successful")
        }
    }
}

fun updateIssueDetailsInComic() {
    val comic: Comic? = askUserToChooseAvailableComic()
    if (comic != null) {
        val issue: Issue? = askUserToChooseIssue(comic)
        if (issue != null) {
            val newDetails = readNextLine("Enter new details: ")
            if (comic.update(issue.issueId, Issue(issueDetails = newDetails))) {
                println("Issue details updated")
            } else {
                println("Issue details NOT updated")
            }
        } else {
            println("Invalid Issue Id")
        }
    }
}

fun deleteAnIssue() {
    val comic: Comic? = askUserToChooseAvailableComic()
    if (comic != null) {
        val issue: Issue? = askUserToChooseIssue(comic)
        if (issue != null) {
            val isDeleted = comic.delete(issue.issueId)
            if (isDeleted) {
                println("Delete Successful!")
            } else {
                println("Delete NOT Successful")
            }
        }
    }
}

fun markIssueStatus() {
    val comic: Comic? = askUserToChooseAvailableComic()
    if (comic != null) {
        val issue: Issue? = askUserToChooseIssue(comic)
       if (issue != null) {
                var changeStatus = 'X'
                if (issue.isIssueDocumented) {
                    changeStatus = readNextChar("The issue condition has been confirmed...do you want to mark it as INSPECTION DUE?")
                    if ((changeStatus == 'Y') ||  (changeStatus == 'y'))
                        issue.isIssueDocumented = false
                }
                else {
                    changeStatus = readNextChar("The item is currently due for INSPECTION...do you want to mark it as Inspection Complete?")
                    if ((changeStatus == 'Y') ||  (changeStatus == 'y'))
                        issue.isIssueDocumented = true
                }
       }
    }
}

//------------------------------------
//NOTE REPORTS MENU
//------------------------------------
fun searchComics() {
    val searchTitle = readNextLine("Enter the description to search by: ")
    val searchResults = comicAPI.searchComicsByTitle(searchTitle)
    if (searchResults.isEmpty()) {
        println("No comics found")
    } else {
        println(searchResults)
    }
}

//------------------------------------
//ITEM REPORTS MENU
//------------------------------------
fun searchIssues() {
    val searchDetails = readNextLine("Enter the issue contents to search by: ")
    val searchResults = comicAPI.searchIssueByDetails(searchDetails)
    if (searchResults.isEmpty()) {
        println("No issues found")
    } else {
        println(searchResults)
    }
}

fun listInspectIssues(){
    if (comicAPI.numberOfInspectIssues() > 0) {
        println("Total Issues requiring condition inspection: ${comicAPI.numberOfInspectIssues()}")
    }
    println(comicAPI.listInspectIssues())
}


//------------------------------------
// Exit App
//------------------------------------
fun exitApp() {
    println("Exiting...bye")
    exitProcess(0)
}

//------------------------------------
//HELPER FUNCTIONS
//------------------------------------

private fun askUserToChooseAvailableComic(): Comic? {
    listAvailableComics()
    if (comicAPI.numberOfAvailableComics() > 0) {
        val comic = comicAPI.findComic(readNextInt("\nEnter the id of the comic: "))
        if (comic != null) {
            if (comic.isComicSold) {
                println("Comic is NOT Available, it is Sold")
            } else {
                return comic //chosen comic is active
            }
        } else {
            println("Comic id is not valid")
        }
    }
    return null //selected note is not active
}

private fun askUserToChooseIssue(comic: Comic): Issue? {
    if (comic.numberOfIssues() > 0) {
        print(comic.listIssues())
        return comic.findOne(readNextInt("\nEnter the id of the issue: "))
    }
    else{
        println ("No issue details for chosen comic")
        return null
    }
}
