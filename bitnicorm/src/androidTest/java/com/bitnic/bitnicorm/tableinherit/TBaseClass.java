package com.bitnic.bitnicorm.tableinherit;

import com.bitnic.bitnicorm.MapPrimaryKey;

import java.util.UUID;

/**
 * The type T base class.
 */
public class TBaseClass {

    /**
     * The Id.
     */
    @MapPrimaryKey
    public UUID id=UUID.randomUUID();
}
