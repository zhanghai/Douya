/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.profile.ui;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.network.api.info.apiv2.User;
import me.zhanghai.android.douya.network.api.info.frodo.Broadcast;
import me.zhanghai.android.douya.network.api.info.frodo.Diary;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleReview;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleUser;
import me.zhanghai.android.douya.network.api.info.frodo.UserItems;
import me.zhanghai.android.douya.ui.BarrierDataAdapter;
import me.zhanghai.android.douya.util.ViewUtils;

public class ProfileDataAdapter extends BarrierDataAdapter<ProfileDataAdapter.ViewHolder> {

    private enum Items {
        INTRODUCTION,
        BROADCASTS,
        FOLLOWSHIP,
        DIARIES,
        BOOKS,
        MOVIES,
        MUSIC,
        REVIEWS
    }

    private ProfileIntroductionLayout.Listener mListener;

    private Data mData;

    public ProfileDataAdapter(ProfileIntroductionLayout.Listener listener) {
        mListener = listener;
    }

    public void setData(Data data) {
        mData = data;
        notifyDataChanged();
    }

    @Override
    public int getTotalItemCount() {
        return Items.values().length;
    }

    @Override
    protected boolean isItemLoaded(int position) {
        if (mData == null) {
            return false;
        }
        if (mData.user == null) {
            return false;
        }
        switch (Items.values()[position]) {
            case INTRODUCTION:
                // HACK: For better visual results, wait until broadcasts are loaded so that we have
                // sufficient height.
                // Fall through!
                //return true;
            case BROADCASTS:
                return mData.broadcastList != null;
            case FOLLOWSHIP:
                return mData.followingList != null;
            case DIARIES:
                return mData.diaryList != null;
            case BOOKS:
                return mData.userItemList != null;
            case MOVIES:
                return mData.userItemList != null;
            case MUSIC:
                return mData.userItemList != null;
            case REVIEWS:
                return mData.reviewList != null;
            default:
                throw new IllegalArgumentException();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutRes;
        switch (Items.values()[viewType]) {
            case INTRODUCTION:
                layoutRes = R.layout.profile_introduction_item;
                break;
            case BROADCASTS:
                layoutRes = R.layout.profile_broadcasts_item;
                break;
            case FOLLOWSHIP:
                layoutRes = R.layout.profile_followship_item;
                break;
            case DIARIES:
                layoutRes = R.layout.profile_diaries_item;
                break;
            case BOOKS:
                layoutRes = R.layout.profile_books_item;
                break;
            case MOVIES:
                layoutRes = R.layout.profile_movies_item;
                break;
            case MUSIC:
                layoutRes = R.layout.profile_music_item;
                break;
            case REVIEWS:
                layoutRes = R.layout.profile_reviews_item;
                break;
            default:
                throw new IllegalArgumentException();
        }
        View itemView;
        if (ViewUtils.isInLandscape(parent.getContext())) {
            itemView = ViewUtils.inflateWithTheme(layoutRes, parent, R.style.Theme_Douya);
        } else {
            itemView = ViewUtils.inflate(layoutRes, parent);
        }
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        switch (Items.values()[position]) {
            case INTRODUCTION: {
                ProfileIntroductionLayout layout = ((ProfileIntroductionLayout) holder.getChild());
                layout.bind(mData.user);
                layout.setListener(mListener);
                break;
            }
            case BROADCASTS:
                ((ProfileBroadcastsLayout) holder.getChild()).bind(mData.user,
                        mData.broadcastList);
                break;
            case FOLLOWSHIP:
                ((ProfileFollowshipLayout) holder.getChild()).bind(mData.user,
                        mData.followingList);
                break;
            case DIARIES:
                ((ProfileDiariesLayout) holder.getChild()).bind(mData.user, mData.diaryList);
                break;
            case BOOKS:
                ((ProfileBooksLayout) holder.getChild()).bind(mData.user, mData.userItemList);
                break;
            case MOVIES:
                ((ProfileMoviesLayout) holder.getChild()).bind(mData.user, mData.userItemList);
                break;
            case MUSIC:
                ((ProfileMusicLayout) holder.getChild()).bind(mData.user, mData.userItemList);
                break;
            case REVIEWS:
                ((ProfileReviewsLayout) holder.getChild()).bind(mData.user, mData.reviewList);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    public static class Data {

        public User user;
        public List<Broadcast> broadcastList;
        public List<SimpleUser> followingList;
        public List<Diary> diaryList;
        public List<UserItems> userItemList;
        public List<SimpleReview> reviewList;

        public Data(User user, List<Broadcast> broadcastList, List<SimpleUser> followingList,
                    List<Diary> diaryList, List<UserItems> userItemList,
                    List<SimpleReview> reviewList) {
            this.user = user;
            this.broadcastList = broadcastList;
            this.followingList = followingList;
            this.diaryList = diaryList;
            this.userItemList = userItemList;
            this.reviewList = reviewList;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public View getChild() {
            return ((ViewGroup) itemView).getChildAt(0);
        }
    }
}
