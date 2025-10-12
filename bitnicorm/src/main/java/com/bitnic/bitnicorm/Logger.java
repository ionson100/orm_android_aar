package com.bitnic.bitnicorm;
/********************************************************************
 * Copyright © 2016-2017 OOO Bitnic                                 *
 * Created by OOO Bitnic on 08.02.16   corp@bitnic.ru               *
 * ******************************************************************/


import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteQuery;
import android.util.Log;

import java.lang.reflect.Field;


class Logger {

    private Logger(){}
    private final static boolean isWrite = Configure.IsWriteLog;

    static  void  LogE(String msg) {
        if (isWrite) {
            Log.e("____ORM____", msg);
        }

    }

    static  void LogI(String msg) {
        if (isWrite) {
            Log.i("____ORM____", msg);
        }

    }

    static  void printSql(Cursor cursor) {
        if (isWrite) {
            try {
                @SuppressLint("PrivateApi") Field mQuery = cursor.getClass().getDeclaredField("mQuery");
                mQuery.setAccessible(true);
                SQLiteQuery v = (SQLiteQuery) mQuery.get(cursor);
                if(v!=null){
                    Logger.LogI(v.toString());
                }

            } catch (Exception ignored) {

            }
        }
    }
}
