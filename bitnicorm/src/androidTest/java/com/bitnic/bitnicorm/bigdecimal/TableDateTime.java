package com.bitnic.bitnicorm.bigdecimal;

import com.bitnic.bitnicorm.MapColumn;
import com.bitnic.bitnicorm.MapPrimaryKey;
import com.bitnic.bitnicorm.MapTableName;

import java.util.Date;

/**
 * The type Table date time.
 */
@MapTableName("34_398483433")
public class TableDateTime {

    /**
     * The Id.
     */
    @MapPrimaryKey
    public int id;

    /**
     * The Date.
     */
    @MapColumn
    public Date date;
}
