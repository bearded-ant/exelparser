package com.akopyan.exelparser.utils

import com.akopyan.exelparser.domain.Folder
import org.springframework.web.multipart.MultipartFile
import java.io.File

private const val BASE_PATH: String = "/home/ant/akopyan/"
//private const val BASE_PATH: String = ""

private const val EXT_ERROR = "extension file wrong"
private const val BODY_ERROR = "file name contains invalid characters"
private const val ENDING_ERROR = "there is no report period in the file name"
private const val FILE_EXISTS_ERROR = "no such file"
private const val FILE_VALID = "valid name"

class FileNameUtils {

    private val regexEnding = Regex("""\d{4}_\d{1,2}""")
    private val regexBody = Regex("""[~!@#${'$'}%^&?:*(){}<>,;'"\[\]№]""")
    private val regexpExt = Regex("""(.xlsx)$""")

    fun fileNameCheck(folderName: List<MultipartFile>): List<Folder> {
        val checkResults = mutableListOf<Folder>()
        for (file in folderName) {
            val fileName = file.originalFilename!!
            when {
                !checkFileExists(fileName) -> checkResults.add(Folder(fileName, false, FILE_EXISTS_ERROR))
                !regexpExt.containsMatchIn(fileName) -> checkResults.add(Folder(fileName, false, EXT_ERROR))
                !regexEnding.containsMatchIn(fileName) -> checkResults.add(Folder(fileName, false, ENDING_ERROR))
                regexBody.containsMatchIn(fileName) -> checkResults.add(Folder(fileName, false, BODY_ERROR))
                else -> checkResults.add(Folder(fileName, true, FILE_VALID))
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
        if (regexEnding.containsMatchIn(fileName)) {
            dateStamp = regexEnding.find(fileName)!!.value
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

    fun checkFileExists(path: String): Boolean = File("$BASE_PATH${path}").isFile
}