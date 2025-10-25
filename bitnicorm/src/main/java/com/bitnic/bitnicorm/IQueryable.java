package com.bitnic.bitnicorm;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Map;

/**
 * The interface Queryable.Allows you to create a chain of queries to the database (Fluent Interface)
 *
 * @param <T> The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
 *           If you use the rawSqlSelect function, the class type can be arbitrary, without annotations.
 */
public interface IQueryable<T> {

    /**
     * A custom query with a condition and the ability to insert parameters
     *
     * @param text       the text
     * @param parameters A collection of parameters that replace the `?` symbols in a script, the order of the parameters matches the order of the `?` symbols.
     * @return the queryable
     * <pre>
     * {@code
     * ISession session=Configure.getSession();
     * var list = session.query(MyTable.class).textSql(" age=? order by name",30).toList();
     * }
     * </pre>
     */
    IQueryable<T> textSql(@NonNull String text, Object... parameters);

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
     */
    IQueryable<T> orderBy(@NonNull String columnName);

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
     */
    T firstOrDefault();

    /**
     * Get the first value based on the condition, if it does not exist, an exception is thrown
     *
     * @return Object of type T or Exception
     * @throws Exception the exception
     *
     *                   <pre>
     *                   {@code
     *                   ISession session=Configure.getSession();
     *                   var user = session.query(MyTable.class).where(" age=?",30).orderBy("name").orderBy("age").first();
     *                   }
     *                   </pre>
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
     */
    T singleOrDefault();

    /**
     * Returns a single object; if there is none, or it is not the only one, null is returned.
     *
     * @return Object of type T or Exception
     * @throws Exception the exception
     *
     *                   <pre>
     *                   {@code
     *                   ISession session=Configure.getSession();
     *                   var user = session.query(MyTable.class).where(" age=?",30).orderBy("name").orderBy("age").single();
     *                   }
     *                   </pre>
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
     */
    List<?> select(@NonNull String columnName);

    /**
     * Drops the table if it exists.
     *
     * <pre>
     * {@code
     * ISession session=Configure.getSession();
     * var res = session.query(MyTable.class).dropTableIfExists();
     * }
     * </pre>
     */
    void dropTableIfExists();

    /**
     * Attempts to create a table; if unsuccessful, an exception is thrown.
     *
     * @throws Exception the exception
     *
     *                   <pre>
     *                   {@code
     *                   ISession session=Configure.getSession();
     *                   var res = session.query(MyTable.class).createTable();
     *                   }
     *                   </pre>
     */
    void createTable() throws Exception;

    /**
     * Creates a table if it does not exist.
     *
     * @throws Exception the exception
     *
     *                   <pre>
     *                   {@code
     *                   ISession session=Configure.getSession();
     *                   var res = session.query(MyTable.class).createTableIfNotExists();
     *                   }
     *                   </pre>
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
     */
     void iterator(IAction<T> action);






}
