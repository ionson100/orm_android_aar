package com.bitnic.bitnicorm;

/**
 * The type Table user.
 */
@MapTableName("user")
public class TableUser {
    /**
     * The Id.
     */
    @MapPrimaryKeyName("id")
    public int id;

    /**
     * The User class.
     */
    @MapColumnName("user")
    //@MapUserType
    public UserClass userClass;

    /**
     * The Address.
     */
    @MapColumnName("address")
    @MapColumnType("TEXT NOT NULL UNIQUE")
    public String address;
}
