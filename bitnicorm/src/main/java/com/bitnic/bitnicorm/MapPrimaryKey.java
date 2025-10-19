package com.bitnic.bitnicorm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Fields of the type marked with this annotation are projected into the table in the primary key column,
 * the column name will correspond to the name of the field of the type
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
 */

@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface MapPrimaryKey {
}

