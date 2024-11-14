package pdf

import com.lowagie.text.*
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter
import SPI.ReportInterface
import java.io.FileOutputStream
import style.loadConfig
import style.TextStyle
import java.awt.Color
import src.AgregateFunc
import src.CalculationProcessor


class PdfReportImpl : ReportInterface {
    override val implName: String = "PDF"
    private val agregateFunc = AgregateFunc()

    override fun generateReport(
        data: Map<String, List<String>>,
        destination: String,
        header: Boolean,
        title: String?,
        summary: String?,
        configPath: String,
        numbers:Boolean,
        calculationType: String?,
        columnIndex:Int?,
        calculationPath:String?
    ) {
        val config = loadConfig(configPath)
        val document = Document()


        try {
            PdfWriter.getInstance(document, FileOutputStream(destination))
            document.open()

            title?.let {
                val titleFont = Font(Font.UNDEFINED, 18f, getFontStyle(config.title))
                config.title.color?.let { color -> titleFont.color = Color.decode(color) }

                val titleParagraph = Paragraph(it, titleFont)
                titleParagraph.alignment = Element.ALIGN_CENTER
                document.add(titleParagraph)
                document.add(Chunk.NEWLINE)
            }

            val mutableData = data.mapValues { it.value.toMutableList() }.toMutableMap()
            processJsonCalculations(mutableData, calculationPath)

            val columns = mutableData.keys.toList()
            val table = PdfPTable(columns.size + 1)

            if (header) {
                if (numbers) {
                    val headerFont = Font(Font.UNDEFINED, 12f, getFontStyle(config.header))
                    val serialHeaderCell = PdfPCell(Paragraph("ROW", headerFont))
                    serialHeaderCell.horizontalAlignment = Element.ALIGN_CENTER
                    table.addCell(serialHeaderCell)
                }

                columns.forEach { column ->
                    val headerFont = Font(Font.UNDEFINED, 12f, getFontStyle(config.header))
                    config.header.color?.let { color -> headerFont.color = Color.decode(color) }
                    val cell = PdfPCell(Paragraph(column, headerFont))
                    cell.horizontalAlignment = Element.ALIGN_CENTER
                    table.addCell(cell)
                }
            }


            val numRows = mutableData.values.first().size
            for (i in 0 until numRows) {
                if (numbers) {
                    val serialCell = PdfPCell(Paragraph((i + 1).toString()))
                    serialCell.horizontalAlignment = Element.ALIGN_CENTER
                    table.addCell(serialCell)
                }

                columns.forEach { column ->
                    val cellData = mutableData[column]?.get(i) ?: ""
                    val cell = PdfPCell(Paragraph(cellData))
                    table.addCell(cell)
                }
            }

            document.add(table)

            val calculatedSummary = if (calculationType != null && columnIndex != null) {
                try {
                    when (calculationType.uppercase()) {
                        "SUM" -> {
                            val sumResult = agregateFunc.sum(mutableData, columnIndex)
                            "SUM (kolona '${columns[columnIndex]}'): $sumResult"
                        }
                        "AVG" -> {
                            val avgResult = agregateFunc.avg(mutableData, columnIndex)
                            "AVG (kolona '${columns[columnIndex]}'): $avgResult"
                        }
                        "COUNT" -> {
                            val countResult = agregateFunc.count(mutableData, columnIndex)
                            "COUNT (kolona '${columns[columnIndex]}'): $countResult"
                        }
                        else -> "Nepoznata kalkulacija."
                    }
                } catch (e: IllegalArgumentException) {
                    "Greška: ${e.message}"
                }
            } else {
                null
            }

            summary?.let {
                val summaryFont = Font(Font.UNDEFINED, 10f, getFontStyle(config.summary))
                config.summary.color?.let { color -> summaryFont.color = Color.decode(color) }

                val finalSummary = buildString {
                    append(it)
                    calculatedSummary?.let { calc -> append("\n").append(calc) }
                }

                val summaryParagraph = Paragraph("Summary:\n$finalSummary", summaryFont)
                document.add(Chunk.NEWLINE)
                document.add(summaryParagraph)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            document.close()
        }
    }

    private fun getFontStyle(style: TextStyle): Int {
        return when {
            style.bold && style.italic -> Font.BOLDITALIC
            style.bold -> Font.BOLD
            style.italic -> Font.ITALIC
            else -> Font.NORMAL
        }
    }

    private fun processJsonCalculations(
        data: MutableMap<String, MutableList<String>>,
        calculationPath: String?
    ) {
        if (calculationPath == null) return

        try {
            val calculationProcessor = CalculationProcessor()
            calculationProcessor.applyCalculations(data, calculationPath)
        } catch (e: Exception) {
            println("Greška pri obradi JSON kalkulacija: ${e.message}")
        }
    }

}