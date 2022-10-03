package com.akopyan.exelparser.domain

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
    val clearing: Float = 0F,
    val floating: Float = 0F,
    val bonusRISK: Float = 0F,
    val deposit: Float = 0F,
    val netto: Float = 0F,
    val bonusPIPS: Float = 0F,
    val IbPayment: Float = 0F,
)