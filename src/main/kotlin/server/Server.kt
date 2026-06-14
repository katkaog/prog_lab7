package server

import network.Request
import network.Response
import collection.CommandManager
import java.io.*
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.*
import java.util.logging.Logger
import java.util.logging.Level

private data class ClientState(
    var expectedLength: Int? = null,
    val data: ByteArrayOutputStream = ByteArrayOutputStream()
)

class Server(
    private val port: Int,
    private val commandManager: CommandManager
) {
    private val logger = Logger.getLogger("ServerLogger")
    private lateinit var selector: Selector

    fun start() {
        selector = Selector.open()
        val serverChannel = ServerSocketChannel.open()
        serverChannel.configureBlocking(false)  // неблокирующий режим
        serverChannel.bind(InetSocketAddress(port))
        serverChannel.register(selector, SelectionKey.OP_ACCEPT)

        logger.info("Сервер запущен на порту $port и готов принимать соединения")

        while (true) {
            if (selector.select(500) == 0) continue

            val keys = selector.selectedKeys().iterator()
            while (keys.hasNext()) {
                val key = keys.next()
                keys.remove()
                if (!key.isValid) continue
                try {
                    when {
                        key.isAcceptable -> acceptConnection(key)
                        key.isReadable   -> handleClientRequest(key)
                    }
                } catch (e: Exception) {
                    logger.warning("Ошибка при обработке ключа: ${e.message}")
                    key.channel().close()
                    key.cancel()
                }
            }
        }
    }

    private fun acceptConnection(key: SelectionKey) {
        val serverChannel = key.channel() as ServerSocketChannel
        val clientChannel = serverChannel.accept()
        clientChannel.configureBlocking(false)
        clientChannel.register(selector, SelectionKey.OP_READ, ClientState())
        logger.info("Новое подключение от ${clientChannel.remoteAddress}")
    }

    private fun handleClientRequest(key: SelectionKey) {
        val channel = key.channel() as SocketChannel
        val state = key.attachment() as ClientState
        val buffer = ByteBuffer.allocate(4096)

        val bytesRead = channel.read(buffer)
        if (bytesRead == -1) {
            logger.info("Клиент ${channel.remoteAddress} отключился")
            channel.close()
            key.cancel()
            return
        }

        buffer.flip()
        while (buffer.hasRemaining()) {
            state.data.write(buffer.get().toInt())
        }

        processData(state, channel)
    }

    private fun processData(state: ClientState, channel: SocketChannel) {
        if (state.expectedLength == null && state.data.size() >= 4) {
            val lengthBytes = state.data.toByteArray().copyOfRange(0, 4)
            state.expectedLength = ByteBuffer.wrap(lengthBytes).int
            val remaining = state.data.toByteArray().copyOfRange(4, state.data.size())
            state.data.reset()
            state.data.write(remaining)
        }

        val length = state.expectedLength
        if (length != null && state.data.size() >= length) {
            val objectBytes = state.data.toByteArray().copyOfRange(0, length)

            ObjectInputStream(ByteArrayInputStream(objectBytes)).use { ois ->
                val request = ois.readObject() as? Request
                if (request != null) {
                    logger.info("Получен запрос: ${request.commandName} (${objectBytes.size} байт)")

                    val response = processRequest(request)

                    sendResponse(channel, response)
                }
            }

            val remainingData = state.data.toByteArray().copyOfRange(length, state.data.size())
            state.data.reset()
            state.data.write(remainingData)
            state.expectedLength = null

            if (state.data.size() > 0) {
                processData(state, channel)
            }
        }
    }

    private fun processRequest(request: Request): Response {
        val success = commandManager.execute(request.commandName, request.args, request.dragon)
        var resultText = commandManager.printer.getAndClearBuffer()

        // Убираем упоминание save из help
        if (request.commandName == "help") {
            resultText = resultText.lines()
                .filter { line -> !line.contains("save", ignoreCase = true) }
                .joinToString("\n")
        }
        logger.info("Команда '${request.commandName}' выполнена, success=$success")
        return Response(resultText, success)
    }

    private fun sendResponse(channel: SocketChannel, response: Response) {
        try {
            val bos = ByteArrayOutputStream()
            ObjectOutputStream(bos).use { oos -> oos.writeObject(response) }
            val responseData = bos.toByteArray()
            channel.write(ByteBuffer.wrap(responseData))
            logger.info("Ответ отправлен клиенту (${responseData.size} байт)")
        } catch (e: Exception) {
            logger.log(Level.SEVERE, "Ошибка при отправке ответа:", e)
        }
    }
}
