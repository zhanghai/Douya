/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network;

import java.io.IOException;

public class ResponseConversionException extends IOException {

    public ResponseConversionException() {}

    public ResponseConversionException(String message) {
        super(message);
    }

    public ResponseConversionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResponseConversionException(Throwable cause) {
        super(cause);
    }
}
