package com.akopyan.exelparser.domain

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Client(
    val report_date: String,
    @Id
    val clientId: Int = 0,
    val city: String = "",
    val account: Int = 0,
    val accountCurrency: String = "USD",
    val clearing: String,
    val floating: Float = 0F,
    val bonusRISK: Float = 0F,
    val deposit: Float = 0F,
    val netto: Float = 0F,
    val bonusPIPS: Float = 0F,
    val IbPayment: Float = 0F,
)