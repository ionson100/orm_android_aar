package com.bitnic.bitnicorm.bigdecimal;

import com.bitnic.bitnicorm.MapColumn;
import com.bitnic.bitnicorm.MapPrimaryKey;
import com.bitnic.bitnicorm.MapTableName;

/**
 * The type Table byte array.
 */
@MapTableName("34_3984834123")
public class TableByteArray {

    /**
     * The Id.
     */
    @MapPrimaryKey
    public int id;

    /**
     * The A bytes 22.
     */
    @MapColumn
    public byte[] aBytes22;
}
