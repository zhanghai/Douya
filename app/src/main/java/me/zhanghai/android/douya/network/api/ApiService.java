/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api;

import android.text.TextUtils;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.util.List;

import me.zhanghai.android.douya.account.info.AccountContract;
import me.zhanghai.android.douya.network.GsonResponseBodyConverterFactory;
import me.zhanghai.android.douya.network.Http;
import me.zhanghai.android.douya.network.api.credential.ApiCredential;
import me.zhanghai.android.douya.network.api.info.AuthenticationResponse;
import me.zhanghai.android.douya.network.api.info.apiv2.Broadcast;
import me.zhanghai.android.douya.network.api.info.apiv2.Comment;
import me.zhanghai.android.douya.network.api.info.apiv2.CommentList;
import me.zhanghai.android.douya.network.api.info.apiv2.SimpleUser;
import me.zhanghai.android.douya.network.api.info.apiv2.User;
import me.zhanghai.android.douya.network.api.info.apiv2.UserList;
import me.zhanghai.android.douya.network.api.info.frodo.DiaryList;
import me.zhanghai.android.douya.network.api.info.frodo.Movie;
import me.zhanghai.android.douya.network.api.info.frodo.Notification;
import me.zhanghai.android.douya.network.api.info.frodo.NotificationList;
import me.zhanghai.android.douya.network.api.info.frodo.ReviewList;
import me.zhanghai.android.douya.network.api.info.frodo.UserItemList;
import me.zhanghai.android.douya.util.StringUtils;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public class ApiService {

    private static final ApiService sInstance = new ApiService();

    private AuthenticationService mAuthenticationService;
    private OkHttpClient mLifeStreamHttpClient;
    private LifeStreamService mLifeStreamService;
    private OkHttpClient mFrodoHttpClient;
    private FrodoService mFrodoService;

    public static ApiService getInstance() {
        return sInstance;
    }

    private ApiService() {
        mAuthenticationService = createAuthenticationService();
        mLifeStreamHttpClient = createApiHttpClient(new LifeStreamInterceptor(),
                AccountContract.AUTH_TOKEN_TYPE_API_V2);
        mLifeStreamService = createApiService(ApiContract.Request.ApiV2.API_HOST,
                mLifeStreamHttpClient, LifeStreamService.class);
        mFrodoHttpClient = createApiHttpClient(new FrodoInterceptor(),
                AccountContract.AUTH_TOKEN_TYPE_FRODO);
        mFrodoService = createApiService(ApiContract.Request.Frodo.API_HOST,
                mFrodoHttpClient, FrodoService.class);
    }

    private static AuthenticationService createAuthenticationService() {
        return new Retrofit.Builder()
                .addCallAdapterFactory(ApiCallAdapter.Factory.create())
                .addConverterFactory(GsonResponseBodyConverterFactory.create())
                // Make Retrofit happy.
                .baseUrl("https://www.douban.com")
                .client(new OkHttpClient.Builder()
                        .addNetworkInterceptor(new StethoInterceptor())
                        .build())
                .build()
                .create(AuthenticationService.class);
    }

    private static OkHttpClient createApiHttpClient(Interceptor interceptor, String authTokenType) {
        return new OkHttpClient.Builder()
                // AuthenticationInterceptor may retry the request, so it must be an application
                // interceptor.
                .addInterceptor(new ApiAuthenticationInterceptor(authTokenType))
                .addNetworkInterceptor(interceptor)
                .addNetworkInterceptor(new StethoInterceptor())
                .build();
    }

    private static <T> T createApiService(String baseUrl, OkHttpClient client,
                                         Class<T> clientClass) {
        return new Retrofit.Builder()
                .addCallAdapterFactory(ApiCallAdapter.Factory.create())
                .addConverterFactory(GsonResponseBodyConverterFactory.create())
                .baseUrl(baseUrl)
                .client(client)
                .build()
                .create(clientClass);
    }

    public ApiRequest<AuthenticationResponse> authenticate(String authTokenType, String username,
                                                           String password) {
        switch (authTokenType) {
            case AccountContract.AUTH_TOKEN_TYPE_API_V2:
                return authenticateApiV2(username, password);
            case AccountContract.AUTH_TOKEN_TYPE_FRODO:
                return authenticateFrodo(username, password);
            default:
                throw new IllegalArgumentException("Unknown authTokenType: " + authTokenType);
        }
    }

    public ApiRequest<AuthenticationResponse> authenticate(String authTokenType, String refreshToken) {
        switch (authTokenType) {
            case AccountContract.AUTH_TOKEN_TYPE_API_V2:
                return authenticateApiV2(refreshToken);
            case AccountContract.AUTH_TOKEN_TYPE_FRODO:
                return authenticateFrodo(refreshToken);
            default:
                throw new IllegalArgumentException("Unknown authTokenType: " + authTokenType);
        }
    }

    private ApiRequest<AuthenticationResponse> authenticateApiV2(String username, String password) {
        return mAuthenticationService.authenticate(ApiContract.Request.ApiV2.USER_AGENT,
                ApiContract.Request.Authentication.ACCEPT_CHARSET, ApiCredential.ApiV2.KEY,
                ApiCredential.ApiV2.SECRET, ApiContract.Request.Authentication.RedirectUris.API_V2,
                ApiContract.Request.Authentication.GrantTypes.PASSWORD, username, password);
    }

    private ApiRequest<AuthenticationResponse> authenticateApiV2(String refreshToken) {
        return mAuthenticationService.authenticate(ApiContract.Request.ApiV2.USER_AGENT,
                ApiContract.Request.Authentication.ACCEPT_CHARSET, ApiCredential.ApiV2.KEY,
                ApiCredential.ApiV2.SECRET, ApiContract.Request.Authentication.RedirectUris.API_V2,
                ApiContract.Request.Authentication.GrantTypes.REFRESH_TOKEN, refreshToken);
    }

    private ApiRequest<AuthenticationResponse> authenticateFrodo(String username, String password) {
        return mAuthenticationService.authenticate(ApiContract.Request.Frodo.USER_AGENT,
                ApiContract.Request.Authentication.ACCEPT_CHARSET, ApiCredential.Frodo.KEY,
                ApiCredential.Frodo.SECRET, ApiContract.Request.Authentication.RedirectUris.FRODO,
                ApiContract.Request.Authentication.GrantTypes.PASSWORD, username, password);
    }

    private ApiRequest<AuthenticationResponse> authenticateFrodo(String refreshToken) {
        return mAuthenticationService.authenticate(ApiContract.Request.Frodo.USER_AGENT,
                ApiContract.Request.Authentication.ACCEPT_CHARSET, ApiCredential.Frodo.KEY,
                ApiCredential.Frodo.SECRET, ApiContract.Request.Authentication.RedirectUris.FRODO,
                ApiContract.Request.Authentication.GrantTypes.PASSWORD, refreshToken);
    }

    public ApiRequest<User> getUser(String userIdOrUid) {
        if (TextUtils.isEmpty(userIdOrUid)) {
            userIdOrUid = "~me";
        }
        return mLifeStreamService.getUser(userIdOrUid);
    }

    public ApiRequest<User> follow(String userIdOrUid, boolean follow) {
        if (follow) {
            return mLifeStreamService.follow(userIdOrUid);
        } else {
            return mLifeStreamService.unfollow(userIdOrUid);
        }
    }

    public ApiRequest<UserList> getFollowingList(String userIdOrUid, Integer start, Integer count,
                                                 String tag) {
        return mLifeStreamService.getFollowingList(userIdOrUid, start, count, tag);
    }

    public ApiRequest<UserList> getFollowingList(String userIdOrUid, Integer start, Integer count) {
        return getFollowingList(userIdOrUid, start, count, null);
    }

    public ApiRequest<UserList> getFollowerList(String userIdOrUid, Integer start, Integer count) {
        return mLifeStreamService.getFollowerList(userIdOrUid, start, count);
    }

    public ApiRequest<NotificationList> getNotificationList(Integer start, Integer count) {
        return new TransformResponseBodyApiRequest<NotificationList>(mFrodoService.getNotificationList(
                start, count)) {
            @Override
            protected void onTransformResponseBody(NotificationList notificationList) {
                // Fix for Frodo API.
                for (Notification notification : notificationList.notifications) {
                    notification.fix();
                }
            }
        };
    }

    public ApiRequest<List<Broadcast>> getBroadcastList(String userIdOrUid, String topic, Long untilId,
                                                        Integer count) {
        String url;
        if (TextUtils.isEmpty(userIdOrUid) && TextUtils.isEmpty(topic)) {
            url = "lifestream/home_timeline";
        } else if (TextUtils.isEmpty(topic)) {
            url = StringUtils.formatUs("lifestream/user_timeline/%s", userIdOrUid);
        } else {
            url = "lifestream/topics";
        }
        return mLifeStreamService.getBroadcastList(url, untilId, count, topic);
    }

    public ApiRequest<Broadcast> getBroadcast(long broadcastId) {
        return mLifeStreamService.getBroadcast(broadcastId);
    }

    public ApiRequest<CommentList> getBroadcastCommentList(long broadcastId, Integer start,
                                                           Integer count) {
        return mLifeStreamService.getBroadcastCommentList(broadcastId, start, count);
    }

    public ApiRequest<Broadcast> likeBroadcast(long broadcastId, boolean like) {
        if (like) {
            return mLifeStreamService.likeBroadcast(broadcastId);
        } else {
            return mLifeStreamService.unlikeBroadcast(broadcastId);
        }
    }

    public ApiRequest<Broadcast> rebroadcastBroadcast(long broadcastId, boolean rebroadcast) {
        if (rebroadcast) {
            return mLifeStreamService.rebroadcastBroadcast(broadcastId);
        } else {
            return mLifeStreamService.unrebroadcastBroadcast(broadcastId);
        }
    }

    public ApiRequest<List<SimpleUser>> getBroadcastLikerList(long broadcastId, Integer start,
                                                              Integer count) {
        return mLifeStreamService.getBroadcastLikerList(broadcastId, start, count);
    }

    public ApiRequest<List<SimpleUser>> getBroadcastRebroadcasterList(long broadcastId, Integer start,
                                                                      Integer count) {
        return mLifeStreamService.getBroadcastRebroadcasterList(broadcastId, start, count);
    }

    public ApiRequest<Boolean> deleteBroadcastComment(long broadcastId, long commentId) {
        return mLifeStreamService.deleteBroadcastComment(broadcastId, commentId);
    }

    public ApiRequest<Comment> sendBroadcastComment(long broadcastId, String comment) {
        return mLifeStreamService.sendBroadcastComment(broadcastId, comment);
    }

    public ApiRequest<Broadcast> deleteBroadcast(long broadcastId) {
        return mLifeStreamService.deleteBroadcast(broadcastId);
    }

    public ApiRequest<DiaryList> getDiaryList(String userIdOrUid, Integer start, Integer count) {
        // TODO: UserIdOrUidFrodoRequest
        return mFrodoService.getDiaryList(userIdOrUid, start, count);
    }

    public ApiRequest<UserItemList> getUserItemList(String userIdOrUid) {
        // TODO: UserIdOrUidFrodoRequest
        return mFrodoService.getUserItemList(userIdOrUid);
    }

    public ApiRequest<ReviewList> getUserReviewList(String userIdOrUid, Integer start, Integer count) {
        // TODO: UserIdOrUidFrodoRequest
        return mFrodoService.getUserReviewList(userIdOrUid, start, count);
    }

    public ApiRequest<Movie> getMovie(long movieId) {
        return mFrodoService.getMovie(movieId);
    }

    public ApiRequest<ReviewList> getItemReviewList(long itemId, Integer start, Integer count) {
        return mFrodoService.getItemReviewList(itemId, start, count);
    }

    public void cancelApiRequests() {
        mLifeStreamHttpClient.dispatcher().cancelAll();
        mFrodoHttpClient.dispatcher().cancelAll();
    }

    public interface AuthenticationService {

        @POST(ApiContract.Request.Authentication.URL)
        @FormUrlEncoded
        ApiRequest<AuthenticationResponse> authenticate(
                @Header(Http.Headers.USER_AGENT) String userAgent,
                @Header(Http.Headers.ACCEPT_CHARSET) String acceptCharset,
                @Field("client_id") String clientId, @Field("client_secret") String clientSecret,
                @Field("redirect_uri") String redirectUri, @Field("grant_type") String grantType,
                @Field("username") String username, @Field("password") String password);

        @POST(ApiContract.Request.Authentication.URL)
        @FormUrlEncoded
        ApiRequest<AuthenticationResponse> authenticate(
                @Header(Http.Headers.USER_AGENT) String userAgent,
                @Header(Http.Headers.ACCEPT_CHARSET) String acceptCharset,
                @Field("client_id") String clientId, @Field("client_secret") String clientSecret,
                @Field("redirect_uri") String redirectUri, @Field("grant_type") String grantType,
                @Field("refresh_token") String refreshToken);
    }

    public interface LifeStreamService {

        @GET("lifestream/user/{userIdOrUid}")
        ApiRequest<User> getUser(@Path("userIdOrUid") String userIdOrUid);

        @POST("lifestream/user/{userIdOrUid}/follow")
        ApiRequest<User> follow(@Path("userIdOrUid") String userIdOrUid);

        @DELETE("lifestream/user/{userIdOrUid}/follow")
        ApiRequest<User> unfollow(@Path("userIdOrUid") String userIdOrUid);

        @GET("lifestream/user/{userIdOrUid}/followings")
        ApiRequest<UserList> getFollowingList(@Path("userIdOrUid") String userIdOrUid,
                                              @Query("start") Integer start,
                                              @Query("count") Integer count, @Query("tag") String tag);

        @GET("lifestream/user/{userIdOrUid}/followers")
        ApiRequest<UserList> getFollowerList(@Path("userIdOrUid") String userIdOrUid,
                                             @Query("start") Integer start,
                                             @Query("count") Integer count);

        @GET
        ApiRequest<List<Broadcast>> getBroadcastList(@Url String url, @Query("until_id") Long untilId,
                                                     @Query("count") Integer count,
                                                     @Query("q") String topic);

        @GET("lifestream/status/{broadcastId}")
        ApiRequest<Broadcast> getBroadcast(@Path("broadcastId") long broadcastId);

        @GET("lifestream/status/{broadcastId}/comments")
        ApiRequest<CommentList> getBroadcastCommentList(@Path("broadcastId") long broadcastId,
                                                        @Query("start") Integer start,
                                                        @Query("count") Integer count);

        @POST("lifestream/status/{broadcastId}/like")
        ApiRequest<Broadcast> likeBroadcast(@Path("broadcastId") long broadcastId);

        @DELETE("lifestream/status/{broadcastId}/like")
        ApiRequest<Broadcast> unlikeBroadcast(@Path("broadcastId") long broadcastId);

        @POST("lifestream/status/{broadcastId}/reshare")
        ApiRequest<Broadcast> rebroadcastBroadcast(@Path("broadcastId") long broadcastId);

        @DELETE("lifestream/status/{broadcastId}/reshare")
        ApiRequest<Broadcast> unrebroadcastBroadcast(@Path("broadcastId") long broadcastId);

        @GET("lifestream/status/{broadcastId}/likers")
        ApiRequest<List<SimpleUser>> getBroadcastLikerList(@Path("broadcastId") long broadcastId,
                                                           @Query("start") Integer start,
                                                           @Query("count") Integer count);

        @GET("lifestream/status/{broadcastId}/resharers")
        ApiRequest<List<SimpleUser>> getBroadcastRebroadcasterList(@Path("broadcastId") long broadcastId,
                                                                   @Query("start") Integer start,
                                                                   @Query("count") Integer count);

        @DELETE("lifestream/status/{broadcastId}/comment/{commentId}")
        ApiRequest<Boolean> deleteBroadcastComment(@Path("broadcastId") long broadcastId,
                                                   @Path("commentId") long commentId);

        @POST("lifestream/status/{broadcastId}/comments")
        @FormUrlEncoded
        ApiRequest<Comment> sendBroadcastComment(@Path("broadcastId") long broadcastId,
                                                 @Field("text") String comment);

        @DELETE("lifestream/status/{broadcastId}")
        ApiRequest<Broadcast> deleteBroadcast(@Path("broadcastId") long broadcastId);
    }

    public interface FrodoService {

        @GET("mine/notifications")
        ApiRequest<NotificationList> getNotificationList(@Query("start") Integer start,
                                                         @Query("count") Integer count);

        @GET("user/{userIdOrUid}/notes")
        ApiRequest<DiaryList> getDiaryList(@Path("userIdOrUid") String userIdOrUid,
                                           @Query("start") Integer start, @Query("count") Integer count);

        @GET("user/{userIdOrUid}/itemlist")
        ApiRequest<UserItemList> getUserItemList(@Path("userIdOrUid") String userIdOrUid);

        @GET("user/{userIdOrUid}/reviews")
        ApiRequest<ReviewList> getUserReviewList(@Path("userIdOrUid") String userIdOrUid,
                                                 @Query("start") Integer start,
                                                 @Query("count") Integer count);

        @GET("movie/{movieId}")
        ApiRequest<Movie> getMovie(@Path("movieId") long movieId);


        @GET("subject/{itemId}/reviews")
        ApiRequest<ReviewList> getItemReviewList(@Path("itemId") long itemId,
                                                 @Query("start") Integer start,
                                                 @Query("count") Integer count);
    }
}
