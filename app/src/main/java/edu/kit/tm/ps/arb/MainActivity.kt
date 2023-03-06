package edu.kit.tm.ps.arb

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Visibility
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView

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

        val ratchet = Ratchet()

        ratchet.benchmarkKeygen().let {
            appendText(String.format("Keygen: %.2f ms\n", it))
        }

        ratchet.benchmarkPubRatchet().let {
            appendText(String.format("PubRatchet: %.2f ms\n", it))
        }

        ratchet.benchmarkPrivRatchet().let {
            appendText(String.format("PrivRatchet: %.2f ms\n", it))
        }

        ratchet.benchmarkEncrypt().let {
            appendText(String.format("Encrypt: %.2f ms\n", it))
        }

        ratchet.benchmarkDecrypt().let {
            appendText(String.format("Decrypt: %.2f ms\n", it))
        }

        benchmarkStopped()
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