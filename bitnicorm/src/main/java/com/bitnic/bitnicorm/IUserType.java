package com.bitnic.bitnicorm;


/**
 * If your field type implements this interface,
 * then this type will be serialized into the table as a row, you must take care of the serialization strategy yourself.
 */
public interface IUserType {
    /**
     * Initialization of the object body based on a string obtained from a table
     *
     * @param str the str
     */
    void initBody(String str);

    /**
     * Return a row for insertion into a table
     *
     * @return the string
     */
    String getString();
}
