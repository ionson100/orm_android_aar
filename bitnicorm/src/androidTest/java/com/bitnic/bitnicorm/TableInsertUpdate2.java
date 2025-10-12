package com.bitnic.bitnicorm;

import java.util.UUID;

@MapTableName("table34")
public class TableInsertUpdate2 {

    @MapPrimaryKey
    public String  uuid=UUID.randomUUID().toString();
    //
    @MapColumn
    public int aInt;

    @MapColumn
    public short aShort;

    @MapColumn
    public float aFloat;

    @MapColumn
    public double aDouble;


    @MapColumn
    public byte aByte;

    //   @MapColumn
//    public byte[] aBytes;
//
    @MapColumn
    public long aLong;
}
