package com.bitnic.bitnicorm;

import android.content.ContentValues;

import java.lang.reflect.Field;
import java.util.Date;

class UtilsContentValues {

    private UtilsContentValues(){}

    public static Object checkFieldValue(Field field, Object item) {
        try {
            Object sd = field.get(item);
            if (sd == null) {
                throw new RuntimeException("Field name: " + field.getName() +
                        "object type: " + item.getClass().getName() + " is null");
            }
            return sd;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static <T> void initContentValues(T item ,CacheMetaData<T> d,ContentValues contentValues){

            innerIntContentValues(item, d,contentValues);

    }

    private static <T> void innerIntContentValues(Object item, CacheMetaData<T> d, ContentValues values) {

        try {
            if (d.keyColumn.isAssigned) {
                Field field = d.keyColumn.field;
                var p = checkFieldValue(field, item);
                values.put(d.keyColumn.columnName, p.toString());
            }
            for (ItemField str : d.listColumn) {
                if (str.notInsert) {
                    continue;
                }
                Field field = str.field;
                field.setAccessible(true);
                Object value = field.get(item);

                extractedSwitch(values, str, value);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void extractedSwitch(ContentValues values, ItemField str, Object value) {
      {

            if (value == null) {
                values.putNull(str.columnName);
                return;
            }
            switch (str.typeName) {

                case "userType":{
                    String json = ((IUserType) value).getString();
                    values.put(str.columnName, json);
                }

                case "UUID":
                case "BigDecimal": {
                    values.put(str.columnName, value.toString());
                    return;
                }
                case "Date": {
                    values.put(str.columnName, UtilsHelper.dateToStringForSQLite((Date) value));
                    return;
                }
                case "String": {
                    values.put(str.columnName, (String) value);
                    return;
                }
                case "int": {
                    values.put(str.columnName, (int) value);
                    return;
                }
                case "long": {
                    values.put(str.columnName, (long) value);
                    return;
                }
                case "short": {
                    values.put(str.columnName, (short) value);
                    return;
                }
                case "Byte":
                case "byte": {
                    if(value.getClass()==byte.class||value.getClass()==Byte.class){
                        values.put(str.columnName, (byte) value);
                    }else{
                        Integer myInt = new Integer((int) value);
                        values.put(str.columnName, myInt.byteValue());
                    }


                    return;
                }
                case "float": {

                    values.put(str.columnName, (float) value);
                    return;
                }
                case "Short": {
                    values.put(str.columnName, (Short) value);
                    return;
                }
                case "Long": {
                    values.put(str.columnName, (Long) value);
                    return;
                }
//                case "Byte": {
//                    if(value.getClass()==byte.class||value.getClass()==Byte.class){
//                        values.put(str.columnName, (byte) value);
//                    }else{
//                        Integer myInt = new Integer((int) value);
//                        values.put(str.columnName, myInt.byteValue());
//                    }
//                }
                case "Integer": {
                    values.put(str.columnName, new Integer((int) value));
                    return;
                }
                case "Double": {
                    values.put(str.columnName, (Double) value);
                    return;
                }
                case "Float": {
                    values.put(str.columnName, (Float) value);
                    return;
                }
                case "boolean": {

                    boolean val = (boolean) value;
                    values.put(str.columnName, val);
                    return;
                }
                case "Boolean": {
                    Boolean val = (Boolean) value;
                    values.put(str.columnName, val);
                    return;
                }
                case "double": {
                    values.put(str.columnName, (double) value);
                    return;
                }



                case "[SB": {
                   byte[] b= UtilsHelper.serializeByte(value);
                   values.put(str.columnName,b);
                    return;
                }
                case "[SJ": {
                    String json= UtilsHelper.serializeJson(value);
                    values.put(str.columnName,json);
                    return;
                }
                default: {
                    byte[] b= UtilsHelper.serializeByte(value);
                    values.put(str.columnName,b);
                    return;
                    //throw new RuntimeException("Не могу определить тип для вставки не известный: " + str.typeName);
                }
            }
        }
    }


}
