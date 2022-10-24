package com.akopyan.exelparser.domain

import org.apache.poi.xssf.usermodel.XSSFWorkbook

interface ExelFileRepo {
    fun openBook(path: String): XSSFWorkbook
    fun getExelData(book: XSSFWorkbook, sheetName: Int):List<List<String>>
    fun writeBook(changePrice: List<List<String>>, numericCell: List<Int>, filePath: String)
    fun closeBook(book: XSSFWorkbook)
}