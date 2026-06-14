package commands

import collection.CollectionManager
import collection.ConsolePrinter
import model.*

class UpdateCommand(
    private val collectionManager: CollectionManager,
    private val printer: ConsolePrinter
) : Command {
    override val name = "update"
    override val description = "обновить значение элемента коллекции, id которого равен заданному"

    override fun execute(args: List<String>, dragon: Dragon?): Boolean {
        if (args.isEmpty()) {
            printer.printError("Введите ID после названия команды. Пример: update 5")
            return false
        }

        val id = args[0].toIntOrNull()
        if (id == null) {
            printer.printError("ID должен быть целым числом")
            return false
        }

        val oldDragon = collectionManager.getById(id)
        if (oldDragon == null) {
            printer.printError("Дракон с ID $id не найден в коллекции")
            return false
        }

        if (dragon == null) {
            printer.printError("Сервер не получил новые данные для обновления")
            return false
        }

        val updatedDragon = dragon.copy(
            id = oldDragon.id,
            creationDate = oldDragon.creationDate
        )

        collectionManager.removeById(id)
        collectionManager.add(updatedDragon)

        printer.printSuccess("Дракон #$id успешно обновлен")
        return true
    }
}