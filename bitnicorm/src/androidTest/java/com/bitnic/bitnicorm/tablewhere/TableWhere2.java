package com.bitnic.bitnicorm.tablewhere;

import com.bitnic.bitnicorm.MapColumn;
import com.bitnic.bitnicorm.MapPrimaryKey;
import com.bitnic.bitnicorm.MapTableName;
import com.bitnic.bitnicorm.MapWhere;

/**
 * The type Table where 2.
 */
@MapTableName("table_where")
@MapWhere(" is_table = false ")
public class TableWhere2 {

    /**
     * Instantiates a new Table where 2.
     */
    public TableWhere2(){

    }

    /**
     * Instantiates a new Table where 2.
     *
     * @param name the name
     */
    public TableWhere2(String name){
        this.name=name;
    }

    /**
     * The Id.
     */
    @MapPrimaryKey
    public long id;

    /**
     * The Name.
     */
    @MapColumn
    public String name;

    /**
     * The Is table.
     */
    @MapColumn
    public boolean is_table=false;
}
