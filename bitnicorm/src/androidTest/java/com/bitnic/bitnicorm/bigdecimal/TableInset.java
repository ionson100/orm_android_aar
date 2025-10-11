package com.bitnic.bitnicorm.bigdecimal;

import com.bitnic.bitnicorm.BaseTable;
import com.bitnic.bitnicorm.MapColumn;
import com.bitnic.bitnicorm.MapColumnName;
import com.bitnic.bitnicorm.MapTableName;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/**
 * The type Table inset.
 */
@MapTableName("4203940249")
public class TableInset extends BaseTable {

    /**
     * Instantiates a new Table inset.
     */
    public TableInset(){}

    /**
     * The Name.
     */
//    public TableInset(String name, long longs, int  inte, short aShort, byte aByte, BigDecimal bigDecimal, double aDouble, float  aFloat){
//        this.name=name;
//        this.longs = longs;
//        this.inte = inte;
//        this.aShort = aShort;
//        this.aByte = aByte;
//        this.bigDecimal = bigDecimal;
//        this.aDouble = aDouble;
//        this.aFloat = aFloat;
//        this.aDoublem=0.6d;
//    }
    @MapColumnName("name")
    public  String name;


    /**
     * The Longs.
     */
//@MapIndex
    @MapColumnName("longs")
    public long longs;

    /**
     * The Inte.
     */
    @MapColumnName("inte")
    public Integer inte;

    /**
     * The A short.
     */
    @MapColumnName("short")
    public short aShort;

    /**
     * The A byte.
     */
    @MapColumnName("abyte")

    public byte aByte;

    /**
     * The Big decimal.
     */
    @MapColumn
    public BigDecimal bigDecimal=new BigDecimal("121212121212");

    /**
     * The A double.
     */
    @MapColumnName("double")
    public Double aDouble=0.5D;

    /**
     * The A doublem.
     */
    @MapColumnName("doublem")

    public double aDoublem=0.6;

    /**
     * The A float.
     */
    @MapColumnName("float_1")
    public float aFloat=5.89F;

    /**
     * The My data.
     */
    @MapColumnName("data")
    public Date myData= Calendar.getInstance().getTime();



}
