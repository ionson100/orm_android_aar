package com.bitnic.bitnicorm;



import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation allows you to execute queries (getList, getListSelect, first, firstOrDefault, single, singleOrDefault,updateRows,deleteRows), with the where condition,б
 * the word 'where' is not required
 * <pre>
 * {@code
 * @MapTableName("myTableSimple")
 * @MapTableWhere("name not null and id > 2")
 * public class SimpleTable {
 *     @MapPrimaryKey
 *     public long id;
 *     @MapColumnName("name")
 *     public String myName;
 *
 *     @MapColumn
 *     public Date date;
 * }
 * }
 * </pre>
 */
@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface MapTableWhere {

    /**
     * A line with a selection condition without the word where
     * @return string
     */
    String value();
}



