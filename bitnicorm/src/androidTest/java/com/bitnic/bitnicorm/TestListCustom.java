package com.bitnic.bitnicorm;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
    public void TestList() {
        initConfig();
        ISession session = Configure.getSession();
        try {
            session.dropTableIfExists(TableMain.class);
            session.createTableIfNotExists(TableMain.class);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < 5; i++) {
            TableMain t=new TableMain();
            t.aDouble=0.67D;
            t.stringList1.add("simple");
            t.stringList2.add("simple");
            session.insert(t);
        }
        List<TableCustom> list=session.getList(TableMain.class,TableCustom.class," aDouble > 0" );
        assertTrue(list.size()==5);
        list.forEach(tableCustom -> {
            assertTrue(tableCustom.aDouble==0.670D);
            assertTrue(tableCustom.stringList1.get(0).equals("simple"));
            assertTrue(tableCustom.stringList2.get(0).equals("simple"));
        });

    }


}
