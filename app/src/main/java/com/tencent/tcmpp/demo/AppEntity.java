package com.tencent.tcmpp.demo;

import android.text.TextUtils;


public class AppEntity {

    /**
     * 正式小程序
     */
    public static final int TYPE_NORMAL = 0;
    /**
     * 调试小程序
     */
    public static final int TYPE_DEBUG = 1;
    /**
     * 预览小程序
     */
    public static final int TYPE_PREVIEW = 2;

    /**
     * 小程序id
     */
    public String appId;
    /**
     * 小程序名
     */
    public String name;
    /**
     * 小程序版本
     */
    public String version;
    /**
     * 小程序图标
     */
    public String iconUrl;
    /**
     * 小程序简介
     */
    public String appIntro;
    /**
     * 开发者企业名称
     */
    public String appDeveloper;
    /**
     * 小程序类型
     */
    public int appType;
    /**
     * 时间戳
     */
    public long time;

    public AppEntity(String appId, String name, String version, int appType, String iconUrl, String appIntro, String appDeveloper) {
        this.appId = appId;
        this.name = name;
        this.version = version;
        this.appType = appType;
        this.iconUrl = iconUrl;
        this.appIntro = appIntro;
        this.appDeveloper = appDeveloper;
        this.time = System.currentTimeMillis();
    }

    public AppEntity(String appId, String name, String version, int appType, String iconUrl) {
        this.appId = appId;
        this.name = name;
        this.version = version;
        this.appType = appType;
        this.iconUrl = iconUrl;
        this.time = System.currentTimeMillis();
    }

    public boolean isValidate() {
        return !TextUtils.isEmpty(appId) && !TextUtils.isEmpty(version);
    }
}
