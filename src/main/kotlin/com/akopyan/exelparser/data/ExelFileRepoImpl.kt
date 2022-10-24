package com.akopyan.exelparser.data

import com.akopyan.exelparser.domain.DuplicatesExelReport
import com.akopyan.exelparser.domain.ExelFileRepo
import com.akopyan.exelparser.domain.WritableInExel
import com.akopyan.exelparser.domain.database.Reports
import com.akopyan.exelparser.utils.ContentChecker
import org.apache.poi.EncryptedDocumentException
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFRow
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

    override fun getExelData(book: XSSFWorkbook, sheetName: Int): MutableList<MutableList<String>> {

        val evaluator: FormulaEvaluator = book.creationHelper.createFormulaEvaluator()

        val exelData = mutableListOf<MutableList<String>>()
        val sheet: XSSFSheet = book.getSheetAt(sheetName)
        contentChecker.removeIfMerged(sheet)
        val rowIterator: Iterator<Row> = sheet.rowIterator()
        val sdf = DataFormatter()

        while (rowIterator.hasNext()) {
            val row: XSSFRow = rowIterator.next() as XSSFRow
            if (!contentChecker.checkIfRowIsEmpty(row)) {
                val cellIterator: Iterator<Cell> = row.cellIterator()
                val string = mutableListOf<String>()

                while (cellIterator.hasNext()) {
                    val cell = sdf.formatCellValue(cellIterator.next(), evaluator)
                    string.add(contentChecker.removeSpaces(cell))
                }
                exelData.add(string)
            }
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
        val mainReport: Reports = reportInterface as Reports
        for (j in 0 until mainReport.fieldsCount) {
            val cell: Cell = row.createCell(j)
            when (j) {
                0 -> cell.setCellValue(mainReport.client.toDouble())
                1 -> cell.setCellValue(mainReport.name)
                2 -> cell.setCellValue(mainReport.netto.toDouble())
                3 -> cell.setCellValue(mainReport.token)
                4 -> cell.setCellValue(mainReport.city)
            }
        }
    }

    private fun recordDuplicatesReportFieldToCell(reportsInterface: WritableInExel, row: Row) {
        val duplicateReport: DuplicatesExelReport = reportsInterface as DuplicatesExelReport
        for (j in 0 until duplicateReport.fieldsCount) {
            val cell: Cell = row.createCell(j)
            when (j) {
                0 -> cell.setCellValue(duplicateReport.token)
                1 -> cell.setCellValue(duplicateReport.client.toDouble())
                2 -> cell.setCellValue(duplicateReport.contactDate)
                3 -> cell.setCellValue(duplicateReport.reportingPeriod)
                4 -> cell.setCellValue(duplicateReport.netto.toDouble())
            }
        }
    }

    override fun closeBook(book: XSSFWorkbook) {
        book.close()
    }
}