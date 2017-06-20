/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.effortlesspermissions;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class EffortlessPermissions {

    private static final String TAG = EffortlessPermissions.class.getSimpleName();

    private EffortlessPermissions() {}

    public static boolean hasPermissions(Context context, @NonNull String... permissions) {
        return EasyPermissions.hasPermissions(context, permissions);
    }

    public static boolean hasPermissions(Fragment fragment, @NonNull String... permissions) {
        return EasyPermissions.hasPermissions(fragment.getContext(), permissions);
    }

    public static boolean hasPermissions(android.app.Fragment fragment,
                                         @NonNull String... permissions) {
        Context context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context = fragment.getContext();
        } else {
            context = fragment.getActivity();
        }
        return EasyPermissions.hasPermissions(context, permissions);
    }

    public static void requestPermissions(@NonNull Activity host, @NonNull String rationale,
                                          int requestCode, @NonNull String... permissions) {
        EasyPermissions.requestPermissions(host, rationale, requestCode, permissions);
    }

    public static void requestPermissions(@NonNull Fragment host, @NonNull String rationale,
                                          int requestCode, @NonNull String... permissions) {
        EasyPermissions.requestPermissions(host, rationale, requestCode, permissions);
    }

    public static void requestPermissions(@NonNull android.app.Fragment host,
                                          @NonNull String rationale, int requestCode,
                                          @NonNull String... permissions) {
        EasyPermissions.requestPermissions(host, rationale, requestCode, permissions);
    }

    public static void requestPermissions(@NonNull Activity host, @NonNull String rationale,
                                          @StringRes int positiveButton,
                                          @StringRes int negativeButton, int requestCode,
                                          @NonNull String... permissions) {
        EasyPermissions.requestPermissions(host, rationale, positiveButton, negativeButton,
                requestCode, permissions);
    }

    public static void requestPermissions(@NonNull Fragment host, @NonNull String rationale,
                                          @StringRes int positiveButton,
                                          @StringRes int negativeButton, int requestCode,
                                          @NonNull String... permissions) {
        EasyPermissions.requestPermissions(host, rationale, positiveButton, negativeButton,
                requestCode, permissions);
    }

    public static void requestPermissions(@NonNull android.app.Fragment host,
                                          @NonNull String rationale, @StringRes int positiveButton,
                                          @StringRes int negativeButton, int requestCode,
                                          @NonNull String... permissions) {
        EasyPermissions.requestPermissions(host, rationale, positiveButton, negativeButton,
                requestCode, permissions);
    }

    public static void requestPermissions(@NonNull Activity host, @StringRes int rationale,
                                          int requestCode, @NonNull String... permissions) {
        requestPermissions(host, host.getString(rationale), requestCode, permissions);
    }

    public static void requestPermissions(@NonNull Fragment host, @StringRes int rationale,
                                          int requestCode, @NonNull String... permissions) {
        requestPermissions(host, host.getString(rationale), requestCode, permissions);
    }

    public static void requestPermissions(@NonNull android.app.Fragment host,
                                          @StringRes int rationale, int requestCode,
                                          @NonNull String... permissions) {
        requestPermissions(host, host.getString(rationale), requestCode, permissions);
    }

    public static void requestPermissions(@NonNull Activity host, @StringRes int rationale,
                                          @StringRes int positiveButton,
                                          @StringRes int negativeButton, int requestCode,
                                          @NonNull String... permissions) {
        requestPermissions(host, host.getString(rationale), positiveButton, negativeButton,
                requestCode, permissions);
    }

    public static void requestPermissions(@NonNull Fragment host, @StringRes int rationale,
                                          @StringRes int positiveButton,
                                          @StringRes int negativeButton, int requestCode,
                                          @NonNull String... permissions) {
        requestPermissions(host, host.getString(rationale), positiveButton, negativeButton,
                requestCode, permissions);
    }

    public static void requestPermissions(@NonNull android.app.Fragment host,
                                          @StringRes int rationale, @StringRes int positiveButton,
                                          @StringRes int negativeButton, int requestCode,
                                          @NonNull String... permissions) {
        requestPermissions(host, host.getString(rationale), positiveButton, negativeButton,
                requestCode, permissions);
    }

    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                  @NonNull int[] grantResults,
                                                  @NonNull final Object... receivers) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults,
                receivers, new EasyPermissions.PermissionCallbacks() {
                    @Override
                    public void onPermissionsGranted(int requestCode,
                                                     List<String> grantedPermissions) {}
                    @Override
                    public void onPermissionsDenied(int requestCode,
                                                    List<String> deniedPermissions) {
                        for (Object object : receivers) {
                            runAfterPermissionDenied(object, requestCode, deniedPermissions);
                        }
                    }
                    @Override
                    public void onRequestPermissionsResult(int requestCode,
                                                           @NonNull String[] permissions,
                                                           @NonNull int[] grantResults) {}
                });
    }

    public static boolean somePermissionPermanentlyDenied(@NonNull Activity host,
                                                          @NonNull List<String> permissions) {
        return EasyPermissions.somePermissionPermanentlyDenied(host, permissions);
    }

    public static boolean somePermissionPermanentlyDenied(@NonNull Fragment host,
                                                          @NonNull List<String> permissions) {
        return EasyPermissions.somePermissionPermanentlyDenied(host, permissions);
    }

    public static boolean somePermissionPermanentlyDenied(@NonNull android.app.Fragment host,
                                                          @NonNull List<String> permissions) {
        return EasyPermissions.somePermissionPermanentlyDenied(host, permissions);
    }

    public static boolean somePermissionPermanentlyDenied(@NonNull Activity host,
                                                          @NonNull String... permissions) {
        return somePermissionPermanentlyDenied(host, Arrays.asList(permissions));
    }

    public static boolean somePermissionPermanentlyDenied(@NonNull Fragment host,
                                                          @NonNull String... permissions) {
        return EasyPermissions.somePermissionPermanentlyDenied(host, Arrays.asList(
                permissions));
    }

    public static boolean somePermissionPermanentlyDenied(@NonNull android.app.Fragment host,
                                                          @NonNull String... permissions) {
        return EasyPermissions.somePermissionPermanentlyDenied(host, Arrays.asList(
                permissions));
    }

    public static boolean permissionPermanentlyDenied(@NonNull Activity host,
                                                      @NonNull String deniedPermission) {
        return EasyPermissions.permissionPermanentlyDenied(host, deniedPermission);
    }

    public static boolean permissionPermanentlyDenied(@NonNull Fragment host,
                                                      @NonNull String deniedPermission) {
        return EasyPermissions.permissionPermanentlyDenied(host, deniedPermission);
    }

    public static boolean permissionPermanentlyDenied(@NonNull android.app.Fragment host,
                                                      @NonNull String deniedPermission) {
        return EasyPermissions.permissionPermanentlyDenied(host, deniedPermission);
    }

    public static boolean somePermissionDenied(@NonNull Activity host,
                                               @NonNull String... permissions) {
        return EasyPermissions.somePermissionDenied(host, permissions);
    }

    public static boolean somePermissionDenied(@NonNull Fragment host,
                                               @NonNull String... permissions) {
        return EasyPermissions.somePermissionDenied(host, permissions);
    }

    public static boolean somePermissionDenied(@NonNull android.app.Fragment host,
                                               @NonNull String... permissions) {
        return EasyPermissions.somePermissionDenied(host, permissions);
    }

    /**
     * @see EasyPermissions#runAnnotatedMethods(Object, int)
     */
    private static void runAfterPermissionDenied(@NonNull Object object, int requestCode,
                                                 List<String> deniedPermissions) {
        Class clazz = object.getClass();
        if (isUsingAndroidAnnotations(object)) {
            clazz = clazz.getSuperclass();
        }
        while (clazz != null) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(AfterPermissionDenied.class)) {
                    AfterPermissionDenied annotation = method.getAnnotation(
                            AfterPermissionDenied.class);
                    if (annotation.value() == requestCode) {
                        Class<?>[] parameterTypes = method.getParameterTypes();
                        if (!(parameterTypes.length == 0 || (parameterTypes.length == 1
                                && parameterTypes[0].isAssignableFrom(List.class)))) {
                            throw new RuntimeException("Cannot execute method " + method.getName() +
                                    " because its parameter list is not empty or containing only" +
                                    " a List<String>.");
                        }
                        try {
                            if (!method.isAccessible()) {
                                method.setAccessible(true);
                            }
                            if (parameterTypes.length == 0) {
                                method.invoke(object);
                            } else {
                                method.invoke(object, deniedPermissions);
                            }
                        } catch (IllegalAccessException e) {
                            Log.e(TAG, "Running AfterPermissionDenied failed", e);
                        } catch (InvocationTargetException e) {
                            Log.e(TAG, "Running AfterPermissionDenied failed", e);
                        }
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    /**
     * @see EasyPermissions#isUsingAndroidAnnotations(Object)
     */
    private static boolean isUsingAndroidAnnotations(@NonNull Object object) {
        if (!object.getClass().getSimpleName().endsWith("_")) {
            return false;
        }
        try {
            Class clazz = Class.forName("org.androidannotations.api.view.HasViews");
            return clazz.isInstance(object);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
