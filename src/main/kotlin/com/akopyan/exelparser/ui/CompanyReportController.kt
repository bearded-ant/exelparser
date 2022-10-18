package com.akopyan.exelparser.ui

import com.akopyan.exelparser.domain.Folder
import com.akopyan.exelparser.domain.database.*
import com.akopyan.exelparser.utils.FileNameUtils
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
    private val fileNameChecker: FileNameUtils = FileNameUtils()

    @Autowired
    private val financeRepository: FinancesRepo? = null

    @Autowired
    private val clientsRepo: ClientsRepo? = null

    @Autowired
    private val accountsRepo: AccountsRepo? = null

    @Autowired
    private val reportingPeriodsRepo: ReportingPeriodsRepo? = null

    @GetMapping(path = ["/company"])
    fun showBlanc(): String = "company"

    @PostMapping(path = ["/company"])
    fun uploadingCompanyReport(
        @RequestParam companyReportFolder: List<MultipartFile>,
        model: MutableMap<String, Any>
    ): String {

        val folders = updateDb(companyReportFolder)

        model["companyReportFolder"] = folders
        return "company"
    }

    private fun updateDb(foldersList: List<MultipartFile>): List<Folder> {

        val folders = fileNameChecker.fileNameCheck(foldersList)

        if (fileNameChecker.allFilenamesOk(folders)) {

            for (folderName in folders) {

                val timeStamp =
                    fileNameChecker.parseNameToTokenAndTimeStamp(folderName.folderName).getValue("dateStamp")

                val reportPeriodId = if (reportingPeriodsRepo!!.findByReportingPeriod(timeStamp).isEmpty()) {
                    with(reportingPeriodsRepo) {
                        save(ReportingPeriods(0, timeStamp))
                        findByReportingPeriod(timeStamp)[0].id
                    }
                } else reportingPeriodsRepo.findByReportingPeriod(timeStamp)[0].id

                val branch = fileNameChecker.parseNameToTokenAndTimeStamp(folderName.folderName).getValue("token")

                val reportFile = parser.parseCompanyReport(folderName.folderName)

                for (i in 0..reportFile.lastIndex) {
                    val reportStringHashCode: Int = ("${reportFile[i]}${timeStamp}").hashCode()
                    val dbEntityHashCode = financeRepository!!.findAllByHash(reportStringHashCode)

                    if (dbEntityHashCode.isEmpty()) {
                        val reportRow = reportFile[i]
                        if (clientsRepo!!.findAllByClient(reportRow[1].toInt()).isEmpty()) {

                            createAllTablesEntity(reportRow, reportStringHashCode, reportPeriodId, branch)

                        } else {
                            val client = clientsRepo.findAllByClient(reportRow[1].toInt())
                            if (accountsRepo!!.findAllByAccount(reportRow[3]).isEmpty()) {
                                createAccountAndFinancesTableEntity(
                                    reportRow,
                                    client[0].id,
                                    reportStringHashCode,
                                    reportPeriodId,
                                )
                            } else {
                                val account = accountsRepo.findAllByAccount(reportRow[3])
                                createFinanceTableEntity(reportRow, account[0].id, reportStringHashCode, reportPeriodId)
                            }
                        }
                    }
                }
            }
        } else return folders
        return folders
    }

    private fun createFinanceTableEntity(
        reportRow: List<String>,
        accountId: Long,
        reportStringHashCode: Int,
        reportPeriodId: Long,
    ) {
        val finance = financeBuilder(reportRow, accountId, reportStringHashCode, reportPeriodId)
        financeRepository!!.save(finance)
    }

    private fun createAccountAndFinancesTableEntity(
        reportRow: List<String>,
        clientId: Long,
        reportStringHashCode: Int,
        reportPeriodId: Long,
    ) {
        val account = accountBuilder(reportRow, clientId)
        accountsRepo!!.save(account)
        createFinanceTableEntity(reportRow, account.id, reportStringHashCode, reportPeriodId)
    }

    private fun createAllTablesEntity(
        reportRow: List<String>,
        reportStringHashCode: Int,
        reportPeriodId: Long,
        branch: String
    ) {
        val client = clientBuilder(reportRow, branch)
        clientsRepo!!.save(client)
        val account = accountBuilder(reportRow, client.id)
        accountsRepo!!.save(account)
        createFinanceTableEntity(reportRow, account.id, reportStringHashCode, reportPeriodId)
    }


    private fun clientBuilder(reportFile: List<String>, branch: String): Clients =
        Clients(
            id = 0,
            client = reportFile[1].toInt(),
            name = reportFile[2],
            city = branch
        )

    private fun accountBuilder(reportFile: List<String>, clientId: Long): Accounts =
        Accounts(
            id = 0,
            clientId = clientId,
            account = reportFile[3],
            accountCurrency = reportFile[4]
        )

    private fun financeBuilder(reportFile: List<String>, accountId: Long, hashCode: Int, reportDateId: Long): Finances =
        Finances(
            id = 0,
            accountId = accountId,
            clearing = reportFile[5],
            floating = reportFile[6],
            bonusRISK = reportFile[7],
            deposit = convertToFloat(reportFile[8]),
            netto = convertToFloat(reportFile[9]),
            bonusPIPS = reportFile[10],
            IbPayment = reportFile[11],
            hash = hashCode,
            reportingPeriodId = reportDateId
        )

    private fun convertToFloat(s: String): Float {
//        val decFormat = DecimalFormat("#.##")
        val replaceSpace = s.replace(" ", "")
        val replaceComma = replaceSpace.replace(",", ".")
        return replaceComma.toFloat()
    }
}

