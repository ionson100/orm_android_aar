package com.bitnic.bitnicorm;


/**
 * If the object type extends this interface, you can listen for insert and update and delete events; these events are not triggered during batch inserts.
 * <pre>
 * {@code
 *@MapTableName("23-34")
 * public class TableActionOrm implements IEventOrm {
 *
 *     @MapPrimaryKey
 *     public UUID id=UUID.randomUUID();
 *
 *     @MapColumn
 *     public String name;
 *
 *
 *     @Override
 *     public void beforeUpdate() {
 *     }
 *
 *     @Override
 *     public void afterUpdate() {
 *     }
 *
 *     @Override
 *     public void beforeInsert() {
 *     }
 *
 *     @Override
 *     public void afterInsert() {
 *     }
 *
 *     @Override
 *     public void beforeDelete() {
 *     }
 *
 *     @Override
 *     public void afterDelete() {
 *     }
 * }
 * }
 * </pre>
 * @see <a href="https://github.com/ionson100/orm_android_aar">Home Page</a>
 */
public  interface IEventOrm {

    /**
     * Before update.
     * You can check something here, and if you are not satisfied with it, throw an exception.
     */
    void beforeUpdate();


    /**
     * After update.
     * You can check something here, and if you are not satisfied with it, throw an exception.
     */
    void afterUpdate();


    /**
     * Before insert.
     * You can check something here, and if you are not satisfied with it, throw an exception.
     */
    void beforeInsert();


    /**
     * After insert.
     * You can check something here, and if you are not satisfied with it, throw an exception.
     */
    void afterInsert();


    /**
     * Before delete.
     * You can check something here, and if you are not satisfied with it, throw an exception.
     */
    void beforeDelete();


    /**
     * After delete.
     * You can check something here, and if you are not satisfied with it, throw an exception.
     */
    void afterDelete();
}

