package com.bitnic.bitnicorm;

import java.util.HashMap;
import java.util.Map;


/**
 * A proxy class through which data is transferred for database updates, in the format: table column name - column value
 */
public class PairColumnValue {

    /**
     * Column type, default value
     */
    public PairColumnValue(){}


    final Map<String,Object> objectMap=new HashMap<>();

    /**
     * Insert pair column name -object value
     * @param columnName table column name
     * @param newValue new value
     * @return this PairColumnValue
     *
     * <pre>
     * {@code
     * session.updateRows(
     *    TableOcean.class,
     *    new PairColumnValue()
     *       .put("name","table11")
     *       .put("shoal",120),
     *    "1=?",
     *    1);
     * }
     * </pre>
     */
    public PairColumnValue put(String columnName, Object newValue){
        if(objectMap.containsValue(columnName)){
            throw new RuntimeException("The column name is already entered into the dictionary.");
        }
        objectMap.put(columnName,newValue);
        return this;
    }



}

