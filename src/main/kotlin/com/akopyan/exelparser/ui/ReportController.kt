package com.akopyan.exelparser.ui

import com.akopyan.exelparser.domain.DuplicatesExelReport
import com.akopyan.exelparser.domain.database.*
import com.akopyan.exelparser.utils.BaseValues
import com.akopyan.exelparser.utils.SaveReport
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam


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
            saveReport.saveReport(reportWithPeriod, baseValues.EMPLOYEE_PATH)
            model["reports"] = reportWithPeriod
        } else {
            val duplicatesReport = duplicatesRepo!!.findAllByReportingPeriodId(selectPeriodId)
            val recordableResult: MutableList<DuplicatesExelReport> = mutableListOf()

            for (duplicate in duplicatesReport) {
                val token: String = employeesRepo!!.findById(duplicate.tokenId).get().token
                val resultRow = dbDuplicatesToExelFormat(token, duplicate)
                recordableResult.add(resultRow)
            }
            saveReport.saveReport(recordableResult, baseValues.DUPLICATES_PATH)
            model["duplicates"] = recordableResult
        }
        return "report"
    }

    private fun dbDuplicatesToExelFormat(token: String, duplicate: Duplicates) = DuplicatesExelReport(
        token = token,
        client = duplicate.client,
        contactDate = duplicate.contactDate,
        reportingPeriod = getReportPeriod(duplicate.reportingPeriodId),
        netto = getNetto(duplicate.client)
    )

    private fun getNetto(client: Int): Float = treatmentsRepo!!.calculateNettoForDuplicate(client) ?: 0f

    private fun getReportPeriod(reportPeriodId: Long): String =
        reportingPeriodsRepo!!.findById(reportPeriodId).get().reportingPeriod
}