/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.credential;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class ApiCredential {

    private static final String PASSWORD = "MIICUjCCAbsCBEty";

    public static final String API_V2_KEY;
    public static final String API_V2_SECRET;
    public static final String FRODO_KEY;
    public static final String FRODO_SECRET;

    static {
        try {
            SecretKeySpec key = new SecretKeySpec(PASSWORD.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(
                    "DOUBANFRODOAPPIV".getBytes()));
            API_V2_KEY = new String(cipher.doFinal(Base64.decode(
                    "+H/RVIwKFXHqNsb6bnXFlRIH0Y9GCqPQO/38NgzTt3g=", Base64.DEFAULT)));
            API_V2_SECRET = new String(cipher.doFinal(Base64.decode("hTwIRVgPq1BS/Olwtv4Vfg==",
                    Base64.DEFAULT)));
            FRODO_KEY = new String(cipher.doFinal(Base64.decode(
                    "74CwfJd4+7LYgFhXi1cx0IQC35UQqYVFycCE+EVyw1E=", Base64.DEFAULT)));
            FRODO_SECRET = new String(cipher.doFinal(Base64.decode("MkFm2XdTnoPKFKXu1gveBQ==",
                    Base64.DEFAULT)));
        } catch (BadPaddingException | IllegalBlockSizeException
                | InvalidAlgorithmParameterException | InvalidKeyException
                | NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new AssertionError(e);
        }
    }

    private ApiCredential() {}
}
