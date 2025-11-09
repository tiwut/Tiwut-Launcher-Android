package de.tiwut.launcher

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize // Annotation hinzufügen
data class App(
    val name: String,
    val downloadUrl: String,
    val websiteUrl: String,
    val iconUrl: String,
    val packageName: String
) : Parcelable // Interface implementieren