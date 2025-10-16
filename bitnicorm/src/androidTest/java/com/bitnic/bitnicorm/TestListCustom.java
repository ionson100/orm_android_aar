package com.bitnic.bitnicorm;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;


@RunWith(AndroidJUnit4.class)
public class TestListCustom extends BaseTestClass {

    @MapTable
    static class TableMain {
        @MapPrimaryKey
        public int anInt;
        @MapColumnIndex
        @MapColumn
        public double aDouble;
        @MapColumn
        public List<String> stringList1=new ArrayList<>();
        @MapColumn
        //@MapColumnJson
        public List<String> stringList2=new ArrayList<>();
    }
    static class TableCustom{
        public double aDouble;
        public List<String> stringList1;

        public List<String> stringList2;
    }

    @Test
    public void TestWritable(){
        initConfig();
        ISession session = Configure.getSession();
        try {
            session.dropTableIfExists(TableMain.class);
            session.createTableIfNotExists(TableMain.class);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String tableName=session.getTableName(TableMain.class);
        TableMain t = new TableMain();
        t.aDouble=0.67D;
        t.stringList1.add("simple");
        t.stringList2.add("simple");

        ContentValues contentValues=session.getContentValues(t);

        SQLiteDatabase sql=session.getSqLiteDatabaseForWritable();

        sql.insert(tableName,null,contentValues);
        List<TableMain> list=session.getList(TableMain.class);
        assertTrue(list.size()==1);
        list.forEach(tableCustom -> {
            assertTrue(tableCustom.aDouble==0.670D);
            assertTrue(tableCustom.stringList1.get(0).equals("simple"));
            assertTrue(tableCustom.stringList2.get(0).equals("simple"));
        });

    }

    @MapTable
    static class TableUser {
        @MapPrimaryKey
        public int id;
        @MapColumn
        String name="name";
        @MapColumn
        int age=18;
        @MapColumn
        String email="ion@qw.com";
    }

    @Test
    public void TestReadable(){
        initConfig();
//        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
//        new Configure("myfile.sqlite",3,appContext);
        ISession session = Configure.getSession();
        try {
            session.dropTableIfExists(TableUser.class);
            session.createTableIfNotExists(TableUser.class);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < 5; i++) {
            session.insert(new TableUser());
        }
        String tableName=session.getTableName(TableUser.class);

        List<TableUser> list = new ArrayList<>();
        SQLiteDatabase sql=session.getSqLiteDatabaseForReadable();
        Cursor cursor=sql.query(tableName,new String[]{"name","age","email","id"},"name not null",null,null,null,null);

        var r=session.getContentValuesForUpdate(TableUser.class,new PairColumnValue()
                .put("name","newName")
                .put("age",20)
                .put("email","ion100@df.com"));
        try {

            if (cursor.moveToFirst()) {
                do {
                    TableUser instance = new TableUser();
                    instance.name=cursor.getString(0);
                    instance.age=cursor.getInt(1);
                    instance.email=cursor.getString(2);
                    instance.id=cursor.getInt(3);
                    list.add(instance);
                } while (cursor.moveToNext());
            }

        }finally {
            cursor.close();
        }
        assertTrue(list.size()==5);
    }

    @Test
    public void TestFilling(){
        initConfig();

        ISession session = Configure.getSession();
        try {
            session.dropTableIfExists(TableUser.class);
            session.createTableIfNotExists(TableUser.class);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < 5; i++) {
            session.insert(new TableUser());
        }

        List<TableUser> list=new ArrayList<>();
        var sql="select * from "+session.getTableName(TableUser.class)+";";
        Log.i("--------------",sql);
        try (Cursor cursor = session.execSQLRaw(sql)) {

            if (cursor.moveToFirst()) {
                do {
                    TableUser u=  session.objectFiller(TableUser.class, cursor);
                    list.add(u);
                } while (cursor.moveToNext());
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        assertTrue(list.size()==5);
        list.forEach(tableUser -> {
            Log.i("------",tableUser.name);
        });


    }
    static class TestFilling {
        public String name;
        public int age;
    }
    @Test
    public void TestFilling2(){
        initConfig();

        ISession session = Configure.getSession();
        try {
            session.dropTableIfExists(TableUser.class);
            session.createTableIfNotExists(TableUser.class);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < 5; i++) {
            session.insert(new TableUser());
        }

        List<TestFilling> list=new ArrayList<>();
        var sql="select name, age from "+session.getTableName(TableUser.class)+";";
        Log.i("--------------",sql);
        try (Cursor cursor = session.execSQLRaw(sql)) {

            if (cursor.moveToFirst()) {
                do {
                    TestFilling u=  session.objectFiller(TestFilling.class, cursor);
                    list.add(u);
                } while (cursor.moveToNext());
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        assertTrue(list.size()==5);
        list.forEach(tableUser -> {
            Log.i("------",tableUser.name);
        });


    }

    @Test
    public void TestFillingLocaleClass(){
        initConfig();

        ISession session = Configure.getSession();
        try {
            session.dropTableIfExists(TableUser.class);
            session.createTableIfNotExists(TableUser.class);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < 5; i++) {
            session.insert(new TableUser());
        }
         class TestFilling2 {
            public String name;
            public int age;
        }

        List<TestFilling2> list=new ArrayList<>();
        var sql="select name, age from "+session.getTableName(TableUser.class)+";";
        Log.i("--------------",sql);
        try (Cursor cursor = session.execSQLRaw(sql)) {

            if (cursor.moveToFirst()) {
                do {
                    TestFilling2 testFilling2=new TestFilling2();
                    session.objectFiller(cursor,testFilling2);
                    list.add(testFilling2);
                } while (cursor.moveToNext());
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        assertTrue(list.size()==5);
        list.forEach(tableUser -> {
            Log.i("------",tableUser.name);
        });


    }




}
