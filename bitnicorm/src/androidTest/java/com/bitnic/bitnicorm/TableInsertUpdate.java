package com.bitnic.bitnicorm;

import java.util.UUID;

@MapTableName("table34")
public class TableInsertUpdate {

    @MapPrimaryKey
    public String  uuid=UUID.randomUUID().toString();
    //
    @MapColumn
    public Integer aInt;

    @MapColumn
    public Short aShort;

    @MapColumn
    public Float aFloat;

    @MapColumn
    public Double aDouble;


    @MapColumn
    public Byte aByte;

    //   @MapColumn
//    public byte[] aBytes;
//
    @MapColumn
    public Long aLong;
}
