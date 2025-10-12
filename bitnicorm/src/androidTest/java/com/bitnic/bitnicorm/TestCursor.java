package com.bitnic.bitnicorm;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@RunWith(AndroidJUnit4.class)
public class TestCursor extends BaseTestClass {

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
    public void TestCursorIterator() {
        initConfig();
        ISession session = Configure.getSession();
        try {
            session.dropTableIfExists(TablePersistent.class);
            session.createTableIfNotExists(TablePersistent.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i <  10 ; i++) {
            TablePersistent persistent=new TablePersistent("name",18);
            session.insert(persistent);
        }

        List<TablePersistent> list=new ArrayList<>();
         session.cursorIterator(TablePersistent.class,user -> {
             list.add(user);
         }," name not null order by name");
         assertTrue(list.size()==10);
         String sql="Select count (*)  from "+session.getTableName(TablePersistent.class);
         int count= (int) session.executeScalar(sql);
         assertTrue(count==10);
         boolean  b=session.any(TablePersistent.class," name is null");
        assertFalse(b);


    }



}
