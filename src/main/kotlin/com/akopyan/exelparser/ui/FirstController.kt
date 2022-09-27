package com.akopyan.exelparser.ui

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class FirstController {

    @GetMapping("/greeting")
    fun greeting(
        @RequestParam(name = "name", required = false, defaultValue = "World")
        name: String,
        model: Model
    ): String {
        model.addAttribute("name", name)
        return "greeting"
    }

}
