package com.lsl.base.net.callback;

import com.lsl.base.net.convert.StringConvert;

import okhttp3.Response;

/**
 * Created by Forrest
 * on 2017/7/13 10:53
 */

public abstract class StringCallback extends AbsCallback<String> {

    @Override
    public String convertSuccess(Response response) throws Exception {
        String s = StringConvert.create().convertSuccess(response);
        response.close();
        return s;
    }

}