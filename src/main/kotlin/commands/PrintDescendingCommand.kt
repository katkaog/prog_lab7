package commands

import collection.CollectionManager
import collection.ConsolePrinter
import model.Dragon

class PrintDescendingCommand(
    private val collectionManager: CollectionManager,
    private val printer: ConsolePrinter
) : Command {
    override val name = "print_descending"
    override val description = "вывести элементы коллекции в порядке убывания (по возрасту)"

    override fun execute(args: List<String>, dragon: Dragon?): Boolean {
        if (args.isNotEmpty()) {
            printer.printError("команда 'print_descending' не принимает аргументы")
            return false
        }
        val sorted = collectionManager.getSortedDescending()
        if (sorted.isEmpty()) {
            printer.printError("Коллекция пустая")
        } else {
            printer.printHeader("Элементы в порядке убывания (по возрасту)")
            sorted.forEach { d -> printer.println(d.toString()) }
        }
        return true
    }
}
