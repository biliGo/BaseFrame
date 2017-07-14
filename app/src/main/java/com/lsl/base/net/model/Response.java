package com.lsl.base.net.model;

import okhttp3.Headers;

/**
 * Created by Forrest
 * on 2017/6/23 19:22
 * 响应体的包装类
 */

public final class Response<T> {
    private final okhttp3.Response rawResponse;
    private final T body;

    public Response(okhttp3.Response rawResponse, T body) {
        this.rawResponse = rawResponse;
        this.body = body;
    }

    public okhttp3.Response raw(){
        return rawResponse;
    }

    public int code(){
        return rawResponse.code();
    }

    public String message(){
        return rawResponse.message();
    }

    public Headers headers(){
        return rawResponse.headers();
    }

    public boolean isSuccessful(){
        return rawResponse.isSuccessful();
    }

    public T body(){
        return body;
    }

    public static <T> Response<T> success(T body , okhttp3.Response rawResponse){
        if (rawResponse == null) throw new NullPointerException("rawResponse == null");
        if (!rawResponse.isSuccessful()) throw new IllegalArgumentException("rawResponse must be successful response");
        return new Response<>(rawResponse ,body);
    }

}
