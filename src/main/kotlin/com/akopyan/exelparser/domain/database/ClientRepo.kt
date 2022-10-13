package com.akopyan.exelparser.domain.database

import org.springframework.data.repository.CrudRepository


interface ClientRepo : CrudRepository<Clients, Int> {
    fun findAllByClientId(clientId: Int): List<Clients>
}