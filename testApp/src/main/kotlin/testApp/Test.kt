package testApp

import SPI.ReportInterface
import java.io.FileInputStream
import java.io.InputStreamReader
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException
import java.util.*


var connection: Connection? = null


fun connect(): Connection? {
    val properties = Properties()
    properties["user"] = "root"
    properties["password"] = ""

    return try {
        val conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/raf_raspored", properties)
        println("Connection to database established successfully.")
        conn
    } catch (e: SQLException) {
        println("Failed to connect to the database.")
        e.printStackTrace()
        null
    }
}


fun main() {
    var cnt = 1
    /*
    val data = listOf(
        listOf("John", "Doe", "25"),
        listOf("Jane", "Smith", "30"),
        listOf("Alice", "Johnson", "22")
    )
    val columnNames = listOf("FirstName", "LastName", "Age")
    */

    connection = connect()

    if (connection == null) {
        println("Exiting program due to database connection failure.")
        return
    }

    val serviceLoader = ServiceLoader.load(ReportInterface::class.java)

    val exporterServices = mutableMapOf<String, ReportInterface>()

    serviceLoader.forEach { service ->
        exporterServices[service.implName] = service
    }


    println("Dostupni formati za generisanje izvestaja: ")

    println(exporterServices.keys)
    while (true) {
        println("Unesite neki SQL upit: ")

        val upit = readln()

        if (upit.equals("izlaz", ignoreCase = true)) {
            println("Zatvaranje programa.")
            break
        }

        println("Izaberite format: ")
        val format = readln()

        var kalkType:String? = null
        var brKolone:Int? = null
        println("Da li zelite kalkulacju? (Da/ne)")
        val kalk = readln()
        if(kalk.equals("Da")){
            println("AVG,SUM,COUNT")
             kalkType = readln()
            println("Unesite broj kolone")
             brKolone = readln().toIntOrNull()?.minus(1)
        }


        try {
            val statement = connection!!.createStatement()
            val resultSet: ResultSet = statement.executeQuery(upit)

            if (format.equals("PDF")){
                if(kalk.equals("Da")){
                    exporterServices["PDF"]?.generateReport(resultSet, "izlaz$cnt.pdf", true, title = "PDF izvestaj",summary = "", configPath = "testApp/src/main/resources/config.json",true, kalkType,  brKolone,"testApp/src/main/resources/calculations.json")
                    cnt += 1
                }else if(kalk.equals("Ne")){
                    exporterServices["PDF"]?.generateReport(resultSet, "izlaz$cnt.pdf", true, title = "NIGER",summary = "", configPath = "testApp/src/main/resources/config.json",true,"testApp/src/main/resources/calculations.json")
                    cnt += 1
                }
            }else if (format.equals("CSV")){
                if(kalk.equals("Da")){
                    exporterServices["CSV"]?.generateReport(resultSet, "izlaz$cnt.csv", true, null , "", "testApp/src/main/resources/config.json",  true, kalkType, brKolone,"testApp/src/main/resources/calculations.json")
                    cnt +=1
                }else{
                    exporterServices["CSV"]?.generateReport(resultSet, "izlaz$cnt.csv", header = true, title = null,  configPath ="testApp/src/main/resources/config.json", numbers =  true, calculationPath = "testApp/src/main/resources/calculations.json")
                    cnt += 1
                }
            }else if (format.equals("XLS")){
                if(kalk.equals("Da")){
                    val jsonDataReader = InputStreamReader(FileInputStream("testApp/src/main/resources/data.json"))
                    exporterServices["XLS"]?.generateReport(jsonDataReader, "izlaz$cnt.xlsx", true,"Excel izvestaj",  "",  "testApp/src/main/resources/config.json",  false,kalkType,brKolone,"testApp/src/main/resources/calculations.json")
                    jsonDataReader.close()
                    cnt += 1
                }else{
                    val jsonDataReader = InputStreamReader(FileInputStream("testApp/src/main/resources/data.json"))
                    exporterServices["XLS"]?.generateReport(jsonDataReader, "izlaz$cnt.xlsx", true, summary = "", configPath = "testApp/src/main/resources/config.json", numbers = false, calculationPath = "testApp/src/main/resources/calculations.json")
                    jsonDataReader.close()
                    cnt += 1
                }
            }else if (format.equals("TXT")){
                if(kalk.equals("Da")){
                    exporterServices["TXT"]?.generateReport(resultSet, "izlaz$cnt.txt", true,  null,  "", "testApp/src/main/resources/config.json",  true, kalkType, brKolone,"testApp/src/main/resources/calculations.json")
                    cnt += 1
                }else{
                    exporterServices["TXT"]?.generateReport(resultSet, "izlaz$cnt.txt", true, configPath = "testApp/src/main/resources/config.json", numbers = true, calculationPath = "testApp/src/main/resources/calculations.json")
                    cnt += 1
                }
            }else{
                println("Nepostojeci format")
            }


            resultSet.close()
            statement.close()

        } catch (e: SQLException) {
            println("Greška pri izvršavanju upita.")
            e.printStackTrace()
        }

    }
}