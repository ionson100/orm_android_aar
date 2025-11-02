package com.bitnic.bitnicorm;



import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation allows you to execute queries (toList, getListSelect, first, firstOrDefault, single, singleOrDefault,updateRows,deleteRows), with the where condition,б
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
 * @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
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



