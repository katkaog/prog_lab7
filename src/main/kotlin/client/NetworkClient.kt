package client

import network.Request
import network.Response
import java.io.*
import java.net.InetSocketAddress
import java.net.Socket

class NetworkClient(private val host: String, private val port: Int) {

    fun sendAndReceive(request: Request): Response {
        return try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(host, port), 5000)
                socket.soTimeout = 10000

                val bos = ByteArrayOutputStream()
                ObjectOutputStream(bos).use { oos ->
                    oos.writeObject(request)
                }
                val requestBytes = bos.toByteArray()

                val out = DataOutputStream(socket.getOutputStream())
                out.writeInt(requestBytes.size)
                out.write(requestBytes)
                out.flush()

                val inputStream = ObjectInputStream(socket.getInputStream())
                inputStream.readObject() as Response
            }
        } catch (e: java.net.ConnectException) {
            Response("Сервер недоступен. Попробуйте позже", false)
        } catch (e: java.net.SocketTimeoutException) {
            Response("Сервер не ответил вовремя. Попробуйте позже", false)
        } catch (e: Exception) {
            Response("Ошибка соединения с сервером: ${e.message}", false)
        }
    }
}
