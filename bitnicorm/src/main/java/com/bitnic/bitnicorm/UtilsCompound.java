package com.bitnic.bitnicorm;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Build;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

class UtilsCompound {

    private UtilsCompound(){}

    @SuppressLint("Range")
    public static void Compound(List<ItemField> listIf, ItemField key, Cursor cursor, Object o) {
        for (ItemField str : listIf) {
            int i = cursor.getColumnIndex(str.columnNameRaw);
            if (i == -1) continue;
            Field res = str.field;
            res.setAccessible(true);


            extractedSwitch(cursor, o, str, res, i);
        }
        try {
            Field field = key.field;
            int index = cursor.getColumnIndex(key.columnNameRaw);
            field.setAccessible(true);
            switch (key.typeName){
                case "UUID":{
                    String uuid = cursor.getString(index);
                    field.set(o, UUID.fromString(uuid));
                    break;
                }
                case "int":{
                    field.set(o, cursor.getInt(index));
                    break;
                }
                case "long":{
                    field.set(o, cursor.getLong(index));
                    break;
                }
                case "float":{
                    field.set(o, cursor.getFloat(index));
                    break;
                }
                case "String":{
                    field.set(o, cursor.getString(index));
                    break;
                }
                default:{
                    throw new RuntimeException(" Не могу вставить первичный ключ: " + key.type);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


     static void extractedSwitch(Cursor cursor, Object o, ItemFieldBase fieldBase, Field field, int i)  {
        try {

            switch (fieldBase.typeName) {

                case "userType":{
                    IUserType data;
                    try {
                        data = (IUserType) fieldBase.aClassUserType.newInstance();
                        String sd = cursor.getString(i);
                        data.initBody(sd);
                        field.set(o, data);
                        return;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                case "int": {
                    if(cursor.isNull(i)){
                        field.setInt(o, 0);
                        return;
                    }
                    field.setInt(o, cursor.getInt(i));
                    return;
                }
                case "Date": {
                    if(cursor.isNull(i)){
                        field.set(o, null);
                        return;
                    }
                    var f = cursor.getString(i);
                    var d = UtilsHelper.stringToDate(f);
                    //2025-09-30 23:16:10
                    field.set(o, d);
                    return;
                }
                case "LocalDateTime": {
                    if(cursor.isNull(i)){
                        field.set(o, null);
                        return;
                    }
                    var f = cursor.getString(i);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        field.set(o, LocalDateTime.parse(f));
                    }
                    return;
                }
                case "UUID":{
                    if(cursor.isNull(i)){
                        field.set(o, null);
                        return;
                    }
                    field.set(o, UUID.fromString(cursor.getString(i)));
                    return;
                }
                case "BigDecimal": {
                    if(cursor.isNull(i)){
                        field.set(o, null);
                        return;
                    }
                    field.set(o, new BigDecimal(cursor.getString(i)));
                    return;
                }
                case "UUId":
                case "String": {

                    field.set(o, cursor.getString(i));
                    return;
                }
                case "double": {
                    if(cursor.isNull(i)){
                        field.setDouble(o, 0D);
                        return;
                    }
                    field.setDouble(o, cursor.getDouble(i));
                    return;
                }
                case "float": {
                    if(cursor.isNull(i)){
                        field.setFloat(o, 0F);
                        return;
                    }
                    field.setFloat(o, cursor.getFloat(i));
                    return;
                }
                case "long": {
                    if(cursor.isNull(i)){
                        field.setLong(o, 0L);
                        return;
                    }
                    long dd = cursor.getLong(i);
                    field.setLong(o, dd);
                    return;
                }
                case "short": {
                    if(cursor.isNull(i)){
                        field.setShort(o, (short) 0);
                        return;
                    }
                    field.setShort(o, cursor.getShort(i));
                    return;
                }




                case "byte": {
                    if(cursor.isNull(i)){
                        field.setByte(o, (byte) 0);
                        return;
                    }
                    Integer myInt = new Integer(cursor.getInt(i));
                    field.setByte(o, myInt.byteValue());
                    return;
                }
                case "Integer": {
                    if (cursor.isNull(i)) {
                        field.set(o, null);
                    } else {
                        Integer ii = cursor.getInt(i);
                        field.set(o, ii);
                    }
                    return;
                }
                case "Double": {
                    if (cursor.isNull(i)) {
                        field.set(o, null);
                    } else {
                        Double d = cursor.getDouble(i);
                        field.set(o, d);
                    }
                    return;
                }
                case "Float":{
                    if (cursor.isNull(i)) {
                        field.set(o, null);
                    } else {
                        Float f = cursor.getFloat(i);
                        field.set(o, f);
                    }
                    return;
                }
                case "Long":{
                    if (cursor.isNull(i)) {
                        field.set(o, null);
                    } else {
                        Long l = cursor.getLong(i);
                        field.set(o, l);
                    }
                    return;
                }
                case "Short":{
                    if (cursor.isNull(i)) {
                        field.set(o, null);
                    } else {
                        Short sh = cursor.getShort(i);
                        field.set(o, sh);
                    }
                    return;
                }
                case "boolean":{
                    if(cursor.isNull(i)){
                        field.setBoolean(o, false);
                        return;
                    }
                    boolean val;
                    val = cursor.getInt(i) != 0;
                    field.setBoolean(o, val);
                    return;
                }
                case "Boolean":{
                    if (cursor.isNull(i)) {
                        field.set(o, null);
                    } else {
                        boolean val;
                        val = cursor.getInt(i) != 0;
                        field.set(o, val);
                    }
                    return;
                }

                case "Byte":{
                    if (cursor.isNull(i)) {
                        field.set(o, null);
                    } else {
                        Integer myInt = new Integer(cursor.getInt(i));
                        field.set(o, myInt.byteValue());
                    }
                    return;
                }




                case "[SB":{
                    if(cursor.isNull(i)){
                        field.set(o, null);
                        return;
                    }else{
                        byte[] b = cursor.getBlob(i);
                        //Base64.decode(baseStr, Base64.DEFAULT)
                        Object res= UtilsHelper.deserializeByte(b);
                        field.set(o, res);
                        return;
                    }
                }
                case "[SJ":{
                    if(cursor.isNull(i)){
                        field.set(o, null);
                        return;
                    }else{
                        String json = cursor.getString(i);
                        Object res= UtilsHelper.deserializeJson(json,field.getType());
                        field.set(o, res);
                        return;
                    }
                }

                default:{
                    if(cursor.isNull(i)){
                        field.set(o, null);
                    }else{
                        byte[] b = cursor.getBlob(i);

                        Object res= UtilsHelper.deserializeByte(b);
                        field.set(o, res);

                    }
                }
            }
        }catch (Exception exception){
            throw new RuntimeException(exception);
        }

    }



    static void CompoundFree(List<ItemField> listIf, Cursor cursor, Object o) {
        for (ItemField str : listIf) {
            int i = cursor.getColumnIndex(str.columnNameRaw);
            if (i == -1) continue;
            Field res = str.field;
            res.setAccessible(true);
            extractedSwitch(cursor, o, str, res, i);

        }
    }
     static void builderInstance(CacheMetaData metaData,Cursor cursor, Object instance) throws Exception {
        if(metaData.isFreeClass){
            CompoundFree(metaData.listColumn,cursor,instance);
        }else{
            Compound(metaData.listColumn,metaData.keyColumn,cursor,instance);
        }
     }
    static Object extractedSwitchSelect(Cursor cursor, ItemField fieldBase, Field field, int i) throws Exception {

        if (cursor.isNull(i)) {

            return null;
        }
        switch (fieldBase.typeName) {

            case "userType": {
                IUserType data;
                try {
                    data = (IUserType) fieldBase.aClassUserType.newInstance();
                    String sd = cursor.getString(i);
                    data.initBody(sd);
                    return data;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            case "int":
            case "Integer": {
                return cursor.getInt(i);
            }
            case "Date": {
                String f = cursor.getString(i);
                return UtilsHelper.stringToDate(f);
            }
            case "LocalDateTime": {
                String f = cursor.getString(i);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    return LocalDateTime.parse(f);
                }
            }
            case "UUID": {
                return UUID.fromString(cursor.getString(i));
            }
            case "BigDecimal": {
                return new BigDecimal(cursor.getString(i));
            }
            case "UUId":
            case "String": {
                return cursor.getString(i);
            }
            case "double":
            case "Double": {
                return cursor.getDouble(i);
            }
            case "float":
            case "Float": {
                return  cursor.getFloat(i);
            }
            case "long":
            case "Long": {
                return cursor.getLong(i);
            }
            case "short":
            case "Short": {
                return cursor.getShort(i);
            }
            case "byte":
            case "Byte": {
                Integer myInt = new Integer(cursor.getInt(i));
                return myInt.byteValue();
            }
            case "boolean":
            case "Boolean": {
                return cursor.getInt(i) != 0;
            }
            case "[SJ": {
                String json = cursor.getString(i);
                return UtilsHelper.deserializeJson(json, field.getType());
            }
            case "[SB":
            default: {
                byte[] b = cursor.getBlob(i);
                return UtilsHelper.deserializeByte(b);
            }
        }
    }

}
