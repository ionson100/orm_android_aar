package com.bitnic.bitnicorm.bigdecimal;

import com.bitnic.bitnicorm.MapColumn;
import com.bitnic.bitnicorm.MapPrimaryKey;
import com.bitnic.bitnicorm.MapTableName;

import java.math.BigDecimal;
import java.util.Date;

@MapTableName("34_39848all34")
public class TableUpdateAll {

    @MapPrimaryKey
    public int id;

    @MapColumn
    public String name1;

    @MapColumn
    public String name2;
    @MapColumn
    public int integer1;
    @MapColumn
    public Integer integer2;

    @MapColumn
    public double double1;
    @MapColumn
    public Double double2;
    @MapColumn
    public float float1;
    @MapColumn
    public Float float2;
    @MapColumn
    public long long1;
    @MapColumn
    public Long long2;
    @MapColumn
    public int[] ints;
    @MapColumn
    public float[] floats1;
    @MapColumn
    public Float[] floats2;
    @MapColumn
    public double[] doubles1;
    @MapColumn
    public Double[] doubles2;
    @MapColumn
    public boolean boolean1;
    @MapColumn
    public Boolean boolean2;
    @MapColumn
    public boolean[] booleans1;
    @MapColumn
    public Boolean[] booleans2;


    @MapColumn
    public int[] integers1;
    @MapColumn
    public Integer[] integers2;

    @MapColumn
    public Date date1;
    @MapColumn
    public Date date2;
    @MapColumn
    public String[] strings;
    @MapColumn
    public BigDecimal bigDecimal1;
    @MapColumn
    public BigDecimal bigDecimal2;

}
