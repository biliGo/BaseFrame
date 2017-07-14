package com.lsl.base.net.request;

import com.lsl.base.net.utils.HttpUtils;

import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Forrest
 * on 2017/7/13 10:28
 */

public class GetRequest extends BaseRequest<GetRequest> {
    public GetRequest(String url) {
        super(url);
        mMethod = "GET";
    }

    @Override
    public RequestBody generateRequestBody() {
        return null;
    }

    @Override
    public Request generateRequest(RequestBody requestBody) {
        Request.Builder requestBuilder = HttpUtils.appendHeaders(mHeaders);
        mUrl = HttpUtils.createUrlFromParams(mBaseUrl, mParams.urlParamsMap);
        return requestBuilder.get().url(mUrl).tag(mTag).build();
    }
}
