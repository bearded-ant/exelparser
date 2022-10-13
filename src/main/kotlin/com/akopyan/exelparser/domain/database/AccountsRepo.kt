package com.akopyan.exelparser.domain.database

import org.springframework.data.repository.CrudRepository


interface AccountsRepo : CrudRepository<Accounts, Int> {
    fun findAllByAccount(account: String): List<Accounts>
}