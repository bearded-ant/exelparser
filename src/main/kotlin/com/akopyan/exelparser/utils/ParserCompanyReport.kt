package com.akopyan.exelparser.utils


import com.akopyan.exelparser.data.ExelFileRepoImpl
import java.io.File

class ParserCompanyReport() {

    private val baseValues: BaseValues = BaseValues()

    fun parseCompanyReport(pathToFile: String): List<List<String>> {

        val exelPrice: List<List<String>>
        val pathUri = "${baseValues.BASE_PATH}${pathToFile}"
        val changePrice = mutableListOf<MutableList<String>>()
        var counter = 0

        if (checkFileExists(pathUri)) {
            exelPrice = readPriceToArray(pathUri)
            for (i in baseValues.ROW_START_INDEX_C until exelPrice.lastIndex) {
                if ((exelPrice[i].isNotEmpty()) && (exelPrice[i][baseValues.CELL_INDEXES_C[0]].isNotEmpty())) {
                    val row = arrayListOf<String>()
                    for (j in 0..exelPrice[i].lastIndex)
                        row.add(exelPrice[i][j])
                    changePrice.add(row)
//                    println(changePrice[counter].toString())
                    counter++
                }
            }
            return changePrice
        } else return changePrice
    }

    private fun readPriceToArray(pathUri: String): List<List<String>> {
        val newBook = ExelFileRepoImpl().openBook(pathUri)
        return ExelFileRepoImpl().getExelData(newBook, baseValues.SHEET_C_INDEX, baseValues.CELL_INDEXES_C)
    }

    private fun checkFileExists(path: String): Boolean = File(path).isFile
}