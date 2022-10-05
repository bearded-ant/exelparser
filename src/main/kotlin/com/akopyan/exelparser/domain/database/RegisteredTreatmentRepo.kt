package com.akopyan.exelparser.domain.database

import org.springframework.data.repository.CrudRepository


interface RegisteredTreatmentRepo : CrudRepository<RegisteredTreatment, Int> {
    fun findAllByClient(clientId: Int): List<RegisteredTreatment>
}