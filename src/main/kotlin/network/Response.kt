package network

import java.io.Serializable

data class Response(
    val message: String,
    val success: Boolean,
    val dragons: List<model.Dragon>? = null,
    val commandInfo: Map<String, String>? = null
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 1234567L
    }
}