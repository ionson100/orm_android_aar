package com.bitnic.bitnicorm;

import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
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
            byte[] raw = bos.toByteArray();
            // Если приложением подключён крипто-провайдер — шифруем blob перед записью.
            IBlobCrypto crypto = Configure.getBlobCrypto();
            if (crypto != null) {
                byte[] enc = crypto.encrypt(raw);
                if (enc != null) {
                    return enc;
                }
            }
            return raw;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Deserialize byte object.
     *
     * <p>Читает blob с защитой от десериализации опасных классов («gadget»-цепочки).
     * Если приложением подключён {@link IBlobCrypto}, сначала расшифровывает данные
     * (старые незашифрованные записи реализация возвращает как есть).</p>
     *
     * @param bytes the bytes
     * @return the object
     */
    static Object deserializeByte(byte[] bytes) {
        byte[] data = bytes;
        IBlobCrypto crypto = Configure.getBlobCrypto();
        if (crypto != null) {
            byte[] dec = crypto.decrypt(bytes);
            if (dec != null) {
                data = dec;
            }
        }
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        try (ObjectInput in = new SafeObjectInputStream(bis)) {
            return in.readObject();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Поток десериализации с фильтром классов: запрещает классы, типичные для
     * «gadget»-цепочек. Работает на всех уровнях API (в отличие от {@code ObjectInputFilter}).
     */
    private static final class SafeObjectInputStream extends ObjectInputStream {
        SafeObjectInputStream(InputStream in) throws IOException {
            super(in);
        }

        @Override
        protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
            String name = desc.getName();
            if (isForbiddenClass(name)) {
                throw new InvalidClassException("Deserialization of class is not allowed", name);
            }
            return super.resolveClass(desc);
        }
    }

    private static boolean isForbiddenClass(String name) {
        if (name == null) {
            return true;
        }
        if (name.startsWith("[")) {
            return false; // массивы: проверяется класс элемента
        }
        return name.startsWith("java.lang.Runtime")
                || name.startsWith("java.lang.ProcessBuilder")
                || name.startsWith("java.lang.reflect.")
                || name.startsWith("java.lang.invoke.")
                || name.startsWith("javax.script.")
                || name.startsWith("javax.management.")
                || name.startsWith("java.rmi.")
                || name.startsWith("java.beans.")
                || name.startsWith("sun.")
                || name.startsWith("com.sun.")
                || name.startsWith("org.apache.commons.collections")
                || name.startsWith("org.springframework.")
                || name.startsWith("com.thoughtworks.xstream");
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
