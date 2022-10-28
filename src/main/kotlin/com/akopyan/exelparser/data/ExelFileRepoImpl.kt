package com.akopyan.exelparser.data

import com.akopyan.exelparser.domain.DuplicatesExelReport
import com.akopyan.exelparser.domain.ExelFileRepo
import com.akopyan.exelparser.domain.WritableInExel
import com.akopyan.exelparser.domain.database.Reports
import com.akopyan.exelparser.utils.ContentChecker
import org.apache.poi.EncryptedDocumentException
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class ExelFileRepoImpl : ExelFileRepo {

    private val contentChecker: ContentChecker = ContentChecker()

    override fun openBook(path: String): XSSFWorkbook {
        var book: XSSFWorkbook? = null

        try {
            val file = File(path)
            book = WorkbookFactory.create(file) as XSSFWorkbook

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: EncryptedDocumentException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return book!!
    }

    override fun getExelData(
        book: XSSFWorkbook,
        sheetName: Int,
        cellIndexes: ArrayList<Int>,
        rowStartIndex: Int
    ): MutableList<MutableList<String>> {

        val evaluator: FormulaEvaluator = book.creationHelper.createFormulaEvaluator()

        val exelData = mutableListOf<MutableList<String>>()
        val sheet: XSSFSheet = book.getSheetAt(sheetName)
        contentChecker.removeIfMerged(sheet)
        val sdf = DataFormatter()
        val rowIterator = sheet.rowIterator()
        var rowCounter = 0

        while (rowIterator.hasNext()) {
            val row = rowIterator.next()
            if (rowCounter >= rowStartIndex) {
                if (!contentChecker.checkIfRowIsEmpty(row)) {
                    val string = mutableListOf<String>()

                    for (i in row.firstCellNum until row.lastCellNum) {
                        if (i in cellIndexes) {
                            val cell = sdf.formatCellValue(row.getCell(i), evaluator)
                            string.add(contentChecker.removeSpaces(cell))
                        }
                    }
                    exelData.add(string)
                }
            }
            rowCounter++
        }
        closeBook(book)
        return exelData
    }

    override fun recordAnyReport(reportsInterface: List<WritableInExel>, savePath: String) {
        val book = XSSFWorkbook()
        val sheet = book.createSheet()
        for (i in 0..reportsInterface.lastIndex) {
            val row = sheet.createRow(i)
            recordFieldToCell(reportsInterface[i], row)
        }
        val out = FileOutputStream(File(savePath))
        book.write(out)
        out.close()
        closeBook(book)
    }

    private fun recordFieldToCell(reportInterface: WritableInExel, row: Row) {
        when (reportInterface) {
            is Reports -> recordMainReportFieldToCell(reportInterface, row)
            is DuplicatesExelReport -> recordDuplicatesReportFieldToCell(reportInterface, row)
        }
    }

    private fun recordMainReportFieldToCell(reportInterface: WritableInExel, row: Row) {
        val report: Reports = reportInterface as Reports
        for (i in 0 until report.fieldsCount) {
            val cell: Cell = row.createCell(i)
            when (i) {
                0 -> cell.setCellValue(report.client.toDouble())
                1 -> cell.setCellValue(report.name)
                2 -> cell.setCellValue(report.netto.toDouble())
                3 -> cell.setCellValue(report.token)
                4 -> cell.setCellValue(report.city)
            }
        }
    }

    private fun recordDuplicatesReportFieldToCell(reportsInterface: WritableInExel, row: Row) {
        val report: DuplicatesExelReport = reportsInterface as DuplicatesExelReport
        for (i in 0 until report.fieldsCount) {
            val cell: Cell = row.createCell(i)
            when (i) {
                0 -> cell.setCellValue(report.token)
                1 -> cell.setCellValue(report.client.toDouble())
                2 -> cell.setCellValue(report.contactDate)
                3 -> cell.setCellValue(report.reportingPeriod)
                4 -> cell.setCellValue(report.netto.toDouble())
            }
        }
    }

    override fun closeBook(book: XSSFWorkbook) {
        book.close()
    }
}