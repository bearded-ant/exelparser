package com.akopyan.exelparser.domain.database

import org.springframework.data.repository.CrudRepository

interface FinancesRepo : CrudRepository<Finances, Int> {
    fun findAllByHash(simpleHashCode: Int): List<Finances>
}