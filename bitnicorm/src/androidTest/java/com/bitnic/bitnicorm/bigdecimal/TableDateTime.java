package com.bitnic.bitnicorm.bigdecimal;

import com.bitnic.bitnicorm.MapColumn;
import com.bitnic.bitnicorm.MapPrimaryKey;
import com.bitnic.bitnicorm.MapTableName;

import java.util.Date;

@MapTableName("34_398483433")
public class TableDateTime {

    @MapPrimaryKey
    public int id;

    @MapColumn
    public Date date;
}
