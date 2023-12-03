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

/**
 * Main entry point for the Comic Application.
 * Initializes the [ComicAPI], sets up the menu, and enters a loop to execute user-selected actions.
 */
fun main() = runMenu()
/**
 * Runs the main menu loop for the Comic Application.
 * Displays the menu, reads user input, and executes corresponding actions until the user chooses to exit.
 */
fun runMenu() {
    do {
        when (val option = mainMenu()) {
            1 -> addComic()
            2 -> listComics()
            3 -> updateComic()
            4 -> deleteComic()
            5 -> soldComic()
            6 -> searchComics()
            7 -> addIssueToComic()
            8 -> updateIssueDetailsInComic()
            9 -> deleteAnIssue()
            10 -> markIssueStatus()
            11 -> searchIssues()
            12 -> listInspectIssues()
            13 -> countTotalComics()
            14 -> save()
            15 -> load()
            0 -> exitApp()
            else -> println("Invalid menu choice: $option")
        }
    } while (true)
}
/**
 * Displays the main menu and reads the user's menu choice.
 * @return The user's menu choice as an integer.
 */
fun mainMenu() = readNextInt(
        """ 
         > -----------------------------------------------------  
         > |      ####     ####    ##   ##   ####     ####     |
         > |     ##  ##   ##  ##   ### ###    ##     ##  ##    |
         > |     ##       ##  ##   #######    ##     ##        |
         > |     ##       ##  ##   ## # ##    ##     ##        |
         > |     ##       ##  ##   ##   ##    ##     ##        |
         > |     ##  ##   ##  ##   ##   ##    ##     ##  ##    |
         > |      ####     ####    ##   ##   ####     ####     |
         > -----------------------------------------------------  
         > | COMIC MENU                                        |
         > |   1) Add a comic series                           |
         > |   2) List comic menu                              |
         > |   3) Update a Comic series                        |
         > |   4) Delete a Comic                               |
         > |   5) Archive a Comic as sold                      |
         > |   6) Search Comic submenu                         |
         > -----------------------------------------------------  
         > | ISSUE MENU                                        | 
         > |   7) Add issue to a comic series                  |
         > |   8) Update issue details on a Comic series       |
         > |   9) Delete Issue from a Comic series             |
         > |   10) Mark Issue status                           |
         > |   11) Search Issue submenu                        |
         >     12) List Inspect issues                         |
         > -----------------------------------------------------  
         > | SAVE AND LOAD MENU                                | 
         > |   13) Count total comics                          |
         > |   14) Save stored information for comic and issue |
         > |   15) Load stored information for comic and issue |
         > -----------------------------------------------------  
         > |   0) Exit                                         |
         > -----------------------------------------------------  
         > ==>> """.trimMargin(">")
    )

//------------------------------------
// MENU
//------------------------------------
/**
 * Adds a new comic to the application.
 * Reads user input for comic details and uses [ComicAPI] to add the comic.
 */
fun addComic() {
    logger.info { "addComic() function invoked" }
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

/**
 * Lists comics based on user's choice (all, sold, or available).
 * Reads user input and calls the appropriate [ComicAPI] function.
 */
fun listComics() {
    logger.info { "listComics() function invoked" }
    if (comicAPI.numberOfComics() > 0) {
        val option = readNextInt(
            """
                  > --------------------------------
                  > |   1) List All comics         |
                  > |   2) List sold comics        |
                  > --------------------------------
         > ==>> """.trimMargin(">")
        )

        when (option) {
            1 -> listAllComics()
            2 -> listSoldComics()
            else -> println("Invalid option entered: $option")
        }
    } else {
        println("Option Invalid - No notes stored")
    }
}
/**
 * Searches for comics based on user's choice (title, writer, artist, publisher).
 * Reads user input and calls the appropriate [ComicAPI] function.
 */
fun searchComics() {
    logger.info { "searchComics() function invoked" }
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
/**
 * Searches for issues based on user's choice (rarity, condition, publication date).
 * Reads user input and calls the appropriate [ComicAPI] function.
 */
fun searchIssues() {
    logger.info { "searchIssues() function invoked" }
    if (comicAPI.numberOfComics() > 0) {
        val option = readNextInt(
            """
                  > --------------------------------
                  > |   1) Search Issue by rarity  |
                  > |   2) Search Issue by condition|
                  > |   3) Search Issue by pub date|
                  > --------------------------------
         > ==>> """.trimMargin(">")
        )

        when (option) {
            1 -> searchIssueByRarity()
            2 -> searchIssueByCondition()
            3 -> searchIssueByDateOfPublication()
            else -> println("Invalid option entered: $option")
        }
    } else {
        println("Option Invalid - No notes stored")
    }
}

fun listAllComics() = println(comicAPI.listAllComics())
fun listAvailableComics() = println(comicAPI.listAvailableComics())
fun listSoldComics() = println(comicAPI.listSoldComics())
fun countTotalComics() = println(comicAPI.comicTotal())
/**
 * Updates the details of a comic.
 * Displays the list of comics, reads user input for the comic to update, and updates using [ComicAPI].
 */
fun updateComic() {
    logger.info { "updateComic() function invoked" }
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
/**
 * Deletes a comic.
 * Displays the list of comics, reads user input for the comic to delete, and deletes using [ComicAPI].
 */
fun deleteComic() {
    logger.info { "deleteComic() function invoked" }
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
/**
 * Marks a comic as sold.
 * Displays the list of available comics, reads user input for the comic to mark as sold, and archives using [ComicAPI].
 */
fun soldComic() {
    logger.info { "soldComic() function invoked" }
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
    logger.info { "addIssueToComic() function invoked" }
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
    logger.info { "updateIssueDetailsInComic() function invoked" }
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
    logger.info { "deleteAnIssue() function invoked" }
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
/**
 * Archives an issue in a comic by marking its inspection status.
 * Displays the list of comics, reads user input for the comic and issue to mark, and updates using [ComicAPI].
 */
fun markIssueStatus() {
    logger.info { "markIssueStatus() function invoked" }
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
    logger.info { "searchComicsByTitle() function invoked" }
    val searchTitle = readNextLine("Enter the description to search by: ")
    val searchResults = comicAPI.searchComicsByTitle(searchTitle)
    if (searchResults.isEmpty()) {
        println("No comics found")
    } else {
        println(searchResults)
    }
}
fun searchComicsByWriter() {
    logger.info { "searchComicsByWriter() function invoked" }
    val searchWriter = readNextLine("Enter the Writer to search by: ")
    val searchResults = comicAPI.searchComicsByWriter(searchWriter)
    if (searchResults.isEmpty()) {
        println("No Writer found")
    } else {
        println(searchResults)
    }
}
fun searchComicsByArtist() {
    logger.info { "searchComicsByArtist() function invoked" }
    val searchArtist = readNextLine("Enter the Artist to search by: ")
    val searchResults = comicAPI.searchComicsByArtist(searchArtist)
    if (searchResults.isEmpty()) {
        println("No artist found")
    } else {
        println(searchResults)
    }
}

fun searchComicsByPublisher() {
    logger.info { "searchComicByPublisher() function invoked" }
    val searchPublisher = readNextLine("Enter the Publisher to search by: ")
    val searchResults = comicAPI.searchComicsByPublisher(searchPublisher)
    if (searchResults.isEmpty()) {
        println("No Publisher found")
    } else {
        println(searchResults)
    }
}
//------------------------------------
//ITEM REPORTS MENU
//------------------------------------
fun searchIssueByRarity() {
    logger.info { "searchIssueByRarity() function invoked" }
    val searchDetails = readNextLine("Enter the issue rarity to search by: ")
    val searchResults = comicAPI.searchIssueByRarity(searchDetails)
    if (searchResults.isEmpty()) {
        println("No issues found")
    } else {
        println(searchResults)
    }
}

fun searchIssueByCondition() {
    logger.info { "searchIssueByCondition() function invoked" }
    val searchDetails = readNextLine("Enter the issue condition to search by: ")
    val searchResults = comicAPI.searchIssueByCondition(searchDetails)
    if (searchResults.isEmpty()) {
        println("No issues found")
    } else {
        println(searchResults)
    }
}

fun searchIssueByDateOfPublication() {
    logger.info { "searchIssueByDateOfPublication() function invoked" }
    val searchDetails = readNextLine("Enter the issue contents to search by: ")
    val searchResults = comicAPI.searchIssueByDateOfPublication(searchDetails)
    if (searchResults.isEmpty()) {
        println("No issues found")
    } else {
        println(searchResults)
    }
}

fun listInspectIssues(){
    logger.info { "listInspectIssues() function invoked" }
    if (comicAPI.numberOfInspectIssues() > 0) {
        println("Total Issues requiring condition inspection: ${comicAPI.numberOfInspectIssues()}")
    }
    println(comicAPI.listInspectIssues())
}

/**
 * Exits the Comic Application.
 * Displays a farewell message and terminates the program.
 */
//------------------------------------
// Exit App
//------------------------------------
fun exitApp() {
    logger.info { "exitApp() function invoked" }
    println("Exiting...bye")
    exitProcess(0)
}

//------------------------------------
//HELPER FUNCTIONS
//------------------------------------
/**
 * Asks the user to choose a comic from the list.
 * Displays the list of all comics and reads user input for the chosen comic.
 * @return The chosen [Comic] or null if the input is invalid.
 */
private fun askUserToChooseComic(): Comic? {
    logger.info { "askUserToChooseComic() function invoked" }
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
/**
 * Asks the user to choose an issue from a comic.
 * Displays the list of issues for the chosen comic and reads user input for the chosen issue.
 * @return The chosen [Issue] or null if the input is invalid or no issues are available.
 */
private fun askUserToChooseIssue(comic: Comic): Issue? {
    logger.info { "askUserToChooseIssue() function invoked" }
    if (comic.numberOfIssues() > 0) {
        print(comic.listIssues())
        return comic.findOne(readNextInt("\nEnter the id of the issue: "))
    }
    else{
        println ("No issue details for chosen comic")
        return null
    }
}
/**
 * Saves the current state of the application to a file using [ComicAPI].
 * Handles potential exceptions and prints an error message if saving fails.
 */
fun save() {
    logger.info { "save() function invoked" }
    try {
        comicAPI.store()
    } catch (e: Exception) {
        System.err.println("Error writing to file: $e")
    }
}
/**
 * Loads the previously saved state of the application from a file using [ComicAPI].
 * Handles potential exceptions and prints an error message if loading fails.
 */
fun load() {
    logger.info { "load() function invoked" }
    try {
        comicAPI.load()
    } catch (e: Exception) {
        System.err.println("Error reading from file: $e")
    }
}