/********************************************************************
 * Copyright © 2016-2017 OOO Bitnic                                 *
 * Created by OOO Bitnic on 08.02.16   corp@bitnic.ru               *
 * ******************************************************************/

package com.bitnic.bitnicorm;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


class AnnotationOrm {

    private AnnotationOrm(){}


    public static ItemField getKeyColumnItemField(Class<?> aClass) {

        ItemField res = null;
        List<Field> df = getAllFields(aClass);
        for (Field f : df) {
            if (f.isAnnotationPresent(MapPrimaryKeyName.class)||f.isAnnotationPresent(MapPrimaryKey.class)) {
                final MapPrimaryKeyName key = f.getAnnotation(MapPrimaryKeyName.class);
                final MapPrimaryKey keyReal = f.getAnnotation(MapPrimaryKey.class);
                res = new ItemField();
                res.type = f.getType();
                res.fieldName = f.getName();
                res.typeName=Utils.getTypeName(f);
                if(key!=null){
                    res.columnName =Utils.clearStringTrim(key.value());
                    res.columnNameRaw=Utils.clearStringTrimRaw(key.value());
                }else if(keyReal!=null){
                    res.columnName =Utils.clearStringTrim(f.getName());
                    res.columnNameRaw=Utils.clearStringTrimRaw(f.getName());
                }

                if(res.type==String.class||res.type== UUID.class){
                    res.isAssigned=true;
                }
                res.field = f;
                break;
            }
        }
        return res;
    }
    public static List<ItemField> getListItemFieldColumnFreeNew(Class<?> aClass) {

        List<ItemField> list = new ArrayList<>();
        for (Field f : getAllFields(aClass)) {
            if(f.getName().contains("$")) continue;
            ItemField itemFieldFree=new ItemField();
            itemFieldFree.columnName="\""+f.getName()+"\"";
            itemFieldFree.columnNameRaw=Utils.clearStringTrimRaw(f.getName());
            itemFieldFree.field=f;
            itemFieldFree.type=f.getType();
            itemFieldFree.typeName=Utils.getTypeName(f);
            list.add(itemFieldFree);
        }
        return list;
    }

    public static List<ItemField> getListItemFieldColumn(Class<?> aClass) {


        List<ItemField> list = new ArrayList<>();
        for (Field f : getAllFields(aClass)) {
            if (f.isAnnotationPresent(MapColumnName.class)||f.isAnnotationPresent(MapColumn.class)) {
                boolean userField = IUserType.class.isAssignableFrom(f.getType());
//                boolean serializableJson =ISerializableJson.class.isAssignableFrom(f.getType());
                final MapColumnName column = f.getAnnotation(MapColumnName.class);
                final MapColumn columnReal = f.getAnnotation(MapColumn.class);
                final MapColumnIndex mapIndex=f.getAnnotation(MapColumnIndex.class);
                final MapColumnJson jsonSerialise=f.getAnnotation(MapColumnJson.class);
                final MapColumnType columnType = f.getAnnotation(MapColumnType.class);
                final MapForeignKey foreignKey = f.getAnnotation(MapForeignKey.class);
                final MapColumnReadOnly notInsert = f.getAnnotation(MapColumnReadOnly.class);

                ItemField fi = new ItemField();
                if(mapIndex!=null){
                    fi.isIndex=true;
                }

                if(column!=null){
                    fi.columnName = Utils.clearStringTrim(column.value());
                    fi.columnNameRaw= Utils.clearStringTrimRaw(column.value());
                }else if(columnReal!=null){
                        fi.columnName = Utils.clearStringTrim(f.getName());
                        fi.columnNameRaw= Utils.clearStringTrimRaw(f.getName());
                }else{
                    throw new RuntimeException("Field : "+f.getName()+"object type: "+aClass.getName()+" does not have a table column name annotation");
                }

                if(foreignKey!=null){
                    fi.foreignKey=foreignKey.value();
                }

                fi.fieldName = f.getName();

                fi.type = f.getType();

                fi.typeName=Utils.getTypeName(f);

                if (userField) {
                    fi.typeName="userType";
                }

                if(jsonSerialise!=null){
                    fi.typeName="[SJ";
                }

                if(notInsert!=null){
                    fi.notInsert=true;
                }

                if(columnType!=null){
                    fi.columnType=columnType.value();
                }

                list.add(fi);

                fi.field = f;
            }
        }
        return list;
    }

    private static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
        Class<?> superClazz = clazz.getSuperclass();
        if (superClazz != null) {
            fields.addAll(getAllFields(superClazz));
        }
        return fields;
    }


}
