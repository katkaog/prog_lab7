package myapp

import client.NetworkClient
import collection.ConsolePrinter
import collection.ConsoleReader
import network.Request
import model.*
import java.util.*
import java.time.ZonedDateTime
import java.io.File

private val KNOWN_COMMANDS = setOf(
    "help", "info", "show", "add", "update", "add_if_min",
    "remove_by_id", "clear", "remove_first", "head",
    "min_by_name", "print_ascending", "print_descending",
    "execute_script", "exit"
)

private val COMMANDS_WITH_ID = setOf("update", "remove_by_id")

private val COMMANDS_WITH_DRAGON = setOf("add", "update", "add_if_min")

private val COMMANDS_NO_ARGS = setOf(
    "help", "info", "show", "clear", "remove_first",
    "head", "min_by_name", "print_ascending", "print_descending"
)

fun main() {
    java.util.logging.LogManager.getLogManager().reset()

    val printer = ConsolePrinter()
    val scanner = Scanner(System.`in`)
    val reader = ConsoleReader(scanner, printer)
    val netClient = NetworkClient("localhost", 1235)

    printer.printHeader("Клиент запущен. Введите help для списка команд")

    val scriptStack = mutableSetOf<String>()

    fun handleCommand(commandName: String, args: List<String>) {
        if (commandName == "save") {
            printer.printError("Команда 'save' недоступна клиенту (только серверная команда)")
            return
        }

        if (commandName == "exit") {
            printer.println("Завершение работы клиента")
            System.exit(0)
        }

        if (commandName !in KNOWN_COMMANDS) {
            printer.printError("Неизвестная команда: '$commandName'. Введите help для списка команд")
            return
        }

        if (commandName in COMMANDS_NO_ARGS && args.isNotEmpty()) {
            printer.printError("Команда '$commandName' не принимает аргументы")
            return
        }

        if (commandName in COMMANDS_WITH_ID) {
            if (args.isEmpty()) {
                printer.printError("Команда '$commandName' требует аргумент ID. Пример: $commandName 5")
                return
            }
            if (args[0].toIntOrNull() == null) {
                printer.printError("ID должен быть целым числом, получено: '${args[0]}'")
                return
            }
        }

        if (commandName == "execute_script") {
            if (args.isEmpty()) {
                printer.printError("Укажите имя файла. Пример: execute_script script.txt")
                return
            }
            val file = File(args[0])
            if (!file.exists()) {
                printer.printError("Файл '${args[0]}' не найден")
                return
            }
            if (scriptStack.contains(file.absolutePath)) {
                printer.printError("Рекурсия: скрипт '${args[0]}' уже выполняется")
                return
            }
            scriptStack.add(file.absolutePath)
            try {
                file.bufferedReader().use { br ->
                    br.forEachLine { line ->
                        val parts = line.trim().split(" ").filter { it.isNotEmpty() }
                        if (parts.isNotEmpty()) {
                            handleCommand(parts[0].lowercase(), parts.drop(1))
                        }
                    }
                }
            } catch (e: Exception) {
                printer.printError("Ошибка скрипта: ${e.message}")
            } finally {
                scriptStack.remove(file.absolutePath)
            }
            return
        }

        var dragonToSend: Dragon? = null
        if (commandName in COMMANDS_WITH_DRAGON) {
            try {
                dragonToSend = askDragon(reader)
            } catch (e: Exception) {
                printer.printError("Ввод прерван. Команда отменена")
                return
            }
        }

        val request = Request(commandName, args, dragonToSend)
        val response = netClient.sendAndReceive(request)

        if (response.success) {
            printer.println(response.message)
        } else {
            printer.printError(response.message)
        }
    }

    while (true) {
        printer.print("> ")
        if (!scanner.hasNextLine()) break
        val line = scanner.nextLine().trim()
        if (line.isEmpty()) continue

        val parts = line.split(" ").filter { it.isNotEmpty() }
        handleCommand(parts[0].lowercase(), parts.drop(1))
    }
}

fun askDragon(reader: ConsoleReader): Dragon {
    val name = reader.readString("Введите имя дракона")
    val x = reader.readDouble("Введите X (> -5)", min = -5.0)
    val y = reader.readLong("Введите Y")
    val age = reader.readInt("Введите возраст (> 0)", min = 0)
    val desc = reader.readString("Введите описание")
    val wingspan = reader.readInt("Введите размах крыльев (> 0)", min = 0)
    val character = reader.readEnum<DragonCharacter>("Выберите характер (WISE/EVIL/GOOD)", canBeNull = true)

    var hasKiller = false
    while (true) {
        val input = reader.readString("Добавить убийцу? (y/n)").lowercase()
        if (input == "y") { hasKiller = true; break }
        if (input == "n") { hasKiller = false; break }
        reader.printer.printError("Введите y или n")
    }

    val killer = if (hasKiller) {
        val kname = reader.readString("Имя убийцы")
        val kheight = reader.readInt("Рост убийцы", min = 0)
        val kcolor = reader.readEnum<Color>("Цвет глаз (GREEN/BLUE/WHITE/BROWN)", canBeNull = false)!!
        Person(kname, kheight, kcolor)
    } else null

    return Dragon(1, name, Coordinates(x, y), ZonedDateTime.now(), age, desc, wingspan, character, killer)
}
