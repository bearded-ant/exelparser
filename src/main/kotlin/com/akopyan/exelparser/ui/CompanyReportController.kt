package com.akopyan.exelparser.ui

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

class CompanyReportController {

    private val parser: ParserCompanyReport = ParserCompanyReport()

    @Autowired
    private val financeRepository: FinancesRepo? = null

    @Autowired
    private val clientRepo: ClientRepo? = null

    @Autowired
    private val accountRepo: AccountRepo? = null

    @GetMapping(path = ["/company"])
    fun showBlanc(): String = "company"

    @PostMapping(path = ["/company"])
    fun uploadingCompanyReport(
        @RequestParam companyReportFolder: List<MultipartFile>,
        model: MutableMap<String, Any>
    ): String {
        updateDb(companyReportFolder)
        model["companyReportFolder"] = "company"
        return "company"
    }

    private fun updateDb(folderName: List<MultipartFile>) {

        for (file in folderName) {

            val timeStamp = parser.parseNameToTokenAndTimeStamp(file.originalFilename!!).getValue("dateStamp")
            val branch = parser.parseNameToTokenAndTimeStamp(file.originalFilename!!).getValue("token")
            val reportFile = parser.parseCompanyReport(file.originalFilename!!)

            for (i in 0..reportFile.lastIndex) {
                val reportStringHashCode: Int = ("${reportFile[i]}${timeStamp}").hashCode()
                val dbEntityHashCode = financeRepository!!.findAllByHash(reportStringHashCode)

                if (dbEntityHashCode.isEmpty()) {
                    val reportRow = reportFile[i]
                    if (clientRepo!!.findAllByClientId(reportRow[1].toInt()).isEmpty()) {

                        createAllTablesEntity(reportRow, reportStringHashCode, timeStamp, branch)

                    } else {
                        val client = clientRepo.findAllByClientId(reportRow[1].toInt())
                        if (accountRepo!!.findAllByAccount(reportRow[3]).isEmpty()) {
                            createAccountAndFinancesTableEntity(
                                reportRow,
                                client[0].id,
                                reportStringHashCode,
                                timeStamp,
                            )
                        } else {
                            val account = accountRepo.findAllByAccount(reportRow[3])
                            createFinanceTableEntity(reportRow, account[0].id, reportStringHashCode, timeStamp)
                        }
                    }
                }
            }
        }
//            files.add(Folder(file.originalFilename))
    }

    private fun createFinanceTableEntity(
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
        createFinanceTableEntity(reportRow, account.id, reportStringHashCode, timeStamp)
    }

    private fun createAllTablesEntity(
        reportRow: List<String>,
        reportStringHashCode: Int,
        timeStamp: String,
        branch: String
    ) {
        val client = clientBuilder(reportRow, branch)
        clientRepo!!.save(client)
        val account = accountBuilder(reportRow, client.id)
        accountRepo!!.save(account)
        createFinanceTableEntity(reportRow, account.id, reportStringHashCode, timeStamp)
    }


    private fun clientBuilder(reportFile: List<String>, branch: String): Clients =
        Clients(
            id = 0,
            client = reportFile[1].toInt(),
            name = reportFile[2],
            city = branch
        )

    private fun accountBuilder(reportFile: List<String>, clientId: Int): Accounts =
        Accounts(
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
            deposit = convertToFloat(reportFile[8]),
            netto =  convertToFloat(reportFile[9]),
            bonusPIPS = reportFile[10],
            IbPayment = reportFile[11],
            hash = hashCode,
            reportingPeriod = reportDate
        )

    private fun convertToFloat(s: String): Float {
//        val decFormat = DecimalFormat("#.##")
        val replaceSpace = s.replace(" ","")
        val replaceComma = replaceSpace.replace(",", ".")
        return replaceComma.toFloat()
    }
}

