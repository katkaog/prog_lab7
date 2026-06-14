package model

import java.io.Serializable

data class Coordinates(
    val x: Double,
    val y: Long
) : Serializable {
    init {
        require(x > -5) { "Координата X должна быть больше -5" }
    }
}