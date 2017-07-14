package com.lsl.base.net.cookie;

import com.lsl.base.net.cookie.store.CookieStore;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * Created by Forrest
 * on 2017/6/30 14:54
 * CookieJar的实现类，默认管理了用户自己维护的Cookie
 */

public class CookieJarImpl implements CookieJar{

    private CookieStore cookieStore;

    public CookieJarImpl(CookieStore cookieStore) {
        if (cookieStore == null){
            throw new IllegalArgumentException("cookieStore can't be null");
        }
        this.cookieStore = cookieStore;

    }

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        cookieStore.saveCookie(url,cookies);
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        return cookieStore.loadCookie(url);
    }

    public CookieStore getCookieStore() {
        return cookieStore;
    }
}
