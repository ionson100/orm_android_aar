package com.bitnic.bitnicorm;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.bitnic.bitnicorm.tablewhere.TableWhere1;
import com.bitnic.bitnicorm.tablewhere.TableWhere2;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@RunWith(AndroidJUnit4.class)
public class TestWhere extends BaseTestClass {

    @Test
    public void TestWhere() {
        initConfig();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableWhere1.class);
            session.createTableIfNotExists(TableWhere2.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        session.deleteRows(TableWhere1.class);
        session.deleteRows(TableWhere2.class);
        for (int i = 0; i < 10; i++) {
            session.insert(new TableWhere1("table1"));
        }
        for (int i = 0; i < 10; i++) {
            session.insert(new TableWhere2("table2"));
        }
        var count1 = session.count(TableWhere1.class);
        var count2 = session.count(TableWhere2.class);
        assertEquals(10, count1);
        assertEquals(10, count2);
        var list1 = session.getList(TableWhere1.class);
        var list2 = session.getList(TableWhere2.class);
        list1.forEach(tableWhere1 -> assertEquals("table1", tableWhere1.name));
        list2.forEach(tableWhere2 -> assertEquals("table2", tableWhere2.name));
        var res = session.updateRows(TableWhere1.class, new PairColumnValue().put("name", "table11"), null);

        list1 = session.getList(TableWhere1.class);
        list2 = session.getList(TableWhere2.class);
        list1.forEach(tableWhere1 -> assertEquals("table11", tableWhere1.name));
        list2.forEach(tableWhere2 -> assertEquals("table2", tableWhere2.name));
        session.deleteRows(TableWhere1.class);
        list1 = session.getList(TableWhere1.class);
        assertEquals(0, list1.size());
        list2 = session.getList(TableWhere2.class);
        assertEquals(10, list2.size());
        list1.forEach(tableWhere1 -> assertEquals("table11", tableWhere1.name));
        list2.forEach(tableWhere2 -> assertEquals("table2", tableWhere2.name));
        session.deleteRows(TableWhere2.class);
        list1 = session.getList(TableWhere1.class);
        assertEquals(0, list1.size());
        list2 = session.getList(TableWhere2.class);
        assertEquals(0, list2.size());
    }

    @Test
    public void TestWhere2() {
        initConfig();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableWhere1.class);
            session.createTableIfNotExists(TableWhere2.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < 10; i++) {
            session.insert(new TableWhere1("table1"));
        }
        for (int i = 0; i < 10; i++) {
            session.insert(new TableWhere2("table2"));
        }
        var count1 = session.count(TableWhere1.class, "1 = 1");
        var count2 = session.count(TableWhere2.class, "1=1");
        assertEquals(10, count1);
        assertEquals(10, count2);
        var list1 = session.getList(TableWhere1.class, "1=1");
        var list2 = session.getList(TableWhere2.class, "1=1");
        list1.forEach(tableWhere1 -> assertEquals("table1", tableWhere1.name));
        list2.forEach(tableWhere2 -> assertEquals("table2", tableWhere2.name));
        session.updateRows(TableWhere1.class, new PairColumnValue().put("name", "table11"), "1=1");

        list1 = session.getList(TableWhere1.class, "1=1");
        list2 = session.getList(TableWhere2.class, "1=1");
        list1.forEach(tableWhere1 -> assertEquals("table11", tableWhere1.name));
        list2.forEach(tableWhere2 -> assertEquals("table2", tableWhere2.name));
        session.deleteRows(TableWhere1.class, "1=1");
        list1 = session.getList(TableWhere1.class, "1=1");
        assertEquals(0, list1.size());
        list2 = session.getList(TableWhere2.class, "1=1");
        assertEquals(10, list2.size());
        list1.forEach(tableWhere1 -> assertEquals("table11", tableWhere1.name));
        list2.forEach(tableWhere2 -> assertEquals("table2", tableWhere2.name));
        session.deleteRows(TableWhere2.class, "1=1");
        list1 = session.getList(TableWhere1.class, "1=1");
        assertEquals(0, list1.size());
        list2 = session.getList(TableWhere2.class, "1=1");
        assertEquals(0, list2.size());

        session.insert(new TableWhere2("table2"));
        var o = session.firstOrDefault(TableWhere2.class, null);
        session.updateRows(TableWhere2.class, new PairColumnValue().put("name", "simple"), null);
        o = session.firstOrDefault(TableWhere2.class, null);
        session.deleteRows(TableWhere1.class);
        var o1 = session.firstOrDefault(TableWhere1.class, null);
        assertNull(o1);
        assertEquals("simple", o.name);


    }

    @MapTable
    static class SimpleTable {
        @MapPrimaryKey
        public long id;
        @MapColumn
        public String name="name";
        public int age=30;


    }

    @Test
    public void Test123() throws IOException {
        initConfig();
        ISession session = Configure.getSession();
        session.dropTableIfExists(SimpleTable.class);
        session.beginTransaction();
        try {
            if (!session.tableExists(SimpleTable.class)) {
                session.createTable(SimpleTable.class);
                //session.execSQLRawInner("script",null);
                List<SimpleTable>  list=new ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    list.add(new SimpleTable());
                }
                session.insertBulk(list);
            }
            session.commitTransaction();


        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            session.endTransaction();
            var t=session.getList(SimpleTable.class);
            assertEquals(10,t.size());
            session.close();
        }


    }

    @Test
    public void Test1234()  {
        initConfig();
        try (ISession session = Configure.getSession()) {

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
    @MapTable
    static class MyTable{
        @MapPrimaryKey
        public long id;
        @MapColumn
        public String name;
        @MapColumn
        public int age;
        @MapColumn
        public String email;
    }

    //@Test
    public void start(){
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        new Configure("myfile.sqlite",3,appContext,true); //start app

        try (ISession session = Configure.getSession()) {
            session.beginTransaction();
            try {
                if (session.tableExists(MyTable.class) == false) {
                    session.createTable(MyTable.class);
                }
                session.commitTransaction();
            } catch (Exception e) {
                throw new Exception(e);
            } finally {
                session.endTransaction();
            }
            List<MyTable> list = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                list.add(new MyTable());
            }

            session.insertBulk(list);
            List<MyTable> result = session.getList(MyTable.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    public void  TestListSelect() throws Exception {
        initConfig();
        ISession session = Configure.getSession();
        session.dropTableIfExists(MyTable.class);
        session.createTableIfNotExists(MyTable.class);
        for (int i = 0; i < 10; i++) {
            MyTable myTable=new MyTable();
            myTable.age=i;
            myTable.name="name"+i;
            session.insert(myTable);
        }
        var list=session.getListSelect("select name from "+session.getTableName(MyTable.class));
        assertTrue(list.size()==10);
    }
}
