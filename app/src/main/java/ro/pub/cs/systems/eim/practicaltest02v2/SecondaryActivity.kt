package ro.pub.cs.systems.eim.practicaltest02v2

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ro.pub.cs.systems.eim.practicaltest02v2.time.TimeServer

class SecondaryActivity  : AppCompatActivity() {
    private val server = TimeServer(12345)
    private lateinit var logText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_python_comm)

        logText = findViewById<TextView>(R.id.textView)

        server.setOnTimeReceivedListener { timeString ->
            runOnUiThread {
                logText.append("\n$timeString")
            }
        }
        server.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        server.stop()
    }
}