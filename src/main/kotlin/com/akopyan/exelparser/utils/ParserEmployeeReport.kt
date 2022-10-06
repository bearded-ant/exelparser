package com.akopyan.exelparser.utils

import com.akopyan.exelparser.data.ExelFileRepoImpl
import java.io.File
import java.text.DecimalFormat

private const val BASE_PATH: String = "/home/ant/akopyan/"
private const val SHEET_NAME: String = "Лист1"
private val COLUM_INDEX: ArrayList<Int> = arrayListOf(0, 2)
private const val ROW_START_INDEX: Int = 0
private val decFormat = DecimalFormat("#.##")

class ParserEmployeeReport() {

    fun parseNameToTokenAndTimeStamp(fileName: String): Map<String, String> {
        val regex = Regex("""\d{4}_\d{1,2}""")
        var dateStamp = ""
        var token = ""
        if (regex.containsMatchIn(fileName)) {
            dateStamp = regex.find(fileName)!!.value
            token = fileName.substring(0, fileName.length - dateStamp.length - 1)
        }
        val result = mutableMapOf<String, String>()
        result["dateStamp"] = dateStamp
        result["token"] = token
        return result
    }

    fun parseEmployeeReport(pathToFile: String): List<List<String>> {

        val exelReport: List<List<String>>
        val pathUri: String = "$BASE_PATH${pathToFile}"
        val parsedReport = mutableListOf<MutableList<String>>()
        var counter = 0

        if (checkFileExists(pathUri)) {
            exelReport = readPriceToArray(pathUri)
            for (i in ROW_START_INDEX..exelReport.lastIndex) {
                if (exelReport[i].isNotEmpty()) {
                    val row = arrayListOf<String>()
                    for (j in COLUM_INDEX)
                        row.add(getFloatFormattedString(exelReport[i][j]))
                    parsedReport.add(row)
                    println(parsedReport[counter].toString())
                    counter++
                }
            }
            return parsedReport
        } else return parsedReport
    }

    private fun readPriceToArray(pathUri: String): List<List<String>> {
        val newBook = ExelFileRepoImpl().openBook(pathUri)
        return ExelFileRepoImpl().getExelData(newBook, SHEET_NAME)
    }

    private fun checkFileExists(path: String): Boolean = File(path).isFile

    private fun getFloatFormattedString(cellValue: String) =
        if (cellValue.toFloatOrNull() != null) decFormat.format(cellValue.toFloat())
            .toString() else cellValue

}