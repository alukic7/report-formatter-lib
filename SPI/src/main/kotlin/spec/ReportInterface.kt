package SPI

import java.sql.ResultSet
import java.sql.ResultSetMetaData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

/**
 * An interface for generating formatted or non-formatted reports from a map of column data to different formats.
 *
 * Implementations of this interface should define how the report is formatted and saved.
 */
interface ReportInterface {

    abstract val implName: String
    /**
     * Generates a report based on the provided data and writes it to the specified destination.
     *
     * @param data A map where the key is the column name and the value is a list of strings representing the column data.
     *             All lists in the map should have the same size to ensure proper row alignment.
     * @param destination The file path where the report will be saved.
     * @param header Indicates if header is provided in data
     * @param title An optional title for the report, used only in the formatted reports.
     * @param summary An optional summary for the report, used only in the formatted reports.
     *
     */
    fun generateReport(data: Map<String, List<String>>, destination: String, header: Boolean, title: String? = null, summary: String? = null,configPath: String, numbers:Boolean,calculationType: String? = null,columnIndex: Int? = null, calculationPath:String?=null)


    fun generateReport(data: ResultSet, destination: String, header: Boolean, title: String? = null, summary: String? = null, configPath: String,numbers: Boolean,calculationType: String? = null,columnIndex: Int? = null,calculationPath:String?=null){
        val preparedData = prepareData(data)
        generateReport(preparedData, destination, header, title, summary,configPath,numbers,calculationType,columnIndex,calculationPath)
    }


    private fun prepareData(resultSet: ResultSet): Map<String, List<String>> {
        val reportData = mutableMapOf<String, MutableList<String>>()

        val metaData: ResultSetMetaData = resultSet.metaData
        val columnCount = metaData.columnCount

        for (i in 1..columnCount) {
            val columnName = metaData.getColumnName(i)
            reportData[columnName] = mutableListOf()
        }

        while (resultSet.next()) {
            for (i in 1..columnCount) {
                val columnName = metaData.getColumnName(i)
                reportData[columnName]!!.add(resultSet.getString(i))
            }
        }

        return reportData
    }

    fun generateReport(data: List<List<String>>, columnNames: List<String>, destination: String, header: Boolean, title: String? = null, summary: String? = null, configPath: String,numbers: Boolean,calculationType: String? = null,columnIndex: Int? = null,calculationPath:String?=null) {
        val preparedData = prepareData(data, columnNames)
        generateReport(preparedData, destination, header, title, summary, configPath,numbers,calculationType,columnIndex,calculationPath)
    }

    private fun prepareData(data: List<List<String>>, columnNames: List<String>): Map<String, List<String>> {
        val reportData = mutableMapOf<String, MutableList<String>>()

        columnNames.forEach { columnName ->
            reportData[columnName] = mutableListOf()
        }

        data.forEach { row ->
            row.forEachIndexed { index, value ->
                val columnName = columnNames[index]
                reportData[columnName]?.add(value)
            }
        }

        return reportData
    }


    fun generateReport(
        jsonData: InputStreamReader,
        destination: String,
        header: Boolean,
        title: String? = null,
        summary: String? = null,
        configPath: String,
        numbers: Boolean,
        calculationType: String? = null,
        columnIndex: Int? = null,
        calculationPath:String?=null
    ) {
        val preparedData = prepareData(jsonData)
        generateReport(preparedData, destination, header, title, summary, configPath,numbers,calculationType,columnIndex,calculationPath)
    }

    private fun prepareData(jsonData: InputStreamReader): Map<String, List<String>> {
        val gson = Gson()
        val type = object : TypeToken<List<Map<String, Any>>>() {}.type
        val dataList: List<Map<String, Any>> = gson.fromJson(jsonData, type)

        val reportData = mutableMapOf<String, MutableList<String>>()

        for (item in dataList) {
            for ((key, value) in item) {
                reportData.putIfAbsent(key, mutableListOf())
                reportData[key]?.add(value.toString())
            }
        }

        return reportData
    }
}