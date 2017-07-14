package com.lsl.base.net.cache.map;

/**
 * Created by Forrest
 * on 2017/6/18 15:55
 */

public class Tuple<A,B> {
    public final A key;
    public final B value;

    public Tuple(A key, B value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
