package com.bitnic.bitnicorm;

import android.annotation.SuppressLint;

class SelectBuilder {
    static String getSql( CacheMetaData d,String where) {

        if(where==null||where.isEmpty()){
            where="";
        }else {
            where = String.format("WHERE %s", where);
        }

        return String.format("SELECT %s FROM %s %s;", String.join(",", d.getStringSelect()), d.tableName, where);

    }
    static String getSqlFree( String selectFields,String tableName,String where) {

        if(where==null||where.isEmpty()){
            where="";
        }else {
            where = String.format("WHERE %s", where);
        }

        return String.format("SELECT %s FROM %s %s;", String.join(",", selectFields), tableName, where);

    }

     static String getSqlDistinct(String columnName, CacheMetaData d,String where) {

         if(where==null||where.isEmpty()){
             where="";
         }else {
             where = String.format("WHERE %s", where);
         }

         return String.format("SELECT DISTINCT  %s FROM %s %s;", columnName, d.tableName, where);

     }

    @SuppressLint("DefaultLocale")
    static String getSqlLimit(CacheMetaData d, String where, int limit) {

        if(where==null||where.isEmpty()){
            where= " LIMIT "+limit;
        }else {
            where = String.format("WHERE %s LIMIT %d", where, limit);
        }

        return String.format("SELECT %s FROM %s %s;", String.join(",", d.getStringSelect()), d.tableName, where);

    }
}
