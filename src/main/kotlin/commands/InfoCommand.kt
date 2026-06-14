package commands

import collection.CollectionManager
import collection.ConsolePrinter
import model.Dragon

class InfoCommand(
    private val collectionManager: CollectionManager,
    private val printer: ConsolePrinter
) : Command {
    override val name = "info"
    override val description = "вывести информацию о коллекции"

    override fun execute(args: List<String>, dragon: Dragon?): Boolean {
        if (args.isNotEmpty()) {
            printer.printError("команда 'info' не принимает аргументы.")
            return false
        }

        printer.println(collectionManager.getInfo())
        return true
    }
}