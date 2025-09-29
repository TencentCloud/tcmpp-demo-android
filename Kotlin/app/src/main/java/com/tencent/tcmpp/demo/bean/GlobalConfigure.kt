package com.tencent.tcmpp.demo.bean

import android.graphics.Bitmap

data class GlobalConfigure(
    val icon: Bitmap? = null,
    val appName: String? = null,
    val description: String? = null,
    val mainTitle: String? = null,
    val mockApi: Boolean = false
) {
    class Builder {
        private var icon: Bitmap? = null
        private var appName: String? = null
        private var description: String? = null
        private var mainTitle: String? = null
        private var mockApi: Boolean = false

        fun icon(icon: Bitmap?): Builder {
            this.icon = icon
            return this
        }

        fun appName(appName: String?): Builder {
            this.appName = appName
            return this
        }

        fun description(description: String?): Builder {
            this.description = description
            return this
        }

        fun mainTitle(mainTitle: String?): Builder {
            this.mainTitle = mainTitle
            return this
        }

        fun mockApi(mock: Boolean): Builder {
            this.mockApi = mock
            return this
        }

        fun build(): GlobalConfigure {
            return GlobalConfigure(icon, appName, description, mainTitle, mockApi)
        }
    }
}