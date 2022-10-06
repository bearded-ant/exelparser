package com.akopyan.exelparser.ui

import com.akopyan.exelparser.domain.Folder
import com.akopyan.exelparser.domain.database.Employee
import com.akopyan.exelparser.domain.database.EmployeeRepo
import com.akopyan.exelparser.domain.database.Treatment
import com.akopyan.exelparser.domain.database.TreatmentRepo
import com.akopyan.exelparser.utils.ParserEmployeeReport
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile

@Controller
@RequestMapping()

class EmployeeReportController {

    private val parser: ParserEmployeeReport = ParserEmployeeReport()

    @Autowired
    private val employeeRepo: EmployeeRepo? = null

    @Autowired
    private val treatmentRepo: TreatmentRepo? = null


    @GetMapping(path = ["/employee"])
    fun showBlanc(): String = "employee"

    @PostMapping(path = ["/employee"])
    fun uploadingEmployeeReport(
        @RequestParam employeeReportFolder: List<MultipartFile>,
        model: MutableMap<String, Any>
    ): String {
        val files = mutableListOf<Folder>()

        updateDb(employeeReportFolder)

        model["employeeReportFolder"] = "employee"
        return "employee"
    }


    private fun updateDb(folderName: List<MultipartFile>) {

        for (file in folderName) {

            val timeStamp = parser.parseNameToTokenAndTimeStamp(file.originalFilename!!).getValue("dateStamp")
            val token = parser.parseNameToTokenAndTimeStamp(file.originalFilename!!).getValue("token")
            //todo надо запилить проверку файлов на формат и содержимое
            val reportFile = parser.parseEmployeeReport(file.originalFilename!!)

            for (i in 0..reportFile.lastIndex) {
                val reportStringHashCode: Int = ("${reportFile[i]}${timeStamp}").hashCode()
                val reportRow = reportFile[i]

                if (employeeRepo!!.findAllByToken(token).isEmpty()) {
                    val employee = Employee(0, token)
                    employeeRepo.save(employee)
                    val treatment =
                        Treatment(
                            0,
                            employeeRepo.findAllByToken(token)[0].id,
                            reportRow[0].toInt(),
                            reportRow[2],
                            timeStamp
                        )
                    treatmentRepo!!.save(treatment)
                } else {
                    val client = reportRow[0].toInt()
                    if (treatmentRepo!!.findAllByClient(client).isEmpty()) {
                        val treatment =
                            Treatment(
                                0,
                                employeeRepo.findAllByToken(token)[0].id,
                                reportRow[0].toInt(),
                                reportRow[2],
                                timeStamp
                            )
                        treatmentRepo.save(treatment)
                    } else {
                        //todo запись есть, надо обрабатывать
                    }
                }
            }
        }
    }
}