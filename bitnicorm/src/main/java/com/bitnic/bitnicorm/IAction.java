package com.bitnic.bitnicorm;


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
