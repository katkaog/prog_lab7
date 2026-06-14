package commands

import collection.CollectionManager
import collection.ConsolePrinter
import model.Dragon

class PrintAscendingCommand(
    private val collectionManager: CollectionManager,
    private val printer: ConsolePrinter
) : Command {
    override val name = "print_ascending"
    override val description = "вывести элементы коллекции в порядке возрастания (по возрасту)"

    override fun execute(args: List<String>, dragon: Dragon?): Boolean {
        if (args.isNotEmpty()) {
            printer.printError("команда 'print_ascending' не принимает аргументы")
            return false
        }
        val sorted = collectionManager.getSortedList()
        if (sorted.isEmpty()) {
            printer.printError("Коллекция пустая")
        } else {
            printer.printHeader("Элементы в порядке возрастания (по возрасту)")
            sorted.forEach { d -> printer.println(d.toString()) }
        }
        return true
    }
}
