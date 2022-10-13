package com.akopyan.exelparser.domain.database

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository


interface ReportRepo : CrudRepository<Reports, Int> {

    @Query(
        "select " +
                "fin.reporting_period " +
                "from " +
                "FINANCES fin ",
        nativeQuery = true
    )
    fun getReportPeriod(): List<String>

    @Query(
        "select " +
                "tre.reporting_period, " +
                "emp.token, " +
                "tre.client, " +
                "cli.name, " +
                "cli.city, " +
                "fin.netto " +
                "from " +
                "EMPLOYEE emp, " +
                "TREATMENT tre, " +
                "CLIENT cli, " +
                "FINANCES fin, " +
                "ACCOUNT acc " +
                "where emp.id = tre.token_id " +
                "and cli.client_id = tre.client " +
                "and acc.client_id = cli.id " +
                "and fin.ACCOUNT_ID = acc.id " +
                "and tre.reporting_period = :reportingPeriod " +
                "and tre.reporting_period = fin.reporting_period",
        nativeQuery = true
    )
    fun generateReportForReportingPeriod(reportingPeriod: String): List<Reports>
}