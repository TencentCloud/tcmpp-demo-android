package com.tencent.tcmpp.demo.utils;

import android.content.Context;


import com.tencent.tcmpp.demo.R;
import com.tencent.tcmpp.demo.sp.impl.CommonSp;

import java.util.Locale;


public class LocalUtil {
    public static final Locale[] SUPPORTED_LOCALES = {
            Locale.SIMPLIFIED_CHINESE,
            Locale.US,
            Locale.FRANCE,
            new Locale("id","ID")
    };

    public static String nextLocale(Context context) {
        int nextIndex = (CommonSp.getInstance().getMiniLanguage() + 1) % SUPPORTED_LOCALES.length;
        CommonSp.getInstance().putMiniLanguage(nextIndex);

        return SUPPORTED_LOCALES[nextIndex].toString();
    }

    public static Locale current() {
        int index = CommonSp.getInstance().getMiniLanguage() % SUPPORTED_LOCALES.length;
        return (Locale) (SUPPORTED_LOCALES[index] == null
                ? Locale.getDefault()
                : SUPPORTED_LOCALES[index]).clone();
    }

    public static void setCurrentLocale(String tag) {
        for (int i = 0; i < SUPPORTED_LOCALES.length; i++) {
            if (SUPPORTED_LOCALES[i].toLanguageTag().equals(tag)) {
                CommonSp.getInstance().putMiniLanguage(i);
                break;
            }
        }
    }
}
