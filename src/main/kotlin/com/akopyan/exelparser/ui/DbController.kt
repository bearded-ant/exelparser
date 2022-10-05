package com.akopyan.exelparser.ui

import com.akopyan.exelparser.domain.Folder
import com.akopyan.exelparser.domain.database.*
import com.akopyan.exelparser.utils.ParserCompanyReport
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile

@Controller
@RequestMapping()

class DbController {

    private val parser: ParserCompanyReport = ParserCompanyReport()

    @Autowired
    private val financeRepository: FinancesRepo? = null
    @Autowired
    private val clientRepo: ClientRepo? = null
    @Autowired
    private val accountRepo: AccountRepo? = null


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


        updateDb(folderName)

        model["folders"] = files
        return "uploading"
    }


    private fun updateDb(folderName: List<MultipartFile>) {

        for (file in folderName) {

            val timeStamp = parser.parseNameToDataStamp(file.originalFilename!!)
            val reportFile = parser.parseCompanyReport(file.originalFilename!!)

            for (i in 0..reportFile.lastIndex) {
                val reportStringHashCode: Int = ("${reportFile[i]}${timeStamp}").hashCode()
                val dbEntityHashCode = financeRepository!!.findByHash(reportStringHashCode)

                if (dbEntityHashCode.isEmpty()) {
                    val reportRow = reportFile[i]
                    if (clientRepo!!.findByClientId(reportRow[1].toInt()).isEmpty()) {

                        createAllTablesEntity(reportRow, reportStringHashCode, timeStamp)

                    } else {
                        val client = clientRepo.findByClientId(reportRow[1].toInt())
                        if (accountRepo!!.findByAccount(reportRow[3]).isEmpty()) {
                            createAccountAndFinancesTableEntity(
                                reportRow,
                                client[0].id,
                                reportStringHashCode,
                                timeStamp,
                            )
                        } else {
                            val account = accountRepo.findByAccount(reportRow[3])
                            createFinanceTaleEntity(reportRow, account[0].id, reportStringHashCode, timeStamp)
                        }
                    }
                }
            }
        }
//            files.add(Folder(file.originalFilename))
    }

    private fun createFinanceTaleEntity(
        reportRow: List<String>,
        accountId: Int,
        reportStringHashCode: Int,
        timeStamp: String,

        ) {
        val finance = financeBuilder(reportRow, accountId, reportStringHashCode, timeStamp)
        financeRepository!!.save(finance)
    }

    private fun createAccountAndFinancesTableEntity(
        reportRow: List<String>,
        clientId: Int,
        reportStringHashCode: Int,
        timeStamp: String,
    ) {
        val account = accountBuilder(reportRow, clientId)
        accountRepo!!.save(account)
        createFinanceTaleEntity(reportRow, account.id, reportStringHashCode, timeStamp)
    }

    private fun createAllTablesEntity(
        reportRow: List<String>,
        reportStringHashCode: Int,
        timeStamp: String,
    ) {
        val client = clientBuilder(reportRow)
        clientRepo!!.save(client)
        val account = accountBuilder(reportRow, client.id)
        accountRepo!!.save(account)
        createFinanceTaleEntity(reportRow, account.id, reportStringHashCode, timeStamp)
    }


    private fun clientBuilder(reportFile: List<String>): Client =
        Client(
            id = 0,
            clientId = reportFile[1].toInt(),
            name = reportFile[2],
            city = reportFile[0]
        )

    private fun accountBuilder(reportFile: List<String>, clientId: Int): Account =
        Account(
            id = 0,
            clientId = clientId,
            account = reportFile[3],
            accountCurrency = reportFile[4]
        )

    private fun financeBuilder(reportFile: List<String>, accountId: Int, hashCode: Int, reportDate: String): Finances =
        Finances(
            id = 0,
            accountId = accountId,
            clearing = reportFile[5],
            floating = reportFile[6],
            bonusRISK = reportFile[7],
            deposit = reportFile[8],
            netto = reportFile[9],
            bonusPIPS = reportFile[10],
            IbPayment = reportFile[11],
            hash = hashCode,
            report_date = reportDate
        )
}