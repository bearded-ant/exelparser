package com.akopyan.exelparser.utils

import com.akopyan.exelparser.domain.Folder
import org.springframework.web.multipart.MultipartFile
import java.io.File

class FileNameUtils {
    private val baseValues: BaseValues = BaseValues()

    private val regexReportPeriod = Regex("""\d{4}_\d{1,2}\.""")
    val regexLong = Regex("""\d{4}_0\d\.""")

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
        var dateStamp = ""
        var token = ""
        if (regexReportPeriod.containsMatchIn(fileName)) {
            val stampWithDot = regexReportPeriod.find(fileName)!!.value

            dateStamp = delNullAndDot(stampWithDot)

            val nameLength = fileName.length
            val endingLength = dateStamp.length + 6
            val slashIndex =
                if (fileName.lastIndexOf('\\') == -1) fileName.lastIndexOf('/') else fileName.lastIndexOf('\\')
            token = fileName.substring(slashIndex + 1, (nameLength - endingLength))
        }
        val result = mutableMapOf<String, String>()
        result["dateStamp"] = dateStamp
        result["token"] = token
        return result
    }

    private fun delNullAndDot(badString: String): String {
        if (regexLong.containsMatchIn(badString)) {
            return badString.removeRange(5, 6).substring(0, badString.length - 2)
        }
        return badString.substring(0, badString.length - 1)
    }

    private fun checkFileExists(path: String): Boolean = File("${baseValues.BASE_PATH}${path}").isFile
}