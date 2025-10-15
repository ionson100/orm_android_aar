package com.bitnic.bitnicorm;
/********************************************************************
 * Copyright © 2016-2017 OOO Bitnic                                 *
 * Created by OOO Bitnic on 08.02.16   corp@bitnic.ru               *
 * ******************************************************************/

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import java.io.Closeable;
import java.util.List;
import java.util.Map;

/**
 * Database work unit
 */
public interface ISession extends Closeable {


    /**
     * Convenience method for updating rows in the database.
     *
     * @param item An object, class type must be marked with annotation @{@link MapTable} or @{@link MapTableName}
     * @param <T>  The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * @return 1-success , 0 - not success
     * <pre>
     * {@code
     * @MapTable
     * public class SimpleTable {
     *     public SimpleTable(){}
     *     public SimpleTable(String name){
     *         this.myName = name;
     *     }
     *     @MapPrimaryKey
     *     public long id;
     *     @MapColumnName("name")
     *     public String myName;
     * }
     * ISession session=Configure.getSession();
     * session.insert(new SimpleTable("name"));
     * var o=session.firstOrDefault(SimpleTable.class,null);
     * o.myName="newName";
     * var res=session.update(o);
     * }
     * </pre>
     */
    <T> int update(@NonNull T item);



    /**
     * Convenience method for inserting a row into the database.
     *
     * @param item An object, class type must be marked with annotation @{@link MapTable} or @{@link MapTableName}
     * @param <T>  The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * <pre>
     * {@code
     * @MapTable
     * public class SimpleTable {
     *     public SimpleTable(){}
     *     public SimpleTable(String name){
     *         this.myName = name;
     *     }
     *     @MapPrimaryKey
     *     public long id;
     *     @MapColumnName("name")
     *     public String myName;
     * }
     * ISession session=Configure.getSession();
     * session.insert(new SimpleTable("name"));
     * var o=session.firstOrDefault(SimpleTable.class,null);
     * o.myName="newName";
     * var res=session.update(o);
     * }
     * </pre>
     */
    <T> void insert(@NonNull T item);

    /**
     * Batch insert, please note that if objects contain incremental primary keys, these key fields are not updated after insertion.
     * @param tList The list of objects of class type must be marked with annotation @{@link MapTable} or @{@link MapTableName}
     * @param <T>   The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * <pre>
     * {@code
     * @MapTable
     * public class SimpleTable {
     *     public SimpleTable(){}
     *     public SimpleTable(String name){
     *         this.myName = name;
     *     }
     *     @MapPrimaryKey
     *     public long id;
     *     @MapColumnName("name")
     *     public String myName;
     * }
     *  List<SimpleTable> list=new ArrayList<>();
     *  for (int i = 0; i < 400 ; i++) {
     *     list.add(new SimpleTable("name:"+i));
     *   }
     * ISession session=Configure.getSession();
     * session.insertBulk(list);
     * }
     * </pre>
     */
    <T> void insertBulk(@NonNull List<T> tList);

    /**
     * Batch insert, please note that if objects contain incremental primary keys, these key fields are not updated after insertion.
     * @param object A collection of objects of class type must be marked with the annotation {@link MapTable} or {@link MapTableName}
     * @param <T>    The generic type must represent a class marked with the annotation {@link MapTable} or {@link MapTableName}
     * <pre>
     * {@code
     * @MapTable
     * public class SimpleTable {
     *     public SimpleTable(){}
     *     public SimpleTable(String name){
     *         this.myName = name;
     *     }
     *     @MapPrimaryKey
     *     public long id;
     *     @MapColumnName("name")
     *     public String myName;
     * }
     * ISession session=Configure.getSession();
     * session.insertBulk(new SimpleTable("name:"+1),new SimpleTable("name:"+2));
     * }
     * </pre>
     */
    <T> void insertBulk(@NonNull T... object);

    /**
     * Convenience method for deleting rows in the database.
     *
     * @param item An object, class type must be marked with annotation @{@link MapTable} or @{@link MapTableName}
     * @param <T>  The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * @return 1-success, 0 - not success
     * <pre>
     * {@code
     *
     * @MapTable
     * public class SimpleTable {
     *     public SimpleTable(){}
     *     public SimpleTable(String name){
     *         this.myName = name;
     *     }
     *     @MapPrimaryKey
     *     public long id;
     *     @MapColumnName("name")
     *     public String myName;
     * }
     * ISession session=Configure.getSession();
     * session.insert(new SimpleTable("name"));
     * var o=session.firstOrDefault(SimpleTable.class,null);
     * var res = session.delete(o);
     *
     * }
     * </pre>
     */
    <T> int delete(@NonNull T item);

    /**
     * Updating rows in a database based on a condition without loading rows on the client
     *
     * @param aClass       Instances of the  represent classes and interfaces in a running Java application.
     *                     This class must be annotated with the @{@link MapTable} or @{@link MapTableName} annotation, have a public parameterless constructor,
     *                     and a public field marked with the primary key annotation.
     * @param columnValues objects of type {@link PairColumnValue}
     * @param where        A fragment of a SQL query script from a condition, where
     *                     the condition, the word where, need not be written, string parameters are replaced with the parameter substitution character `?`,
     *                     and parameter values are entered into the object collection in the order they appear in the query.
     * @param objects      A collection of objects that replace the `?` symbols in a script, the order of the objects matches the order of the `?` symbols.
     * @param <T>          The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * @return Number of affected records or 0
     * <pre>
     * {@code
     * @MapTable
     * public class SimpleTable {
     *     public SimpleTable(){}
     *     public SimpleTable(String name){
     *         this.myName = name;
     *     }
     *     @MapPrimaryKey
     *     public long id;
     *     @MapColumnName("name")
     *     public String myName;
     * }
     * ISession session=Configure.getSession();
     * session.insert(new SimpleTable("name"));
     * session.updateRows(SimpleTable.class,new PairColumnValue().put("myName","newName"),null);
     * }
     * </pre>
     */
    <T> int updateRows(@NonNull Class<T> aClass, @NonNull PairColumnValue columnValues, String where, Object... objects);



    /**
     * Getting a list of rows from a database table based on a condition
     *
     * @param aClass  Instances of the  represent classes and interfaces in a running Java application.
     *                This class must be annotated with the @{@link MapTable} or @{@link MapTableName} annotation, have a public parameterless constructor,
     *                and a public field marked with the primary key annotation.
     * @param where   A fragment of a SQL query script from a condition, where
     *                the condition, the word where, need not be written, string parameters are replaced with the parameter substitution character `?`,
     *                and parameter values are entered into the object collection in the order they appear in the query.
     * @param objects A collection of objects that replace the `?` symbols in a script, the order of the objects matches the order of the `?` symbols.
     * @param <T>     The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * @return list objects of type T
     * <pre>
     * {@code
     * @MapTable
     * public class SimpleTable {
     *     public SimpleTable(){}
     *     public SimpleTable(String name){
     *         this.myName = name;
     *     }
     *     @MapPrimaryKey
     *     public long id;
     *     @MapColumnName("name")
     *     public String myName;
     * }
     * ISession session=Configure.getSession();
     * session.insert(new SimpleTable("name1"));
     * var list=session.getList(SimpleTable.class,"name = ?","name1");
     * }
     * </pre>
     */
    <T> List<T> getList(@NonNull Class<T> aClass, String where, Object... objects);



    /**
     * @param aClassFrom  Instances of the  represent classes and interfaces in a running Java application.
     *                This class must be annotated with the @{@link MapTable} or @{@link MapTableName} annotation, have a public parameterless constructor,
     *                and a public field marked with the primary key annotation.
     * @param aClassTo This is an arbitrary class type that should not be marked with any annotations.
     *                 The class field names must match the table class fields. The query will be built on the basis of these fields.
     * @param where   A fragment of a SQL query script from a condition, where
     *                the condition, the word where, need not be written, string parameters are replaced with the parameter substitution character `?`,
     *                and parameter values are entered into the object collection in the order they appear in the query.
     * @param objects A collection of objects that replace the `?` symbols in a script, the order of the objects matches the order of the `?` symbols.
     * @param <T>     The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * @param <D> Custom class type
     * @return list objects of type D
     */
    <T,D> List<D> getList(@NonNull Class<T> aClassFrom,@NonNull Class<D> aClassTo, String where, Object... objects);

    /**
     * @param aClass
     * @param where
     * @param objects
     * @param <T>
     * @return
     */
    <T> List<T> getListFree(@NonNull Class<T> aClass,String tableName, String where, Object... objects);

    /**
     * Allows you to bypass the selection cursor without creating a result list; on each bypass, a callback function will be called.
     * @param callback A callback function that will be called each time the course call is traversed
     * @param aClass  Instances of the  represent classes and interfaces in a running Java application.
     *                This class must be annotated with the @{@link MapTable} or @{@link MapTableName} annotation, have a public parameterless constructor,
     *                and a public field marked with the primary key annotation.
     * @param where   A fragment of a SQL query script from a condition, where
     *                the condition, the word where, need not be written, string parameters are replaced with the parameter substitution character `?`,
     *                and parameter values are entered into the object collection in the order they appear in the query.
     * @param objects A collection of objects that replace the `?` symbols in a script, the order of the objects matches the order of the `?` symbols.
     * @param <T>     The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     */
    <T> void cursorIterator(@NonNull Class<T> aClass,@NonNull IAction<T> callback, String where, Object... objects);


    /**
     * An iterator for traversing a Cursor with a callback function
     * @param aClass   Instances of the  represent classes and interfaces in a running Java application.
     *                 This class must be annotated with the @{@link MapTable} or @{@link MapTableName} annotation, have a public parameterless constructor,
     *                 and a public field marked with the primary key annotation.
     * @param cursor   @see <a href="https://developer.android.com/reference/android/database/Cursor">Cursor</a>
     * @param function lambda realisation {@link IAction}
     * @param <T>      The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * <pre>
     * {@code
     *
     * @MapTable
     * public class SimpleTable {
     *     public SimpleTable(){}
     *     public SimpleTable(String name){
     *         this.myName = name;
     *     }
     *     @MapPrimaryKey
     *     public long id;
     *     @MapColumnName("name")
     *     public String myName;
     * }
     * ISession session=Configure.getSession();
     * var cursor=session.execSQLRaw("select "name" from "+session.getTableName(SimpleTable.class));
     * List<String> stringList=new ArrayList<>();
     * session.cursorIterator(SimpleTable.class,cursor,o -> {
     *   stringList.add(o.name);
     * });
     *
     * }
     *
     * </pre>
     */
    <T> void cursorIterator(@NonNull Class<T> aClass, Cursor cursor, IAction<T> function);



    /**
     * Getting a list of all rows from a database table
     *
     * @param aClass Instances of the  represent classes and interfaces in a running Java application.
     *               This class must be annotated with the @{@link MapTable} or @{@link MapTableName} annotation, have a public parameterless constructor,
     *               and a public field marked with the primary key annotation.
     * @param <T>    The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * @return list objects of type T
     * <pre>
     * {@code
     * @MapTable
     * public class SimpleTable {
     *     public SimpleTable(){}
     *     public SimpleTable(String name){
     *         this.myName = name;
     *     }
     *     @MapPrimaryKey
     *     public long id;
     *     @MapColumnName("name")
     *     public String myName;
     * }
     * ISession session=Configure.getSession();
     * session.insert(new SimpleTable("name1"));
     * var list=session.getList(SimpleTable.class);
     * //select all rows
     * }
     * </pre>
     */
    <T> List<T> getList(@NonNull Class<T> aClass);

    /**
     * Use the ExecuteScalar method to retrieve a single value (for example, an aggregate value) from a database.Using parameters
     *
     * @param sql     SQL script in raw form, the ability to change values
     *                with the ? symbol, the observed values must be written to the object parameter, in the order in which they are written in the script.
     * @param objects A collection of objects that replace the `?` symbols in a script, the order of the objects matches the order of the `?` symbols.
     * @return objects of any type
     * <pre>
     * {@code
     * @MapTable
     * public class SimpleTable {
     *     public SimpleTable(){}
     *     public SimpleTable(String name){
     *         this.myName = name;
     *     }
     *     @MapPrimaryKey
     *     public long id;
     *     @MapColumnName("name")
     *     public String myName;
     * }
     * ISession session=Configure.getSession();
     * session.insert(new SimpleTable("name1"));
     * var count =session.executeScalar("Select count(*) from 'SimpleTable' where name = ?","name1");
     * }
     * </pre>
     */
    Object executeScalar(@NonNull String sql, Object... objects);

    /**
     * Use the ExecuteScalar method to retrieve a single value (for example, an aggregate value) from a database
     *
     * @param sql SQL script in raw form, the ability to change values
     *            with the ? symbol, the observed values must be written to the object parameter, in the order in which they are written in the script.
     * @return objects of any type
     * <pre>
     * {@code
     * @MapTable
     * public class SimpleTable {
     *     public SimpleTable(){}
     *     public SimpleTable(String name){
     *         this.myName = name;
     *     }
     *     @MapPrimaryKey
     *     public long id;
     *     @MapColumnName("name")
     *     public String myName;
     * }
     * ISession session=Configure.getSession();
     * session.insert(new SimpleTable("name1"));
     * var id =session.executeScalar(""SELECT last_insert_rowid();"");
     * }
     * </pre>
     */
    Object executeScalar(@NonNull String sql);

    /**
     * Executing a raw query to the database
     * @param sql     SQL script in raw form, the ability to change values
     *                with the ? symbol, the observed values must be written to the object parameter, in the order in which they are written in the script.
     * @param objects A collection of objects that replace the `?` symbols in a script, the order of the objects matches the order of the `?` symbols.
     * <pre>
     * {@code
     * @MapTable
     * ISession session=Configure.getSession();
     * session.execSQL("CREATE INDEX IF NOT EXISTS test_name ON 'test' ('name');");
     * }
     * </pre>
     */
    void executeSQL(@NonNull String sql, Object... objects);



    /**
     * Executing a raw query to the database, obtaining a cursor
     * @param sql     SQL script in raw form, the ability to change values
     *                with the ? symbol, the observed values must be written to the object parameter, in the order in which they are written in the script.
     * @param objects A collection of objects that replace the `?` symbols in a script, the order of the objects matches the order of the `?` symbols.
     * @return @see <a href="https://developer.android.com/reference/android/database/Cursor">Cursor</a>
     * <pre>
     * {@code
     * @MapTable
     * public class SimpleTable {
     *     public SimpleTable(){}
     *     public SimpleTable(String name){
     *         this.myName = name;
     *     }
     *     @MapPrimaryKey
     *     public long id;
     *     @MapColumnName("name")
     *     public String myName;
     * }
     * ISession session=Configure.getSession();
     * Cursor cursor= execSQLRaw("select * from "+ session.getTableName(SimpleTable.class));
     * }
     * </pre>
     */
    Cursor execSQLRaw(@NonNull String sql, Object... objects);

    /**
     * Obtaining the resulting sample based on a condition
     * @param sql     SQL script in raw form, the ability to change values
     *                with the ? symbol, the observed values must be written to the object parameter, in the order in which they are written in the script.
     * @param objects A collection of objects that replace the `?` symbols in a script, the order of the objects matches the order of the `?` symbols.
     * @return Dictionary: table column name - column value
     * <pre>
     * {@code
     * @MapTable
     * public class SimpleTable {
     *     public SimpleTable(){}
     *     public SimpleTable(String name){
     *         this.myName = name;
     *     }
     *     @MapPrimaryKey
     *     public long id;
     *     @MapColumnName("name")
     *     public String myName;
     * }
     * ISession session=Configure.getSession();
     * List<Map<String,Object>> res= execSQLRawMap("select mame,id from "+ session.getTableName(SimpleTable.class));
     * res.forEach(row -> {
     *  Log.I("---",row.get("name"));
     *  Log.I("---",row.get("id"));
     * });
     * }
     * </pre>
     */
    List<Map<String, Object>> execSQLRawMap(@NonNull String sql, Object... objects);

    /**
     * Obtaining the resulting sample based on a condition
     * @param sql     SQL script in raw form, the ability to change values
     *                with the ? symbol, the observed values must be written to the object parameter, in the order in which they are written in the script.
     * @param objects A collection of objects that replace the `?` symbols in a script, the order of the objects matches the order of the `?` symbols.
     * @return list Object[]
     * <pre>
     * {@code
     * @MapTable
     * public class SimpleTable {
     *     public SimpleTable(){}
     *     public SimpleTable(String name){
     *         this.myName = name;
     *     }
     *     @MapPrimaryKey
     *     public long id;
     *     @MapColumnName("name")
     *     public String myName;
     * }
     * ISession session=Configure.getSession();
     *  List<Object[]> res= execSQLRawMap("select mame,id from "+ session.getTableName(SimpleTable.class));
     * res.forEach(row -> {
     *  Log.I("---",row[0]);//name
     *  Log.I("---",row[1]);//id
     * });
     * }
     * </pre>
     */
    List<Object[]> execSQLRawArray(@NonNull String sql, Object... objects);

    /**
     * Getting the table name is usually needed to build a raw query.
     * @param <T>    The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * @param aClass Instances of the  represent classes and interfaces in a running Java application.
     *               This class must be annotated with the @{@link MapTable} or @{@link MapTableName} annotation, have a public parameterless constructor,
     *               and a public field marked with the primary key annotation.
     * @return string as table name
     * <pre>
     * {@code
     * @MapTable
     * public class SimpleTable {
     *     public SimpleTable(){}
     *     public SimpleTable(String name){
     *         this.myName = name;
     *     }
     *     @MapPrimaryKey
     *     public long id;
     *     @MapColumnName("name")
     *     public String myName;
     * }
     * ISession session=Configure.getSession();
     * Cursor cursor= execSQLRaw("select * from "+ session.getTableName(SimpleTable.class));
     * }
     * </pre>
     */
    <T> String getTableName(@NonNull Class<T> aClass);

    /**
     * Begins a transaction in EXCLUSIVE mode.
     * <pre>
     * {@code
     * ISession session=Configure.getSession();
     * session.deleteRows(MyTable.class);
     * session.beginTransaction();
     * try {
     *     for (int i = 0; i < 10; i++) {
     *         session.insert(factoryTable());
     *     }
     *     session.commitTransaction();
     * }catch (Exception e){}
     * finally {
     *     session.endTransaction();
     * }
     * var res=session.count(MyTable.class);
     * assertEquals(10,res);
     * }
     * </pre>
     */
    void beginTransaction();

    /**
     * Marks the current transaction as successful. Do not do any more database work between calling this and calling endTransaction.
     * Do as little non-database work as possible in that situation too.
     * If any errors are encountered between this and endTransaction the transaction will still be committed.
     * <pre>
     * {@code
     * ISession session=Configure.getSession();
     * session.deleteRows(MyTable.class);
     * session.beginTransaction();
     * try {
     *     for (int i = 0; i < 10; i++) {
     *         session.insert(factoryTable());
     *     }
     *     session.commitTransaction();
     * }catch (Exception e){}
     * finally {
     *     session.endTransaction();
     * }
     * var res=session.count(MyTable.class);
     * assertEquals(10,res);
     * }
     * </pre>
     */
    void commitTransaction();

    /**
     * End a transaction
     * <pre>
     * {@code
     * ISession session=Configure.getSession();
     * session.deleteRows(MyTable.class);
     * session.beginTransaction();
     * try {
     *     for (int i = 0; i < 10; i++) {
     *         session.insert(factoryTable());
     *     }
     *     session.commitTransaction();
     * }catch (Exception e){}
     * finally {
     *     session.endTransaction();
     * }
     * var res=session.count(MyTable.class);
     * assertEquals(10,res);
     * }
     * </pre>
     */
    void endTransaction();

    /**
     * Checks if a table exists in the database.
     * @param <T>    The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * @param aClass Instances of the  represent classes and interfaces in a running Java application.
     *               This class must be annotated with the @{@link MapTable} or @{@link MapTableName} annotation, have a public parameterless constructor,
     *               and a public field marked with the primary key annotation.
     * @return true - is existing, false - not exists
     * <pre>
     * {@code
     * @MapTable
     *
     * ISession session=Configure.getSession();
     * try{
     *    if(session.tableExists(SimpleTable.class)==false){
     *       session.createTable(SimpleTable.class);
     *     }
     * }catch(Exception e){}
     * }
     * </pre>
     *
     */
    <T> boolean tableExists(@NonNull Class<T> aClass);

    /**
     *  * Checks if a table exists in the database.
     * @param tableName  particular table name
     * @return true - is existing, false - not exists
     */
    boolean tableExists(@NonNull String tableName);

    /**
     * Deletes all rows from a table
     * @param aClass Instances of the  represent classes and interfaces in a running Java application.
     *               This class must be annotated with the @{@link MapTable} or @{@link MapTableName} annotation, have a public parameterless constructor,
     *               and a public field marked with the primary key annotation.
     * @param <T>    The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * @return Number of affected records or 0
     * <pre>
     * {@code
     * ISession session=Configure.getSession();
     * for (int i = 0; i < 10; i++) {
     *    session.insert(new SimpleTable("name:"+i);
     * }
     * var res=session.deleteRows(SimpleTable.class));
     * // delete all rows
     * assertEquals(10,res);
     * var list=session.getList(SimpleTable.class);
     * assertEquals(0,list.size());
     * }
     * </pre>
     *
     */
    <T> int deleteRows(@NonNull Class<T> aClass);

    /**
     * Deletes rows from a table based on a condition
     * @param aClass  Instances of the  represent classes and interfaces in a running Java application.
     *                This class must be annotated with the @{@link MapTable} or @{@link MapTableName} annotation, have a public parameterless constructor,
     *                and a public field marked with the primary key annotation.
     * @param <T>    The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * @param where   A fragment of a SQL query script from a condition, where
     *                the condition, the word where, need not be written, string parameters are replaced with the parameter substitution character `?`,
     *                and parameter values are entered into the object collection in the order they appear in the query.
     * @param objects A collection of objects that replace the `?` symbols in a script, the order of the objects matches the order of the `?` symbols.
     * @return Number of affected records or 0
     * <pre>
     * {@code
     * ISession session=Configure.getSession();
     * for (int i = 0; i < 10; i++) {
     *    session.insert(new SimpleTable("name:"+i);
     * }
     * var res=session.deleteRows(SimpleTable.class,"name not null"));
     * // delete rows only where name not null
     * assertEquals(10,res);
     * var list=session.getList(SimpleTable.class);
     * assertEquals(0,list.size());
     * }
     * </pre>
     *

     */
    <T>int deleteRows(@NonNull Class<T> aClass, String where, Object... objects);

    /**
     * Drops the table if it exists.
     * @param tableName table name
     * <pre>
     * {@code
     * ISession session=Configure.getSession();
     * session.dropTableIfExists(session.getTableName(MyTable.class));
     * var res=session.tableExists(MyTable.class);
     * assertEquals(false,res);
     * }
     * </pre>
     */
    void dropTableIfExists(@NonNull String tableName);

    /**
     * Drops the table if it exists.
     * @param aClass Instances of the  represent classes and interfaces in a running Java application.
     *               This class must be annotated with the @{@link MapTable} or @{@link MapTableName} annotation, have a public parameterless constructor,
     *               and a public field marked with the primary key annotation.
     * @param <T>    The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * <pre>
     * {@code
     * ISession session=Configure.getSession();
     * session.dropTableIfExists(MyTable.class);
     * var res=session.tableExists(MyTable.class);
     * assertEquals(false,res);
     * }
     * </pre>
     */
    <T> void dropTableIfExists(@NonNull Class<T> aClass);

    /**
     * Attempts to create a table; if unsuccessful, an exception is thrown.
     * @param aClass Instances of the  represent classes and interfaces in a running Java application.
     *               This class must be annotated with the @{@link MapTable} or @{@link MapTableName} annotation, have a public parameterless constructor,
     *               and a public field marked with the primary key annotation.
     * @param <T>    The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * @throws Exception
     * <pre>
     * {@code
     * @MapTable
     *
     * ISession session=Configure.getSession();
     * try{
     *    if(session.tableExists(SimpleTable.class)==false){
     *       session.createTable(SimpleTable.class);
     *     }
     * }catch(Exception e){}
     *
     * }
     * </pre>
     *
     * @exception Error creating table
     */
    <T> void  createTable(@NonNull Class<T> aClass) throws Exception;

    /**
     *
     * Creates a table if it does not exist.
     * @param <T>    The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * @param aClass Instances of the  represent classes and interfaces in a running Java application.
     *               This class must be annotated with the @{@link MapTable} or @{@link MapTableName} annotation, have a public parameterless constructor,
     *               and a public field marked with the primary key annotation.
     * @throws Exception Error creating table
     */
   <T> void createTableIfNotExists(@NonNull Class<T> aClass) throws Exception;

    /**
     * Gets the path to the database file.
     *
     * @return string as the path to the database file.
     */
    String getPath();

    /**
     * Returns a readable SQLiteDatabase object.
     *
     * @return object SQLiteDatabase
     * @see <a href="https://developer.android.com/reference/android/database/sqlite/SQLiteDatabase">SQLiteDatabase</a>
     */
    SQLiteDatabase getSqLiteDatabaseForReadable();

    /**
     * Returns a writable SQLiteDatabase object.
     *
     * @return object SQLiteDatabase
     * @see <a href="https://developer.android.com/reference/android/database/sqlite/SQLiteDatabase">SQLiteDatabase</a>
     */
    SQLiteDatabase getSqLiteDatabaseForWritable();

    /**
     * Get total number of records in the table
     * @param aClass Instances of the  represent classes and interfaces in a running Java application.
     *               This class must be annotated with the @{@link MapTable} or @{@link MapTableName} annotation, have a public parameterless constructor,
     *               and a public field marked with the primary key annotation.
     * @param <T>    The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * @return total number of records in the table
     * <pre>
     * {@code
     * }
     * </pre>
     *
     */
    <T> int count(@NonNull Class<T> aClass);

    /**
     * Gets the number of rows in a table, based on a condition
     * @param <T>    The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * @param aClass  Instances of the  represent classes and interfaces in a running Java application.
     *                This class must be annotated with the @{@link MapTable} or @{@link MapTableName} annotation, have a public parameterless constructor,
     *                and a public field marked with the primary key annotation.
     * @param where   A fragment of a SQL query script from a condition, where
     *                the condition, the word where, need not be written, string parameters are replaced with the parameter substitution character `?`,
     *                and parameter values are entered into the object collection in the order they appear in the query.
     * @param objects A collection of objects that replace the `?` symbols in a script, the order of the objects matches the order of the `?` symbols. A collection of objects that replace the `?` symbols in a script, the order of the objects matches the order of the `?` symbols.
     * @return number of records in a table with selection condition
     * <pre>
     * {@code
     * }
     * </pre>
     *
     */
    <T> int count(@NonNull Class<T> aClass, String where, Object... objects);

    /**
     * Checks if rows exist in a table
     * @param <T>    The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * @param aClass Instances of the  represent classes and interfaces in a running Java application.
     *               This class must be annotated with the @{@link MapTable} or @{@link MapTableName} annotation, have a public parameterless constructor,
     *               and a public field marked with the primary key annotation.
     * @return true not Empty table, false - Empty table
     * <pre>
     * {@code
     * ISession session=Configure.getSession();
     * session.deleteRows(SimpleTable.class);
     * for (int i = 0; i < 10; i++) {
     *     session.insert(new SimpleTable("name:"+i));
     * }
     * var res=session.any(SimpleTable.class);
     * assertEquals(true,res);
     * var res2=session.any(SimpleTable.class,"name=?","simple");
     * assertEquals(false,res2);
     * }
     * </pre>
     *
     */
    <T>boolean  any(@NonNull Class<T> aClass);

    /**
     * Checks if rows exist in a table based on a condition
     *
     * @param <T>    The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * @param aClass  Instances of the  represent classes and interfaces in a running Java application.
     *                This class must be annotated with the @{@link MapTable} or @{@link MapTableName} annotation, have a public parameterless constructor,
     *                and a public field marked with the primary key annotation.
     * @param where   A fragment of a SQL query script from a condition, where
     *                the condition, the word where, need not be written, string parameters are replaced with the parameter substitution character `?`,
     *                and parameter values are entered into the object collection in the order they appear in the query.
     * @param objects A collection of objects that replace the `?` symbols in a script, the order of the objects matches the order of the `?` symbols.
     * @return true - there are records, false - no entries
     * <pre>
     * {@code
     * ISession session=Configure.getSession();
     * session.deleteRows(SimpleTable.class);
     * for (int i = 0; i < 10; i++) {
     *     session.insert(new SimpleTable("name:"+i));
     * }
     * var res=session.any(SimpleTable.class);
     * assertEquals(true,res);
     * var res2=session.any(SimpleTable.class,"name=?","simple");
     * assertEquals(false,res2);
     * }
     * </pre>
     *
     */
    <T> boolean any(@NonNull Class<T> aClass, String where, Object... objects);

    /**
     * Gets the first value based on a condition; if it doesn't exist, returns null.
     * @param <T>    The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * @param aClass  Instances of the  represent classes and interfaces in a running Java application.
     *                This class must be annotated with the @{@link MapTable} or @{@link MapTableName} annotation, have a public parameterless constructor,
     *                and a public field marked with the primary key annotation.
     * @param where   A fragment of a SQL query script from a condition, where
     *                the condition, the word where, need not be written, string parameters are replaced with the parameter substitution character `?`,
     *                and parameter values are entered into the object collection in the order they appear in the query.
     * @param objects A collection of objects that replace the `?` symbols in a script, the order of the objects matches the order of the `?` symbols.
     * @return object of type T or null
     * <pre>
     * {@code
     * @MapTable
     * public class SimpleTable {
     *     public SimpleTable(){}
     *     public SimpleTable(String name){
     *         this.myName = name;
     *     }
     *     @MapPrimaryKey
     *     public long id;
     *     @MapColumnName("name")
     *     public String myName;
     * }
     * ISession session=Configure.getSession();
     * session.insert(new SimpleTable("name"));
     * var o=session.firstOrDefault(SimpleTable.class,"name not null");
     * o.myName="newName";
     * var res=session.update(o);
     * }
     * </pre>
     *
     */
    <T> T firstOrDefault(@NonNull Class<T> aClass, String where, Object... objects);

    /**
     * Get the first value based on the condition, if it does not exist, an exception is thrown
     * @param aClass  Instances of the  represent classes and interfaces in a running Java application.
     *                This class must be annotated with the @{@link MapTable} or @{@link MapTableName} annotation, have a public parameterless constructor,
     *                and a public field marked with the primary key annotation.
     * @param where   A fragment of a SQL query script from a condition, where
     *                the condition, the word where, need not be written, string parameters are replaced with the parameter substitution character `?`,
     *                and parameter values are entered into the object collection in the order they appear in the query.
     * @param objects A collection of objects that replace the `?` symbols in a script, the order of the objects matches the order of the `?` symbols.
     * @param <T>     The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * @return objects of type T or {@link Exception}
     * @throws Exception <pre>
     * {@code
     * @MapTable
     * public class SimpleTable {
     *     public SimpleTable(){}
     *     public SimpleTable(String name){
     *         this.myName = name;
     *     }
     *     @MapPrimaryKey
     *     public long id;
     *     @MapColumnName("name")
     *     public String myName;
     * }
     * ISession session=Configure.getSession();
     * try {
     *   SimpleTable  res=session.first(SimpleTable.class,"name=?","ion");
     * } catch (Exception e) {}
     * }
     * </pre>
     */
    <T> T first(@NonNull Class<T> aClass, String where, Object... objects) throws Exception;

    /**
     * Gets a single object based on a condition. If the condition is not met: the object is not found, or there is more than one, an exception is thrown.
     * @param aClass  Instances of the  represent classes and interfaces in a running Java application.
     *                This class must be annotated with the @{@link MapTable} or @{@link MapTableName} annotation, have a public parameterless constructor,
     *                and a public field marked with the primary key annotation.
     * @param where   A fragment of a SQL query script from a condition, where
     *                the condition, the word where, need not be written, string parameters are replaced with the parameter substitution character `?`,
     *                and parameter values are entered into the object collection in the order they appear in the query.
     * @param objects A collection of objects that replace the `?` symbols in a script, the order of the objects matches the order of the `?` symbols.
     * @param <T>     The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * @throws Exception <pre>
     * {@code
     * @MapTable
     * public class SimpleTable {
     *     public SimpleTable(){}
     *     public SimpleTable(String name){
     *         this.myName = name;
     *     }
     *     @MapPrimaryKey
     *     public long id;
     *     @MapColumnName("name")
     *     public String myName;
     * }
     * ISession session=Configure.getSession();
     * try {
     *   SimpleTable  res=session.single(SimpleTable.class,"id=10");
     *   // or session.single(SimpleTable.class,"id=?",10);
     * } catch (Exception e) {}
     * }
     * </pre>
     * @return  objects or Exception
     */
    <T> T single(@NonNull Class<T> aClass, String where, Object... objects) throws Exception;

    /**
     * Returns a single object; if there is none or it is not the only one, null is returned.
     * @param aClass  Instances of the  represent classes and interfaces in a running Java application.
     *                This class must be annotated with the @{@link MapTable} or @{@link MapTableName} annotation, have a public parameterless constructor,
     *                and a public field marked with the primary key annotation.
     * @param where   A fragment of a SQL query script from a condition, where
     *                the condition, the word where, need not be written, string parameters are replaced with the parameter substitution character `?`,
     *                and parameter values are entered into the object collection in the order they appear in the query.
     * @param objects A collection of objects that replace the `?` symbols in a script, the order of the objects matches the order of the `?` symbols.
     * @param <T>     The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * <pre>
     * {@code
     * @MapTable
     * public class SimpleTable {
     *     public SimpleTable(){}
     *     public SimpleTable(String name){
     *         this.myName = name;
     *     }
     *     @MapPrimaryKey
     *     public long id;
     *     @MapColumnName("name")
     *     public String myName;
     * }
     * ISession session=Configure.getSession();
     *   SimpleTable  res=session.singleOrDefault(SimpleTable.class,"id=10");
     *   // or session.singleOrDefault(SimpleTable.class,"id=?",10);
     * }
     * </pre>
     * @return The first object obtained by the condition, if the object does not exist, will return null
     */
    <T> T singleOrDefault(@NonNull Class<T> aClass, String where, Object... objects);

    /**
     * Getting a list of values for one field of a database table
     * @param aClass     Instances of the  represent classes and interfaces in a running Java application.
     *                   This class
     * @param columnName The name of the field in the table by which the selection is made
     * @param where      A fragment of a SQL query script from a condition, where
     *                   the condition, the word where, need not be written, string parameters are replaced with the parameter substitution character `?`,
     *                   and parameter values are entered into the object collection in the order they appear in the query.
     * @param objects    A collection of objects that replace the `?` symbols in a script, the order of the objects matches the order of the `?` symbols.
     * @param <T>        The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * @param <D>        any type
     * @return List objects any types
     * <pre>
     * {@code
     * @MapTable
     * public class SimpleTable {
     *     public SimpleTable(){}
     *     public SimpleTable(String name){
     *         this.myName = name;
     *     }
     *     @MapPrimaryKey
     *     public long id;
     *     @MapColumnName("name")
     *     public String myName;
     * }
     * ISession session=Configure.getSession();
     * List<String>  res=session.getListSelect(SimpleTable.class,"name","name not null");
     * }
     * </pre>
     */
    <T, D> List<D> getListSelect(@NonNull Class<T> aClass, @NonNull String columnName, String where, Object... objects);


    /**
     *Obtaining a dictionary of objects grouped by a database table column, with a selection condition
     * @param aClass   Instances of the  represent classes and interfaces in a running Java application.
     *                 This class must be annotated with the @{@link MapTable} or @{@link MapTableName} annotation, have a public parameterless constructor,
     *                 and a public field marked with the primary key annotation.
     * @param columnName The column in the database table by which grouping occurs
     * @param where   A fragment of a SQL query script from a condition, where
     *                the condition, the word where, need not be written, string parameters are replaced with the parameter substitution character `?`,
     *                and parameter values are entered into the object collection in the order they appear in the query.
     * @param objects A collection of objects that replace the `?` symbols in a script, the order of the objects matches the order of the `?` symbols.
     * @param <T>     The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * @return result map, key is the grouping value, value is the list of objects in which this value occurs
     */
    <T> Map<Object,List<T>> groupBy(@NonNull Class<T> aClass, @NonNull String columnName, String where, Object... objects);

    /**
     * Getting unique values for one table column
     * @param aClass   Instances of the  represent classes and interfaces in a running Java application.
     *                 This class must be annotated with the @{@link MapTable} or @{@link MapTableName} annotation, have a public parameterless constructor,
     *                 and a public field marked with the primary key annotation.
     * @param columnName The column in a database table by which data is retrieved
     * @param where   A fragment of a SQL query script from a condition, where
     *                the condition, the word where, need not be written, string parameters are replaced with the parameter substitution character `?`,
     *                and parameter values are entered into the object collection in the order they appear in the query.
     * @param objects A collection of objects that replace the `?` symbols in a script, the order of the objects matches the order of the `?` symbols.
     * @param <T>     The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * @return List of unique values
     */
    <T> List<Object> distinctBy(@NonNull Class<T> aClass, @NonNull String columnName, String where, Object... objects);

    /**
     * Checks if the session is closed
     * @return false - session closed, true - session alive
     */
    boolean IsAlive();


    /**
     * Getting object {@link ContentValues} from object DTO. You may need this if you will be working directly with {@link SQLiteDatabase}
     * @param item An object, class type must be marked with annotation @{@link MapTable} or @{@link MapTableName}
     * @param <T>     The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}

     * @return object {@link ContentValues}
     */
    <T> ContentValues getContentValues(@NonNull T item);


    /**
     * Getting object {@link ContentValues} You may need this if you will be working directly with {@link SQLiteDatabase}
     * @param aClass   Instances of the  represent classes and interfaces in a running Java application.
     *                 This class must be annotated with the @{@link MapTable} or @{@link MapTableName} annotation, have a public parameterless constructor,
     *                 and a public field marked with the primary key annotation.
     * @param columnValues objects of type {@link PairColumnValue}
     * @param <T>     The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}

     * @return object {@link ContentValues}
     */
    <T> ContentValues getContentValuesForUpdate(@NonNull Class<T> aClass,PairColumnValue columnValues);

    /**
     * This method inserts or updates objects that inherit the Persistent class, depending on the value of orm it decides whether to insert or update the object.
     * @param item An object, class type must be marked with annotation @{@link MapTable} or @{@link MapTableName}
     * @param <T>  The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * @return 1-success , 0 - not success
     */
    <T> int save(@NonNull T item);

}
