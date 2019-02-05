/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.util.Pair;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.network.api.info.frodo.CollectableItem;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleCelebrity;
import me.zhanghai.android.douya.ui.AdapterGridLinearLayout;
import me.zhanghai.android.douya.util.CollectionUtils;
import me.zhanghai.android.douya.util.FragmentUtils;
import me.zhanghai.android.douya.util.StringCompat;
import me.zhanghai.android.douya.util.TintHelper;
import me.zhanghai.android.douya.util.ViewUtils;

public abstract class BaseItemIntroductionFragment<T extends CollectableItem> extends Fragment {

    private static final String KEY_PREFIX = BaseItemIntroductionFragment.class.getName() + '.';

    private static final String EXTRA_ITEM = KEY_PREFIX + "item";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.introduction)
    TextView mIntroductionText;
    @BindView(R.id.information)
    AdapterGridLinearLayout mInformationLayout;

    protected T mItem;

    protected void setArguments(T item) {
        FragmentUtils.getArgumentsBuilder(this)
                .putParcelable(EXTRA_ITEM, item);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        mItem = arguments.getParcelable(EXTRA_ITEM);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.item_introduction_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        TintHelper.onSetSupportActionBar(mToolbar);
        activity.setTitle(mItem.title);

        mIntroductionText.setText(!TextUtils.isEmpty(mItem.introduction) ?
                mItem.getPrettyIntroduction() : activity.getString(
                R.string.item_introduction_empty));

        List<Pair<String, String>> informationData = makeInformationData();
        boolean hasInformationData = !informationData.isEmpty();
        ViewUtils.setVisibleOrGone(mInformationLayout, hasInformationData);
        if (hasInformationData) {
            ItemIntroductionPairListAdapter informationAdapter =
                    new ItemIntroductionPairListAdapter();
            informationAdapter.replace(informationData);
            mInformationLayout.setColumnCount(2);
            // HACK: Disabled for looking weird; anyway we always have the space from word break.
            //mInformationLayout.setHorizontalDivider(R.drawable.transparent_divider_vertical_16dp);
            mInformationLayout.setAdapter(informationAdapter);
        }
    }

    protected abstract List<Pair<String, String>> makeInformationData();

    protected void addTextToData(int titleRes, String text, List<Pair<String, String>> data) {
        if (!TextUtils.isEmpty(text)) {
            String title = getString(titleRes);
            data.add(new Pair<>(title, text));
        }
    }

    private void addTextListToData(int titleRes, List<String> textList, String delimiter,
                                   List<Pair<String, String>> data) {
        if (!CollectionUtils.isEmpty(textList)) {
            String title = getString(titleRes);
            String text = StringCompat.join(delimiter, textList);
            data.add(new Pair<>(title, text));
        }
    }

    protected void addTextListToData(int titleRes, List<String> textList,
                                     List<Pair<String, String>> data) {
        addTextListToData(titleRes, textList, getString(R.string.item_information_delimiter_slash),
                data);
    }

    protected void addCelebrityListToData(int titleRes, List<SimpleCelebrity> celebrityList,
                                          List<Pair<String, String>> data) {
        if (!CollectionUtils.isEmpty(celebrityList)) {
            List<String> celebrityNameList = new ArrayList<>();
            for (SimpleCelebrity director : celebrityList) {
                celebrityNameList.add(director.name);
            }
            addTextListToData(titleRes, celebrityNameList, getString(
                    R.string.item_introduction_celebrity_delimiter), data);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
