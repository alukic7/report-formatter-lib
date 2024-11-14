package src

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.FileReader
import java.lang.IllegalArgumentException
/**
 * Represents a single calculation operation to be performed on tabular data.
 *
 * @param operation The type of operation to perform. Supported operations:
 *                  - "plus" for addition
 *                  - "mnozenje" for multiplication
 *                  - "minus" for subtraction
 *                  - "deljenje" for division
 * @param columns A list of column names on which the operation will be applied.
 *                The size and type of the list depend on the operation:
 *                - For "plus" and "mnozenje", multiple columns can be specified.
 *                - For "minus" and "deljenje", exactly two columns must be specified.
 * @param result The name of the column where the calculated result will be stored.
 */
data class Calculation(
    val operation: String,
    val columns: List<String>,
    val result: String
)
/**
 * Represents a configuration for a set of calculations to be applied to tabular data.
 *
 * This class is designed to parse JSON configuration files that specify multiple calculations.
 *
 * @param calculations A list of `Calculation` objects, where each object defines
 *                     an operation, the input columns, and the output column.
 */
data class CalculationsConfig(
    @SerializedName("calculations") val calculations: List<Calculation>
)

/**
 * A processor for applying mathematical calculations on tabular data.
 *
 * This class reads a configuration file specifying the operations to perform,
 * applies those operations on the provided data, and stores the results in new or existing columns.
 */
class CalculationProcessor {
    /**
     * Applies the specified calculations from the configuration file to the provided data.
     *
     * @param data A mutable map where the key is the column name and the value is a list of strings representing the column data.
     *             All columns must have the same number of rows.
     * @param configPath The file path to the JSON configuration file that specifies the calculations to be performed.
     *
     * @throws IllegalArgumentException If an operation in the configuration is unknown, a column is missing or contains invalid data,
     *                                  or the configuration is malformed.
     * @throws IOException If there is an issue reading the configuration file.
     */
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
