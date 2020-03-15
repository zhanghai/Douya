/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.app

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.squareup.moshi.Moshi
import me.zhanghai.android.douya.api.info.ApiContract
import me.zhanghai.android.douya.api.info.ErrorResponse
import me.zhanghai.android.douya.api.info.Session
import me.zhanghai.android.douya.api.info.Timeline
import me.zhanghai.android.douya.api.info.User
import me.zhanghai.android.douya.network.DoubanZonedDateTimeAdapter
import me.zhanghai.android.douya.network.EmptyObjectToNullJsonAdapter
import me.zhanghai.android.douya.network.NullToEmptyStringOrCollectionJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import org.threeten.bp.ZonedDateTime
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

object ApiService {
    private val converterFactory = MoshiConverterFactory.create(
        Moshi.Builder()
            .add(NullToEmptyStringOrCollectionJsonAdapterFactory)
            .add(EmptyObjectToNullJsonAdapter.Factory)
            .add(ZonedDateTime::class.java, DoubanZonedDateTimeAdapter)
            .build()
    )
        .withNullSerialization()

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "UNCHECKED_CAST")
    internal val errorResponseConverter: Converter<ResponseBody, ErrorResponse?> =
        converterFactory.responseBodyConverter(ErrorResponse::class.java, arrayOf(), null)!!
            as Converter<ResponseBody, ErrorResponse?>

    private val authenticationService = Retrofit.Builder()
        .client(
            OkHttpClient.Builder()
                .addNetworkInterceptor(ApiKeyInterceptor())
                .addNetworkInterceptor(ApiSignatureInterceptor())
                .addNetworkInterceptor(StethoInterceptor())
                .build()
        )
        .baseUrl(ApiContract.Authentication.BASE_URL)
        .addConverterFactory(converterFactory)
        .build()
        .create(AuthenticationService::class.java)

    private val apiHttpClient = OkHttpClient.Builder()
        .addInterceptor(ApiAuthenticationInterceptor())
        .addNetworkInterceptor(ApiKeyInterceptor())
        .addNetworkInterceptor(ApiSignatureInterceptor())
        .addNetworkInterceptor(StethoInterceptor())
        .build()

    private val apiService = Retrofit.Builder()
        .client(apiHttpClient)
        .baseUrl(ApiContract.Api.BASE_URL)
        .addConverterFactory(converterFactory)
        .build()
        .create(ApiService::class.java)

    fun cancelApiRequests() {
        apiHttpClient.dispatcher.cancelAll()
    }

    suspend fun authenticate(username: String, password: String): Session =
        authenticationService.authenticate(
            ApiContract.Credential.KEY, ApiContract.Credential.SECRET,
            ApiContract.Authentication.REDIRECT_URI, false,
            ApiContract.Authentication.GrantTypes.PASSWORD, username, password
        )

    suspend fun authenticate(refreshToken: String): Session =
        authenticationService.authenticate(
            ApiContract.Credential.KEY, ApiContract.Credential.SECRET,
            ApiContract.Authentication.REDIRECT_URI, false,
            ApiContract.Authentication.GrantTypes.REFRESH_TOKEN, refreshToken
        )

    suspend fun getHomeTimeline(maxId: String? = null): Timeline = apiService.getHomeTimeline(maxId)

    suspend fun getUser(userId: String): User = apiService.getUser(userId)

    suspend fun getUserTimeline(userId: String, maxId: String? = null): Timeline =
        apiService.getUserTimeline(userId, maxId = maxId)

    private interface AuthenticationService {
        @POST(ApiContract.Authentication.URL)
        @FormUrlEncoded
        suspend fun authenticate(
            @Field("client_id") clientId: String,
            @Field("client_secret") clientSecret: String,
            @Field("redirect_uri") redirectUri: String,
            @Field("disable_account_create") disableAccountCreation: Boolean,
            @Field("grant_type") grantType: String,
            @Field("username") username: String,
            @Field("password") password: String
        ): Session

        @POST(ApiContract.Authentication.URL)
        @FormUrlEncoded
        suspend fun authenticate(
            @Field("client_id") clientId: String,
            @Field("client_secret") clientSecret: String,
            @Field("redirect_uri") redirectUri: String,
            @Field("disable_account_create") disableAccountCreation: Boolean,
            @Field("grant_type") grantType: String,
            @Field("refresh_token") refreshToken: String
        ): Session
    }

    private interface ApiService {
        @GET("elendil/home_timeline")
        suspend fun getHomeTimeline(
            @Query("max_id") maxId: String? = null,
            @Query("count") count: Int = 20,
            @Query("last_visit_id") lastVisitId: String? = null,
            @Query("ad_ids") adIds: String? = null
        ): Timeline

        @GET("user/{userId}")
        suspend fun getUser(@Path("userId") userId: String): User

        @GET("elendil/user/{userId}/timeline")
        suspend fun getUserTimeline(
            @Path("userId") userId: String,
            @Query("count") count: Int = 15,
            @Query("max_id") maxId: String? = null
        ): Timeline
    }
}
