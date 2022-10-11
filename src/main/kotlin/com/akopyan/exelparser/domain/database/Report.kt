package com.akopyan.exelparser.domain.database

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class Report(
    @Id
    val id: Int = 0,
    val client: Int = 0,
    val name: String = "",
    val netto: String = "",
    val token: String = "",
    val city: String = "",
    val reportingPeriod: String =""
)