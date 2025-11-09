package de.tiwut.launcher

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import coil.load
import java.io.File

class DetailsActivity : AppCompatActivity() {

    private var app: App? = null
    private var downloadId: Long = -1

    private lateinit var btnInstall: Button
    private lateinit var btnLaunch: Button
    private lateinit var btnUninstall: Button
    private lateinit var downloadProgress: ProgressBar

    // Dieser Receiver hört, wenn ein Download beendet ist
    private val onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id == downloadId) {
                downloadProgress.visibility = View.GONE
                Toast.makeText(this@DetailsActivity, "Download abgeschlossen", Toast.LENGTH_SHORT).show()
                installApp()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        // Den Receiver registrieren
        registerReceiver(onDownloadComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), RECEIVER_EXPORTED)

        // App-Daten aus dem Intent holen
        app = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("EXTRA_APP", App::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("EXTRA_APP")
        }

        if (app == null) {
            finish() // Activity schließen, wenn keine App-Daten vorhanden sind
            return
        }

        // UI-Elemente initialisieren
        val appIcon: ImageView = findViewById(R.id.detail_app_icon)
        val appName: TextView = findViewById(R.id.detail_app_name)
        btnInstall = findViewById(R.id.btn_install)
        btnLaunch = findViewById(R.id.btn_launch)
        btnUninstall = findViewById(R.id.btn_uninstall)
        val btnWebsite: Button = findViewById(R.id.btn_website)
        downloadProgress = findViewById(R.id.download_progress)

        // UI mit App-Daten füllen
        appName.text = app!!.name
        appIcon.load(app!!.iconUrl) {
            crossfade(true)
            error(R.drawable.ic_launcher_foreground)
        }

        // Button-Klicks einrichten
        btnInstall.setOnClickListener { startDownload() }
        btnLaunch.setOnClickListener { launchApp() }
        btnUninstall.setOnClickListener { uninstallApp() }
        btnWebsite.setOnClickListener { openWebsite() }

        updateButtonStates()
    }

    override fun onResume() {
        super.onResume()
        // Wenn der Benutzer zur App zurückkehrt (z.B. nach einer Deinstallation),
        // aktualisieren wir den Zustand der Buttons.
        updateButtonStates()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Wichtig: Den Receiver wieder abmelden, um Speicherlecks zu vermeiden
        unregisterReceiver(onDownloadComplete)
    }

    private fun isAppInstalled(): Boolean {
        return try {
            packageManager.getPackageInfo(app!!.packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun updateButtonStates() {
        if (isAppInstalled()) {
            btnInstall.visibility = View.GONE
            btnLaunch.visibility = View.VISIBLE
            btnUninstall.visibility = View.VISIBLE
        } else {
            btnInstall.visibility = View.VISIBLE
            btnLaunch.visibility = View.GONE
            btnUninstall.visibility = View.GONE
        }
    }

    private fun startDownload() {
        Toast.makeText(this, "Download startet...", Toast.LENGTH_SHORT).show()
        downloadProgress.visibility = View.VISIBLE
        btnInstall.isEnabled = false

        val request = DownloadManager.Request(Uri.parse(app!!.downloadUrl))
            .setTitle(app!!.name)
            .setDescription("Wird heruntergeladen...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "${app!!.name}.apk")
            .setAllowedOverMetered(true)

        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadId = downloadManager.enqueue(request)
    }

    private fun installApp() {
        val destination = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "${app!!.name}.apk"
        )
        if (!destination.exists()) {
            Toast.makeText(this, "APK nicht gefunden!", Toast.LENGTH_SHORT).show()
            return
        }

        val authority = "${applicationContext.packageName}.provider"
        val apkUri = FileProvider.getUriForFile(this, authority, destination)

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(intent)
    }

    private fun launchApp() {
        val launchIntent = packageManager.getLaunchIntentForPackage(app!!.packageName)
        if (launchIntent != null) {
            startActivity(launchIntent)
        } else {
            Toast.makeText(this, "App konnte nicht gestartet werden", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uninstallApp() {
        val intent = Intent(Intent.ACTION_DELETE, Uri.parse("package:${app!!.packageName}"))
        startActivity(intent)
    }

    private fun openWebsite() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(app!!.websiteUrl))
        startActivity(intent)
    }
}