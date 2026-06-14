package commands

import collection.CollectionManager
import collection.ConsolePrinter
import model.Dragon

class ClearCommand(
    private val collectionManager: CollectionManager,
    private val printer: ConsolePrinter
) : Command {
    override val name = "clear"
    override val description = "очистить коллекцию"

    override fun execute(args: List<String>, dragon: Dragon?): Boolean {
        if (args.isNotEmpty()) {
            printer.printError("команда 'clear' не принимает аргументы")
            return false
        }
        collectionManager.clear()
        return true
    }
}