package com.akopyan.exelparser


import com.akopyan.exelparser.data.ExelFileRepoImpl
import java.text.DecimalFormat
import kotlin.io.path.Path

const val BASE_PATH_FILE: String = "/home/ant/akopyan/"
const val SHEET_NAME: String = "Detailed franchisee"
const val RESULT_PATH_FILE: String = "/home/ant/a_$SHEET_NAME.xlsx"
private val COLUM_INDEX: ArrayList<Int> = arrayListOf(1, 2, 3, 4, 8, 12, 16, 20, 28, 32, 33)
private val ROW_START_INDEX: Int = 7

class ParseXMLX() {
    fun parse(PATH_FILE: String): List<List<String>> {

        val exelPrice = readPriceToArray(PATH_FILE)
        val changePrice = mutableListOf<MutableList<String>>()
        val decFormat = DecimalFormat("#.##")
        var counter = 0


        for (i in ROW_START_INDEX..exelPrice.lastIndex) {
            if (exelPrice[i].lastIndex == COLUM_INDEX[COLUM_INDEX.lastIndex] && exelPrice[i].isNotEmpty()) {
                val row = arrayListOf<String>()
                for (j in COLUM_INDEX)
                    row.add(
                        if (exelPrice[i][j].toFloatOrNull() != null) decFormat.format(exelPrice[i][j].toFloat())
                            .toString() else exelPrice[i][j]
                    )
                changePrice.add(row)
                println(changePrice[counter].toString())
                counter++
            }
        }
        return changePrice
    }

    private fun readPriceToArray(PATH_FILE: String): MutableList<MutableList<String>> {
        val pathUri = Path("${BASE_PATH_FILE}${PATH_FILE}")
        val newBook = ExelFileRepoImpl().openBook(pathUri.toString())
        return ExelFileRepoImpl().getExelData(newBook, SHEET_NAME)
    }
}