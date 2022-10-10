package com.akopyan.exelparser.domain.database

import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.transaction.annotation.Transactional


interface TreatmentRepo : CrudRepository<Treatment, Int> {
    fun findAllByClientAndTokenId(clientId: Int, tokenId: Int): List<Treatment>

    @Transactional
    @Modifying
    @Query("update Treatment t set t.contactDate = :contactDate where t.client = :client and t.tokenId = :tokenId")
    fun updateContactDate(contactDate: String, client: Int, tokenId: Int)


    @Query("SELECT st.* " +
            "FROM Treatment st " +
            "WHERE st.client IN  " +
            "(SELECT st.client FROM Treatment st GROUP BY st.client HAVING COUNT(*) > 1)  " +
            "ORDER BY st.client", nativeQuery = true)
    fun findDub(): List<Treatment>

//    @Query("SELECT * FROM Treatment st WHERE st.client IN " +
//            "(SELECT st.client FROM Treatment st GROUP BY st.client HAVING COUNT(*) > 1) " +
//            "ORDER BY st.client")
//    fun findAllByClient(clientId: Int): List<Treatment>
}
