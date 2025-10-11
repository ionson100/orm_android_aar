package com.bitnic.bitnicorm.tableinherit;

import com.bitnic.bitnicorm.MapColumn;
import com.bitnic.bitnicorm.MapTableName;

/**
 * The type Table top.
 */
@MapTableName("t32")
public class TableTop extends TableMiddle {
    /**
     * Instantiates a new Table top.
     */
    public TableTop(){
        super();
    }

    /**
     * Instantiates a new Table top.
     *
     * @param name  the name
     * @param email the email
     * @param age   the age
     */
    public TableTop(String name,String email,int age){

        super(name,age);
        this.email=email;
    }

    /**
     * The Email.
     */
    @MapColumn
    public String email;
}
