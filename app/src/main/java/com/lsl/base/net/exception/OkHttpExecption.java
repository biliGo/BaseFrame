package com.lsl.base.net.exception;

/**
 * Created by Forrest
 * on 2017/6/25 16:46
 */

public class OkHttpExecption extends Exception {

    public static OkHttpExecption INSTANCE(String msg) {
        return new OkHttpExecption(msg);
    }

    public OkHttpExecption() {
        super();
    }

    public OkHttpExecption(String detailMessage) {
        super(detailMessage);
    }

    public OkHttpExecption(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public OkHttpExecption(Throwable throwable) {
        super(throwable);
    }
}
