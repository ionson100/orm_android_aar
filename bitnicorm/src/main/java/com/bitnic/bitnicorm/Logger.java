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

    static  void  E(String msg) {
        if (Configure.IsWriteLog) {
            Log.e("____ORM____", msg);
        }
    }

    static  void I(String msg) {
        if (Configure.IsWriteLog) {
            Log.i("____ORM____", msg);
        }

    }

    static  void printSql(Cursor cursor) {
        if (Configure.IsWriteLog) {
            try {
                @SuppressLint("PrivateApi") Field mQuery = cursor.getClass().getDeclaredField("mQuery");
                mQuery.setAccessible(true);
                SQLiteQuery v = (SQLiteQuery) mQuery.get(cursor);
                if(v!=null){
                    Logger.I(v.toString());
                }

            } catch (Exception ignored) {

            }
        }
    }
}
