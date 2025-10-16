package com.bitnic.bitnicorm;
/********************************************************************
 * Copyright © 2016-2017 OOO Bitnic                                 *
 * Created by OOO Bitnic on 08.02.16   corp@bitnic.ru               *
 * ******************************************************************/

import android.util.Log;

import java.util.List;
import java.util.Objects;

class CacheMetaData<T> {

    public List<ItemField> listColumn = null;
    public ItemField keyColumn = null;
    String tableName = null;
    String tableNameRaw = null;
    boolean isPersistent;
    String where = null;
    String appendCreateTable = null;
    boolean isIAction = false;
    List<String>  stringListColumnName;

    boolean isTableReadOnly;
    private String[] listSelectColumns = null;

    boolean isFreeClass;


    public List<String> getListColumnName(){
        return List.of(listSelectColumns);
    }





    public CacheMetaData(Class<T> aClass) {
        SetClass(aClass);
    }

    private void SetClass(Class<?> tClass) {


        isRecursiveSubclassOf(tClass);
        if(tableName!=null){
            if(tableName.isEmpty()){
                throw new RuntimeException("The class does not contain a table name annotation (@MapTableName or MapTable);" +
                        " such a class cannot be used in ORM.type:"+tClass.getName());
            }

            keyColumn = AnnotationOrm.getKeyColumnItemField(tClass);

            if(keyColumn==null||keyColumn.columnName==null||keyColumn.columnName.isEmpty()){
                throw new RuntimeException("There was a problem defining the primary key, or you did not specify a field with an annotation, " +
                        "or you tried to specify an empty value in it. type: "+tClass.getName());
            }
            listColumn = AnnotationOrm.getListItemFieldColumn(tClass);

            if(listColumn.isEmpty()){
                Log.w("---ORM WARNING---","Your class is missing fields to associate with table fields.(@MapColumn or @MapColumnName)  type:"+tClass.getName());
            }

            if (tClass.isAnnotationPresent(MapTableReadOnly.class)) {
                isTableReadOnly=true;
            }



            int count=listColumn.size() + 1;
            listSelectColumns = new String[count];

            for (int i = 0; i < listColumn.size(); i++) {
                listSelectColumns[i] = listColumn.get(i).columnName;
            }
            listSelectColumns[listColumn.size()] = keyColumn.columnName;
            listSelectColumns[0] = keyColumn.columnName;
            listSelectColumns[listSelectColumns.length - 1] = listColumn.get(0).columnName;

        }else {
            isFreeClass=true;
            isRecursiveSubclassOfFree(tClass);
            listColumn = AnnotationOrm.getListItemFieldColumnFreeNew(tClass);
            tableName =tClass.getName();
            var index=tableName.lastIndexOf(".");
            if(index!=-1){
                tableName=Utils.clearStringTrim(tableName.substring(index+1));
            }else{
                tableName=Utils.clearStringTrim(tableName);
            }
            listSelectColumns = new String[listColumn.size()];
            for (int i = 0; i < listColumn.size(); i++) {
                    listSelectColumns[i] = listColumn.get(i).columnName;
            }
        }

    }
    String getSelectColumns(){

            StringBuilder stringBuilder=new StringBuilder(listSelectColumns.length);
            for (int i = 0; i < listSelectColumns.length; i++) {
                stringBuilder.append(listSelectColumns[i]);
                if(i<listSelectColumns.length-1){
                    stringBuilder.append(", ");
                }
            }


        return stringBuilder.toString();

    }
    public String[] getStringSelect() {
        return listSelectColumns;

    }

    public  void isRecursiveSubclassOf( Class<?> parentClass) {

        String nameCore=parentClass.getName();
        Class<?> currentClass = parentClass;
        while (currentClass != null) {
            if (currentClass.equals(Persistent.class)) {
                isPersistent=true;
            }

            if ((currentClass.isAnnotationPresent(MapTableName.class)||currentClass.isAnnotationPresent(MapTable.class))&&tableName==null) {
                final MapTableName fName = currentClass.getAnnotation(MapTableName.class);
                if(fName !=null){
                    tableName =Utils.clearStringTrim(fName.value());
                }else{
                    var name=currentClass.getName();
                    var index=name.lastIndexOf(".");
                    if(index!=-1){
                        tableName=Utils.clearStringTrim(name.substring(index+1));
                    }else{
                        tableName=Utils.clearStringTrim(name);
                    }
                }
            }

            if (currentClass.isAnnotationPresent(MapTableWhere.class)&&where==null) {
                where = Objects.requireNonNull(currentClass.getAnnotation(MapTableWhere.class)).value();
            }

            if (currentClass.isAnnotationPresent(MapAppendCommandCreateTable.class)) {
                if(appendCreateTable==null){
                    appendCreateTable="";
                }
                appendCreateTable =appendCreateTable+
                        Objects.requireNonNull(currentClass.getAnnotation(MapAppendCommandCreateTable.class)).value()+System.lineSeparator();
            }
            if(!isIAction){
                isIAction=IEventOrm.class.isAssignableFrom(currentClass);
            }


            currentClass = currentClass.getSuperclass();
        }

    }

    public  void isRecursiveSubclassOfFree( Class<?> parentClass) {

        String nameCore=parentClass.getName();
        Class<?> currentClass = parentClass;
        while (currentClass != null) {
            if (currentClass.equals(Persistent.class)) {
                isPersistent=true;
            }

            if(!isIAction){
                isIAction=IEventOrm.class.isAssignableFrom(currentClass);
            }
            currentClass = currentClass.getSuperclass();
        }

    }
}
