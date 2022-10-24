package com.akopyan.exelparser.domain

import org.apache.poi.xssf.usermodel.XSSFWorkbook

interface ExelFileRepo {
    fun openBook(path: String): XSSFWorkbook
    fun getExelData(book: XSSFWorkbook, sheetName: Int):List<List<String>>
    fun recordAnyReport(reportsInterface: List<WritableInExel>, savePath: String)
    fun closeBook(book: XSSFWorkbook)
}