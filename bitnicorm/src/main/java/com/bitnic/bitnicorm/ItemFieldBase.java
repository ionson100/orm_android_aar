package com.bitnic.bitnicorm;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

 class ItemFieldBase {

    public Field field;
    public String columnName;
    public String columnNameRaw;
    public String fieldName;

    public Type type;

    public String typeName;
    public Class aClassUserType;
}
