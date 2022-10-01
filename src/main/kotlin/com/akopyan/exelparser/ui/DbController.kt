package com.akopyan.exelparser.ui

import com.akopyan.exelparser.domain.H2db
import com.akopyan.exelparser.domain.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping(path = ["/demo"])

class DbController {

    @Autowired
    private val userRepository: UserRepository? = null

    @GetMapping(path = ["/add"]) // Map ONLY POST Requests
    @ResponseBody
    fun addNewUser(
        @RequestParam name: String?
    ): String {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request
        val n = H2db(0, name = name)
        userRepository!!.save<H2db>(n)
        return "greeting"
    }

//    // This returns a JSON or XML with the users
//    @get:ResponseBody
//    @get:GetMapping(path = ["/all"])
//    val allUsers: Iterable<Any>
//        get() = userRepository!!.findAll()

    @GetMapping(path = ["/all"])
    fun dball(model: MutableMap<String, Any>): String {
        val allUsers = userRepository!!.findAll()
        model.put("h2db", allUsers)
        return "dball"
    }

}