package com.bitnic.bitnicorm;


import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * The type Data base helper.
 */
class DataBaseHelper extends SQLiteOpenHelper {


    private IAction<SQLiteDatabase> iOnOpenHelper;

    /**
     * Instantiates a new Data base helper.
     *
     * @param context      the context
     * @param databasePath the database path
     * @param version      the version
     */
    public DataBaseHelper(Context context, String databasePath, int version) {
        super(context, databasePath, null, version);
    }

    /**
     * Instantiates a new Data base helper.
     *
     * @param context       the context
     * @param databasePath  the database path
     * @param version       the version
     * @param iOnOpenHelper the on open helper
     */
    public DataBaseHelper(Context context, String databasePath, int version, IAction<SQLiteDatabase> iOnOpenHelper) {
        super(context, databasePath, null, version);
        this.iOnOpenHelper = iOnOpenHelper;
    }


    /**
     * Open data base for readable sq lite database.
     *
     * @return the sq lite database
     * @throws SQLException the sql exception
     */
    public SQLiteDatabase openDataBaseForReadable() throws SQLException {

        return this.getReadableDatabase();
    }

    /**
     * Open data base for writable sq lite database.
     *
     * @return the sq lite database
     * @throws SQLException the sql exception
     */
    public SQLiteDatabase openDataBaseForWritable() throws SQLException {

        return this.getWritableDatabase();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        if(iOnOpenHelper!=null){
            iOnOpenHelper.invoke(db);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}

