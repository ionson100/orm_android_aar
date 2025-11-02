package com.bitnic.bitnicorm;


import static com.bitnic.bitnicorm.Utils.getStringListSqlCreateTable;
import static com.bitnic.bitnicorm.Utils.parametrize;
import static com.bitnic.bitnicorm.Utils.partition;
import static com.bitnic.bitnicorm.Utils.whereBuilder;
import static com.bitnic.bitnicorm.Utils.whereBuilderRaw;
import static com.bitnic.bitnicorm.UtilsCompound.builderInstance;
import static com.bitnic.bitnicorm.UtilsCompound.extractedSwitchSelect;
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
 * <pre>
 * {@code
 * new Configure("db.sqlite",3,appContext);
 * .....
 * ISession session=Configure.getSession();
 * session.dropTableIfExists(session.getTableName(MyTable.class));
 * var res=session.tableExists(MyTable.class);
 * assertEquals(false,res);
 * session.close();
 * }
 * </pre>
 * @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
 */
public class Configure implements ISession {


    static boolean IsWriteLog = false;
    static String dataBaseName;
    private static DataBaseHelper myDbHelper;

    private SQLiteDatabase sqLiteDatabaseForReadable = null;
    private SQLiteDatabase sqLiteDatabaseForWritable = null;
    private boolean isAutoClose;

    private Configure() {
        sqLiteDatabaseForReadable = GetSqLiteDatabaseForReadable();
        sqLiteDatabaseForWritable = GetSqLiteDatabaseForWritable();
    }

    /**
     * Configuration initialization, called once at application startup, creates the database file if it does not exist
     *
     * @param dataBaseName dataBaseName database file name with full path
     * @param version      database file version
     * @param context      context App
     *
     * <pre>
     * {@code
     *  try {
     *    new Configure("db.sqlite",3,appContext);
     *  } catch (Exception e) {
     *     throw new RuntimeException(e);
     *  }
     * }
     * </pre>
     * @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
     */
    public Configure(String dataBaseName, int version, Context context) {
        InitCtor(dataBaseName, version, context);
    }

    /**
     * Configuration initialization, called once at application startup, creates the database file if it does not exist
     *
     * @param dataBaseName database file name with full path
     * @param version      database file version
     * @param context      context App
     * @param classList    list of DTO types, upon initialization, tables are automatically created based on the type if the table does not exist
     * @param isWriteLog   force logging, only for debugging the application
     *<pre>
     * {@code
     *  List<Class> classList=new ArrayList<>();
     *  classList.add(MyTable.class);
     *  try {
     *    new Configure("db.sqlite",3,appContext,classList,true);
     *  } catch (Exception e) {
     *     throw new RuntimeException(e);
     *  }
     * }
     * </pre>
     * @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
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
     *
     * @param dataBaseName database file name with full path
     * @param version      database file version
     * @param context      context App
     * @param isWriteLog   force logging, only for debugging the application
     *
     *
     * <pre>
     * {@code
     * new Configure("db.sqlite",3,this,true);
     * ......
     * ISession session=Configure.getSession();
     * session.dropTableIfExists(session.getTableName(MyTable.class));
     * var res=session.tableExists(MyTable.class);
     * assertEquals(false,res);
     * session.close();
     * }
     * </pre>
     *
     * @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
     */
    public Configure(String dataBaseName, int version, Context context, boolean isWriteLog) {
        Configure.IsWriteLog = isWriteLog;
        InitCtor(dataBaseName, version, context);
    }

    private void InitCtor(String dataBaseName, int version, Context context) {
        Configure.dataBaseName = dataBaseName;
        myDbHelper = new DataBaseHelper(context, Configure.dataBaseName, version);
    }

    private void InitCtor(String dataBaseName, int version, Context context, ITask<SQLiteDatabase> iOnOpenHelper) {
        Configure.dataBaseName = dataBaseName;
        myDbHelper = new DataBaseHelper(context, Configure.dataBaseName, version, iOnOpenHelper);
    }


    /**
     * Getting the session object, now you can do something
     * @return object {@link ISession}
     * <pre>
     * {@code
     *   ISession session=Configure.getSession();
     *   List<MyTable> list=session.query(MyTable.class).toList()
     *   }
     * </pre>
     *
     * @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
     */
    public static ISession getSession() {
        Configure configure= new Configure();
        configure.isAutoClose=false;
        return  configure;
    }

    /**
     * Gets a session for one operation, after execution the session will be closed.
     * @return object {@link ISession}
     *
     * <pre>
     * {@code
     * Configure.getSessionAutoClose().query(MyTable.class).distinctByAsync("name").thenAcceptAsync(list -> {
     *     binding.textviewFirst.setText(String.valueOf(list.size()));
     * });
     *
     * }
     * </pre>
     * @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
     */
    public static ISession getSessionAutoClose() {
        Configure configure= new Configure();
        configure.isAutoClose=true;
        return  configure;
    }

    void closeAuto(){
        if(isAutoClose){
            try {
                close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static SQLiteDatabase GetSqLiteDatabaseForReadable() {
        return myDbHelper.openDataBaseForReadable();
    }

    private static SQLiteDatabase GetSqLiteDatabaseForWritable() {

        return myDbHelper.openDataBaseForWritable();
    }

    private <T> void createTableInner(Class<T> aClass, String ifNotExist) throws Exception {
        CacheMetaData<T> metaData = getCacheMetaData(aClass);
        checkingUsageType(metaData, "createTable", "aClass");
        if (metaData.isTableReadOnly) {
            throw new RuntimeException("You can only extract data from the table. (@MapTableReadOnly)");
        }
        if (metaData.keyColumn == null) {
            String msg = aClass.getName() + ": Поле первичного ключа отсутствует.";

            throw new Exception(msg);
        }

        List<String> sqlList = getStringListSqlCreateTable(ifNotExist, metaData);
        getStringAppend(metaData, sqlList);
        StringBuilder sqlBuilder = new StringBuilder();
        for (String s : sqlList) {
            sqlBuilder.append(System.lineSeparator()).append(s);
        }
        String sql = sqlBuilder.toString();
        Logger.I(sql);
        sqLiteDatabaseForWritable.execSQL(sql);


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
        try {
            return sqLiteDatabaseForWritable.getPath();
        }finally {
            closeAuto();
        }

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
    public <T, r> r count(@NonNull Class<T> aClass) {
        return count(aClass, null);
    }

    @Override
    public <T, r> r count(@NonNull Class<T> aClass, String where, Object... parameters) {

        CacheMetaData<T> metaData = getCacheMetaData(aClass);
        checkingUsageType(metaData, "count", "aClass");
        where = whereBuilderRaw(where, metaData);

        String sql = MessageFormat.format("SELECT COUNT(*) FROM {0} {1};", metaData.tableName, where);
        return (r) executeScalar(sql, parameters);
    }

    @Override
    public <T> boolean any(@NonNull Class<T> aClass) {
        return any(aClass, null);
    }

    @Override
    public <T> boolean any(@NonNull Class<T> aClass, String where, Object... parameters) {

        CacheMetaData<T> metaData = getCacheMetaData(aClass);
        checkingUsageType(metaData, "any", "aClass");
        where = whereBuilderRaw(where, metaData);
        String sql = MessageFormat.format(" SELECT EXISTS ( select * from {0}  {1});", metaData.tableName, where);
        int res = (int) executeScalar(sql, parameters);
        return res == 1;
    }

    @Override
    public <T> int update(@NonNull T item) {
        try {
            CacheMetaData<T> metaData = getCacheMetaData(item.getClass());
            checkingUsageType(metaData, "update", "item");
            if (metaData.isTableReadOnly) {
                throw new RuntimeException("You can only extract data from the table. (@MapTableReadOnly)");
            }
            if (metaData.isPersistent) {
                if (!((Persistent) item).isPersistent) {
                    throw new RuntimeException("You are trying to update a non-persistent object that is not in the database.");
                }
            }
            ContentValues contentValues = getInnerContentValues(item, metaData);
            int res;
            try {
                if (metaData.isIAction) {
                    ((IEventOrm) item).beforeUpdate();
                }
                Object key = metaData.keyColumn.field.get(item);
                String where = whereBuilder(metaData.keyColumn.columnName + " = ?", metaData);
                String[] param = new String[]{String.valueOf(key)};
                res = sqLiteDatabaseForWritable.update(metaData.tableName, contentValues, where, param);
                if (Configure.IsWriteLog) {
                    Logger.I(Utils.getStringUpdate(metaData.tableName, contentValues, where + " params: " + String.join(" - ", param)));
                }

                if (metaData.isPersistent) {
                    ((Persistent) item).isPersistent = true;
                }
                if (metaData.isIAction) {
                    ((IEventOrm) item).afterUpdate();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return res;
        }finally {
            closeAuto();
        }


    }

    String[] concat(String[] t1, String[] t2) {
        String[] res = new String[t1.length + t2.length];
        var index = 0;
        for (String s : t1) {
            res[index] = s;
            index++;
        }
        for (String s : t2) {
            res[index] = s;
            index++;
        }
        return res;
    }

    @Override
    public <T> int update(@NonNull T item, String appendWhere, Object... parameters) {
        try {
            CacheMetaData<T> metaData = getCacheMetaData(item.getClass());
            checkingUsageType(metaData, "update", "item");
            if (metaData.isTableReadOnly) {
                throw new RuntimeException("You can only extract data from the table. (@MapTableReadOnly)");
            }
            if (metaData.isPersistent) {
                if (!((Persistent) item).isPersistent) {
                    throw new RuntimeException("You are trying to update a non-persistent object that is not in the database.");
                }
            }
            ContentValues contentValues = getInnerContentValues(item, metaData);
            int res;
            try {
                if (metaData.isIAction) {
                    ((IEventOrm) item).beforeUpdate();
                }
                Object key = metaData.keyColumn.field.get(item);
                String where = whereBuilder(metaData.keyColumn.columnName + " = ?", metaData);
                if (!appendWhere.isEmpty()) {
                    where = where + " and " + appendWhere;
                }
                String[] param = new String[]{String.valueOf(key)};
                if (parameters.length > 0) {
                    String[] append = parametrize(parameters);
                    param = concat(param, append);


                }
                res = sqLiteDatabaseForWritable.update(metaData.tableName, contentValues, where, param);
                if (Configure.IsWriteLog) {
                    Logger.I(Utils.getStringUpdate(metaData.tableName, contentValues, where + " params: " + String.join(" - ", param)));
                }

                if (metaData.isPersistent) {
                    ((Persistent) item).isPersistent = true;
                }
                if (metaData.isIAction) {
                    ((IEventOrm) item).afterUpdate();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return res;
        }finally {
            closeAuto();
        }

    }

    @Override
    public <T> void insert(@NonNull T item) {
        try {
            CacheMetaData<T> metaData = getCacheMetaData(item.getClass());
            checkingUsageType(metaData, "insert", "item");
            if (metaData.isTableReadOnly) {
                throw new RuntimeException("You can only extract data from the table. (@MapTableReadOnly)");
            }
            if (metaData.isPersistent) {
                var per = ((Persistent) item);
                if (per.isPersistent) {
                    throw new RuntimeException("You are trying to insert an object into the database that was previously retrieved from the database, which is not very logical.");
                }
            }

            if (metaData.isIAction) {
                ((IEventOrm) item).beforeInsert();
            }
            ContentValues contentValues = getInnerContentValues(item, metaData);
            boolean runTransaction = false;
            if (!sqLiteDatabaseForWritable.inTransaction() && !metaData.keyColumn.isAssigned) {
                beginTransaction();
                runTransaction = true;
            }
            try {
                sqLiteDatabaseForWritable.insertOrThrow(metaData.tableName, null, contentValues);
                Logger.I(Utils.getStringInsert(metaData.tableName, contentValues));
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
            if (metaData.isPersistent) {
                ((Persistent) item).isPersistent = true;
            }
            if (metaData.isIAction) {
                ((IEventOrm) item).afterInsert();
            }
        }finally {
            closeAuto();
        }


    }

    @Override
    public <T> int delete(@NonNull T item) {
        try {
            CacheMetaData<T> metaData = getCacheMetaData(item.getClass());
            checkingUsageType(metaData, "delete", "item");
            if (metaData.isTableReadOnly) {
                throw new RuntimeException("You can only extract data from the table. (@MapTableReadOnly)");
            }
            if (metaData.isPersistent) {
                var per = ((Persistent) item);
                if (!per.isPersistent) {
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
        }finally {
            closeAuto();
        }

    }

    @Override
    public <T> int updateRows(@NonNull Class<T> aClass, @NonNull PairColumnValue columnValues, String where, Object... parameters) {
        try {
            CacheMetaData<T> metaData = getCacheMetaData(aClass);
            checkingUsageType(metaData, "updateRows", "aClass");
            if (metaData.isTableReadOnly) {
                throw new RuntimeException("You can only extract data from the table. (@MapTableReadOnly)");
            }
            where = whereBuilder(where, metaData);
            ContentValues contentValues = getInnerContentValuesForUpdate(metaData, columnValues);
            Logger.I("UPDATEALL WHERE: " + where);
            Logger.I(Utils.getStringUpdate(metaData.tableName, contentValues, where));
            return sqLiteDatabaseForWritable.update(metaData.tableName, contentValues, where, parametrize(parameters));
        }finally {
            closeAuto();
        }


    }


    public <T> List<T> getList(@NonNull Class<T> aClass) {
        return getList(aClass, null);
    }

    @Override
    public <T> List<T> getList(@NonNull Class<T> aClass, String where, Object... parameters) {
        try {
            CacheMetaData<T> metaData = getCacheMetaData(aClass);
            checkingUsageType(metaData, "toList", "aClass");
            where = whereBuilder(where, metaData);


            String sql = SelectBuilder.getSql(metaData, where);
            List<T> list;
            try (Cursor cursor = execSQLRawInner(sql, parameters)) {
                list = new ArrayList<>();
                if (cursor.moveToFirst()) {
                    do {
                        T instance = aClass.newInstance();
                        if (metaData.isPersistent) {
                            ((Persistent) instance).isPersistent = true;
                        }
                        builderInstance(metaData, cursor, instance);

                        list.add(instance);
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return list;
        }finally {
            closeAuto();
        }


    }


    @Override
    public <T> List<T> getListFree(@NonNull Class<T> aClass, String sql, Object... parameters) {
        try {
            CacheMetaData<?> metaData = CacheDictionary.getCacheMetaData(aClass);
            Logger.I(sql);
            List<T> list = new ArrayList<>();
            try (Cursor cursor = execSQLRawInner(sql, parameters)) {

                if (cursor.moveToFirst()) {
                    do {
                        T instance = aClass.newInstance();
                        builderInstance(metaData, cursor, instance);
                        list.add(instance);
                    } while (cursor.moveToNext());
                }
                return list;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }finally {
            closeAuto();
        }

    }


    @Override
    public <T> T firstOrDefault(@NonNull Class<T> aClass, String where, Object... parameters) {
        try {
            CacheMetaData<T> metaData = getCacheMetaData(aClass);
            checkingUsageType(metaData, "firstOrDefault", "aClass");
            where = whereBuilder(where, metaData);
            String sql = SelectBuilder.getSqlLimit(metaData, where, 1);


            try (Cursor cursor = execSQLRawInner(sql, parameters)) {
                if (cursor.moveToFirst()) {
                    T instance = aClass.newInstance();
                    if (metaData.isPersistent) {
                        ((Persistent) instance).isPersistent = true;
                    }
                    builderInstance(metaData, cursor, instance);
                    return instance;

                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            return null;
        }finally {
            closeAuto();
        }

    }

    @Override
    public <T> T first(@NonNull Class<T> aClass, String where, Object... parameters) throws Exception {
        T t = firstOrDefault(aClass, where, parameters);
        if (t == null) {
            throw new Exception("!!!The sample did not yield any results.");
        }
        return t;
    }

    @Override
    public <T> T single(@NonNull Class<T> aClass, String where, Object... parameters) throws Exception {
        try {
            CacheMetaData<T> metaData = getCacheMetaData(aClass);
            checkingUsageType(metaData, "single", "aClass");
            where = whereBuilder(where, metaData);
            String sql = SelectBuilder.getSqlLimit(metaData, where, 2);
            Logger.I(sql);
            Object[] resultArray = new Object[]{null, null};
            int index = 0;

            try (Cursor cursor = execSQLRawInner(sql, parameters)) {
                if (cursor.moveToFirst()) {
                    do {
                        T instance = aClass.newInstance();
                        if (metaData.isPersistent) {
                            ((Persistent) instance).isPersistent = true;
                        }
                        builderInstance(metaData, cursor, instance);
                        resultArray[index] = instance;
                        index++;
                    } while (cursor.moveToNext());

                }
            }
            if (resultArray[0] == null && resultArray[1] == null) {
                throw new Exception("!!!No object was found matching the selection criteria.");
            }
            if (resultArray[1] == null) {
                return (T) resultArray[0];
            } else {
                throw new Exception("!!!There is more than one object by condition");
            }
        }finally {
            closeAuto();
        }



    }


    @Override
    public <T> T singleOrDefault(@NonNull Class<T> aClass, String where, Object... parameters) {
        try {
            CacheMetaData<T> metaData = getCacheMetaData(aClass);
            checkingUsageType(metaData, "singleOrDefault", "aClass");
            where = whereBuilder(where, metaData);
            String sql = SelectBuilder.getSqlLimit(metaData, where, 2);
            Logger.I(sql);
            Object[] resultArray = new Object[]{null, null};
            int index = 0;

            try (Cursor cursor = execSQLRawInner(sql, parameters)) {
                if (cursor.moveToFirst()) {
                    do {
                        T instance = aClass.newInstance();
                        if (metaData.isPersistent) {
                            ((Persistent) instance).isPersistent = true;
                        }
                        builderInstance(metaData, cursor, instance);
                        resultArray[index] = instance;
                        index++;
                    } while (cursor.moveToNext());

                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (resultArray[1] == null) {
                return (T) resultArray[0];
            } else {
                return null;
            }
        }finally {
            closeAuto();
        }


    }


    @Override
    public <T, D> List<D> getListSelect(@NonNull Class<T> aClass, @NonNull String columnName, String where, Object... parameters) {
        try {
            List<D> list;
            CacheMetaData<T> metaData = getCacheMetaData(aClass);
            checkingUsageType(metaData, "getListSelect", "aClass");
            where = whereBuilder(where, metaData);
            if(!where.isEmpty()){
                where=" WHERE "+where;
            }


            ItemField itemField = getItemField(columnName, metaData);

            String sql="SELECT "+columnName+" FROM  "+metaData.tableName+" "+where+";";
            try (Cursor cursor = execSQLRawInner(sql,parameters)) {
                list = new ArrayList<>(cursor.getCount());
                Logger.printSql(cursor);

                int indexColumn=-1;
                if (cursor.moveToFirst()) {
                    do {

                        if(indexColumn==-1){
                            indexColumn=cursor.getColumnIndex(columnName);
                            if(indexColumn==-1){
                                throw new RuntimeException("Column with name \""+columnName+"\" was not found in the cursor output");
                            }
                        }

                        Object o = extractedSwitchSelect(cursor, itemField, itemField.field, indexColumn);
                        D d = (D) o;
                        list.add(d);
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return list;
        }finally {
            closeAuto();
        }

    }

    private static <T> @NonNull ItemField getItemField(@NonNull String columnName, CacheMetaData<T> metaData) {
        ItemField itemField = null;
        if (metaData.keyColumn.columnNameRaw.equals(columnName)) {
            itemField = metaData.keyColumn;
        } else {
            for (ItemField itemField1 : metaData.listColumn) {
                if (itemField1.columnNameRaw.equals(columnName)) {
                    itemField = itemField1;
                    break;
                }
            }
        }
        if (itemField == null) {
            throw new RuntimeException("Column name: " + columnName + " is not found in the table: " + metaData.tableName);
        }
        return itemField;
    }

    @Override
    public <T> List<T> getListSelect(@NonNull String sql, Object... parameters) {
        try {
            List<T> list;
            Logger.I(sql);
            try (Cursor cursor = execSQLRawInner(sql, parameters)) {
                list = new ArrayList<>(cursor.getCount());
                if (cursor.moveToFirst()) {
                    do {
                        int columnType = cursor.getType(0);
                        switch (columnType) {
                            case Cursor.FIELD_TYPE_NULL: {
                                list.add(null);
                                continue;
                            }
                            case Cursor.FIELD_TYPE_STRING: {
                                list.add((T) cursor.getString(0));
                                continue;
                            }
                            case Cursor.FIELD_TYPE_INTEGER: {
                                list.add((T) (Object) cursor.getInt(0));
                                continue;
                            }
                            case Cursor.FIELD_TYPE_FLOAT: {
                                list.add((T) (Object) cursor.getFloat(0));
                                continue;
                            }
                            case Cursor.FIELD_TYPE_BLOB: {
                                list.add((T) cursor.getBlob(0));
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
        }finally {
            closeAuto();
        }

    }


    @Override
    public <T> Map<Object, List<T>> groupBy(@NonNull Class<T> aClass, @NonNull String columnName, String where, Object... parameters) {
        try {
            if (columnName.isEmpty()) {
                throw new ArithmeticException("Parameter columnName, empty or is null,");
            }
            CacheMetaData<T> metaData = getCacheMetaData(aClass);
            checkingUsageType(metaData, "groupBy", "aClass");
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
            try (Cursor cursor = execSQLRawInner(sql, parameters)) {
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
                        builderInstance(metaData, cursor, instance);
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
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return map;
        }finally {
            closeAuto();
        }


    }

    @Override
    public <T> List<Object> distinctBy(@NonNull Class<T> aClass, @org.jspecify.annotations.NonNull String columnName, String where, Object... parameters) {
        try {
            if (columnName.isEmpty()) {
                throw new ArithmeticException("Parameter columnName, empty or is null,");
            }
            CacheMetaData<T> metaData = getCacheMetaData(aClass);
            checkingUsageType(metaData, "distinctBy", "aClass");
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
            try (Cursor cursor = execSQLRawInner(sql, parameters)) {
                objectList = new ArrayList<>();
                if (cursor.moveToFirst()) {
                    do {
                        Object key = Utils.getObjectFromCursor(cursor, 0);
                        objectList.add(key);
                    } while (cursor.moveToNext());
                }
            }
            return objectList;
        }finally {
            closeAuto();
        }


    }

    @Override
    public boolean IsAlive() {
        try {
            return sqLiteDatabaseForReadable.isOpen();
        }finally {
            closeAuto();
        }

    }

    @Override
    public <T> ContentValues getContentValues(@NonNull T item) {
        try {
            CacheMetaData<T> metaData = getCacheMetaData(item.getClass());
            checkingUsageType(metaData, "getContentValues", "item");
            return getInnerContentValues(item, metaData);
        }finally {
            closeAuto();
        }


    }

    @Override
    public <T> ContentValues getContentValuesForUpdate(@NonNull Class<T> aClass, PairColumnValue columnValues) {
        try {
            CacheMetaData<T> metaData = getCacheMetaData(aClass);
            checkingUsageType(metaData, "getContentValuesForUpdate", "aClass");
            return getInnerContentValuesForUpdate(metaData, columnValues);
        }finally {
            closeAuto();
        }

    }

    @Override
    public <T> int save(@NonNull T item) {

        CacheMetaData<T> metaData = getCacheMetaData(item.getClass());
        checkingUsageType(metaData, "save", "item");
        if (!metaData.isPersistent) {
            throw new RuntimeException("An object of type " + item.getClass() + " does not inherit the class Persistent");
        }
        var per = ((Persistent) item).isPersistent;
        if (!per) {
            insert(item);
            return 1;
        }
        return update(item);
    }

    @Override
    public <T> T objectFiller(Class<T> aClass, Cursor cursor) throws Exception {
        try {
            CacheMetaData<T> metaData = getCacheMetaData(aClass);
            T instance = aClass.newInstance();
            builderInstance(metaData, cursor, instance);
            return instance;
        }finally {
            closeAuto();
        }

    }

    @Override
    public <T> void objectFiller(Cursor cursor, T instance) throws Exception {
        try {
            CacheMetaData<T> metaData = getCacheMetaData(instance.getClass());
            builderInstance(metaData, cursor, instance);
        }finally {
            closeAuto();
        }

    }

    @Override
    public <T> IQueryable<T> query(Class<T> aClass) {
        return new ScopedValue<T>(this, aClass);
    }

    @Override
    public <T> void iterator(@NonNull Class<T> aClass, @NonNull ITask<T> action, String where, Object... parameters) {
        try {
            CacheMetaData<T> metaData = getCacheMetaData(aClass);
            checkingUsageType(metaData, "toList", "aClass");
            where = whereBuilder(where, metaData);
            String sql = SelectBuilder.getSql(metaData, where);
            try (Cursor cursor = execSQLRawInner(sql, parameters)) {

                if (cursor.moveToFirst()) {
                    do {
                        T instance = aClass.newInstance();
                        if (metaData.isPersistent) {
                            ((Persistent) instance).isPersistent = true;
                        }
                        action.invoke(instance);
                        builderInstance(metaData, cursor, instance);
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }finally {
            closeAuto();
        }

    }

    @Override
    public <T> void iteratorFree(@NonNull Class<T> aClass, @NonNull String sql, ITask<T> action, Object... parameters) {
        try {
            CacheMetaData<?> metaData = CacheDictionary.getCacheMetaData(aClass);
            Logger.I(sql);
            try (Cursor cursor = execSQLRawInner(sql, parameters)) {

                if (cursor.moveToFirst()) {
                    do {
                        T instance = aClass.newInstance();
                        builderInstance(metaData, cursor, instance);
                        action.invoke(instance);
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }finally {
            closeAuto();
        }

    }

    @Override
    public <T> T getById(@NonNull Class<T> aClass, @NonNull Object primaryKey) {
        CacheMetaData<?> metaData = CacheDictionary.getCacheMetaData(aClass);
        return singleOrDefault(aClass,metaData.keyColumn.columnName+" = ?",primaryKey);
    }

    <T> ContentValues getInnerContentValuesForUpdate(CacheMetaData<T> data, PairColumnValue columnValues) {
        ContentValues contentValues = new ContentValues(columnValues.objectMap.size());
        Utils.builderSqlNew(data, contentValues, columnValues.objectMap);
        return contentValues;
    }

    <T> ContentValues getInnerContentValues(@NonNull T item, CacheMetaData<T> data) {

        ContentValues contentValues = new ContentValues(data.listColumn.size() + 1);
        UtilsContentValues.initContentValues(item, data, contentValues);
        return contentValues;

    }

    @Override
    public Object executeScalar(@NonNull String sql, Object... parameters) {
        String[] array = parametrize(parameters);
        Logger.I(sql);
        return InnerListExe(sql, array);
    }

    @Override
    public Object executeScalar(@NonNull String sql) {
        Logger.I(sql);
        return InnerListExe(sql, null);
    }

    @Override
    public void executeSQL(@NonNull String sql, Object... parameters) {
        try {
            sqLiteDatabaseForWritable.execSQL(sql, parameters);
            Logger.I(sql);
        }finally {
            closeAuto();
        }

    }

    @Override
    public Cursor execSQLRaw(@NonNull String sql, Object... objects) {
        try {
            return execSQLRawInner(sql,objects);
        }finally {
            closeAuto();
        }

    }

    @Override
    public <T> void insertBulk(@NonNull List<T> tList) {
        try {
            if (tList.isEmpty()) {
                throw new ArithmeticException("The list is Empty");
            }
            tList.forEach(t -> {
                if (t == null) {
                    throw new ArithmeticException("The list must not contain empty objects as null");
                }

            });
            var metaData = getCacheMetaData(tList.get(0).getClass());

            checkingUsageType(metaData, "insetBulk", "item of lis");
            if (metaData.isTableReadOnly) {
                throw new RuntimeException("You can only extract data from the table. (@MapTableReadOnly)");
            }
            tList.forEach(t -> {
                if (metaData.isPersistent) {
                    if (((Persistent) t).isPersistent) {
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
            if (metaData.isPersistent) {
                tList.forEach(t -> ((Persistent) t).isPersistent = true);
            }
        }finally {
            closeAuto();
        }

    }

    @SafeVarargs
    @Override
    public final <T> void insertBulk(@NonNull T... object) {
        List<T> list = Arrays.asList(object);
        insertBulk(list);
    }


     Cursor execSQLRawInner(@NonNull String sql, Object... parameters) {
        try {
            String[] params = parametrize(parameters);
            return sqLiteDatabaseForWritable.rawQuery(sql, params);
        } finally {
            Logger.I(sql);

        }
    }


    @Override
    public <T> String getTableName(@NonNull Class<T> aClass) {
        try {
            CacheMetaData<T> metaData = getCacheMetaData(aClass);
            return metaData.tableName;
        }finally {
            closeAuto();
        }

    }

    @Override
    public <T> int deleteRows(@NonNull Class<T> aClass) {
        try {
            CacheMetaData<T> metaData = getCacheMetaData(aClass);
            checkingUsageType(metaData, "deleteRows", "aClass");
            if (metaData.isTableReadOnly) {
                throw new RuntimeException("You can only extract data from the table. (@MapTableReadOnly)");
            }
            String where = whereBuilder(null, metaData);
            Logger.I("DELETE FROM " + metaData.tableName + where);
            return sqLiteDatabaseForWritable.delete(metaData.tableName, where, null);
        }finally {
            closeAuto();
        }

    }

    @Override
    public <T> int deleteRows(@NonNull Class<T> aClass, String where, Object... parameters) {
        try {
            CacheMetaData<T> metaData = getCacheMetaData(aClass);
            checkingUsageType(metaData, "deleteRows", "aClass");
            if (metaData.isTableReadOnly) {
                throw new RuntimeException("You can only extract data from the table. (@MapTableReadOnly)");
            }
            String tableName = metaData.tableName;
            if (tableName == null || tableName.trim().isEmpty()) return 0;
            String[] par = parametrize(parameters);
            where = whereBuilder(where, metaData);
            Logger.I("DELETE FROM " + tableName + where + Arrays.toString(par));
            return sqLiteDatabaseForWritable.delete(tableName, where, par);
        }finally {
            closeAuto();
        }


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

        try {
            if (tableName.trim().isEmpty()) {
                throw new RuntimeException("Missing table name in parameter");
            }
            sqLiteDatabaseForWritable.execSQL("DROP TABLE IF EXISTS " + tableName);
            Logger.I("DROP TABLE IF EXISTS" + tableName);
        }finally {
            closeAuto();
        }

    }

    @Override
    public <T> void dropTableIfExists(@NonNull Class<T> aClass) {
        try {
            CacheMetaData<T> metaData = getCacheMetaData(aClass);
            checkingUsageType(metaData, "dropTableIfExists", "aClass");

            if (metaData.isTableReadOnly) {
                throw new RuntimeException("You can only extract data from the table. (@MapTableReadOnly)");
            }

            sqLiteDatabaseForWritable.execSQL("DROP TABLE  IF EXISTS " + metaData.tableName);
            Logger.I("DROP TABLE IF EXISTS " + metaData.tableName);
        }finally {
            closeAuto();
        }

    }

    @Override
    public <T> void createTable(@NonNull Class<T> aClass) throws Exception {
        try {
            createTableInner(aClass, "");
        }finally {
            closeAuto();
        }

    }

    @Override
    public <T> void createTableIfNotExists(@NonNull Class<T> aClass) throws Exception {
        try {
            createTableInner(aClass, "IF NOT EXISTS");
        }finally {
            closeAuto();
        }

    }


    /**
     * Obtaining a crypt string to create a table, taking into account all indexing annotations
     *
     * @param aClass        Instances of the  represent classes and interfaces in a running Java application.
     *                      This class must be annotated with the @{@link MapTable} or @{@link MapTableName} annotation, have a public parameterless constructor,
     *                      and a public field marked with the primary key annotation.
     * @param useIsNotExist include string IF NOT EXISTS
     * @return String value
     */
    public static String getSqlCreateTable(Class<?> aClass, boolean useIsNotExist) {
        String s = null;
        if (useIsNotExist) {
            s = "IF NOT EXISTS";
        }
        var metaData = getCacheMetaData(aClass);
        var res = getStringListSqlCreateTable(s, metaData);
        getStringAppend(metaData, res);
        StringBuilder builder = new StringBuilder();
        for (String string : res) {
            builder.append(string).append(System.lineSeparator());
        }
        return builder.toString();
    }

    @Override
    public void beginTransaction() {
        if(isAutoClose){
            throw new RuntimeException("It is prohibited to use transactions in the automatic session closing mode.");
        }
        sqLiteDatabaseForWritable.beginTransaction();
    }

    @Override
    public void commitTransaction() {
        if(isAutoClose){
            throw new RuntimeException("It is prohibited to use transactions in the automatic session closing mode.");
        }
        sqLiteDatabaseForWritable.setTransactionSuccessful();
    }

    @Override
    public void endTransaction() {
        if(isAutoClose){
            throw new RuntimeException("It is prohibited to use transactions in the automatic session closing mode.");
        }
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

        try {
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
        }finally {
            closeAuto();
        }

    }

    /**
     * Closing the current session
     *
     * @throws IOException An error may occur if the transaction is not closed.
     */
    @Override
    public void close() throws IOException {
        if (sqLiteDatabaseForWritable.inTransaction()) {
            throw new IOException("An error occurred while closing the session; there is an unclosed transaction.");
        }
        if (sqLiteDatabaseForWritable != null && sqLiteDatabaseForWritable.isOpen()) {
            sqLiteDatabaseForWritable.close();
        }
        if (sqLiteDatabaseForReadable != null && sqLiteDatabaseForReadable.isOpen()) {
            sqLiteDatabaseForReadable.close();
        }
    }

    private void checkingUsageType(CacheMetaData<?> metaData, String methodName, String parameterName) {
        if (metaData.isFreeClass) {
            throw new RuntimeException(MessageFormat.format("In the update {0}, in the {1} parameter, a type must be used whose class has annotated markup.", methodName, parameterName));
        }
    }
}
