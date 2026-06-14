package commands

import collection.CollectionManager
import collection.ConsolePrinter
import model.Dragon

class RemoveByIdCommand(
    private val collectionManager: CollectionManager,
    private val printer: ConsolePrinter
) : Command {
    override val name = "remove_by_id"
    override val description = "удалить элемент из коллекции по его id"

    override fun execute(args: List<String>, dragon: Dragon?): Boolean {
        if (args.isEmpty()) {
            printer.printError("Введите ID после названия команды")
            return false
        }

        val id = args[0].toIntOrNull()
        if (id == null) {
            printer.printError("ID должен быть целым числом")
            return false
        }

        val removed = collectionManager.removeById(id)

        if (removed) {
            printer.printSuccess("Дракон с ID $id успешно удален")
        } else {
            printer.printError("Дракон с ID $id не найден в коллекции")
        }

        return true
    }
}