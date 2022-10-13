package com.akopyan.exelparser.domain.database

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository


interface ReportsRepo : CrudRepository<Reports, Int> {

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
                "EMPLOYEES emp, " +
                "TREATMENTS tre, " +
                "CLIENTS cli, " +
                "FINANCES fin, " +
                "ACCOUNTS acc " +
                "where emp.id = tre.token_id " +
                "and cli.client = tre.client " +
                "and acc.client_id = cli.id " +
                "and fin.ACCOUNT_ID = acc.id " +
                "and tre.reporting_period = :reportingPeriod " +
                "and tre.reporting_period = fin.reporting_period",
        nativeQuery = true
    )
    fun generateReportForReportingPeriod(reportingPeriod: String): List<Reports>
}