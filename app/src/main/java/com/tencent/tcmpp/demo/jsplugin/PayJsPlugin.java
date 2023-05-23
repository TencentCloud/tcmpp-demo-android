package com.tencent.tcmpp.demo.jsplugin;

import com.tencent.tcmpp.demo.pay.CustomPayDemo;
import com.tencent.tmfmini.sdk.annotation.JsEvent;
import com.tencent.tmfmini.sdk.annotation.JsPlugin;
import com.tencent.tmfmini.sdk.launcher.core.model.RequestEvent;
import com.tencent.tmfmini.sdk.launcher.core.plugins.BaseJsPlugin;
import org.json.JSONObject;

@JsPlugin(secondary = true)
public class PayJsPlugin extends BaseJsPlugin {

    @JsEvent("requestPayment")
    public void requestPayment(final RequestEvent req) {
        try {
            JSONObject paramsObject = new JSONObject(req.jsonParams);
            JSONObject data = paramsObject.optJSONObject("data");
            double count = data.optDouble("money");
            if (!mIsContainer && !mIsDestroyed) {
                CustomPayDemo.requestPay(mMiniAppContext.getAttachedActivity(), count, (retCode, msg) -> {
                    if (retCode == 0) {
                        req.ok();
                    } else {
                        req.fail(msg);
                    }
                });
            }
        } catch (Throwable e) {
            req.fail("invalid params");
        }
    }
}
