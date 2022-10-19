package com.akopyan.exelparser.domain.database

import org.springframework.data.repository.CrudRepository


interface ReportingPeriodsRepo : CrudRepository<ReportingPeriods, Long> {
    fun findByReportingPeriod(reportingPeriod: String): List<ReportingPeriods>
}