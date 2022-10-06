package com.akopyan.exelparser.domain.database

import org.springframework.data.repository.CrudRepository


interface TreatmentRepo : CrudRepository<Treatment, Int> {
    fun findAllByClient(clientId: Int): List<Treatment>
}