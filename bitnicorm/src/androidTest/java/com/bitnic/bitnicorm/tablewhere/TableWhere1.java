package com.bitnic.bitnicorm.tablewhere;

import com.bitnic.bitnicorm.MapColumn;
import com.bitnic.bitnicorm.MapPrimaryKey;
import com.bitnic.bitnicorm.MapTableName;
import com.bitnic.bitnicorm.MapWhere;

/**
 * The type Table where 1.
 */
@MapTableName("table_where")
@MapWhere(" is_table = true ")
public class TableWhere1 {
    /**
     * Instantiates a new Table where 1.
     */
    public TableWhere1(){

    }

    /**
     * Instantiates a new Table where 1.
     *
     * @param name the name
     */
    public TableWhere1(String name){
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
    public boolean is_table=true;
}
