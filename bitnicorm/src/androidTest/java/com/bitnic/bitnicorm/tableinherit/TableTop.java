package com.bitnic.bitnicorm.tableinherit;

import com.bitnic.bitnicorm.MapColumn;
import com.bitnic.bitnicorm.MapTableName;

@MapTableName("t32")
public class TableTop extends TableMiddle {
    public TableTop(){
        super();
    }

    public TableTop(String name,String email,int age){

        super(name,age);
        this.email=email;
    }

    @MapColumn
    public String email;
}
