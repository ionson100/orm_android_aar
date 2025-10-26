package com.bitnic.bitnicorm;


import java.lang.reflect.Field;

class ItemField extends ItemFieldBase {
    public boolean isIndex;
    public String columnType;
    public String foreignKey;
    public boolean isAssigned;// строковый тип
    public boolean notInsert=false;

}

