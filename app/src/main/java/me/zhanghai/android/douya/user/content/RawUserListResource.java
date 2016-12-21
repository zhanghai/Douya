/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.user.content;

import com.android.volley.VolleyError;

import java.util.List;

import me.zhanghai.android.douya.network.api.ApiRequest;
import me.zhanghai.android.douya.network.api.info.apiv2.User;

public abstract class RawUserListResource extends BaseUserListResource<List<User>> {

    protected abstract ApiRequest<List<User>> onCreateRequest(Integer start, Integer count);

    @Override
    protected void onCallRawLoadFinished(boolean more, int count, boolean successful,
                                         List<User> response, VolleyError error) {
        onRawLoadFinished(more, count, successful, response, error);
    }
}
