package src

class AgregateFunc {
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

    fun count(data: Map<String, List<String>>, columnIndex: Int): Int {
        val columnName = data.keys.toList().getOrNull(columnIndex)
            ?: throw IllegalArgumentException("Neispravan broj kolone: $columnIndex")

        val values = data[columnName]
            ?: throw IllegalArgumentException("Kolona '$columnName' ne sadrži podatke.")

        return values.size
    }
}

