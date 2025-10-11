package com.bitnic.bitnicorm;
/********************************************************************
 * Copyright © 2016-2017 OOO Bitnic                                 *
 * Created by OOO Bitnic on 08.02.16   corp@bitnic.ru               *
 * ******************************************************************/

import java.util.List;

class CacheMetaData<T> {

    public List<ItemField> listColumn = null;
    public ItemField keyColumn = null;
    String tableName = null;

    String tableNameRaw = null;
    String where = null;
    String appendCreateTable = null;
    boolean isIAction = false;
    private String[] listSelectColumns = null;


    public CacheMetaData(Class<T> aClass) {
        SetClass(aClass);
    }

    private void SetClass(Class tClass) {

        if (tableName == null) {
            tableName = Utils.clearStringTrim(AnnotationOrm.getTableName(tClass));
        }
        tableNameRaw=Utils.clearStringTrimRaw(tableName);

        if (where == null) {
            where = AnnotationOrm.getWhere(tClass);
        }
        if (appendCreateTable == null) {
            appendCreateTable = AnnotationOrm.getAppendCommand(tClass);
        }
        if (keyColumn == null) {
            keyColumn = AnnotationOrm.getKeyColumnItemField(tClass);
        }
        if (listColumn == null) {
            listColumn = AnnotationOrm.getListItemFieldColumn(tClass);
        }
        if (!isIAction) {

            for (Class aClass : tClass.getInterfaces()) {
                if (aClass == IEventOrm.class) {
                    isIAction = true;
                    break;
                }
            }
        }
        if (listSelectColumns == null) {
            listSelectColumns = new String[listColumn.size() + 1];
            for (int i = 0; i < listColumn.size(); i++) {
                listSelectColumns[i] = listColumn.get(i).columnName;
            }
            listSelectColumns[listColumn.size()] = keyColumn.columnName;
            listSelectColumns[0] = keyColumn.columnName;
            listSelectColumns[listSelectColumns.length - 1] = listColumn.get(0).columnName;
        }
    }
    public String[] getStringSelect() {
        return listSelectColumns;
    }
}
