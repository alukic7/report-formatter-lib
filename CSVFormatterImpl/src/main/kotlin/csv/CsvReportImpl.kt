package csv

import SPI.ReportInterface
import src.AgregateFunc
import src.CalculationProcessor
import java.io.File

class CsvReportImpl : ReportInterface {

    override val implName: String = "CSV"
    private val agregateFunc = AgregateFunc()

    override fun generateReport(
        data: Map<String, List<String>>,
        destination: String,
        header: Boolean,
        title: String?,
        summary: String?,
        configPath: String,
        numbers: Boolean,
        calculationType: String?,
        columnIndex:Int?,
        calculationPath:String?
    ) {

        val mutableData = data.mapValues { it.value.toMutableList() }.toMutableMap()
        processJsonCalculations(mutableData, calculationPath)

        val columns = mutableData.keys.toList()
        val numRows = mutableData.values.first().size

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

        File(destination).printWriter().use { writer ->
            if (header) {
                val headerRow = mutableListOf<String>()
                if (numbers) headerRow.add("ROW")
                headerRow.addAll(columns)
                writer.println(headerRow.joinToString(","))
            }

            for (i in 0 until numRows) {
                val row = mutableListOf<String>()
                if (numbers) row.add((i + 1).toString())
                row.addAll(columns.map { column -> mutableData[column]?.get(i) ?: "" })
                writer.println(row.joinToString(","))
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
