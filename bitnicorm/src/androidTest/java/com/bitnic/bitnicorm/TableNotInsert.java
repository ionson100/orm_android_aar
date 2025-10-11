package com.bitnic.bitnicorm;

/**
 * The type Table not insert.
 */
@MapTable
public class TableNotInsert {
    /**
     * The Id.
     */
    @MapPrimaryKey
    public long id;

    /**
     * The Name.
     */
    @MapColumn
    @MapColumnReadOnly
    @MapColumnType("TEXT DEFAULT SIMPLE")
    public String name;

    /**
     * The Age.
     */
    @MapColumnName("age")
    public int age;

    /**
     * The My email.
     */
    @MapColumn
    public String myEmail="assHol@ass.com";
}
