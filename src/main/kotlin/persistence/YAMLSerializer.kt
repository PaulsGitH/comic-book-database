package persistence

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import models.Comic
import models.Issue
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.lang.Exception

class YAMLSerializer(private val file: File) : Serializer {

    //Researched how to return multiple values in Kotlin, discovered return Pair
    //https://stackoverflow.com/questions/65034880/how-to-return-two-values-in-kotlin
    @Throws(Exception::class)
    override fun read(): Any {
        val objectMapper = ObjectMapper(YAMLFactory())
        val reader = FileReader(file)
        val comicList = objectMapper.readValue(reader, object : TypeReference<List<Comic>>() {})
        val issueList = objectMapper.readValue(reader, object : TypeReference<List<Issue>>() {})
        reader.close()
        return Pair (comicList, issueList)
    }

    @Throws(Exception::class)
    override fun write(obj: Any?) {
        val objectMapper = ObjectMapper(YAMLFactory())
        val writer = FileWriter(file)
        objectMapper.writeValue(writer, obj)
        writer.close()
    }
}