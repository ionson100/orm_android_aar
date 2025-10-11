package com.bitnic.bitnicorm;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/**
 * The type My table.
 */
@MapAppendCommandCreateTable("CREATE INDEX IF NOT EXISTS test_inte ON 'test' ('inte');")

//@MapWhere("name not null")
public class MyTable extends BaseTable implements IEventOrm {

    /**
     * Instantiates a new My table.
     */
    public MyTable(){}


    /**
     * Instantiates a new My table.
     *
     * @param name       the name
     * @param longs      the longs
     * @param inte       the inte
     * @param aShort     the a short
     * @param aByte      the a byte
     * @param bigDecimal the big decimal
     * @param aDouble    the a double
     * @param aFloat     the a float
     */
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

    /**
     * The Name.
     */
    @MapColumnName("name")
    @MapForeignKey("FOREIGN KEY (name) REFERENCES test (name) ")
    public  String name;


    /**
     * The Longs.
     */
    @MapIndex
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
    @MapForeignKey("FOREIGN KEY (aByte) REFERENCES test (aByte)")
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
