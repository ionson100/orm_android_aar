package com.bitnic.bitnicorm;

@MapTableName("user")
public class TableUser {
    @MapPrimaryKeyName("id")
    public int id;

    @MapColumnName("user")
    public UserClass userClass;

    @MapColumnName("address")
    @MapColumnType("TEXT NOT NULL UNIQUE")
    public String address;
}
