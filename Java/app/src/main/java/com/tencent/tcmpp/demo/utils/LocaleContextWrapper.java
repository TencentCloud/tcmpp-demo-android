package com.tencent.tcmpp.demo.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.view.LayoutInflater;

import java.util.Locale;

public class LocaleContextWrapper extends ContextWrapper {

    private final Configuration mOverrideConfig;
    private Resources mResources = null;
    private LayoutInflater mInflater;

    LocaleContextWrapper(Context base, Configuration overrideConfig) {
        super(base);
        mOverrideConfig = overrideConfig;
    }

    public static ContextWrapper create(Context base) {
        return create(base, null);
    }

    public static ContextWrapper create(Context base, Locale locale) {
        Configuration baseConfig = base.getResources().getConfiguration();
        Configuration overrideConfig = new Configuration();

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            setConfigLocale(overrideConfig, locale != null ? locale : getConfigLocale(baseConfig));
        } else {
            setConfigLocaleLegacy(overrideConfig, locale != null ? locale : getConfigLocaleLegacy(baseConfig));
        }
        return new LocaleContextWrapper(base, overrideConfig);
    }

    /**
     * update locale of current ContextWrapper
     *
     * @param locale target locale
     * @return is updated
     */
    public boolean updateLocale(Locale locale) {
        if (locale != null) {
            Locale currentLocale = null;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                currentLocale = getConfigLocale(mOverrideConfig);
            } else {
                currentLocale = getConfigLocaleLegacy(mOverrideConfig);
            }
            if (!currentLocale.getLanguage().equals(locale.getLanguage())) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    setConfigLocale(mOverrideConfig, locale);
                    if (mResources != null) {
                        Configuration resConfig = mResources.getConfiguration();
                        setConfigLocale(resConfig, locale);
                        mResources.updateConfiguration(resConfig, mResources.getDisplayMetrics());
                    }
                } else {
                    setConfigLocaleLegacy(mOverrideConfig, locale);
                    if (mResources != null) {
                        Configuration resConfig = mResources.getConfiguration();
                        setConfigLocaleLegacy(resConfig, locale);
                        mResources.updateConfiguration(resConfig, mResources.getDisplayMetrics());
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public AssetManager getAssets() {
        return getResourcesInternal().getAssets();
    }

    @Override
    public Resources getResources() {
        return getResourcesInternal();
    }

    private Resources getResourcesInternal() {
        if (mResources == null) {
            final Context resContext = createConfigurationContext(mOverrideConfig != null ? mOverrideConfig :
                    new Configuration());
            mResources = resContext.getResources();
            mResources.updateConfiguration(mOverrideConfig, mResources.getDisplayMetrics());
        } else {
            // FIXME: 2024/5/30 why resource's language differs from override config? Is it changed by something?
            Locale currentLocale;
            Locale targetLocale;
            Configuration resConfig = mResources.getConfiguration();
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                currentLocale = getConfigLocale(resConfig);
                targetLocale = getConfigLocale(mOverrideConfig);
            } else {
                currentLocale = getConfigLocaleLegacy(resConfig);
                targetLocale = getConfigLocaleLegacy(mOverrideConfig);
            }
            if (!currentLocale.getLanguage().equals(targetLocale.getLanguage())) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    setConfigLocale(resConfig, targetLocale);
                } else {
                    setConfigLocaleLegacy(resConfig, targetLocale);
                }
                mResources.updateConfiguration(resConfig, mResources.getDisplayMetrics());
            }
        }
        return mResources;
    }

    @Override
    public Object getSystemService(String name) {
        if (LAYOUT_INFLATER_SERVICE.equals(name)) {
            if (mInflater == null) {
                mInflater = LayoutInflater.from(getBaseContext()).cloneInContext(this);
            }
            return mInflater;
        }
        return getBaseContext().getSystemService(name);
    }

    private static Locale getConfigLocaleLegacy(Configuration config) {
        return config.locale;
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static Locale getConfigLocale(Configuration config) {
        return config.getLocales().get(0);
    }

    private static void setConfigLocaleLegacy(Configuration config, Locale locale) {
        config.locale = locale;
        config.setLayoutDirection(locale);
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static void setConfigLocale(Configuration config, Locale locale) {
        config.setLocale(locale);
        config.setLayoutDirection(locale);
    }
}
