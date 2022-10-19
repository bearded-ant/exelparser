package com.akopyan.exelparser.domain.database

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository


interface ReportsRepo : CrudRepository<Reports, Int> {

    @Query(
        "select " +
                "fin.reporting_period_id " +
                "from " +
                "FINANCES fin ",
        nativeQuery = true
    )
    fun getReportPeriod(): List<String>

    @Query(
        "SELECT " +
                "t.CLIENT, " +
                "stable.name, " +
                "stable.netto, " +
                "e.TOKEN, " +
                "stable.city " +
        "FROM " +
                "EMPLOYEES e " +
        "JOIN TREATMENTS t ON " +
                "e.ID = t.EMPLOYEE_ID " +
        "JOIN ( " +
                "SELECT " +
                   "cj.client, " +
                    "cj.name, " +
                    "cj.city, " +
                    "SUM(fj.NETTO) AS netto " +
                "FROM " +
                    "CLIENTS cj, " +
                    "ACCOUNTS aj , " +
                    "FINANCES fj " +
                "WHERE " +
                    "cj.ID = aj.CLIENT_ID " +
                    "AND aj.ID = fj.ACCOUNT_ID " +
                    "AND fj.REPORTING_PERIOD_ID = :reportingPeriodId " +
                "GROUP BY " +
                    "cj.CLIENT) AS stable " +
        "ON " +
                "stable.client = t.CLIENT " +
        "WHERE " +
                "t.REPORTING_PERIOD_ID = :reportingPeriodId ",
        nativeQuery = true
    )
    fun generateReportForReportingPeriodId(reportingPeriodId: Long): List<Reports>
}