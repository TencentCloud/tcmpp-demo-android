package com.tencent.tcmpp.demo.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;

import com.tencent.tcmpp.demo.R;

import java.util.Locale;

public class DynamicLanguageUtil {
    public static void setAppLanguage(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
        Log.e("TAG","get test "+resources.getString(R.string.applet_system_language));
    }
}
