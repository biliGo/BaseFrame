package com.lsl.base.net.adapter;

/**
 * Created by Forrest
 * on 2017/6/25 10:19
 * 返回值适配器
 */

public interface CallAdapter<T> {

    /** call执行的代理方法*/
    <R> T adapt(Call<R> call);
}
