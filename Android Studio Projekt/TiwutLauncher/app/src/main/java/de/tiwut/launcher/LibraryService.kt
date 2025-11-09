import de.tiwut.launcher.App // <-- DIESE ZEILE HINZUFÜGEN
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

object LibraryService {

    private const val LIBRARY_URL = "https://launcher.tiwut.de/Android/library.tiwut"

    suspend fun fetchApps(): Result<List<App>> {
        return withContext(Dispatchers.IO) {
            try {
                val content = URL(LIBRARY_URL).readText()
                val apps = parseLibraryContent(content)
                Result.success(apps)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e)
            }
        }
    }

    private fun parseLibraryContent(content: String): List<App> {
        val apps = mutableListOf<App>()
        content.split("\n").forEach { line ->
            if (line.isNotBlank()) {
                val parts = line.split(";")
                if (parts.size >= 5) {
                    apps.add(App( // <-- Dieser Fehler wird jetzt behoben
                        name = parts[0].trim(),
                        downloadUrl = parts[1].trim(),
                        websiteUrl = parts[2].trim(),
                        iconUrl = parts[3].trim(),
                        packageName = parts[4].trim()
                    ))
                }
            }
        }
        return apps
    }
}