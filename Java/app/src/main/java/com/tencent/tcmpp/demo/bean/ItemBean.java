package com.tencent.tcmpp.demo.bean;

import com.tencent.tmf.mini.api.bean.MiniApp;

public class ItemBean {
    public static final int ITEM_TYPE_VERT = 0;
    public static final int ITEM_TYPE_HORZ = 1;
    public static final int ITEM_TYPE_GROUP = 2;
    public static final int ITEM_TYPE_EMPTY = 3;
    public static final int ITEM_TYPE_GROUP2 = 4;

    public final int mType;
    public final MiniApp mAppInfo;
    public final String mTitle;

    public ItemBean(int type, String title, MiniApp app) {
        this.mType = type;
        this.mTitle = title;
        this.mAppInfo = app;
    }
}
