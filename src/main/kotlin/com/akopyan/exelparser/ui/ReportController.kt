package com.akopyan.exelparser.ui

import com.akopyan.exelparser.domain.database.ReportRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping()
class ReportController {

    @Autowired
    private val reportRepo: ReportRepo? = null

    @GetMapping(path = ["/report"])
    fun showBlanc(model: MutableMap<String, Any>): String {

        val report = reportRepo!!.generateReport()
        model["reports"] = report
        return "report"
    }

}