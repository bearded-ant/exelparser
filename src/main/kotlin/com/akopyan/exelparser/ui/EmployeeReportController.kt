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
import java.text.SimpleDateFormat

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
        //todo надо запилить проверку файлов на формат и содержимое
        for (file in folderName) {

            val timeStamp = parser.parseNameToTokenAndTimeStamp(file.originalFilename!!).getValue("dateStamp")
            val token = parser.parseNameToTokenAndTimeStamp(file.originalFilename!!).getValue("token")
            val reportFile = parser.parseEmployeeReport(file.originalFilename!!)

            for (i in 0..reportFile.lastIndex) {
                val reportRow = reportFile[i]
//если не записи employee создаем ее и обращение
                if (employeeRepo!!.findAllByToken(token).isEmpty()) {
                    val employee = Employee(0, token)
                    employeeRepo.save(employee)
                    val treatment =
                        Treatment(
                            0,
                            employeeRepo.findAllByToken(token)[0].id,
                            reportRow[0].toInt(),
                            reportRow[1],
                            timeStamp
                        )
                    treatmentRepo!!.save(treatment)
                } else {
                    val client = reportRow[0].toInt()
//employee существует, ищем запись treatment. если такой нет - создаем
                    if (treatmentRepo!!.findAllByClient(client).isEmpty()) {
                        val treatment =
                            Treatment(
                                0,
                                employeeRepo.findAllByToken(token)[0].id,
                                reportRow[0].toInt(),
                                reportRow[1],
                                timeStamp
                            )
                        treatmentRepo.save(treatment)
                    } else {
//treatment существует, ищем запись сравниваем с текущей, большую записываем
                        val treatments = treatmentRepo.findAllByClient(client)
                        var savedDay = SimpleDateFormat("dd/MM/yy HH:mm").parse(treatments[0].contactDate)
                        var newDay = SimpleDateFormat("dd/MM/yy HH:mm").parse(reportRow[1])
                        for (treatment in treatments) {
                            if (savedDay < newDay) {
                                val treatment =
                                    Treatment(
                                        0,
                                        employeeRepo.findAllByToken(token)[0].id,
                                        reportRow[0].toInt(),
                                        reportRow[1],
                                        timeStamp
                                    )
                                treatmentRepo.save(treatment)
                            }
                        }
                    }
                }
            }
        }
    }
}