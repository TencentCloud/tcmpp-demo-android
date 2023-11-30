package com.tencent.tcmpp.demo;

import android.Manifest;
import android.Manifest.permission;
import java.util.Date;

public class Constants {

    public static final String LOG_TAG = "TCMPPDemo";

    public static final String TCMPP_CONFIG_FILE = "tcmpp-android-configurations.json";

    public static final String IMEI = "test002";

    public static  String COUNTRY = "";
    public static  String PROVINCE = "";
    public static  String CITY = "";

    public static String USER_INFO_NAME = "userInfo测试" + new Date();

    public static String USER_INFO_AVATAR_URL = "https://gimg2.baidu.com/image_search"
            + "/src=http%3A%2F%2Fimg.daimg.com%2Fuploads%2Fallimg%2F210114%2F1-210114151951.jpg"
            + "&refer=http%3A%2F%2Fimg.daimg.com&app=2002&size=f9999,"
            + "10000&q=a80&n=0&g=0n&fmt=auto?sec=1673852149&t=e2a830d9fabd7e0818059d92c3883017";

    public static String[] perms = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.VIBRATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.SYSTEM_ALERT_WINDOW,
            //蓝牙
            permission.BLUETOOTH,
            permission.BLUETOOTH_ADMIN,
            permission.BLUETOOTH_ADVERTISE,
            permission.BLUETOOTH_SCAN,
            permission.BLUETOOTH_PRIVILEGED,
            permission.ACCESS_COARSE_LOCATION,
            "android.permission.BLUETOOTH_SCAN",
            "android.permission.BLUETOOTH_CONNECT",
            //nfc
            permission.NFC,
            //日历
            permission.READ_CALENDAR,
            permission.WRITE_CONTACTS,
            //联系人
            permission.READ_CONTACTS,
            permission.WRITE_CONTACTS,
            //短信
            permission.SEND_SMS,
            permission.READ_SMS,
            permission.RECORD_AUDIO
    };
}
