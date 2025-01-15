package ro.pub.cs.systems.eim.practicaltest02v2.server

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.PrintWriter
import java.net.Socket

class DictClient {
    suspend fun requestDefinition(host: String, port: Int, word: String): String {
        return withContext(Dispatchers.IO) {
            Socket(host, port).use { socket ->
                val writer = PrintWriter(socket.getOutputStream(), true)
                val reader = BufferedReader(socket.getInputStream().reader())

                writer.println(word)
                reader.readLine() ?: "No response received"
            }
        }
    }
}