package com.bitnic.bitnicorm.tablewhere;

import com.bitnic.bitnicorm.MapColumn;
import com.bitnic.bitnicorm.MapPrimaryKey;
import com.bitnic.bitnicorm.MapTableName;
import com.bitnic.bitnicorm.MapWhere;

@MapTableName("table_where")
@MapWhere(" is_table = true ")
public class TableWhere1 {
    public TableWhere1(){

    }

    public TableWhere1(String name){
        this.name=name;
    }

    @MapPrimaryKey
    public long id;

    @MapColumn
    public String name;

    @MapColumn
    public boolean is_table=true;
}
