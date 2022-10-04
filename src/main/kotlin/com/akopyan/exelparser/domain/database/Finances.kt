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
    val clientId: Int = 0,
    val report_date: String,
    val clearing: String = "",
    val floating: String = "",
    val bonusRISK: String = "",
    val deposit: String = "",
    val netto: String = "",
    val bonusPIPS: String = "",
    val IbPayment: String = "",
    val has: Int = 0,
)