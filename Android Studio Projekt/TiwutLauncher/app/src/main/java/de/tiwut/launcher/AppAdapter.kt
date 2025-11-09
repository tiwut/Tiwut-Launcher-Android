package de.tiwut.launcher

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import de.tiwut.launcher.App
import de.tiwut.launcher.R

// Die neue Signatur akzeptiert eine Funktion als Parameter
class AppAdapter(
    private val apps: List<App>,
    private val onItemClick: (App) -> Unit // Funktion für den Klick-Handler
) : RecyclerView.Adapter<AppAdapter.AppViewHolder>() {

    class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val appIcon: ImageView = itemView.findViewById(R.id.app_icon)
        val appName: TextView = itemView.findViewById(R.id.app_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.app_card_item, parent, false)
        return AppViewHolder(view)
    }

    override fun getItemCount(): Int = apps.size

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val app = apps[position]
        holder.appName.text = app.name
        holder.appIcon.load(app.iconUrl) {
            crossfade(true)
            placeholder(R.drawable.ic_launcher_background)
            error(R.drawable.ic_launcher_foreground)
        }

        // Setze den OnClickListener für die gesamte Karte
        holder.itemView.setOnClickListener {
            onItemClick(app)
        }
    }
}