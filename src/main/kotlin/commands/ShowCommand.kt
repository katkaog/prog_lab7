package commands

import collection.CollectionManager
import collection.ConsolePrinter
import model.Dragon

class ShowCommand(
    private val collectionManager: CollectionManager,
    private val printer: ConsolePrinter
) : Command {
    override val name = "show"
    override val description = "вывести все элементы коллекции в строковом представлении"

    override fun execute(args: List<String>, dragon: Dragon?): Boolean {
        if (args.isNotEmpty()) {
            printer.printError("команда 'show' не принимает аргументы.")
            return false
        }
        collectionManager.show()
        return true
    }
}