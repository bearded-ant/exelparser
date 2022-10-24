package com.akopyan.exelparser.domain

data class DuplicatesExelReport(
    val token: String,
    val client: Int,
    val contactDate: String,
    val reportingPeriod: String,
    val netto: Float,
    override val fieldsCount: Int = 5
) : WritableInExel