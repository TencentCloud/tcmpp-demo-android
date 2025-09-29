package com.tencent.tcmpp.demo.utils

import android.text.TextUtils
import android.util.Log
import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.StringReader
import java.io.StringWriter

object XmlConverter {

    @JvmStatic
    fun mapToXml(map: Map<String, String>?, bodyTag: String): String {
        val xmlSerializer = Xml.newSerializer()
        val stringWriter = StringWriter()

        try {
            xmlSerializer.setOutput(stringWriter)
            xmlSerializer.startDocument("UTF-8", true)
            xmlSerializer.startTag("", bodyTag)
            if (map != null && map.keys.isNotEmpty()) {
                for (key in map.keys) {
                    xmlSerializer.startTag("", key)
                    xmlSerializer.text(map[key])
                    xmlSerializer.endTag("", key)
                }
            }
            xmlSerializer.endTag("", bodyTag)
            xmlSerializer.endDocument()
        } catch (e: Exception) {
            Log.e("PaymentUtil", "exception ${e.message}")
        }
        return stringWriter.toString()
    }

    @JvmStatic
    @Throws(XmlPullParserException::class, IOException::class)
    fun xmlStringToMap(xmlString: String): Map<String, String> {
        val resultMap = mutableMapOf<String, String>()
        val parser = Xml.newPullParser()
        parser.setInput(StringReader(xmlString))
        var startTag = ""

        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    startTag = parser.name
                    resultMap[startTag] = ""
                }
                XmlPullParser.TEXT -> {
                    val text = parser.text
                    if (text.trim().isNotEmpty() && !TextUtils.isEmpty(startTag)) {
                        resultMap[startTag] = parser.text
                    }
                }
                XmlPullParser.END_TAG -> {
                    // No action needed for END_TAG
                    if (startTag == parser.name) {
                        startTag = ""
                    }
                }
            }
            eventType = parser.next()
        }

        return resultMap
    }
}