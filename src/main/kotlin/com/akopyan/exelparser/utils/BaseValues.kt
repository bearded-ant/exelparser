package com.akopyan.exelparser.utils

class BaseValues {
    //all
    val BASE_PATH: String = "/home/ant/akopyan/"
    //private const val BASE_PATH: String = ""

    //company
    val SHEET_NAME_C: String = "Detailed franchisee"
    val COLUM_INDEX_C: ArrayList<Int> = arrayListOf(0, 1, 2, 3, 4, 8, 12, 16, 20, 28, 32, 33)
    val ROW_START_INDEX_C: Int = 7

    //employee
    val SHEET_NAME_E: String = "Лист1"
    val COLUM_INDEX_E: ArrayList<Int> = arrayListOf(0, 2)
    val ROW_START_INDEX_E: Int = 0

    //file name utils
    val EXT_ERROR = "extension file wrong"
    val BODY_ERROR = "file name contains invalid characters"
    val ENDING_ERROR = "there is no report period in the file name"
    val FILE_EXISTS_ERROR = "no such file"
    val FILE_VALID = "valid name"

}