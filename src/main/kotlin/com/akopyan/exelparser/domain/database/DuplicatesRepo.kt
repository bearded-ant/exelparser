package com.akopyan.exelparser.domain.database

import org.springframework.data.repository.CrudRepository


interface DuplicatesRepo : CrudRepository<Duplicate, Int> {
}
