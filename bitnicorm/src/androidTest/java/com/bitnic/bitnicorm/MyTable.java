package com.bitnic.bitnicorm;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

@MapAppendCommandCreateTable("CREATE INDEX IF NOT EXISTS test_inte ON 'test' ('inte');")

//@MapTableWhere("name not null")
public class MyTable extends BaseTable implements IEventOrm {

    public MyTable(){}


    public MyTable(String name,long longs,int  inte,short aShort,byte aByte,BigDecimal bigDecimal,double aDouble,float  aFloat){
        this.name=name;
        this.longs = longs;
        this.inte = inte;
        this.aShort = aShort;
        this.aByte = aByte;
        this.bigDecimal = bigDecimal;
        this.aDouble = aDouble;
        this.aFloat = aFloat;
        this.aDoublem=0.6d;
    }

    @MapColumnName("name")
    @MapForeignKey("FOREIGN KEY (name) REFERENCES test (name) ")
    public  String name;


    @MapColumnIndex
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
    @MapForeignKey("FOREIGN KEY (aByte) REFERENCES test (aByte)")
    public double aDoublem=0.6;

    @MapColumnName("float_1")
    public float aFloat=5.89F;

    @MapColumnName("data")
    public Date myData= Calendar.getInstance().getTime();




    @Override
    public void beforeUpdate() {

    }

    @Override
    public void afterUpdate() {

    }

    @Override
    public void beforeInsert() {

    }

    @Override
    public void afterInsert() {

    }

    @Override
    public void beforeDelete() {

    }

    @Override
    public void afterDelete() {

    }
}
