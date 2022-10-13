package com.akopyan.exelparser.domain.database

import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.transaction.annotation.Transactional


interface TreatmentsRepo : CrudRepository<Treatments, Int> {
    fun findAllByClientAndTokenIdAndReportingPeriod(
        clientId: Int,
        tokenId: Int,
        reportingPeriod: String
    ): List<Treatments>

    @Transactional
    @Modifying
    @Query("update Treatments t set t.contactDate = :contactDate where t.client = :client and t.tokenId = :tokenId")
    fun updateContactDate(contactDate: String, client: Int, tokenId: Int)

    @Query(
        "SELECT st.* " +
                "FROM Treatments st " +
                "WHERE st.client IN  " +
                "(SELECT st.client FROM Treatments st GROUP BY st.client HAVING COUNT(*) > 1)  " +
                "ORDER BY st.client", nativeQuery = true
    )
    fun findDub(): List<Treatments>
}
