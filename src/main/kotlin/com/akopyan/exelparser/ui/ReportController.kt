package com.akopyan.exelparser.ui

import com.akopyan.exelparser.domain.database.DuplicatesRepo
import com.akopyan.exelparser.domain.database.EmployeeRepo
import com.akopyan.exelparser.domain.database.ReportRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping()
class ReportController {

    @Autowired
    private val reportRepo: ReportRepo? = null

    @Autowired
    private val duplicatesRepo: DuplicatesRepo? = null

    @GetMapping(path = ["/report"])
    fun showBlanc(model: MutableMap<String, Any>): String {

        val report = reportRepo!!.generateReport()
        val period = mutableSetOf<String>()
        for (reportRow in report)
            period.add(reportRow.reportingPeriod)
        model["reportPeriod"] = period
//        model["reports"] = report
        return "report"
    }

    @PostMapping(path = ["/report"])
    fun uploadingEmployeeReport(
        @RequestParam selectPeriod: String,
        @RequestParam reportType: String,
        model: MutableMap<String, Any>
    ): String {

        val reportWithPeriod = reportRepo!!.generateReportForReportingPeriod(selectPeriod)

        val report = reportRepo.generateReport()
        val period = mutableSetOf<String>()
        for (reportRow in report)
            period.add(reportRow.reportingPeriod)
        model["reportPeriod"] = period
        if (reportType == "MAIN") {
            model["reports"] = reportWithPeriod
        } else {
            val duplicatesReport = duplicatesRepo!!.findAll()
            model["duplicates"] = duplicatesReport
        }
        return "report"
    }
}