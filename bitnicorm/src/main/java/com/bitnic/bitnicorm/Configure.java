package com.bitnic.bitnicorm;
/********************************************************************
 * Copyright © 2016-2017 OOO Bitnic                                 *
 * Created by OOO Bitnic on 08.02.16   corp@bitnic.ru               *
 * ******************************************************************/

import static com.bitnic.bitnicorm.Utils.getStringListSqlCreateTable;
import static com.bitnic.bitnicorm.Utils.parametrize;
import static com.bitnic.bitnicorm.Utils.partition;
import static com.bitnic.bitnicorm.Utils.whereBuilder;
import static com.bitnic.bitnicorm.Utils.whereBuilderRaw;
import static com.bitnic.bitnicorm.UtilsCompound.builderInstance;
import static com.bitnic.bitnicorm.UtilsContentValues.checkFieldValue;
import static com.bitnic.bitnicorm.UtilsHelper.bytesToHex;

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
        CacheMetaData<T> metaData = getCacheMetaData(aClass);
        checkingUsageType(metaData,"createTable","aClass");
        if(metaData.isTableReadOnly){
            throw new RuntimeException("You can only extract data from the table. (@MapTableReadOnly)");
        }
        if (metaData.keyColumn == null) {
            String msg = aClass.getName() + ": Поле первичного ключа отсутствует.";

            throw new Exception(msg);
        }

        List<String> sqlList = getStringListSqlCreateTable(ifNotExist, metaData);
        getStringAppend(metaData, sqlList);
        String curSql = null;
        try {
            for (String sqlOne : sqlList) {
                curSql = sqlOne;
                Logger.I(sqlOne);
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
    public SQLiteDatabase getSqLiteDatabaseForReadable() {
        return sqLiteDatabaseForReadable;
    }

    @Override
    public SQLiteDatabase getSqLiteDatabaseForWritable() {
        return sqLiteDatabaseForWritable;
    }

    @Override
    public <T> int count(@NonNull Class<T> aClass) {
        return count(aClass, null);
    }

    @Override
    public <T> int count(@NonNull Class<T> aClass, String where, Object... objects) {
        CacheMetaData<T> metaData = getCacheMetaData(aClass);
        checkingUsageType(metaData,"count","aClass");
        where = whereBuilderRaw(where, metaData);

        String sql = MessageFormat.format("SELECT COUNT(*) FROM {0} {1};", metaData.tableName, where);
        return (int) executeScalar(sql, objects);
    }

    @Override
    public <T> boolean any(@NonNull Class<T> aClass) {
        return any(aClass, null);
    }

    @Override
    public <T> boolean any(@NonNull Class<T> aClass, String where, Object... objects) {
        CacheMetaData<T> metaData = getCacheMetaData(aClass);
        checkingUsageType(metaData,"any","aClass");
        where = whereBuilderRaw(where, metaData);
        String sql = MessageFormat.format(" SELECT EXISTS ( select * from {0}  {1});", metaData.tableName, where);
        int res = (int) executeScalar(sql, objects);
        return res == 1;
    }

    @Override
    public <T> int update(@NonNull T item) {
        CacheMetaData<T> metaData = getCacheMetaData(item.getClass());
        checkingUsageType(metaData,"update","item");
        if(metaData.isTableReadOnly){
            throw new RuntimeException("You can only extract data from the table. (@MapTableReadOnly)");
        }
        if(metaData.isPersistent){
            if(!((Persistent)item).isPersistent){
                throw new RuntimeException("You are trying to update a non-persistent object that is not in the database.");
            }
        }
        ContentValues contentValues =getInnerContentValues(item,metaData);
        int res;
        try {
            if (metaData.isIAction) {
                ((IEventOrm) item).beforeUpdate();
            }
            Object key = metaData.keyColumn.field.get(item);
            String where = whereBuilder(metaData.keyColumn.columnName + " = ?", metaData);
            String[] param = new String[]{String.valueOf(key)};
            res=sqLiteDatabaseForWritable.update(metaData.tableName, contentValues, where, param);
            Logger.I(Utils.getStringUpdate(metaData.tableName,contentValues,where));
            if(metaData.isPersistent){
                ((Persistent)item).isPersistent=true;
            }
            if (metaData.isIAction) {
                ((IEventOrm) item).afterUpdate();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return res;

    }

    @Override
    public <T> void insert(@NonNull T item) {
        CacheMetaData<T> metaData = getCacheMetaData(item.getClass());
        checkingUsageType(metaData,"insert","item");
        if(metaData.isTableReadOnly){
            throw new RuntimeException("You can only extract data from the table. (@MapTableReadOnly)");
        }
        if(metaData.isPersistent){
            var per=((Persistent)item);
            if(per.isPersistent){
                throw new RuntimeException("You are trying to insert an object into the database that was previously retrieved from the database, which is not very logical.");
            }
        }

        if (metaData.isIAction) {
            ((IEventOrm) item).beforeInsert();
        }
        ContentValues contentValues =getInnerContentValues(item,metaData);
        boolean runTransaction = false;
        if (!sqLiteDatabaseForWritable.inTransaction() && !metaData.keyColumn.isAssigned) {
            beginTransaction();
            runTransaction = true;
        }
        try {
            sqLiteDatabaseForWritable.insertOrThrow(metaData.tableName, null, contentValues);
            Logger.I(Utils.getStringInsert(metaData.tableName,contentValues));
            if (!metaData.keyColumn.isAssigned) {
                var id = executeScalar("SELECT last_insert_rowid();");
                metaData.keyColumn.field.set(item, id);
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
        if(metaData.isPersistent){
            ((Persistent)item).isPersistent=true;
        }
        if (metaData.isIAction) {
            ((IEventOrm) item).afterInsert();
        }

    }

    @Override
    public <T> int delete(@NonNull T item) {

        CacheMetaData<T> metaData = getCacheMetaData(item.getClass());
        checkingUsageType(metaData,"delete","item");
        if(metaData.isTableReadOnly){
            throw new RuntimeException("You can only extract data from the table. (@MapTableReadOnly)");
        }
        if(metaData.isPersistent){
            var per=((Persistent)item);
            if(!per.isPersistent){
                throw new RuntimeException("You cannot delete the object because it was not retrieved from the database.");
            }
        }
        Object key;
        try {
            Field field = metaData.keyColumn.field;
            field.setAccessible(true);
            key = checkFieldValue(field, item);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (metaData.isIAction) {
            ((IEventOrm) item).beforeDelete();
        }
        int res = sqLiteDatabaseForWritable.delete(metaData.tableName, metaData.tableName + "." + metaData.keyColumn.columnName + "=?", new String[]{key.toString()});
        if (res != 0) {
            if (metaData.isIAction) {
                ((IEventOrm) item).afterDelete();
            }
        }
        return res;
    }

    @Override
    public <T> int updateRows(@NonNull Class<T> aClass, @NonNull PairColumnValue columnValues, String where, Object... objects) {

        CacheMetaData<T> metaData = getCacheMetaData(aClass);
        checkingUsageType(metaData,"updateRows","aClass");
        if(metaData.isTableReadOnly){
            throw new RuntimeException("You can only extract data from the table. (@MapTableReadOnly)");
        }
        where = whereBuilder(where, metaData);
        ContentValues contentValues =  getInnerContentValuesForUpdate(metaData,columnValues);
        Logger.I("UPDATEALL WHERE: " + where);
        Logger.I(Utils.getStringUpdate(metaData.tableName,contentValues,where));
        return sqLiteDatabaseForWritable.update(metaData.tableName, contentValues, where, parametrize(objects));

    }



    public <T> List<T> getList(@NonNull Class<T> aClass) {
        return getList(aClass, null);
    }

    @Override
    public <T> List<T> getList(@NonNull Class<T> aClass, String where, Object... objects) {

        CacheMetaData<T> metaData = getCacheMetaData(aClass);
        checkingUsageType(metaData,"getList","aClass");
        where = whereBuilder(where, metaData);


        String sql = SelectBuilder.getSql(metaData, where);
        List<T> list;
        try (Cursor cursor = execSQLRaw(sql, objects)) {
            list = new ArrayList<>();
            if (cursor.moveToFirst()) {
                do {
                    T instance = aClass.newInstance();
                    if (metaData.isPersistent) {
                        ((Persistent) instance).isPersistent = true;
                    }
                    builderInstance(metaData,cursor,instance);

                    list.add(instance);
                } while (cursor.moveToNext());
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        return list;

    }


    @Override
    public <T> List<T> getListFree(@NonNull Class<T> aClass, String sql, Object... objects) {
        CacheMetaData<?> metaData = CacheDictionary.getCacheMetaData(aClass);


        Logger.I(sql);
        List<T> list=new ArrayList<>();
        try (Cursor cursor = execSQLRaw(sql, objects)) {

            if (cursor.moveToFirst()) {
                do {
                    T instance = aClass.newInstance();
                    builderInstance(metaData,cursor,instance);
                    list.add(instance);
                } while (cursor.moveToNext());
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public <T> T firstOrDefault(@NonNull Class<T> aClass, String where, Object... objects) {

        CacheMetaData<T> metaData = getCacheMetaData(aClass);
        checkingUsageType(metaData,"firstOrDefault","aClass");
        where = whereBuilder(where, metaData);
        String sql = SelectBuilder.getSqlLimit(metaData, where, 1);


        try (Cursor cursor = execSQLRaw(sql, objects)) {
            if (cursor.moveToFirst()) {
                T instance = aClass.newInstance();
                if (metaData.isPersistent) {
                    ((Persistent) instance).isPersistent = true;
                }
                builderInstance(metaData,cursor,instance);
                return instance;

            }
        }catch (Exception e){
            throw new RuntimeException(e);
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


        CacheMetaData<T> metaData = getCacheMetaData(aClass);
        checkingUsageType(metaData,"single","aClass");
        where = whereBuilder(where, metaData);
        String sql = SelectBuilder.getSqlLimit(metaData, where, 2);
        Logger.I(sql);
        Object[] resultArray = new Object[]{null, null};
        int index = 0;

        try (Cursor cursor = execSQLRaw(sql, objects)) {
            if (cursor.moveToFirst()) {
                do {
                    T instance = aClass.newInstance();
                    if (metaData.isPersistent) {
                        ((Persistent) instance).isPersistent = true;
                    }
                    builderInstance(metaData,cursor,instance);
                    resultArray[index] = instance;
                    index++;
                } while (cursor.moveToNext());

            }
        }
        if(resultArray[0]==null&&resultArray[1]==null){
                throw new Exception("!!!No object was found matching the selection criteria.");
            }
            if (resultArray[1] == null) {
                return (T) resultArray[0];
            } else {
               throw new Exception("!!!There is more than one object by condition");
            }

    }


    @Override
    public <T> T singleOrDefault(@NonNull Class<T> aClass, String where, Object... objects) {

        CacheMetaData<T> metaData = getCacheMetaData(aClass);
        checkingUsageType(metaData,"singleOrDefault","aClass");
        where = whereBuilder(where, metaData);
        String sql = SelectBuilder.getSqlLimit(metaData, where, 2);
        Logger.I(sql);
        Object[] resultArray = new Object[]{null, null};
        int index = 0;

        try (Cursor cursor = execSQLRaw(sql, objects)) {
            if (cursor.moveToFirst()) {
                do {
                    T instance = aClass.newInstance();
                    if (metaData.isPersistent) {
                        ((Persistent) instance).isPersistent = true;
                    }
                    builderInstance(metaData,cursor,instance);
                    resultArray[index] = instance;
                    index++;
                } while (cursor.moveToNext());

            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        if (resultArray[1] == null) {
                return (T) resultArray[0];
            } else {
                return null;
            }

    }





    @Override
    public <T, D> List<D> getListSelect(@NonNull Class<T> aClass,@NonNull String columnName, String where, Object... objects) {

        List<D> list;
        CacheMetaData<T> metaData = getCacheMetaData(aClass);
        checkingUsageType(metaData,"getListSelect","aClass");
        where = whereBuilder(where, metaData);
        String[] sdd = new String[]{Utils.clearStringTrimRaw(columnName)};
        String[] str = parametrize(objects);
        try (Cursor cursor = sqLiteDatabaseForReadable.query(metaData.tableName, sdd, where, str, null, null, null, null)) {
            list = new ArrayList<>(cursor.getCount());
            Logger.printSql(cursor);

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
        }


        return list;
    }



    @Override
    public <T> Map<Object, List<T>> groupBy(@NonNull Class<T> aClass, @NonNull String columnName, String where, Object... objects) {
        if (columnName.isEmpty()) {
            throw new ArithmeticException("Parameter columnName, empty or is null,");
        }
        CacheMetaData<T> metaData = getCacheMetaData(aClass);
        checkingUsageType(metaData,"groupBy","aClass");
        boolean isthis = false;
        for (int i = 0; i < metaData.listColumn.size(); i++) {
            ItemField field = metaData.listColumn.get(i);
            if (field.columnNameRaw.equals(columnName)) {
                isthis = true;
            }
        }
        if (!isthis) {
            throw new RuntimeException("The column with the name " + columnName + " was not found in the table " + metaData.tableName + ". " +
                    "Perhaps you meant the name of the primary key, which is prohibited.");
        }
        where = whereBuilder(where, metaData);


        String sql = SelectBuilder.getSql(metaData, where);
        Map<Object, List<T>> map;
        try (Cursor cursor = execSQLRaw(sql, objects)) {
            map = new HashMap<>();
            int colimnIndex = -1;
            if (cursor.moveToFirst()) {
                do {
                    if (colimnIndex == -1) {
                        colimnIndex = cursor.getColumnIndex(columnName);
                    }
                    Object key = Utils.getObjectFromCursor(cursor, colimnIndex);

                    T instance = aClass.newInstance();
                    if (metaData.isPersistent) {
                        ((Persistent) instance).isPersistent = true;
                    }
                    builderInstance(metaData,cursor,instance);
                    if (map.containsKey(key)) {
                        var maplist = map.get(key);
                        if (maplist == null) {
                            throw new RuntimeException("list is null");
                        }
                        maplist.add(instance);
                    } else {
                        List<T> tList = new ArrayList<>();
                        tList.add(instance);
                        map.put(key, tList);
                    }
                } while (cursor.moveToNext());
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        return map;

    }

    @Override
    public <T> List<Object> distinctBy(@NonNull Class<T> aClass, @org.jspecify.annotations.NonNull String columnName, String where, Object... objects) {
        if (columnName.isEmpty()) {
            throw new ArithmeticException("Parameter columnName, empty or is null,");
        }
        CacheMetaData<T> metaData = getCacheMetaData(aClass);
        checkingUsageType(metaData,"distinctBy","aClass");
        boolean isthis = false;
        for (int i = 0; i < metaData.listColumn.size(); i++) {
            ItemField field = metaData.listColumn.get(i);
            if (field.columnNameRaw.equals(columnName)) {
                isthis = true;
            }
        }
        if (!isthis) {
            throw new RuntimeException("The column with the name " + columnName + " was not found in the table " + metaData.tableName + ". " +
                    "Perhaps you meant the name of the primary key, which is prohibited.");
        }
        where = whereBuilder(where, metaData);


        String sql = SelectBuilder.getSqlDistinct(columnName, metaData, where);

        List<Object> objectList;
        try (Cursor cursor = execSQLRaw(sql, objects)) {
            objectList = new ArrayList<>();
            if (cursor.moveToFirst()) {
                do {
                    Object key = Utils.getObjectFromCursor(cursor, 0);
                    objectList.add(key);
                } while (cursor.moveToNext());
            }
        }
        return objectList;

    }

    @Override
    public boolean IsAlive() {
       return sqLiteDatabaseForReadable.isOpen();
    }

    @Override
    public <T> ContentValues getContentValues(@NonNull T item) {
        CacheMetaData<T> metaData = getCacheMetaData(item.getClass());
        checkingUsageType(metaData,"getContentValues","item");
        return getInnerContentValues(item,metaData);

    }

    @Override
    public <T> ContentValues getContentValuesForUpdate(@NonNull Class<T> aClass, PairColumnValue columnValues) {
        CacheMetaData<T> metaData = getCacheMetaData(aClass);
        checkingUsageType(metaData,"getContentValuesForUpdate","aClass");
        return getInnerContentValuesForUpdate(metaData,columnValues);
    }

    @Override
    public <T> int save(@NonNull T item) {
        CacheMetaData<T> metaData = getCacheMetaData(item.getClass());
        checkingUsageType(metaData,"save","item");
        if(!metaData.isPersistent){
            throw new RuntimeException("An object of type "+item.getClass()+" does not inherit the class Persistent");
        }
        var per=((Persistent)item).isPersistent;
        if(!per){
            insert(item);
            return 1;
        }
         return update(item);
    }

    @Override
    public <T> T objectFiller(Class<T> aClass, Cursor cursor) throws Exception {
        CacheMetaData<T> metaData = getCacheMetaData(aClass);
        T instance = aClass.newInstance();
        builderInstance(metaData,cursor,instance);
        return instance;
    }

    @Override
    public <T> void objectFiller(Cursor cursor, T instance)  throws Exception {
        CacheMetaData<T> metaData = getCacheMetaData(instance.getClass());
        builderInstance(metaData,cursor,instance);
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
        Logger.I(sql);
        return InnerListExe(sql, array);
    }

    @Override
    public Object executeScalar(@NonNull String sql) {
        Logger.I(sql);
        return InnerListExe(sql, null);
    }

    @Override
    public void executeSQL(@NonNull String sql, Object... objects) {

        sqLiteDatabaseForWritable.execSQL(sql, objects);
        Logger.I(sql);
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
        var metaData=getCacheMetaData(tList.get(0).getClass());

        checkingUsageType(metaData,"insetBulk","item of lis");
        if(metaData.isTableReadOnly){
            throw new RuntimeException("You can only extract data from the table. (@MapTableReadOnly)");
        }
        tList.forEach(t -> {
           if(metaData.isPersistent){
               if(((Persistent)t).isPersistent){
                   throw new RuntimeException("Your list contains a persistent object that was previously retrieved from the database.");
               }
           }

        });

        List<List<T>> sd = partition(tList);
        for (List<T> ts : sd) {
            InnerInsertBulk<T> s = new InnerInsertBulk<>(metaData);
            for (T t : ts) {
                s.add(t);
            }
            String sql = s.getSql();
            if (sql != null) {
                try {
                    var param = s.getParamsObjectList().toArray();
                    sqLiteDatabaseForWritable.execSQL(sql, param);
                    if (Configure.IsWriteLog) {
                        Logger.I(sql + System.lineSeparator() + " params:" + Arrays.toString(param));
                    }

                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        if(metaData.isPersistent){
            tList.forEach(t -> ((Persistent)t).isPersistent=true);
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
            Logger.I(sql);
        }
    }





    @Override
    public <T> String getTableName(@NonNull Class<T> aClass) {
        CacheMetaData<T> metaData = getCacheMetaData(aClass);
        return metaData.tableName;
    }

    @Override
    public <T> int deleteRows(@NonNull Class<T> aClass) {

        CacheMetaData<T> metaData = getCacheMetaData(aClass);
        checkingUsageType(metaData,"deleteRows","aClass");
        if(metaData.isTableReadOnly){
            throw new RuntimeException("You can only extract data from the table. (@MapTableReadOnly)");
        }
        String where = whereBuilder(null, metaData);
        Logger.I("DELETE FROM " + metaData.tableName + where);
        return sqLiteDatabaseForWritable.delete(metaData.tableName, where, null);
    }

    @Override
    public <T> int deleteRows(@NonNull Class<T> aClass,  String where, Object... objects) {
        CacheMetaData<T> metaData = getCacheMetaData(aClass);
        checkingUsageType(metaData,"deleteRows","aClass");
        if(metaData.isTableReadOnly){
            throw new RuntimeException("You can only extract data from the table. (@MapTableReadOnly)");
        }
        String tableName = metaData.tableName;
        if (tableName == null || tableName.trim().isEmpty()) return 0;
        String[] par = parametrize(objects);
        where = whereBuilder(where, metaData);
        Logger.I("DELETE FROM " + tableName  + where + Arrays.toString(par));
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
        Logger.I("DROP TABLE IF EXISTS" + tableName);
    }

    @Override
    public <T> void dropTableIfExists(@NonNull Class<T> aClass) {
        CacheMetaData<T> metaData = getCacheMetaData(aClass);
        checkingUsageType(metaData,"dropTableIfExists","aClass");

        if(metaData.isTableReadOnly){
            throw new RuntimeException("You can only extract data from the table. (@MapTableReadOnly)");
        }

        sqLiteDatabaseForWritable.execSQL("DROP TABLE  IF EXISTS " + metaData.tableName);
        Logger.I("DROP TABLE IF EXISTS " + metaData.tableName);
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
    public static String getSqlCreateTable(Class<?> aClass, boolean useIsNotExist) {
        String s = null;
        if (useIsNotExist) {
            s = "IF NOT EXISTS";
        }
        var metaData=getCacheMetaData(aClass);
        var res = getStringListSqlCreateTable(s, metaData);
        getStringAppend(metaData, res);
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
        CacheMetaData<T> metaData = getCacheMetaData(aClass);
        String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name= " + metaData.tableName;
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
    private void checkingUsageType(CacheMetaData<?> metaData,String methodName,String parameterName){
        if(metaData.isFreeClass){
            throw new RuntimeException(MessageFormat.format("In the update {0}, in the {1} parameter, a type must be used whose class has annotated markup.", methodName, parameterName));
        }
    }
}
