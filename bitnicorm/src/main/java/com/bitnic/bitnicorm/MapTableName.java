package com.bitnic.bitnicorm;
/********************************************************************
 * Copyright © 2016-2017 OOO Bitnic                                 *
 * Created by OOO Bitnic on 08.02.16   corp@bitnic.ru               *
 * ******************************************************************/


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The type class marked with this annotation is projected into a table; you must define the name of this table.
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
@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface MapTableName {
    /**
     * Table name, quotation marks are optional
     *
     * @return the string
     */
    String value();
}



