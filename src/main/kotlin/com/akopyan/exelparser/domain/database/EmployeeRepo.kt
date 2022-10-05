package com.akopyan.exelparser.domain.database

import org.springframework.data.repository.CrudRepository


interface EmployeeRepo : CrudRepository<Employee, Int> {
    fun findByClientId(clientId: Int): List<Client>
}