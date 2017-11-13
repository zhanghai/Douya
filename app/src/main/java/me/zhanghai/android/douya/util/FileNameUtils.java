/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

/**
 * This class assumes the only separator to be '/'.
 *
 * Terminology:
 * <ul>
 * <li>file = path + SEPARATOR + fileName</li>
 * <li>fileName = baseName + EXTENSION_SEPARATOR + extension</li>
 * </ul>
 */
public class FileNameUtils {

    private static final char EXTENSION_SEPARATOR = '.';
    // Not using File.separatorChar so that behavior is consistent and always ready for URIs.
    // Anyway we are on Android. If one day we were moved to Windows, fail-fast is also good.
    private static final char SEPARATOR = '/';

    private FileNameUtils() {}

    public static String getBaseName(String file) {
        return removeExtension(getFileName(file));
    }

    public static String getExtension(String file) {
        int index = indexOfExtensionSeparator(file);
        return index != -1 ? file.substring(index + 1) : "";
    }

    public static String getFileName(String file) {
        int index = indexOfLastSeparator(file);
        return file.substring(index + 1);
    }

    public static String getPath(String file) {
        int index = indexOfLastSeparator(file);
        return index != -1 ? file.substring(0, index) : ".";
    }

    public static String getPathWithEndSeparator(String file) {
        // We assume the only separator is '/'.
        return getPath(file) + SEPARATOR;
    }

    public static int indexOfExtensionSeparator(String file) {
        int lastSeparatorIndex = indexOfLastSeparator(file);
        int lastExtensionSeparatorIndex = file.lastIndexOf(EXTENSION_SEPARATOR);
        return lastSeparatorIndex > lastExtensionSeparatorIndex ? -1 : lastExtensionSeparatorIndex;
    }

    public static int indexOfLastSeparator(String file) {
        return file.lastIndexOf(SEPARATOR);
    }

    public static String removeExtension(String file) {
        int index = indexOfExtensionSeparator(file);
        return index != -1 ? file.substring(0, index) : file;
    }
}
