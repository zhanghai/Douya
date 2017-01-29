/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import android.content.Context;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.network.api.info.apiv2.SimpleUser;

public class DoubanUtils {

    private static final Map<String, String> INTEREST_TYPE_URL_MAP;
    static {
        INTEREST_TYPE_URL_MAP = new HashMap<>();
        INTEREST_TYPE_URL_MAP.put("热门精选", "https://www.douban.com/interest/1/1/");
        INTEREST_TYPE_URL_MAP.put("电影", "https://www.douban.com/interest/2/1/");
        INTEREST_TYPE_URL_MAP.put("音乐", "https://www.douban.com/interest/2/2/");
        INTEREST_TYPE_URL_MAP.put("读书", "https://www.douban.com/interest/2/3/");
        INTEREST_TYPE_URL_MAP.put("时尚", "https://www.douban.com/interest/2/4/");
        INTEREST_TYPE_URL_MAP.put("艺术", "https://www.douban.com/interest/2/5/");
        INTEREST_TYPE_URL_MAP.put("人文", "https://www.douban.com/interest/2/6/");
        INTEREST_TYPE_URL_MAP.put("建筑", "https://www.douban.com/interest/2/7/");
        INTEREST_TYPE_URL_MAP.put("设计", "https://www.douban.com/interest/2/8/");
        INTEREST_TYPE_URL_MAP.put("摄影", "https://www.douban.com/interest/2/9/");
        INTEREST_TYPE_URL_MAP.put("自然", "https://www.douban.com/interest/2/10/");
        INTEREST_TYPE_URL_MAP.put("历史", "https://www.douban.com/interest/2/11/");
        INTEREST_TYPE_URL_MAP.put("科学", "https://www.douban.com/interest/2/12/");
        INTEREST_TYPE_URL_MAP.put("健康", "https://www.douban.com/interest/2/13/");
        INTEREST_TYPE_URL_MAP.put("体育", "https://www.douban.com/interest/2/14/");
        INTEREST_TYPE_URL_MAP.put("教育", "https://www.douban.com/interest/2/15/");
        INTEREST_TYPE_URL_MAP.put("旅行", "https://www.douban.com/interest/2/16/");
        INTEREST_TYPE_URL_MAP.put("居家", "https://www.douban.com/interest/2/17/");
        INTEREST_TYPE_URL_MAP.put("美食", "https://www.douban.com/interest/2/18/");
        INTEREST_TYPE_URL_MAP.put("宠物", "https://www.douban.com/interest/2/19/");
        INTEREST_TYPE_URL_MAP.put("娱乐", "https://www.douban.com/interest/2/20/");
        INTEREST_TYPE_URL_MAP.put("趣味", "https://www.douban.com/interest/2/21/");
        INTEREST_TYPE_URL_MAP.put("财经", "https://www.douban.com/interest/2/22/");
        INTEREST_TYPE_URL_MAP.put("动漫", "https://www.douban.com/interest/2/23/");
        INTEREST_TYPE_URL_MAP.put("成长", "https://www.douban.com/interest/2/24/");
        INTEREST_TYPE_URL_MAP.put("情感", "https://www.douban.com/interest/2/25/");
        INTEREST_TYPE_URL_MAP.put("美女", "https://www.douban.com/interest/2/26/");
        INTEREST_TYPE_URL_MAP.put("同性", "https://www.douban.com/interest/2/27/");
        INTEREST_TYPE_URL_MAP.put("创意", "https://www.douban.com/interest/2/28/");
        INTEREST_TYPE_URL_MAP.put("科技", "https://www.douban.com/interest/2/29/");
        INTEREST_TYPE_URL_MAP.put("星座", "https://www.douban.com/interest/2/30/");
        INTEREST_TYPE_URL_MAP.put("时事", "https://www.douban.com/interest/2/31/");
        INTEREST_TYPE_URL_MAP.put("言论", "https://www.douban.com/interest/2/32/");
        INTEREST_TYPE_URL_MAP.put("汽车", "https://www.douban.com/interest/2/33/");
        INTEREST_TYPE_URL_MAP.put("自我管理", "https://www.douban.com/interest/2/34/");
        INTEREST_TYPE_URL_MAP.put("移动应用", "https://www.douban.com/interest/2/35/");
        INTEREST_TYPE_URL_MAP.put("男装", "https://www.douban.com/interest/3/1/");
        INTEREST_TYPE_URL_MAP.put("女装", "https://www.douban.com/interest/3/2/");
        INTEREST_TYPE_URL_MAP.put("数码", "https://www.douban.com/interest/3/4/");
        INTEREST_TYPE_URL_MAP.put("家居生活", "https://www.douban.com/interest/3/5/");
        INTEREST_TYPE_URL_MAP.put("美容护肤", "https://www.douban.com/interest/3/6/");
        INTEREST_TYPE_URL_MAP.put("户外运动", "https://www.douban.com/interest/3/7/");
    }
    private static final String INTEREST_URL_DEFAULT = "https://www.douban.com/interest/1/1/";

    private DoubanUtils() {}

    public static String getAtUserString(String userIdOrUid) {
        return '@' + userIdOrUid + ' ';
    }

    public static String getAtUserString(SimpleUser user) {
        //noinspection deprecation
        return getAtUserString(user.uid);
    }

    public static String getInterestTypeUrl(String type) {
        String url = null;
        if (!TextUtils.isEmpty(type)) {
            url = INTEREST_TYPE_URL_MAP.get(type);
        }
        if (TextUtils.isEmpty(url)) {
            LogUtils.w("Unknown interest type: " + type);
            url = INTEREST_URL_DEFAULT;
        }
        return url;
    }

    public static String getRatingHint(int rating, Context context) {
        String[] ratingHints = context.getResources().getStringArray(R.array.item_rating_hints);
        if (rating == 0) {
            return "";
        } else if (rating > 0 && rating <= ratingHints.length) {
            return ratingHints[rating - 1];
        } else {
            return context.getString(R.string.item_rating_hint_unknown);
        }
    }
}
