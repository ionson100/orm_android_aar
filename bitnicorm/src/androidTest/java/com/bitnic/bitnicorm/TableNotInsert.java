package com.bitnic.bitnicorm;

@MapTable
public class TableNotInsert {
    @MapPrimaryKey
    public long id;

    @MapColumn
    @MapColumnReadOnly
    @MapColumnType("TEXT DEFAULT SIMPLE")
    public String name;

    @MapColumnName("age")
    public int age;

    @MapColumn
    public String myEmail="assHol@ass.com";
}
