package commands

import collection.CollectionManager
import collection.ConsolePrinter
import model.Dragon

class RemoveFirstCommand(
    private val collectionManager: CollectionManager,
    private val printer: ConsolePrinter
) : Command {
    override val name = "remove_first"
    override val description = "удалить первый элемент из коллекции"

    override fun execute(args: List<String>, dragon: Dragon?): Boolean {
        if (args.isNotEmpty()) {
            printer.printError("команда 'remove_first' не принимает аргументы")
            return false
        }

        collectionManager.removeFirst()
        return true
    }
}