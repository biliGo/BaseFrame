package com.lsl.base.net.interceptor;

import com.lsl.base.common.BLog;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;

/**
 * Created by Forrest
 * on 2017/6/19 14:20
 * OkHttp拦截器，主要用于打印日志
 */

public class HttpLoggingInterceptor implements Interceptor{

    private static final Charset UTF8 = Charset.forName("UTF-8");

    private volatile Level printLevel = Level.NONE;

    private java.util.logging.Level colorLevel;

    private Logger logger;

    public enum Level {
        NONE,       //不打印Log
        BASIC,      //只打印请求首行和响应首行
        HEADERS,    //打印请求和响应的所有 Header
        BODY        //多有的数据全部打印
    }

    public HttpLoggingInterceptor(String tag ){
        logger = Logger.getLogger(tag);
    }

    public void setPrintLevel(Level printLevel) {
        this.printLevel = printLevel;
    }

    public void setColorLevel(java.util.logging.Level colorLevel) {
        this.colorLevel = colorLevel;
    }

    public void log(String message){
        logger.log(colorLevel,message);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        //不打印日志
        if (printLevel == Level.NONE){
            return chain.proceed(request);
        }
        //请求日志拦截
        logForRequest(request , chain.connection());

        //执行请求，计算请求时间
        long startTime = System.nanoTime();
        //得到返回结果
        Response response;
        try {
            response = chain.proceed(request);
        }catch (Exception e){
            log("<-- Http Failed: " +e);
            throw  e;
        }

        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
        //响应日志拦截
        return logForResponse(response ,tookMs);
    }

    private void logForRequest(Request request , Connection connection) throws IOException{
        boolean logBody = (printLevel == Level.BODY);
        boolean logHeaders = (printLevel == Level.BODY || printLevel == Level.HEADERS);
        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;
        Protocol protocol =connection != null ? connection.protocol() : Protocol.HTTP_1_1;

        try {
            //打印请求开始的信息
            String requestStartMessage = "Start Request-->" + request.method() + " " + request.url() + " " +protocol;
            log(requestStartMessage);

            if (logHeaders){
                Headers headers =request.headers();
                for (int i =0 , count = headers.size() ; i < count ; i++ ){
                    log("\t" + headers.name(i) + ": " + headers.value(i));
                }

                log(" ");

                if (logBody && hasRequestBody){
                    if (isPlaintext(requestBody.contentType())){
                       bodyToString(request);
                    }else {
                        log("\tbody: maybe [file part] , too large too print , ignored!");
                    }
                }
            }
        }catch (Exception e){
            BLog.e(e);
        }finally {
            log("<--End Request " + request.method());
        }
    }

    private Response logForResponse(Response response , long tookMs){
        Response.Builder builder = response.newBuilder();
        Response clone = builder.build();
        ResponseBody responseBody = clone.body();
        boolean logBody = (printLevel == Level.BODY);
        boolean logHeaders = (printLevel == Level.BODY || printLevel == Level.HEADERS);

        try {
            log("Start Response-->" + clone.code() + " " + clone.message() + " " + clone.request().url() + " (" + tookMs + "ms) ");
            if (logHeaders) {
                Headers headers = clone.headers();
                for (int i = 0, count = headers.size(); i < count; i++) {
                    log("\t" + headers.name(i) + ": " + headers.value(i));
                }
                log(" ");
                if (logBody && HttpHeaders.hasBody(clone)) {
                    if (isPlaintext(responseBody.contentType())) {
                        String body = responseBody.string();
                        log("\tbody: "+body);
                        responseBody = ResponseBody.create(responseBody.contentType(),body);
                        return response.newBuilder().body(responseBody).build();
                    }else {
                        log("\tbody: maybe [file part] , too large too print ,ignored!" );
                    }
                }
            }
        }catch (Exception e){
            BLog.e(e);
        }finally {
            log("<--End Response");
        }
        return response;
    }

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    private static boolean isPlaintext(MediaType mediaType){
        if (mediaType == null) return false;
        if (mediaType.type() != null && mediaType.type().equals("text")){
            return true;
        }

        String subtype = mediaType.subtype();
        if (subtype !=null ){
            subtype = subtype.toLowerCase();
            if (subtype.contains("x-www-form-urlencoded") ||
                subtype.contains("json") ||
                subtype.contains("xml") ||
                subtype.contains("html")){
                return true;

            }
        }
        return false;
    }

    private void bodyToString (Request request){
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            Charset charset = UTF8;
            MediaType contentType = copy.body().contentType();
            if (contentType != null ){
                charset = contentType.charset(UTF8);
            }
            log("\tbody: " + buffer.readString(charset));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
