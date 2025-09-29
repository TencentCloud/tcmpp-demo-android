package com.tencent.tcmpp.demo.utils;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class MiniAppCategoryHelper {

    public static List<MiniAppCategory> getCategoryFromString(String categoryStrings) {
        List<MiniAppCategory> ret = new ArrayList<>();
        if (!TextUtils.isEmpty(categoryStrings)) {
            String[] categories = categoryStrings.split(",");
            for (String category : categories) {
                String[] levels = category.split("->");
                if (levels.length == 2) {
                    String firstCategory = levels[0];
                    String second = levels[1];
                    if (second.contains("_")) {
                        String[] secondCatIdAndName = second.split("_");
                        ret.add(new MiniAppCategory(firstCategory, secondCatIdAndName[1], Integer.parseInt(secondCatIdAndName[0])));
                    } else {
                        ret.add(new MiniAppCategory(levels[0], levels[1]));
                    }
                }
            }
        }
        return ret;
    }

    public static class MiniAppCategory {
        public String firstLevelCategory;
        public String secondLevelCategory;
        public int cateId;

        public MiniAppCategory(String firstLevel, String secondLevel) {
            this.firstLevelCategory = firstLevel;
            this.secondLevelCategory = secondLevel;
        }

        public MiniAppCategory(String firstLevelCategory, String secondLevelCategory, int cateId) {
            this.firstLevelCategory = firstLevelCategory;
            this.secondLevelCategory = secondLevelCategory;
            this.cateId = cateId;
        }
    }
}
