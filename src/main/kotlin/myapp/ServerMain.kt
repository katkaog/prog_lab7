package myapp

import collection.*
import commands.*
import id.IdGenerator
import server.Server
import java.util.Scanner
import java.util.logging.*

fun main() {
    setupServerLogging()
    val logger = Logger.getLogger("ServerLogger")
    logger.info("Запуск серверного приложения")

    val printer = ConsolePrinter()
    val collectionManager = CollectionManager(printer)

    val filePath = System.getenv("MY_LAB_FILE") ?: "data.json"
    val fileManager = FileManager(filePath, printer)

    try {
        val loadedData = fileManager.load()
        loadedData.forEach { dragon -> collectionManager.add(dragon) }
        val maxId = collectionManager.getCollection().maxOfOrNull { d -> d.id } ?: 0
        IdGenerator.updateId(maxId)
        logger.info("Коллекция загружена. Элементов: ${collectionManager.getCollection().size}, следующий ID: ${maxId + 1}")
    } catch (e: Exception) {
        logger.log(Level.SEVERE, "Ошибка при загрузке коллекции", e)
    }

    val commandManager = CommandManager(printer)
    commandManager.register(HelpCommand(commandManager, printer))
    commandManager.register(InfoCommand(collectionManager, printer))
    commandManager.register(ShowCommand(collectionManager, printer))
    commandManager.register(AddCommand(collectionManager, printer))
    commandManager.register(UpdateCommand(collectionManager, printer))
    commandManager.register(AddIfMinCommand(collectionManager, printer))
    commandManager.register(RemoveByIdCommand(collectionManager, printer))
    commandManager.register(ClearCommand(collectionManager, printer))
    commandManager.register(RemoveFirstCommand(collectionManager, printer))
    commandManager.register(HeadCommand(collectionManager, printer))
    commandManager.register(MinByNameCommand(collectionManager, printer))
    commandManager.register(PrintAscendingCommand(collectionManager, printer))
    commandManager.register(PrintDescendingCommand(collectionManager, printer))
    commandManager.register(SaveCommand(collectionManager, fileManager, printer))

    Runtime.getRuntime().addShutdownHook(Thread {
        logger.info("Завершение работы. Автосохранение коллекции...")
        fileManager.save(collectionManager.getCollection())
        logger.info("Коллекция сохранена при завершении")
    })

    val serverConsoleThread = Thread({
        val scanner = Scanner(System.`in`)
        println("Серверные команды: save, exit")
        while (true) {
            print("server> ")
            if (!scanner.hasNextLine()) break
            when (scanner.nextLine().trim().lowercase()) {
                "save" -> {
                    fileManager.save(collectionManager.getCollection())
                    logger.info("Ручное сохранение коллекции выполнено")
                }
                "exit" -> {
                    logger.info("Получена команда exit от администратора сервера")
                    System.exit(0)
                }
                "" -> {}
                else -> println("Неизвестная серверная команда. Доступно: save, exit")
            }
        }
    }, "server-console")
    serverConsoleThread.isDaemon = true
    serverConsoleThread.start()

    val port = 1235
    logger.info("Запуск прослушивания на порту $port")
    val server = Server(port, commandManager)
    try {
        server.start()
    } catch (e: Exception) {
        println("ПОЛНАЯ ОШИБКА:")
        e.printStackTrace() // Это выведет в консоль красным причину (например, Address already in use)
        logger.log(Level.SEVERE, "Критическая ошибка сервера", e)
    }
//    try {
//        server.start()
//    } catch (e: Exception) {
//        logger.log(Level.SEVERE, "Критическая ошибка сервера", e)
//    }
}

private fun setupServerLogging() {
    val rootLogger = Logger.getLogger("")
    rootLogger.handlers.forEach { rootLogger.removeHandler(it) }

    val consoleHandler = ConsoleHandler()
    consoleHandler.level = Level.INFO
    consoleHandler.formatter = object : SimpleFormatter() {
        override fun format(record: LogRecord): String {
            val time = java.time.LocalTime.now().toString().take(12)
            return "[$time] [${record.level}] [${record.loggerName.substringAfterLast('.')}] ${record.message}\n"
        }
    }
    
    try {
        val fileHandler = FileHandler("server.log", true)
        fileHandler.level = Level.ALL
        fileHandler.formatter = SimpleFormatter()
        rootLogger.addHandler(fileHandler)
    } catch (e: Exception) {
        System.err.println("Не удалось создать файл лога: ${e.message}")
    }

    rootLogger.addHandler(consoleHandler)
    rootLogger.level = Level.ALL

    Logger.getLogger("ServerLogger").level = Level.ALL
    Logger.getLogger("server.Server").level = Level.ALL
    Logger.getLogger("collection.CollectionManager").level = Level.ALL
}
