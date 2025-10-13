package com.bitnic.bitnicorm.bigdecimal;

import com.bitnic.bitnicorm.BaseTable;
import com.bitnic.bitnicorm.MapColumn;
import com.bitnic.bitnicorm.MapColumnName;
import com.bitnic.bitnicorm.MapTableName;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

@MapTableName("4203940249")
public class TableInset extends BaseTable {

    public TableInset(){}

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


    //@MapColumnIndex
    @MapColumnName("longs")
    public long longs;

    @MapColumnName("inte")
    public Integer inte;

    @MapColumnName("short")
    public short aShort;

    @MapColumnName("abyte")

    public byte aByte;

    @MapColumn
    public BigDecimal bigDecimal=new BigDecimal("121212121212");

    @MapColumnName("double")
    public Double aDouble=0.5D;

    @MapColumnName("doublem")

    public double aDoublem=0.6;

    @MapColumnName("float_1")
    public float aFloat=5.89F;

    @MapColumnName("data")
    public Date myData= Calendar.getInstance().getTime();



}
