package com.bitnic.bitnicorm;



import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The type class marked with this annotation is projected into a table, the table name will correspond to the type class name.
 * <pre>
 * {@code
 * @MapTable
 * public class SimpleTable {
 *     @MapPrimaryKeyName("idKey")
 *     public long id;
 *     @MapColumn
 *     public String myName;
 * }
 * sql:
 * CREATE TABLE IF NOT EXISTS 'SimpleTable' (
 * 'idKey'  INTEGER  PRIMARY KEY,
 * 'myName' TEXT DEFAULT NULL);
 * }
 * </pre>
 * @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
 */
@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface MapTable {

}



