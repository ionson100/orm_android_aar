package com.bitnic.bitnicorm.bigdecimal;

import com.bitnic.bitnicorm.MapColumn;
import com.bitnic.bitnicorm.MapPrimaryKey;
import com.bitnic.bitnicorm.MapTableName;

import java.math.BigDecimal;

@MapTableName("34_3984834")
public class TableBigDecimal {

    @MapPrimaryKey
    public int id;

    @MapColumn
    public BigDecimal bigDecimal;
}
