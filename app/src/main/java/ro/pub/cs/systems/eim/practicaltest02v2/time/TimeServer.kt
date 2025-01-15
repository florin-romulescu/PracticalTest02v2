package ro.pub.cs.systems.eim.practicaltest02v2.time

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import ro.pub.cs.systems.eim.practicaltest02v2.server.DictData
import java.io.BufferedReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.net.URL

class TimeServer(val port: Int) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private var serverSocket: ServerSocket? = null
    private var onTimeReceived: ((String) -> Unit)? = null

    fun start() {
        scope.launch {
            serverSocket = ServerSocket(port)
            println("Dictionary Server started on port $port")

            while (true) {
                val clientSocket = serverSocket?.accept() ?: continue
                handleClient(clientSocket)
            }
        }
    }

    fun handleClient(clientSocket: Socket) {
        scope.launch {
            try {
                val reader = BufferedReader(clientSocket.getInputStream().reader())
                val timeString = reader.readLine()
                onTimeReceived?.invoke(timeString)
                clientSocket.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun setOnTimeReceivedListener(listener: (String) -> Unit) {
        onTimeReceived = listener
    }

    fun stop() {
        try {
            serverSocket?.close()
            scope.cancel()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}