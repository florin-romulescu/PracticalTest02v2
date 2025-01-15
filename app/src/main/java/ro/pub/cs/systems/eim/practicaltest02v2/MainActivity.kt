package ro.pub.cs.systems.eim.practicaltest02v2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ro.pub.cs.systems.eim.practicaltest02v2.server.DictClient
import ro.pub.cs.systems.eim.practicaltest02v2.server.DictServer

class MainActivity : AppCompatActivity() {
    private val client = DictClient()
    private lateinit var server: DictServer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val wordInput = findViewById<EditText>(R.id.editText)
        val callBtn = findViewById<Button>(R.id.callBtn)
        val resultTxt = findViewById<TextView>(R.id.textView)
        val pythonCommButton = findViewById<Button>(R.id.pythonCommButton)

        pythonCommButton.setOnClickListener {
            startActivity(Intent(this, SecondaryActivity::class.java))
        }

        server = DictServer(port = 8080)
        server.start()

        callBtn.setOnClickListener {
            val port = 8080
            val word = wordInput.text.toString()

            if (word.isBlank()) {
                wordInput.error = "Please enter a word"
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val startTime = System.currentTimeMillis()
                    val response = client.requestDefinition("localhost", port, word)
                    val endTime = System.currentTimeMillis()

                    resultTxt.text = response
                    Log.i("DictServer", "Response: $response")
                } catch (e: Exception) {
                    resultTxt.append("Error: ${e.message}\n\n")
                    Log.e("DictServer", "Error: ${e.message}")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        server.stop()
    }
}