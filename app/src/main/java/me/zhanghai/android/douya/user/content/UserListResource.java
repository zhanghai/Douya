/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.user.content;

import com.android.volley.VolleyError;

import me.zhanghai.android.douya.network.api.ApiRequest;
import me.zhanghai.android.douya.network.api.info.apiv2.UserList;

public abstract class UserListResource extends BaseUserListResource<UserList> {

    protected abstract ApiRequest<UserList> onCreateRequest(Integer start, Integer count);

    @Override
    protected void onDeliverLoadFinished(boolean successful, UserList userList, VolleyError error,
                                         boolean loadMore, int count) {
        onLoadFinished(successful, userList != null ? userList.users : null, error, loadMore,
                count);
    }
}
