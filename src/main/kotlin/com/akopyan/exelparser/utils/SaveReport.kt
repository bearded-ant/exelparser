package com.akopyan.exelparser.utils

import com.akopyan.exelparser.data.ExelFileRepoImpl

class SaveReport {
    private val exelRepo = ExelFileRepoImpl()

    fun saveReport(report:List<List<String>>, numericCell:List<Int>, path:String) {
        exelRepo.writeBook(report, numericCell, path)
    }
}