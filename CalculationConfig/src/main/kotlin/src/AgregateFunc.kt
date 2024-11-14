package src
    /**
     * A utility class for performing aggregate operations on tabular data represented as a map.
     *
     * This class provides methods for calculating the sum, average, and count of values
     * in a specified column of the data.
     */
class AgregateFunc {
    /**
     * Calculates the sum of all numeric values in the specified column.
     *
     * @param data A map where the key is the column name and the value is a list of strings representing the column data.
     * @param columnIndex The index of the column whose values will be summed.
     * @return The sum of all numeric values in the specified column.
     *
     * @throws IllegalArgumentException If the column index is invalid, the column contains non-numeric values,
     * or the column has no data.
     */
    fun sum(data: Map<String, List<String>>, columnIndex: Int): Int {
        val columnName = data.keys.toList().getOrNull(columnIndex)
            ?: throw IllegalArgumentException("Neispravan broj kolone: $columnIndex")

        val values = data[columnName]?.map { value ->
            value.toIntOrNull() ?: throw IllegalArgumentException(
                "Kolona '$columnName' sadrži nevalidnu vrednost: '$value'. Sve vrednosti moraju biti Int."
            )
        } ?: throw IllegalArgumentException("Kolona '$columnName' ne sadrži podatke.")

        return values.sum()
    }
    /**
     * Calculates the average of all numeric values in the specified column.
     *
     * @param data A map where the key is the column name and the value is a list of strings representing the column data.
     * @param columnIndex The index of the column whose values will be averaged.
     * @return The average of all numeric values in the specified column, or 0.0 if the column is empty.
     *
     * @throws IllegalArgumentException If the column index is invalid, the column contains non-numeric values,
     * or the column has no data.
     */
    fun avg(data: Map<String, List<String>>, columnIndex: Int): Double {
        val columnName = data.keys.toList().getOrNull(columnIndex)
            ?: throw IllegalArgumentException("Neispravan broj kolone: $columnIndex")

        val values = data[columnName]?.map { value ->
            value.toIntOrNull() ?: throw IllegalArgumentException(
                "Kolona '$columnName' sadrži nevalidnu vrednost: '$value'. Sve vrednosti moraju biti Int."
            )
        } ?: throw IllegalArgumentException("Kolona '$columnName' ne sadrži podatke.")

        return if (values.isNotEmpty()) values.average() else 0.0
    }

    /**
     * Counts the number of rows in the specified column.
     *
     * @param data A map where the key is the column name and the value is a list of strings representing the column data.
     * @param columnIndex The index of the column whose rows will be counted.
     * @return The number of rows in the specified column.
     *
     * @throws IllegalArgumentException If the column index is invalid or the column has no data.
     */
    fun count(data: Map<String, List<String>>, columnIndex: Int): Int {
        val columnName = data.keys.toList().getOrNull(columnIndex)
            ?: throw IllegalArgumentException("Neispravan broj kolone: $columnIndex")

        val values = data[columnName]
            ?: throw IllegalArgumentException("Kolona '$columnName' ne sadrži podatke.")

        return values.size
    }
}

