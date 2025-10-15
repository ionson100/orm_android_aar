package com.bitnic.bitnicorm;
/********************************************************************
 * Copyright © 2016-2017 OOO Bitnic                                 *
 * Created by OOO Bitnic on 08.02.16   corp@bitnic.ru               *
 * ******************************************************************/
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;


class CacheDictionary {

    private static final Object lock = new Object();
    private static final Object lockFree = new Object();

    private static final Map<String, CacheMetaData> dic = new Hashtable();
    private static final Map<String, CacheMetaDataFree> dicFree = new Hashtable();

    public static CacheMetaData<?> getCacheMetaDataFromTableName(String tableName)  {
        AtomicReference<CacheMetaData> metaData= new AtomicReference<>();
        dic.forEach((s, cacheMetaData) -> {
            if(cacheMetaData.tableName.equals(tableName)){
                metaData.set(cacheMetaData);
            }
        });
        return metaData.get();

    }

    public static CacheMetaData<?> getCacheMetaData(Class aClass)  {
        if (dic.get(aClass.getName()) == null) {
            synchronized (lock) {
                if (dic.get(aClass.getName()) == null) {
                    dic.put(aClass.getName(), new CacheMetaData(aClass));
                }
            }
        }
        return dic.get(aClass.getName());
    }
    public static CacheMetaDataFree<?> getCacheMetaDataFree(Class aClass)  {
        if (dicFree.get(aClass.getName()) == null) {
            synchronized (lockFree) {
                if (dicFree.get(aClass.getName()) == null) {
                    dicFree.put(aClass.getName(), new CacheMetaDataFree(aClass));
                }
            }
        }
        return dicFree.get(aClass.getName());
    }

}
