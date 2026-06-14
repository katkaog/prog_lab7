package model

import java.io.Serializable

data class Person(
    val name: String,
    val height: Int,
    val eyeColor: Color
) : Serializable{
    init {
        require(name.isNotBlank()) { "Имя человека не может быть пустым" }
        require(height > 0) { "Рост должен быть больше 0" }
    }
}
