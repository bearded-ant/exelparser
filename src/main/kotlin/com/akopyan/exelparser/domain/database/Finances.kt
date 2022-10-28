package com.akopyan.exelparser.domain.database

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class Finances(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0,
    val accountId: Long = 0,
    val reportingPeriodId: Long = 0,
    val hash: Int = 0,

    val calculationScheme: String = "",
    val clearing: Float? = 0F,
    val floating: Float? = 0F,
    val bonusRISK: Float? = 0F,
    val deposit: Float? = 0F,
    val withdrawal: Float = 0F,
    val netto: Float? = 0F,
    val bonusPIPS: Float? = 0F,
    val IbPayment: Float? = 0F,
)