/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.user.content;

import java.util.Collections;
import java.util.List;

import me.zhanghai.android.douya.content.MoreRawListResourceFragment;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleUser;

public abstract class BaseUserListResource<ResponseType>
        extends MoreRawListResourceFragment<ResponseType, SimpleUser> {

    @Override
    protected void onLoadStarted() {
        getListener().onLoadUserListStarted(getRequestCode());
    }

    @Override
    protected final void onLoadFinished(boolean more, int count, boolean successful,
                                        ResponseType response, ApiError error) {
        onCallRawLoadFinished(more, count, successful, response, error);
    }

    protected abstract void onCallRawLoadFinished(boolean more, int count, boolean successful,
                                                  ResponseType response, ApiError error);

    protected void onRawLoadFinished(boolean more, int count, boolean successful,
                                     List<SimpleUser> response, ApiError error) {
        if (successful) {
            if (more) {
                append(response);
                getListener().onLoadUserListFinished(getRequestCode());
                getListener().onUserListAppended(getRequestCode(),
                        Collections.unmodifiableList(response));
            } else {
                set(response);
                getListener().onLoadUserListFinished(getRequestCode());
                getListener().onUserListChanged(getRequestCode(),
                        Collections.unmodifiableList(get()));
            }
        } else {
            getListener().onLoadUserListFinished(getRequestCode());
            getListener().onLoadUserListError(getRequestCode(), error);
        }
    }

    private Listener getListener() {
        return (Listener) getTarget();
    }

    public interface Listener {
        void onLoadUserListStarted(int requestCode);
        void onLoadUserListFinished(int requestCode);
        void onLoadUserListError(int requestCode, ApiError error);
        /**
         * @param newUserList Unmodifiable.
         */
        void onUserListChanged(int requestCode, List<SimpleUser> newUserList);
        /**
         * @param appendedUserList Unmodifiable.
         */
        void onUserListAppended(int requestCode, List<SimpleUser> appendedUserList);
    }
}
