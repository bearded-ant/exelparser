package com.akopyan.exelparser.utils

import org.apache.commons.lang3.StringUtils
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFSheet

class ContentChecker {
    fun checkIfRowIsEmpty(row: Row): Boolean {
        if (row.lastCellNum <= 0) {
            return true;
        }
        for (cellNum in row.firstCellNum..row.lastCellNum) {
            val cell: Cell = row.getCell(cellNum, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
            if (cell.cellType != CellType.BLANK && StringUtils.normalizeSpace(cell.toString()).isNotBlank()) {
                return false
            }
        }
        return true
    }

    fun removeIfMerged(sheet: XSSFSheet) {
        val merged = sheet.mergedRegions.indices.toMutableList()
        println(merged)
        sheet.removeMergedRegions(merged)
    }

    fun removeSpaces(cellValue: String): String = StringUtils.normalizeSpace(cellValue).trim()

}
