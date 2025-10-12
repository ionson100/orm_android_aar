package com.bitnic.bitnicorm.tableinherit;

import com.bitnic.bitnicorm.MapPrimaryKey;

import java.util.UUID;

public class TBaseClass {

    @MapPrimaryKey
    public UUID id=UUID.randomUUID();
}
