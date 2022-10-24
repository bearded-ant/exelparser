package com.akopyan.exelparser.utils

import com.akopyan.exelparser.data.ExelFileRepoImpl
import com.akopyan.exelparser.domain.WritableInExel

class SaveReport {
    private val exelRepo = ExelFileRepoImpl()

    fun saveReport(reports: List<WritableInExel>, savePath: String) {
        exelRepo.recordAnyReport(reports, savePath)
    }
}