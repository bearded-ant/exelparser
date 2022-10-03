package com.akopyan.exelparser.domain

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class Folder(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id:Int = 0,
    val folderName: String? = ""
)