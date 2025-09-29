package com.tencent.tcmpp.demo

import android.Manifest
import android.os.Build
import java.util.*

object Constants {
    const val LOG_TAG = "TCMPPDemo"
    const val TCMPP_CONFIG_FILE = "tcsas-android-configurations.json"
    const val IMEI = "test002"
    
    var COUNTRY = ""
    var PROVINCE = ""
    var CITY = ""
    
    val USER_INFO_NAME = "userInfo测试${Date()}"
    
    const val USER_INFO_AVATAR_URL = "https://gimg2.baidu.com/image_search" +
            "/src=http%3A%2F%2Fimg.daimg.com%2Fuploads%2Fallimg%2F210114%2F1-210114151951.jpg" +
            "&refer=http%3A%2F%2Fimg.daimg.com&app=2002&size=f9999," +
            "10000&q=a80&n=0&g=0n&fmt=auto?sec=1673852149&t=e2a830d9fabd7e0818059d92c3883017"
    
    /**
     * Get basic permissions supported by all API levels
     */
    fun getBasicPermissions(): Array<String> {
        return arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.VIBRATE,
            Manifest.permission.SYSTEM_ALERT_WINDOW,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_PRIVILEGED,
            "android.permission.BLUETOOTH_SCAN",
            "android.permission.BLUETOOTH_CONNECT",
            Manifest.permission.NFC,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECORD_AUDIO
        )
    }

    /**
     * Get storage permissions based on API level
     */
    fun getStoragePermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_AUDIO
            )
        } else {
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    /**
     * Get notification permissions (Android 13+)
     */
    fun getNotificationPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            emptyArray()
        }
    }

    /**
     * Get all permissions
     */
    fun getAllPermissions(): Array<String> {
        val allPerms = mutableListOf<String>()
        allPerms.addAll(getBasicPermissions())
        allPerms.addAll(getStoragePermissions())
        allPerms.addAll(getNotificationPermissions())
        return allPerms.toTypedArray()
    }

    /**
     * Maintain backward compatibility
     */
    @Deprecated("Use getAllPermissions() instead")
    val perms = getAllPermissions()
}