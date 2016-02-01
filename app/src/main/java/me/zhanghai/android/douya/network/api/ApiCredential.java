/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api;

interface ApiCredential {

    interface Douya {
        String KEY = Frodo.KEY;
        String SECRET = Frodo.SECRET;
    }

    interface Frodo {
        String KEY = "<KEY>";
        String SECRET = "<SECRET>";
    }
}
