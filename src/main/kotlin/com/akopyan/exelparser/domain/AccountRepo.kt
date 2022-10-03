package com.akopyan.exelparser.domain

import org.springframework.data.repository.CrudRepository


interface AccountRepo : CrudRepository<Account, Int>