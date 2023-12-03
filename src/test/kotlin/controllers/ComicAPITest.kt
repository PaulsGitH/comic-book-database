package controllers
import models.Comic
import models.Issue
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import persistence.JSONSerializer
import persistence.XMLSerializer
import persistence.YAMLSerializer
import java.io.File
import kotlin.test.assertEquals
class ComicAPITest {
    private var testApp: Comic? = null
    private var swim: Comic? = null
    private var summerHoliday: Comic? = null
    private var storingComics: ComicAPI? = ComicAPI(JSONSerializer(File("comics.json")))
    private var loadingComics: ComicAPI? = ComicAPI(JSONSerializer(File("comics.json")))

    @BeforeEach
    fun setup() {
        testApp = Comic(comicTitle = "Test App", comicWriter = "Writer1", comicArtist = "Artist1", comicPublisher = "Publisher1", isComicSold = false, issues = mutableSetOf())
        swim = Comic(comicTitle = "Swim - Pool", comicWriter = "Writer2", comicArtist = "Artist2", comicPublisher = "Publisher2", isComicSold = true, issues = mutableSetOf())
        summerHoliday = Comic(comicTitle = "Summer Holiday", comicWriter = "Writer3", comicArtist = "Artist3", comicPublisher = "Publisher3", isComicSold = false, issues = mutableSetOf())
        storingComics!!.add(testApp!!)
        storingComics!!.add(swim!!)
        storingComics!!.add(summerHoliday!!)
    }
    @AfterEach
    fun tearDown() {
        testApp = null
        swim = null
        summerHoliday = null
        storingComics = null
        loadingComics = null
    }

    @Nested
    inner class AddComics {

        @Test
        fun `adding a Comic to a populated list adds to ArrayList`() {
            val newComic = Comic(comicTitle = "New Comic", comicWriter = "Writer4", comicArtist = "Artist4", comicPublisher = "Publisher4", isComicSold = false, issues = mutableSetOf())
            assertEquals(3, storingComics!!.numberOfComics())
            assertTrue(storingComics!!.add(newComic))
            assertEquals(4, storingComics!!.numberOfComics())
            assertEquals(newComic, storingComics!!.findComic(storingComics!!.numberOfComics() - 1))
        }

        @Test
        fun `adding a Comic to an empty list adds to ArrayList`() {
            val newComic = Comic(comicTitle = "New Comic", comicWriter = "Writer4", comicArtist = "Artist4", comicPublisher = "Publisher4", isComicSold = false, issues = mutableSetOf())
            assertEquals(0, loadingComics!!.numberOfComics())
            assertTrue(loadingComics!!.add(newComic))
            assertEquals(1, loadingComics!!.numberOfComics())
            assertEquals(newComic, loadingComics!!.findComic(loadingComics!!.numberOfComics() - 1))
        }
    }

}