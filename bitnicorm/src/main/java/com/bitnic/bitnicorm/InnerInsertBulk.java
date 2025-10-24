package com.bitnic.bitnicorm;


import static com.bitnic.bitnicorm.CacheDictionary.getCacheMetaData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class InnerInsertBulk<F> {
    final private StringBuilder sql = new StringBuilder();
    final private List<Object> objectList = new ArrayList<>();
    private int it = 0;
    private final CacheMetaData metaData;

    InnerInsertBulk( CacheMetaData data) {
        metaData = data;
        sql.append(" INSERT INTO ");
        sql.append(metaData.tableName).append(" (");
        List<ItemField> itemFields = new ArrayList<>(metaData.listColumn);
        if (metaData.keyColumn.isAssigned) {
            itemFields.add(0, metaData.keyColumn);
        }
        for (int i = 0; i < itemFields.size(); i++) {
            ItemField f = itemFields.get(i);
            if (f.notInsert) {
                sql.append("");
            } else {
                sql.append(f.columnName);
                sql.append(", ");
            }

        }
        int start = sql.lastIndexOf(",");
        sql.delete(start, sql.length());
        sql.append(") VALUES ").append(System.lineSeparator());

    }


    public void add(F o) {
        it++;
        sql.append("(");
        List<ItemField> itemFields = new ArrayList<>(metaData.listColumn);
        if (metaData.keyColumn.isAssigned) {
            itemFields.add(0, metaData.keyColumn);
        }
        for (int i = 0; i < itemFields.size(); i++) {
            ItemField f = itemFields.get(i);
            f.field.setAccessible(true);

            try {
                if (f.notInsert) {
                    sql.append("");
                } else {
                    Object value = f.field.get(o);
                    var v = getString(value,  f,objectList);
                    sql.append(v);
                    sql.append(", ");
                }


            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        int start = sql.lastIndexOf(",");
        sql.delete(start, sql.length());
        sql.append(") ,").append(System.lineSeparator());
    }

    String getSql() {
        if (it == 0) {
            return null;
        } else {
            return sql.toString().substring(0, sql.toString().lastIndexOf(",")).trim();
        }

    }

    List<Object> getParamsObjectList() {

        return objectList;
    }

     static String getString(Object o,  ItemField field, List<Object> objectList) {

        String typeName =field.typeName;
        field.field.setAccessible(true);

        switch (typeName) {
            case "userType":{
                return  "'"+((IUserType)o).getString()+"'";
            }
            case "LocalDateTime":{
                if (o == null) {
                    return "null";// Utils.dateToStringForSQLite(new Date(0));
                } else {
                    objectList.add(o);
                    return "?";
                }
            }
            case "Date": {
                if (o == null) {
                    return "null";// Utils.dateToStringForSQLite(new Date(0));
                } else {
                    //long ld= ((Date) o).getTime();
                    return "'" + UtilsHelper.dateToStringForSQLite((Date) o) + "'";//String.valueOf(ld);
                }
            }

            case "BigDecimal": {
                if (o == null) {
                    return "null";
                } else {
                    return o.toString();
                }
            }
            case "UUID": {
                if (o == null) {
                    return "null";
                } else {
                    return String.format("'%s'", o);
                }

            }
            case "String": {
                if (o == null) {
                    return "null";
                } else {
                    objectList.add(o);
                    return "?";
                }
            }
            case "boolean": {
                if (o == null) {
                    throw new RuntimeException("Поле: "+field.columnName+" не может быть null");
                } else {
                    if ((Boolean) o) {
                        return "1";
                    } else {
                        return "0";
                    }
                }
            }
            case "Boolean": {
                if (o == null) {
                    return "null";
                } else {
                    if ((Boolean) o) {
                        return "1";
                    } else {
                        return "0";
                    }
                }
            }
            case "byte":
            case "int":
            case "long":
            case "float":
            case "double":
            case "short": {
                if (o == null) {
                    throw new RuntimeException("Поле: "+field.columnName+" не может быть null");
                } else {
                    return String.valueOf(o);
                }
            }
            case "Byte":
            case "Integer":
            case "Float":
            case "Double":
            case "Long":
            case "Short": {
                if (o == null) {
                    return "null";
                } else {
                    return String.valueOf(o);
                }
            }
//            case "Enum":{
//                if (o == null) {
//                    return "null";
//                } else {
//                    int v=((Enum)o).ordinal();
//                    return String.valueOf(v);
//                }
//            }

            case "[SB":{
                if (o == null) {
                    return "null";
                } else {
                    byte[] s= UtilsHelper.serializeByte(o);
                    objectList.add(s);
                    return "?";
                }
            }
            case "[SJ":{
                if (o == null) {
                    return "null";
                } else {
                    String s= UtilsHelper.serializeJson(o);
                    objectList.add(s);
                    return  "?";
                }
            }
            default:{
                if (o == null) {
                    return "null";
                } else {
                    byte[] s= UtilsHelper.serializeByte(o);
                    objectList.add(s);
                    return "?";
                }
                //throw new RuntimeException("InsertBulk:не могу определить тип: " +typeName);
            }
        }



    }
}
