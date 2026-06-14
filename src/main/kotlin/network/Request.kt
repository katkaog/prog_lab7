package network

import model.Dragon
import java.io.Serializable

data class Request(
    val commandName: String,
    val args: List<String> = emptyList(),
    val dragon: Dragon? = null
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 1234567L
    }
}