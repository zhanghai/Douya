/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Type adapter for Android Bundle. It only stores the actual properties set in the bundle
 *
 * @author Inderjeet Singh
 * @author Zhang Hai
 */
public class BundleTypeAdapterFactory implements TypeAdapterFactory {

    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(final Gson gson, TypeToken<T> type) {

        if (!Bundle.class.isAssignableFrom(type.getRawType())) {
            return null;
        }

        return (TypeAdapter<T>) new TypeAdapter<Bundle>() {

            @Override
            public void write(JsonWriter out, Bundle bundle) throws IOException {
                if (bundle == null) {
                    out.nullValue();
                    return;
                }
                out.beginObject();
                for (String key : bundle.keySet()) {
                    out.name(key);
                    Object value = bundle.get(key);
                    if (value == null) {
                        out.nullValue();
                    } else {
                        gson.toJson(value, value.getClass(), out);
                    }
                }
                out.endObject();
            }

            @Override
            public Bundle read(JsonReader in) throws IOException {
                switch (in.peek()) {
                    case NULL:
                        in.nextNull();
                        return null;
                    case BEGIN_OBJECT:
                        return toBundle(readObject(in));
                    default:
                        throw new IOException("Expecting object: " + in.getPath());
                }
            }

            private Bundle toBundle(List<Pair<String, Object>> values) throws IOException {
                Bundle bundle = new Bundle();
                for (Pair<String, Object> entry : values) {
                    String key = entry.first;
                    Object value = entry.second;
                    if (value instanceof String) {
                        bundle.putString(key, (String) value);
                    } else if (value instanceof Boolean) {
                        bundle.putBoolean(key, (Boolean) value);
                    } else if (value instanceof Integer) {
                        bundle.putInt(key, (Integer) value);
                    } else if (value instanceof Long) {
                        bundle.putLong(key, (Long) value);
                    } else if (value instanceof Double) {
                        bundle.putDouble(key, (Double) value);
                    } else if (value instanceof Parcelable) {
                        bundle.putParcelable(key, (Parcelable) value);
                    } else if (value instanceof List) {
                        List<Pair<String, Object>> objectValues =
                                (List<Pair<String, Object>>) value;
                        Bundle subBundle = toBundle(objectValues);
                        bundle.putParcelable(key, subBundle);
                    } else {
                        throw new IOException("Unparcelable key, value: " + key + ", "+ value);
                    }
                }
                return bundle;
            }

            private List<Pair<String, Object>> readObject(JsonReader in) throws IOException {
                List<Pair<String, Object>> object = new ArrayList<>();
                in.beginObject();
                while (in.peek() != JsonToken.END_OBJECT) {
                    switch (in.peek()) {
                        case NAME:
                            String name = in.nextName();
                            Object value = readValue(in);
                            object.add(new Pair<>(name, value));
                            break;
                        case END_OBJECT:
                            break;
                        default:
                            throw new IOException("Expecting object: " + in.getPath());
                    }
                }
                in.endObject();
                return object;
            }

            private Object readValue(JsonReader in) throws IOException {
                switch (in.peek()) {
                    case BEGIN_ARRAY:
                        return readArray(in);
                    case BEGIN_OBJECT:
                        return readObject(in);
                    case BOOLEAN:
                        return in.nextBoolean();
                    case NULL:
                        in.nextNull();
                        return null;
                    case NUMBER:
                        return readNumber(in);
                    case STRING:
                        return in.nextString();
                    default:
                        throw new IOException("Expecting value: " + in.getPath());
                }
            }

            private Object readNumber(JsonReader in) throws IOException {
                double doubleValue = in.nextDouble();
                if (doubleValue - Math.ceil(doubleValue) == 0) {
                    long longValue = (long) doubleValue;
                    if (longValue >= Integer.MIN_VALUE && longValue <= Integer.MAX_VALUE) {
                        return (int) longValue;
                    }
                    return longValue;
                }
                return doubleValue;
            }

            private List<Object> readArray(JsonReader in) throws IOException {
                List<Object> list = new ArrayList<>();
                in.beginArray();
                while (in.peek() != JsonToken.END_ARRAY) {
                    Object element = readValue(in);
                    list.add(element);
                }
                in.endArray();
                return list;
            }
        };
    }
}
