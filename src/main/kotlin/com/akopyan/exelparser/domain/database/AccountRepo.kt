package com.akopyan.exelparser.domain.database

import org.springframework.data.repository.CrudRepository


interface AccountRepo : CrudRepository<Account, Int> {
    fun findAllByAccount(account: String): List<Account>
}