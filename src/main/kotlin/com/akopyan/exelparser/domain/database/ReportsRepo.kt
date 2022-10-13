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
        "SELECT " +
                "e.TOKEN, " +
                "t.CLIENT, " +
                "c.NAME, " +
                "c.CITY, " +
                "SUM(f.NETTO) AS NETTO " +
        "FROM " +
                "EMPLOYEES e, " +
                "TREATMENTS t " +
                "JOIN CLIENTS c ON c.CLIENT = t.CLIENT " +
                "JOIN ACCOUNTS a ON a.CLIENT_ID = c.ID " +
                "JOIN FINANCES f ON f.ACCOUNT_ID = a.ID " +
        "WHERE " +
                "e.ID = t.TOKEN_ID " +
                "AND t.REPORTING_PERIOD = :reportingPeriod " +
        "GROUP BY e.TOKEN , c.CLIENT, c.NAME, c.CITY",
        nativeQuery = true
    )
    fun generateReportForReportingPeriod(reportingPeriod: String): List<Reports>
}