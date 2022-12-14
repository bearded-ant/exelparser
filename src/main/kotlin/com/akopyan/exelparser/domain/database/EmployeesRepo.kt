package com.akopyan.exelparser.domain.database

import org.springframework.data.repository.CrudRepository


interface EmployeesRepo : CrudRepository<Employees, Long> {
    fun findAllByToken(token: String): List<Employees>
}