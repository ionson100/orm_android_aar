package com.bitnic.bitnicorm;


/**
 * The interface Event orm.
 */
public  interface IEventOrm {

    /**
     * Before update.
     */
    void beforeUpdate();


    /**
     * After update.
     */
    void afterUpdate();


    /**
     * Before insert.
     */
    void beforeInsert();


    /**
     * After insert.
     */
    void afterInsert();


    /**
     * Before delete.
     */
    void beforeDelete();


    /**
     * After delete.
     */
    void afterDelete();
}

