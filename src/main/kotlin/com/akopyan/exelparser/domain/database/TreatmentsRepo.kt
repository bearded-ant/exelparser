package com.akopyan.exelparser.domain.database

import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.transaction.annotation.Transactional


interface TreatmentsRepo : CrudRepository<Treatments, Int> {
    fun findAllByClientAndEmployeeIdAndReportingPeriodId(
        client: Int,
        employeeId: Long,
        reportingPeriodId: Long
    ): List<Treatments>

    @Transactional
    @Modifying
    @Query("update Treatments t set t.contactDate = :contactDate where t.client = :client and t.employeeId = :employeeId")
    fun updateContactDate(contactDate: String, client: Int, employeeId: Long)

    @Query(
        "SELECT t.* " +
                "FROM TREATMENTS t " +
                "WHERE  t.client IN " +
                "(SELECT st.client " +
                "FROM " +
                "(SELECT * " +
                "FROM TREATMENTS t " +
                "WHERE t.REPORTING_PERIOD_ID = :reportingPeriodId) " +
                "AS st " +
                "GROUP BY st.client " +
                "HAVING COUNT(*) > 1) " +
                "AND t.REPORTING_PERIOD_ID = :reportingPeriodId " +
                "ORDER BY t.client",
        nativeQuery = true
    )
    fun findDub(reportingPeriodId: Long): List<Treatments>

    @Query(
        "SELECT " +
                "SUM(f.NETTO) " +
        "FROM " +
                "ACCOUNTS a, " +
                "CLIENTS c, " +
                "FINANCES f " +
        "WHERE " +
                "f.ACCOUNT_ID = a.ID " +
                "AND a.CLIENT_ID = c.ID " +
                "AND c.CLIENT =:client " +
        "GROUP BY c.CLIENT",
        nativeQuery = true
    )
    fun calculateNettoForDuplicate(client: Int): Float
}
