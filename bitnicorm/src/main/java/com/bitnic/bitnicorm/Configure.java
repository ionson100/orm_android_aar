package com.bitnic.bitnicorm;
/********************************************************************
 * Copyright © 2016-2017 OOO Bitnic                                 *
 * Created by OOO Bitnic on 08.02.16   corp@bitnic.ru               *
 * ******************************************************************/

import static com.bitnic.bitnicorm.Utils.getStringListSqlCreateTable;
import static com.bitnic.bitnicorm.Utils.partition;
import static com.bitnic.bitnicorm.UtilsCompound.Compound;
import static com.bitnic.bitnicorm.UtilsCompound.CompoundFree;
import static com.bitnic.bitnicorm.UtilsContentValues.checkFieldValue;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Root type for working with ORM
 */
public class Configure implements ISession {


    static boolean IsWriteLog = false;
    static String dataBaseName;
    private static DataBaseHelper myDbHelper;

    private SQLiteDatabase sqLiteDatabaseForReadable = null;
    private SQLiteDatabase sqLiteDatabaseForWritable = null;

    private Configure() {
        sqLiteDatabaseForReadable = GetSqLiteDatabaseForReadable();
        sqLiteDatabaseForWritable = GetSqLiteDatabaseForWritable();
    }

    /**
     * Configuration initialization, called once at application startup, creates the database file if it does not exist
     * @param dataBaseName dataBaseName database file name with full path
     * @param version      database file version
     * @param context      context App
     *
     *                     <pre>
     *                     {@code
     *                      try {
     *                        new Configure("db.sqlite",3,appContext);
     *                      } catch (Exception e) {
     *                         throw new RuntimeException(e);
     *                      }
     *                     }
     *                     </pre>
     */
    public Configure(String dataBaseName, int version, Context context) {
        InitCtor(dataBaseName, version, context);
    }

    /**
     * Configuration initialization, called once at application startup, creates the database file if it does not exist
     * @param dataBaseName database file name with full path
     * @param version      database file version
     * @param context      context App
     * @param classList    list of DTO types, upon initialization, tables are automatically created based on the type if the table does not exist
     * @param isWriteLog   force logging, only for debugging the application
     *                     <pre>
     *                     {@code
     *                      List<Class> classList=new ArrayList<>();
     *                      classList.add(MyTable.class);
     *                      try {
     *                        new Configure("db.sqlite",3,appContext,classList,true);
     *                      } catch (Exception e) {
     *                         throw new RuntimeException(e);
     *                      }
     *                     }
     *                     </pre>
     */
    @SuppressLint("NewApi")
    public Configure(String dataBaseName, int version, Context context, List<Class> classList, boolean isWriteLog) {
        Configure.IsWriteLog = isWriteLog;
        InitCtor(dataBaseName, version, context, database -> {

            database.beginTransaction();
            try {
                classList.forEach(aClass -> {
                    String sql = Configure.getSqlCreateTable(aClass, true);
                    database.execSQL(sql);
                });
                database.setTransactionSuccessful();

            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                database.endTransaction();
            }
        });

    }

    /**
     * Configuration initialization, called once at application startup, creates the database file if it does not exist
     * @param dataBaseName database file name with full path
     * @param version      database file version
     * @param context      context App
     * @param isWriteLog   force logging, only for debugging the application
     *
     *
     *                     <pre>
     *                     {@code
     *                     try {
     *                       new Configure("db.sqlite",3,appContext,true);
     *                     } catch (Exception e) {
     *                      throw new RuntimeException(e);
     *                     }
     *                     }
     *                     </pre>
     */
    public Configure(String dataBaseName, int version, Context context, boolean isWriteLog) {
        Configure.IsWriteLog = isWriteLog;
        InitCtor(dataBaseName, version, context);
    }

    private void InitCtor(String dataBaseName, int version, Context context) {
        Configure.dataBaseName = dataBaseName;
        myDbHelper = new DataBaseHelper(context, Configure.dataBaseName, version);
    }

    private void InitCtor(String dataBaseName, int version, Context context, IAction<SQLiteDatabase> iOnOpenHelper) {
        Configure.dataBaseName = dataBaseName;
        myDbHelper = new DataBaseHelper(context, Configure.dataBaseName, version, iOnOpenHelper);
    }


    /**
     * Getting the session object, now you can do something
     *
     * @return object {@link ISession}
     */
    public static ISession getSession() {
        return new Configure();
    }

    private static SQLiteDatabase GetSqLiteDatabaseForReadable() {
        return myDbHelper.openDataBaseForReadable();
    }

    private static SQLiteDatabase GetSqLiteDatabaseForWritable() {

        return myDbHelper.openDataBaseForWritable();
    }

    private <T> void createTableInner(Class<T> aClass, String ifNotExist) throws Exception {
        CacheMetaData<T> data = getCacheMetaData(aClass);
        if (data.keyColumn == null) {
            String msg = aClass.getName() + ": Поле первичного ключа отсутствует.";

            throw new Exception(msg);
        }

        List<String> sqlList = getStringListSqlCreateTable(ifNotExist, data);
        getStringAppend(data, sqlList);
        String curSql = null;
        try {
            for (String sqlOne : sqlList) {
                curSql = sqlOne;
                Logger.LogI(sqlOne);
                sqLiteDatabaseForWritable.execSQL(sqlOne);
            }

        } catch (Exception exception) {
            throw new Exception("Scope Create table,Exception on execute command: " + curSql);
        }

    }


    private static <T> void getStringAppend(@NonNull CacheMetaData<T> data, List<String> sqlList) {
        String tb = Utils.clearStringTrimRaw(data.tableName);
        for (ItemField f : data.listColumn) {
            if (!f.isIndex) continue;
            sqlList.add("CREATE INDEX IF NOT EXISTS " + tb + "_" + f.columnNameRaw + " ON " + data.tableName + " (" + f.columnName + "); \n");
        }
        if (data.appendCreateTable != null) {
            sqlList.add(data.appendCreateTable);
        }
    }

    @Override
    public String getPath() {
        return sqLiteDatabaseForWritable.getPath();
    }

    @Override
    public SQLiteDatabase SqLiteDatabaseForReadable() {
        return sqLiteDatabaseForReadable;
    }

    @Override
    public SQLiteDatabase SqLiteDatabaseForWritable() {
        return sqLiteDatabaseForWritable;
    }

    @Override
    public <T> int count(@NonNull Class<T> aClass) {
        return count(aClass, null);
    }

    @Override
    public <T> int count(@NonNull Class<T> aClass, String where, Object... objects) {
        CacheMetaData<T> d = getCacheMetaData(aClass);
        where = whereBuilderRaw(where, d);

        String sql = MessageFormat.format("SELECT COUNT(*) FROM {0} {1};", d.tableName, where);
        return (int) executeScalar(sql, objects);
    }

    @Override
    public <T> boolean any(@NonNull Class<T> aClass) {
        return any(aClass, null);
    }

    @Override
    public <T> boolean any(@NonNull Class<T> aClass, String where, Object... objects) {
        CacheMetaData<T> d = getCacheMetaData(aClass);
        where = whereBuilderRaw(where, d);
        String sql = MessageFormat.format(" SELECT EXISTS ( select * from {0}  {1});", d.tableName, where);
        int res = (int) executeScalar(sql, objects);
        return res == 1;
    }

    @Override
    public <T> int update(@NonNull T item) {
        CacheMetaData<T> d = getCacheMetaData(item.getClass());
        if(d.isPersistent){
            if(!((Persistent)item).isPersistent){
                throw new RuntimeException("You are trying to update a non-persistent object that is not in the database.");
            }
        }
        ContentValues contentValues =getInnerContentValues(item,d);
        int res;
        try {
            if (d.isIAction) {
                ((IEventOrm) item).beforeUpdate();
            }
            Object key = d.keyColumn.field.get(item);
            String where = whereBuilder(d.keyColumn.columnName + " = ?", d);
            String[] param = new String[]{String.valueOf(key)};
            res=sqLiteDatabaseForWritable.update(d.tableName, contentValues, where, param);
            Logger.LogI(Utils.getStringUpdate(d.tableName,contentValues,where));
            if(d.isPersistent){
                ((Persistent)item).isPersistent=true;
            }
            if (d.isIAction) {
                ((IEventOrm) item).afterUpdate();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return res;

    }

    @Override
    public <T> void insert(@NonNull T item) {
        CacheMetaData<T> d = getCacheMetaData(item.getClass());
        if(d.isPersistent){
            var per=((Persistent)item);
            if(per.isPersistent){
                throw new RuntimeException("You are trying to insert an object into the database that was previously retrieved from the database, which is not very logical.");
            }
        }

        if (d.isIAction) {
            ((IEventOrm) item).beforeInsert();
        }
        ContentValues contentValues =getInnerContentValues(item,d);
        boolean runTransaction = false;
        if (!sqLiteDatabaseForWritable.inTransaction() && !d.keyColumn.isAssigned) {
            beginTransaction();
            runTransaction = true;
        }
        try {
            sqLiteDatabaseForWritable.insertOrThrow(d.tableName, null, contentValues);
            Logger.LogI(Utils.getStringInsert(d.tableName,contentValues));
            if (!d.keyColumn.isAssigned) {
                var id = executeScalar("SELECT last_insert_rowid();");
                d.keyColumn.field.set(item, id);
            }
            if (runTransaction) {
                commitTransaction();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (runTransaction) {
                endTransaction();
            }
        }
        if(d.isPersistent){
            ((Persistent)item).isPersistent=true;
        }
        if (d.isIAction) {
            ((IEventOrm) item).afterInsert();
        }

    }

    @Override
    public <T> int delete(@NonNull T item) {

        CacheMetaData<T> data = getCacheMetaData(item.getClass());
        Object key;
        try {
            Field field = data.keyColumn.field;
            field.setAccessible(true);
            key = checkFieldValue(field, item);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (data.isIAction) {
            ((IEventOrm) item).beforeDelete();
        }
        int res = sqLiteDatabaseForWritable.delete(data.tableName, data.tableName + "." + data.keyColumn.columnName + "=?", new String[]{key.toString()});
        if (res != 0) {
            if (data.isIAction) {
                ((IEventOrm) item).afterDelete();
            }
        }
        return res;
    }


    @Override
    public <T> int updateRows(@NonNull Class<T> aClass, @NonNull PairColumnValue columnValues, String where, Object... objects) {

        CacheMetaData<T> d = getCacheMetaData(aClass);
        where = whereBuilder(where, d);
        ContentValues contentValues =  getInnerContentValuesForUpdate(d,columnValues);
        Logger.LogI("UPDATEALL WHERE: " + where);
        Logger.LogI(Utils.getStringUpdate(d.tableName,contentValues,where));
        return sqLiteDatabaseForWritable.update(d.tableName, contentValues, where, parametrize(objects));

    }


    static <T> String whereBuilderRaw(String where, CacheMetaData<T> data) {

        if (where == null || where.trim().isEmpty()) {
            where = "";
        }
        if ((data.where == null || data.where.trim().isEmpty()) && where.isEmpty()) {
            return "";
        }
        if ((data.where == null || data.where.trim().isEmpty()) && !where.isEmpty()) {
            return "WHERE " + where;
        }

        String t = "";
        if (data.where != null) {
            t = data.where;
        }
        return " WHERE " + t + ((where.trim().isEmpty()) ? " " : " and " + where) + " ";


    }

    static <T> String whereBuilder(String where, CacheMetaData<T> data) {

        if (where == null || where.trim().isEmpty()) {
            where = "";
        }
        if ((data.where == null || data.where.trim().isEmpty()) && where.isEmpty()) {
            return "";
        }
        if ((data.where == null || data.where.trim().isEmpty()) && !where.isEmpty()) {
            return where;
        }
        String t = "";
        if (data.where != null) {
            t = data.where;
        }

        return " " + t + ((where.trim().isEmpty()) ? " " : " and " + where) + " ";


    }

    public <T> List<T> getList(@NonNull Class<T> aClass) {
        return getList(aClass, null);
    }

    @Override
    public <T> List<T> getList(@NonNull Class<T> aClass, String where, Object... objects) {

        CacheMetaData<T> d = getCacheMetaData(aClass);
        where = whereBuilder(where, d);
        Cursor cursor = null;
        try {
            String sql = SelectBuilder.getSql(d, where);
            cursor = execSQLRaw(sql, objects);
            List<T> list = new ArrayList<>();
            if (cursor.moveToFirst()) {
                do {
                    T sd = aClass.newInstance();
                    if(d.isPersistent){
                        ((Persistent)sd).isPersistent=true;
                    }
                    Compound(d.listColumn, d.keyColumn, cursor, sd);
                    list.add(sd);
                } while (cursor.moveToNext());
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public <T> T firstOrDefault(@NonNull Class<T> aClass, String where, Object... objects) {
        Cursor cursor = null;
        CacheMetaData<T> d = getCacheMetaData(aClass);
        where = whereBuilder(where, d);
        String sql = SelectBuilder.getSqlLimit(d, where, 1);

        try {
            cursor = execSQLRaw(sql, objects);
            if (cursor.moveToFirst()) {
                T sd = aClass.newInstance();
                if(d.isPersistent){
                    ((Persistent)sd).isPersistent=true;
                }
                Compound(d.listColumn, d.keyColumn, cursor, sd);
                return sd;

            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    @Override
    public <T> T first(@NonNull Class<T> aClass, String where, Object... objects) throws Exception {
        T t = firstOrDefault(aClass, where, objects);
        if (t == null) {
            throw new Exception("!!!The sample did not yield any results.");
        }
        return t;
    }

    @Override
    public <T> T single(@NonNull Class<T> aClass, String where, Object... objects) throws Exception {
        Cursor cursor = null;
        CacheMetaData<T> d = getCacheMetaData(aClass);
        where = whereBuilder(where, d);
        String sql = SelectBuilder.getSqlLimit(d, where, 2);
        Logger.LogI(sql);
        Object[] resultArray = new Object[]{null, null};
        int index = 0;
        try {
            cursor = execSQLRaw(sql, objects);
            if (cursor.moveToFirst()) {
                do {
                    T sd = aClass.newInstance();
                    if(d.isPersistent){
                        ((Persistent)sd).isPersistent=true;
                    }
                    Compound(d.listColumn, d.keyColumn, cursor, sd);
                    resultArray[index] = sd;
                    index++;
                } while (cursor.moveToNext());

            }
            if(resultArray[0]==null&&resultArray[1]==null){
                throw new Exception("!!!No object was found matching the selection criteria.");
            }
            if (resultArray[1] == null) {
                return (T) resultArray[0];
            } else {
               throw new Exception("!!!There is more than one object by condition");
            }
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    @Override
    public <T> T singleOrDefault(@NonNull Class<T> aClass, String where, Object... objects) {
        Cursor cursor = null;
        CacheMetaData<T> d = getCacheMetaData(aClass);
        where = whereBuilder(where, d);
        String sql = SelectBuilder.getSqlLimit(d, where, 2);
        Logger.LogI(sql);
        Object[] resultArray = new Object[]{null, null};
        int index = 0;
        try {
            cursor = execSQLRaw(sql, objects);
            if (cursor.moveToFirst()) {
                do {
                    T sd = aClass.newInstance();
                    if(d.isPersistent){
                        ((Persistent)sd).isPersistent=true;
                    }
                    Compound(d.listColumn, d.keyColumn, cursor, sd);
                    resultArray[index] = sd;
                    index++;
                } while (cursor.moveToNext());

            }
            if (resultArray[1] == null) {
                return (T) resultArray[0];
            } else {
                return null;
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    static String[] parametrize(Object... objects) {
        String[] str = null;
        if (objects.length > 0) {
            str = new String[objects.length];
            for (int i = 0; i < objects.length; i++) {
                if (objects[i] instanceof byte[]) {

                    String hexString = bytesToHex((byte[]) objects[i]);
                    String strt = "0x" + hexString;
                    str[i] = strt;
                } else {
                    var s = String.valueOf(objects[i]);
                    str[i] = s;
                }

            }

        }
        return str;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length); // Initialize with estimated capacity

        for (byte b : bytes) {
            // Convert byte to int, ensuring it's treated as unsigned for hex conversion
            String hex = String.format("%02X", b);
            hexString.append(hex);
        }

        return hexString.toString();
    }

    @Override
    public <T, D> List<D> getListSelect(@NonNull Class<T> aClass,@NonNull String columnName, String where, Object... objects) {
        List<D> list;

        try {

            CacheMetaData<T> d = getCacheMetaData(aClass);
            where = whereBuilder(where, d);
            Cursor cursor;
            String[] sdd = new String[]{Utils.clearStringTrimRaw(columnName)};
            String[] str = parametrize(objects);
            cursor = sqLiteDatabaseForReadable.query(d.tableName, sdd, where, str, null, null, null, null);
            list = new ArrayList<>(cursor.getCount());
            Logger.printSql(cursor);
            try {
                if (cursor.moveToFirst()) {
                    do {

                        int columnType = cursor.getType(0);
                        switch (columnType) {
                            case Cursor.FIELD_TYPE_NULL: {
                                list.add(null);
                                continue;
                            }
                            case Cursor.FIELD_TYPE_STRING: {
                                list.add((D) cursor.getString(0));
                                continue;
                            }
                            case Cursor.FIELD_TYPE_INTEGER: {
                                list.add((D) (Object) cursor.getInt(0));
                                continue;
                            }
                            case Cursor.FIELD_TYPE_FLOAT: {
                                list.add((D) (Object) cursor.getFloat(0));
                                continue;
                            }
                            case Cursor.FIELD_TYPE_BLOB: {
                                list.add((D) cursor.getBlob(0));
                                continue;
                            }
                            default: {
                                throw new RuntimeException("не могу определить тип поля:" + columnType);
                            }
                        }
                    } while (cursor.moveToNext());
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            } finally {
                cursor.close();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public <T> void cursorIterator(@NonNull Class<T> aClass, Cursor cursor, IAction<T> function) {
        try (cursor) {
            CacheMetaDataFree<?> d = CacheDictionary.getCacheMetaDataFree(aClass);
            if (cursor.moveToFirst()) {
                do {
                    T sd = aClass.newInstance();
                    CompoundFree(d.listColumn, cursor, sd);
                    function.invoke(sd);
                } while (cursor.moveToNext());
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public <T> Map<Object, List<T>> groupBy(@NonNull Class<T> aClass, @NonNull String columnName, String where, Object... objects) {
        if (columnName.isEmpty()) {
            throw new ArithmeticException("Parameter columnName, empty or is null,");
        }
        CacheMetaData<T> d = getCacheMetaData(aClass);
        boolean isthis = false;
        for (int i = 0; i < d.listColumn.size(); i++) {
            ItemField field = d.listColumn.get(i);
            if (field.columnNameRaw.equals(columnName)) {
                isthis = true;
            }
        }
        if (!isthis) {
            throw new RuntimeException("The column with the name " + columnName + " was not found in the table " + d.tableName + ". " +
                    "Perhaps you meant the name of the primary key, which is prohibited.");
        }
        where = whereBuilder(where, d);
        Cursor cursor = null;
        try {
            String sql = SelectBuilder.getSql(d, where);
            cursor = execSQLRaw(sql, objects);
            Map<Object, List<T>> map = new HashMap<>();
            int colimnIndex = -1;
            if (cursor.moveToFirst()) {
                do {
                    if (colimnIndex == -1) {
                        colimnIndex = cursor.getColumnIndex(columnName);
                    }
                    Object key = Utils.getObjectFromCursor(cursor, colimnIndex);

                    T sd = aClass.newInstance();
                    if(d.isPersistent){
                        ((Persistent)sd).isPersistent=true;
                    }
                    Compound(d.listColumn, d.keyColumn, cursor, sd);
                    if (map.containsKey(key)) {
                        var maplist = map.get(key);
                        if (maplist == null) {
                            throw new RuntimeException("list is null");
                        }
                        maplist.add(sd);
                    } else {
                        List<T> tList = new ArrayList<>();
                        tList.add(sd);
                        map.put(key, tList);
                    }
                } while (cursor.moveToNext());
            }
            return map;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public <T> List<Object> distinctBy(@NonNull Class<T> aClass, @org.jspecify.annotations.NonNull String columnName, String where, Object... objects) {
        if (columnName.isEmpty()) {
            throw new ArithmeticException("Parameter columnName, empty or is null,");
        }
        CacheMetaData<T> d = getCacheMetaData(aClass);
        boolean isthis = false;
        for (int i = 0; i < d.listColumn.size(); i++) {
            ItemField field = d.listColumn.get(i);
            if (field.columnNameRaw.equals(columnName)) {
                isthis = true;
            }
        }
        if (!isthis) {
            throw new RuntimeException("The column with the name " + columnName + " was not found in the table " + d.tableName + ". " +
                    "Perhaps you meant the name of the primary key, which is prohibited.");
        }
        where = whereBuilder(where, d);
        Cursor cursor = null;
        try {
            String sql = SelectBuilder.getSqlDistinct(columnName, d, where);
            cursor = execSQLRaw(sql, objects);
            List<Object> objectList = new ArrayList<>();
            if (cursor.moveToFirst()) {
                do {
                    Object key = Utils.getObjectFromCursor(cursor, 0);
                    objectList.add(key);
                } while (cursor.moveToNext());
            }
            return objectList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public boolean IsAlive() {
       return sqLiteDatabaseForReadable.isOpen();
    }

    @Override
    public <T> ContentValues getContentValues(@NonNull T item) {
        CacheMetaData<T> data = getCacheMetaData(item.getClass());
        return getInnerContentValues(item,data);

    }

    @Override
    public <T> ContentValues getContentValuesForUpdate(@NonNull Class<T> aClass, PairColumnValue columnValues) {
        CacheMetaData<T> data = getCacheMetaData(aClass);
        return getInnerContentValuesForUpdate(data,columnValues);
    }

    @Override
    public <T> int save(@NonNull T item) {
        CacheMetaData<T> data = getCacheMetaData(item.getClass());
        if(data.isPersistent==false){
            throw new RuntimeException("An object of type "+item.getClass()+" does not inherit the class Persistent");
        }
        var per=((Persistent)item).isPersistent;
        if(per==false){
            insert(item);
            return 1;
        }
         return update(item);
    }

    <T> ContentValues getInnerContentValuesForUpdate(CacheMetaData<T> data, PairColumnValue columnValues) {
        ContentValues contentValues =  new ContentValues(columnValues.objectMap.size());
        Utils.builderSqlNew(data, contentValues, columnValues.objectMap);
        return contentValues;
    }

    <T> ContentValues getInnerContentValues(@NonNull T item,CacheMetaData<T> data) {

        ContentValues contentValues = new ContentValues(data.listColumn.size()+1);
        UtilsContentValues.initContentValues(item,data,contentValues);
        return contentValues;

    }


    @Override
    public Object executeScalar(@NonNull String sql, Object... objects) {
        String[] array = parametrize(objects);
        Logger.LogI(sql);
        return InnerListExe(sql, array);
    }

    @Override
    public Object executeScalar(@NonNull String sql) {
        Logger.LogI(sql);
        return InnerListExe(sql, null);
    }

    @Override
    public void executeSQL(@NonNull String sql, Object... objects) {

        sqLiteDatabaseForWritable.execSQL(sql, objects);
        Logger.LogI(sql);
    }

    @Override
    public <T> void insertBulk(@NonNull List<T> tList) {

        if(tList.isEmpty()){
            throw new ArithmeticException("The list is Empty");
        }
        tList.forEach(t -> {
            if (t == null) {
                throw new ArithmeticException("The list must not contain empty objects as null");
            }

        });
        var data=getCacheMetaData(tList.get(0).getClass());
        tList.forEach(t -> {
           if(data.isPersistent){
               if(((Persistent)t).isPersistent){
                   throw new RuntimeException("Your list contains a persistent object that was previously retrieved from the database.");
               }
           }

        });

        List<List<T>> sd = partition(tList);
        for (List<T> ts : sd) {
            InnerInsertBulk<T> s = new InnerInsertBulk(data);
            for (T t : ts) {
                s.add(t);
            }
            String sql = s.getSql();
            if (sql != null) {
                try {
                    var param = s.getParamsObjectList().toArray();
                    sqLiteDatabaseForWritable.execSQL(sql, param);
                    if (Configure.IsWriteLog) {
                        Logger.LogI(sql + System.lineSeparator() + " params:" + Arrays.toString(param));
                    }

                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        if(data.isPersistent){
            tList.forEach(t -> {
                ((Persistent)t).isPersistent=true;
            });
        }
    }

    @SafeVarargs
    @Override
    public final <T> void insertBulk(@NonNull T... object) {
        List<T> list = Arrays.asList(object);
        insertBulk(list);
    }

    @Override
    public Cursor execSQLRaw(@NonNull String sql, Object... objects) {
        try {
            String[] params = parametrize(objects);
            return sqLiteDatabaseForWritable.rawQuery(sql, params);
        } finally {
            Logger.LogI(sql);
        }
    }

    @Override
    public List<Map<String, Object>> execSQLRawMap(@NonNull String sql, Object... objects) {
        try {

            String[] params = parametrize(objects);
            Cursor cursor = sqLiteDatabaseForWritable.rawQuery(sql, params);

            List<Map<String, Object>> list = new ArrayList<>(cursor.getCount());
            if (cursor.moveToFirst()) {
                do {
                    list.add(Utils.cursorToMap(cursor));

                } while (cursor.moveToNext());
            }
            return list;

        } finally {
            Logger.LogI(sql);
        }
    }

    @Override
    public List<Object[]> execSQLRawArray(@NonNull String sql, Object... objects) {
        try {

            String[] params = parametrize(objects);
            Cursor cursor = sqLiteDatabaseForWritable.rawQuery(sql, params);
            List<Object[]> list = new ArrayList<>(cursor.getCount());
            if (cursor.moveToFirst()) {
                do {
                    list.add(Utils.CursorToArray(cursor));
                } while (cursor.moveToNext());
            }
            return list;

        } finally {
            Logger.LogI(sql);
        }
    }

    @Override
    public <T> String getTableName(@NonNull Class<T> aClass) {
        CacheMetaData<T> d = getCacheMetaData(aClass);
        return d.tableName;
    }

    @Override
    public <T> int deleteRows(@NonNull Class<T> aClass) {

        CacheMetaData<T> d = getCacheMetaData(aClass);
        String where = whereBuilder(null, d);
        Logger.LogI("DELETE FROM " + d.tableName + where);
        return sqLiteDatabaseForWritable.delete(d.tableName, where, null);
    }

    @Override
    public <T> int deleteRows(@NonNull Class<T> aClass,  String where, Object... objects) {
        CacheMetaData<T> data = getCacheMetaData(aClass);
        String tableName = data.tableName;
        if (tableName == null || tableName.trim().isEmpty()) return 0;
        String[] par = parametrize(objects);
        where = whereBuilder(where, data);
        Logger.LogI("DELETE FROM " + tableName  + where + Arrays.toString(par));
        return sqLiteDatabaseForWritable.delete(tableName, where, par);

    }

    private static <T> CacheMetaData getCacheMetaData(@NonNull Class<T> aClass) {
        CacheMetaData<T> d = (CacheMetaData<T>) CacheDictionary.getCacheMetaData(aClass);
        if (d == null) {
            String msg = "Perhaps the class:" + aClass.getName() + " does not implement the Map annotation";
            throw new RuntimeException(msg);
        }
        return d;
    }



    @Override
    public void dropTableIfExists(@NonNull String tableName) {

        if (tableName.trim().isEmpty()) {
            throw new RuntimeException("Missing table name in parameter");
        }
        sqLiteDatabaseForWritable.execSQL("DROP TABLE IF EXISTS " + tableName);
        Logger.LogI("DROP TABLE IF EXISTS" + tableName);
    }

    @Override
    public <T> void dropTableIfExists(@NonNull Class<T> aClass) {
        CacheMetaData<T> data = getCacheMetaData(aClass);

        sqLiteDatabaseForWritable.execSQL("DROP TABLE  IF EXISTS " + data.tableName);
        Logger.LogI("DROP TABLE IF EXISTS " + data.tableName);
    }

    @Override
    public <T> void createTable(@NonNull Class<T> aClass) throws Exception {
        createTableInner(aClass, "");
    }

    @Override
    public <T> void createTableIfNotExists(@NonNull Class<T> aClass) throws Exception {
        createTableInner(aClass, "IF NOT EXISTS");
    }


    /**
     * Obtaining a crypt string to create a table, taking into account all indexing annotations
     * @param aClass   Instances of the  represent classes and interfaces in a running Java application.
     *                 This class must be annotated with the @{@link MapTable} or @{@link MapTableName} annotation, have a public parameterless constructor,
     *                 and a public field marked with the primary key annotation.
     * @param useIsNotExist  include string IF NOT EXISTS
     * @return String value
     */
    public static String getSqlCreateTable(Class aClass, boolean useIsNotExist) {
        String s = null;
        if (useIsNotExist) {
            s = "IF NOT EXISTS";
        }
        var data=getCacheMetaData(aClass);
        var res = getStringListSqlCreateTable(s, data);
        getStringAppend(data, res);
        StringBuilder builder=new StringBuilder();
        for (String string : res) {
            builder.append(string).append(System.lineSeparator());
        }
        return builder.toString();
    }

    @Override
    public void beginTransaction() {
        sqLiteDatabaseForWritable.beginTransaction();
    }

    @Override
    public void commitTransaction() {
        sqLiteDatabaseForWritable.setTransactionSuccessful();
    }

    @Override
    public void endTransaction() {
        sqLiteDatabaseForWritable.endTransaction();
    }

    @Override
    public <T> boolean tableExists(@NonNull Class<T> aClass) {
        CacheMetaData<T> data = getCacheMetaData(aClass);
        String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name= " + data.tableName;
        return executeScalar(sql) != null;
    }

    @Override
    public boolean tableExists(@NonNull String tableName) {
        String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name= " + tableName;
        return executeScalar(sql) != null;
    }


    private Object InnerListExe(String sql, String[] strings) {

        try (Cursor cursor = sqLiteDatabaseForReadable.rawQuery(sql, strings)) {
            if (cursor.moveToFirst()) {
                do {
                    int i = cursor.getType(0);
                    switch (i) {
                        case Cursor.FIELD_TYPE_NULL: {
                            return null;
                        }
                        case Cursor.FIELD_TYPE_INTEGER: {
                            return cursor.getInt(0);
                        }
                        case Cursor.FIELD_TYPE_FLOAT: {
                            return cursor.getFloat(0);
                        }
                        case Cursor.FIELD_TYPE_STRING: {
                            return cursor.getString(0);
                        }
                        case Cursor.FIELD_TYPE_BLOB: {
                            return cursor.getBlob(0);
                        }
                    }
                } while (cursor.moveToNext());
            }
        }
        return null;
    }

    /**
     * Closing the current session
     * @throws IOException An error may occur if the transaction is not closed.
     */
    @Override
    public void close() throws IOException {
        if(sqLiteDatabaseForWritable.inTransaction()){
            throw new IOException("An error occurred while closing the session; there is an unclosed transaction.");
        }
        if (sqLiteDatabaseForWritable != null && sqLiteDatabaseForWritable.isOpen()) {
            sqLiteDatabaseForWritable.close();
        }
        if (sqLiteDatabaseForReadable != null && sqLiteDatabaseForReadable.isOpen()) {
            sqLiteDatabaseForReadable.close();
        }
    }
}
