package com.tencent.tcmpp.demo.jsplugin;

import com.tencent.tcmpp.demo.Constants;
import com.tencent.tmfmini.sdk.annotation.JsEvent;
import com.tencent.tmfmini.sdk.annotation.JsPlugin;
import com.tencent.tmfmini.sdk.launcher.core.model.RequestEvent;
import com.tencent.tmfmini.sdk.launcher.core.plugins.BaseJsPlugin;
import org.json.JSONException;
import org.json.JSONObject;

@JsPlugin(secondary = true)
public class WxApiPlugin extends BaseJsPlugin {

    @JsEvent("login")
    public void login(final RequestEvent req) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("key", "wx.login" + mMiniAppInfo.appId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        req.ok(jsonObject);
    }

    @JsEvent("getUserInfo")
    public void getUserInfo(final RequestEvent req) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("key", "wx.getUserInfo");
            JSONObject userInfo = new JSONObject();
            //返回昵称
            userInfo.put("nickName", Constants.USER_INFO_NAME);
            //返回头像url
            userInfo.put("avatarUrl", Constants.USER_INFO_AVATAR_URL);
            jsonObject.put("userInfo", userInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        req.ok(jsonObject);
    }

    @JsEvent("getUserProfile")
    public void getUserProfile(final RequestEvent req) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("key", "wx.getUserProfile");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        req.ok(jsonObject);
    }
}
