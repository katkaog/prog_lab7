package commands

import collection.CollectionManager
import collection.ConsolePrinter
import id.IdGenerator
import model.Dragon
import java.time.ZonedDateTime

class AddCommand(
    private val collectionManager: CollectionManager,
    private val printer: ConsolePrinter
) : Command {
    override val name = "add"
    override val description = "добавить новый элемент в коллекцию"

    override fun execute(args: List<String>, dragon: Dragon?): Boolean {
        if (dragon == null) {
            printer.printError("Сервер не получил объект дракона для добавления")
            return false
        }

        val finalDragon = dragon.copy(
            id = IdGenerator.generate(),
            creationDate = ZonedDateTime.now()
        )
        collectionManager.add(finalDragon)
        return true
    }
}