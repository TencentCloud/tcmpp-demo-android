package com.tencent.tcmpp.demo;

import android.Manifest;
import android.Manifest.permission;
import java.util.Date;

public class Constants {

    public static final String LOG_TAG = "TCMPPDemo";
    


    public static final String TCMPP_CONFIG_FILE = "tcsas-android-configurations.json";

    public static final String IMEI = "test002";

    public static  String COUNTRY = "";
    public static  String PROVINCE = "";
    public static  String CITY = "";

    public static String USER_INFO_NAME = "userInfo测试" + new Date();

    public static String USER_INFO_AVATAR_URL = "https://gimg2.baidu.com/image_search"
            + "/src=http%3A%2F%2Fimg.daimg.com%2Fuploads%2Fallimg%2F210114%2F1-210114151951.jpg"
            + "&refer=http%3A%2F%2Fimg.daimg.com&app=2002&size=f9999,"
            + "10000&q=a80&n=0&g=0n&fmt=auto?sec=1673852149&t=e2a830d9fabd7e0818059d92c3883017";

    /**
     * Get basic permissions supported by all API levels
     */
    public static String[] getBasicPermissions() {
        java.util.List<String> permissions = new java.util.ArrayList<>();
        
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissions.add(Manifest.permission.READ_PHONE_STATE);
        permissions.add(Manifest.permission.VIBRATE);
        
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            permissions.add(Manifest.permission.SYSTEM_ALERT_WINDOW);
        }
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            permissions.add(permission.BLUETOOTH_ADVERTISE);
            permissions.add(permission.BLUETOOTH_SCAN);
            permissions.add(permission.BLUETOOTH_CONNECT);
        } else {
            permissions.add(permission.BLUETOOTH);
            permissions.add(permission.BLUETOOTH_ADMIN);
        }
        
        permissions.add(permission.NFC);
        permissions.add(permission.READ_CALENDAR);
        permissions.add(permission.WRITE_CALENDAR);
        permissions.add(permission.READ_CONTACTS);
        permissions.add(permission.WRITE_CONTACTS);
        permissions.add(permission.SEND_SMS);
        permissions.add(permission.READ_SMS);
        permissions.add(permission.RECORD_AUDIO);
        
        return permissions.toArray(new String[0]);
    }

    /**
     * Get storage permissions based on API level
     */
    public static String[] getStoragePermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            return new String[]{
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_AUDIO
            };
        } else {
            return new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };
        }
    }

    /**
     * Get notification permissions (Android 13+)
     */
    public static String[] getNotificationPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            return new String[]{
                    Manifest.permission.POST_NOTIFICATIONS
            };
        }
        return new String[]{};
    }

    /**
     * Get all permissions
     */
    public static String[] getAllPermissions() {
        java.util.List<String> allPerms = new java.util.ArrayList<>();
        java.util.Collections.addAll(allPerms, getBasicPermissions());
        java.util.Collections.addAll(allPerms, getStoragePermissions());
        java.util.Collections.addAll(allPerms, getNotificationPermissions());
        return allPerms.toArray(new String[0]);
    }

    /**
     * Maintain backward compatibility
     */
    @Deprecated
    public static String[] perms = getAllPermissions();
}
