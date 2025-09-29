package com.tencent.tcmpp.demo.bean;

import android.graphics.Bitmap;

public class GlobalConfigure {
    public Bitmap icon;


    public String appName;
    public String description;
    public String mainTitle;
    public boolean mockApi;

    private GlobalConfigure(Bitmap icon, String appName, String description) {
        this.icon = icon;
        this.appName = appName;
        this.description = description;
    }

    public GlobalConfigure(Bitmap icon, String appName, String description, String mainTitle) {
        this.icon = icon;
        this.appName = appName;
        this.description = description;
        this.mainTitle = mainTitle;
    }

    public GlobalConfigure(Bitmap icon, String appName, String description, String mainTitle, boolean mockApi) {
        this.icon = icon;
        this.appName = appName;
        this.description = description;
        this.mainTitle = mainTitle;
        this.mockApi = mockApi;
    }

    private GlobalConfigure() {

    }

    public static class Builder {
        private Bitmap icon;
        private String appName;
        private String description;
        private String mainTitle;
        private boolean mockApi;


        public Builder icon(Bitmap icon) {
            this.icon = icon;
            return this;
        }

        public Builder appName(String appName) {
            this.appName = appName;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder mainTitle(String mainTitle) {
            this.mainTitle = mainTitle;
            return this;
        }

        public Builder mockApi(boolean mock) {
            this.mockApi = mock;
            return this;
        }

        public GlobalConfigure build() {
            return new GlobalConfigure(icon, appName, description, mainTitle, mockApi);
        }
    }
}
