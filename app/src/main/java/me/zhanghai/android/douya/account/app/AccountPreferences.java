/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.account.app;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import me.zhanghai.android.douya.util.IoUtils;

/**
 * An {@link Account} based {@link SharedPreferences} implementation using
 * {@link AccountManager#getUserData(Account, String)} and
 * {@link AccountManager#setUserData(Account, String, String)}.
 *
 * <p>Due to the limitation of {@link AccountManager}, we can only access a value by its key, but we
 * cannot access the key set or the entire map. So {@link #getAll()} and {@link #clear()} are
 * unsupported, {@link #edit()}, {@link #commit()} and {@link #apply()} are stub, and changes are
 * committed immediately.</p>
 *
 * <p>Also due to the limitation of {@link AccountManager}, all values are stored as {@link String},
 * so {@link #putStringSet(String, Set)} needs to flatten the {@link String} array into one
 * {@link String}, in this case the character bar ('|') is used as the delimiter.</p>
 *
 * @see SharedPreferences
 * @see AccountManager
 */
public class AccountPreferences implements SharedPreferences, SharedPreferences.Editor {

    private static final String TRUE_STRING = "true";
    private static final String FALSE_STRING = "false";

    private static final Object INSTANCES_LOCK = new Object();
    // NOTE: Not using WeakHashMap and WeakReference for instance map because we don't won't to
    // lose registered listeners.
    private static final Map<Account, AccountPreferences> INSTANCES = new HashMap<>();

    private Account account;
    private AccountManager accountManager;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private Set<OnSharedPreferenceChangeListener> listeners = Collections.newSetFromMap(
            new WeakHashMap<OnSharedPreferenceChangeListener, Boolean>());

    private AccountPreferences(Account account, AccountManager accountManager) {
        this.account = account;
        this.accountManager = accountManager;
    }

    public static AccountPreferences from(AccountManager accountManager, Account account) {
        if (account == null) {
            throw new IllegalArgumentException("account is null");
        }
        synchronized (INSTANCES_LOCK) {
            AccountPreferences accountPreferences = INSTANCES.get(account);
            if (accountPreferences == null) {
                accountPreferences = new AccountPreferences(account, accountManager);
                INSTANCES.put(account, accountPreferences);
            }
            return accountPreferences;
        }
    }

    public static AccountPreferences from(Account account, Context context) {
        return from(AccountManager.get(context), account);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getString(String key, String defaultValue) {
        String value = accountManager.getUserData(account, key);
        return value != null ? value : defaultValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getStringSet(String key, Set<String> defaultValue) {
        String stringValue = getString(key, null);
        if (stringValue == null) {
            return defaultValue;
        } else {
            Set<String> value = new HashSet<>();
            Collections.addAll(value, IoUtils.stringToStringArray(stringValue));
            return value;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getInt(String key, int defaultValue) {
        String stringValue = getString(key, null);
        if (stringValue == null) {
            return defaultValue;
        } else {
            try {
                return Integer.parseInt(stringValue);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return defaultValue;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getLong(String key, long defaultValue) {
        String stringValue = getString(key, null);
        if (stringValue == null) {
            return defaultValue;
        } else {
            try {
                return Long.parseLong(stringValue);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return defaultValue;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getFloat(String key, float defaultValue) {
        String stringValue = getString(key, null);
        if (stringValue == null) {
            return defaultValue;
        } else {
            try {
                return Float.parseFloat(stringValue);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return defaultValue;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        String stringValue = getString(key, null);
        if (stringValue == null) {
            return defaultValue;
        } else {
            switch (stringValue) {
                case TRUE_STRING:
                    return true;
                case FALSE_STRING:
                    return false;
                default:
                    return defaultValue;
            }
        }
    }


    /**
     * Unsupported operation.
     *
     * Due to the limitation of {@link AccountManager}, we can only access a value by its key, but we
     * cannot access the key set or the entire map.. Calling this method will throw an
     * {@link UnsupportedOperationException}.
     *
     * @throws UnsupportedOperationException
     */
    @Override
    public Map<String, ?> getAll() {
        throw new UnsupportedOperationException("getAll() is not supported by AccountManager");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(String key) {
        return getString(key, null) != null;
    }


    /**
     * Stub method.
     *
     * Due to the limitation of {@link AccountManager}, we cannot batch commit changes, so this
     * instance itself is returned.
     *
     * @return Returns this instance.
     */
    @Override
    public Editor edit() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AccountPreferences putString(String key, String value) {
        accountManager.setUserData(account, key, value);
        notifyChanged(key);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Due to the limitation of {@link AccountManager}, all values are stored as {@link String},
     * so {@link #putStringSet(String, Set)} needs to flatten the {@link String} array into one
     * {@link String}, in this case the character bar ('|') is used as the delimiter.</p>
     */
    @Override
    public AccountPreferences putStringSet(String key, Set<String> value) {
        return putString(key, value != null ? IoUtils.collectionToString(value) : null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AccountPreferences putInt(String key, int value) {
        return putString(key, Integer.toString(value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AccountPreferences putLong(String key, long value) {
        return putString(key, Long.toString(value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AccountPreferences putFloat(String key, float value) {
        return putString(key, Float.toString(value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AccountPreferences putBoolean(String key, boolean value) {
        return putString(key, value ? TRUE_STRING : FALSE_STRING);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AccountPreferences remove(String key) {
        return putString(key, null);
    }

    /**
     * Unsupported operation.
     *
     * Due to the limitation of {@link AccountManager}, we can only access a value by its key, but we
     * cannot access the key set or the entire map. Calling this method will throw an
     * {@link UnsupportedOperationException}.
     *
     * @throws UnsupportedOperationException
     */
    @Override
    public Editor clear() {
        throw new UnsupportedOperationException("clear() is not supported by AccountManager");
    }

    /**
     * Stub method.
     *
     * Due to the limitation of {@link AccountManager}, we cannot batch commit changes, so nothing
     * is done and it will always success.
     *
     * @return Always returns true.
     */
    @Override
    public boolean commit() {
        return true;
    }

    /**
     * Stub method.
     *
     * Due to the limitation of {@link AccountManager}, we cannot batch commit changes, so nothing
     * is done.
     */
    @Override
    public void apply() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerOnSharedPreferenceChangeListener(
            OnSharedPreferenceChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unregisterOnSharedPreferenceChangeListener(
            OnSharedPreferenceChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyChanged(final String key) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            for (OnSharedPreferenceChangeListener listener : listeners) {
                if (listener != null) {
                    listener.onSharedPreferenceChanged(AccountPreferences.this, key);
                }
            }
        } else {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    notifyChanged(key);
                }
            });
        }
    }
}
