package controllers

import models.Comic
import persistence.Serializer
import utils.Utilities.formatListString
import java.util.ArrayList

class ComicAPI(serializerType: Serializer) {

    private var serializer: Serializer = serializerType

    private var comics = ArrayList<Comic>()

    // ----------------------------------------------
    //  For Managing the id internally in the program
    // ----------------------------------------------
    private var lastId = 0
    private fun getId() = lastId++

    // ----------------------------------------------
    //  CRUD METHODS FOR NOTE ArrayList
    // ----------------------------------------------
    fun add(comic: Comic): Boolean {
        comic.comicId = getId()
        return comics.add(comic)
    }

    fun delete(id: Int) = comics.removeIf { comic -> comic.comicId == id }

    fun update(id: Int, comic: Comic?): Boolean {
        // find the note object by the index number
        val foundComic = findComic(id)

        // if the note exists, use the note details passed as parameters to update the found note in the ArrayList.
        if ((foundComic != null) && (comic != null)) {
            foundComic.comicTitle = comic.comicTitle
            foundComic.comicWriter = comic.comicWriter
            foundComic.comicArtist = comic.comicArtist
            foundComic.comicPublisher = comic.comicPublisher
            return true
        }

        // if the note was not found, return false, indicating that the update was not successful
        return false
    }

    fun soldComic(id: Int): Boolean {
        val foundComic = findComic(id)
        if (( foundComic != null) && (!foundComic.isComicSold)
            && ( foundComic.checkNoteCompletionStatus())) {
              foundComic.isComicSold = true
              return true
        }
        return false
    }
    // ----------------------------------------------
    //  LISTING METHODS FOR NOTE ArrayList
    // ----------------------------------------------
    fun listAllComics() =
        if (comics.isEmpty()) "No comics stored"
        else formatListString(comics)

    fun listAvailableComics() =
        if (numberOfAvailableComics() == 0) "No available comics stored"
        else formatListString(comics.filter { comic -> !comic.isComicSold })

    fun listSoldComics() =
        if (numberOfSoldComics() == 0) "No sold comics stored"
        else formatListString(comics.filter { comic -> comic.isComicSold })

    // ----------------------------------------------
    //  COUNTING METHODS FOR COMIC ArrayList
    // ----------------------------------------------
    fun numberOfComics() = comics.size
    fun numberOfAvailableComics(): Int = comics.count { comic: Comic -> comic.isComicSold }
    fun numberOfSoldComics(): Int = comics.count { comic: Comic -> !comic.isComicSold }

    // ----------------------------------------------
    //  SEARCHING METHODS
    // ---------------------------------------------
    fun findComic(comicId : Int) =  comics.find{ comic -> comic.comicId == comicId }

    fun searchComicsByTitle(searchString: String) =
       formatListString(
            comics.filter { comic -> comic.comicTitle.contains(searchString, ignoreCase = true) }
        )

    fun searchIssueByRarity(searchString: String): String {
        return if (numberOfComics() == 0) "No comics stored"
        else {
            var listOfComics = ""
            for (comic in comics) {
                for (issue in comic.issues) {
                    if (issue.rarity.contains(searchString, ignoreCase = true)) {
                        listOfComics += "${comic.comicId}: ${comic.comicTitle} \n\t${issue}\n"
                    }
                }
            }
            if (listOfComics == "") "No items found for: $searchString"
            else listOfComics
        }
    }

    // ----------------------------------------------
    //  LISTING METHODS FOR ITEMS
    // ----------------------------------------------
    fun listInspectIssues(): String =
         if (numberOfComics() == 0) "No notes stored"
         else {
             var listOfInspectIssues = ""
             for (comic in comics) {
                 for (issue in comic.issues) {
                     if (!issue.isIssueDocumented) {
                         listOfInspectIssues += comic.comicTitle + ": " + issue.condition + "\n"
                     }
                 }
             }
             listOfInspectIssues
         }

    // ----------------------------------------------
    //  COUNTING METHODS FOR ITEMS
    // ----------------------------------------------
    fun numberOfInspectIssues(): Int {
        var numberOfInspectIssues = 0
        for (comic in comics) {
            for (issue in comic.issues) {
                if (!issue.isIssueDocumented) {
                    numberOfInspectIssues++
                }
            }
        }
        return numberOfInspectIssues
    }

    @Throws(Exception::class)
    fun load() {
        comics = serializer.read() as ArrayList<Comic>
    }

    @Throws(Exception::class)
    fun store() {
        serializer.write(comics)
    }

}