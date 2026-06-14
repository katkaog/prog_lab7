package commands

import collection.CollectionManager
import collection.ConsolePrinter
import model.Dragon

class MinByNameCommand(
    private val collectionManager: CollectionManager,
    private val printer: ConsolePrinter
) : Command {
    override val name = "min_by_name"
    override val description = "вывести объект, имя которого является минимальным"

    override fun execute(args: List<String>, dragon: Dragon?): Boolean {
        if (args.isNotEmpty()) {
            printer.printError("команда 'min_by_name' не принимает аргументы")
            return false
        }

        val minDragon = collectionManager.minByName()

        if (minDragon == null) {
            printer.printError("Коллекция пустая")
        } else {
            printer.println("Дракон с минимальным именем:")
            printer.println(minDragon.toString())
        }
        return true
    }
}