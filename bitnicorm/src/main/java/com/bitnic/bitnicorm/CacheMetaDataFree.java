package com.bitnic.bitnicorm;
/********************************************************************
 * Copyright © 2016-2017 OOO Bitnic                                 *
 * Created by OOO Bitnic on 08.02.16   corp@bitnic.ru               *
 * ******************************************************************/

import java.util.List;

class CacheMetaDataFree<T> {
    public List<ItemFieldFree> listColumn;
    private String listSelectColumns;

    public CacheMetaDataFree(Class<T> aClass) {
        listColumn = AnnotationOrm.getListItemFieldColumnFree(aClass);
    }
    String getSelectColumns(){
        if(listSelectColumns==null){
            StringBuilder stringBuilder=new StringBuilder(listColumn.size());
            for (int i = 0; i < listColumn.size(); i++) {
                stringBuilder.append(listColumn.get(i).columnName);
                if(i<listColumn.size()-1){
                    stringBuilder.append(",");
                }
            }
            listSelectColumns=stringBuilder.toString();
        }
        return listSelectColumns;

    }
}
