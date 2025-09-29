package com.tencent.tcmpp.demo.utils

import java.io.*

object FileUtil {
    
    private const val TAG = "FileUtil"

    fun readFileContent(inputStream: InputStream): String {
        val sbf = StringBuffer()
        var reader: BufferedReader? = null
        try {
            reader = BufferedReader(InputStreamReader(inputStream))
            var tempStr: String?
            while (reader.readLine().also { tempStr = it } != null) {
                sbf.append(tempStr)
            }
            return sbf.toString()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            reader?.let {
                try {
                    it.close()
                } catch (e1: IOException) {
                    e1.printStackTrace()
                }
            }
        }
        return sbf.toString()
    }

    fun deleteFileOrDir(path: File?): Boolean {
        if (path == null || !path.exists()) {
            return true
        }
        if (path.isFile) {
            return path.delete()
        }
        val files = path.listFiles()
        files?.forEach { file ->
            deleteFileOrDir(file)
        }
        return path.delete()
    }

    fun writeText(filePath: String, content: String, isAppend: Boolean) {
        var outputStream: FileOutputStream? = null
        var outputStreamWriter: OutputStreamWriter? = null
        var bufferedWriter: BufferedWriter? = null
        try {
            outputStream = FileOutputStream(filePath, isAppend)
            outputStreamWriter = OutputStreamWriter(outputStream)
            bufferedWriter = BufferedWriter(outputStreamWriter)
            bufferedWriter.write(content)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                bufferedWriter?.close()
                outputStreamWriter?.close()
                outputStream?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}