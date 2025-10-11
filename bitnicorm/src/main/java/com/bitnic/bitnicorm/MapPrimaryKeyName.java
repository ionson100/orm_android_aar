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
 * Fields of the type marked with this annotation are projected into the table in the primary key column,
 * you should note the name of this column
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
 */
@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface MapPrimaryKeyName {
    /**
     * Column name, quotation marks are optional
     *
     * @return the string
     */
    String value();
}

