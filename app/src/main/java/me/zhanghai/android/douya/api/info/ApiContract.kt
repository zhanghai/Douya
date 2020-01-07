/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.info

import android.os.Build
import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object ApiContract {

    object Credential {

        val KEY: String

        val SECRET: String

        init {
            val cipher = Cipher.getInstance("AES/CBC/NoPadding")
            val key = SecretKeySpec("MIICUjCCAbsCBEty".toByteArray(Charsets.UTF_8), "AES")
            val parameters = IvParameterSpec("DOUBANFRODOAPPIV".toByteArray(Charsets.UTF_8))
            cipher.init(Cipher.DECRYPT_MODE, key, parameters)
            val keyInput = Base64.decode(
                "74CwfJd4+7LYgFhXi1cx0IQC35UQqYVFycCE+EVyw1E=", Base64.DEFAULT
            )
            KEY = cipher.doFinal(keyInput).toString(Charsets.UTF_8)
            val secretInput = Base64.decode("MkFm2XdTnoPKFKXu1gveBQ==", Base64.DEFAULT)
            SECRET = cipher.doFinal(secretInput).toString(Charsets.UTF_8)
        }
    }

    object Error {

        object Codes {

            object Token {
                const val INVALID_ACCESS_TOKEN = 103
                const val ACCESS_TOKEN_HAS_EXPIRED = 106
                const val INVALID_REFRESH_TOKEN = 119
                const val USERNAME_PASSWORD_MISMATCH = 120
                const val ACCESS_TOKEN_HAS_EXPIRED_SINCE_PASSWORD_CHANGED = 123
            }
        }
    }

    object Authentication {

        const val BASE_URL = "https://frodo.douban.com/"

        const val URL = "service/auth2/token"

        const val REDIRECT_URI = "frodo://app/oauth/callback/"

        object GrantTypes {
            const val PASSWORD = "password"
            const val REFRESH_TOKEN = "refresh_token"
        }
    }

    object Api {

        const val MAX_AUTH_RETRIES = 2

        const val BASE_URL = "https://frodo.douban.com/api/v2/"

        // API protocol version is derived from user agent string.
        val USER_AGENT = "api-client/1 com.douban.frodo/6.0.1(138) Android/" +
            Build.VERSION.SDK_INT + " product/" + Build.PRODUCT + " vendor/" +
            // Sorry Frodo, but we don't want to request ACCESS_NETWORK_STATE.
            Build.MANUFACTURER + " model/" + Build.MODEL + "  rom/android  network/wifi"

        const val API_KEY = "apikey"

        const val CHANNEL = "channel"
        object Channels {
            const val DOUBAN = "Douban"
        }

        const val UDID = "udid"

        const val OS_ROM = "os_rom"
        object OsRoms {
            const val ANDROID = "android"
        }

        val SIGNATURE_HOSTS = listOf("frodo.douban.com", "api.douban.com")
        const val SIG = "_sig"
        const val TS = "_ts"
    }
}
