/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.util.Linkify;
import android.util.Patterns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.zhanghai.android.douya.ui.UriSpan;

public class SpanUtils {

    /**
     * Modified from {@link Patterns#WEB_URL}, with ftp scheme added and an optional trailing slash.
     */
    @SuppressWarnings("deprecation")
    public static final Pattern WEB_URL_PATTERN = Pattern.compile(
            "((?:(http|https|Http|Https|ftp|Ftp|rtsp|Rtsp):\\/\\/(?:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)"
                    + "\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,64}(?:\\:(?:[a-zA-Z0-9\\$\\-\\_"
                    + "\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,25})?\\@)?)?"
                    + "(?:" + Patterns.DOMAIN_NAME + ")"
                    + "(?:\\:\\d{1,5})?)"
                    + "(\\/(?:(?:[" + Patterns.GOOD_IRI_CHAR + "\\;\\/\\?\\:\\@\\&\\=\\#\\~"
                    + "\\-\\.\\+\\!\\*\\'\\(\\)\\,\\_])|(?:\\%[a-fA-F0-9]{2}))*)?"
                    + "(?=\\W|$)");

    private static final String[] WEB_URL_SCHEMES = { "http://", "https://", "ftp://", "rtsp://" };

    /**
     *  Filters out web URL matches that occur after an at-sign (@).  This is
     *  to prevent turning the domain name in an email address into a web link.
     */
    private static final MatchFilter WEB_URL_MATCH_FILTER = new MatchFilter() {
        public final boolean acceptMatch(CharSequence s, int start, int end) {
            if (start == 0) {
                return true;
            }
            if (s.charAt(start - 1) == '@') {
                return false;
            }
            return true;
        }
    };

    /**
     * Modified from {@link Patterns#EMAIL_ADDRESS}, with optional mailto scheme added.
     */
    public static final Pattern EMAIL_PATTERN = Pattern.compile(
            "(mailto:)?"
                    + "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}"
                    + "\\@"
                    + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}"
                    + "("
                    + "\\."
                    + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}"
                    + ")+"
    );

    private static final String[] EMAIL_SCHEMES = { "mailto:" };

    /**
     * Modified from {@link Patterns#PHONE}, with required tel/sms/smsto scheme added.
     */
    public static final Pattern PHONE_URI_PATTERN = Pattern.compile(
            "(tel|sms|smsto):"
                    + "(\\+[0-9]+[\\- \\.]*)?"
                    + "(\\([0-9]+\\)[\\- \\.]*)?"
                    + "([0-9][0-9\\- \\.]+[0-9])");

    /**
     * Modified from {@link Linkify#addLinks(Spannable, int)}.
     */
    public static Spannable addLinks(Spannable spannable) {

        List<Link> links = new ArrayList<>();
        gatherLinks(links, spannable, WEB_URL_PATTERN, WEB_URL_SCHEMES, WEB_URL_MATCH_FILTER);
        gatherLinks(links, spannable, EMAIL_PATTERN, EMAIL_SCHEMES, null);
        gatherLinks(links, spannable, PHONE_URI_PATTERN, null, null);

        pruneOverlaps(links);

        if (links.size() == 0) {
            return spannable;
        }

        for (Link link: links) {
            applyLink(link, spannable);
        }

        return spannable;
    }

    public static Spannable addLinks(CharSequence text) {
        return addLinks(new SpannableString(text));
    }

    private static void gatherLinks(List<Link> links, CharSequence text, Pattern pattern,
                                    String[] schemes, MatchFilter matchFilter) {
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            if (matchFilter == null || matchFilter.acceptMatch(text, start, end)) {
                links.add(new Link(start, end, makeUrl(matcher.group(0), schemes)));
            }
        }
    }

    private static String makeUrl(String url, String[] prefixes) {

        if (ArrayUtils.isEmpty(prefixes)) {
            return url;
        }

        boolean hasPrefix = false;
        for (String prefix : prefixes) {
            if (url.regionMatches(true, 0, prefix, 0, prefix.length())) {
                hasPrefix = true;
                // Fix capitalization if necessary
                if (!url.regionMatches(false, 0, prefix, 0, prefix.length())) {
                    url = prefix + url.substring(prefix.length());
                }
                break;
            }
        }

        if (!hasPrefix) {
            url = prefixes[0] + url;
        }

        return url;
    }

    private static void pruneOverlaps(List<Link> links) {

        Collections.sort(links);

        int i = 0;
        int last = links.size() - 1;
        while (i < last) {

            Link a = links.get(i);
            Link b = links.get(i + 1);
            int remove = -1;

            if ((a.start <= b.start) && (a.end > b.start)) {

                if (b.end <= a.end) {
                    remove = i + 1;
                } else if ((a.end - a.start) > (b.end - b.start)) {
                    remove = i + 1;
                } else if ((a.end - a.start) < (b.end - b.start)) {
                    remove = i;
                }

                if (remove != -1) {
                    links.remove(remove);
                    --last;
                    continue;
                }
            }

            ++i;
        }
    }

    private static void applyLink(Link link, Spannable spannable) {
        spannable.setSpan(new UriSpan(link.url), link.start, link.end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private interface MatchFilter {
        boolean acceptMatch(CharSequence s, int start, int end);
    }

    private static class Link implements Comparable<Link> {

        public int start;
        public int end;
        public String url;

        public Link(int start, int end, String url) {
            this.start = start;
            this.end = end;
            this.url = url;
        }

        @Override
        public int compareTo(@NonNull Link that) {
            if (start < that.start) {
                return -1;
            }
            if (start > that.start) {
                return 1;
            }
            if (end < that.end) {
                return 1;
            }
            if (end > that.end) {
                return -1;
            }
            return 0;
        }
    }
}
