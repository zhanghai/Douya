/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.content.Context;
import android.content.res.Resources;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.ThemedSpinnerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ArrayAdapterCompat<T> extends ArrayAdapter<T> implements ThemedSpinnerAdapter {

    /*
     * @see ArrayAdapter#mDropDownResource
     */
    private int mDropDownResource;
    /*
     * @see ArrayAdapter#mFieldId
     */
    private int mFieldId;

    private ThemedSpinnerAdapter.Helper mHelper;

    /**
     * {@inheritDoc}
     */
    public ArrayAdapterCompat(@NonNull Context context, int resource) {
        super(context, resource);

        init(resource, 0);
    }

    /**
     * {@inheritDoc}
     */
    public ArrayAdapterCompat(@NonNull Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);

        init(resource, textViewResourceId);
    }

    /**
     * {@inheritDoc}
     */
    public ArrayAdapterCompat(@NonNull Context context, int resource, @NonNull T[] objects) {
        super(context, resource, objects);

        init(resource, 0);
    }

    /**
     * {@inheritDoc}
     */
    public ArrayAdapterCompat(@NonNull Context context, int resource, int textViewResourceId,
                              @NonNull T[] objects) {
        super(context, resource, textViewResourceId, objects);

        init(resource, textViewResourceId);
    }

    /**
     * {@inheritDoc}
     */
    public ArrayAdapterCompat(@NonNull Context context, int resource, @NonNull List<T> objects) {
        super(context, resource, objects);

        init(resource, 0);
    }

    /**
     * {@inheritDoc}
     */
    public ArrayAdapterCompat(@NonNull Context context, int resource, int textViewResourceId,
                              @NonNull List<T> objects) {
        super(context, resource, textViewResourceId, objects);

        init(resource, textViewResourceId);
    }

    private void init(int resource, int textViewResourceId) {

        mDropDownResource = resource;
        mFieldId = textViewResourceId;

        mHelper = new ThemedSpinnerAdapter.Helper(getContext());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDropDownViewResource(int resource) {
        super.setDropDownViewResource(resource);

        mDropDownResource = resource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDropDownViewTheme(@Nullable Resources.Theme theme) {
        mHelper.setDropDownViewTheme(theme);
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public Resources.Theme getDropDownViewTheme() {
        return mHelper.getDropDownViewTheme();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View getDropDownView(int position, @Nullable View convertView,
                                @NonNull ViewGroup parent) {
        LayoutInflater inflater = mHelper.getDropDownViewInflater();
        return createViewFromResource(inflater, position, convertView, parent, mDropDownResource);
    }

    /*
     * @see ArrayAdapter#createViewFromResource(LayoutInflater, int, View, ViewGroup, int)
     */
    @NonNull
    private View createViewFromResource(@NonNull LayoutInflater inflater, int position,
                                        @Nullable View convertView, @NonNull ViewGroup parent,
                                        int resource) {
        final View view;
        final TextView text;

        if (convertView == null) {
            view = inflater.inflate(resource, parent, false);
        } else {
            view = convertView;
        }

        try {
            if (mFieldId == 0) {
                //  If no custom field is assigned, assume the whole resource is a TextView
                text = (TextView) view;
            } else {
                //  Otherwise, find the TextView field within the layout
                text = view.findViewById(mFieldId);

                if (text == null) {
                    throw new RuntimeException("Failed to find view with ID "
                            + getContext().getResources().getResourceName(mFieldId)
                            + " in item layout");
                }
            }
        } catch (ClassCastException e) {
            Log.e("ArrayAdapter", "You must supply a resource ID for a TextView");
            throw new IllegalStateException(
                    "ArrayAdapter requires the resource ID to be a TextView", e);
        }

        final T item = getItem(position);
        if (item instanceof CharSequence) {
            text.setText((CharSequence) item);
        } else {
            text.setText(item.toString());
        }

        return view;
    }
}