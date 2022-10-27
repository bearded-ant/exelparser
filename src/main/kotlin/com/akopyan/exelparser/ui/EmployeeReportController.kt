package com.akopyan.exelparser.ui

import com.akopyan.exelparser.domain.Folder
import com.akopyan.exelparser.domain.database.*
import com.akopyan.exelparser.utils.FileNameUtils
import com.akopyan.exelparser.utils.ParserEmployeeReport
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile
import java.text.SimpleDateFormat

@Controller

class EmployeeReportController {

    private val parser: ParserEmployeeReport = ParserEmployeeReport()
    private val fileNameChecker: FileNameUtils = FileNameUtils()

    @Autowired
    private val employeesRepo: EmployeesRepo? = null

    @Autowired
    private val treatmentsRepo: TreatmentsRepo? = null

    @Autowired
    private val duplicatesRepo: DuplicatesRepo? = null

    @Autowired
    private val reportingPeriodsRepo: ReportingPeriodsRepo? = null

     @GetMapping(path = ["/employee"])
    fun showBlanc(): String = "employee"

    @PostMapping(path = ["/employee"])
    fun uploadingEmployeeReport(
        @RequestParam employeeReportFolder: List<MultipartFile>,
        model: MutableMap<String, Any>
    ): String {

        val folders = updateDb(employeeReportFolder)

        model["employeeReportFolder"] = folders
        return "employee"
    }


    private fun updateDb(foldersList: List<MultipartFile>): List<Folder> {

        val folders = fileNameChecker.fileNameCheck(foldersList)

        if (fileNameChecker.allFilenamesOk(folders)) {

            for (folderName in folders) {

                val timeStamp =
                    fileNameChecker.parseNameToTokenAndTimeStamp(folderName.folderName).getValue("dateStamp")

                val reportPeriodId = getReportingPeriodId(timeStamp)

                val token = fileNameChecker.parseNameToTokenAndTimeStamp(folderName.folderName).getValue("token")
                val reportFile = parser.parseEmployeeReport(folderName.folderName)

                for (i in 0..reportFile.lastIndex) {
                    val reportRow = reportFile[i]
                    val client = reportRow[0].toInt()
                    //если нет записи employee создаем ее и обращение
                    if (employeesRepo!!.findAllByToken(token).isEmpty()) {

                        val employees = Employees(0, token)
                        employeesRepo.save(employees)
                        treatmentBuilder(employees, reportRow, reportPeriodId)

                    } else {

                        val employee = employeesRepo.findAllByToken(token)[0]
                        //employee существует, ищем запись treatment. если такой нет - создаем
                        if (treatmentsRepo!!.findAllByClientAndEmployeeIdAndReportingPeriodId(
                                client,
                                employee.id,
                                reportPeriodId
                            ).isEmpty()
                        ) {
                            treatmentBuilder(employee, reportRow, reportPeriodId)
                        } else {
                            //treatment существует, ищем запись сравниваем с текущей, большую записываем
                            val treatments = treatmentsRepo.findAllByClientAndEmployeeIdAndReportingPeriodId(
                                client,
                                employee.id,
                                reportPeriodId
                            )
                            val savedDay = SimpleDateFormat("dd/MM/yy HH:mm").parse(treatments[0].contactDate)
                            val newDay = SimpleDateFormat("dd/MM/yy HH:mm").parse(reportRow[1])

                            for (treatment in treatments) {
                                if (savedDay < newDay) {
                                    treatmentsRepo.updateContactDate(
                                        contactDate = reportRow[1],
                                        client = client,
                                        employeeId = treatment.employeeId
                                    )
                                }
                            }
                        }
                    }
                }
            }
            //todo временно ерешение. передалать и передать нормально период
            //todo будут проблемы если в файлах разные периоды
            val timeStamp =
                fileNameChecker.parseNameToTokenAndTimeStamp(folders[0].folderName).getValue("dateStamp")

            val reportPeriodId = getReportingPeriodId(timeStamp)

            deleteDuplicates(reportPeriodId)
        } else return folders
        return folders
    }

    private fun getReportingPeriodId(timeStamp: String): Long {
        return if (reportingPeriodsRepo!!.findByReportingPeriod(timeStamp).isEmpty()) {
            with(reportingPeriodsRepo) {
                save(ReportingPeriods(0, timeStamp))
                findByReportingPeriod(timeStamp)[0].id
            }
        } else reportingPeriodsRepo.findByReportingPeriod(timeStamp)[0].id
    }

    private fun treatmentBuilder(
        employees: Employees,
        reportRow: List<String>,
        reportingPeriod: Long
    ) {
        val client = reportRow[0].toInt()
        val treatments =
            Treatments(
                id = 0,
                employeeId = employees.id,
                client = client,
                contactDate = reportRow[1],
                reportingPeriodId = reportingPeriod
            )
        treatmentsRepo!!.save(treatments)
    }

    private fun deleteDuplicates(reportingPeriodId: Long) {
        val dupTreatments = treatmentsRepo!!.findDub(reportingPeriodId)
        for (dupTreatment in dupTreatments) {
            treatmentsRepo.delete(dupTreatment)
            val duplicates =
                with(dupTreatment) { Duplicates(id, employeeId, client, contactDate, reportingPeriodId, 0F) }
            duplicatesRepo!!.save(duplicates)
        }
    }
}