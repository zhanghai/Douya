/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import me.zhanghai.android.douya.util.IoUtils;
import me.zhanghai.android.douya.util.LogUtils;
import me.zhanghai.android.douya.util.StandardCharsetsCompat;

interface ApiCredential {

    interface Douya {
        String KEY = Frodo.KEY;
        String SECRET = Frodo.SECRET;
    }

    interface Frodo {
        String KEY = HackyApiCredentialHelper.loadStringFromFile(new File(
                Environment.getExternalStorageDirectory(), "Douya/API_KEY"));
        String SECRET = HackyApiCredentialHelper.loadStringFromFile(new File(
                Environment.getExternalStorageDirectory(), "Douya/API_SECRET"));
    }
}

class HackyApiCredentialHelper {

    public static String loadStringFromFile(File file) {
        if (!file.exists()) {
            LogUtils.e("File " + file + " does not exist.");
            return "";
        }
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            return IoUtils.inputStreamToString(fileInputStream, StandardCharsetsCompat.UTF_8.name())
                    .trim();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } finally {
            if (fileInputStream != null) {
                IoUtils.close(fileInputStream);
            }
        }
    }
}
