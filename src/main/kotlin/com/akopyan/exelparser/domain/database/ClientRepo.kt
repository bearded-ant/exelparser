package com.akopyan.exelparser.domain.database

import org.springframework.data.repository.CrudRepository


interface ClientRepo : CrudRepository<Client, Int>