package com.akopyan.exelparser.domain

import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<H2db, Int> {
}