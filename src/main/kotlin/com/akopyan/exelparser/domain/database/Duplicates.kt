package com.akopyan.exelparser.domain.database

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class Duplicates(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0,
    val tokenId: Long = 0,
    val client: Int = 0,
    val contactDate: String = "",
    val reportingPeriodId: Long = 0,
    val netto: Float = 0F
)
