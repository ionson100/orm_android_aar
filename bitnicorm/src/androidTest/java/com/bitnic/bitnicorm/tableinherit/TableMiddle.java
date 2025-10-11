package com.bitnic.bitnicorm.tableinherit;

import com.bitnic.bitnicorm.MapColumn;
import com.bitnic.bitnicorm.MapTableName;

/**
 * The type Table middle.
 */
@MapTableName("t32")
public class TableMiddle extends TBaseClass{

    /**
     * Instantiates a new Table middle.
     */
    public TableMiddle(){
        super();
    }

    /**
     * Instantiates a new Table middle.
     *
     * @param name the name
     * @param age  the age
     */
    public TableMiddle(String name,int age){

        this.name = name;
        this.age = age;
    }

    /**
     * The Name.
     */
    @MapColumn
    public String name;
    /**
     * The Age.
     */
    @MapColumn
    public int age;
}
