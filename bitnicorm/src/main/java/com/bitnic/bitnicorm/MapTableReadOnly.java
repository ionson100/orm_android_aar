package com.bitnic.bitnicorm;



import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * A type marked with this interface can only retrieve data from a table.
 *
 * <pre>
 * {@code
 * @MapTableReadOnly
 * @MapTableName("myTableSimple")
 * public class SimpleTable {
 *
 *     @MapPrimaryKey
 *     public long id;
 *
 *     @MapColumn
 *     public String name;
 * }
 * }
 * </pre>
 * @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
 */
@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface MapTableReadOnly {

}



