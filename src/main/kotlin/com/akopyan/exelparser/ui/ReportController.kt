package com.akopyan.exelparser.ui

import com.akopyan.exelparser.domain.database.*
import com.akopyan.exelparser.utils.BaseValues
import com.akopyan.exelparser.utils.SaveReport
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import java.text.DecimalFormat


@Controller
class ReportController {
    private val baseValues: BaseValues = BaseValues()
    private val saveReport: SaveReport = SaveReport()

    @Autowired
    private val reportsRepo: ReportsRepo? = null

    @Autowired
    private val duplicatesRepo: DuplicatesRepo? = null

    @Autowired
    private val employeesRepo: EmployeesRepo? = null

    @Autowired
    private val treatmentsRepo: TreatmentsRepo? = null

    @Autowired
    private val reportingPeriodsRepo: ReportingPeriodsRepo? = null

    @GetMapping(path = ["/report"])
    fun showBlanc(model: MutableMap<String, Any>): String {

        val reportPeriod = reportingPeriodsRepo!!.findAll()
        val period = mutableSetOf<String>()
        for (reportRow in reportPeriod)
            period.add(reportRow.reportingPeriod)
        model["reportPeriod"] = period
        return "report"
    }

    @PostMapping(path = ["/report"])
    fun uploadingEmployeeReport(
        @RequestParam selectPeriod: String,
        @RequestParam reportType: String,
        model: MutableMap<String, Any>
    ): String {

        showBlanc(model)
        val selectPeriodId = reportingPeriodsRepo!!.findByReportingPeriod(selectPeriod)[0].id
        if (reportType == "MAIN") {
            val reportWithPeriod = reportsRepo!!.generateReportForReportingPeriodId(selectPeriodId)
            val stringFormattedReport: MutableList<List<String>> = mutableListOf()
            for (i in 0..reportWithPeriod.lastIndex) {
                stringFormattedReport.add(mainReportToRow(reportWithPeriod[i]))
            }
            saveReport.saveReport(stringFormattedReport, baseValues.NUMERIC_CELL_MAIN, baseValues.EMPLOYEE_PATH)
            model["reports"] = reportWithPeriod
        } else {
            val duplicatesReport = duplicatesRepo!!.findAllByReportingPeriodId(selectPeriodId)
            val result: MutableList<Any> = mutableListOf()
            val stringFormattedReport: MutableList<List<String>> = mutableListOf()

            for (duplicate in duplicatesReport) {
                val token: String = employeesRepo!!.findById(duplicate.tokenId).get().token
                val resultRow = reportDuplicates(token, duplicate)
                result.add(resultRow)

                stringFormattedReport.add(
                    arrayListOf(
                        resultRow.token,
                        resultRow.client.toString(),
                        resultRow.contactDate,
                        selectPeriod,
                        resultRow.netto.toString()
                    )
                )
            }
            saveReport.saveReport(stringFormattedReport, baseValues.NUMERIC_CELL_DUPLICATES, baseValues.DUPLICATES_PATH)
            model["duplicates"] = result
        }
        return "report"
    }

    private fun reportDuplicates(token: String, duplicate: Duplicates) = object {
        val token: String = token
        val client: Int = duplicate.client
        val contactDate: String = duplicate.contactDate
        val reportingPeriodId: String = getReportPeriod(duplicate.reportingPeriodId)
        val netto: Float = getNetto(duplicate.client)
    }

    private fun mainReportToRow(reportsWithPeriod: Reports): List<String> {
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

    private fun getNetto(client: Int): Float = treatmentsRepo!!.calculateNettoForDuplicate(client)?:0f

    private fun getReportPeriod(reportPeriodId: Long): String =
        reportingPeriodsRepo!!.findById(reportPeriodId).get().reportingPeriod
}