package com.bitnic.bitnicorm;

import java.util.UUID;

/**
 * The type Table insert update 2.
 */
@MapTableName("table34")
public class TableInsertUpdate2 {

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
    public int aInt;

    /**
     * The A short.
     */
    @MapColumn
    public short aShort;

    /**
     * The A float.
     */
    @MapColumn
    public float aFloat;

    /**
     * The A double.
     */
    @MapColumn
    public double aDouble;


    /**
     * The A byte.
     */
    @MapColumn
    public byte aByte;

    /**
     * The A long.
     */
//   @MapColumn
//    public byte[] aBytes;
//
    @MapColumn
    public long aLong;
}
