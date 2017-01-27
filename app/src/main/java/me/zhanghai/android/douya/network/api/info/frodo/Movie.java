/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Movie extends CollectableItem {

    public List<Celebrity> actors = new ArrayList<>();

    @SerializedName("aka")
    public List<String> alternativeNames = new ArrayList<>();

    public List<String> countries = new ArrayList<>();

    @SerializedName("cover")
    public Photo poster;

    public List<Celebrity> directors = new ArrayList<>();

    public List<String> durations = new ArrayList<>();

    @SerializedName("episodes_count")
    public int episodeCount;

    public List<String> genres = new ArrayList<>();

    @SerializedName("has_linewatch")
    public boolean hasOnlineSource;

    @SerializedName("in_blacklist")
    public boolean isInBlacklist;

    @SerializedName("info_url")
    public String informationUrl;

    @SerializedName("is_released")
    public boolean isReleased;

    @SerializedName("is_tv")
    public boolean isTv;

    public List<String> languages = new ArrayList<>();

    @SerializedName("lineticket_url")
    public String ticketUrl;

    @SerializedName("original_title")
    public String originalTitle;

    @SerializedName("pubdate")
    public List<String> releaseDates = new ArrayList<>();

    @SerializedName("ticket_price_info")
    public String ticketPriceInformation;

    public MovieTrailer trailer;

    public String year;
}
