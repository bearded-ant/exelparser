package com.akopyan.exelparser.ui

import com.akopyan.exelparser.ParseXMLX
import com.akopyan.exelparser.domain.database.Finances
import com.akopyan.exelparser.domain.database.FinancesRepo
import com.akopyan.exelparser.domain.Folder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@Controller
@RequestMapping(path = ["/db"])

class DbController {

    @Autowired
    private val financeRepository: FinancesRepo? = null

//    @PostMapping(path = ["/all"])
//    fun add(@RequestParam name: String?, model: MutableMap<String, Any>): String {
//        val n = H2db(0, name)
//        userRepository!!.save(n)
//
//        val allUsers = userRepository.findAll()
//        model["h2db"] = allUsers
//
//        return "dball"
//    }

    @GetMapping(path = ["/all"])
    fun viewAll(model: MutableMap<String, Iterable<Finances>>): String {
        val finances = financeRepository!!.findAll()
        model["finances"] = finances
        return "dball"
    }

    @GetMapping(path = ["/uploading"])
    fun showBlanc(): String = "uploading"


    @PostMapping(path = ["/uploading"])
    fun show(@RequestParam folderName: List<MultipartFile>, model: MutableMap<String, Any>): String {
        val files = mutableListOf<Folder>()
        for (file in folderName) {
            ParseXMLX().parse(file.originalFilename!!)
            files.add(Folder(file.originalFilename))
        }
        model["folders"] = files
        return "uploading"
    }
}