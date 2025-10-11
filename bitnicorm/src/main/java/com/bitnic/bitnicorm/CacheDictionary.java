package com.bitnic.bitnicorm;
/********************************************************************
 * Copyright © 2016-2017 OOO Bitnic                                 *
 * Created by OOO Bitnic on 08.02.16   corp@bitnic.ru               *
 * ******************************************************************/
import java.util.Dictionary;
import java.util.Hashtable;



class CacheDictionary {

    private static final Object lock = new Object();
    private static final Object lockFree = new Object();

    private static final Dictionary<String, CacheMetaData> dic = new Hashtable();
    private static final Dictionary<String, CacheMetaDataFree> dicFree = new Hashtable();

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
