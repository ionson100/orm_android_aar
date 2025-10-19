package com.bitnic.bitnicorm;
//./gradlew :bitnicorm:assembleRelease

import static com.bitnic.bitnicorm.UtilsHelper.bytesToHex;

import android.content.ContentValues;
import android.database.Cursor;

import androidx.annotation.NonNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Utils {

    private Utils(){}

    static String[] parametrize(Object... objects) {
        String[] str = null;
        if (objects.length > 0) {
            str = new String[objects.length];
            for (int i = 0; i < objects.length; i++) {
                if (objects[i] instanceof byte[]) {

                    String hexString = bytesToHex((byte[]) objects[i]);
                    String strCore = "0x" + hexString;
                    str[i] = strCore;
                } else if(objects[i] instanceof Date){
                    var s = UtilsHelper.dateToStringForSQLite((Date)objects[i]);
                    str[i] = s;
                }else {
                    var s = String.valueOf(objects[i]);
                    str[i] = s;
                }
            }
        }
        return str;
    }

    static <T> String whereBuilderRaw(String where, CacheMetaData<T> data) {

        if (where == null || where.trim().isEmpty()) {
            where = "";
        }
        if ((data.where == null || data.where.trim().isEmpty()) && where.isEmpty()) {
            return "";
        }
        if ((data.where == null || data.where.trim().isEmpty()) && !where.isEmpty()) {
            return "WHERE " + where;
        }

        String t = "";
        if (data.where != null) {
            t = data.where;
        }
        return " WHERE " + t + ((where.trim().isEmpty()) ? " " : " and " + where) + " ";


    }

    static <T> String whereBuilder(String where, CacheMetaData<T> data) {

        if (where == null || where.trim().isEmpty()) {
            where = "";
        }
        if ((data.where == null || data.where.trim().isEmpty()) && where.isEmpty()) {
            return "";
        }
        if ((data.where == null || data.where.trim().isEmpty()) && !where.isEmpty()) {
            return where;
        }
        String t = "";
        if (data.where != null) {
            t = data.where;
        }

        return " " + t + ((where.trim().isEmpty()) ? " " : " and " + where) + " ";


    }

    public static String getStringInsert(String tableName,ContentValues contentValues){
        if(Configure.IsWriteLog){
            StringBuilder stringBuilder=new StringBuilder();
            contentValues.valueSet().forEach(stringObjectEntry -> {
                stringBuilder.append(stringObjectEntry.getKey()).append(" = ").append(stringObjectEntry.getValue()).append(",").append(System.lineSeparator());
            });
            return "INSERT "+tableName+ " SET "+System.lineSeparator()+ stringBuilder;
        }
        return null;
    }

    public static String getStringUpdate(String tableName,ContentValues contentValues,String where){
        if(Configure.IsWriteLog){
            StringBuilder stringBuilder=new StringBuilder();
            contentValues.valueSet().forEach(stringObjectEntry -> {
                stringBuilder.append(stringObjectEntry.getKey()).append(" = ").append(stringObjectEntry.getValue()).append(",").append(System.lineSeparator());
            });
            return "UPDATE "+tableName+ " KEY-VALUE "+System.lineSeparator()+ stringBuilder+System.lineSeparator()+where;
        }
        return null;
    }
    private  static ItemField getItemField(List<ItemField> list,String name){
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i).columnNameRaw.equals(name)){
                return list.get(i);
            }
        }
        return null;
    }


    public static void builderSqlNew(CacheMetaData d, ContentValues contentValues, Map<String, Object> objectMap){
        objectMap.forEach((s, o) -> {
            var itemField=getItemField(d.listColumn,s);
            if(itemField==null){
                throw new RuntimeException("!!!Column with name: "+s+" not found in table:"+d.tableName);
            }
            if(!itemField.notInsert){
                UtilsContentValues.extractedSwitch(contentValues,itemField,o);
            }

        });
    }


    @NonNull
    public static List<String> getStringListSqlCreateTable(String ifNotExist, CacheMetaData<?> data) {
        StringBuilder sb = new StringBuilder("CREATE TABLE " + ifNotExist + " " + data.tableName + " (" + System.lineSeparator());

        StringBuilder foreignKey = new StringBuilder();

        sb.append(data.keyColumn.columnName).append(" ");
        sb.append(pizdaticusKey(data.keyColumn));
        sb.append(" PRIMARY KEY, ").append(System.lineSeparator());
        for (ItemField f : data.listColumn) {
            sb.append(f.columnName);
            sb.append(pizdaticusField(f));
            sb.append(System.lineSeparator());
            if (f.foreignKey != null) {
                foreignKey.append(",").append(f.foreignKey).append(System.lineSeparator());
            }
        }
        String s = sb.toString().trim();
        String ss = s.substring(0, s.length() - 1);
        var forkey = "";
        if (foreignKey.length() > 0) {
            forkey = forkey + System.lineSeparator() + foreignKey;
        }
        String sql = ss + forkey + "); ";
        sb.delete(0, sb.length() - 1);
        List<String> sqlList = new ArrayList<>();
        sqlList.add(sql);
        return sqlList;
    }
    public static <T> List<List<T>> partition(Collection<T> members) {
        List<List<T>> res = new ArrayList<>();
        List<T> internal = new ArrayList<>();
        for (T member : members) {
            internal.add(member);
            if (internal.size() == 500) {
                res.add(internal);
                internal = new ArrayList<>();
            }
        }
        if (!internal.isEmpty()) {
            res.add(internal);
        }
        return res;
    }
    public static byte[] ByteBigToByte(Byte[] bytes){
        byte[] res=new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            res[i]=bytes[i];
        }
        return res;
    }

    public static String pizdaticusKey(ItemField field) {

        switch (field.typeName) {
            case "float":
            case "Float":
            case "Double":
            case "double": {
                return " REAL ";
            }
            case "int":
            case "Integer":
            case "long":
            case "Long":
            case "short":
            case "Short":
            case "byte":
            case "Byte": {
                return " INTEGER ";
            }
            case "boolean":
            case "Boolean": {
                return " BOOL ";
            }
            default: {
                return " TEXT ";
            }

        }
    }

    public static String pizdaticusField(ItemField field) {

        if (field.columnType != null) {
            return " " + field.columnType + ", ";
        } else
            switch (field.typeName) {
                case "userType":
                case "[SJ":
                case "String":
                case "UUID":
                case "BigDecimal": {
                    return " TEXT, ";
                }
                case "float":

                case "double": {
                    return " REAL DEFAULT 0, ";
                }
                case "long":
                case "short":
                case "byte":
                case "int": {
                    return " INTEGER DEFAULT 0 , ";
                }

                case "Float":
                case "Double":{
                    return " REAL DEFAULT NULL, ";
                }

                case "Byte":
                case "Long":
                case "Short":
                case "Integer":{
                    return " INTEGER DEFAULT NULL , ";
                }
                case "boolean": {
                    return " BOOL DEFAULT 0, ";
                }

                case "Boolean":{
                    return " BOOL DEFAULT NULL, ";
                }



                case "[SB":// serializable blob
                case "Image": {

                    return " BLOB, ";

                }
                case "Date": {
                    return " DATETIME, ";
                }
                default: {
                    return " BLOB, ";
                    //throw new RuntimeException("Не известный для меня тип: " + field.typeName);
                }

            }


    }

    public static Object getObjectFromCursor(Cursor cursor,int index){
        switch (cursor.getType(index)) {
            case Cursor.FIELD_TYPE_NULL: {
                return null;
            }
            case Cursor.FIELD_TYPE_BLOB: {
                 return cursor.getBlob(index);
            }
            case Cursor.FIELD_TYPE_FLOAT: {
                return cursor.getFloat(index);
            }
            case Cursor.FIELD_TYPE_INTEGER: {
                return cursor.getInt(index);
            }
            case Cursor.FIELD_TYPE_STRING: {
                return cursor.getString(index);
            }
            default:
                throw new IllegalStateException("Unexpected value: " + cursor.getType(index));
        }
    }

    public static Object[] CursorToArray(Cursor cursor) {
        Object[] objects = new Object[cursor.getColumnCount()];
        for (int i = 0; i < cursor.getColumnCount(); i++) {
            switch (cursor.getType(i)) {
                case Cursor.FIELD_TYPE_NULL: {
                    objects[i] = null;
                    break;
                }
                case Cursor.FIELD_TYPE_BLOB: {
                    objects[i] = cursor.getBlob(i);
                    break;
                }
                case Cursor.FIELD_TYPE_FLOAT: {
                    objects[i] = cursor.getFloat(i);
                    break;
                }
                case Cursor.FIELD_TYPE_INTEGER: {
                    objects[i] = cursor.getInt(i);
                    break;
                }
                case Cursor.FIELD_TYPE_STRING: {
                    objects[i] = cursor.getString(i);
                    break;
                }
                default:
                    throw new IllegalStateException("Unexpected value: " + cursor.getType(i));
            }
        }
        return objects;
    }


    public static Map<String, Object> cursorToMap(Cursor cursor) {
        Map<String, Object> objectMap = new HashMap<>(cursor.getColumnCount());
        var court = cursor.getColumnCount();

        for (int i = 0; i < court; i++) {
            //var tt=cursor.getString(i);
            //var ttf=cursor.getColumnName(i);
            switch (cursor.getType(i)) {
                case Cursor.FIELD_TYPE_NULL: {
                    objectMap.put(cursor.getColumnName(i), null);
                    break;
                }
                case Cursor.FIELD_TYPE_BLOB: {
                    objectMap.put(cursor.getColumnName(i), cursor.getBlob(i));
                    break;
                }
                case Cursor.FIELD_TYPE_FLOAT: {
                    objectMap.put(cursor.getColumnName(i), cursor.getFloat(i));
                    break;
                }
                case Cursor.FIELD_TYPE_INTEGER: {
                    objectMap.put(cursor.getColumnName(i), cursor.getInt(i));
                    break;
                }
                case Cursor.FIELD_TYPE_STRING: {

                    objectMap.put(cursor.getColumnName(i), cursor.getString(i));
                    break;
                }
                default:
                    throw new IllegalStateException("Unexpected value: " + cursor.getType(i));
            }
        }
        return objectMap;
    }




    public static String trimStart(String str, Character... c) {
        if (str == null) return null;
        str = str.trim();
        if (str.isEmpty()) return str;

        for (int i = 0; i < str.length(); i++) {
            if (ContainsArray(c, str.charAt(i))) {
                return str.substring(i);
            }
        }
        return "";
    }

    public static String trimEnd(String str, Character... c) {
        if (str == null) return null;
        str = str.trim();
        if (str.isEmpty()) return str;
        StringBuilder builder = new StringBuilder();
        for (int i = str.length() - 1; i > 0; i--) {
            if (ContainsArray(c, str.charAt(i))) {
                builder.append(str.substring(0, i + 1));
                break;
            }
        }
        return builder.toString();
    }

    public static String trimAll(String str, Character... c) {
        String s = trimStart(str, c);
        return trimEnd(s, c);
    }

    private static <T> boolean ContainsArray(T[] t, T d) {
        for (T w : t) {

            if (w.equals(d)) {
                return false;
            }
        }
        return true;
    }

    public static String clearStringTrim(String str) {
        return "\"" + trimAll(str, ' ', '`', '[', ']', '\'') + "\"";
    }



    public static String clearStringTrimRaw(String str) {
        return trimAll(str, '"',' ', '`', '[', ']', '\'');
    }




    public static String getTypeName(Field f) {

        String res = f.getType().getName();
        switch (res) {
            case "java.util.List":
            case "java.util.Set":
            case "java.util.Map": {
                return "[SB";
            }


            default:{
                if(res.startsWith("[")){
                    return "[SB";
                }
                var index = res.lastIndexOf(".");
                if (index != -1) {
                    return res.substring(index + 1);
                } else {
                    return res;
                }
            }
        }


    }
}
