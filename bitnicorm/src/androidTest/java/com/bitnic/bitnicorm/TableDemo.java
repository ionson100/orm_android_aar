package com.bitnic.bitnicorm;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@MapTableName("table_demo")
public class TableDemo {
    @MapPrimaryKey
    public UUID id=UUID.randomUUID();


    @MapColumn
    public String string="name";
    @MapColumn
    public int anInt=1;
    @MapColumn
    public Integer bInt=1;
    @MapColumn
    public byte aByte=1;
    @MapColumn
    public Byte bByte=122;
    @MapColumn
    public float aFloat=1F;
    @MapColumn
    public Float bFloat=1F;
    @MapColumn
    public double aDouble=1D;
    @MapColumn
    public Double bDouble=1D;
    @MapColumn
    public long aLong=1L;
    @MapColumn
    public Long bLong=1L;
    @MapColumn
    public short aShort=1;
    @MapColumn
    public Short bShort=1;
    @MapColumn
    public boolean aBoolean=true;
    @MapColumn
    public Boolean bBoolean=true;
    @MapColumn
    public BigDecimal bigDecimal=new BigDecimal("111111111");
    @MapColumn
    public MyLevel enumLevel=MyLevel.HIGH;
    @MapColumn
    public Object[] objects=new Object[]{1,null};
    @MapColumn
    public UUID uuid=UUID.randomUUID();
    @MapColumn
    public List<String> strings=List.of("Яблоко", "Банан", "Вишня", "Финик");
    @MapColumn
    public DemoByte demoByte=new DemoByte();
    @MapColumn
    @MapColumnJson
    public DemoJson demoJson=new DemoJson();
    @MapColumn
    public DemoExternalizable externalizable=new DemoExternalizable();






}
