import controllers.ComicAPI
import models.Issue
import models.Comic
import mu.KotlinLogging
import persistence.JSONSerializer
import persistence.XMLSerializer
import persistence.YAMLSerializer
import utils.ScannerInput.readNextChar
import utils.ScannerInput.readNextInt
import utils.ScannerInput.readNextLine
import java.io.File
import kotlin.system.exitProcess

//private val comicAPI = ComicAPI(XMLSerializer(File("comics.xml")))
//private val comicAPI = ComicAPI(JSONSerializer(File("comics.json")))
private val comicAPI = ComicAPI(YAMLSerializer(File("comics.yaml")))
private val logger = KotlinLogging.logger {}

fun main() = runMenu()

fun runMenu() {
    do {
        when (val option = mainMenu()) {
            1 -> addComic()
            3 -> listComics()
            4 -> updateComic()
            5 -> deleteComic()
            6 -> soldComic()
            7 -> addIssueToComic()
            8 -> updateIssueDetailsInComic()
            9 -> deleteAnIssue()
            10 -> markIssueStatus()
            11 -> searchComics()
            12 -> save()
            13 -> load()
            14 -> searchIssues()
            15 -> listInspectIssues()
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
         > |   1) Add a comic                                  |
         > |   2) Add Available comic                          |
         > |   3) List notes                                   |
         > |   4) Update a note                                |
         > |   5) Delete a note                                |
         > |   6) Archive a note                               |
         > -----------------------------------------------------  
         > | ITEM MENU                                         | 
         > |   7) Add item to a note                           |
         > |   8) Update item contents on a note               |
         > |   9) Delete item from a note                      |
         > |   10) Mark item as complete/todo                  | 
         > -----------------------------------------------------  
         > | REPORT MENU FOR NOTES                             | 
         > |   11) Search for all notes (by note title)        |
         > |   12) Save stored information for comic and issue |
         > |   13) Load stored information for comic and issue |
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
    val comicTitle = readNextLine("Enter Comic series title: ")
    val comicWriter = readNextLine("Enter the writer for the comic: ")
    val comicArtist = readNextLine("Enter the artist for the comic: ")
    val comicPublisher = readNextLine("Enter the publisher for the comic: ")

    val isAdded = comicAPI.add(Comic(
        comicTitle = comicTitle,
        comicWriter = comicWriter,
        comicArtist = comicArtist,
        comicPublisher = comicPublisher
    ))

    if (isAdded) {
        println("Successfully added comic")
    } else {
        println("Adding comic failed")
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

fun searchComics() {
    if (comicAPI.numberOfComics() > 0) {
        val option = readNextInt(
            """
                  > --------------------------------
                  > |   1) Search comic by title   |
                  > |   2) Search comic by writer  |
                  > |   3) Search comic by artist  |
                  > |   4) Search comic by publisher|
                  > --------------------------------
         > ==>> """.trimMargin(">")
        )

        when (option) {
            1 -> searchComicsByTitle()
            2 -> searchComicsByWriter()
            3 -> searchComicsByArtist()
            4 -> searchComicsByPublisher()
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
        // only ask the user to choose the comic if comics exist
        val id = readNextInt("Enter the id of the comic to update: ")
        if (comicAPI.findComic(id) != null) {
            val comicTitle = readNextLine("Enter a title for the comic: ")
            val comicWriter = readNextLine("Enter the writer for the comic: ")
            val comicArtist = readNextLine("Enter the artist for the comic: ")
            val comicPublisher = readNextLine("Enter the publisher for the comic: ")

            // pass the index of the comic and the new comic details to ComicAPI for updating and check for success.
            if (comicAPI.update(id, Comic(
                    comicTitle = comicTitle,
                    comicWriter = comicWriter,
                    comicArtist = comicArtist,
                    comicPublisher = comicPublisher,
                ))) {
                println("Update Successful")
            } else {
                println("Update Failed")
            }
        } else {
            println("There is no comic with this id")
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
        // only ask the user to mark the comic as sold if available comic exists
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
    val comic: Comic? = askUserToChooseComic()
    if (comic != null) {
        val issueNo = readNextInt("\t Enter issue number: ")
        val dateOfPublication = readNextLine("\t Date of Publication: ")
        val rrp = readNextInt("\t RRP: ")
        val currentMarketValue = readNextInt("\t Current Market Value: ")
        val rarity = readNextLine("\t Rarity: ")
        val condition = readNextLine("\t Condition: ")

        val newIssue = Issue(
            issueNo = issueNo,
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
    val comic: Comic? = askUserToChooseComic()
    if (comic != null) {
        val issue: Issue? = askUserToChooseIssue(comic)
        if (issue != null) {
            val newIssueNo = readNextInt("Enter new issue number")
            val newDateOfPublication = readNextLine("Enter new Date of Publication: ")
            val newRRP = readNextInt("Enter new RRP: ")
            val newRarity = readNextLine("Enter new Rarity: ")
            val newCurrentMarketValue = readNextInt("Enter new market value")
            val newCondition = readNextLine("Enter new Condition: ")

            val updatedIssue = Issue(
                issueId = issue.issueId,
                issueNo = newIssueNo,
                dateOfPublication = newDateOfPublication,
                rrp = newRRP,
                currentMarketValue = newCurrentMarketValue,
                rarity = newRarity,
                condition = newCondition
            )

            if (comic.update(issue.issueId, updatedIssue)) {
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
    val comic: Comic? = askUserToChooseComic()
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
    val comic: Comic? = askUserToChooseComic()
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
fun searchComicsByTitle() {
    val searchTitle = readNextLine("Enter the description to search by: ")
    val searchResults = comicAPI.searchComicsByTitle(searchTitle)
    if (searchResults.isEmpty()) {
        println("No comics found")
    } else {
        println(searchResults)
    }
}
fun searchComicsByWriter() {
    val searchWriter = readNextLine("Enter the Writer to search by: ")
    val searchResults = comicAPI.searchComicsByWriter(searchWriter)
    if (searchResults.isEmpty()) {
        println("No Writer found")
    } else {
        println(searchResults)
    }
}
fun searchComicsByArtist() {
    val searchArtist = readNextLine("Enter the Artist to search by: ")
    val searchResults = comicAPI.searchComicsByArtist(searchArtist)
    if (searchResults.isEmpty()) {
        println("No artist found")
    } else {
        println(searchResults)
    }
}

fun searchComicsByPublisher() {
    val searchPublisher = readNextLine("Enter the Publisher to search by: ")
    val searchResults = comicAPI.searchComicsByWriter(searchPublisher)
    if (searchResults.isEmpty()) {
        println("No Publisher found")
    } else {
        println(searchResults)
    }
}
//------------------------------------
//ITEM REPORTS MENU
//------------------------------------
fun searchIssues() {
    val searchDetails = readNextLine("Enter the issue contents to search by: ")
    val searchResults = comicAPI.searchIssueByRarity(searchDetails)
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

private fun askUserToChooseComic(): Comic? {
    listAllComics()  // Use listAllComics to display all comics
    if (comicAPI.numberOfComics() > 0) {
        val comic = comicAPI.findComic(readNextInt("\nEnter the id of the comic: "))
        if (comic != null) {
            return comic
        } else {
            println("Comic id is not valid")
        }
    }
    return null
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

fun save() {
    try {
        comicAPI.store()
    } catch (e: Exception) {
        System.err.println("Error writing to file: $e")
    }
}

fun load() {
    try {
        comicAPI.load()
    } catch (e: Exception) {
        System.err.println("Error reading from file: $e")
    }
}