package com.lsl.base.net;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.lsl.base.common.BLog;
import com.lsl.base.net.cache.CacheEntity;
import com.lsl.base.net.cache.CacheMode;
import com.lsl.base.net.cookie.CookieJarImpl;
import com.lsl.base.net.cookie.store.CookieStore;
import com.lsl.base.net.https.HttpsUtils;
import com.lsl.base.net.interceptor.HttpLoggingInterceptor;
import com.lsl.base.net.model.HttpHeaders;
import com.lsl.base.net.model.HttpParams;
import com.lsl.base.net.request.GetRequest;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.X509TrustManager;
import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * Created by Forrest
 * on 2017/6/18 17:11
 */

public class OkHttp {
    public static final int DEFAULT_MILLISECONDS = 60000;       //默认的超时时间
    public static int REFRESH_TIME = 100;                       //回调刷新时间（单位ms）

    private Handler mDelivery;                                  //用于在主线程执行的调度器
    private OkHttpClient.Builder okHttpClientBuilder;           //ok请求的客户端
    private OkHttpClient okHttpClient;                          //ok请求的客户端
    private HttpParams mCommonParams;                           //全局公共请求参数
    private HttpHeaders mCommonHeaders;                         //全局公共请求头
    private CacheMode mCacheMode;                               //全局缓存模式
    private int mRetryCount = 3;                                //全局超时重试次数
    private long mCacheTime = CacheEntity.CACHE_NEVER_EXPIRE;   //全局缓存过期时间,默认永不过期
    private static Application context;                         //全局上下文
    private CookieJarImpl cookieJar;                            //全局 Cookie 实例

    private OkHttp() {
        //该类只在GApp中调用一次，生成一个公用的HttpClient,
        // 每次请求其实调用的是单例过的okHttpClientBuilder.build()
        //这个整个工程的okHttpClient都会公用一个连接池，能节省很大的开销
        okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.hostnameVerifier(HttpsUtils.UnSafeHostnameVerifier);
        okHttpClientBuilder.connectTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        okHttpClientBuilder.readTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        okHttpClientBuilder.writeTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        mDelivery = new Handler(Looper.getMainLooper());
    }

    public static OkHttp getInstance() {
        return OkHttpHolder.holder;
    }

    private static class OkHttpHolder {
        private static OkHttp holder = new OkHttp();
    }

    /** 必须在全局Application先调用，获取context上下文，否则缓存无法使用 */
    public static void init(Application app) {
        context = app;
    }

    /** 获取全局上下文 */
    public static Context getContext() {
        if (context == null) throw new IllegalStateException("请先在全局Application中调用 OkGo.init() 初始化！");
        return context;
    }

    public Handler getDelivery() {
        return mDelivery;
    }

    public OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) okHttpClient = okHttpClientBuilder.build();
        return okHttpClient;
    }

    /** 对外暴露 OkHttpClient,方便自定义 */
    public OkHttpClient.Builder getOkHttpClientBuilder() {
        return okHttpClientBuilder;
    }

    /** 调试模式,默认打开所有的异常调试 */
    public OkHttp debug(String tag){
        debug(tag,Level.INFO,true);
        return this;
    }

    /** get请求 */
    public static GetRequest get(String url) {
        return new GetRequest(url);
    }

    /**
     * 调试模式,第三个参数表示所有catch住的log是否需要打印
     * 一般来说,这些异常是由于不标准的数据格式,或者特殊需要主动产生的,并不是框架错误,如果不想每次打印,这里可以关闭异常显示
     */
    public OkHttp debug(String tag, Level level ,boolean isPrintException){
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(tag);
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
        loggingInterceptor.setColorLevel(level);
        okHttpClientBuilder.addInterceptor(loggingInterceptor);
        BLog.debug(isPrintException);
        return this;
    }
    /** https的自定义域名访问规则*/
    public OkHttp setHostnameVerifier(HostnameVerifier hostnameVerifier){
        okHttpClientBuilder.hostnameVerifier(hostnameVerifier);
        return this;
    }

    /**
     * https单向认证
     * 用含有服务端公钥的证书校验服务端证书
     */
    public OkHttp setCertificates(InputStream... certificates) {
        setCertificates(null, null, certificates);
        return this;
    }

    /**
     * https单向认证
     * 可以额外配置信任服务端的证书策略，否则默认是按CA证书去验证的，若不是CA可信任的证书，则无法通过验证
     */
    public OkHttp setCertificates(X509TrustManager trustManager) {
        setCertificates(null, null, trustManager);
        return this;
    }

    /**
     * https双向认证
     * bksFile 和 password -> 客户端使用bks证书校验服务端证书
     * certificates -> 用含有服务端公钥的证书校验服务端证书
     */
    public OkHttp setCertificates(InputStream bksFile, String password, InputStream... certificates) {
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSSLSocketFactory(null, bksFile, password, certificates);
        okHttpClientBuilder.sslSocketFactory(sslParams.mSSLSocketFactory, sslParams.mTrustManager);
        return this;
    }
    /** https双向认证
     * bksFile 和 password -> 客户端使用bks证书校验服务端证书
     * certificates -> 用含有服务端公钥的证书校验服务端证书
     **/
    public OkHttp setCertificates(InputStream bskFile, String password , X509TrustManager trustManager){
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSSLSocketFactory(trustManager,bskFile,password,null);
        okHttpClientBuilder.sslSocketFactory(sslParams.mSSLSocketFactory,sslParams.mTrustManager);
        return this;
    }

    public OkHttp setCookieStore(CookieStore cookieStore){
        cookieJar = new CookieJarImpl(cookieStore);
        okHttpClientBuilder.cookieJar(cookieJar);
        return this;
    }
    /** 获取全局的cookie实例 */
    public CookieJarImpl getCookieJar() {
        return cookieJar;
    }
    /** 全局读取超时时间 */
    public OkHttp setReadTimeOut(long readTimeOut){
        okHttpClientBuilder.readTimeout(readTimeOut,TimeUnit.MILLISECONDS);
        return this;
    }
    /** 全局写入超时时间 */
    public OkHttp setWriteTimeOut(long writeTimeout) {
        okHttpClientBuilder.writeTimeout(writeTimeout, TimeUnit.MILLISECONDS);
        return this;
    }

    /** 全局连接超时时间 */
    public OkHttp setConnectTimeout(long connectTimeout) {
        okHttpClientBuilder.connectTimeout(connectTimeout, TimeUnit.MILLISECONDS);
        return this;
    }

    /** 超时重试次数 */
    public OkHttp setRetryCount(int retryCount) {
        if (retryCount < 0) throw new IllegalArgumentException("retryCount must > 0");
        mRetryCount = retryCount;
        return this;
    }

    /** 超时重试次数 */
    public int getRetryCount() {
        return mRetryCount;
    }

    /** 全局的缓存模式 */
    public OkHttp setCacheMode(CacheMode cacheMode) {
        mCacheMode = cacheMode;
        return this;
    }

    /** 获取全局的缓存模式 */
    public CacheMode getCacheMode() {
        return mCacheMode;
    }

    /** 全局的缓存过期时间 */
    public OkHttp setCacheTime(long cacheTime) {
        if (cacheTime <= -1) cacheTime = CacheEntity.CACHE_NEVER_EXPIRE;
        mCacheTime = cacheTime;
        return this;
    }

    /** 获取全局的缓存过期时间 */
    public long getCacheTime() {
        return mCacheTime;
    }

    /** 获取全局公共请求参数 */
    public HttpParams getCommonParams() {
        return mCommonParams;
    }

    /** 添加全局公共请求参数 */
    public OkHttp addCommonParams(HttpParams commonParams) {
        if (mCommonParams == null) mCommonParams = new HttpParams();
        mCommonParams.put(commonParams);
        return this;
    }

    /** 获取全局公共请求头 */
    public HttpHeaders getCommonHeaders() {
        return mCommonHeaders;
    }

    /** 添加全局公共请求参数 */
    public OkHttp addCommonHeaders(HttpHeaders commonHeaders) {
        if (mCommonHeaders == null) mCommonHeaders = new HttpHeaders();
        mCommonHeaders.put(commonHeaders);
        return this;
    }

    /** 添加全局拦截器 */
    public OkHttp addInterceptor(Interceptor interceptor) {
        okHttpClientBuilder.addInterceptor(interceptor);
        return this;
    }

    /** 根据Tag取消请求 */
    public void cancelTag(Object tag) {
        for (Call call : getOkHttpClient().dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : getOkHttpClient().dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }

    /** 取消所有请求请求 */
    public void cancelAll() {
        for (Call call : getOkHttpClient().dispatcher().queuedCalls()) {
            call.cancel();
        }
        for (Call call : getOkHttpClient().dispatcher().runningCalls()) {
            call.cancel();
        }
    }
}
