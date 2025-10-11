package com.bitnic.bitnicorm;

import java.util.Date;

/**
 * The type Simple table.
 */
@MapTableName("myTableSimple")
@MapWhere("name not null and id > 2")
public class SimpleTable {
    /**
     * The Id.
     */
    @MapPrimaryKey
    public long id;
    /**
     * The Name.
     */
    @MapColumnName("name")
    public String name;

    /**
     * The Date.
     */
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