package com.akopyan.exelparser.domain.database

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository


interface ReportRepo : CrudRepository<Report, Int> {

    @Query("select " +
            "tre.id, " +
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
            "where emp.id = tre.token_id and cli.client_id = tre.client and acc.client_id = cli.id and fin.ACCOUNT_ID = acc.id",
        nativeQuery = true
    )
    fun generateReport(): List<Report>
}