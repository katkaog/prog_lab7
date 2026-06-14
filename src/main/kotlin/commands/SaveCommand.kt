package commands

import collection.CollectionManager
import collection.FileManager
import collection.ConsolePrinter
import model.Dragon

class SaveCommand(
    private val collectionManager: CollectionManager,
    private val fileManager: FileManager,
    private val printer: ConsolePrinter
) : Command {
    override val name = "save"
    override val description = "сохранить коллекцию в файл (команда сервера)"

    override fun execute(args: List<String>, dragon: Dragon?): Boolean {
        if (args.isNotEmpty()) {
            printer.printError("команда 'save' не принимает аргументы")
            return false
        }

        fileManager.save(collectionManager.getCollection())
        return true
    }
}