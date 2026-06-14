package commands

import collection.CollectionManager
import collection.ConsolePrinter
import model.Dragon

class HeadCommand(
    private val collectionManager: CollectionManager,
    private val printer: ConsolePrinter
) : Command {
    override val name = "head"
    override val description = "вывести первый элемент коллекции"

    override fun execute(args: List<String>, dragon: Dragon?): Boolean {
        if (args.isNotEmpty()) {
            printer.printError("команда 'head' не принимает аргументы")
            return false
        }
        collectionManager.head()
        return true
    }
}