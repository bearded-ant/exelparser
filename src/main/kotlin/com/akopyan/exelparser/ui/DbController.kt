package com.akopyan.exelparser.ui

import com.akopyan.exelparser.domain.Client
import com.akopyan.exelparser.domain.ClientRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping(path = ["/db"])

class DbController {

    @Autowired
    private val userRepository: ClientRepo? = null

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
    fun viewAll(model: MutableMap<String, Iterable<Client>>): String {
        val allUsers = userRepository!!.findAll()
        model["h2db"] = allUsers
        return "dball"
    }
}