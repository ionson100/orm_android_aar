package com.bitnic.bitnicorm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.bitnic.bitnicorm.actionorm.TableActionOrm;
import com.bitnic.bitnicorm.bigdecimal.TableBigDecimal;
import com.bitnic.bitnicorm.bigdecimal.TableBooleanArray;
import com.bitnic.bitnicorm.bigdecimal.TableBooleanArray2;
import com.bitnic.bitnicorm.bigdecimal.TableByteArray;
import com.bitnic.bitnicorm.bigdecimal.TableByteArray2;
import com.bitnic.bitnicorm.bigdecimal.TableDateTime;
import com.bitnic.bitnicorm.bigdecimal.TableDoubleArray;
import com.bitnic.bitnicorm.bigdecimal.TableDoubleArray2;
import com.bitnic.bitnicorm.bigdecimal.TableFloatArray;
import com.bitnic.bitnicorm.bigdecimal.TableFloatArray2;
import com.bitnic.bitnicorm.bigdecimal.TableInset;
import com.bitnic.bitnicorm.bigdecimal.TableIntegerArray;
import com.bitnic.bitnicorm.bigdecimal.TableIntegerArray2;
import com.bitnic.bitnicorm.bigdecimal.TableLongArray;
import com.bitnic.bitnicorm.bigdecimal.TableLongArray2;
import com.bitnic.bitnicorm.bigdecimal.TableShortArray;
import com.bitnic.bitnicorm.bigdecimal.TableShortArray2;
import com.bitnic.bitnicorm.bigdecimal.TableStringArray;
import com.bitnic.bitnicorm.bigdecimal.TableUpdateAll;


import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RunWith(AndroidJUnit4.class)
public class TestMethod extends BaseTestClass {

    private void preInit() {

        initConfig();
        Configure.getSession().deleteRows(MyTable.class);
    }

    private MyTable factoryTable() {
        return new MyTable("name", 1L, 100, (short) 1, (byte) 8, new BigDecimal("1111111111111"), 1.1D, 1.1F);
    }

    @Test
    public void TestInsert() {
        preInit();

        ISession session = Configure.getSession();
        for (int i = 0; i < 10; i++) {

            session.insert(factoryTable());
        }
        var rowCount = session.deleteRows(MyTable.class, "id=?", 5);
        assertEquals(1, rowCount);
        var list = session.getList(MyTable.class);
        assertEquals(9, list.size());
        session.insert(factoryTable());
        var table = session.firstOrDefault(MyTable.class, "1=1 order by id DESC");
        assertEquals(11, table.id);

    }

    @Test
    public void TestDelete() {
        preInit();

        List<MyTable> list;
        try (ISession session = Configure.getSession()) {
            for (int i = 0; i < 10; i++) {
                session.insert(factoryTable());
            }
            var table = session.firstOrDefault(MyTable.class, "1=1 order by id DESC");
            var res = session.delete(table);
            assertEquals(1, res);
            list = session.getList(MyTable.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals(9, list.size());
    }

    @Test
    public void TestDeleteRowsAll() {
        preInit();

        List<MyTable> list;
        try (ISession session = Configure.getSession()) {
            for (int i = 0; i < 10; i++) {
                session.insert(factoryTable());
            }
            var res = session.deleteRows(MyTable.class);
            assertEquals(10, res);
            list = session.getList(MyTable.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals(0, list.size());
    }

    @Test
    public void TestUpDateAll() throws IOException {
        preInit();
        List<MyTable> list;
        try (ISession session = Configure.getSession()) {
            for (int i = 0; i < 10; i++) {
                session.insert(factoryTable());
            }
            session.updateRows(MyTable.class,
                    new PairColumnValue()
                            .put("name", "ibanes")
                            .put("inte", 20),
                    "name not null and longs =?",
                    1L);

            list = session.getList(MyTable.class);
        }
        assertEquals(10, list.size());
        list.forEach(myTable -> {
            assertEquals("ibanes", myTable.name);
            assertEquals(20, (int) myTable.inte);
        });

    }

    @Test
    public void TestNotInsert() throws IOException {
        preInit();
        try (ISession session = Configure.getSession()) {
            try {
                session.createTableIfNotExists(TableNotInsert.class);
                session.deleteRows(TableNotInsert.class);
                TableNotInsert tableNotInsert = new TableNotInsert();
                tableNotInsert.name = "11";
                tableNotInsert.age = 34;
                session.insert(tableNotInsert);
                var t = session.first(TableNotInsert.class, null);
                assertEquals("SIMPLE", t.name);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    public void TestNotInsertBulk() {
        preInit();
        try (ISession session = Configure.getSession()) {


            session.createTableIfNotExists(TableNotInsert.class);

            session.deleteRows(TableNotInsert.class);
            TableNotInsert tableNotInsert = new TableNotInsert();
            tableNotInsert.name = "11";
            tableNotInsert.age = 34;
            List<TableNotInsert> list = new ArrayList<>(1);
            list.add(tableNotInsert);
            session.insertBulk(list);
            var t = session.first(TableNotInsert.class, null);
            assertEquals("SIMPLE", t.name);


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void TestSimpleCreate() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(SimpleTable.class);

            SimpleTable simpleTable = new SimpleTable();
            simpleTable.date = new Date();
            session.insert(simpleTable);
            var t = session.getListSelect(SimpleTable.class, "name", null);
            assertEquals(0, t.size());
            assertEquals(1, 1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void TestInsertBulkAssArray() {
        preInit();
        ISession session = Configure.getSession();
        session.deleteRows(MyTable.class);
        List<MyTable> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            list.add(factoryTable());
        }
        session.insertBulk(list.get(0), list.get(1), list.get(2));
        var count = session.count(MyTable.class);
        assertEquals(3, count);

    }

    @Test
    public void TestRawExecute() {
        preInit();
        ISession session = Configure.getSession();
        session.deleteRows(MyTable.class);
        for (int i = 0; i < 10; i++) {
            session.insert(factoryTable());
        }
        var cursor = session.execSQLRaw("select name from " + session.getTableName(MyTable.class));
        List<String> stringList = new ArrayList<>();
        session.cursorIterator(MyTable.class, cursor, o -> stringList.add(o.name));
        assertEquals(10, stringList.size());
        stringList.forEach(s -> assertEquals("name", s));
    }

    static class tempName {
        public tempName() {
            myName = "no";
        }

        public String myName;
    }

    @Test
    public void TestRawExecute2() {
        preInit();
        ISession session = Configure.getSession();
        session.deleteRows(MyTable.class);
        for (int i = 0; i < 10; i++) {
            session.insert(factoryTable());
        }
        var cursor = session.execSQLRaw("select name as 'myName' from " + session.getTableName(MyTable.class));
        List<tempName> stringList = new ArrayList<>();
        session.cursorIterator(tempName.class, cursor, stringList::add);
        assertEquals(10, stringList.size());
        stringList.forEach(s -> assertEquals("name", s.myName));
    }

    @MapTable
    static class MFloat {
        @MapPrimaryKey
        public int id;
        @MapColumn
        public String name = "name";
        @MapColumn
        public float mFloat = 3.3F;
    }

    @Test
    public void TestCursorRowAsMap() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(MFloat.class);
            session.deleteRows(MFloat.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < 1; i++) {
            var t = new MFloat();
            t.name = "name";
            t.mFloat = 22F;
            session.insert(t);
        }

        var list = session.execSQLRawMap("select name, mFloat from 'TestMethod$MFloat'");
        assertEquals(1, list.size());

        list.forEach(row -> {
            assertEquals("name", row.get("name"));
            assertEquals(22.0F, row.get("mFloat"));
        });
    }

    @Test
    public void TestCursorRowAsArray() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(MFloat.class);
            session.deleteRows(MFloat.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < 1; i++) {
            var t = new MFloat();
            t.name = "name";
            t.mFloat = 22F;
            session.insert(t);
        }
        var list = session.execSQLRawArray("select name, mFloat from 'TestMethod$MFloat'");
        assertEquals(1, list.size());
        list.forEach(row -> {
            assertEquals("name", row[0]);
            assertEquals(22.0F, row[1]);
        });
    }

    @Test
    public void TestDropTable() {
        preInit();
        ISession session = Configure.getSession();
        session.dropTableIfExists(MyTable.class);
        var res = session.tableExists(MyTable.class);
        assertFalse(res);
        try {
            session.createTableIfNotExists(MyTable.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void TestDropTable2() {
        preInit();
        ISession session = Configure.getSession();
        session.dropTableIfExists(session.getTableName(MyTable.class));
        var res = session.tableExists(MyTable.class);
        assertFalse(res);
        try {
            session.createTableIfNotExists(MyTable.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void TestAny() {
        preInit();
        ISession session = Configure.getSession();
        session.deleteRows(MyTable.class);
        for (int i = 0; i < 10; i++) {
            session.insert(factoryTable());
        }
        var res = session.any(MyTable.class);
        assertTrue(res);
        var res2 = session.any(MyTable.class, "name=?", "simple");
        assertFalse(res2);
    }

    @Test
    public void TestTransaction() {
        preInit();
        ISession session = Configure.getSession();
        session.deleteRows(MyTable.class);
        session.beginTransaction();

        try {
            for (int i = 0; i < 10; i++) {
                session.insert(factoryTable());
            }
            if (true) {
                throw new RuntimeException();
            }
            session.commitTransaction();
        } catch (Exception ignored) {
        } finally {
            session.endTransaction();
        }
        var res = session.count(MyTable.class);
        assertEquals(0, res);

    }

    public void TestTransaction2() {
        preInit();
        ISession session = Configure.getSession();
        session.deleteRows(MyTable.class);
        session.beginTransaction();
        try {

            for (int i = 0; i < 10; i++) {
                session.insert(factoryTable());
            }
            session.commitTransaction();
        } catch (Exception ignored) {

        } finally {
            session.endTransaction();
        }
        var res = session.count(MyTable.class);
        assertEquals(10, res);

    }

    @Test
    public void TestTableActionOrm() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableActionOrm.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var t = new TableActionOrm();
        t.name = "name45";
        session.insert(t);
        assertEquals(200, t.action);
        var list = session.getList(TableActionOrm.class);
        assertEquals(1, list.size());
        t.action = 0;
        t.name = null;
        session.update(t);
        assertEquals(2, t.action);
        t.action = 0;
        session.delete(t);
        assertEquals(6, t.action);
    }

    @Test
    public void TestInsertUpdate() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableInsertUpdate.class);
            session.deleteRows(TableInsertUpdate.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var t = new TableInsertUpdate();


        session.insert(t);


        var tt = session.firstOrDefault(TableInsertUpdate.class, null);

        assertNull(tt.aByte);
        assertNull(tt.aDouble);
        assertNull(t.aFloat);
        assertNull(tt.aShort);
        assertNull(tt.aInt);
        assertNull(tt.aLong);

        t.aByte = 1;
        t.aDouble = 1D;
        t.aFloat = 1F;
        t.aShort = 1;
        t.aInt = 1;
        t.aLong = 1L;
        session.update(t);
        tt = session.firstOrDefault(TableInsertUpdate.class, null);
        assertEquals(1, (byte) tt.aByte);
        assertEquals(1D, tt.aDouble, 0.0);
        assertEquals(1F, tt.aFloat, 0.0);
        assertEquals(1, (short) tt.aShort);
        assertEquals(1, (int) tt.aInt);
        assertEquals(1, (long) tt.aLong);


    }

    @Test
    public void TestInsertUpdate2() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableInsertUpdate2.class);
            session.deleteRows(TableInsertUpdate2.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var t = new TableInsertUpdate2();


        session.insert(t);


        var tt = session.firstOrDefault(TableInsertUpdate2.class, null);

        assertEquals(0, tt.aByte);
        assertEquals(0D, tt.aDouble, 0.0);
        assertEquals(0F, tt.aFloat, 0.0);
        assertEquals(0, tt.aShort);
        assertEquals(0, tt.aInt);
        assertEquals(0, tt.aLong);

        t.aByte = 1;
        t.aDouble = 1D;
        t.aFloat = 1F;
        t.aShort = 1;
        t.aInt = 1;
        t.aLong = 1L;
        session.update(t);
        tt = session.firstOrDefault(TableInsertUpdate2.class, null);
        assertEquals(1, tt.aByte);
        assertEquals(1D, tt.aDouble, 0.0);
        assertEquals(1F, tt.aFloat, 0.0);
        assertEquals(1, tt.aShort);
        assertEquals(1, tt.aInt);
        assertEquals(1, tt.aLong);


    }

    @Test
    public void TestBigDecimal() {
        preInit();
        new Thread(() -> {
            ISession session = Configure.getSession();
            try {
                session.createTableIfNotExists(TableBigDecimal.class);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            session.deleteRows(TableBigDecimal.class);
            var t = new TableBigDecimal();
            assertTrue(session.tableExists(TableBigDecimal.class));

            session.insert(t);
            var o = session.firstOrDefault(TableBigDecimal.class, null);
            assertNull(o.bigDecimal);
            o.bigDecimal = new BigDecimal("1");
            session.update(o);
            o = session.firstOrDefault(TableBigDecimal.class, null);
            assertEquals(o.bigDecimal.toString(), new BigDecimal("1").toString());
        }).start();


    }

    @Test
    public void TestDateTime() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableDateTime.class);
            session.deleteRows(TableDateTime.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var t = new TableDateTime();
        session.insert(t);
        var o = session.firstOrDefault(TableDateTime.class, null);
        assertNull(o.date);
        var date = new Date();
        o.date = date;
        session.update(o);
        o = session.firstOrDefault(TableDateTime.class, null);
        assertEquals(date.toString(), o.date.toString());


    }

    @Test
    public void TestByteArray() {
        preInit();
        TableByteArray o;
        String res;
        try (ISession session = Configure.getSession()) {
            try {
                session.createTableIfNotExists(TableByteArray.class);
                session.deleteRows(TableByteArray.class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            var t = new TableByteArray();
            session.insert(t);
            o = session.firstOrDefault(TableByteArray.class, null);
            assertNull(o.aBytes22);
            o.aBytes22 = "12345".getBytes();
            session.update(o);
            o = session.firstOrDefault(TableByteArray.class, null);
            res = new String(o.aBytes22, StandardCharsets.UTF_8);
            assertEquals("12345", res);

            session.deleteRows(TableByteArray.class);

            session.insertBulk(new TableByteArray());
            o = session.firstOrDefault(TableByteArray.class, null);
            assertNull(o.aBytes22);

            session.deleteRows(TableByteArray.class);
            o = new TableByteArray();
            o.aBytes22 = "12345".getBytes();
            session.insertBulk(o);
            o = session.firstOrDefault(TableByteArray.class, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        res = new String(o.aBytes22, StandardCharsets.UTF_8);
        assertEquals("12345", res);

    }

    @Test
    public void TestByteArray2() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableByteArray2.class);
            session.deleteRows(TableByteArray2.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var t = new TableByteArray2();
        session.insert(t);

        var o = session.firstOrDefault(TableByteArray2.class, null);

        assertNull(o.aBytes22);
        var b = new Byte[]{1, 2, 3, 4};
        o.aBytes22 = b;
        session.update(o);

        o = session.firstOrDefault(TableByteArray2.class, null);
        assertEquals(1, (byte) o.aBytes22[0]);
        assertEquals(2, (byte) o.aBytes22[1]);
        assertEquals(3, (byte) o.aBytes22[2]);
        assertEquals(4, (byte) o.aBytes22[3]);


        session.deleteRows(TableByteArray2.class);

        session.insertBulk(new TableByteArray2());
        o = session.firstOrDefault(TableByteArray2.class, null);
        assertNull(o.aBytes22);

        session.deleteRows(TableByteArray2.class);
        o = new TableByteArray2();
        b = new Byte[]{1, 2, 3, 4};
        o.aBytes22 = b;
        session.insertBulk(o);
        o = session.firstOrDefault(TableByteArray2.class, null);
        assertEquals(1, (byte) o.aBytes22[0]);
        assertEquals(2, (byte) o.aBytes22[1]);
        assertEquals(3, (byte) o.aBytes22[2]);
        assertEquals(4, (byte) o.aBytes22[3]);


    }

    @Test
    public void TestIntArray() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableIntegerArray.class);
            session.deleteRows(TableIntegerArray.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var t = new TableIntegerArray();
        session.insert(t);

        var o = session.firstOrDefault(TableIntegerArray.class, null);

        assertNull(o.aBytes22);
        o.aBytes22 = new int[]{1, 2, 3, 4};
        session.update(o);
        o = session.firstOrDefault(TableIntegerArray.class, null);
        assertEquals(1, o.aBytes22[0]);
        assertEquals(2, o.aBytes22[1]);
        assertEquals(3, o.aBytes22[2]);
        assertEquals(4, o.aBytes22[3]);


    }

    @Test
    public void TestIntArrayBulkNull() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableIntegerArray.class);
            session.deleteRows(TableIntegerArray.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var o = new TableIntegerArray();
        session.insertBulk(o);

        o = session.firstOrDefault(TableIntegerArray.class, null);
        assertNull(o.aBytes22);
    }

    @Test
    public void TestIntArrayBulkNull2() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableIntegerArray2.class);
            session.deleteRows(TableIntegerArray2.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var o = new TableIntegerArray2();
        session.insertBulk(o);

        o = session.firstOrDefault(TableIntegerArray2.class, null);
        assertNull(o.aBytes22);
    }

    @Test
    public void TestIntArrayBulkNotNull() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableIntegerArray.class);
            session.deleteRows(TableIntegerArray.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var o = new TableIntegerArray();

        o.aBytes22 = new int[]{1, 2, 3, 4};

        session.insertBulk(o);

        o = session.firstOrDefault(TableIntegerArray.class, null);
        assertEquals(1, o.aBytes22[0]);
        assertEquals(2, o.aBytes22[1]);
        assertEquals(3, o.aBytes22[2]);
        assertEquals(4, o.aBytes22[3]);
    }

    @Test
    public void TestIntArrayBulkNotNull2() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableIntegerArray2.class);
            session.deleteRows(TableIntegerArray2.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var o = new TableIntegerArray2();

        o.aBytes22 = new Integer[]{0xFF, 2, 3, 4};

        session.insertBulk(o);

        o = session.firstOrDefault(TableIntegerArray2.class, null);
        assertEquals(0xFF, (int) o.aBytes22[0]);
        assertEquals(2, (int) o.aBytes22[1]);
        assertEquals(3, (int) o.aBytes22[2]);
        assertEquals(4, (int) o.aBytes22[3]);
    }

    @Test
    public void TestIntArray2() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableIntegerArray2.class);
            session.deleteRows(TableIntegerArray2.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var t = new TableIntegerArray2();
        session.insert(t);

        var o = session.firstOrDefault(TableIntegerArray2.class, null);

        assertNull(o.aBytes22);
        o.aBytes22 = new Integer[]{1, 2, 3, 4};
        session.update(o);
        o = session.firstOrDefault(TableIntegerArray2.class, null);
        assertEquals(1, (int) o.aBytes22[0]);
        assertEquals(2, (int) o.aBytes22[1]);
        assertEquals(3, (int) o.aBytes22[2]);
        assertEquals(4, (int) o.aBytes22[3]);
    }

    @Test
    public void TestLongArray() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableLongArray.class);
            session.deleteRows(TableLongArray.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var t = new TableLongArray();
        session.insert(t);

        var o = session.firstOrDefault(TableLongArray.class, null);

        assertNull(o.aBytes22);
        o.aBytes22 = new long[]{1, 2, 3, 4};
        session.update(o);
        o = session.firstOrDefault(TableLongArray.class, null);
        assertEquals(1, o.aBytes22[0]);
        assertEquals(2, o.aBytes22[1]);
        assertEquals(3, o.aBytes22[2]);
        assertEquals(4, o.aBytes22[3]);
    }

    @Test
    public void TestLongArrayBulkNull() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableLongArray.class);
            session.deleteRows(TableLongArray.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var t = new TableLongArray();
        session.insert(t);
        var o = session.firstOrDefault(TableLongArray.class, null);

        assertNull(o.aBytes22);
    }

    @Test
    public void TestLongArrayBulkNull2() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableLongArray2.class);
            session.deleteRows(TableLongArray2.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var t = new TableLongArray2();
        session.insert(t);
        var o = session.firstOrDefault(TableLongArray2.class, null);

        assertNull(o.aBytes22);
    }


    @Test
    public void TestLongArray2() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableLongArray2.class);
            session.deleteRows(TableLongArray2.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var t = new TableLongArray2();
        session.insert(t);

        var o = session.firstOrDefault(TableLongArray2.class, null);

        assertNull(o.aBytes22);
        o.aBytes22 = new Long[]{1L, 2L, 3L, 4L};
        session.update(o);
        o = session.firstOrDefault(TableLongArray2.class, null);
        assertEquals(1L, (long) o.aBytes22[0]);
        assertEquals(2L, (long) o.aBytes22[1]);
        assertEquals(3L, (long) o.aBytes22[2]);
        assertEquals(4L, (long) o.aBytes22[3]);
    }


    @Test
    public void TestLongBulkValue() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableLongArray.class);
            session.deleteRows(TableLongArray.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var o = new TableLongArray();

        o.aBytes22 = new long[]{1L, 2L, 3L, 4L};
        session.insertBulk(o);
        o = session.firstOrDefault(TableLongArray.class, null);
        assertEquals(1L, o.aBytes22[0]);
        assertEquals(2L, o.aBytes22[1]);
        assertEquals(3L, o.aBytes22[2]);
        assertEquals(4L, o.aBytes22[3]);
    }

    @Test
    public void TestLongBulkValue2() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableLongArray2.class);
            session.deleteRows(TableLongArray2.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var o = new TableLongArray2();

        o.aBytes22 = new Long[]{1L, 2L, 3L, 4L};
        session.insertBulk(o);
        o = session.firstOrDefault(TableLongArray2.class, null);
        assertEquals(1L, (long) o.aBytes22[0]);
        assertEquals(2L, (long) o.aBytes22[1]);
        assertEquals(3L, (long) o.aBytes22[2]);
        assertEquals(4L, (long) o.aBytes22[3]);
    }

    /* ----------------------------- double_____________________*/
    @Test
    public void TestDoubleArrayInsertBulkNull() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableDoubleArray.class);
            session.deleteRows(TableDoubleArray.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var o = new TableDoubleArray();
        session.insertBulk(o);
        o = session.firstOrDefault(TableDoubleArray.class, null);
        assertNull(o.aBytes22);
    }

    @Test
    public void TestDoubleArrayInsertBulkNotNull() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableDoubleArray.class);
            session.deleteRows(TableDoubleArray.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var o = new TableDoubleArray();
        o.aBytes22 = new double[]{1.1D, 1.2D};
        session.insertBulk(o);
        o = session.firstOrDefault(TableDoubleArray.class, null);
        assertEquals(1.1D, o.aBytes22[0], 0.0);
        assertEquals(1.2D, o.aBytes22[1], 0.0);
    }

    @Test
    public void TestDoubleArrayInsertBulkNull2() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableDoubleArray2.class);
            session.deleteRows(TableDoubleArray2.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var o = new TableDoubleArray2();
        session.insertBulk(o);
        o = session.firstOrDefault(TableDoubleArray2.class, null);
        assertNull(o.aBytes22);
    }

    @Test
    public void TestDoubleArrayInsertBulkNotNull2() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableDoubleArray2.class);
            session.deleteRows(TableDoubleArray2.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var o = new TableDoubleArray2();
        o.aBytes22 = new Double[]{1.1D, 1.2D};
        session.insertBulk(o);
        o = session.firstOrDefault(TableDoubleArray2.class, null);
        assertEquals(1.1D, o.aBytes22[0], 0.0);
        assertEquals(1.2D, o.aBytes22[1], 0.0);
    }

    @Test
    public void TestDoubleArrayInsertNull() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableDoubleArray.class);
            session.deleteRows(TableDoubleArray.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var o = new TableDoubleArray();
        session.insert(o);
        o = session.firstOrDefault(TableDoubleArray.class, null);
        assertNull(o.aBytes22);
    }

    @Test
    public void TestDoubleArrayInsertNotNull() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableDoubleArray.class);
            session.deleteRows(TableDoubleArray.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var o = new TableDoubleArray();
        o.aBytes22 = new double[]{1.1D, 1.2D};
        session.insert(o);
        o = session.firstOrDefault(TableDoubleArray.class, null);
        assertEquals(1.1D, o.aBytes22[0], 0.0);
        assertEquals(1.2D, o.aBytes22[1], 0.0);
    }

    @Test
    public void TestDoubleArrayInsertNull2() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableDoubleArray2.class);
            session.deleteRows(TableDoubleArray2.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var o = new TableDoubleArray2();
        session.insert(o);
        o = session.firstOrDefault(TableDoubleArray2.class, null);
        assertNull(o.aBytes22);
    }

    @Test
    public void TestDoubleArrayInsertNotNull2() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableDoubleArray2.class);
            session.deleteRows(TableDoubleArray2.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var o = new TableDoubleArray2();
        o.aBytes22 = new Double[]{1.1D, 1.2D};
        session.insert(o);
        o = session.firstOrDefault(TableDoubleArray2.class, null);
        assertEquals(1.1D, o.aBytes22[0], 0.0);
        assertEquals(1.2D, o.aBytes22[1], 0.0);
    }
    /*---------------------------------------float___________________________*/

    @Test
    public void TestFloatArrayInsertBulkNull() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableFloatArray.class);
            session.deleteRows(TableFloatArray.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var o = new TableFloatArray();
        session.insertBulk(o);
        o = session.firstOrDefault(TableFloatArray.class, null);
        assertNull(o.aBytes22);
    }

    @Test
    public void TestFloatArrayInsertBulkNotNull() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableFloatArray.class);
            session.deleteRows(TableFloatArray.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var o = new TableFloatArray();
        o.aBytes22 = new float[]{1.1F, 1.2F};
        session.insertBulk(o);
        o = session.firstOrDefault(TableFloatArray.class, null);
        assertEquals(1.1F, o.aBytes22[0], 0.0);
        assertEquals(1.2F, o.aBytes22[1], 0.0);
    }

    @Test
    public void TestFloatArrayInsertBulkNull2() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableFloatArray.class);
            session.deleteRows(TableFloatArray.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var o = new TableFloatArray();
        session.insertBulk(o);
        o = session.firstOrDefault(TableFloatArray.class, null);
        assertNull(o.aBytes22);
    }

    @Test
    public void TestFloatArrayInsertBulkNotNull2() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableFloatArray2.class);
            session.deleteRows(TableFloatArray2.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var o = new TableFloatArray2();
        o.aBytes22 = new Float[]{1.1F, 1.2F};
        session.insertBulk(o);
        o = session.firstOrDefault(TableFloatArray2.class, null);
        assertEquals(1.1F, o.aBytes22[0], 0.0);
        assertEquals(1.2F, o.aBytes22[1], 0.0);
    }

    @Test
    public void TestFloatArrayInsertNull() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableFloatArray.class);
            session.deleteRows(TableFloatArray.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var o = new TableFloatArray();
        session.insert(o);
        o = session.firstOrDefault(TableFloatArray.class, null);
        assertNull(o.aBytes22);
    }

    @Test
    public void TestFloatArrayInsertNotNull() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableFloatArray.class);
            session.deleteRows(TableFloatArray.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var o = new TableFloatArray();
        o.aBytes22 = new float[]{1.1F, 1.2F};
        session.insert(o);
        o = session.firstOrDefault(TableFloatArray.class, null);
        assertEquals(1.1F, o.aBytes22[0], 0.0);
        assertEquals(1.2F, o.aBytes22[1], 0.0);
    }

    @Test
    public void TestFloatArrayInsertNull2() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableFloatArray2.class);
            session.deleteRows(TableFloatArray2.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var o = new TableFloatArray2();
        session.insert(o);
        o = session.firstOrDefault(TableFloatArray2.class, null);
        assertNull(o.aBytes22);
    }

    @Test
    public void TestFloatArrayInsertNotNull2() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableFloatArray2.class);
            session.deleteRows(TableFloatArray2.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var o = new TableFloatArray2();
        o.aBytes22 = new Float[]{1.1F, 1.2F};
        session.insert(o);
        o = session.firstOrDefault(TableFloatArray2.class, null);
        assertEquals(1.1F, o.aBytes22[0], 0.0);
        assertEquals(1.2F, o.aBytes22[1], 0.0);
    }
    /*___________________________________bool___________________________________*/

    @Test
    public void TestShortArrayInsertBulkNull() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableShortArray.class);
            session.deleteRows(TableShortArray.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var o = new TableShortArray();
        session.insertBulk(o);
        o = session.firstOrDefault(TableShortArray.class, null);
        assertNull(o.aBytes22);
    }

    @Test
    public void TestShortArrayInsertBulkNotNull() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableShortArray.class);
            session.deleteRows(TableShortArray.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var o = new TableShortArray();
        o.aBytes22 = new short[]{1, 2};
        session.insertBulk(o);
        o = session.firstOrDefault(TableShortArray.class, null);
        assertEquals(1, o.aBytes22[0]);
        assertEquals(2, o.aBytes22[1]);
    }

    @Test
    public void TestShortArrayInsertBulkNull2() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableShortArray2.class);
            session.deleteRows(TableShortArray2.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var o = new TableShortArray2();
        session.insertBulk(o);
        o = session.firstOrDefault(TableShortArray2.class, null);
        assertNull(o.aBytes22);
    }

    @Test
    public void TestShortArrayInsertBulkNotNull2() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableShortArray2.class);
            session.deleteRows(TableShortArray2.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var o = new TableShortArray2();
        o.aBytes22 = new Short[]{1, 2};
        session.insertBulk(o);
        o = session.firstOrDefault(TableShortArray2.class, null);
        assertEquals(1, (short) o.aBytes22[0]);
        assertEquals(2, (short) o.aBytes22[1]);
    }

    @Test
    public void TestShortArrayInsertNull() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableShortArray.class);
            session.deleteRows(TableShortArray.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var o = new TableShortArray();
        session.insert(o);
        o = session.firstOrDefault(TableShortArray.class, null);
        assertNull(o.aBytes22);
    }

    @Test
    public void TestShortArrayInsertNotNull() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableShortArray.class);
            session.deleteRows(TableShortArray.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var o = new TableShortArray();
        o.aBytes22 = new short[]{1, 2};
        session.insert(o);
        o = session.firstOrDefault(TableShortArray.class, null);
        assertEquals(1, o.aBytes22[0]);
        assertEquals(2, o.aBytes22[1]);
    }

    @Test
    public void TestShortArrayInsertNull2() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableShortArray2.class);
            session.deleteRows(TableShortArray2.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var o = new TableShortArray2();
        session.insert(o);
        o = session.firstOrDefault(TableShortArray2.class, null);
        assertNull(o.aBytes22);
    }

    @Test
    public void TestShortArrayInsertNotNull2() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableShortArray2.class);
            session.deleteRows(TableShortArray2.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var o = new TableShortArray2();
        o.aBytes22 = new Short[]{1, 2};
        session.insert(o);
        o = session.firstOrDefault(TableShortArray2.class, null);
        assertEquals(1, (short) o.aBytes22[0]);
        assertEquals(2, (short) o.aBytes22[1]);
    }
    /*---------------------------------stringArray-----------------------*/

    @Test
    public void TestStringArrayInsertBulkNull() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableStringArray.class);
            session.deleteRows(TableStringArray.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var o = new TableStringArray();
        session.insertBulk(o);
        o = session.firstOrDefault(TableStringArray.class, null);
        assertNull(o.aBytes22);
    }

    @Test
    public void TestStringArrayInsertBulkNotNull() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableStringArray.class);
            session.deleteRows(TableStringArray.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var o = new TableStringArray();
        o.aBytes22 = new String[]{"1", "2"};
        session.insertBulk(o);
        o = session.firstOrDefault(TableStringArray.class, null);

        assertEquals("1", o.aBytes22[0]);
        assertEquals("2", o.aBytes22[1]);
    }

    @Test
    public void TestStringArrayInsertNull() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableStringArray.class);
            session.deleteRows(TableStringArray.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var o = new TableStringArray();
        session.insert(o);
        o = session.firstOrDefault(TableStringArray.class, null);
        assertNull(o.aBytes22);
    }

    @Test
    public void TestStringArrayInsertNotNull() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableStringArray.class);
            session.deleteRows(TableStringArray.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var o = new TableStringArray();
        o.aBytes22 = new String[]{"1", "   2"};
        session.insert(o);
        o = session.firstOrDefault(TableStringArray.class, null);

        assertEquals("1", o.aBytes22[0]);
        assertEquals("   2", o.aBytes22[1]);
    }




    /*___________________________________bool___________________________________*/

    @Test
    public void TestBooleanArrayInsertBulkNull() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableBooleanArray.class);
            session.deleteRows(TableBooleanArray.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var o = new TableBooleanArray();
        session.insertBulk(o);
        o = session.firstOrDefault(TableBooleanArray.class, null);
        assertNull(o.aBytes22);
    }

    @Test
    public void TestBooleanArrayInsertBulkNotNull() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableBooleanArray.class);
            session.deleteRows(TableBooleanArray.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var o = new TableBooleanArray();
        o.aBytes22 = new boolean[]{true, true};
        session.insertBulk(o);
        o = session.firstOrDefault(TableBooleanArray.class, null);
        assertTrue(o.aBytes22[0]);
        assertTrue(o.aBytes22[1]);
    }

    @Test
    public void TestBooleanArrayInsertBulkNull2() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableBooleanArray2.class);
            session.deleteRows(TableBooleanArray2.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var o = new TableBooleanArray2();
        session.insertBulk(o);
        o = session.firstOrDefault(TableBooleanArray2.class, null);
        assertNull(o.aBytes22);
    }

    @Test
    public void TestBooleanArrayInsertBulkNotNull2() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableBooleanArray2.class);
            session.deleteRows(TableBooleanArray2.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var o = new TableBooleanArray2();
        o.aBytes22 = new Boolean[]{true, true};
        session.insertBulk(o);
        o = session.firstOrDefault(TableBooleanArray2.class, null);
        assertTrue(o.aBytes22[0]);
        assertTrue(o.aBytes22[1]);
    }

    @Test
    public void TestBooleanArrayInsertNull() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableBooleanArray.class);
            session.deleteRows(TableBooleanArray.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var o = new TableBooleanArray();
        session.insert(o);
        o = session.firstOrDefault(TableBooleanArray.class, null);
        assertNull(o.aBytes22);
    }

    @Test
    public void TestBooleanArrayInsertNotNull() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableBooleanArray.class);
            session.deleteRows(TableBooleanArray.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var o = new TableBooleanArray();
        o.aBytes22 = new boolean[]{true, true};
        session.insert(o);
        o = session.firstOrDefault(TableBooleanArray.class, null);
        assertTrue(o.aBytes22[0]);
        assertTrue(o.aBytes22[1]);
    }

    @Test
    public void TestBooleanArrayInsertNull2() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableBooleanArray2.class);
            session.deleteRows(TableBooleanArray2.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var o = new TableBooleanArray2();
        session.insert(o);
        o = session.firstOrDefault(TableBooleanArray2.class, null);
        assertNull(o.aBytes22);
    }

    @Test
    public void TestBooleanArrayInsertNotNull2() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableBooleanArray2.class);
            session.deleteRows(TableBooleanArray2.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var o = new TableBooleanArray2();
        o.aBytes22 = new Boolean[]{true, true};
        session.insert(o);
        o = session.firstOrDefault(TableBooleanArray2.class, null);
        assertTrue(o.aBytes22[0]);
        assertTrue(o.aBytes22[1]);
    }

    @Test
    public void TestUpdateAll() {
        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableUpdateAll.class);
            session.deleteRows(TableUpdateAll.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < 5; i++) {
            session.insert(new TableUpdateAll());
        }
        var res = session.updateRows(TableUpdateAll.class, new PairColumnValue().
                        put("name1", "name1").
                        put("name2", "name2")
                        .put("integer1", 10)
                        .put("integer2", null)
                        .put("double1", 1D)
                        .put("double2", null).
                        put("float1", 1F)
                        .put("float2", null)
                        .put("long1", 1L)
                        .put("long2", null)
                        .put("ints", new int[]{1, 1})
                        .put("integers2", new Integer[]{1, 1})
                        .put("integers1", new int[]{1, 1})
                        .put("floats1", new float[]{1F, 1F})
                        .put("floats2", null)
                        //.put("doubles1",new double[]{1D,1D})
                        .put("doubles2", null)
                        .put("boolean1", true)
                        .put("boolean2", null)
                        .put("booleans1", new boolean[]{true, true})
                        .put("booleans2", null)
                        .put("date1", new Date())
                        .put("date2", null)
                        .put("strings", new String[]{"name1", "name2"})
                        .put("bigDecimal1", new BigDecimal("111111111"))
                        .put("bigDecimal2", null)

                , " id < ? ", 20);
        assertEquals(5, res);
        var o = session.getList(TableUpdateAll.class, "1 =1 order by id");
        assertEquals(5, o.size());


    }

    //@Test
    public void TestInsetNew() {


        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableInset.class);
            session.deleteRows(TableInset.class);
        } catch (Exception e) {
            throw new RuntimeException(e);


        }
        var o = new TableInset();
        for (int i = 0; i < 4000; i++) {
            session.insert(o);
        }

    }

    //@Test
    public void TestInsetOld() {


        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableInset.class);
            session.deleteRows(TableInset.class);
        } catch (Exception e) {
            throw new RuntimeException(e);


        }
        var o = new TableInset();
        for (int i = 0; i < 4000; i++) {
            session.insert(o);
        }

    }

    @Test
    public void TestInsetBulkTime() {


        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableInset.class);
            session.deleteRows(TableInset.class);
        } catch (Exception e) {
            throw new RuntimeException(e);


        }
        List<TableInset> list = new ArrayList<>(4000);

        for (int i = 0; i < 40; i++) {
            list.add(new TableInset());
        }
        session.insertBulk(list);

    }

    @Test
    public void TestUpdateCore() {


        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableInset.class);
            session.deleteRows(TableInset.class);
        } catch (Exception e) {
            throw new RuntimeException(e);


        }
        var o = new TableInset();

        session.insert(o);


        session.update(o);
        o = session.firstOrDefault(TableInset.class, null);
        assertNotNull(o.myData);
        assertTrue(true);


    }

    //@Test
    public void TestUpdateOld() {


        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableInset.class);
            session.deleteRows(TableInset.class);
        } catch (Exception e) {
            throw new RuntimeException(e);


        }
        var o = new TableInset();
        for (int i = 0; i < 4000; i++) {
            session.insert(o);
        }
        var list = session.getList(TableInset.class);
        list.forEach(session::update);

    }

    @Test
    public void TestUpdateNew() {


        preInit();
        ISession session = Configure.getSession();
        try {
            session.createTableIfNotExists(TableInset.class);
            session.deleteRows(TableInset.class);
        } catch (Exception e) {
            throw new RuntimeException(e);


        }
        var o = new TableInset();
        o.name = "111'111";
        session.insert(o);
        session.update(session.firstOrDefault(TableInset.class, null));


        var list = session.getList(TableInset.class);

        assertEquals("111'111", list.get(0).name);

    }

    @Test
    public void TestCreateTable() {
        preInit();
        ISession session = Configure.getSession();
        var ee = Configure.getSqlCreateTable(MyTable.class, true);
        assertTrue(true);
    }

    @Test
    public void TestNotInsertUpdateAll() throws IOException {
        preInit();
        try (ISession session = Configure.getSession()) {

            try {
                session.createTableIfNotExists(TableNotInsert.class);
                session.deleteRows(TableNotInsert.class);
                TableNotInsert tableNotInsert = new TableNotInsert();
                tableNotInsert.name = "11";
                tableNotInsert.age = 34;
                session.insert(tableNotInsert);
                var t = session.first(TableNotInsert.class, null);
                session.updateRows(TableNotInsert.class, new PairColumnValue()
                        .put("name", "111")
                        .put("age", 100), null);
                t = session.first(TableNotInsert.class, null);
                assertEquals("SIMPLE", t.name);
                assertEquals(100, t.age);
                t.name = "1212";
                t.age = 0;
                session.update(t);
                t = session.first(TableNotInsert.class, "1");
                assertEquals("SIMPLE", t.name);
                assertEquals(0, t.age);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


    @MapTable
    static class TableList {
        @MapPrimaryKey

        public int id;

        @MapColumn
        public List<Object> list = new ArrayList<>();
    }

    @Test
    public void TestList() {
        preInit();
        ISession session = Configure.getSession();
        session.dropTableIfExists(TableList.class);
        try {
            session.createTableIfNotExists(TableList.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        TableList list = new TableList();
        list.list.add(3);
        session.insert(list);
        var o = session.getList(TableList.class);
        assertEquals(3, o.get(0).list.get(0));
    }

    @MapTable
    static class tableListJson {
        @MapPrimaryKey

        public int id;

        @MapColumn
        public List<Integer> list = new ArrayList<>();
    }

    @Test
    public void TestListJoin() {
        preInit();
        ISession session = Configure.getSession();
        session.dropTableIfExists(tableListJson.class);
        try {
            session.createTableIfNotExists(tableListJson.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        tableListJson list = new tableListJson();
        list.list.add(3);
        session.insert(list);
        var o = session.firstOrDefault(tableListJson.class, null);

        assertEquals(3, (int) o.list.get(0));
    }

    @MapTable
    static class TableListMap {
        @MapPrimaryKey
        public int id;
        @MapColumn
        public Map<String, Integer> list = new HashMap<>();
    }

    @Test
    public void TestMap() {
        preInit();
        ISession session = Configure.getSession();
        session.dropTableIfExists(TableListMap.class);
        try {
            session.createTableIfNotExists(TableListMap.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        TableListMap list = new TableListMap();
        list.list.put("assa", 3);
        session.insert(list);
        var o = session.firstOrDefault(TableListMap.class, null);

        assertEquals(3, (int) o.list.get("assa"));
    }

    @MapTable
    static class tableSet {
        @MapPrimaryKey
        public int id;
        @MapColumn
        public Set<String> list = new HashSet<>();
    }

    @Test
    public void TesSet() {
        preInit();
        ISession session = Configure.getSession();
        session.dropTableIfExists(tableSet.class);
        try {
            session.createTableIfNotExists(tableSet.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        tableSet list = new tableSet();
        list.list.add("assa");
        session.insert(list);
        var o = session.firstOrDefault(tableSet.class, null);


        assertTrue(o.list.contains("assa"));
    }


    @MapTable
    static class TableBigDecimal2 {
        @MapPrimaryKey
        public int id;
        @MapColumn
        public BigDecimal list = new BigDecimal("11");
    }

    @Test
    public void TesBigDecimal() {
        preInit();
        ISession session = Configure.getSession();
        session.dropTableIfExists(TableBigDecimal2.class);
        try {
            session.createTableIfNotExists(TableBigDecimal2.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        TableBigDecimal2 list = new TableBigDecimal2();

        session.insert(list);
        var o = session.firstOrDefault(TableBigDecimal2.class, null);


        assertEquals("11", o.list.toString());
    }

    @MapTable
    static class TableUUID {
        @MapPrimaryKey
        public int id;
        @MapColumn
        public UUID list = UUID.randomUUID();
    }

    @Test
    public void TesUUID() {
        preInit();
        ISession session = Configure.getSession();
        session.dropTableIfExists(TableUUID.class);
        try {
            session.createTableIfNotExists(TableUUID.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        TableUUID list = new TableUUID();

        session.insert(list);
        var o = session.firstOrDefault(TableUUID.class, null);


        assertEquals(list.list.toString(), o.list.toString());
    }

    @MapTable
    static class TableExternalizable implements Externalizable {
        @MapPrimaryKey
        public int id;
        @MapColumn
        public String name;

        @Override
        public void readExternal(ObjectInput in) throws ClassNotFoundException, IOException {
            id = (int) in.readObject();
            name = (String) in.readObject();
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(this.id);
            out.writeObject(this.name);
        }
    }

    @Test
    public void TesExternalizable() {
        preInit();
        ISession session = Configure.getSession();
        session.dropTableIfExists(TableExternalizable.class);
        try {
            session.createTableIfNotExists(TableExternalizable.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        TableExternalizable ext = new TableExternalizable();
        ext.name = "simple";
        ext.id = 1000;


        session.insert(ext);

        var o = session.firstOrDefault(TableExternalizable.class, null);


        assertNotNull(o.id == 1000);
    }


    @MapTable
    static class TableEnum {
        @MapPrimaryKey
        UUID uuid = UUID.randomUUID();
        @MapColumn
        private final int ee = 3;

        @MapColumn
        @MapColumnType("DATE DEFAULT CURRENT_TIMESTAMP")
        @MapColumnReadOnly
        public Date dateCreate;
    }

    @Test
    public void TestEnum() {

        preInit();
        ISession session = Configure.getSession();
        session.dropTableIfExists(TableEnum.class);
        try {
            session.createTableIfNotExists(TableEnum.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        List<TableEnum> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(new TableEnum());

        }
        session.insertBulk(list);

        var o = session.firstOrDefault(TableEnum.class, null);
        //assertEquals(Level.HIGH,o.level);
    }

    @Test
    public void TestDemo() {

        preInit();
        ISession session = Configure.getSession();
        session.dropTableIfExists(TableDemo.class);
        try {
            session.createTableIfNotExists(TableDemo.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        session.insertBulk(new TableDemo());
        var o = session.firstOrDefault(TableDemo.class, null);
        assertEquals("name", o.string);
        o.string = "name1";
        session.update(o);
        o = session.firstOrDefault(TableDemo.class, null);
        assertEquals("name1", o.string);
        session.updateRows(TableDemo.class, new PairColumnValue()
                .put("string", "name2").put("aByte", 123), null);
        o = session.firstOrDefault(TableDemo.class, null);
        assertEquals("name2", o.string);
        assertEquals(123, o.aByte);
        var ee = o.demoJson.name;
        assertEquals("name", ee);
    }

    @Test
    public void TestUpdate() {
        preInit();

        ISession session = Configure.getSession();
        session.dropTableIfExists(TableDemo.class);
        try {
            session.createTableIfNotExists(TableDemo.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        session.insertBulk(new TableDemo());
        var o = session.firstOrDefault(TableDemo.class, null);
        var i = session.update(o);
        assertEquals(1, i);
        o.id = UUID.randomUUID();

        i = session.update(o);
        assertEquals(0, i);
    }

    @Test
    public void TestDelete2() {
        preInit();

        ISession session = Configure.getSession();
        session.dropTableIfExists(TableDemo.class);
        try {
            session.createTableIfNotExists(TableDemo.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        session.insertBulk(new TableDemo());
        var o = session.firstOrDefault(TableDemo.class, null);
        var i = session.delete(o);
        assertEquals(1, i);
        o.id = UUID.randomUUID();

        i = session.delete(o);
        assertEquals(0, i);
    }

    @Test
    public void TestListySelect() {
        preInit();

        ISession session = Configure.getSession();
        session.dropTableIfExists(TableDemo.class);
        try {
            session.createTableIfNotExists(TableDemo.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        session.insertBulk(new TableDemo());
        var o = session.getListSelect(TableDemo.class, "string", null);
        assertEquals(1, o.size());

    }

    static class Children implements Serializable {
        public String name = "Leo";
        public int age = 3;
    }

    static class ExternalizableDemo implements Externalizable {
        public String firstName = "ion";
        public String lastName = "Ionow";
        public int age = 18;

        public ExternalizableDemo() {
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            if (in.readBoolean()) {
                firstName = in.readUTF();
            }
            if (in.readBoolean()) {
                lastName = in.readUTF();
            }
            age = in.readInt();
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            if (firstName == null) {
                out.writeBoolean(false);
            } else {
                out.writeBoolean(true);
                out.writeUTF(firstName);
            }
            if (lastName == null) {
                out.writeBoolean(false);
            } else {
                out.writeBoolean(true);
                out.writeUTF(lastName);
            }
            out.writeInt(age);
        }
    }

    @MapTableName("ny_table")
    static class MyUser {
        @MapPrimaryKey
        public long id;
        @MapColumn
        public String name = "simple";
        @MapColumn
        public int age = 15;
        @MapColumn
        public String email = "ion@df.com";
        @MapColumn
        public List<Children> childrenList = new ArrayList<>();

        @MapColumn
        public ExternalizableDemo externalizable;
    }

    @Test
    public void TestSingleOK() {
        preInit();
        ISession session = Configure.getSession();
        session.dropTableIfExists(MyUser.class);
        try {
            session.createTableIfNotExists(MyUser.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        session.insertBulk(new MyUser());
        MyUser o = null;
        try {
            o = session.single(MyUser.class, null);
        } catch (Exception e) {
            throw new RuntimeException();
        }
        assertTrue(true);
    }

    @Test
    public void TestSingleNotOK() {
        preInit();
        ISession session = Configure.getSession();
        session.dropTableIfExists(MyUser.class);
        try {
            session.createTableIfNotExists(MyUser.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        session.insertBulk(new MyUser(), new MyUser());

        try {
            MyUser o = session.single(MyUser.class, null);
        } catch (Exception e) {
            assertTrue(true);
            return;
        }
        fail();

    }

    @Test
    public void TestFirstOK() {
        preInit();
        ISession session = Configure.getSession();
        session.dropTableIfExists(MyUser.class);
        try {
            session.createTableIfNotExists(MyUser.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        session.insertBulk(new MyUser());

        try {
            MyUser o = session.first(MyUser.class, null);
        } catch (Exception e) {
            throw new RuntimeException();
        }
        assertTrue(true);
    }

    @Test
    public void TestFirstNotOK() {
        preInit();
        ISession session = Configure.getSession();
        session.dropTableIfExists(MyUser.class);
        try {
            session.createTableIfNotExists(MyUser.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        session.insertBulk(new MyUser(), new MyUser());
        try {
            MyUser o = session.single(MyUser.class, "name=?", "asas");
        } catch (Exception e) {
            assertTrue(true);
            return;
        }
        fail();

    }

    @Test
    public void TestGroupBy() {
        preInit();
        ISession session = Configure.getSession();
        session.dropTableIfExists(MyUser.class);
        try {
            session.createTableIfNotExists(MyUser.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        session.insertBulk(new MyUser(), new MyUser());


        var map = session.groupBy(MyUser.class, "name", null);
        assertEquals(1, map.size());
        assertEquals(2, map.get("simple").size());
    }

    @Test
    public void TestSelect() {
        preInit();
        ISession session = Configure.getSession();
        session.dropTableIfExists(MyUser.class);
        try {
            session.createTableIfNotExists(MyUser.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        session.insertBulk(new MyUser(), new MyUser());


        var list = session.getListSelect(MyUser.class, "name", null);
        assertEquals(2, list.size());

    }

    @Test
    public void TestDistinct() {
        preInit();
        ISession session = Configure.getSession();
        session.dropTableIfExists(MyUser.class);
        try {
            session.createTableIfNotExists(MyUser.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        session.insertBulk(new MyUser(), new MyUser());
        var list = session.distinctBy(MyUser.class, "name", null);
        assertEquals(1, list.size());

    }

    @Test
    public void DemoSerialise() {
        preInit();
        ISession session = Configure.getSession();
        session.dropTableIfExists(MyUser.class);
        try {
            session.createTableIfNotExists(MyUser.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var table = new MyUser();
        table.childrenList.add(new Children());
        table.externalizable = new ExternalizableDemo();

        session.insert(table);
        var list = session.getList(MyUser.class);
        list.forEach(myTable1 -> {
            myTable1.childrenList.forEach(children -> {
                Log.i("------------", children.name + "@" + children.age);
            });
            Log.i("------------", myTable1.externalizable.firstName + "@" + myTable1.externalizable.lastName + "@" + myTable1.externalizable.age);
        });
        assertEquals(1, list.size());
        assertEquals(1L, list.get(0).id);

    }


}
