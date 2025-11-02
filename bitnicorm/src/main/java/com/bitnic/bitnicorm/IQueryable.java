package com.bitnic.bitnicorm;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * The interface Queryable.Allows you to create a chain of queries to the database (Fluent Interface)
 *
 * @param <T> The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
 *           If you use the rawSqlSelect function, the class type can be arbitrary, without annotations.
 *
 * <pre>
 * {@code
 * ISession session=Configure.getSession();
 * var list = session.query(MyTable.class).where(" age=?",30).where(" name=?","name").orderBy("name").limit(5).toList();
 * }
 * </pre>
 * @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
 */
public interface IQueryable<T> {



    /**
     * Where queryable.
     *
     * @param where      A fragment of a SQL query script from a condition, where
     *                   the condition, the word where, need not be written, string parameters are replaced with the parameter substitution character `?`,
     *                   and parameter values are entered into the object collection in the order they appear in the query.
     * @param parameters A collection of parameters that replace the `?` symbols in a script, the order of the parameters matches the order of the `?` symbols.
     * @return the queryable
     *
     * <pre>
     * {@code
     * ISession session=Configure.getSession();
     * var list = session.query(MyTable.class).where(" age=?",30).where(" name=?","name").orderBy("name").toList();
     * }
     * </pre>
     * @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
     */
    IQueryable<T> where(@NonNull String where, Object... parameters);

    /**
     * Adds the LIMIT parameter to the database query.
     *
     * @param limit Parameter value
     * @return the queryable
     *
     * <pre>
     * {@code
     * ISession session=Configure.getSession();
     * var list = session.query(MyTable.class).where(" age=?",30).where(" name=?","name").orderBy("name").limit(5).toList();
     * }
     * </pre>
     * @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
     */
    IQueryable<T> limit(int limit);

    /**
     * Adds the LIMIT OFFSET parameter to the database query.
     *
     * @param rowCount Parameter value
     * @param offSet   Parameter value
     * @return the queryable
     *
     * <pre>
     * {@code
     * ISession session=Configure.getSession();
     * var list = session.query(MyTable.class).where(" age=?",30).where(" name=?","name").orderBy("name").limitOffSet(5,2).toList();
     * }
     * </pre>
     * @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
     */

    IQueryable<T> limitOffSet(int rowCount, int offSet);

    /**
     * Adds the ORDER BY parameter to the database query.
     *
     * @param columnName the column name
     * @return the queryable
     *
     * <pre>
     * {@code
     * ISession session=Configure.getSession();
     * var list = session.query(MyTable.class).where(" age=?",30).orderBy("name").orderBy("age").toList();
     * }
     * </pre>
     * @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
     */
    IQueryable<T> orderBy(@NonNull String columnName);

    /**
     * Adds the ORDER BY parameter to the database query.
     *
     * @param columnName the column name
     * @return the queryable
     *
     * <pre>
     * {@code
     * ISession session=Configure.getSession();
     * var list = session.query(MyTable.class).where(" age=?",30).orderByDesc("age").toList();
     * }
     * </pre>
     * @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
     */
    IQueryable<T> orderByDesc(@NonNull String columnName);

    /**
     * Update queryable.( column, new value)
     *
     * @param columnName the column name
     * @param value      the new value
     * @return the queryable
     *
     * <pre>
     * {@code
     * ISession session=Configure.getSession();
     * var i = session.query(MyTable.class).where(" age=?",30).update("name","newName").update("age",30).updateNow();
     * }
     * </pre>
     * @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
     */
    IQueryable<T> update(@NonNull String columnName, Object value);

    /**
     * Allows you to get a sample for any types, even those whose classes are not marked with annotation
     * @param sql Database query string in the form (select * from table)
     * @return IQueryable
     */
    IQueryable<T> rawSqlSelect(@NonNull String sql);

    /**
     * Gets the first value based on a condition; if it doesn't exist, returns null.
     *
     * @return Object of type T or null
     *
     * <pre>
     * {@code
     * ISession session=Configure.getSession();
     * var user = session.query(MyTable.class).where(" age=?",30).orderBy("name").orderBy("age").firstOrDefault();
     * }
     * </pre>
     * @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
     */
    T firstOrDefault();

    /**
     * Get the first value based on the condition, if it does not exist, an exception is thrown
     *
     * @return Object of type T or Exception
     * @throws Exception the exception
     *
     *  <pre>
     *  {@code
     *  ISession session=Configure.getSession();
     *  var user = session.query(MyTable.class).where(" age=?",30).orderBy("name").orderBy("age").first();
     *  }
     *  </pre>
     *  @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
     */
    T first() throws Exception;

    /**
     * Returns a single object; if there is none, or it is not the only one, null is returned.
     *
     * @return Object of type T or null
     *
     * <pre>
     * {@code
     * ISession session=Configure.getSession();
     * var user = session.query(MyTable.class).where(" age=?",30).orderBy("name").orderBy("age").singleOrDefault();
     * }
     * </pre>
     * @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
     */
    T singleOrDefault();

    /**
     * Returns a single object; if there is none, or it is not the only one, null is returned.
     *
     * @return Object of type T or Exception
     * @throws Exception the exception
     *
     * <pre>
     * {@code
     * ISession session=Configure.getSession();
     * var user = session.query(MyTable.class).where(" age=?",30).orderBy("name").orderBy("age").single();
     * }
     * </pre>
     * @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
     */
    T single() throws Exception;

    /**
     * Obtaining a dictionary of parameters grouped by a database table column, with a selection condition
     *
     * @param columnName The column in the database table by which grouping occurs
     * @return result map, key is the grouping value, value is the list of parameters in which this value occurs
     *
     * <pre>
     * {@code
     * ISession session=Configure.getSession();
     * var map = session.query(MyTable.class).groupBy("age");
     * }
     * </pre>
     * @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
     */

    Map<Object, List<T>> groupBy(@NonNull String columnName);

    /**
     * Getting the table name is usually needed to build a raw query.
     *
     * @return the table name
     *
     * <pre>
     * {@code
     * ISession session=Configure.getSession();
     * var tableName = session.query(MyTable.class).getTableName();
     * }
     * </pre>
     * @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
     */
    String getTableName();

    /**
     * Deletes all rows from a table
     *
     * @return Number of affected records or 0
     *
     * <pre>
     * {@code
     * ISession session=Configure.getSession();
     * var i = session.query(MyTable.class).where(" age=?",30).deleteRows();
     * }
     * </pre>
     * @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
     */
    int deleteRows();

    /**
     * Checks if a table exists in the database.
     *
     * @return true - is existing, false - not exists
     *
     * <pre>
     * {@code
     * ISession session=Configure.getSession();
     * var res = session.query(MyTable.class).tableExists();
     * }
     * </pre>
     * @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
     */
    boolean tableExists();

    /**
     * Getting a list of rows from a database table based on a condition
     *
     * @return list parameters of type T
     *
     * <pre>
     * {@code
     * ISession session=Configure.getSession();
     * var list = session.query(MyTable.class).where(" age=?",30).orderBy("name").orderBy("age").toList();
     * }
     * </pre>
     * @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
     */
    List<T> toList();

    /**
     * Distinct by list.
     *
     * @param columnName the column name
     * @return the list
     *
     * <pre>
     * {@code
     * ISession session=Configure.getSession();
     * var list = session.query(MyTable.class).distinctBy("age");
     * }
     * </pre>
     * @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
     */
    List<Object> distinctBy(@NonNull String columnName);

    /**
     * GGetting a list of values for one field of a database table
     *
     * @param columnName The name of the field in the table by which the selection is made
     * @return List parameters any type
     *
     * <pre>
     * {@code
     * ISession session=Configure.getSession();
     * var list = session.query(MyTable.class).select("name");
     * }
     * </pre>
     * @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
     */
    List<Object> select(@NonNull String columnName);

    /**
     * Drops the table if it exists.
     *
     * <pre>
     * {@code
     * ISession session=Configure.getSession();
     * var res = session.query(MyTable.class).dropTableIfExists();
     * }
     * </pre>
     * @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
     */
    void dropTableIfExists();

    /**
     * Attempts to create a table; if unsuccessful, an exception is thrown.
     *
     * @throws Exception the exception
     *
     * <pre>
     * {@code
     * ISession session=Configure.getSession();
     * var res = session.query(MyTable.class).createTable();
     * }
     * </pre>
     * @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
     */
    void createTable() throws Exception;

    /**
     * Creates a table if it does not exist.
     *
     * @throws Exception the exception
     *
     * <pre>
     * {@code
     * ISession session=Configure.getSession();
     * var res = session.query(MyTable.class).createTableIfNotExists();
     * }
     * </pre>
     * @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
     */
    void createTableIfNotExists() throws Exception;

    /**
     * Get total number of records in the table
     *
     * @return the value
     *
     * <pre>
     * {@code
     * ISession session=Configure.getSession();
     * var res = session.query(MyTable.class).where(" age=?",30).count();
     * }
     * </pre>
     * @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
     */
    int count();

    /**
     * Checks if rows exist in a table
     *
     * @return true not Empty table, false - Empty table
     *
     * <pre>
     * {@code
     * ISession session=Configure.getSession();
     * var res = session.query(MyTable.class).where(" age=?",30).any();
     * }
     * </pre>
     * @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
     */
    boolean any();



    /**
     * Update now
     *
     * @return Number of affected records or 0
     *
     * <pre>
     * {@code
     * ISession session=Configure.getSession();
     * var i = session.query(MyTable.class).where(" age=?",30).update("name","newName").update("age",30).updateNow();
     * }
     * </pre>
     * @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
     */
    int updateNow();

    /**
     * Called on each iteration of the cursor, without creating a list
     *
     * @param action Called on each cursor iteration
     *
     * <pre>
     * {@code
     * ISession session=Configure.getSession();
     * List<Integer> integers=new ArrayList<>();
     * session.query(MyTable.class).where(" name not null").iterator(o -> {
     *    integers.add(o.age);
     * });
     * //or for simple class, without annotation
     * session.query(AnyTAble.class).rawSqlSelect("select id,name from").where(" name not null").iterator(o -> {
     *    integers.add(o.age);
     * });
     * }
     * </pre>
     *  @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
     */
     void iterator(ITask<T> action);

    /** Getting a list of values or one table field, the field can be calculated,
     * @param expression Expression value string
     * @return CompletableFuture&lt;List&lt;T&gt;&gt;
     *
     * <pre>
     * {@code
     * ISession session=Configure.getSession();
     * var list = session.query(MyTable.class).where("age>10").selectExpression("age*10");
     * }
     * </pre>
     * @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
     */
    List<Object> selectExpression(String expression);

    /**
     * Gets a wrapper around the CompletableFuture's toList method, allowing non-blocking operations to be performed on the main thread.
     * @return CompletableFuture&lt;List&lt;T&gt;&gt;
     *
     * <pre>
     * {@code
     * ISession session=Configure.getSession();
     * session.query(MyTable.class).toListAsync()
     * .handleAsync((myTables, throwable) -> {
     *     if(throwable!=null){
     *          List<MyTable> list1 = new ArrayList<>();
     *          return list1;
     *     }
     *     return myTables;
     *
     *  }).thenAcceptAsync(myTables -> {
     *       for (MyTable myTable : myTables) {
     *            list.add(myTable);
     *            Log.i("____________",myTable.name);
     *        }
     *  });
     * }
     * </pre>
     * @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
     */
    CompletableFuture<List<T>> toListAsync();

    /**
     *  Gets a wrapper around the CompletableFuture's select method, allowing non-blocking operations to be performed on the main thread.
     * @param columnName column name of table
     * @return CompletableFuture&lt;List&lt;Object&gt;&gt;
     *
     * <pre>
     * {@code
     * ISession session=Configure.getSession();
     * List<String> list = new ArrayList<>();
     * CompletableFuture<List<Object>> f;
     * f = session.query(MyTable.class).where("age > ?",-1).selectAsync("name");
     * f.thenAcceptAsync(objects -> {
     *     for (Object o : objects) {
     *         list.add((String) o);
     *         Log.i("____________",String.valueOf(o));
     *     }
     * });
     *
     * }
     * </pre>
     * @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
     */
    CompletableFuture<List<Object>> selectAsync(@NonNull String columnName);

    /**
     * Gets a wrapper around the CompletableFuture's selectExpression method, allowing non-blocking operations to be performed on the main thread.
     * @param expression Expression value string
     * @return CompletableFuture&lt;List&lt;Object&gt;&gt;
     *
     * <pre>
     * {@code
     *  session.query(MyTable.class).selectAsync("name").thenAcceptAsync(list -> {
     *          binding.textviewFirst.setText(String.valueOf(list.size()));
     *  });
     * }
     * </pre>
     * @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
     */
    CompletableFuture<List<Object>> selectExpressionAsync(@NonNull String expression);


    /**
     * Gets a wrapper around the CompletableFuture's any method, allowing non-blocking operations to be performed on the main thread.
     *
     * @return CompletableFuture&lt;Boolean&gt;
     *
     * <pre>
     * {@code
     * session.query(MyTable.class).anyAsync().thenAcceptAsync(any -> {
     *     binding.textviewFirst.setText(String.valueOf(any));
     * });
     *
     * }
     * </pre>
     * @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
     */
    CompletableFuture<Boolean> anyAsync();

    /**
     * Gets a wrapper around the CompletableFuture's count method, allowing non-blocking operations to be performed on the main thread.
     * @return CompletableFuture&lt;Integer&gt;
     *
     * <pre>
     * {@code
     * session.query(MyTable.class).anyAsync().thenAcceptAsync(count -> {
     *    binding.textviewFirst.setText(String.valueOf(count));
     * });
     *
     * }
     * </pre>
     * @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
     */
    CompletableFuture<Integer> countAsync();

    /**
     *  Gets a wrapper around the CompletableFuture's updateNow method, allowing non-blocking operations to be performed on the main thread.
     *
     * @return CompletableFuture &lt;List&lt;Object&gt;&gt;
     *
     * <pre>
     * {@code
     *  session.query(MyTable.class)
     *     .update("name","newName")
     *     .where("age = ",10)
     *     .updateNowAsync()
     *     .thenAcceptAsync(integer -> {
     *         Log.i("update",String.valueOf(integer));
     *     });
     *
     * }
     * </pre>
     * @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
     */
    CompletableFuture<Integer> updateNowAsync();

    /**
     *  Gets a wrapper around the CompletableFuture's distinctBy method, allowing non-blocking operations to be performed on the main thread.
     *
     * @param columnName column name of table
     * @return CompletableFuture&lt;List&lt;Object&gt;&gt;
     *
     * <pre>
     * {@code
     * session.query(MyTable.class).distinctByAsync("name").thenAcceptAsync(list -> {
     *     binding.textviewFirst.setText(String.valueOf(list.size()));
     * });
     *
     * }
     * </pre>
     * @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
     */
    CompletableFuture<List<Object>> distinctByAsync(@NonNull String columnName);

    /**
     *  Gets a wrapper around the CompletableFuture's groupBy method, allowing non-blocking operations to be performed on the main thread.
     *
     * @param columnName column name of table
     * @return CompletableFuture&lt;Map&lt;Object, List&lt;T&gt;&gt;&gt;
     *
     * <pre>
     * {@code
     * session.query(MyTable.class)
     *      .where("name not null")
     *      .groupByAsync("age")
     *      .thenAcceptAsync(list -> {
     *          binding.textviewFirst.setText(String.valueOf(list.size()));
     *      });
     *
     * }
     * </pre>
     * @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
     */
    CompletableFuture<Map<Object, List<T>>> groupByAsync(@NonNull String columnName);


    /**
     *  Gets a wrapper around the CompletableFuture's singleOrDefault method, allowing non-blocking operations to be performed on the main thread.
     *
     * @return CompletableFuture&lt;T&gt;
     *
     * <pre>
     * {@code
     * session.query(MyTable.class)
     *       .singleOrDefaultAsync()
     *       .thenAcceptAsync(myTable -> {
     *           binding.textviewFirst.setText(myTable==null?"none":myTable.name);
     *       });
     * }
     * </pre>
     * @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
     */
    CompletableFuture<T> singleOrDefaultAsync();

    /**
     *  Gets a wrapper around the CompletableFuture's firstOrDefault method, allowing non-blocking operations to be performed on the main thread.
     *
     * @return CompletableFuture&lt;T&gt;
     *
     * <pre>
     * {@code
     * session.query(MyTable.class)
     *       .firstOrDefaultAsync()
     *       .thenAcceptAsync(myTable -> {
     *           binding.textviewFirst.setText(myTable==null?"none":myTable.name);
     *       });
     * }
     * </pre>
     * @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
     */
    CompletableFuture<T> firstOrDefaultAsync();

    /**
     * Gets a wrapper around the CompletableFuture's getById method, allowing non-blocking operations to be performed on the main thread.
     *
     * @param primaryKey Primary key value
     * @return CompletableFuture&lt;T&gt;
     *
     * <pre>
     * {@code
     *  session.query(MyTable.class).getByIdAsync(5).thenAcceptAsync(myTable -> {
     *       binding.textviewFirst.setText(myTable==null?"none":myTable.name);
     * });
     * }
     * </pre>
     * @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
     */
    CompletableFuture<T> getByIdAsync( @NonNull Object primaryKey);

    /**
     * Gets a wrapper around the CompletableFuture's getById method, allowing non-blocking operations to be performed on the main thread.
     *
     * @param primaryKey Primary key value
     * @return T object
     *
     * <pre>
     *     Get an Object from a Database by Primary Key
     * {@code
     *  MyClass o=session.query(MyTable.class).getById(5);
     * });
     * }
     * </pre>
     * @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
     */
    T getById( @NonNull Object primaryKey);



}
