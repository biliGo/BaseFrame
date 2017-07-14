package com.lsl.base.net.request;

import android.text.TextUtils;

import com.lsl.base.net.OkHttp;
import com.lsl.base.net.adapter.CacheCall;
import com.lsl.base.net.adapter.Call;
import com.lsl.base.net.adapter.CallAdapter;
import com.lsl.base.net.adapter.DefaultCallAdapter;
import com.lsl.base.net.cache.CacheEntity;
import com.lsl.base.net.cache.CacheMode;
import com.lsl.base.net.callback.AbsCallback;
import com.lsl.base.net.convert.Converter;
import com.lsl.base.net.model.HttpHeaders;
import com.lsl.base.net.model.HttpParams;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Forrest
 * on 2017/6/18 15:49
 * 所有请求的基类，其中泛型 R 主要用于属性设置方法后，返回对应的子类型，以便于实现链试调用
 */

public abstract class BaseRequest<R extends BaseRequest> {
    protected String mUrl;
    protected String mMethod;
    protected String mBaseUrl;
    protected Object mTag;
    protected long mReadTimeOut;
    protected long mWriteTimeOut;
    protected long mConnectTimeOut;
    protected int mRetryCount;
    protected CacheMode mCacheMode;
    protected String mCacheKey;
    protected long mCacheTime = CacheEntity.CACHE_NEVER_EXPIRE;      //默认缓存的超时时间
    protected HttpParams mParams = new HttpParams();                 //添加的param
    protected HttpHeaders mHeaders = new HttpHeaders();              //添加的header
    protected List<Interceptor> mInterceptors = new ArrayList<>();   //额外的拦截器

    private AbsCallback mCallback;
    private Converter mConverter;
    private Request mRequest;

    public BaseRequest(String url){
        this.mUrl = url;
        mBaseUrl = url;
        OkHttp okHttp = OkHttp.getInstance();
        //默认添加 Accept-Language
        String acceptLanguage = HttpHeaders.getAcceptLanguage();
        if (!TextUtils.isEmpty(acceptLanguage))headers(HttpHeaders.HEAD_KEY_ACCEPT_LANGUAGE,acceptLanguage);
        //默认添加User-Agent
        String userAgent = HttpHeaders.getUserAgent();
        if (!TextUtils.isEmpty(userAgent)) headers(HttpHeaders.HEAD_KEY_USER_AGENT,userAgent);
        //添加公共参数
        if (okHttp.getCommonHeaders() != null) mHeaders.put(okHttp.getCommonHeaders());
        if (okHttp.getCommonParams() != null) mParams.put(okHttp.getCommonParams());
        //添加缓存模式
        if (okHttp.getCacheMode() !=null) mCacheMode = okHttp.getCacheMode();
        mCacheTime =okHttp.getCacheTime();
        mRetryCount = okHttp.getRetryCount();

    }

    @SuppressWarnings("unchecked")
    public R url(String url) {
        this.mUrl = url;
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R tag(Object tag) {
        this.mTag = tag;
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R readTimeOut(long readTimeOut) {
        this.mReadTimeOut = readTimeOut;
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R writeTimeOut(long writeTimeOut) {
        this.mWriteTimeOut = writeTimeOut;
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R connTimeOut(long connTimeOut) {
        this.mConnectTimeOut = connTimeOut;
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R cacheMode(CacheMode cacheMode) {
        this.mCacheMode = cacheMode;
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R cacheKey(String cacheKey) {
        this.mCacheKey = cacheKey;
        return (R) this;
    }
    /** 传入 -1 表示永久有效,默认值即为 -1 */
    @SuppressWarnings("unchecked")
    public R cacheTime(long cacheTime) {
        if (cacheTime <= -1) cacheTime = CacheEntity.CACHE_NEVER_EXPIRE;
        this.mCacheTime = cacheTime;
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R headers(HttpHeaders headers) {
        this.mHeaders.put(headers);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R headers(String key, String value) {
        mHeaders.put(key, value);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R removeHeader(String key) {
        mHeaders.remove(key);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R removeAllHeaders() {
        mHeaders.clear();
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(HttpParams params) {
        this.mParams.put(params);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(Map<String, String> params, boolean... isReplace) {
        this.mParams.put(params, isReplace);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(String key, String value, boolean... isReplace) {
        mParams.put(key, value, isReplace);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(String key, int value, boolean... isReplace) {
        mParams.put(key, value, isReplace);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(String key, float value, boolean... isReplace) {
        mParams.put(key, value, isReplace);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(String key, double value, boolean... isReplace) {
        mParams.put(key, value, isReplace);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(String key, long value, boolean... isReplace) {
        mParams.put(key, value, isReplace);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(String key, char value, boolean... isReplace) {
        mParams.put(key, value, isReplace);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(String key, boolean value, boolean... isReplace) {
        mParams.put(key, value, isReplace);
        return (R) this;
    }
    @SuppressWarnings("unchecked")
    public R addUrlParams(String key, List<String> values) {
        mParams.putUrlParams(key, values);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R removeParam(String key) {
        mParams.remove(key);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R removeAllParams() {
        mParams.clear();
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R setCallback(AbsCallback callback) {
        this.mCallback = callback;
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R addInterceptor(Interceptor interceptor) {
        mInterceptors.add(interceptor);
        return (R) this;
    }
    /** 默认返回第一个参数 */
    public String getUrlParam(String key) {
        List<String> values = mParams.urlParamsMap.get(key);
        if (values != null && values.size() > 0) return values.get(0);
        return null;
    }

    /** 默认返回第一个参数 */
    public HttpParams.FileWrapper getFileParam(String key) {
        List<HttpParams.FileWrapper> values = mParams.fileParamsMap.get(key);
        if (values != null && values.size() > 0) return values.get(0);
        return null;
    }
    public HttpParams getParams() {
        return mParams;
    }

    public HttpHeaders getHeaders() {
        return mHeaders;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getBaseUrl() {
        return mBaseUrl;
    }

    public Object getTag() {
        return mTag;
    }

    public CacheMode getCacheMode() {
        return mCacheMode;
    }

    public void setCacheMode(CacheMode cacheMode) {
        this.mCacheMode = cacheMode;
    }

    public String getCacheKey() {
        return mCacheKey;
    }

    public void setCacheKey(String cacheKey) {
        this.mCacheKey = cacheKey;
    }

    public long getCacheTime() {
        return mCacheTime;
    }

    public int getRetryCount() {
        return mRetryCount;
    }

    public Request getRequest() {
        return mRequest;
    }

    public AbsCallback getCallback() {
        return mCallback;
    }

    public Converter getConverter() {
        return mConverter;
    }

    /**
     * 返回当前的请求方法
     * GET,POST,HEAD,PUT,DELETE,OPTIONS
     */
    public String getMethod() {
        return mMethod;
    }

    /** 根据不同的请求方式和参数，生成不同的RequestBody */
    public abstract RequestBody generateRequestBody();

    public RequestBody wrapRequestBody(RequestBody requestBody){
        ProgressRequestBody progressRequestBody = new ProgressRequestBody(requestBody);
        progressRequestBody.setListener(new ProgressRequestBody.Listener() {
            @Override
            public void onRequestProgress(final long bytesWritten, final long contentLength, final long networkSpeed) {
                OkHttp.getInstance().getDelivery().post(new Runnable() {
                    @Override
                    public void run() {
                        if (mCallback != null) mCallback.upProgress(bytesWritten,contentLength,bytesWritten*1.0f/contentLength,networkSpeed);
                    }
                });
            }
        });
        return progressRequestBody;
    }
    /** 根据不同的请求方式，将RequestBody转换成Request对象*/
    public abstract Request generateRequest(RequestBody requestBody);

    /**根据当前的请求参数，生成对应的Call任务*/
    public okhttp3.Call generateCall(Request request){
        mRequest = request;
        if (mReadTimeOut <= 0 && mWriteTimeOut <=0 && mConnectTimeOut <=0 && mInterceptors.size() == 0){
            return OkHttp.getInstance().getOkHttpClient().newCall(request);
        }else {
            OkHttpClient.Builder newClientBuilder = OkHttp.getInstance().getOkHttpClient().newBuilder();
            if (mReadTimeOut >0 ) newClientBuilder.readTimeout(mReadTimeOut, TimeUnit.MILLISECONDS);
            if (mWriteTimeOut > 0) newClientBuilder.writeTimeout(mWriteTimeOut, TimeUnit.SECONDS);
            if (mConnectTimeOut > 0) newClientBuilder.connectTimeout(mConnectTimeOut ,TimeUnit.SECONDS);
            if (mInterceptors.size() > 0){
                for (Interceptor interceptor : mInterceptors){
                    newClientBuilder.addInterceptor(interceptor);
                }
            }
            return newClientBuilder.build().newCall(request);
        }
    }

    /** 获取同步call对象 */
    public okhttp3.Call getCall() {
        //构建请求体，返回call对象
        RequestBody requestBody = generateRequestBody();
        mRequest = generateRequest(wrapRequestBody(requestBody));
        return generateCall(mRequest);
    }

    /** Rx支持,获取同步call对象 */
    public <T> Call<T> getCall(Converter<T> converter) {
        mConverter = converter;
        return DefaultCallAdapter.<T>create().adapt(new CacheCall<T>(this));
    }

    /** Rx支持,获取同步call对象 */
    public <T, E> E getCall(Converter<T> converter, CallAdapter<E> adapter) {
        mConverter = converter;
        return adapter.adapt(getCall(converter));
    }

    /** 普通调用，阻塞方法，同步请求执行 */
    public Response execute() throws IOException {
        return getCall().execute();
    }

    /** 非阻塞方法，异步请求，但是回调在子线程中执行 */
    @SuppressWarnings("unchecked")
    public <T> void execute(AbsCallback<T> callback) {
        mCallback = callback;
        mConverter = callback;
        new CacheCall<T>(this).execute(callback);
    }
}
