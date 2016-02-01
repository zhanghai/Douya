/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import android.util.LruCache;

import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DiskCache {

    private DiskLruCache mCache;
    private SafeKeyGenerator mSafeKeyGenerator = new SafeKeyGenerator();

    private DiskCache(DiskLruCache cache) {
        mCache = cache;
    }

    public static DiskCache openOrThrow(File directory, int version, long maxSize)
            throws IOException {
        return new DiskCache(DiskLruCache.open(directory, version, 1, maxSize));
    }

    public static DiskCache open(File directory, int version, long maxSize) {
        try {
            return openOrThrow(directory, version, maxSize);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private DiskLruCache.Snapshot getOrThrow(String key) throws IOException, NullPointerException {
        DiskLruCache.Snapshot snapshot = mCache.get(mSafeKeyGenerator.getSafeKey(key));
        if (snapshot == null) {
            throw new NullPointerException("DiskLruCache.get() returned null");
        }
        return snapshot;
    }

    public InputStream getInputStreamOrThrow(String key) throws IOException, NullPointerException {
        return getOrThrow(key).getInputStream(0);
    }

    public InputStream getInputStream(String key) {
        try {
            return getInputStreamOrThrow(key);
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getStringOrThrow(String key) throws IOException {
        return getOrThrow(key).getString(0);
    }

    public String getString(String key) {
        try {
            return getStringOrThrow(key);
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T> T getGsonOrThrow(String key, Type type) throws IOException, JsonParseException {
        // Gson.fromJson() creates a JsonReader which does its own buffering.
        Reader reader = new InputStreamReader(getInputStreamOrThrow(key));
        try {
            return GsonHelper.get().fromJson(reader, type);
        } finally {
            reader.close();
        }
    }

    public <T> T getGson(String key, Type type) {
        try {
            return getGsonOrThrow(key, type);
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T> T getGson(String key, TypeToken<T> typeToken) {
        return getGson(key, typeToken.getType());
    }

    private DiskLruCache.Editor editOrThrow(String key) throws IOException, NullPointerException {
        DiskLruCache.Editor editor = mCache.edit(mSafeKeyGenerator.getSafeKey(key));
        if (editor == null) {
            throw new NullPointerException("DiskLruCache.edit() returned null");
        }
        return editor;
    }

    public void putBytesOrThrow(String key, byte[] value) throws IOException, NullPointerException {
        DiskLruCache.Editor editor = editOrThrow(key);
        try {
            editor.newOutputStream(0).write(value);
            editor.commit();
        } finally {
            editor.abortUnlessCommitted();
        }
    }

    public void putBytes(String key, byte[] value) {
        try {
            putBytesOrThrow(key, value);
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void putStringOrThrow(String key, String value)
            throws IOException, NullPointerException {
        putBytesOrThrow(key, value.getBytes(StandardCharsetsCompat.UTF_8));
    }

    public void putString(String key, String value) {
        try {
            putStringOrThrow(key, value);
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    public <T> void putGsonOrThrow(String key, T value, Type type)
            throws IOException, NullPointerException {
        DiskLruCache.Editor editor = editOrThrow(key);
        try {
            Writer writer = new BufferedWriter(new OutputStreamWriter(editor.newOutputStream(0)));
            try {
                GsonHelper.get().toJson(value, type, writer);
            } finally {
                writer.close();
            }
            editor.commit();
        } finally {
            editor.abortUnlessCommitted();
        }
    }

    public <T> void putGson(String key, T value, Type type) {
        try {
            putGsonOrThrow(key, value, type);
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    public <T> void putGson(String key, T value, TypeToken<T> typeToken) {
        putGson(key, value, typeToken.getType());
    }

    public boolean removeOrThrow(String key) throws IOException {
        return mCache.remove(key);
    }

    public boolean remove(String key) {
        try {
            return removeOrThrow(key);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void deleteOrThrow() throws IOException {
        mCache.delete();
        mCache = null;
    }

    public void delete() {
        try {
            deleteOrThrow();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeOrThrow() throws IOException {
        mCache.close();
    }

    public void close() {
        try {
            closeOrThrow();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class SafeKeyGenerator {

        private final LruCache<String, String> loadIdToSafeHash = new LruCache<>(100);

        public String getSafeKey(String key) {
            String safeKey;
            synchronized (loadIdToSafeHash) {
                safeKey = loadIdToSafeHash.get(key);
            }
            if (safeKey == null) {
                try {
                    MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                    messageDigest.update(key.getBytes(StandardCharsetsCompat.UTF_8));
                    safeKey = IoUtils.byteArrayToHexString(messageDigest.digest(), true);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                synchronized (loadIdToSafeHash) {
                    loadIdToSafeHash.put(key, safeKey);
                }
            }
            return safeKey;
        }
    }
}
