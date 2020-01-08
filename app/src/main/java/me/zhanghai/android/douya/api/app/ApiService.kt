/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.app

import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import me.zhanghai.android.douya.R
import me.zhanghai.android.douya.api.info.ApiContract
import me.zhanghai.android.douya.api.info.AuthenticationResponse
import me.zhanghai.android.douya.api.info.ErrorResponse
import me.zhanghai.android.douya.appContext
import me.zhanghai.android.douya.network.AuthenticationException
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import timber.log.Timber
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object ApiService {

    private val converterFactory = MoshiConverterFactory.create().withNullSerialization()

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "UNCHECKED_CAST")
    private val errorConverter: Converter<ResponseBody, ErrorResponse?> =
        converterFactory.responseBodyConverter(ErrorResponse::class.java, arrayOf(), null)!!
            as Converter<ResponseBody, ErrorResponse?>

    private val authenticationService = Retrofit.Builder()
        .client(OkHttpClient.Builder()
            .addNetworkInterceptor(ApiKeyInterceptor())
            .addNetworkInterceptor(ApiSignatureInterceptor())
            //.addNetworkInterceptor(StethoInterceptor())
            .build())
        .baseUrl(ApiContract.Authentication.BASE_URL)
        .addConverterFactory(converterFactory)
        .build()
        .create(AuthenticationService::class.java)

    private val apiHttpClient = OkHttpClient.Builder()
        .addNetworkInterceptor(ApiKeyInterceptor())
        .addNetworkInterceptor(ApiSignatureInterceptor())
        //.addNetworkInterceptor(StethoInterceptor())
        .addInterceptor(ApiAuthenticationInterceptor())
        .build()

    private val apiService = Retrofit.Builder()
        .client(apiHttpClient)
        .baseUrl(ApiContract.Api.BASE_URL)
        .addConverterFactory(converterFactory)
        .build()
        .create(ApiService::class.java)

    fun errorResponse(body: ResponseBody): ErrorResponse? = try {
        errorConverter.convert(body)
    } catch (e: Exception) {
        Timber.e(e)
        null
    }

    fun errorResponse(exception: HttpException): ErrorResponse? {
        val body = exception.response()?.errorBody() ?: return null
        return errorResponse(body)
    }

    fun errorMessage(exception: Exception): String =
        if (exception is HttpException) {
            val errorResponse = errorResponse(exception)
            errorResponse?.localizedMessage
                ?: errorResponse?.message
                ?: appContext.getString(R.string.error_message_parse)
        } else {
            when (exception) {
                is JsonDataException, is JsonEncodingException, is KotlinNullPointerException ->
                    appContext.getString(R.string.error_message_parse)
                is AuthenticationException ->
                    appContext.getString(R.string.error_message_authentication)
                is SocketTimeoutException -> appContext.getString(R.string.error_message_timeout)
                is UnknownHostException ->
                    appContext.getString(R.string.error_message_no_connection)
                is IOException -> appContext.getString(R.string.error_message_network)
                else -> exception.toString()
            }
        }

    fun errorMessage(errorResponse: ErrorResponse) =
        if (errorResponse.localizedMessage != null) {
            errorResponse.localizedMessage
        } else {
            errorResponse.message
        }

    fun cancelApiRequests() = apiHttpClient.dispatcher.cancelAll()

    suspend fun authenticate(username: String, password: String) =
        authenticationService.authenticate(
            ApiContract.Credential.KEY, ApiContract.Credential.SECRET,
            ApiContract.Authentication.REDIRECT_URI, false,
            ApiContract.Authentication.GrantTypes.PASSWORD, username, password
        )

    suspend fun authenticate(refreshToken: String) = authenticationService.authenticate(
        ApiContract.Credential.KEY, ApiContract.Credential.SECRET,
        ApiContract.Authentication.REDIRECT_URI, false,
        ApiContract.Authentication.GrantTypes.REFRESH_TOKEN, refreshToken
    )

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
        ): AuthenticationResponse

        @POST(ApiContract.Authentication.URL)
        @FormUrlEncoded
        suspend fun authenticate(
            @Field("client_id") clientId: String,
            @Field("client_secret") clientSecret: String,
            @Field("redirect_uri") redirectUri: String,
            @Field("disable_account_create") disableAccountCreation: Boolean,
            @Field("grant_type") grantType: String,
            @Field("refresh_token") refreshToken: String
        ): AuthenticationResponse
    }

    private interface ApiService
}
