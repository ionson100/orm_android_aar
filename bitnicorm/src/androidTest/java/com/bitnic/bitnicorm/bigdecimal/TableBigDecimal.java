package com.bitnic.bitnicorm.bigdecimal;

import com.bitnic.bitnicorm.MapColumn;
import com.bitnic.bitnicorm.MapPrimaryKey;
import com.bitnic.bitnicorm.MapTableName;

import java.math.BigDecimal;

/**
 * The type Table big decimal.
 */
@MapTableName("34_3984834")
public class TableBigDecimal {

    /**
     * The Id.
     */
    @MapPrimaryKey
    public int id;

    /**
     * The Big decimal.
     */
    @MapColumn
    public BigDecimal bigDecimal;
}
