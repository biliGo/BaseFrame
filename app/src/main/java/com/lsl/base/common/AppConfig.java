package com.lsl.base.common;

import android.app.Application;
import android.content.Context;
import com.lsl.base.net.OkHttp;
import com.lsl.base.net.cache.CacheEntity;
import com.lsl.base.net.cache.CacheMode;
import com.lsl.base.net.cookie.store.MemoryCookieStore;
import com.lsl.base.net.cookie.store.PersistentCookieStore;
import com.lsl.base.net.model.HttpHeaders;
import com.lsl.base.net.model.HttpParams;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;
import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by Forrest
 * on 2017/7/3 10:12
 */

public class AppConfig {

    /**
     * APP的初始化
     * @param context
     */
    public static void init(Application context){
         initOkHttp(context);

    }

    public static void initOkHttp(Application context){
        //---------这里给出的是示例代码,告诉你可以这么传,实际使用的时候,根据需要传,不需要就不传-------------//
        HttpHeaders headers = new HttpHeaders();
//        headers.put("commonHeaderKey1", "commonHeaderValue1");    //header不支持中文，不允许有特殊字符
//        headers.put("commonHeaderKey2", "commonHeaderValue2");
        HttpParams params = new HttpParams();
//        params.put("commonParamsKey1", "commonParamsValue1");     //param支持中文,直接传,不要自己编码
//        params.put("commonParamsKey2", "这里支持中文参数");
        //-----------------------------------------------------------------------------------//

        //必须调用初始化
        OkHttp.init(context);

        //以下设置的所有参数是全局参数,同样的参数可以在请求的时候再设置一遍,那么对于该请求来讲,请求中的参数会覆盖全局参数
        //好处是全局参数统一,特定请求可以特别定制参数
        try {
            //一下都不是必须的，根据需要自行选择，一般来说只需要 debug，缓存相关，cookie相关的 就可以了
            OkHttp.getInstance()
                    // 打开该调试开关,打印级别INFO,并不是异常,是为了显眼,不需要就不要加入该行
                    // 最后的true表示是否打印OkHttp的内部异常，一般打开方便调试错误
                    .debug("OkHttp", Level.INFO,true)

                    //如果使用默认的 60秒，以下三行也不需要更改
                    .setConnectTimeout(OkHttp.DEFAULT_MILLISECONDS)   //全局的连接超时时间
                    .setReadTimeOut(OkHttp.DEFAULT_MILLISECONDS)      //全局的读取超时时间
                    .setWriteTimeOut(OkHttp.DEFAULT_MILLISECONDS)     //全局的写入超时时间

                    //可以全局统一设置缓存模式，默认是不使用缓存的，可以不传
                    .setCacheMode(CacheMode.NO_CACHE)

                    //可以全局统一设置缓存时间，默认永不过期
                    .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)

                    //可以全局统一设置超时重连次数，默认三次，那么最差的情况会请求4次（1次原始请求，3次重连请求）不需要可以设置为0
                    .setRetryCount(3)
                    //如果不想让框架管理cookie（或者叫session的保持），以下不需要
//                    .setCookieStore(new MemoryCookieStore())         //cookie使用内存缓存，(app退出后，cookie消失)
                    .setCookieStore(new PersistentCookieStore())     //cookie持久化存储，如果cookie不过期，则一直有效

                    //可以设置https的证书，以下几种方案根据需要自己设置
                    .setCertificates()                                        //方法一：信任所有证书，不安全有风险
//                    .setCertificates(new SafeTrustManager())                //方法二：自定义信任规则，校验服务端证书
//                    .setCertificates(context.getAssets().open("srca.cer"))  //方法三：使用预埋证书，校验服务端证书（自签名证书）
                      //方法四：使用bks证书和密码管理客户端证书(双向认证)，使用预埋证书，校验服务端证书（自签名证书）
//                    .setCertificates(context.getAssets().open("xx.bks"),"123456",context.getAssets().open("yyy.cer"))


                    //配置https的域名匹配规则，详细看demo的初始化介绍，不需要就不要加入，使用不当会导致https握手失败
//                    .setHostnameVerifier(new SafeHostnameVerifier())

                    //可以添加全局拦截器，不需要就不要加入，错误写法直接导致任何回调不执行
//                    .addInterceptor(new Interceptor() {
//                        @Override
//                        public Response intercept(Chain chain) throws IOException {
//                            return chain.proceed(chain.request());
//                        }
//                    })

                    .addCommonHeaders(headers)    //设置全局公共头
                    .addCommonParams(params);     //设置全局公共参数

        }catch (Exception e){
            e.printStackTrace();
        }



    }


    /**
     * 这里只是我谁便写的认证规则，具体每个业务是否需要验证，以及验证规则是什么，请与服务端或者leader确定
     * 这里只是我谁便写的认证规则，具体每个业务是否需要验证，以及验证规则是什么，请与服务端或者leader确定
     * 这里只是我谁便写的认证规则，具体每个业务是否需要验证，以及验证规则是什么，请与服务端或者leader确定
     * 重要的事情说三遍，以下代码不要直接使用
     */
    private static class SafeTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            try {
                for (X509Certificate certificate : chain) {
                    certificate.checkValidity(); //检查证书是否过期，签名是否通过等
                }
            } catch (Exception e) {
                throw new CertificateException(e);
            }
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    /**
     * 这里只是我谁便写的认证规则，具体每个业务是否需要验证，以及验证规则是什么，请与服务端或者leader确定
     * 这里只是我谁便写的认证规则，具体每个业务是否需要验证，以及验证规则是什么，请与服务端或者leader确定
     * 这里只是我谁便写的认证规则，具体每个业务是否需要验证，以及验证规则是什么，请与服务端或者leader确定
     * 重要的事情说三遍，以下代码不要直接使用
     */
    private static class SafeHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            //验证主机名是否匹配
//            return hostname.equals("server.jeasonlzy.com");
            return true;
        }
    }

}
