package com.akopyan.exelparser.domain.database

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class Account(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Int = 0,
    val client: Int = 0,
    val account: String? = "",
    val accountCurrency: String = "USD"
)