

package com.bitnic.bitnicorm;

/**
 *If a DTO class inherits this class, the ORM can record where the object was obtained from, whether it was created on the client or retrieved from the database, and you can use the save method.
 * You can always know where the object was obtained from.
 *
 * <pre>
 * {@code
 *
 * @MapTableName("myTableSimple")
 * public class SimpleTable extends Persistent {
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
public class Persistent {

    /**
     * Default constructor
     */
    public Persistent(){}
    /**
     * true - an object obtained from the database
     * false - the object was created on the client and was not saved in the database
     */
    boolean isPersistent;

    /**
     * Where the object was obtained from, from the database or a new one created
     *
     *@return  true - an object obtained from the database, false - the object was created on the client and was not saved in the database
     *
     */
    public boolean isPersistent(){
        return isPersistent;
    }
}
