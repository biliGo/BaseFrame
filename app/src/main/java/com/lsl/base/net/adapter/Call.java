package com.lsl.base.net.adapter;

import com.lsl.base.net.callback.AbsCallback;
import com.lsl.base.net.model.Response;
import com.lsl.base.net.request.BaseRequest;


/**
 * Created by Forrest
 * on 2017/6/23 19:17
 */

public interface Call<T> {

    /**同步执行*/
    Response<T> execute() throws Exception;

    /**异步回调执行*/
    void execute(AbsCallback<T> callback);

    /**是否已经执行*/
    boolean isExecuted();

    /** 取消*/
    void cancel();

    /**是否取消*/
    boolean isCanceled();

    Call<T> clone();

    BaseRequest getBaseRequest();
}
