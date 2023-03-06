package edu.kit.tm.ps.arb

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Visibility
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject
import org.json.JSONArray
import java.io.BufferedOutputStream
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.button_run).setOnClickListener {

            Thread { this.doBenchmarks() }.start()
        }
    }


    private fun doBenchmarks() {
        benchmarkStarted()
        appendText("Running benchmarks...\n")

        val jsonResult = JSONObject()
        jsonResult.put("nickname", findViewById<EditText>(R.id.editTextId).text)
        val ratchet = Ratchet()

        ratchet.benchmarkKeygen().let {
            jsonResult.put("keygen", JSONArray(it))
            appendText(String.format("Keygen: %.2f ms\n", it.average() / 1000000))
        }

        ratchet.benchmarkPubRatchet().let {
            jsonResult.put("pub_ratchet", JSONArray(it))
            appendText(String.format("PubRatchet: %.2f ns\n", it.average()))
        }

        ratchet.benchmarkPrivRatchet().let {
            jsonResult.put("priv_ratchet", JSONArray(it))
            appendText(String.format("PrivRatchet: %.2f ms\n", it.average() / 1000000))
        }

        ratchet.benchmarkEncrypt().let {
            jsonResult.put("encrypt", JSONArray(it))
            appendText(String.format("Encrypt: %.2f ms\n", it.average() / 1000000))
        }

        ratchet.benchmarkDecrypt().let {
            jsonResult.put("decrypt", JSONArray(it))
            appendText(String.format("Decrypt: %.2f ms\n", it.average() / 1000000))
        }

        benchmarkStopped()
        uploadResult(jsonResult)
    }

    private fun uploadResult(resultObject: JSONObject) {
        val targetUrl = URL("https://i63schadt.tm.kit.edu/cgi-bin/record-android-benchmark.cgi")
        try {
            val data = resultObject.toString().toByteArray()
            val connection = targetUrl.openConnection() as HttpURLConnection
            connection.doOutput = true
            connection.setFixedLengthStreamingMode(data.size)

            val stream = BufferedOutputStream(connection.outputStream)
            stream.write(data)
            stream.flush()
            connection.disconnect()

            Snackbar.make(findViewById(R.id.button_run), "Benchmark uploaded", Snackbar.LENGTH_SHORT).show()
        }
        catch (e: Exception) {
            Snackbar.make(findViewById(R.id.button_run), "Upload failed :-(", Snackbar.LENGTH_SHORT).show()
            Log.e("BenchmarkUpload", "Couldn't upload", e)
        }
    }

    private fun benchmarkStarted() {
        runOnUiThread {
            findViewById<Button>(R.id.button_run).isEnabled = false
            findViewById<TextView>(R.id.label_output).text = ""
            findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
        }
    }

    private fun benchmarkStopped() {
        runOnUiThread {
            findViewById<Button>(R.id.button_run).isEnabled = true
            findViewById<ProgressBar>(R.id.progressBar).visibility = View.INVISIBLE
        }
    }

    private fun appendText(text: String) {
        runOnUiThread {
            val view = findViewById<TextView>(R.id.label_output)
            view.text = view.text.toString() + text
            view.invalidate()
        }
    }
}