package com.akopyan.exelparser.ui

import com.akopyan.exelparser.domain.Folder
import com.akopyan.exelparser.domain.database.*
import com.akopyan.exelparser.utils.FileNameUtils
import com.akopyan.exelparser.utils.ParserEmployeeReport
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile
import java.text.SimpleDateFormat

@Controller
@RequestMapping()

class EmployeeReportController {

    private val parser: ParserEmployeeReport = ParserEmployeeReport()
    private val fileNameChecker: FileNameUtils = FileNameUtils()

    @Autowired
    private val employeesRepo: EmployeesRepo? = null

    @Autowired
    private val treatmentsRepo: TreatmentsRepo? = null

    @Autowired
    private val duplicatesRepo: DuplicatesRepo? = null

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

                val reportingPeriod =
                    fileNameChecker.parseNameToTokenAndTimeStamp(folderName.folderName).getValue("dateStamp")
                val token = fileNameChecker.parseNameToTokenAndTimeStamp(folderName.folderName).getValue("token")
                val reportFile = parser.parseEmployeeReport(folderName.folderName)

                for (i in 0..reportFile.lastIndex) {
                    val reportRow = reportFile[i]
                    //если нет записи employee создаем ее и обращение
                    if (employeesRepo!!.findAllByToken(token).isEmpty()) {

                        val employees = Employees(0, token)
                        employeesRepo.save(employees)
                        treatmentBuilder(employees, reportRow, reportingPeriod)

                    } else {
                        val client = reportRow[0].toInt()
                        val employee = employeesRepo.findAllByToken(token)[0]
                        //employee существует, ищем запись treatment. если такой нет - создаем
                        if (treatmentsRepo!!.findAllByClientAndTokenIdAndReportingPeriod(
                                client,
                                employee.id,
                                reportingPeriod
                            ).isEmpty()
                        ) {
                            treatmentBuilder(employee, reportRow, reportingPeriod)
                        } else {
                            //treatment существует, ищем запись сравниваем с текущей, большую записываем
                            val treatments = treatmentsRepo.findAllByClientAndTokenIdAndReportingPeriod(
                                client,
                                employee.id,
                                reportingPeriod
                            )
                            val savedDay = SimpleDateFormat("dd/MM/yy HH:mm").parse(treatments[0].contactDate)
                            val newDay = SimpleDateFormat("dd/MM/yy HH:mm").parse(reportRow[1])

                            for (treatment in treatments) {
                                if (savedDay < newDay) {
                                    treatmentsRepo.updateContactDate(
                                        reportRow[1],
                                        reportRow[0].toInt(),
                                        treatment.tokenId
                                    )
                                }
                            }
                        }
                    }
                }
            }
            deleteDuplicates()
        } else return folders
        return folders
    }

    private fun treatmentBuilder(
        employees: Employees,
        reportRow: List<String>,
        reportingPeriod: String
    ) {
        val treatments =
            Treatments(
                id = 0,
                tokenId = employees.id,
                client = reportRow[0].toInt(),
                contactDate = reportRow[1],
                reportingPeriod = reportingPeriod
            )
        treatmentsRepo!!.save(treatments)
    }

    private fun deleteDuplicates() {
        val dupTreatments = treatmentsRepo!!.findDub()
        for (dupTreatment in dupTreatments) {
            treatmentsRepo.delete(dupTreatment)
            val duplicates =
                with(dupTreatment) { Duplicates(id, tokenId, client, contactDate, reportingPeriod, 0F) }
            duplicatesRepo!!.save(duplicates)
        }
    }
}