package model

import java.io.Serializable
import java.time.ZonedDateTime

data class Dragon(
    val id: Int,
    val name: String,
    val coordinates: Coordinates,
    val creationDate: ZonedDateTime,
    val age: Int,
    val description: String,
    val wingspan: Int,
    val character: DragonCharacter?,
    val killer: Person?
) : Comparable<Dragon>, Serializable {

    init {
        require(id > 0) { "ID должен быть больше 0" }
        require(name.isNotBlank()) { "Имя не может быть пустым" }
        require(age > 0) { "Возраст должен быть больше 0" }
        require(wingspan > 0) { "Размах крыльев должен быть больше 0" }
    }

    override fun compareTo(other: Dragon): Int {
        return this.age.compareTo(other.age)
    }

    override fun toString(): String {
        return """
            Dragon #$id
            - Имя: $name
            - Возраст: $age
            - Описание: $description
            - Координаты: $coordinates
            - Характер: ${character ?: "не определен"}
            - Убийца: ${killer ?: "отсутствует"}
            - Дата создания: $creationDate
        """.trimIndent()
    }
}