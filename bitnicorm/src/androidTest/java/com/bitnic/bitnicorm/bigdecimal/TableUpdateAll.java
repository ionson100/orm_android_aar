package com.bitnic.bitnicorm.bigdecimal;

import com.bitnic.bitnicorm.MapColumn;
import com.bitnic.bitnicorm.MapPrimaryKey;
import com.bitnic.bitnicorm.MapTableName;

import java.math.BigDecimal;
import java.util.Date;

/**
 * The type Table update all.
 */
@MapTableName("34_39848all34")
public class TableUpdateAll {

    /**
     * The Id.
     */
    @MapPrimaryKey
    public int id;

    /**
     * The Name 1.
     */
    @MapColumn
    public String name1;

    /**
     * The Name 2.
     */
    @MapColumn
    public String name2;
    /**
     * The Integer 1.
     */
    @MapColumn
    public int integer1;
    /**
     * The Integer 2.
     */
    @MapColumn
    public Integer integer2;

    /**
     * The Double 1.
     */
    @MapColumn
    public double double1;
    /**
     * The Double 2.
     */
    @MapColumn
    public Double double2;
    /**
     * The Float 1.
     */
    @MapColumn
    public float float1;
    /**
     * The Float 2.
     */
    @MapColumn
    public Float float2;
    /**
     * The Long 1.
     */
    @MapColumn
    public long long1;
    /**
     * The Long 2.
     */
    @MapColumn
    public Long long2;
    /**
     * The Ints.
     */
    @MapColumn
    public int[] ints;
    /**
     * The Floats 1.
     */
    @MapColumn
    public float[] floats1;
    /**
     * The Floats 2.
     */
    @MapColumn
    public Float[] floats2;
    /**
     * The Doubles 1.
     */
    @MapColumn
    public double[] doubles1;
    /**
     * The Doubles 2.
     */
    @MapColumn
    public Double[] doubles2;
    /**
     * The Boolean 1.
     */
    @MapColumn
    public boolean boolean1;
    /**
     * The Boolean 2.
     */
    @MapColumn
    public Boolean boolean2;
    /**
     * The Booleans 1.
     */
    @MapColumn
    public boolean[] booleans1;
    /**
     * The Booleans 2.
     */
    @MapColumn
    public Boolean[] booleans2;


    /**
     * The Integers 1.
     */
    @MapColumn
    public int[] integers1;
    /**
     * The Integers 2.
     */
    @MapColumn
    public Integer[] integers2;

    /**
     * The Date 1.
     */
    @MapColumn
    public Date date1;
    /**
     * The Date 2.
     */
    @MapColumn
    public Date date2;
    /**
     * The Strings.
     */
    @MapColumn
    public String[] strings;
    /**
     * The Big decimal 1.
     */
    @MapColumn
    public BigDecimal bigDecimal1;
    /**
     * The Big decimal 2.
     */
    @MapColumn
    public BigDecimal bigDecimal2;

}
