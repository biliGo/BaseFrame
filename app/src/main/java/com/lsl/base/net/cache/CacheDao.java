package com.lsl.base.net.cache;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

/**
 * Created by Forrest
 * on 2017/6/25 15:57
 */

public class CacheDao<T> extends DataBaseDao<CacheEntity<T>> {

    public CacheDao(){
        super(new CacheHelper());
    }

    /** 根据key获取缓存*/
    public CacheEntity<T> get(String key){
        String selection = CacheHelper.KEY + "=?";
        String[] selectionArgs = new String[]{key};
        List<CacheEntity<T>> cacheEntities = get(selection, selectionArgs);
        return cacheEntities.size() > 0 ? cacheEntities.get(0) : null;
    }

    /** 移除一个缓存 */
    public boolean remove(String key){
        String whereClause = CacheHelper.KEY + "=?";
        String[] whereArgs = new String[]{key};
        int delete = delete(whereClause, whereArgs);
        return delete > 0;
    }

    @Override
    protected String getTableName() {
        return CacheHelper.TABLE_NAME;
    }

    @Override
    public CacheEntity<T> parseCursorToBean(Cursor cursor) {
        return CacheEntity.parseCursorToBean(cursor);
    }

    @Override
    public ContentValues getContentValues(CacheEntity<T> cacheEntity) {
        return CacheEntity.getContentValues(cacheEntity);
    }
}
