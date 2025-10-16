package com.bitnic.bitnicorm;

import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * The type Utils helper.
 */
public class UtilsHelper {


    private UtilsHelper(){}

    /**
     * Bytes to HEX string
     * @param bytes  array byte
     * @return String
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length); // Initialize with estimated capacity

        for (byte b : bytes) {
            // Convert byte to int, ensuring it's treated as unsigned for hex conversion
            String hex = String.format("%02X", b);
            hexString.append(hex);
        }

        return hexString.toString();
    }


    /**
     * Date to string for sqlite format date.
     *
     * @param date the date
     * @return the string
     */
    public static String dateToStringForSQLite(Date date) {
        // Use ISO 8601 format for consistency with SQLite's date/time functions
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(date);
    }

    /**
     * String to date
     *
     * @param str the str
     * @return the date
     */
    public static Date stringToDate(String str) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            return formatter.parse(str);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Serialize to byte  [ ].
     *
     * @param obj the obj
     * @return the byte [ ]
     */
    static byte[] serializeByte(final Object obj) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try (ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(obj);
            out.flush();
            return bos.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Deserialize byte object.
     *
     * @param bytes the bytes
     * @return the object
     */
    static Object deserializeByte(byte[] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        try (ObjectInput in = new ObjectInputStream(bis)) {
            return in.readObject();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Serialize json string.
     *
     * @param obj the obj
     * @return the string
     */
    static String serializeJson(final Object obj) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(obj);
        return jsonString;
    }

    /**
     * Deserialize json object.
     *
     * @param str    the str
     * @param aClass the a class
     * @return the object
     */
    static Object deserializeJson(String str,Class aClass) {
        Gson gson = new Gson();
        return gson.fromJson(str, aClass);
    }
}
