package com.lsl.base.net.callback;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Forrest
 * on 2017/6/25 11:26
 * 所有回调的包装类，空实现
 */

public class AbsCallbackWrapper<T> extends AbsCallback<T> {
    @Override
    public T convertSuccess(Response response) throws Exception {
        response.close();
        return (T) response;
    }

    @Override
    public void onSuccess(T t, Call call, Response response) {

    }
}
