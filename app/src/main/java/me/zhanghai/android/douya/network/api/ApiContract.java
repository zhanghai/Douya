/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api;

import android.os.Build;

import com.android.volley.DefaultRetryPolicy;

public interface ApiContract {

    interface Request {

        interface Token {

            String URL = "https://www.douban.com/service/auth2/token";

            String CLIENT_ID = "client_id";
            String CLIENT_SECRET = "client_secret";
            String REDIRECT_URI = "redirect_uri";
            interface RedirectUris {
                String FRODO = "frodo://app/oauth/callback/";
            }
            String GRANT_TYPE = "grant_type";
            interface GrantTypes {
                String PASSWORD = "password";
                String REFRESH_TOKEN = "refresh_token";
            }
            String USERNAME = "username";
            String PASSWORD = "password";
            String REFRESH_TOKEN = "refresh_token";
        }

        interface Base {
            int INITIAL_TIMEOUT_MS = 10000;
            int MAX_NUM_RETRIES = 2;
            float BACKOFF_MULTIPLIER = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        }

        interface Frodo {

            // API protocol version is derived from user agent string.
            String USER_AGENT = "api-client/Volley/1 com.douban.frodo/4.0.0(66) Android/"
                    + Build.VERSION.SDK_INT+ " " + Build.PRODUCT + " " + Build.MANUFACTURER + " "
                    + Build.MODEL + "  rom:android";

            String API_HOST = "https://frodo.douban.com/api/v2/";

            interface Base {
                String API_KEY = "apiKey";
                String UDID = "udid";
                String DEVICE_ID = "device_id";
                String CHANNEL = "channel";
                interface Channels {
                    String DOUBAN = "Douban";
                }
                String OS_ROM = "os_rom";
            }

            interface NotificationList {

                String URL = API_HOST + "mine/notifications";

                String START = "start";
                String COUNT = "count";
            }

            interface UserDiaryList {

                String URL_FORMAT = API_HOST + "user/%s/notes";

                String START = "start";
                String COUNT = "count";
            }

            interface UserItemList {
                String URL_FORMAT = API_HOST + "user/%s/itemlist";
            }

            interface UserReviewList {

                String URL_FORMAT = API_HOST + "user/%s/reviews";

                String START = "start";
                String COUNT = "count";
            }

            interface ItemReviewList {

                String URL_FORMAT = API_HOST + "subject/%d/reviews";

                String START = "start";
                String COUNT = "count";
            }
        }

        interface ApiV2 {

            String API_HOST = "https://api.douban.com/v2/";

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

            interface UserInfo {

                String URL_FORMAT = API_HOST + "lifestream/user/%s";

                String UID_CURRENT = "~me";
            }

            interface Followship {
                String URL_FORMAT = API_HOST + "lifestream/user/%s/follow";
            }

            interface FollowingList {

                String URL_FORMAT = API_HOST + "lifestream/user/%s/followings";

                String COUNT = "count";
                String START = "start";
                String TAG = "tag";
            }

            interface FollowerList {

                String URL_FORMAT = API_HOST + "lifestream/user/%s/followers";

                String COUNT = "count";
                String START = "start";
            }

            interface BroadcastList {

                interface Urls {
                    String HOME = API_HOST + "lifestream/home_timeline";
                    String USER_FORMAT = API_HOST + "lifestream/user_timeline/%s";
                    String TOPIC = API_HOST + "lifestream/topics";
                }

                String SINCE_ID = "since_id";
                String UNTIL_ID = "until_id";
                String COUNT = "count";
                String START = "start";
                String Q = "q";
            }

            interface Broadcast {
                String URL_FORMAT = API_HOST + "lifestream/status/%d";
            }

            interface LikeBroadcast {
                String URL_FORMAT = API_HOST + "lifestream/status/%d/like";
            }

            interface RebroadcastBroadcast {
                String URL_FORMAT = API_HOST + "lifestream/status/%d/reshare";
            }

            interface BroadcastCommentList {

                String URL_FORMAT = API_HOST + "lifestream/status/%d/comments";

                String COUNT = "count";
                String START = "start";
            }

            interface BroadcastLikerList {

                String URL_FORMAT = API_HOST + "lifestream/status/%d/likers";

                String COUNT = "count";
                String START = "start";
            }

            interface BroadcastRebroadcasterList {

                String URL_FORMAT = API_HOST + "lifestream/status/%d/resharers";

                String COUNT = "count";
                String START = "start";
            }

            interface DeleteBroadcastComment {
                String URL_FORMAT = API_HOST + "lifestream/status/%d/comment/%d";
            }

            interface SendBroadcastComment {

                String URL_FORMAT = API_HOST + "lifestream/status/%d/comments";

                String TEXT = "text";
            }

            interface DeleteBroadcast {
                String URL_FORMAT = API_HOST + "lifestream/status/%d";
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
                    int NOT_FOUND = 11110;
                    int AUTHOR_BANNED = 11111;
                }
                interface LikeBroadcast {
                    int ALREADY_LIKED = 11107;
                    int NOT_LIKED_YET = 11108;
                }
                interface RebroadcastBroadcast {
                    int ALREADY_REBROADCASTED = 11104;
                    int NOT_REBROADCASTED_YET = 11105;
                }
            }
            String MSG = "msg";
            String REQUEST = "request";
            String LOCALIZED_MESSAGE = "localized_message";
        }

        interface Token {
            String DOUBAN_UESR_NAME = "douban_user_name";
            String DOUBAN_USER_ID = "douban_user_id";
            String ACCESS_TOKEN = "access_token";
            String REFRESH_TOKEN = "refresh_token";
            String EXPIRES_IN = "expires_in";
        }
    }
}
