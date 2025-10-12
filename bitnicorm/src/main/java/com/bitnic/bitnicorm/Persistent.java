/********************************************************************
 * Copyright © 2016-2017 OOO Bitnic                                 *
 * Created by OOO Bitnic on 08.02.16   corp@bitnic.ru               *
 * ******************************************************************/

package com.bitnic.bitnicorm;

/**
 *If a DTO class inherits this class, the ORM can record where the object was obtained from, whether it was created on the client or retrieved from the database, and you can use the save method.
 * You can always know where the object was obtained from.
 */
public class Persistent {

    /**
     * Default constructor
     */
    public Persistent(){}
    /**
     * true - an object obtained from the database
     * false - the object was created on the client and was not saved in the database
     */
    boolean isPersistent;
}
