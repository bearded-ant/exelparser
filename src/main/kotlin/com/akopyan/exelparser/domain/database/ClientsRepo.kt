package com.akopyan.exelparser.domain.database

import org.springframework.data.repository.CrudRepository


interface ClientsRepo : CrudRepository<Clients, Int> {
    fun findAllByClient(client: Int): List<Clients>
}