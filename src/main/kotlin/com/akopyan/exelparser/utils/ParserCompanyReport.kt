package com.akopyan.exelparser.utils


import com.akopyan.exelparser.data.ExelFileRepoImpl
import java.io.File
import java.text.DecimalFormat

private const val BASE_PATH: String = "/home/ant/akopyan/"
private const val SHEET_NAME: String = "Detailed franchisee"
private val COLUM_INDEX: ArrayList<Int> = arrayListOf(0, 1, 2, 3, 4, 8, 12, 16, 20, 28, 32, 33)
private const val ROW_START_INDEX: Int = 7
private val decFormat = DecimalFormat("#.##")

class ParserCompanyReport() {

    fun parseNameToDataStamp(fileName: String): String {
        val regex = Regex("""\d{4}_\d{1,2}""")
        var dateStamp = ""
        if (regex.containsMatchIn(fileName)) {
            dateStamp = regex.find(fileName)!!.value
        }
        return dateStamp
    }

    fun parseCompanyReport(pathToFile: String): List<List<String>> {

        val exelPrice: List<List<String>>
        val pathUri: String = "$BASE_PATH${pathToFile}"
        val changePrice = mutableListOf<MutableList<String>>()
        var counter = 0

        if (checkFileExists(pathUri)) {
            exelPrice = readPriceToArray(pathUri)
            for (i in ROW_START_INDEX until exelPrice.lastIndex) {
                if ((exelPrice[i].isNotEmpty()) && (exelPrice[i][COLUM_INDEX[0]].isNotEmpty())) {
                    val row = arrayListOf<String>()
                    for (j in COLUM_INDEX)
                        row.add(getFloatFormattedString(exelPrice, i, j))
                    changePrice.add(row)
                    println(changePrice[counter].toString())
                    counter++
                }
            }
            return changePrice
        } else return changePrice
    }

    private fun getFloatFormattedString(
        exelPrice: List<List<String>>,
        i: Int,
        j: Int,
        ) = if (exelPrice[i][j].toFloatOrNull() != null) decFormat.format(exelPrice[i][j].toFloat())
        .toString() else exelPrice[i][j]

    private fun readPriceToArray(pathUri: String): List<List<String>> {
        val newBook = ExelFileRepoImpl().openBook(pathUri)
        return ExelFileRepoImpl().getExelData(newBook, SHEET_NAME)
    }

    private fun checkFileExists(path: String): Boolean = File(path).isFile

}