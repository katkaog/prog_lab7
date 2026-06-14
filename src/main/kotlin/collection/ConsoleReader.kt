package collection

import java.util.Scanner


class ConsoleReader(
    var scanner: Scanner,
    val printer: ConsolePrinter
) {

    fun readString(message: String): String {
        while (true) {
            printer.print("$message: ")
            val input = scanner.nextLine().trim()
            if (input.isNotEmpty()) return input
            printer.printError("поле не может быть пустым")
        }
    }

    fun readInt(message: String, min: Int = Int.MIN_VALUE): Int {
        while (true) {
            printer.print("$message: ")
            val input = scanner.nextLine().trim()
            val value = input.toIntOrNull()
            if (value != null && value > min) return value
            printer.printError("введите целое число больше $min")
        }
    }

    fun readDouble(message: String, min: Double = Double.NEGATIVE_INFINITY): Double {
        while (true) {
            printer.print("$message: ")
            val input = scanner.nextLine().trim()
            val value = input.toDoubleOrNull()
            if (value != null && value > min) return value
            printer.printError("введите число больше $min")
        }
    }

    fun readLong(message: String): Long {
        while (true) {
            printer.print("$message: ")
            val input = scanner.nextLine().trim()
            val value = input.toLongOrNull()
            if (value != null) return value
            printer.printError("введите целое число (Long)")
        }
    }

    inline fun <reified T : Enum<T>> readEnum(message: String, canBeNull: Boolean): T? {
        val values = enumValues<T>()
        val list = values.joinToString(", ")
        while (true) {
            printer.println("$message ($list)${if (canBeNull) " [Enter для null]" else ""}: ")
            printer.print("> ")

            val input = scanner.nextLine().trim().uppercase()

            if (input.isEmpty() && canBeNull) return null

            try {
                return enumValueOf<T>(input)
            } catch (_: Exception) {
                printer.printError("выберите значение из списка: $list")
            }
        }
    }
}