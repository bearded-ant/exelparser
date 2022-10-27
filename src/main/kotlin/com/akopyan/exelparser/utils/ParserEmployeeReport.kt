package com.akopyan.exelparser.utils

import com.akopyan.exelparser.data.ExelFileRepoImpl
import java.io.File
import java.time.Instant

class ParserEmployeeReport() {

    private val baseValues: BaseValues = BaseValues()

    fun parseEmployeeReport(pathToFile: String): List<List<String>> {

        val exelReport: List<List<String>>
        val pathUri = "${baseValues.BASE_PATH}${pathToFile}"
        val parsedReport = mutableListOf<MutableList<String>>()
        var counter = 0

        val startJob = Instant.now().toEpochMilli()

        if (checkFileExists(pathUri)) {
            exelReport = readPriceToArray(pathUri)
            for (i in baseValues.ROW_START_INDEX_E..exelReport.lastIndex) {
                if (exelReport[i].isNotEmpty() && exelReport[i][0].isNotBlank()) {
                    val row = arrayListOf<String>()
                    for (j in 0..exelReport[i].lastIndex)
                        row.add(exelReport[i][j])
                    parsedReport.add(row)
//                    println(parsedReport[counter].toString())
                    counter++
                }
            }

            val endJob = Instant.now().toEpochMilli()
            println("время ${endJob - startJob}, файл $pathToFile")
            return parsedReport
        } else return parsedReport
    }

    private fun readPriceToArray(pathUri: String): List<List<String>> {
        val newBook = ExelFileRepoImpl().openBook(pathUri)
        return ExelFileRepoImpl().getExelData(newBook, baseValues.SHEET_E_INDEX, baseValues.CELL_INDEXES_E)
    }

    private fun checkFileExists(path: String): Boolean = File(path).isFile

}