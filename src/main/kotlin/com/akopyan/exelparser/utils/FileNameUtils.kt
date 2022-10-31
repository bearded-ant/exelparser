package com.akopyan.exelparser.utils

import com.akopyan.exelparser.domain.Folder
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.lang.StringBuilder

class FileNameUtils {
    private val baseValues: BaseValues = BaseValues()

    private val regexReportPeriod = Regex("""\d{4}_\d{1,2}\.""")
    private val regexLong = Regex("""(\d{4}_\d)$""")

    private val regexBody = Regex("""[~!@#${'$'}%^&?:*(){}<>,;'"\[\]№]""")
    private val regexpExt = Regex("""(.xlsx)$""")

    fun fileNameCheck(folderName: List<MultipartFile>): List<Folder> {
        val checkResults = mutableListOf<Folder>()
        for (file in folderName) {
            val fileName = file.originalFilename!!
            when {
                !checkFileExists(fileName) ->
                    checkResults.add(Folder(fileName, false, baseValues.FILE_EXISTS_ERROR))

                !regexpExt.containsMatchIn(fileName) ->
                    checkResults.add(Folder(fileName, false, baseValues.EXT_ERROR))

                !regexReportPeriod.containsMatchIn(fileName) ->
                    checkResults.add(Folder(fileName, false, baseValues.ENDING_ERROR))

                regexBody.containsMatchIn(fileName) ->
                    checkResults.add(Folder(fileName, false, baseValues.BODY_ERROR))

                else -> checkResults.add(Folder(fileName, true, baseValues.FILE_VALID))
            }
        }
        return checkResults
    }

    fun allFilenamesOk(folders: List<Folder>): Boolean {
        var result = true
        for (folder in folders) {
            result = result && folder.status
        }
        return result
    }

    fun parseNameToTokenAndTimeStamp(fileName: String): Map<String, String> {
        var dateStamp = StringBuilder()
        var token = ""
        var endingLength = 0
        if (regexReportPeriod.containsMatchIn(fileName)) {
            dateStamp.append(regexReportPeriod.find(fileName)!!.value)
            dateStamp.deleteCharAt(dateStamp.lastIndex)

            if (regexLong.containsMatchIn(dateStamp)) {
                dateStamp.insert(5, '0')
                endingLength = dateStamp.length + 5
            } else endingLength = dateStamp.length + 6

            val nameLength = fileName.length

            val slashIndex =
                if (fileName.lastIndexOf('\\') == -1) fileName.lastIndexOf('/') else fileName.lastIndexOf('\\')
            token = fileName.substring(slashIndex + 1, (nameLength - endingLength))
        }
        val result = mutableMapOf<String, String>()
        result["dateStamp"] = dateStamp.toString()
        result["token"] = token
        return result
    }

    private fun checkFileExists(path: String): Boolean = File("${baseValues.BASE_PATH}${path}").isFile
}