/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network;

public class Http {

    public interface Methods {
        String GET = "GET";
        String HEAD = "HEAD";
        String POST = "POST";
        String PUT = "PUT";
        String DELETE = "DELETE";
        String CONNECT = "CONNECT";
        String OPTIONS = "OPTIONS";
        String TRACE = "TRACE";
        String PATCH = "PATCH";
    }

    public static class Headers {
        public static final String ACCEPT = "Accept";
        public static final String ACCEPT_CHARSET = "Accept-Charset";
        public static final String ACCEPT_ENCODING = "Accept-Encoding";
        public static final String ACCEPT_VERSION = "X-Accept-Version";
        public static final String AUTHORIZATION = "Authorization";
        private static final String BEARER_PREFIX = "Bearer ";
        public static String makeBearerAuthorization(String token) {
            return BEARER_PREFIX + token;
        }
        public static String getTokenFromBearerAuthorization(String bearerAuthorization) {
            int bearerPrefixLength = BEARER_PREFIX.length();
            if (bearerAuthorization == null || bearerAuthorization.length() < bearerPrefixLength) {
                return null;
            } else {
                return bearerAuthorization.substring(bearerPrefixLength);
            }
        }
        public static final String CONTENT_DISPOSITION = "Content-Disposition";
        public static final String CONTENT_ENCODING = "Content-Encoding";
        public static final String CONTENT_LENGTH = "Content-Length";
        public static final String CONTENT_TYPE = "Content-Type";
        public static final String Cookie = "Cookie";
        public static final String REFERER = "Referer";
        public static final String USER_AGENT = "User-Agent";
    }

    public static class ContentTypes {
        public static final String FORM = "application/x-www-form-urlencoded";
        public static final String FORM_UTF8 = Http.ContentTypes.withCharset(Http.ContentTypes.FORM, Http.Charsets.UTF8);
        public static final String JSON = "application/json";
        public static final String JSON_UTF8 = Http.ContentTypes.withCharset(ContentTypes.JSON, Http.Charsets.UTF8);
        public static String withCharset(String contentType, String charset) {
            return contentType + "; charset=" + charset;
        }
    }

    public interface Charsets {
        String UTF8 = "UTF-8";
    }

    public interface Encodings {
        String GZIP = "gzip";
    }
}
