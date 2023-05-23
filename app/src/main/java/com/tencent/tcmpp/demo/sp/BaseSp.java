package com.tencent.tcmpp.demo.sp;

import android.content.SharedPreferences;
import android.text.TextUtils;
import java.util.Iterator;
import java.util.Map;

/**
 * SharedPreferences基类
 *
 * @author robincxiao 2019/9/3 11:11
 */
public class BaseSp {

    protected SharedPreferences mSharedPreferences;
    protected SharedPreferences.Editor mEditor;

    protected void putString(SharedPreferences.Editor editor, String key, String value) {
        if (editor == null || TextUtils.isEmpty(key)) {
            return;
        }

        editor.putString(key, value).commit();
    }

    protected void putMap(SharedPreferences.Editor editor, Map<String, Object> map) {
        if (editor == null || map == null || map.size() <= 0) {
            return;
        }

        Iterator<String> iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Object value = map.get(key);
            if (value instanceof String) {
                editor.putString(key, (String) value);
            } else if (value instanceof Long) {
                editor.putLong(key, (Long) value);
            } else if (value instanceof Integer) {
                editor.putInt(key, (Integer) value);
            } else if (value instanceof Boolean) {
                editor.putBoolean(key, (Boolean) value);
            } else if (value instanceof Float) {
                editor.putFloat(key, (Float) value);
            }
        }

        editor.commit();
    }

    protected String getString(SharedPreferences sp, String key, String def) {
        if (sp == null || TextUtils.isEmpty(key)) {
            return "";
        }

        return sp.getString(key, def);
    }

    protected void putLong(SharedPreferences.Editor editor, String key, long value) {
        if (editor == null || TextUtils.isEmpty(key)) {
            return;
        }

        editor.putLong(key, value).commit();
    }

    public long getLong(SharedPreferences sp, String key, long def) {
        if (sp == null || TextUtils.isEmpty(key)) {
            return def;
        }

        return sp.getLong(key, def);
    }

    protected boolean contains(SharedPreferences sp, String key) {
        if (sp == null || TextUtils.isEmpty(key)) {
            return false;
        }

        return sp.contains(key);
    }

    protected void putInt(SharedPreferences.Editor editor, String key, int value) {
        if (editor == null || TextUtils.isEmpty(key)) {
            return;
        }

        editor.putInt(key, value).commit();
    }

    protected int getInt(SharedPreferences sp, String key, int def) {
        if (sp == null || TextUtils.isEmpty(key)) {
            return def;
        }

        return sp.getInt(key, def);
    }

    protected void putBoolean(SharedPreferences.Editor editor, String key, boolean value) {
        if (editor == null || TextUtils.isEmpty(key)) {
            return;
        }

        editor.putBoolean(key, value).commit();
    }

    protected boolean getBoolean(SharedPreferences sp, String key, boolean def) {
        if (sp == null || TextUtils.isEmpty(key)) {
            return def;
        }

        return sp.getBoolean(key, def);
    }

    protected void remove(SharedPreferences.Editor editor, String key) {
        if (editor == null) {
            return;
        }

        editor.remove(key).commit();
    }

    protected void clear(SharedPreferences.Editor editor) {
        if (editor == null) {
            return;
        }

        editor.clear().commit();
    }
}
