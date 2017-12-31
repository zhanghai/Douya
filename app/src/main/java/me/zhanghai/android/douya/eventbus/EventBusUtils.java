/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.eventbus;

import android.os.Handler;

import org.greenrobot.eventbus.EventBus;

public class EventBusUtils {

    private static final EventBus sEventBus = EventBus.builder()
            .strictMethodVerification(true)
            .throwSubscriberException(true)
            .build();

    private EventBusUtils() {}

    public static void register(Object subscriber) {
        sEventBus.register(subscriber);
    }

    public static void unregister(Object subscriber) {
        sEventBus.unregister(subscriber);
    }

    public static void postSync(Object event) {
        sEventBus.post(event);
    }

    public static void postAsync(Object event) {
        new Handler().post(() -> sEventBus.post(event));
    }

    public static void cancel(Object event) {
        sEventBus.cancelEventDelivery(event);
    }
}
