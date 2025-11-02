package com.bitnic.bitnicorm;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A field marked with this annotation will not participate in insert and update queries.
 *
 * <pre>
 * {@code
 * @MapTableName("myTableSimple")
 * public class SimpleTable {
 *
 *     @MapPrimaryKey
 *     public long id;
 *
 *     @MapColumnReadOnly
 *     @MapColumn
 *     public Date insertDate;
 * }
 * }
 * </pre>
 * @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
 */
@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface MapColumnReadOnly {

}

