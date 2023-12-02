package utils

import models.Issue
import models.Comic

object Utilities {

    // NOTE: JvmStatic annotation means that the methods are static i.e. we can call them over the class
    //      name; we don't have to create an object of Utilities to use them.

    @JvmStatic
    fun formatListString(notesToFormat: List<Comic>): String =
        notesToFormat
            .joinToString(separator = "\n") { comic ->  "$comic" }

    @JvmStatic
    fun formatSetString(itemsToFormat: Set<Issue>): String =
        itemsToFormat
            .joinToString(separator = "\n") { issue ->  "\t$issue" }

}
