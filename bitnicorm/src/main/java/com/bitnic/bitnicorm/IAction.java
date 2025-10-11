package com.bitnic.bitnicorm;
/********************************************************************
 * Copyright © 2016-2017 OOO Bitnic                                 *
 * Created by OOO Bitnic on 08.02.16   corp@bitnic.ru               *
 * ******************************************************************/

/**
 * The interface Function callback.
 *
 * @param <T> the type parameter
 */
public  interface IAction<T> {
    /**
     * Action.
     *
     * @param o the {@link Object}
     */
    void invoke(T o);

}
