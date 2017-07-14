package com.lsl.base.net.utils;

import com.lsl.base.common.BLog;
import com.lsl.base.net.model.HttpHeaders;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.Request;

/**
 * Created by Forrest
 * on 2017/6/25 14:58
 */

public class HttpUtils {

    /** 将传递进来的参数拼接成url*/
    public static String createUrlFromParams(String url, Map<String,List<String>> params){
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(url);
            if (url.indexOf('&') > 0 || url.indexOf('?') > 0) sb.append("&");
            else sb.append("?");
            for (Map.Entry<String, List<String>> urlParams : params.entrySet()){
                List<String> urlValues = urlParams.getValue();
                for (String value : urlValues){
                    String urlValue = URLEncoder.encode(value, "UTF-8");
                    sb.append(urlParams.getKey()).append("=").append(urlValue).append("&");
                }
            }
            sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            BLog.e(e);
        }
        return url;
    }

    /** 通用的拼接请求头 */
    public static Request.Builder appendHeaders(HttpHeaders headers) {
        Request.Builder requestBuilder = new Request.Builder();
        if (headers.headersMap.isEmpty()) return requestBuilder;
        Headers.Builder headerBuilder = new Headers.Builder();
        try {
            for (Map.Entry<String, String> entry : headers.headersMap.entrySet()) {
                //对头信息进行 utf-8 编码,防止头信息传中文,这里暂时不编码,可能出现未知问题,如有需要自行编码
//                String headerValue = URLEncoder.encode(entry.getValue(), "UTF-8");
                headerBuilder.add(entry.getKey(), entry.getValue());
            }
        } catch (Exception e) {
            BLog.e(e);
        }
        requestBuilder.headers(headerBuilder.build());
        return requestBuilder;
    }
}
