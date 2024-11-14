package style

import com.google.gson.Gson
import java.io.FileReader

/**
 * Represents the styling options for text in formatted reports.
 *
 * This class is used to define the visual appearance of text elements like the title, summary, and header.
 *
 * @param bold Indicates whether the text should be bold. Default is `false`.
 * @param italic Indicates whether the text should be italicized. Default is `false`.
 * @param underline Indicates whether the text should be underlined. Default is `false`.
 * @param color The color of the text in hexadecimal format (e.g., `#FFFFFF` for white).
 *              Default is `null`, which means the default color will be used.
 */
data class TextStyle(
    val bold: Boolean = false,
    val italic: Boolean = false,
    val underline: Boolean = false,
    val color: String? = null
)

/**
 * Represents the styling options for a table in formatted reports.
 *
 * This class is used to define the visual appearance of the table, including its border properties.
 *
 * @param borderThickness The thickness of the table's border. Default is `1`.
 * @param borderColor The color of the table's border in hexadecimal format (e.g., `#000000` for black). Default is `#000000`.
 */
data class TableStyle(
    val borderThickness: Int = 1,
    val borderColor: String = "#000000"
)

/**
 * Represents the configuration for a report's styling.
 *
 * This class aggregates styling options for various elements of the report, including the title, summary, header, and table.
 *
 * @param title The styling options for the report's title.
 * @param summary The styling options for the report's summary section.
 * @param header The styling options for the table headers.
 * @param table The styling options for the table, including border thickness and color.
 */
data class ReportConfig(
    val title: TextStyle,
    val summary: TextStyle,
    val header: TextStyle,
    val table: TableStyle
)

/**
 * Loads a report configuration from a JSON file.
 *
 * This function parses the JSON configuration file to create a `ReportConfig` object
 * that defines the styling for the report's elements, such as the title, summary, header, and table.
 *
 * @param configPath The file path to the JSON configuration file.
 * @return A `ReportConfig` object containing the styling settings for the report.
 *
 * @throws com.google.gson.JsonSyntaxException If the JSON file is not properly formatted.
 * @throws java.io.FileNotFoundException If the configuration file is not found at the specified path.
 */
fun loadConfig(configPath: String): ReportConfig {
    val gson = Gson()
    FileReader(configPath).use { reader ->
        return gson.fromJson(reader, ReportConfig::class.java)
    }
}
