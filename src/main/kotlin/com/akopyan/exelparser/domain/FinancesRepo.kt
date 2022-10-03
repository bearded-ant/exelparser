package com.akopyan.exelparser.domain

import org.springframework.data.repository.CrudRepository

interface FinancesRepo : CrudRepository<Finances, Int> {
}