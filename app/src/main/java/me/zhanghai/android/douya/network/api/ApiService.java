/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.util.List;

import me.zhanghai.android.douya.account.info.AccountContract;
import me.zhanghai.android.douya.network.GsonResponseBodyConverterFactory;
import me.zhanghai.android.douya.network.Http;
import me.zhanghai.android.douya.network.api.credential.ApiCredential;
import me.zhanghai.android.douya.network.api.info.AuthenticationResponse;
import me.zhanghai.android.douya.network.api.info.apiv2.User;
import me.zhanghai.android.douya.network.api.info.frodo.Broadcast;
import me.zhanghai.android.douya.network.api.info.frodo.LikerList;
import me.zhanghai.android.douya.network.api.info.frodo.RebroadcastList;
import me.zhanghai.android.douya.network.api.info.frodo.CelebrityList;
import me.zhanghai.android.douya.network.api.info.frodo.CollectableItem;
import me.zhanghai.android.douya.network.api.info.frodo.Comment;
import me.zhanghai.android.douya.network.api.info.frodo.CommentList;
import me.zhanghai.android.douya.network.api.info.frodo.CompleteCollectableItem;
import me.zhanghai.android.douya.network.api.info.frodo.DiaryList;
import me.zhanghai.android.douya.network.api.info.frodo.DoulistList;
import me.zhanghai.android.douya.network.api.info.frodo.DoumailThread;
import me.zhanghai.android.douya.network.api.info.frodo.DoumailThreadList;
import me.zhanghai.android.douya.network.api.info.frodo.ItemAwardList;
import me.zhanghai.android.douya.network.api.info.frodo.ItemCollection;
import me.zhanghai.android.douya.network.api.info.frodo.ItemCollectionList;
import me.zhanghai.android.douya.network.api.info.frodo.ItemCollectionState;
import me.zhanghai.android.douya.network.api.info.frodo.ItemForumTopicList;
import me.zhanghai.android.douya.network.api.info.frodo.NotificationCount;
import me.zhanghai.android.douya.network.api.info.frodo.NotificationList;
import me.zhanghai.android.douya.network.api.info.frodo.PhotoList;
import me.zhanghai.android.douya.network.api.info.frodo.Rating;
import me.zhanghai.android.douya.network.api.info.frodo.ReviewList;
import me.zhanghai.android.douya.network.api.info.frodo.TimelineList;
import me.zhanghai.android.douya.network.api.info.frodo.UploadedImage;
import me.zhanghai.android.douya.network.api.info.frodo.UserItemList;
import me.zhanghai.android.douya.network.api.info.frodo.UserList;
import me.zhanghai.android.douya.util.CollectionUtils;
import me.zhanghai.android.douya.util.StringCompat;
import me.zhanghai.android.douya.util.StringUtils;
import me.zhanghai.android.douya.util.UriUtils;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public class ApiService {

    private static final ApiService sInstance = new ApiService();

    private ApiV2AuthenticationService mApiV2AuthenticationService;
    private FrodoAuthenticationService mFrodoAuthenticationService;
    private OkHttpClient mLifeStreamHttpClient;
    private LifeStreamService mLifeStreamService;
    private OkHttpClient mFrodoHttpClient;
    private FrodoService mFrodoService;

    public static ApiService getInstance() {
        return sInstance;
    }

    private ApiService() {
        mApiV2AuthenticationService = createAuthenticationService(
                ApiContract.Request.Authentication.BaseUrls.API_V2,
                ApiV2AuthenticationService.class);
        mFrodoAuthenticationService = createAuthenticationService(
                ApiContract.Request.Authentication.BaseUrls.FRODO, FrodoAuthenticationService.class,
                new FrodoInterceptor(), new FrodoSignatureInterceptor());
        mLifeStreamHttpClient = createApiHttpClient(AccountContract.AUTH_TOKEN_TYPE_API_V2,
                new LifeStreamInterceptor());
        mLifeStreamService = createApiService(ApiContract.Request.ApiV2.BASE_URL,
                mLifeStreamHttpClient, LifeStreamService.class);
        mFrodoHttpClient = createApiHttpClient(AccountContract.AUTH_TOKEN_TYPE_FRODO,
                new FrodoInterceptor(), new FrodoSignatureInterceptor());
        mFrodoService = createApiService(ApiContract.Request.Frodo.BASE_URL, mFrodoHttpClient,
                FrodoService.class);
    }

    private static OkHttpClient.Builder createHttpClientBuilder(
            Interceptor... networkInterceptors) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        for (Interceptor interceptor : networkInterceptors) {
            builder.addNetworkInterceptor(interceptor);
        }
        return builder
                .addNetworkInterceptor(new StethoInterceptor());
    }

    private static <T> T createAuthenticationService(String baseUrl, Class<T> serviceClass,
                                                     Interceptor... networkInterceptors) {
        return new Retrofit.Builder()
                .addCallAdapterFactory(ApiCallAdapter.Factory.create())
                .addConverterFactory(GsonResponseBodyConverterFactory.create())
                .baseUrl(baseUrl)
                .client(createHttpClientBuilder(networkInterceptors).build())
                .build()
                .create(serviceClass);
    }

    private static OkHttpClient createApiHttpClient(String authTokenType,
                                                    Interceptor... networkInterceptors) {
        return createHttpClientBuilder(networkInterceptors)
                // AuthenticationInterceptor may retry the request, so it must be an application
                // interceptor.
                .addInterceptor(new ApiAuthenticationInterceptor(authTokenType))
                .build();
    }

    private static <T> T createApiService(String baseUrl, OkHttpClient client,
                                          Class<T> serviceClass) {
        return new Retrofit.Builder()
                .addCallAdapterFactory(ApiCallAdapter.Factory.create())
                .addConverterFactory(GsonResponseBodyConverterFactory.create())
                .baseUrl(baseUrl)
                .client(client)
                .build()
                .create(serviceClass);
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

    public ApiRequest<AuthenticationResponse> authenticate(String authTokenType,
                                                           String refreshToken) {
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
        return mApiV2AuthenticationService.authenticate(ApiContract.Request.ApiV2.USER_AGENT,
                ApiContract.Request.Authentication.ACCEPT_CHARSET, ApiCredential.ApiV2.KEY,
                ApiCredential.ApiV2.SECRET, ApiContract.Request.Authentication.RedirectUris.API_V2,
                ApiContract.Request.Authentication.GrantTypes.PASSWORD, username, password);
    }

    private ApiRequest<AuthenticationResponse> authenticateApiV2(String refreshToken) {
        return mApiV2AuthenticationService.authenticate(ApiContract.Request.ApiV2.USER_AGENT,
                ApiContract.Request.Authentication.ACCEPT_CHARSET, ApiCredential.ApiV2.KEY,
                ApiCredential.ApiV2.SECRET, ApiContract.Request.Authentication.RedirectUris.API_V2,
                ApiContract.Request.Authentication.GrantTypes.REFRESH_TOKEN, refreshToken);
    }

    private ApiRequest<AuthenticationResponse> authenticateFrodo(String username, String password) {
        return mFrodoAuthenticationService.authenticate(ApiCredential.Frodo.KEY,
                ApiCredential.Frodo.SECRET, ApiContract.Request.Authentication.RedirectUris.FRODO,
                false, ApiContract.Request.Authentication.GrantTypes.PASSWORD, username, password);
    }

    private ApiRequest<AuthenticationResponse> authenticateFrodo(String refreshToken) {
        return mFrodoAuthenticationService.authenticate(ApiCredential.Frodo.KEY,
                ApiCredential.Frodo.SECRET, ApiContract.Request.Authentication.RedirectUris.FRODO,
                false, ApiContract.Request.Authentication.GrantTypes.PASSWORD, refreshToken);
    }

    public ApiRequest<NotificationCount> getNotificationCount() {
        return mFrodoService.getNotificationCount();
    }

    public ApiRequest<NotificationList> getNotificationList(Integer start, Integer count) {
        return mFrodoService.getNotificationList(start, count);
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
                                                 boolean followersFirst) {
        return mFrodoService.getFollowingList(userIdOrUid, start, count, followersFirst ? "true"
                : null);
    }

    public ApiRequest<UserList> getFollowingList(String userIdOrUid, Integer start, Integer count) {
        return getFollowingList(userIdOrUid, start, count, false);
    }

    public ApiRequest<UserList> getFollowerList(String userIdOrUid, Integer start, Integer count) {
        return mFrodoService.getFollowerList(userIdOrUid, start, count);
    }

    public ApiRequest<List<me.zhanghai.android.douya.network.api.info.apiv2.Broadcast>>
            getApiV2BroadcastList(String userIdOrUid, String topic, Long untilId, Integer count) {
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

    public ApiRequest<TimelineList> getTimelineList(String userIdOrUid, String topic, Long untilId,
                                                    Integer count) {
        return getTimelineList(userIdOrUid, topic, untilId, count, null, false);
    }

    public ApiRequest<TimelineList> getTimelineList(String userIdOrUid, String topic, Long untilId,
                                                    Integer count, Long lastVisitedId,
                                                    boolean guestOnly) {
        String url;
        if (TextUtils.isEmpty(userIdOrUid) && TextUtils.isEmpty(topic)) {
            url = "elendil/home_timeline";
        } else if (TextUtils.isEmpty(topic)) {
            url = StringUtils.formatUs("elendil/user/%s/timeline", userIdOrUid);
        } else {
            url = "status/topic/timeline";
        }
        return mFrodoService.getTimelineList(url, untilId, count, lastVisitedId, topic, guestOnly ?
                1 : null);
    }

    public ApiRequest<UploadedImage> uploadBroadcastImage(Uri uri, Context context) {
        String fileName = UriUtils.getDisplayName(uri, context);
        RequestBody body = new ImageTypeUriRequestBody(uri, context);
        MultipartBody.Part part = MultipartBody.Part.createFormData("image", fileName, body);
        return mFrodoService.uploadBroadcastImage(part);
    }

    public ApiRequest<Broadcast> sendBroadcast(String text, List<String> imageUrls,
                                               String linkTitle, String linkUrl) {
        boolean isImagesEmpty = CollectionUtils.isEmpty(imageUrls);
        if (isImagesEmpty && !TextUtils.isEmpty(linkUrl)) {
            return new ConvertBroadcastApiRequest(mFrodoService.sendBroadcastWithLifeStream(text,
                    null, linkTitle, linkUrl));
        }
        String imageUrlsString = !isImagesEmpty ? StringCompat.join(",", imageUrls) : null;
        return mFrodoService.sendBroadcast(text, imageUrlsString, linkTitle, linkUrl);
    }

    public ApiRequest<Broadcast> getBroadcast(long broadcastId) {
        return mFrodoService.getBroadcast(broadcastId);
    }

    public ApiRequest<CommentList> getBroadcastCommentList(long broadcastId, Integer start,
                                                           Integer count) {
        return mFrodoService.getBroadcastCommentList(broadcastId, start, count);
    }

    public ApiRequest<Broadcast> likeBroadcast(long broadcastId, boolean like) {
        if (like) {
            return mFrodoService.likeBroadcast(broadcastId);
        } else {
            return mFrodoService.unlikeBroadcast(broadcastId);
        }
    }

    public ApiRequest<Broadcast> rebroadcastBroadcast(long broadcastId, String text) {
        return mFrodoService.rebroadcastBroadcast(broadcastId, text);
    }

    public ApiRequest<LikerList> getBroadcastLikerList(long broadcastId, Integer start,
                                                       Integer count) {
        return mFrodoService.getBroadcastLikerList(broadcastId, start, count);
    }

    public ApiRequest<RebroadcastList> getBroadcastRebroadcastList(long broadcastId, Integer start,
                                                                   Integer count) {
        return mFrodoService.getBroadcastRebroadcastList(broadcastId, start, count);
    }

    public ApiRequest<Void> deleteBroadcastComment(long broadcastId, long commentId) {
        return mFrodoService.deleteBroadcastComment(broadcastId, commentId);
    }

    public ApiRequest<Comment> sendBroadcastComment(long broadcastId, String comment) {
        return mFrodoService.sendBroadcastComment(broadcastId, comment);
    }

    public ApiRequest<Void> deleteBroadcast(long broadcastId) {
        return mFrodoService.deleteBroadcast(broadcastId);
    }

    public ApiRequest<DiaryList> getDiaryList(String userIdOrUid, Integer start, Integer count) {
        // TODO: UserIdOrUidFrodoRequest
        return mFrodoService.getDiaryList(userIdOrUid, start, count);
    }

    public ApiRequest<UserItemList> getUserItemList(String userIdOrUid) {
        // TODO: UserIdOrUidFrodoRequest
        return mFrodoService.getUserItemList(userIdOrUid);
    }

    public ApiRequest<ReviewList> getUserReviewList(String userIdOrUid, Integer start,
                                                    Integer count) {
        // TODO: UserIdOrUidFrodoRequest
        return mFrodoService.getUserReviewList(userIdOrUid, start, count);
    }

    public <ItemType> ApiRequest<ItemType> getItem(CollectableItem.Type itemType, long itemId) {
        //noinspection unchecked
        return (ApiRequest<ItemType>) mFrodoService.getItem(itemType.getApiString(), itemId);
    }

    public ApiRequest<ItemCollection> collectItem(CollectableItem.Type itemType, long itemId,
                                                  ItemCollectionState state, int rating,
                                                  List<String> tags, String comment,
                                                  List<Long> gamePlatformIds,
                                                  boolean shareToBroadcast, boolean shareToWeibo,
                                                  boolean shareToWeChatMoments) {
        return mFrodoService.collectItem(itemType.getApiString(), itemId, state.getApiString(),
                rating, StringCompat.join(",", tags), comment, gamePlatformIds,
                shareToBroadcast ? 1 : null, shareToWeibo ? 1 : null,
                shareToWeChatMoments ? 1 : null);
    }

    public ApiRequest<ItemCollection> uncollectItem(CollectableItem.Type itemType, long itemId) {
        return mFrodoService.uncollectItem(itemType.getApiString(), itemId);
    }

    public ApiRequest<Rating> getItemRating(CollectableItem.Type itemType, long itemId) {
        return mFrodoService.getItemRating(itemType.getApiString(), itemId);
    }

    public ApiRequest<PhotoList> getItemPhotoList(CollectableItem.Type itemType, long itemId,
                                                  Integer start, Integer count) {
        return mFrodoService.getItemPhotoList(itemType.getApiString(), itemId, start, count);
    }

    public ApiRequest<CelebrityList> getItemCelebrityList(CollectableItem.Type itemType,
                                                          long itemId) {
        return mFrodoService.getItemCelebrityList(itemType.getApiString(), itemId);
    }

    public ApiRequest<ItemAwardList> getItemAwardList(CollectableItem.Type itemType, long itemId,
                                                      Integer start, Integer count) {
        return mFrodoService.getItemAwardList(itemType.getApiString(), itemId, start, count);
    }

    public ApiRequest<ItemCollectionList> getItemCollectionList(CollectableItem.Type itemType,
                                                                long itemId,
                                                                boolean followingsFirst,
                                                                Integer start, Integer count) {
        return mFrodoService.getItemCollectionList(itemType.getApiString(), itemId,
                followingsFirst ? 1 : null, start, count);
    }

    public ApiRequest<ItemCollection> voteItemCollection(CollectableItem.Type itemType, long itemId,
                                                         long itemCollectionId) {
        return mFrodoService.voteItemCollection(itemType.getApiString(), itemId, itemCollectionId);
    }

    public ApiRequest<ReviewList> getItemReviewList(CollectableItem.Type itemType, long itemId,
                                                    Integer start, Integer count) {
        return mFrodoService.getItemReviewList(itemType.getApiString(), itemId, null, start, count);
    }

    public ApiRequest<ReviewList> getGameGuideList(long itemId, Integer start, Integer count) {
        return mFrodoService.getItemReviewList(CollectableItem.Type.GAME.getApiString(), itemId,
                "guide", start, count);
    }

    public ApiRequest<ItemForumTopicList> getItemForumTopicList(CollectableItem.Type itemType,
                                                                long itemId, Integer episode,
                                                                Integer start, Integer count) {
        return mFrodoService.getItemForumTopicList(itemType.getApiString(), itemId, episode, start,
                count);
    }

    public ApiRequest<List<CollectableItem>> getItemRecommendationList(
            CollectableItem.Type itemType, long itemId, Integer count) {
        return mFrodoService.getItemRecommendationList(itemType.getApiString(), itemId, count);
    }

    public ApiRequest<DoulistList> getItemRelatedDoulistList(CollectableItem.Type itemType,
                                                             long itemId, Integer start,
                                                             Integer count) {
        return mFrodoService.getItemRelatedDoulistList(itemType.getApiString(), itemId, start,
                count);
    }

    public ApiRequest<DoumailThreadList> getDoumailThreadList(Integer start, Integer count) {
        return mFrodoService.getDoumailThreadList(start, count);
    }

    public ApiRequest<DoumailThread> getDoumailThread(long userId) {
        return mFrodoService.getDoumailThread(userId);
    }

    public void cancelApiRequests() {
        mLifeStreamHttpClient.dispatcher().cancelAll();
        mFrodoHttpClient.dispatcher().cancelAll();
    }

    public interface ApiV2AuthenticationService {

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

    public interface FrodoAuthenticationService {

        @POST(ApiContract.Request.Authentication.URL)
        @FormUrlEncoded
        ApiRequest<AuthenticationResponse> authenticate(
                @Field("client_id") String clientId, @Field("client_secret") String clientSecret,
                @Field("redirect_uri") String redirectUri,
                @Field("disable_account_create") Boolean disableAccountCreation,
                @Field("grant_type") String grantType, @Field("username") String username,
                @Field("password") String password);

        @POST(ApiContract.Request.Authentication.URL)
        @FormUrlEncoded
        ApiRequest<AuthenticationResponse> authenticate(
                @Field("client_id") String clientId, @Field("client_secret") String clientSecret,
                @Field("redirect_uri") String redirectUri,
                @Field("disable_account_create") Boolean disableAccountCreation,
                @Field("grant_type") String grantType, @Field("refresh_token") String refreshToken);
    }

    public interface LifeStreamService {

        @GET("lifestream/user/{userIdOrUid}")
        ApiRequest<User> getUser(@Path("userIdOrUid") String userIdOrUid);

        @POST("lifestream/user/{userIdOrUid}/follow")
        ApiRequest<User> follow(@Path("userIdOrUid") String userIdOrUid);

        @DELETE("lifestream/user/{userIdOrUid}/follow")
        ApiRequest<User> unfollow(@Path("userIdOrUid") String userIdOrUid);

        @GET("lifestream/user/{userIdOrUid}/followings")
        ApiRequest<me.zhanghai.android.douya.network.api.info.apiv2.UserList> getFollowingList(
                @Path("userIdOrUid") String userIdOrUid, @Query("start") Integer start,
                @Query("count") Integer count, @Query("tag") String tag);

        @GET("lifestream/user/{userIdOrUid}/followers")
        ApiRequest<me.zhanghai.android.douya.network.api.info.apiv2.UserList> getFollowerList(
                @Path("userIdOrUid") String userIdOrUid, @Query("start") Integer start,
                @Query("count") Integer count);

        @POST("lifestream/statuses")
        @FormUrlEncoded
        ApiRequest<me.zhanghai.android.douya.network.api.info.apiv2.Broadcast> sendBroadcast(
                @Field("text") String text, @Field("image_urls") String imageUrls,
                @Field("rec_title") String linkTitle, @Field("rec_url") String linkUrl);

        @GET
        ApiRequest<List<me.zhanghai.android.douya.network.api.info.apiv2.Broadcast>>
                getBroadcastList(@Url String url, @Query("until_id") Long untilId,
                                 @Query("count") Integer count, @Query("q") String topic);

        @GET("lifestream/status/{broadcastId}")
        ApiRequest<me.zhanghai.android.douya.network.api.info.apiv2.Broadcast> getBroadcast(
                @Path("broadcastId") long broadcastId);

        @GET("lifestream/status/{broadcastId}/comments")
        ApiRequest<me.zhanghai.android.douya.network.api.info.apiv2.CommentList>
                getBroadcastCommentList(@Path("broadcastId") long broadcastId,
                                        @Query("start") Integer start,
                                        @Query("count") Integer count);

        @POST("lifestream/status/{broadcastId}/like")
        ApiRequest<me.zhanghai.android.douya.network.api.info.apiv2.Broadcast> likeBroadcast(
                @Path("broadcastId") long broadcastId);

        @DELETE("lifestream/status/{broadcastId}/like")
        ApiRequest<me.zhanghai.android.douya.network.api.info.apiv2.Broadcast> unlikeBroadcast(
                @Path("broadcastId") long broadcastId);

        @POST("lifestream/status/{broadcastId}/reshare")
        ApiRequest<me.zhanghai.android.douya.network.api.info.apiv2.Broadcast> rebroadcastBroadcast(
                @Path("broadcastId") long broadcastId);

        @DELETE("lifestream/status/{broadcastId}/reshare")
        ApiRequest<me.zhanghai.android.douya.network.api.info.apiv2.Broadcast>
                unrebroadcastBroadcast(@Path("broadcastId") long broadcastId);

        @GET("lifestream/status/{broadcastId}/likers")
        ApiRequest<List<me.zhanghai.android.douya.network.api.info.apiv2.SimpleUser>>
                getBroadcastLikerList(@Path("broadcastId") long broadcastId,
                                      @Query("start") Integer start, @Query("count") Integer count);

        @GET("lifestream/status/{broadcastId}/resharers")
        ApiRequest<List<me.zhanghai.android.douya.network.api.info.apiv2.SimpleUser>>
                getBroadcastRebroadcasterList(@Path("broadcastId") long broadcastId,
                                              @Query("start") Integer start,
                                              @Query("count") Integer count);

        @DELETE("lifestream/status/{broadcastId}/comment/{commentId}")
        ApiRequest<Boolean> deleteBroadcastComment(@Path("broadcastId") long broadcastId,
                                                   @Path("commentId") long commentId);

        @POST("lifestream/status/{broadcastId}/comments")
        @FormUrlEncoded
        ApiRequest<me.zhanghai.android.douya.network.api.info.apiv2.Comment> sendBroadcastComment(
                @Path("broadcastId") long broadcastId, @Field("text") String comment);

        @DELETE("lifestream/status/{broadcastId}")
        ApiRequest<me.zhanghai.android.douya.network.api.info.apiv2.Broadcast> deleteBroadcast(
                @Path("broadcastId") long broadcastId);
    }

    public interface FrodoService {

        @GET("notification_chart")
        ApiRequest<NotificationCount> getNotificationCount();

        @GET("mine/notifications")
        ApiRequest<NotificationList> getNotificationList(@Query("start") Integer start,
                                                         @Query("count") Integer count);

        @GET("user/{userIdOrUid}/following")
        ApiRequest<UserList> getFollowingList(@Path("userIdOrUid") String userIdOrUid,
                                              @Query("start") Integer start,
                                              @Query("count") Integer count,
                                              @Query("contact_prior") String followersFirst);

        @GET("user/{userIdOrUid}/followers")
        ApiRequest<UserList> getFollowerList(@Path("userIdOrUid") String userIdOrUid,
                                             @Query("start") Integer start,
                                             @Query("count") Integer count);

        @GET
        ApiRequest<TimelineList> getTimelineList(@Url String url,
                                                 @Query("max_id") Long untilId,
                                                 @Query("count") Integer count,
                                                 @Query("last_visit_id") Long lastVisitedId,
                                                 @Query("name") String topic,
                                                 @Query("guest_only") Integer guestOnly);

        @POST("status/upload")
        @Multipart
        ApiRequest<UploadedImage> uploadBroadcastImage(@Part MultipartBody.Part part);

        @POST("status/create_status")
        @FormUrlEncoded
        ApiRequest<Broadcast> sendBroadcast(@Field("text") String text,
                                            @Field("image_urls") String imageUrls,
                                            @Field("rec_title") String linkTitle,
                                            @Field("rec_url") String linkUrl);

        @POST("https://api.douban.com/v2/lifestream/statuses")
        @FormUrlEncoded
        ApiRequest<me.zhanghai.android.douya.network.api.info.apiv2.Broadcast>
        sendBroadcastWithLifeStream(@Field("text") String text,
                                    @Field("image_urls") String imageUrls,
                                    @Field("rec_title") String linkTitle,
                                    @Field("rec_url") String linkUrl);

        @GET("status/{broadcastId}")
        ApiRequest<Broadcast> getBroadcast(@Path("broadcastId") long broadcastId);

        @GET("status/{broadcastId}/likers")
        ApiRequest<LikerList> getBroadcastLikerList(@Path("broadcastId") long broadcastId,
                                                    @Query("start") Integer start,
                                                    @Query("count") Integer count);

        @GET("status/{broadcastId}/resharers_statuses")
        ApiRequest<RebroadcastList> getBroadcastRebroadcastList(
                @Path("broadcastId") long broadcastId, @Query("start") Integer start,
                @Query("count") Integer count);

        @POST("status/{broadcastId}/like")
        ApiRequest<Broadcast> likeBroadcast(@Path("broadcastId") long broadcastId);

        @POST("status/{broadcastId}/unlike")
        ApiRequest<Broadcast> unlikeBroadcast(@Path("broadcastId") long broadcastId);

        @POST("status/{broadcastId}/reshare")
        @FormUrlEncoded
        ApiRequest<Broadcast> rebroadcastBroadcast(@Path("broadcastId") long broadcastId,
                                                   @Field("text") String text);

        @POST("status/{broadcastId}/report")
        @FormUrlEncoded
        ApiRequest<Void> reportBroadcast(@Path("broadcastId") long broadcastId,
                                         @Field("reason") int reason);

        @POST("status/{broadcastId}/delete")
        ApiRequest<Void> deleteBroadcast(@Path("broadcastId") long broadcastId);

        @GET("status/{broadcastId}/comments")
        ApiRequest<CommentList> getBroadcastCommentList(@Path("broadcastId") long broadcastId,
                                                        @Query("start") Integer start,
                                                        @Query("count") Integer count);

        @POST("status/{broadcastId}/create_comment")
        @FormUrlEncoded
        ApiRequest<Comment> sendBroadcastComment(@Path("broadcastId") long broadcastId,
                                                 @Field("text") String text);

        @POST("status/{broadcastId}/report_unfriendly_comment")
        @FormUrlEncoded
        ApiRequest<Comment> reportBroadcastComment(@Path("broadcastId") long broadcastId,
                                                   @Field("comment_id") long commentId);

        @POST("status/{broadcastId}/delete_comment")
        @FormUrlEncoded
        ApiRequest<Void> deleteBroadcastComment(@Path("broadcastId") long broadcastId,
                                                @Field("comment_id") long commentId);

        @GET("user/{userIdOrUid}/notes")
        ApiRequest<DiaryList> getDiaryList(@Path("userIdOrUid") String userIdOrUid,
                                           @Query("start") Integer start,
                                           @Query("count") Integer count);

        @GET("user/{userIdOrUid}/itemlist")
        ApiRequest<UserItemList> getUserItemList(@Path("userIdOrUid") String userIdOrUid);

        @GET("user/{userIdOrUid}/reviews")
        ApiRequest<ReviewList> getUserReviewList(@Path("userIdOrUid") String userIdOrUid,
                                                 @Query("start") Integer start,
                                                 @Query("count") Integer count);

        @GET("{itemType}/{itemId}")
        ApiRequest<CompleteCollectableItem> getItem(@Path("itemType") String itemType,
                                                    @Path("itemId") long itemId);

        @POST("{itemType}/{itemId}/{state}")
        @FormUrlEncoded
        ApiRequest<ItemCollection> collectItem(@Path("itemType") String itemType,
                                               @Path("itemId") long itemId,
                                               @Path("state") String state,
                                               @Field("rating") int rating,
                                               @Field("tags") String tags,
                                               @Field("comment") String comment,
                                               @Field("platform") List<Long> gamePlatformIds,
                                               @Field("sync_douban") Integer shareToBroadcast,
                                               @Field("sync_weibo") Integer shareToWeibo,
                                               @Field("sync_wechat_timeline") Integer shareToWeChatMoments);

        @POST("{itemType}/{itemId}/unmark")
        ApiRequest<ItemCollection> uncollectItem(@Path("itemType") String itemType,
                                                 @Path("itemId") long itemId);

        @GET("{itemType}/{itemId}/rating")
        ApiRequest<Rating> getItemRating(@Path("itemType") String itemType,
                                         @Path("itemId") long itemId);

        @GET("{itemType}/{itemId}/photos")
        ApiRequest<PhotoList> getItemPhotoList(@Path("itemType") String itemType,
                                               @Path("itemId") long itemId,
                                               @Query("start") Integer start,
                                               @Query("count") Integer count);

        @GET("{itemType}/{itemId}/celebrities")
        ApiRequest<CelebrityList> getItemCelebrityList(@Path("itemType") String itemType,
                                                       @Path("itemId") long itemId);

        @GET("{itemType}/{itemId}/awards")
        ApiRequest<ItemAwardList> getItemAwardList(@Path("itemType") String itemType,
                                                   @Path("itemId") long itemId,
                                                   @Query("start") Integer start,
                                                   @Query("count") Integer count);

        @GET("{itemType}/{itemId}/interests")
        ApiRequest<ItemCollectionList> getItemCollectionList(
                @Path("itemType") String itemType, @Path("itemId") long itemId,
                @Query("following") Integer followingsFirst, @Query("start") Integer start,
                @Query("count") Integer count);

        @POST("{itemType}/{itemId}/vote_interest")
        @FormUrlEncoded
        ApiRequest<ItemCollection> voteItemCollection(@Path("itemType") String itemType,
                                                      @Path("itemId") long itemId,
                                                      @Field("interest_id") long itemCollectionId);

        @GET("{itemType}/{itemId}/forum_topics")
        ApiRequest<ItemForumTopicList> getItemForumTopicList(@Path("itemType") String itemType,
                                                             @Path("itemId") long itemId,
                                                             @Query("episode") Integer episode,
                                                             @Query("start") Integer start,
                                                             @Query("count") Integer count);

        @GET("{itemType}/{itemId}/reviews")
        ApiRequest<ReviewList> getItemReviewList(@Path("itemType") String itemType,
                                                 @Path("itemId") long itemId,
                                                 @Query("rtype") String reviewType,
                                                 @Query("start") Integer start,
                                                 @Query("count") Integer count);

        @GET("{itemType}/{itemId}/recommendations")
        ApiRequest<List<CollectableItem>> getItemRecommendationList(
                @Path("itemType") String itemType, @Path("itemId") long itemId,
                @Query("count") Integer count);

        @GET("{itemType}/{itemId}/related_doulists")
        ApiRequest<DoulistList> getItemRelatedDoulistList(@Path("itemType") String itemType,
                                                          @Path("itemId") long itemId,
                                                          @Query("start") Integer start,
                                                          @Query("count") Integer count);

        @GET("chat_list")
        ApiRequest<DoumailThreadList> getDoumailThreadList(@Query("start") Integer start,
                                                           @Query("count") Integer count);

        @GET("user/{userId}/chat")
        ApiRequest<DoumailThread> getDoumailThread(@Path("userId") long userId);
    }

    private static class ConvertBroadcastApiRequest extends ConvertApiRequest<
            me.zhanghai.android.douya.network.api.info.apiv2.Broadcast, Broadcast> {

        public ConvertBroadcastApiRequest(
                ApiRequest<me.zhanghai.android.douya.network.api.info.apiv2.Broadcast> request) {
            super(request);
        }

        @Override
        protected Broadcast transform(
                me.zhanghai.android.douya.network.api.info.apiv2.Broadcast responseBody) {
            Broadcast broadcast = responseBody.toFrodo();
            // Can contain "分享" instead of "推荐".
            broadcast.fix();
            return broadcast;
        }
    }
}
