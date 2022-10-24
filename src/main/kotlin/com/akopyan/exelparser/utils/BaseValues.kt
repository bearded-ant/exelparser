package com.akopyan.exelparser.utils

class BaseValues {
    //all
    val BASE_PATH: String = "/home/ant/akopyan/"
//    val BASE_PATH: String = ""

    //company
    val SHEET_NAME_C: String = "Detailed franchisee"
    val SHEET_NAME_C_INDEX: Int = 1
    val COLUM_INDEX_C: ArrayList<Int> = arrayListOf(0, 1, 2, 3, 4, 8, 12, 16, 20, 28, 32, 33)
    val ROW_START_INDEX_C: Int = 7

    //employee
    val SHEET_NAME_E: String = "Лист1"
    val SHEET_NAME_E_INDEX: Int = 0
    val COLUM_INDEX_E: ArrayList<Int> = arrayListOf(0, 2)
    val ROW_START_INDEX_E: Int = 0

    //file name utils
    val EXT_ERROR = "extension file wrong"
    val BODY_ERROR = "file name contains invalid characters"
    val ENDING_ERROR = "there is no report period in the file name"
    val FILE_EXISTS_ERROR = "no such file"
    val FILE_VALID = "valid name"

    //report
    val NUMERIC_CELL_MAIN: List<Int> = arrayListOf(0, 2)
    val NUMERIC_CELL_DUPLICATES: List<Int> = arrayListOf(1, 4)

        val EMPLOYEE_PATH: String = "/home/ant/employeeReport.xlsx"
//    val EMPLOYEE_PATH: String = "employeeReport.xlsx"

        val DUPLICATES_PATH: String = "/home/ant/duplicateReport.xlsx"
//    val DUPLICATES_PATH: String = "duplicateReport.xlsx"

}