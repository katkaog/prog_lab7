package commands

import collection.CommandManager
import collection.ConsolePrinter
import model.Dragon

class HelpCommand(
    private val commandManager: CommandManager,
    private val printer: ConsolePrinter
) : Command {
    override val name = "help"
    override val description = "вывести справку по доступным командам"

    override fun execute(args: List<String>, dragon: Dragon?): Boolean {
        if (args.isNotEmpty()) {
            printer.printError("команда 'help' не принимает аргументы")
            return false
        }

        printer.println("Доступные команды:")

        commandManager.getCommands().forEach { command ->
            printer.println("${command.name} : ${command.description}")
        }
        return true
    }
}