package com.akopyan.exelparser.domain.database

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class Finances(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Int =0,
    val accountId: Int = 0,
    val reportingPeriod: String,
    val clearing: String = "",
    val floating: String = "",
    val bonusRISK: String = "",
    val deposit: Float? = 0F,
    val netto: Float? = 0F,
    val bonusPIPS: String = "",
    val IbPayment: String = "",
    val hash: Int = 0,
)