/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.util.Property;

/**
 * From {@code android.util.FloatProperty}.
 *
 * An implementation of {@link Property} to be used specifically with fields of type
 * <code>float</code>. This type-specific subclass enables performance benefit by allowing
 * calls to a {@link #set(Object, Float) set()} function that takes the primitive
 * <code>float</code> type and avoids autoboxing and other overhead associated with the
 * <code>Float</code> class.
 *
 * @param <T> The class on which the Property is declared.
 */
public abstract class FloatProperty<T> extends Property<T, Float> {

    public FloatProperty(String name) {
        super(Float.class, name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void set(T object, Float value) {
        setValue(object, value);
    }

    /**
     * A type-specific override of the {@link #set(Object, Float)} that is faster when dealing
     * with fields of type <code>float</code>.
     *
     * @param object The target object.
     * @param value The <code>float</code> type value.
     */
    public abstract void setValue(T object, float value);
}
