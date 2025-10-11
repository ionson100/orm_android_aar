package com.bitnic.bitnicorm;

import java.util.UUID;

/**
 * The type Table insert update.
 */
@MapTableName("table34")
public class TableInsertUpdate {

    /**
     * The Uuid.
     */
    @MapPrimaryKey
    public String  uuid=UUID.randomUUID().toString();
    /**
     * The A int.
     */
//
    @MapColumn
    public Integer aInt;

    /**
     * The A short.
     */
    @MapColumn
    public Short aShort;

    /**
     * The A float.
     */
    @MapColumn
    public Float aFloat;

    /**
     * The A double.
     */
    @MapColumn
    public Double aDouble;


    /**
     * The A byte.
     */
    @MapColumn
    public Byte aByte;

    /**
     * The A long.
     */
//   @MapColumn
//    public byte[] aBytes;
//
    @MapColumn
    public Long aLong;
}
