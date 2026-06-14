package commands

import collection.CollectionManager
import collection.ConsolePrinter
import id.IdGenerator
import model.Dragon
import java.time.ZonedDateTime

class AddIfMinCommand(
    private val collectionManager: CollectionManager,
    private val printer: ConsolePrinter
) : Command {
    override val name = "add_if_min"
    override val description = "добавить новый элемент, если его значение меньше, чем у наименьшего элемента"

    override fun execute(args: List<String>, dragon: Dragon?): Boolean {
        if (dragon == null) {
            printer.printError("Команда ожидает объект дракона")
            return false
        }

        val finalDragon = dragon.copy(
            id = IdGenerator.generate(),
            creationDate = ZonedDateTime.now()
        )

        val added = collectionManager.addIfMin(finalDragon)

        if (added) {
            printer.printSuccess("Дракон успешно добавлен (он меньше минимального)")
        } else {
            printer.println("Дракон не добавлен (он не меньше минимального)")
        }
        return true
    }
}