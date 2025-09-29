package com.tencent.tcmpp.demo.utils;

import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class XmlConverter {

    public static String mapToXml(Map<String, String> map, String bodyTag) {
        XmlSerializer xmlSerializer = Xml.newSerializer();
        StringWriter stringWriter = new StringWriter();

        try {
            xmlSerializer.setOutput(stringWriter);
            xmlSerializer.startDocument("UTF-8", true);
            xmlSerializer.startTag("", bodyTag);
            if (map != null && !map.keySet().isEmpty()) {
                for (String key : map.keySet()) {
                    xmlSerializer.startTag("", key);
                    xmlSerializer.text(map.get(key));
                    xmlSerializer.endTag("", key);
                }
            }
            xmlSerializer.endTag("", bodyTag);
            xmlSerializer.endDocument();
        } catch (Exception e) {
            Log.e("PaymentUtil", "exception " + e.getMessage());
        }
        return stringWriter.toString();
    }

    public static Map<String, String> xmlStringToMap(String xmlString) throws XmlPullParserException, IOException {
        Map<String, String> resultMap = new HashMap<>();
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(new StringReader(xmlString));
        String startTag = "";

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    startTag = parser.getName();
                    resultMap.put(startTag, "");
                    break;
                case XmlPullParser.TEXT:
                    String text = parser.getText();
                    if (!text.trim().isEmpty() && !TextUtils.isEmpty(startTag)) {
                        resultMap.put(startTag, parser.getText());
                    }
                    break;
                case XmlPullParser.END_TAG:
                    // No action needed for END_TAG
                    if (startTag.equals(parser.getName())) {
                        startTag = "";
                    }
                    break;
            }
            eventType = parser.next();
        }

        return resultMap;
    }


}
