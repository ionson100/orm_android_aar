package com.bitnic.bitnicorm;



import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 *Adds a row when creating a table with a foreign key
 * <pre>
 * {@code
 *     @MapColumnName("name")
 *     @MapForeignKey("FOREIGN KEY (name) REFERENCES test (name) ")
 *     public  String name;
 * }
 * </pre>
 */
@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface MapForeignKey {
    /**
     * Value string.
     *
     * @return the string
     */
    String value();
}
