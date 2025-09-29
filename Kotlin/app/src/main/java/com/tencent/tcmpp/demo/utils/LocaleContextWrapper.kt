package com.tencent.tcmpp.demo.utils

import android.annotation.TargetApi
import android.content.Context
import android.content.ContextWrapper
import android.content.res.AssetManager
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.view.LayoutInflater
import java.util.*

class LocaleContextWrapper private constructor(
    base: Context,
    private val mOverrideConfig: Configuration
) : ContextWrapper(base) {
    
    private var mResources: Resources? = null
    private var mInflater: LayoutInflater? = null

    companion object {
        fun create(base: Context): ContextWrapper {
            return create(base, null)
        }

        fun create(base: Context, locale: Locale?): ContextWrapper {
            val baseConfig = base.resources.configuration
            val overrideConfig = Configuration()

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                setConfigLocale(overrideConfig, locale ?: getConfigLocale(baseConfig))
            } else {
                setConfigLocaleLegacy(overrideConfig, locale ?: getConfigLocaleLegacy(baseConfig))
            }
            return LocaleContextWrapper(base, overrideConfig)
        }

        private fun getConfigLocaleLegacy(config: Configuration): Locale {
            return config.locale
        }

        @TargetApi(Build.VERSION_CODES.N)
        private fun getConfigLocale(config: Configuration): Locale {
            return config.locales[0]
        }

        private fun setConfigLocaleLegacy(config: Configuration, locale: Locale) {
            config.locale = locale
            config.setLayoutDirection(locale)
        }

        @TargetApi(Build.VERSION_CODES.N)
        private fun setConfigLocale(config: Configuration, locale: Locale) {
            config.setLocale(locale)
            config.setLayoutDirection(locale)
        }
    }

    /**
     * update locale of current ContextWrapper
     *
     * @param locale target locale
     * @return is updated
     */
    fun updateLocale(locale: Locale?): Boolean {
        if (locale != null) {
            val currentLocale = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                getConfigLocale(mOverrideConfig)
            } else {
                getConfigLocaleLegacy(mOverrideConfig)
            }
            
            if (currentLocale.language != locale.language) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    setConfigLocale(mOverrideConfig, locale)
                    mResources?.let { resources ->
                        val resConfig = resources.configuration
                        setConfigLocale(resConfig, locale)
                        resources.updateConfiguration(resConfig, resources.displayMetrics)
                    }
                } else {
                    setConfigLocaleLegacy(mOverrideConfig, locale)
                    mResources?.let { resources ->
                        val resConfig = resources.configuration
                        setConfigLocaleLegacy(resConfig, locale)
                        resources.updateConfiguration(resConfig, resources.displayMetrics)
                    }
                }
                return true
            }
        }
        return false
    }

    override fun getAssets(): AssetManager {
        return resourcesInternal.assets
    }

    override fun getResources(): Resources {
        return resourcesInternal
    }

    private val resourcesInternal: Resources
        get() {
            if (mResources == null) {
                val resContext = createConfigurationContext(mOverrideConfig)
                mResources = resContext.resources
                mResources!!.updateConfiguration(mOverrideConfig, mResources!!.displayMetrics)
            } else {
                // FIXME: 2024/5/30 why resource's language differs from override config? Is it changed by something?
                val resConfig = mResources!!.configuration
                val currentLocale: Locale
                val targetLocale: Locale
                
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                    currentLocale = getConfigLocale(resConfig)
                    targetLocale = getConfigLocale(mOverrideConfig)
                } else {
                    currentLocale = getConfigLocaleLegacy(resConfig)
                    targetLocale = getConfigLocaleLegacy(mOverrideConfig)
                }
                
                if (currentLocale.language != targetLocale.language) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        setConfigLocale(resConfig, targetLocale)
                    } else {
                        setConfigLocaleLegacy(resConfig, targetLocale)
                    }
                    mResources!!.updateConfiguration(resConfig, mResources!!.displayMetrics)
                }
            }
            return mResources!!
        }

    override fun getSystemService(name: String): Any? {
        if (LAYOUT_INFLATER_SERVICE == name) {
            if (mInflater == null) {
                mInflater = LayoutInflater.from(baseContext).cloneInContext(this)
            }
            return mInflater
        }
        return baseContext.getSystemService(name)
    }
}