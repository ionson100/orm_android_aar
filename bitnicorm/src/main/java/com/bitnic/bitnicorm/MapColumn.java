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
 * fields of the type class marked with this annotation are projected into table columns,
 * the column name will correspond to the name of the field of the type class
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
public @interface MapColumn {
}

