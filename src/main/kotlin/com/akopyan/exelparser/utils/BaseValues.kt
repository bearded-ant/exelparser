package com.akopyan.exelparser.utils

class BaseValues {
    //all
    val BASE_PATH: String = "/home/ant/akopyan/"
//    val BASE_PATH: String = ""

    //company
    val SHEET_NAME_C: String = "Detailed franchisee"
    val SHEET_C_INDEX: Int = 1
    val CELL_INDEXES_C: ArrayList<Int> = arrayListOf(0, 1, 2, 3, 4, 7, 8, 12, 16, 20, 24, 28, 32, 33)
    val ROW_START_INDEX_C: Int = 7

    //employee
    val SHEET_NAME_E: String = "Лист1"
    val SHEET_E_INDEX: Int = 0
    val CELL_INDEXES_E: ArrayList<Int> = arrayListOf(0, 2)
    val ROW_START_INDEX_E: Int = 0

    //file name utils
    val EXT_ERROR = "не верное расширение файла"
    val BODY_ERROR = "имя файла содержит недопустимые символы"
    val ENDING_ERROR = "в имени файла не найден отчетный период YYYY_MM"
    val FILE_EXISTS_ERROR = "файл не найден"
    val FILE_VALID = "верное имя файла"

    //report
    val EMPLOYEE_PATH: String = "/home/ant/employeeReport.xlsx"
//    val EMPLOYEE_PATH: String = "employeeReport.xlsx"

    val DUPLICATES_PATH: String = "/home/ant/duplicateReport.xlsx"
//    val DUPLICATES_PATH: String = "duplicateReport.xlsx"

}