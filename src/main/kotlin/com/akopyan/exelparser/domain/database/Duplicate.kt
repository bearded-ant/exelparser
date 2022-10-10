package com.akopyan.exelparser.domain.database

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Duplicate(
    @Id
    val id: Int = 0,
    val tokenId: Int = 0,
    val client: Int = 0,
    val contactDate: String = "",
    val reportingPeriod: String = ""
)
