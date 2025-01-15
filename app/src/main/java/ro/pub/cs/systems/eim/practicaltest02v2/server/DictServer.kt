package ro.pub.cs.systems.eim.practicaltest02v2.server

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.BufferedReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.net.URL

class DictServer(private val port: Int) {
    private val url: String = "https://api.dictionaryapi.dev/v2/entries/en"
    private val cache = mutableMapOf<String, DictData>()
    private val scope = CoroutineScope(Dispatchers.IO)
    private var serverSocket: ServerSocket? = null

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
            val reader = BufferedReader(clientSocket.getInputStream().reader())
            val writer = PrintWriter(clientSocket.getOutputStream(), true)

            val word = reader.readLine().toLowerCase()
            val dictionaryData = getDefinition(word)
            writer.println(dictionaryData.definition)
        }
    }

    private suspend fun getDefinition(word: String): DictData {
        return cache[word] ?: fetchDefinition(word).also {
            cache[word] = it
        }
    }

    private suspend fun fetchDefinition(word: String): DictData {
        return withContext(Dispatchers.IO) {
            try {
                val url = "https://api.dictionaryapi.dev/api/v2/entries/en/$word"
                val response = URL(url).readText()
                val jsonArray = JSONArray(response)
                val firstEntry = jsonArray.getJSONObject(0)
                val meanings = firstEntry.getJSONArray("meanings")
                val firstMeaning = meanings.getJSONObject(0)
                val definitions = firstMeaning.getJSONArray("definitions")
                val firstDefinition = definitions.getJSONObject(0).getString("definition")

                DictData(word, firstDefinition)
            } catch (e: Exception) {
                DictData(word, "Definition not found")
            }
        }
    }

    fun stop() {
        serverSocket?.close()
        scope.cancel()
    }
}