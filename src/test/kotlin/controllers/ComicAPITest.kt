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
        testApp = Comic(
            comicTitle = "Test App",
            comicWriter = "Writer1",
            comicArtist = "Artist1",
            comicPublisher = "Publisher1",
            isComicSold = false,
            issues = mutableSetOf()
        )
        swim = Comic(
            comicTitle = "Swim - Pool",
            comicWriter = "Writer2",
            comicArtist = "Artist2",
            comicPublisher = "Publisher2",
            isComicSold = true,
            issues = mutableSetOf()
        )
        summerHoliday = Comic(
            comicTitle = "Summer Holiday",
            comicWriter = "Writer3",
            comicArtist = "Artist3",
            comicPublisher = "Publisher3",
            isComicSold = false,
            issues = mutableSetOf()
        )
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
            val newComic = Comic(
                comicTitle = "New Comic",
                comicWriter = "Writer4",
                comicArtist = "Artist4",
                comicPublisher = "Publisher4",
                isComicSold = false,
                issues = mutableSetOf()
            )
            assertEquals(3, storingComics!!.numberOfComics())
            assertTrue(storingComics!!.add(newComic))
            assertEquals(4, storingComics!!.numberOfComics())
            assertEquals(newComic, storingComics!!.findComic(storingComics!!.numberOfComics() - 1))
        }

        @Test
        fun `adding a Comic to an empty list adds to ArrayList`() {
            val newComic = Comic(
                comicTitle = "New Comic",
                comicWriter = "Writer4",
                comicArtist = "Artist4",
                comicPublisher = "Publisher4",
                isComicSold = false,
                issues = mutableSetOf()
            )
            assertEquals(0, loadingComics!!.numberOfComics())
            assertTrue(loadingComics!!.add(newComic))
            assertEquals(1, loadingComics!!.numberOfComics())
            assertEquals(newComic, loadingComics!!.findComic(loadingComics!!.numberOfComics() - 1))
        }
    }

    @Nested
    inner class ListComics {

        @Test
        fun `listAllComics returns No Comics Stored message when ArrayList is empty`() {
            assertEquals(0, loadingComics!!.numberOfComics())
            assertTrue(loadingComics!!.listAllComics().lowercase().contains("no comics"))
        }

        @Test
        fun `listAllComics returns Comics when ArrayList has comics stored`() {
            assertEquals(3, storingComics!!.numberOfComics())
            val comicsString = storingComics!!.listAllComics().lowercase()
            assertTrue(comicsString.contains("test app"))
            assertTrue(comicsString.contains("swim - pool"))
            assertTrue(comicsString.contains("summer holiday"))
        }

        @Test
        fun `listAvailableComics returns no available comics stored when ArrayList is empty`() {
            assertEquals(0, loadingComics!!.numberOfAvailableComics())
            assertTrue(
                loadingComics!!.listAvailableComics().lowercase().contains("no available comics")
            )
        }


        @Test
        fun `listSoldComics returns no sold comics when ArrayList is empty`() {
            assertEquals(0, loadingComics!!.numberOfSoldComics())
            assertTrue(
                loadingComics!!.listSoldComics().lowercase().contains("no sold comics")
            )
        }


        @Nested
        inner class UpdateComics {

            @Test
            fun `updating a comic that does not exist returns false`() {
                assertFalse(
                    storingComics!!.update(
                        4,
                        Comic(
                            comicTitle = "Updating Comic",
                            comicWriter = "Writer",
                            comicArtist = "Artist",
                            comicPublisher = "Publisher",
                            isComicSold = false,
                            issues = mutableSetOf()
                        )
                    )
                )
                assertFalse(
                    storingComics!!.update(
                        -1,
                        Comic(
                            comicTitle = "Updating Comic",
                            comicWriter = "Writer",
                            comicArtist = "Artist",
                            comicPublisher = "Publisher",
                            isComicSold = false,
                            issues = mutableSetOf()
                        )
                    )
                )
                assertFalse(
                    loadingComics!!.update(
                        0,
                        Comic(
                            comicTitle = "Updating Comic",
                            comicWriter = "Writer",
                            comicArtist = "Artist",
                            comicPublisher = "Publisher",
                            isComicSold = false,
                            issues = mutableSetOf()
                        )
                    )
                )
            }

            @Test
            fun `updating a comic that exists returns true and updates`() {
                //check comic 3 exists and check the contents
                assertEquals(summerHoliday, storingComics!!.findComic(2))
                assertEquals("Summer Holiday", storingComics!!.findComic(2)!!.comicTitle)
                assertEquals("Writer3", storingComics!!.findComic(2)!!.comicWriter)
                assertEquals("Artist3", storingComics!!.findComic(2)!!.comicArtist)
                assertEquals("Publisher3", storingComics!!.findComic(2)!!.comicPublisher)

                //update comic 3 with new information and ensure contents updated successfully
                assertTrue(
                    storingComics!!.update(
                        2,
                        Comic(
                            comicTitle = "Updating Comic",
                            comicWriter = "WriterUpdated",
                            comicArtist = "ArtistUpdated",
                            comicPublisher = "PublisherUpdated",
                            isComicSold = false,
                            issues = mutableSetOf()
                        )
                    )
                )
                assertEquals("Updating Comic", storingComics!!.findComic(2)!!.comicTitle)
                assertEquals("WriterUpdated", storingComics!!.findComic(2)!!.comicWriter)
                assertEquals("ArtistUpdated", storingComics!!.findComic(2)!!.comicArtist)
                assertEquals("PublisherUpdated", storingComics!!.findComic(2)!!.comicPublisher)
            }
        }


    }

    @Nested
    inner class PersistenceTests {

        @Test
        fun `saving and loading an empty collection in XML doesn't crash app`() {
            // Saving an empty comics.xml file.
            val storingComics = ComicAPI(XMLSerializer(File("comics.xml")))
            storingComics.store()

            // Loading the empty comics.xml file into a new object
            val loadedComics = ComicAPI(XMLSerializer(File("comics.xml")))
            loadedComics.load()

            // Comparing the source of the comics (storingComics) with the XML loaded comics (loadedComics)
            assertEquals(0, storingComics.numberOfComics())
            assertEquals(0, loadedComics.numberOfComics())
            assertEquals(storingComics.numberOfComics(), loadedComics.numberOfComics())
        }

        @Test
        fun `saving and loading an loaded collection in XML doesn't lose data`() {
            // Storing 3 comics to the comics.xml file.
            val storingComics = ComicAPI(XMLSerializer(File("comics.xml")))
            storingComics.add(testApp!!)
            storingComics.add(swim!!)
            storingComics.add(summerHoliday!!)
            storingComics.store()

            // Loading comics.xml into a different collection
            val loadedComics = ComicAPI(XMLSerializer(File("comics.xml")))
            loadedComics.load()

            // Comparing the source of the comics (storingComics) with the XML loaded comics (loadedComics)
            assertEquals(3, storingComics.numberOfComics())
            assertEquals(3, loadedComics.numberOfComics())
            assertEquals(storingComics.numberOfComics(), loadedComics.numberOfComics())
            assertEquals(storingComics.findComic(0), loadedComics.findComic(0))
            assertEquals(storingComics.findComic(1), loadedComics.findComic(1))
            assertEquals(storingComics.findComic(2), loadedComics.findComic(2))
        }

        @Test
        fun `saving and loading an empty collection in JSON doesn't crash app`() {
            // Saving an empty comics.json file.
            val storingComics = ComicAPI(JSONSerializer(File("comics.json")))
            storingComics.store()

            // Loading the empty comics.json file into a new object
            val loadedComics = ComicAPI(JSONSerializer(File("comics.json")))
            loadedComics.load()

            // Comparing the source of the comics (storingComics) with the json loaded comics (loadedComics)
            assertEquals(0, storingComics.numberOfComics())
            assertEquals(0, loadedComics.numberOfComics())
            assertEquals(storingComics.numberOfComics(), loadedComics.numberOfComics())
        }

        @Test
        fun `saving and loading an loaded collection in JSON doesn't lose data`() {
            // Storing 3 comics to the comics.json file.
            val storingComics = ComicAPI(JSONSerializer(File("comics.json")))
            storingComics.add(testApp!!)
            storingComics.add(swim!!)
            storingComics.add(summerHoliday!!)
            storingComics.store()

            // Loading comics.json into a different collection
            val loadedComics = ComicAPI(JSONSerializer(File("comics.json")))
            loadedComics.load()

            // Comparing the source of the comics (storingComics) with the json loaded comics (loadedComics)
            assertEquals(3, storingComics.numberOfComics())
            assertEquals(3, loadedComics.numberOfComics())
            assertEquals(storingComics.numberOfComics(), loadedComics.numberOfComics())
            assertEquals(storingComics.findComic(0), loadedComics.findComic(0))
            assertEquals(storingComics.findComic(1), loadedComics.findComic(1))
            assertEquals(storingComics.findComic(2), loadedComics.findComic(2))
        }

        @Test
        fun `saving and loading an empty collection in YAML doesn't crash app`() {
            // Saving an empty comics.yaml file.
            val storingComics = ComicAPI(YAMLSerializer(File("comics.yaml")))
            storingComics.store()

            // Loading the empty comics.yaml file into a new object
            val loadedComics = ComicAPI(YAMLSerializer(File("comics.yaml")))
            loadedComics.load()

            // Comparing the source of the comics (storingComics) with the YAML loaded comics (loadedComics)
            assertEquals(0, storingComics.numberOfComics())
            assertEquals(0, loadedComics.numberOfComics())
            assertEquals(storingComics.numberOfComics(), loadedComics.numberOfComics())
        }

        @Test
        fun `saving and loading a loaded collection in YAML doesn't lose data`() {
            // Storing 3 comics to the comics.yaml file.
            val storingComics = ComicAPI(YAMLSerializer(File("comics.yaml")))
            storingComics.add(testApp!!)
            storingComics.add(swim!!)
            storingComics.add(summerHoliday!!)
            storingComics.store()

            // Loading comics.yaml into a different collection
            val loadedComics = ComicAPI(YAMLSerializer(File("comics.yaml")))
            loadedComics.load()

            // Comparing the source of the comics (storingComics) with the YAML loaded comics (loadedComics)
            assertEquals(3, storingComics.numberOfComics())
            assertEquals(3, loadedComics.numberOfComics())
            assertEquals(storingComics.numberOfComics(), loadedComics.numberOfComics())
            assertEquals(storingComics.findComic(0), loadedComics.findComic(0))
            assertEquals(storingComics.findComic(1), loadedComics.findComic(1))
            assertEquals(storingComics.findComic(2), loadedComics.findComic(2))
        }
    }

}