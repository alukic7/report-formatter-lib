package txt

import SPI.ReportInterface
import src.AgregateFunc
import src.CalculationProcessor
import java.io.File

class TxtReportImpl : ReportInterface {

    override val implName: String = "TXT"
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
        val mutableData = data.mapValues { it.value.toMutableList() }.toMutableMap()
        processJsonCalculations(mutableData, calculationPath)
        val columns = mutableData.keys.toList()
        val numRows = mutableData.values.first().size

        val columnWidths = columns.map { column ->
            val maxDataWidth = mutableData[column]?.maxOfOrNull { it.length } ?: 0
            maxOf(column.length, maxDataWidth)
        }.toMutableList()

        if (numbers) {
            columnWidths.add(0, numRows.toString().length)
        }

        File(destination).printWriter().use { writer ->
            title?.let {
                writer.println(it)
                writer.println()
            }

            if (numbers) {
                writer.print("ROW".padEnd(columnWidths[0] + 2))
            }

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

            columns.forEachIndexed { index, column ->
                writer.print(column.padEnd(columnWidths[if (numbers) index + 1 else index] + 2))
            }
            writer.println()

            columnWidths.forEach { width ->
                writer.print("-".repeat(width + 2))
            }
            writer.println()

            for (i in 0 until numRows) {
                if (numbers) {
                    writer.print((i + 1).toString().padEnd(columnWidths[0] + 2))
                }
                columns.forEachIndexed { index, column ->
                    val cell = mutableData[column]?.get(i) ?: ""
                    writer.print(cell.padEnd(columnWidths[if (numbers) index + 1 else index] + 2))
                }
                writer.println()
            }

            if (!summary.isNullOrEmpty() || calculatedSummary != null) {
                writer.println()
                writer.println("Summary:")
                summary?.let { writer.println(it) }
                calculatedSummary?.let { writer.println(it) }
            }
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