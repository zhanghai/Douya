/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.diary.content;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
import java.util.List;

import me.zhanghai.android.douya.content.MoreBaseListResourceFragment;
import me.zhanghai.android.douya.eventbus.DiaryDeletedEvent;
import me.zhanghai.android.douya.eventbus.DiaryUpdatedEvent;
import me.zhanghai.android.douya.eventbus.EventBusUtils;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.ApiRequest;
import me.zhanghai.android.douya.network.api.ApiService;
import me.zhanghai.android.douya.network.api.info.frodo.Diary;
import me.zhanghai.android.douya.network.api.info.frodo.DiaryList;
import me.zhanghai.android.douya.util.FragmentUtils;

public class UserDiaryListResource extends MoreBaseListResourceFragment<DiaryList, Diary> {

    private static final String KEY_PREFIX = UserDiaryListResource.class.getName() + '.';

    private static final String EXTRA_USER_ID_OR_UID = KEY_PREFIX + "user_id_or_uid";

    private String mUserIdOrUid;

    private static final String FRAGMENT_TAG_DEFAULT = UserDiaryListResource.class.getName();

    private static UserDiaryListResource newInstance(String userIdOrUid) {
        //noinspection deprecation
        return new UserDiaryListResource().setArguments(userIdOrUid);
    }

    public static UserDiaryListResource attachTo(String userIdOrUid, Fragment fragment, String tag,
                                                 int requestCode) {
        FragmentActivity activity = fragment.getActivity();
        UserDiaryListResource instance = FragmentUtils.findByTag(activity, tag);
        if (instance == null) {
            instance = newInstance(userIdOrUid);
            FragmentUtils.add(instance, activity, tag);
        }
        instance.setTarget(fragment, requestCode);
        return instance;
    }

    public static UserDiaryListResource attachTo(String userIdOrUid, Fragment fragment) {
        return attachTo(userIdOrUid, fragment, FRAGMENT_TAG_DEFAULT, REQUEST_CODE_INVALID);
    }

    /**
     * @deprecated Use {@code attachTo()} instead.
     */
    public UserDiaryListResource() {}

    protected UserDiaryListResource setArguments(String userIdOrUid) {
        FragmentUtils.getArgumentsBuilder(this)
                .putString(EXTRA_USER_ID_OR_UID, userIdOrUid);
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserIdOrUid = getArguments().getString(EXTRA_USER_ID_OR_UID);
    }

    @Override
    protected ApiRequest<DiaryList> onCreateRequest(Integer start, Integer count) {
        return ApiService.getInstance().getDiaryList(mUserIdOrUid, start, count);
    }

    @Override
    protected void onLoadStarted() {
        getListener().onLoadDiaryListStarted(getRequestCode());
    }

    @Override
    protected void onLoadFinished(boolean more, int count, boolean successful, List<Diary> response,
                                  ApiError error) {
        if (successful) {
            if (more) {
                append(response);
                getListener().onLoadDiaryListFinished(getRequestCode());
                getListener().onDiaryListAppended(getRequestCode(),
                        Collections.unmodifiableList(response));
            } else {
                set(response);
                getListener().onLoadDiaryListFinished(getRequestCode());
                getListener().onDiaryListChanged(getRequestCode(),
                        Collections.unmodifiableList(get()));
            }
            for (Diary diary : response) {
                EventBusUtils.postAsync(new DiaryUpdatedEvent(diary, this));
            }
        } else {
            getListener().onLoadDiaryListFinished(getRequestCode());
            getListener().onLoadDiaryListError(getRequestCode(), error);
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onDiaryUpdated(DiaryUpdatedEvent event) {

        if (event.isFromMyself(this) || isEmpty()) {
            return;
        }

        List<Diary> diaryList = get();
        for (int i = 0, size = diaryList.size(); i < size; ++i) {
            Diary diary = diaryList.get(i);
            if (diary.id == event.diary.id) {
                diaryList.set(i, event.diary);
                getListener().onDiaryChanged(getRequestCode(), i, diaryList.get(i));
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onDiaryDeleted(DiaryDeletedEvent event) {

        if (event.isFromMyself(this) || isEmpty()) {
            return;
        }

        List<Diary> diaryList = get();
        for (int i = 0, size = diaryList.size(); i < size; ) {
            Diary diary = diaryList.get(i);
            if (diary.id == event.diaryId) {
                diaryList.remove(i);
                getListener().onDiaryRemoved(getRequestCode(), i);
                --size;
            } else {
                ++i;
            }
        }
    }

    private Listener getListener() {
        return (Listener) getTarget();
    }

    public interface Listener {
        void onLoadDiaryListStarted(int requestCode);
        void onLoadDiaryListFinished(int requestCode);
        void onLoadDiaryListError(int requestCode, ApiError error);
        /**
         * @param newDiaryList Unmodifiable.
         */
        void onDiaryListChanged(int requestCode, List<Diary> newDiaryList);
        /**
         * @param appendedDiaryList Unmodifiable.
         */
        void onDiaryListAppended(int requestCode, List<Diary> appendedDiaryList);
        void onDiaryChanged(int requestCode, int position, Diary newDiary);
        void onDiaryRemoved(int requestCode, int position);
    }
}
