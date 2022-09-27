package com.akopyan.exelparser.domain

import org.apache.poi.xssf.usermodel.XSSFWorkbook

interface ExelFileRepo {
    fun openBook(path: String): XSSFWorkbook
    fun getExelData(book: XSSFWorkbook, sheetName: String):List<List<String>>
    fun writeBook(changePrice: MutableList<MutableList<String>>, filePath: String)
    fun closeBook(book: XSSFWorkbook)
}