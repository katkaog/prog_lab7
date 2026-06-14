package commands

import model.Dragon

interface Command {
    val name: String
    val description: String

    fun execute(args: List<String>, dragon: Dragon? = null): Boolean
}