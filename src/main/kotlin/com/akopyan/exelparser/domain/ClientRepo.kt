package com.akopyan.exelparser.domain

import org.springframework.data.repository.CrudRepository

interface ClientRepo : CrudRepository<Client, Int> {
}