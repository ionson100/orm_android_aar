package com.bitnic.bitnicorm.tableinherit;

import com.bitnic.bitnicorm.MapColumn;
import com.bitnic.bitnicorm.MapTableName;

@MapTableName("t32")
public class TableMiddle extends TBaseClass{

    public TableMiddle(){
        super();
    }

    public TableMiddle(String name,int age){

        this.name = name;
        this.age = age;
    }

    @MapColumn
    public String name;
    @MapColumn
    public int age;
}
