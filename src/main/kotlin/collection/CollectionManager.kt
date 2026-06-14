package collection

import model.Dragon
import java.time.ZonedDateTime
import java.util.*
import java.util.logging.Logger

class CollectionManager(private val printer: ConsolePrinter) {
    private val collection = ArrayDeque<Dragon>()
    private val initializationDate: ZonedDateTime = ZonedDateTime.now()
    private val logger = Logger.getLogger(CollectionManager::class.java.name)

    fun getInitializationDate(): ZonedDateTime = initializationDate
    fun getCollection(): ArrayDeque<Dragon> = collection

    fun show() {
        if (collection.isEmpty()) {
            printer.printError("Коллекция пустая")
        } else {
            printer.printHeader("Элементы коллекции (отсортированы по возрасту)")
            collection.stream()
                .sorted()
                .forEach { dragon -> printer.println(dragon.toString()) }
        }
    }

    fun add(dragon: Dragon) {
        collection.add(dragon)
        logger.fine("Добавлен дракон: ${dragon.name}, id=${dragon.id}")
    }

    fun clear() {
        collection.clear()
        id.IdGenerator.reset()
        printer.printSuccess("Коллекция очищена, счётчик ID сброшен")
        logger.info("Коллекция очищена")
    }

    fun head() {
        val first = collection.stream().findFirst().orElse(null)
        if (first == null) {
            printer.printError("Коллекция пустая")
        } else {
            printer.printHeader("Первый элемент очереди")
            printer.println(first.toString())
        }
    }

    fun removeFirst() {
        if (collection.isNotEmpty()) {
            collection.removeFirst()
            printer.printSuccess("Первый элемент удалён")
        } else {
            printer.printError("Коллекция уже пустая")
        }
    }

    fun getInfo(): String {
        val count = collection.stream().count()
        return """
        Тип коллекции: java.util.ArrayDeque
        Дата инициализации: ${getInitializationDate()}
        Количество элементов: $count
    """.trimIndent()
    }

    fun removeById(id: Int): Boolean {
        return collection.removeIf { it.id == id }
    }

    fun getById(id: Int): Dragon? {
        return collection.stream()
            .filter { it.id == id }
            .findFirst()
            .orElse(null)
    }

    fun addIfMin(newDragon: Dragon): Boolean {
        val isMin = collection.stream()
            .allMatch { existing -> newDragon < existing }
        return if (isMin || collection.isEmpty()) {
            add(newDragon)
            true
        } else false
    }


    fun minByName(): Dragon? {
        return collection.stream()
            .min(compareBy { it.name.length })
            .orElse(null)
    }



    fun getSortedList(): List<Dragon> {
        return collection.stream()
            .sorted()
            .toList()
    }

    fun getSortedDescending(): List<Dragon> {
        return collection.stream()
            .sorted(Comparator.reverseOrder())
            .toList()
    }
}
