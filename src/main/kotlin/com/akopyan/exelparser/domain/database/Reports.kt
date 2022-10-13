package com.akopyan.exelparser.domain.database

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Reports(
    @Id
    val client: Int = 0,
    val name: String = "",
    val netto: Float = 0F,
    val token: String = "",
    val city: String = ""
)