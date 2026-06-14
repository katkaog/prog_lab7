package collection

import commands.Command
import model.Dragon

class CommandManager(val printer: ConsolePrinter) {
    private val commands = mutableMapOf<String, Command>()

    fun register(command: Command) {
        commands[command.name] = command
    }

    fun execute(name: String, args: List<String>, dragon: Dragon? = null): Boolean {
        val command = commands[name]
        if (command == null) {
            printer.printError("Команда '$name' не найдена")
            return false
        }
        return command.execute(args, dragon)
    }

    fun getCommands() = commands.values.toList()
}