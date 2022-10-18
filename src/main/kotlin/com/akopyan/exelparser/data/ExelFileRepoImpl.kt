package com.akopyan.exelparser.data

import com.akopyan.exelparser.domain.ExelFileRepo
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

    override fun getExelData(book: XSSFWorkbook, sheetName: String): MutableList<MutableList<String>> {

        val evaluator: FormulaEvaluator = book.creationHelper.createFormulaEvaluator()

        val exelData = mutableListOf<MutableList<String>>()
        val sheet: XSSFSheet = book.getSheet(sheetName)
        val rowIterator: Iterator<Row> = sheet.rowIterator()

        while (rowIterator.hasNext()) {
            val row: XSSFRow = rowIterator.next() as XSSFRow
            val cellIterator: Iterator<Cell> = row.cellIterator()
            val string = mutableListOf<String>()
            val sdf = DataFormatter()

            while (cellIterator.hasNext()) {
                val cell = sdf.formatCellValue(cellIterator.next(), evaluator)
                string.add(cell)
            }
            exelData.add(string)
        }
        closeBook(book)
        return exelData
    }


    override fun writeBook(changePrice: List<List<String>>, filePath: String) {

        val changeBook = XSSFWorkbook()
        val changeSheet = changeBook.createSheet()
        for (i in 0..changePrice.lastIndex) {
            val row = changeSheet.createRow(i)
            for (j in 0..changePrice[i].lastIndex) {
                val cell = row.createCell(j, CellType.STRING)
                cell.setCellValue(changePrice[i][j])
            }
        }

        val out = FileOutputStream(File(filePath))
        changeBook.write(out)
        out.close()

        closeBook(changeBook)
    }

    override fun closeBook(book: XSSFWorkbook) {
        book.close()
    }
}