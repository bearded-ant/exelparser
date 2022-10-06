package com.akopyan.exelparser.domain.database

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class Treatment(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Int = 0,
    val tokenId: Int = 0,
    val client: Int = 0,
    val contactDate:String = "",
    val reportingPeriod: String = ""
)
