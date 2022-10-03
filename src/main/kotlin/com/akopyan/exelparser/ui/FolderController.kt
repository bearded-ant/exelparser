package com.akopyan.exelparser.ui

import com.akopyan.exelparser.ParseXMLX
import com.akopyan.exelparser.domain.Folder
import com.akopyan.exelparser.domain.FolderRepo

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile


@Controller
class FolderController {
    @Autowired
    private val folderRepo: FolderRepo? = null

    @GetMapping(path = ["/uploading"])
    fun showBlanc(): String = "uploading"


    @PostMapping(path = ["/uploading"])
    fun show(@RequestParam folderName: List<MultipartFile>, model: MutableMap<String, Any>): String {
        val files = mutableListOf<Folder>()
        for (file in folderName) {
            ParseXMLX().parse(file.originalFilename!!)
            files.add(Folder(0, file.originalFilename))
        }
        model["folders"] = files
        return "uploading"
    }
}