package com.akopyan.exelparser.domain.database

import org.springframework.data.repository.CrudRepository


interface EmployeeRepo : CrudRepository<Employees, Int> {
    fun findAllByToken(token: String): List<Employees>
}