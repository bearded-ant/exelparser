package com.akopyan.exelparser


import com.akopyan.exelparser.data.ExelFileRepoImpl
import java.text.DecimalFormat

const val PATH_FILE: String = "/home/ant/Ekaterinburg_2022_7.xlsx"
const val SHEET_NAME: String = "Detailed franchisee"
const val RESULT_PATH_FILE: String = "/home/ant/a_$SHEET_NAME.xlsx"
private val COLUM_INDEX: ArrayList<Int> = arrayListOf(1, 2, 3, 4, 8, 12, 16, 20, 28, 32, 33)

fun main(args: Array<String>) {

    val exelPrice = readPriceToArray()
    val changePrice = mutableListOf<MutableList<String>>()
    val decFormat = DecimalFormat("#.##")
    var counter = 0


    for (i in 0..exelPrice.lastIndex) {
        if (exelPrice[i].lastIndex > 30) {
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

//    ExelFileRepoImpl().writeBook(changePrice, RESULT_PATH_FILE)

}

private fun readPriceToArray(): MutableList<MutableList<String>> {
    val newBook = ExelFileRepoImpl().openBook(PATH_FILE)
    return ExelFileRepoImpl().getExelData(newBook, SHEET_NAME)
}
