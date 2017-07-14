package com.lsl.base.net.cache;

import android.content.ContentValues;
import android.database.Cursor;

import com.lsl.base.common.BLog;
import com.lsl.base.net.model.HttpHeaders;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by Forrest
 * on 2017/6/21 11:59
 * 缓存实体
 */

public class CacheEntity<T> implements Serializable {
    private static final long serialVersionUID = -4337711009801627866L;
    public static final long CACHE_NEVER_EXPIRE = -1;        //缓存永不过期

    private long id;
    private String key;
    private HttpHeaders responseHeaders;
    private T data;
    private long localExpire;

    //该变量不必保存到数据库,程序运行起来后会动态计算
    private boolean isExpire;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public HttpHeaders getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(HttpHeaders responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public long getLocalExpire() {
        return localExpire;
    }

    public void setLocalExpire(long localExpire) {
        this.localExpire = localExpire;
    }

    public boolean isExpire() {
        return isExpire;
    }

    public void setExpire(boolean expire) {
        isExpire = expire;
    }

    /**
     * @param cacheTime 允许的缓存时间
     * @param baseTime  基准时间,小于当前时间视为过期
     * @return 是否过期
     */
    public boolean checkExpire(CacheMode cacheMode, long cacheTime, long baseTime){
        if (cacheMode == CacheMode.DEFAULT) return getLocalExpire() < baseTime;
        if (cacheTime == CACHE_NEVER_EXPIRE) return false;
        return getLocalExpire() + cacheTime < baseTime;
    }

    public static <T>ContentValues getContentValues(CacheEntity<T> cacheEntity) {
        ContentValues values = new ContentValues();
        values.put(CacheHelper.KEY, cacheEntity.getKey());
        values.put(CacheHelper.LOCAL_EXPIRE, cacheEntity.getLocalExpire());

        HttpHeaders headers = cacheEntity.getResponseHeaders();
        ByteArrayOutputStream headersBAOS = null;
        ObjectOutputStream headersOOS = null;
        try {
            if (headers != null) {
                headersBAOS = new ByteArrayOutputStream();
                headersOOS = new ObjectOutputStream(headersBAOS);
                headersOOS.writeObject(headers);
                headersOOS.flush();
                byte[] headersData = headersBAOS.toByteArray();
                values.put(CacheHelper.HEAD, headersData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (headersOOS != null) headersOOS.close();
                if (headersBAOS != null) headersBAOS.close();
            } catch (IOException e) {
                BLog.e(e);
            }
        }
        T data = cacheEntity.getData();
        ByteArrayOutputStream dataBAOS = null;
        ObjectOutputStream dataOOS = null;
        try {
            if (data != null) {
                dataBAOS = new ByteArrayOutputStream();
                dataOOS = new ObjectOutputStream(dataBAOS);
                dataOOS.writeObject(data);
                dataOOS.flush();
                byte[] dataData = dataBAOS.toByteArray();
                values.put(CacheHelper.DATA, dataData);
            }
        } catch (IOException e) {
            BLog.e(e);
        } finally {
            try {
                if (dataOOS != null) dataOOS.close();
                if (dataBAOS != null) dataBAOS.close();
            } catch (IOException e) {
                BLog.e(e);

            }
        }
        return values;
    }

    public static <T> CacheEntity<T> parseCursorToBean(Cursor cursor){
        CacheEntity<T> cacheEntity = new CacheEntity<>();
        cacheEntity.setId(cursor.getInt(cursor.getColumnIndex(CacheHelper.ID)));
        cacheEntity.setKey(cursor.getString(cursor.getColumnIndex(CacheHelper.KEY)));
        cacheEntity.setLocalExpire(cursor.getLong(cursor.getColumnIndex(CacheHelper.LOCAL_EXPIRE)));

        byte[] headerData = cursor.getBlob(cursor.getColumnIndex(CacheHelper.HEAD));
        ByteArrayInputStream headerBAIS = null;
        ObjectInputStream headerOIS = null;
        try {
            if (headerData != null) {
                headerBAIS = new ByteArrayInputStream(headerData);
                headerOIS = new ObjectInputStream(headerBAIS);
                Object header = headerOIS.readObject();
                cacheEntity.setResponseHeaders((HttpHeaders) header);
            }
        } catch (Exception e) {
            BLog.e(e);
        } finally {
            try {
                if (headerOIS != null) headerOIS.close();
                if (headerBAIS != null) headerBAIS.close();
            } catch (IOException e) {
                BLog.e(e);
            }
        }

        byte[] dataData = cursor.getBlob(cursor.getColumnIndex(CacheHelper.DATA));
        ByteArrayInputStream dataBAIS = null;
        ObjectInputStream dataOIS = null;
        try {
            if (dataData != null) {
                dataBAIS = new ByteArrayInputStream(dataData);
                dataOIS = new ObjectInputStream(dataBAIS);
                T data = (T) dataOIS.readObject();
                cacheEntity.setData(data);
            }
        } catch (Exception e) {
            BLog.e(e);
        } finally {
            try {
                if (dataOIS != null) dataOIS.close();
                if (dataBAIS != null) dataBAIS.close();
            } catch (IOException e) {
                BLog.e(e);
            }
        }

        return cacheEntity;

    }

    @Override
    public String toString() {
        return "CacheEntity{" +
                "id=" + id +
                ", key='" + key + '\'' +
                ", responseHeaders=" + responseHeaders +
                ", data=" + data +
                ", localExpire=" + localExpire +
                '}';
    }
}
