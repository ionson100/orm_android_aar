package com.bitnic.bitnicorm;

import java.util.Date;

@MapTableName("myTableSimple")
@MapTableWhere("name not null and id > 2")
public class SimpleTable {
    @MapPrimaryKey
    public long id;
    @MapColumnName("name")
    public String name;

    @MapColumn
    public Date date;
}

//@MapTable
//public class SimpleTable {
//    public SimpleTable(){}
//    public SimpleTable(String name){
//        this.myName = name;
//    }
//    @MapPrimaryKey
//    public long id;
//    @MapColumnName("name")
//    public String myName;
//}