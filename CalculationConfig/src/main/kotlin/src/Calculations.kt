package src

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.FileReader
import java.lang.IllegalArgumentException

data class Calculation(
    val operation: String,
    val columns: List<String>,
    val result: String
)

data class CalculationsConfig(
    @SerializedName("calculations") val calculations: List<Calculation>
)

class CalculationProcessor {

    fun applyCalculations(
        data: MutableMap<String, MutableList<String>>,
        configPath: String
    ) {
        val gson = Gson()
        val config: CalculationsConfig = FileReader(configPath).use { reader ->
            gson.fromJson(reader, CalculationsConfig::class.java)
        }

        config.calculations.forEach { calculation ->
            when (calculation.operation.lowercase()) {
                "plus" -> applyPlus(data, calculation)
                "mnozenje" -> applyMultiplication(data, calculation)
                "minus" -> applySubtraction(data, calculation)
                "deljenje" -> applyDivision(data, calculation)
                else -> throw IllegalArgumentException("Nepoznata operacija: ${calculation.operation}")
            }
        }
    }

    private fun applyPlus(data: MutableMap<String, MutableList<String>>, calculation: Calculation) {
        val resultColumn = calculation.result
        val columnValues = calculation.columns.map { column ->
            data[column]?.mapNotNull { it.toDoubleOrNull() }
                ?: throw IllegalArgumentException("Kolona '$column' ne sadrži validne numeričke podatke.")
        }

        val resultValues = columnValues.reduce { acc, list ->
            acc.zip(list) { a, b -> a + b }
        }

        data[resultColumn] = resultValues.map { it.toString() }.toMutableList()
    }

    private fun applyMultiplication(data: MutableMap<String, MutableList<String>>, calculation: Calculation) {
        val resultColumn = calculation.result
        val columnValues = calculation.columns.map { column ->
            data[column]?.mapNotNull { it.toDoubleOrNull() }
                ?: throw IllegalArgumentException("Kolona '$column' ne sadrži validne numeričke podatke.")
        }

        val resultValues = columnValues.reduce { acc, list ->
            acc.zip(list) { a, b -> a * b }
        }

        data[resultColumn] = resultValues.map { it.toString() }.toMutableList()
    }

    private fun applySubtraction(data: MutableMap<String, MutableList<String>>, calculation: Calculation) {
        if (calculation.columns.size != 2)
            throw IllegalArgumentException("Operacija 'minus' zahteva tačno dve kolone.")

        val columnA = calculation.columns[0]
        val columnB = calculation.columns[1]
        val resultColumn = calculation.result

        val valuesA = data[columnA]?.mapNotNull { it.toDoubleOrNull() }
            ?: throw IllegalArgumentException("Kolona '$columnA' ne sadrži validne numeričke podatke.")
        val valuesB = data[columnB]?.mapNotNull { it.toDoubleOrNull() }
            ?: throw IllegalArgumentException("Kolona '$columnB' ne sadrži validne numeričke podatke.")

        val resultValues = valuesA.zip(valuesB) { a, b -> a - b }
        data[resultColumn] = resultValues.map { it.toString() }.toMutableList()
    }

    private fun applyDivision(data: MutableMap<String, MutableList<String>>, calculation: Calculation) {
        if (calculation.columns.size != 2)
            throw IllegalArgumentException("Operacija 'deljenje' zahteva tačno dve kolone.")

        val columnA = calculation.columns[0]
        val columnB = calculation.columns[1]
        val resultColumn = calculation.result

        val valuesA = data[columnA]?.mapNotNull { it.toDoubleOrNull() }
            ?: throw IllegalArgumentException("Kolona '$columnA' ne sadrži validne numeričke podatke.")
        val valuesB = data[columnB]?.mapNotNull { it.toDoubleOrNull() }
            ?: throw IllegalArgumentException("Kolona '$columnB' ne sadrži validne numeričke podatke.")

        val resultValues = valuesA.zip(valuesB) { a, b ->
            if (b == 0.0) throw IllegalArgumentException("Deljenje sa nulom nije dozvoljeno.")
            a / b
        }
        data[resultColumn] = resultValues.map { it.toString() }.toMutableList()
    }


}
