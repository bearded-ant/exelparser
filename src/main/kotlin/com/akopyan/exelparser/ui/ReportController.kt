package com.akopyan.exelparser.ui

import com.akopyan.exelparser.domain.database.*
import com.akopyan.exelparser.utils.SaveReport
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import java.text.DecimalFormat

@Controller
class ReportController {

    val saveReport: SaveReport = SaveReport()

    @Autowired
    private val reportsRepo: ReportsRepo? = null

    @Autowired
    private val duplicatesRepo: DuplicatesRepo? = null

    @Autowired
    private val employeesRepo: EmployeesRepo? = null

    @GetMapping(path = ["/report"])
    fun showBlanc(model: MutableMap<String, Any>): String {

        val reportPeriod = reportsRepo!!.getReportPeriod()
        val period = mutableSetOf<String>()
        for (reportRow in reportPeriod)
            period.add(reportRow)
        model["reportPeriod"] = period
        return "report"
    }

    @PostMapping(path = ["/report"])
    fun uploadingEmployeeReport(
        @RequestParam selectPeriod: String,
        @RequestParam reportType: String,
        model: MutableMap<String, Any>
    ): String {

        val reportWithPeriod = reportsRepo!!.generateReportForReportingPeriod(selectPeriod)

        val reportPeriod = reportsRepo.getReportPeriod()
        val period = mutableSetOf<String>()
        for (reportRow in reportPeriod)
            period.add(reportRow)
        model["reportPeriod"] = period
        if (reportType == "MAIN") {
            model["reports"] = reportWithPeriod

            val stringFormattedReport: MutableList<List<String>> = mutableListOf()
            for (i in 0..reportWithPeriod.lastIndex) {
                stringFormattedReport.add(reportToRow(reportWithPeriod[i]))
            }
            saveReport.saveReport(stringFormattedReport, "/home/ant/employeeReport.xlsx")
//            saveReport.saveReport(stringFormattedReport, "employeeReport.xlsx")

        } else {
            val duplicatesReport = duplicatesRepo!!.findAll()
            val result: MutableList<Any> = mutableListOf()
            val stringFormattedReport: MutableList<List<String>> = mutableListOf()

            for (value in duplicatesReport) {
                val token: String = employeesRepo!!.findById(value.tokenId).get().token
                val resultRow = object {
                    val token: String = token
                    val client: Int = value.client
                    val contactDate: String = value.contactDate
                    val reportingPeriod: String = value.reportingPeriod
                }
                result.add(resultRow)
                stringFormattedReport.add(duplicatesReportToRow(value, token))
            }
            saveReport.saveReport(stringFormattedReport, "/home/ant/duplicateReport.xlsx")
//            saveReport.saveReport(stringFormattedReport, "duplicateReport.xlsx")
            model["duplicates"] = result
        }
        return "report"
    }

    private fun duplicatesReportToRow(value: Duplicates, token: String): List<String> {
        val row = mutableListOf<String>()
        with(value) {
            row.add(token)
            row.add(client.toString())
            row.add(contactDate)
            row.add(reportingPeriod)
        }
        return row
    }

    private fun reportToRow(reportsWithPeriod: Reports): List<String> {
        val row = mutableListOf<String>()
        with(reportsWithPeriod) {
            row.add(client.toString())
            row.add(name)
            row.add(getFloatFormattedString(netto))
            row.add(token)
            row.add(city)
        }
        return row
    }

    private fun getFloatFormattedString(string: Float): String {
        val decFormat = DecimalFormat("#.##")
        return decFormat.format(string)
    }
}