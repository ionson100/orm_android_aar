package com.bitnic.bitnicorm;
/********************************************************************
 * Copyright © 2016-2017 OOO Bitnic                                 *
 * Created by OOO Bitnic on 08.02.16   corp@bitnic.ru               *
 * ******************************************************************/


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


class AnnotationOrm {
    public static String getTableName(Class aClass) {
        Temp t = new Temp();
        getTableNameInner(aClass, t);
        return t.name;
    }

    public static String getWhere(Class aClass) {
        Temp t = new Temp();
        getWhereInner(aClass, t);
        return t.where;
    }

    public static String getAppendCommand(Class aClass) {
        Temp t = new Temp();
        getAppendCommandCreateTable(aClass, t);
        return t.append;
    }
    private static void getAppendCommandCreateTable(Class aClass,Temp t){
        if (aClass == null) return ;
        try {
            if (aClass.isAnnotationPresent(MapAppendCommandCreateTable.class)) {
                if(t.append==null){
                    t.append="";
                }
                t.append =t.append+ ((MapAppendCommandCreateTable) Objects.requireNonNull(aClass.getAnnotation(MapAppendCommandCreateTable.class))).value()+System.lineSeparator();
            }
            Class superclass = aClass.getSuperclass();
            getAppendCommandCreateTable(superclass, t);
        } catch (Exception e) {

            throw new RuntimeException(e);

        }
    }

    private static void getWhereInner(Class aClass, Temp t) {
        if (aClass == null) return;
        try {
            if (aClass.isAnnotationPresent(MapWhere.class)) {
                t.where = ((MapWhere) Objects.requireNonNull(aClass.getAnnotation(MapWhere.class))).value();
            } else {
                Class superClazz = aClass.getSuperclass();
                getWhereInner(superClazz, t);
            }
        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }


    public static ItemField getKeyColumnItemField(Class aClass) {

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

    public static List<ItemFieldFree> getListItemFieldColumnFree(Class aClass) {


        List<ItemFieldFree> list = new ArrayList<>();
        for (Field f : getAllFields(aClass)) {
            ItemFieldFree itemFieldFree=new ItemFieldFree();
            itemFieldFree.columnName=f.getName();
            itemFieldFree.columnNameRaw=Utils.clearStringTrimRaw(f.getName());
            itemFieldFree.field=f;
            itemFieldFree.type=f.getType();
            itemFieldFree.typeName=Utils.getTypeName(f);
            list.add(itemFieldFree);
        }
        return list;
    }

    public static List<ItemField> getListItemFieldColumn(Class aClass) {


        List<ItemField> list = new ArrayList<>();
        for (Field f : getAllFields(aClass)) {
            if (f.isAnnotationPresent(MapColumnName.class)||f.isAnnotationPresent(MapColumn.class)) {
                boolean userField = IUserType.class.isAssignableFrom(f.getType());
//                boolean serializableJson =ISerializableJson.class.isAssignableFrom(f.getType());
                final MapColumnName column = f.getAnnotation(MapColumnName.class);
                final MapColumn columnReal = f.getAnnotation(MapColumn.class);
                final MapIndex mapIndex=f.getAnnotation(MapIndex.class);
                final MapJsonColumn jsonSerialise=f.getAnnotation(MapJsonColumn.class);

                final MapColumnType columnType = f.getAnnotation(MapColumnType.class);
                final MapForeignKey foreignKey = f.getAnnotation(MapForeignKey.class);
                final MapColumnReadOnly notInsert = f.getAnnotation(MapColumnReadOnly.class);

                //final MapSerializableByte serializableByte = f.getAnnotation(MapSerializableByte.class);
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
                if(fi.fieldName=="aBytes22"){
                    int r=0;
                }
                fi.type = f.getType();
                fi.typeName=Utils.getTypeName(f);
                if (userField) {

                    fi.typeName="userType";
                    fi.isUserType = true;
                    fi.aClassUserType = f.getType();
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

    private static List<Field> getAllFields(Class clazz) {
        List<Field> fields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
        Class superClazz = clazz.getSuperclass();
        if (superClazz != null) {
            fields.addAll(getAllFields(superClazz));
        }
        return fields;
    }

    static void getTableNameInner(Class aClass, Temp table) {

        try {
            if (aClass.isAnnotationPresent(MapTableName.class)||aClass.isAnnotationPresent(MapTable.class)) {
                final MapTableName fUser=(MapTableName)aClass.getAnnotation(MapTableName.class);
                //final MapTable fReal=(MapTable) aClass.getAnnotation(MapTable.class);
                if(fUser!=null){
                    table.name =fUser.value();
                }else{
                    var name=aClass.getName();
                    var index=name.lastIndexOf(".");
                    if(index!=-1){
                        table.name=name.substring(index+1);
                    }else{
                        table.name=name;
                    }

                }

            } else {
                Class superClass = aClass.getSuperclass();
                getTableNameInner(superClass, table);
            }
        } catch (Exception e) {
            String msg="Отсутствует аннотация названия таблицы для типа: "+ aClass.getName()+System.lineSeparator()+e.getMessage();

            throw new RuntimeException(msg);
        }

    }

    static class Temp {
        public String name;
        public String where;
        public String append;
    }

}
