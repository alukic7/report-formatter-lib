package exc

import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import SPI.ReportInterface
import java.io.FileOutputStream
import style.loadConfig
import java.awt.Color
import org.apache.poi.xssf.usermodel.XSSFFont
import src.AgregateFunc
import src.CalculationProcessor

class ExcelReportImpl : ReportInterface {
    override val implName: String = "XLS"
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
        val workbook: Workbook = XSSFWorkbook()
        val sheet: Sheet = workbook.createSheet("Report")
        var currentRowIdx = 0

        val mutableData = data.mapValues { it.value.toMutableList() }.toMutableMap()
        processJsonCalculations(mutableData, calculationPath)

        val columns = mutableData.keys.toList()


        title?.let {
            val titleRow: Row = sheet.createRow(0)
            val titleCell: Cell = titleRow.createCell(0)
            titleCell.setCellValue(it)

            sheet.addMergedRegion(CellRangeAddress(0, 0, 0, data.size - 1))

            val titleStyle = workbook.createCellStyle().apply {
                alignment = HorizontalAlignment.CENTER
                val titleFont: Font = workbook.createFont().apply {
                    bold = config.title.bold
                    italic = config.title.italic
                    fontHeightInPoints = 18
                    config.title.color?.let { color ->
                        setFontColorFromHex(this, color)
                    }
                }
                setFont(titleFont)
            }
            titleCell.cellStyle = titleStyle
        }

        if (header) {
            val headerRow: Row = sheet.createRow(1)
            mutableData.keys.forEachIndexed { index, columnName ->
                val headerCell = headerRow.createCell(index)
                headerCell.setCellValue(columnName)

                val headerStyle = workbook.createCellStyle().apply {
                    val headerFont: Font = workbook.createFont().apply {
                        bold = config.header.bold
                        italic = config.header.italic
                        config.header.color?.let { color ->
                            setFontColorFromHex(this, color)
                        }
                    }
                    setFont(headerFont)
                }
                headerCell.cellStyle = headerStyle
            }
        }

        val numRows = mutableData.values.first().size
        for (i in 0 until numRows) {
            val dataRow: Row = sheet.createRow(if (header) i + 2 else i + 1)
            mutableData.keys.forEachIndexed { index, columnName ->
                dataRow.createCell(index).setCellValue(mutableData[columnName]?.get(i) ?: "")
            }
        }

        val calculatedSummary = if (calculationType != null && columnIndex != null) {
            try {
                when (calculationType.uppercase()) {
                    "SUM" -> {
                        val sumResult = agregateFunc.sum(mutableData, columnIndex)
                        "SUM (kolona '${mutableData.keys.toList()[columnIndex]}'): $sumResult"
                    }
                    "AVG" -> {
                        val avgResult = agregateFunc.avg(mutableData, columnIndex)
                        "AVG (kolona '${mutableData.keys.toList()[columnIndex]}'): $avgResult"
                    }
                    "COUNT" -> {
                        val countResult = agregateFunc.count(mutableData, columnIndex)
                        "COUNT (kolona '${mutableData.keys.toList()[columnIndex]}'): $countResult"
                    }
                    else -> "Nepoznata kalkulacija."
                }
            } catch (e: IllegalArgumentException) {
                "Greška: ${e.message}"
            }
        } else {
            null
        }

        if (!summary.isNullOrEmpty() || calculatedSummary != null) {
            val summaryRowIdx = if (header) numRows + 2 else numRows + 1
            val summaryRow: Row = sheet.createRow(summaryRowIdx)
            val summaryCell: Cell = summaryRow.createCell(0)


            val finalSummary = buildString {
                summary?.let { append(it).append("\n") }
                calculatedSummary?.let { append(it) }
            }
            summaryCell.setCellValue(finalSummary)

            val summaryStyle = workbook.createCellStyle().apply {
                val summaryFont: Font = workbook.createFont().apply {
                    bold = config.summary.bold
                    italic = config.summary.italic
                    config.summary.color?.let { color ->
                        setFontColorFromHex(this, color)
                    }
                }
                setFont(summaryFont)
            }
            summaryCell.cellStyle = summaryStyle


            val startRow = currentRowIdx - 1
            val endRow = currentRowIdx - 1
            val startColumn = 0
            val endColumn = data.size - 1

            val regionExists = sheet.mergedRegions.any { region ->
                region.firstRow == startRow && region.lastRow == endRow && region.firstColumn == startColumn && region.lastColumn == endColumn
            }

            if (!regionExists) {
                sheet.addMergedRegion(CellRangeAddress(summaryRowIdx, summaryRowIdx, 0, data.size - 1))
            }
        }

        FileOutputStream(destination).use { outputStream ->
            workbook.write(outputStream)
        }

        workbook.close()
    }

    private fun setFontColorFromHex(font: Font, colorHex: String) {
        val awtColor = Color.decode(colorHex)
        if (font is XSSFFont) {
            font.setColor(org.apache.poi.xssf.usermodel.XSSFColor(byteArrayOf(awtColor.red.toByte(), awtColor.green.toByte(), awtColor.blue.toByte()), null))
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