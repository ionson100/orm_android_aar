package com.bitnic.bitnicorm;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.bitnic.bitnicorm.tablewhere.TableWhere1;
import com.bitnic.bitnicorm.tablewhere.TableWhere2;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@RunWith(AndroidJUnit4.class)
public class TestPersistent extends BaseTestClass {

    static class PersBase extends Persistent{
        @MapPrimaryKey
        public UUID uuid=UUID.randomUUID();
    }
    @MapTable
    static class TablePersistent extends PersBase{

        public TablePersistent(){}

        public TablePersistent(String name,int age){

            this.name = name;
            this.age = age;
        }

        @MapColumn
        public String name;
        @MapColumn
        public int age;
    }
    @Test
    public void TestInsert() {
        initConfig();
        ISession session = Configure.getSession();
        try {
            session.dropTableIfExists(TablePersistent.class);
            session.createTableIfNotExists(TablePersistent.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        TablePersistent persistent=new TablePersistent("name",18);
        assertFalse(persistent.isPersistent);
        session.insert(persistent);
        session.update(persistent);
        assertTrue(persistent.isPersistent);
        assertTrue(persistent.name.equals("name"));

        try {
            session.insert(persistent);
        }catch (Exception e){
            assertTrue(true);
            return;

        }
        assertTrue(false);

    }

    @Test
    public void TestInsertBulk() {
        initConfig();
        ISession session = Configure.getSession();
        try {
            session.dropTableIfExists(TablePersistent.class);
            session.createTableIfNotExists(TablePersistent.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        TablePersistent persistent=new TablePersistent("name",18);
        assertFalse(persistent.isPersistent);
        session.insertBulk(persistent);
        session.update(persistent);
        assertTrue(persistent.isPersistent);
        assertTrue(persistent.name.equals("name"));

        try {
            session.insertBulk(persistent);
        }catch (Exception e){
            assertTrue(true);
            return;
        }
        assertTrue(false);
    }
    @Test
    public void TestUpdate() {
        initConfig();
        ISession session = Configure.getSession();
        try {
            session.dropTableIfExists(TablePersistent.class);
            session.createTableIfNotExists(TablePersistent.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        TablePersistent persistent=new TablePersistent("name",18);
        assertFalse(persistent.isPersistent);
        try {
            session.update(persistent);
        }catch (Exception e){
            assertTrue(true);
            return;

        }
        assertTrue(false);

    }

    @Test
    public void TestSave() {
        initConfig();
        ISession session = Configure.getSession();
        try {
            session.dropTableIfExists(TablePersistent.class);
            session.createTableIfNotExists(TablePersistent.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        TablePersistent persistent=new TablePersistent("name",18);
        session.save(persistent);

        persistent=session.firstOrDefault(TablePersistent.class,null);
        assertTrue(persistent.isPersistent);
        assertTrue(persistent.name.equals("name"));

        persistent.name="newName";

        session.save(persistent);

        persistent=session.firstOrDefault(TablePersistent.class,null);
        assertTrue(persistent.isPersistent);
        assertTrue(persistent.name.equals("newName"));
        try {
            session.save(new MyTable());
        }catch (Exception e){
            assertTrue(true);
            return;

        }
        assertTrue(false);

    }

    @Test
    public void TestList() {
        initConfig();
        ISession session = Configure.getSession();
        try {
            session.dropTableIfExists(TablePersistent.class);
            session.createTableIfNotExists(TablePersistent.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        List<TablePersistent>  list=new ArrayList<>();
        for (int i = 0; i <10 ; i++) {
            if ((i & 1) == 0) {
                list.add(new TablePersistent("name2",18));
            } else {
                list.add(new TablePersistent("name3",18));
            }

        }
        session.insertBulk(list);
        var res=session.getList(TablePersistent.class);
        assertEquals(10,res.size());
        res.forEach(tablePersistent -> {
            assertTrue(tablePersistent.isPersistent);
        });

    }

    @Test
    public void TestGroupBy() {
        initConfig();
        ISession session = Configure.getSession();
        try {
            session.dropTableIfExists(TablePersistent.class);
            session.createTableIfNotExists(TablePersistent.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        List<TablePersistent>  list=new ArrayList<>();
        for (int i = 0; i <10 ; i++) {
            if ((i & 1) == 0) {
                list.add(new TablePersistent("name2",18));
            } else {
                list.add(new TablePersistent("name3",18));
            }

        }
        session.insertBulk(list);
        var ref = new Object() {
            int ii = 0;
        };
        var res=session.groupBy(TablePersistent.class,"name",null);
        assertEquals(2,res.size());
        res.forEach((o, tablePersistents) -> {
            tablePersistents.forEach(tablePersistent -> {
                assertTrue(tablePersistent.isPersistent);
                ref.ii = ref.ii +1;
            });
        });
        assertTrue(ref.ii==10);

    }

    @Test
    public void TestSingleFirst() {
        initConfig();
        ISession session = Configure.getSession();
        try {
            session.dropTableIfExists(TablePersistent.class);
            session.createTableIfNotExists(TablePersistent.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        session.save(new TablePersistent("name2",18));
        var o=session.firstOrDefault(TablePersistent.class,null);
        assertTrue(o.isPersistent);
        o=session.singleOrDefault(TablePersistent.class,null);
        assertTrue(o.isPersistent);

        try {
            o=session.first(TablePersistent.class,null);
            assertTrue(o.isPersistent);
            o=session.single(TablePersistent.class,null);
            assertTrue(o.isPersistent);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
