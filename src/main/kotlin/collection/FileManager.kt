package collection

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import model.Dragon
import java.io.*
import java.time.ZonedDateTime
import java.util.ArrayDeque

class FileManager(
    private val filePath: String,
    private val printer: ConsolePrinter
) {

    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(ZonedDateTime::class.java, ZonedDateTimeAdapter())
        .setPrettyPrinting()
        .create()

    fun save(collection: ArrayDeque<Dragon>) {
        try {
            val file = File(filePath)
            BufferedWriter(OutputStreamWriter(FileOutputStream(file))).use { writer ->
                gson.toJson(collection, writer)
            }
            printer.printSuccess("Коллекция успешно сохранена в файл: $filePath")
        } catch (e: IOException) {
            printer.printError("Ошибка при сохранении: ${e.message}")
        }
    }

    fun load(): ArrayDeque<Dragon> {
        val file = File(filePath)
        if (!file.exists()) {
            printer.println("Файл не найден. Будет создана пустая коллекция")
            return ArrayDeque()
        }

        return try {
            BufferedReader(InputStreamReader(FileInputStream(file))).use { reader ->
                val type = object : TypeToken<ArrayDeque<Dragon>>() {}.type
                val loaded: ArrayDeque<Dragon>? = gson.fromJson(reader, type)
                loaded ?: ArrayDeque()
            }
        } catch (e: Exception) {
            printer.printError("Ошибка при загрузке файла: ${e.message}")
            ArrayDeque()
        }
    }
}