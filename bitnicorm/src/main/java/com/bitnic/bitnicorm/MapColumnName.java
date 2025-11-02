package com.bitnic.bitnicorm;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * fields of the type class marked with this annotation are projected into table columns,
 * you must define the column name yourself
 * <pre>
 * {@code
 * @MapTableName("myTableSimple")
 * public class SimpleTable {
 *     @MapPrimaryKey
 *     public long id;
 *     @MapColumnName("name")
 *     public String myName;
 * }
 * sql:
 * CREATE TABLE IF NOT EXISTS 'myTableSimple' (
 * 'id'  INTEGER  PRIMARY KEY,
 * 'name' TEXT DEFAULT NULL);
 * }
 * </pre>
 * @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
 */
@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface MapColumnName {
    /**
     * Column name, quotation marks are optional
     *
     * @return the string
     */
    String value();
}

