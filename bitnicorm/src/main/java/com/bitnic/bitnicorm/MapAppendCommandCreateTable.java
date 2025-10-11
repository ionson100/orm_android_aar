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
 * This annotation allows you to execute a command after creating a table if it does not exist in the database.
 * <pre>
 * {@code
 * @MapTableName("myTableSimple")
 * @MapAppendCommandCreateTable("CREATE INDEX index_name on myTableSimple (name);")
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
 * CREATE INDEX index_name on myTableSimple (name);
 * }
 * </pre>
 */
@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)

public @interface MapAppendCommandCreateTable {
    /**
     * Sql command to execute after creating the table
     *
     * @return the string
     */
    String value();
}
