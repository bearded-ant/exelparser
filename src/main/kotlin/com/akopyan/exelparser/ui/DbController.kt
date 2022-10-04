package com.akopyan.exelparser.ui

import com.akopyan.exelparser.utils.ParseXMLX
import com.akopyan.exelparser.domain.Folder
import com.akopyan.exelparser.domain.database.ClientRepo
import com.akopyan.exelparser.domain.database.Finances
import com.akopyan.exelparser.domain.database.FinancesRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile

@Controller
@RequestMapping(path = ["/db"])

class DbController {

    private val parser: ParseXMLX = ParseXMLX()

    @Autowired
    private val financeRepository: FinancesRepo? = null
    private val clientRepo: ClientRepo? = null

//    @PostMapping(path = ["/all"])
//    fun add(@RequestParam name: String?, model: MutableMap<String, Any>): String {
//        val n = H2db(0, name)
//        userRepository!!.save(n)
//
//        val allUsers = userRepository.findAll()
//        model["h2db"] = allUsers
//
//        return "dball"
//    }

    @GetMapping(path = ["/all"])
    fun viewAll(model: MutableMap<String, Iterable<Finances>>): String {
        val finances = financeRepository!!.findAll()
        model["finances"] = finances
        return "dball"
    }

    @GetMapping(path = ["/uploading"])
    fun showBlanc(): String = "uploading"


    @PostMapping(path = ["/uploading"])
    fun uploadingCompanyReport(@RequestParam folderName: List<MultipartFile>, model: MutableMap<String, Any>): String {
        val files = mutableListOf<Folder>()
        var result = ""

        updateDb(folderName)

        model["folders"] = files
        return "uploading"
    }


    private fun updateDb(folderName: List<MultipartFile>) {

        for (file in folderName) {

            val timeStamp = parser.parseNameToDataStamp(file.originalFilename!!)
            val reportFile = parser.parseCompanyReport(file.originalFilename!!)

            for (i in 0..reportFile.lastIndex) {
                val reportStringHashCode: Int = reportFile[i].toString().hashCode()
                val dbEntityHashCode = financeRepository!!.findByHas(reportStringHashCode)

                if (dbEntityHashCode.isEmpty()) {
                    val finance = financeEntityBuilder(reportFile[i], reportStringHashCode, timeStamp)
                    financeRepository.save(finance)
                }
            }
//            files.add(Folder(file.originalFilename))
        }
    }


    private fun financeEntityBuilder(
        financeParse: List<String>,
        simpleHashCode: Int,
        reportDate: String
    ): Finances {
        return Finances(
            id = 0,
            accountId = financeParse[1].toInt(),
            clearing = financeParse[5],
            floating = financeParse[6],
            bonusRISK = financeParse[7],
            deposit = financeParse[8],
            netto = financeParse[9],
            bonusPIPS = financeParse[10],
            IbPayment = financeParse[11],
            has = simpleHashCode,
            report_date = reportDate
        )
    }
}