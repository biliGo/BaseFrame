package com.lsl.base.net.adapter;

/**
 * Created by Forrest
 * on 2017/6/25 10:21
 * 默认的工厂处理，不对返回值做任何处理
 */

public class DefaultCallAdapter<T> implements CallAdapter<Call<T>> {

    public static <T> DefaultCallAdapter<T> create(){
        return new DefaultCallAdapter<>();
    }


    @Override
    public <R> Call<T> adapt(Call<R> call) {
        return (Call<T>) call;
    }
}
