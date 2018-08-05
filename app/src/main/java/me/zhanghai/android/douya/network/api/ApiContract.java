/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api;

import android.os.Build;

import java.util.Arrays;
import java.util.List;

import me.zhanghai.android.douya.network.Http;

public interface ApiContract {

    interface Request {

        interface Authentication {

            interface BaseUrls {
                String API_V2 = "https://www.douban.com/";
                String FRODO = "https://frodo.douban.com/";
            }
            String URL = "service/auth2/token";

            String ACCEPT_CHARSET = Http.Charsets.UTF8;
            interface RedirectUris {
                String API_V2 = "http://shuo.douban.com/!service/android";
                String FRODO = "frodo://app/oauth/callback/";
            }
            interface GrantTypes {
                String PASSWORD = "password";
                String REFRESH_TOKEN = "refresh_token";
            }
        }

        interface Base {
            int MAX_NUM_RETRIES = 2;
        }

        interface Frodo {

            String BASE_URL = "https://frodo.douban.com/api/v2/";

            // API protocol version is derived from user agent string.
            String USER_AGENT = "api-client/1 com.douban.frodo/6.0.1(138) Android/" +
                    Build.VERSION.SDK_INT+ " product/" + Build.PRODUCT + " vendor/" +
                    // Sorry Frodo, but we don't want to hold ACCESS_NETWORK_STATE.
                    Build.MANUFACTURER + " model/" + Build.MODEL + "  rom/android  network/wifi";

            String API_KEY = "apikey";
            String CHANNEL = "channel";
            interface Channels {
                String DOUBAN = "Douban";
            }
            String UDID = "udid";
            String OS_ROM = "os_rom";
            interface OsRoms {
                String ANDROID = "android";
            }

            List<String> SIGNATURE_HOSTS = Arrays.asList("frodo.douban.com", "api.douban.com");
            String SIG = "_sig";
            String TS = "_ts";
        }

        interface ApiV2 {

            String BASE_URL = "https://api.douban.com/v2/";

            String USER_AGENT = "api-client/2.0 com.douban.shuo/2.2.7(123) Android/" +
                    Build.VERSION.SDK_INT + " " + Build.PRODUCT + " " + Build.MANUFACTURER + " " +
                    Build.MODEL;

            interface Base {
                String API_KEY = "apikey";
                String UDID = "udid";
            }

            interface LifeStream {
                String VERSION = "version";
                interface Versions {
                    int TWO = 2;
                }
            }
        }
    }

    interface Response {

        interface Error {
            String CODE = "code";
            interface Codes {
                interface Custom {
                    int INVALID_ERROR_RESPONSE = -1;
                }
                interface Base {
                    int INVALID_REQUEST_997 = 997;
                    int UNKNOWN_V2_ERROR = 999;
                    int NEED_PERMISSION = 1000;
                    int URI_NOT_FOUND = 1001;
                    int MISSING_ARGS = 1002;
                    int IMAGE_TOO_LARGE = 1003;
                    int HAS_BAN_WORD = 1004;
                    int INPUT_TOO_SHORT = 1005;
                    int TARGET_NOT_FOUND = 1006;
                    int NEED_CAPTCHA = 1007;
                    int IMAGE_UNKNOWN = 1008;
                    int IMAGE_WRONG_FORMAT = 1009;
                    int IMAGE_WRONG_CK = 1010;
                    int IMAGE_CK_EXPIRED = 1011;
                    int TITLE_MISSING = 1012;
                    int DESC_MISSING = 1013;
                }
                interface Token {
                    int INVALID_REQUEST_SCHEME = 100;
                    int INVALID_REQUEST_METHOD = 101;
                    int ACCESS_TOKEN_IS_MISSING = 102;
                    int INVALID_ACCESS_TOKEN = 103;
                    int INVALID_APIKEY = 104;
                    int APIKEY_IS_BLOCKED = 105;
                    int ACCESS_TOKEN_HAS_EXPIRED = 106;
                    int INVALID_REQUEST_URI = 107;
                    int INVALID_CREDENCIAL1 = 108;
                    int INVALID_CREDENCIAL2 = 109;
                    int NOT_TRIAL_USER = 110;
                    int RATE_LIMIT_EXCEEDED1 = 111;
                    int RATE_LIMIT_EXCEEDED2 = 112;
                    int REQUIRED_PARAMETER_IS_MISSING = 113;
                    int UNSUPPORTED_GRANT_TYPE = 114;
                    int UNSUPPORTED_RESPONSE_TYPE = 115;
                    int CLIENT_SECRET_MISMATCH = 116;
                    int REDIRECT_URI_MISMATCH = 117;
                    int INVALID_AUTHORIZATION_CODE = 118;
                    int INVALID_REFRESH_TOKEN = 119;
                    int USERNAME_PASSWORD_MISMATCH = 120;
                    int INVALID_USER = 121;
                    int USER_HAS_BLOCKED = 122;
                    int ACCESS_TOKEN_HAS_EXPIRED_SINCE_PASSWORD_CHANGED = 123;
                    int ACCESS_TOKEN_HAS_NOT_EXPIRED = 124;
                    int INVALID_REQUEST_SCOPE = 125;
                    int INVALID_REQUEST_SOURCE = 126;
                    int THIRDPARTY_LOGIN_AUTH_FAILED = 127;
                    int USER_LOCKED = 128;
                }
                interface Followship {
                    int ALREADY_FOLLOWED = 10003;
                    int NOT_FOLLOWED_YET = 10005;
                }
                interface Broadcast {
                    int NOT_FOUND = 1212;
                }
                interface RebroadcastBroadcast {
                    int REBROADCASTED_BROADCAST_DELETED = 1265;
                }
            }
            String MSG = "msg";
            String REQUEST = "request";
            String LOCALIZED_MESSAGE = "localized_message";
        }
    }
}
