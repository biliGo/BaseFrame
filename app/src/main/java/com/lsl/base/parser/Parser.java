package com.lsl.base.parser;

import java.text.ParseException;

/**
 * Created by Forrest on 16/6/23.
 * 将字符串解析转化为java对象
 */
public interface Parser<T> {
    /**
     * 执行解析
     */
    T parse(byte[] data) throws ParseException;
}
