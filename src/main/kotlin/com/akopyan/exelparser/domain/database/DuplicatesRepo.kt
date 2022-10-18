package com.akopyan.exelparser.domain.database

import org.springframework.data.repository.CrudRepository


interface DuplicatesRepo : CrudRepository<Duplicates, Int> {
    fun findAllByReportingPeriod(reportingPeriod:String): List<Duplicates>
}
