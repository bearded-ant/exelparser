package com.akopyan.exelparser.domain.database

import org.springframework.data.repository.CrudRepository


interface AccountRepo : CrudRepository<Accounts, Int> {
    fun findAllByAccount(account: String): List<Accounts>
}