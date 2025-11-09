package de.tiwut.launcher

import android.content.Intent // <-- DIESE ZEILE HINZUFÜGEN
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recycler_view_apps)
        progressBar = findViewById(R.id.progress_bar)

        loadApps()
    }

    private fun loadApps() {
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE

            val result = LibraryService.fetchApps()

            result.onSuccess { apps ->
                recyclerView.adapter = AppAdapter(apps) { selectedApp ->
                    // Dieser Intent-Aufruf funktioniert jetzt
                    val intent = Intent(this@MainActivity, DetailsActivity::class.java)
                    intent.putExtra("EXTRA_APP", selectedApp)
                    startActivity(intent)
                }
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }.onFailure { error ->
                progressBar.visibility = View.GONE
                Toast.makeText(this@MainActivity, "Error loading apps: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}