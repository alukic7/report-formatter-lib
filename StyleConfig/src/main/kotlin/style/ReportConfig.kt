package style

import com.google.gson.Gson
import java.io.FileReader

data class TextStyle(
    val bold: Boolean = false,
    val italic: Boolean = false,
    val underline: Boolean = false,
    val color: String? = null
)

data class TableStyle(
    val borderThickness: Int = 1,
    val borderColor: String = "#000000"
)

data class ReportConfig(
    val title: TextStyle,
    val summary: TextStyle,
    val header: TextStyle,
    val table: TableStyle
)

fun loadConfig(configPath: String): ReportConfig {
    val gson = Gson()
    FileReader(configPath).use { reader ->
        return gson.fromJson(reader, ReportConfig::class.java)
    }
}
