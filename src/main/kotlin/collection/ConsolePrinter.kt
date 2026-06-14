package collection

class ConsolePrinter {
    private val responseBuffer = StringBuilder()

    fun println(message: Any?) {
        val text = message.toString()
        kotlin.io.println(text)
        responseBuffer.append(text).append("\n")
    }

    fun print(message: Any?) {
        val text = message.toString()
        kotlin.io.print(text)
        responseBuffer.append(text)
    }

    fun printError(message: String) {
        val text = "Ошибка: $message"
        kotlin.io.println(text)
        responseBuffer.append(text).append("\n")
    }

    fun printSuccess(message: String) {
        val text = "Успешно: $message"
        kotlin.io.println(text)
        responseBuffer.append(text).append("\n")
    }

    fun printHeader(message: String) {
        val text = "\n$message"
        kotlin.io.println(text)
        responseBuffer.append(text).append("\n")
    }

    fun getAndClearBuffer(): String {
        val result = responseBuffer.toString()
        responseBuffer.clear()
        return result
    }
}