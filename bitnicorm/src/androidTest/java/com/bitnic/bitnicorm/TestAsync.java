package com.bitnic.bitnicorm;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;


@RunWith(AndroidJUnit4.class)
public class TestAsync extends BaseTestClass {


    @MapTable
    static class MyTable {
        @MapPrimaryKey
        public long id;
        @MapColumn
        public String name;
        @MapColumn
        public int age;
        @MapColumn
        public String email;
    }


    @Test
    public void TestCore() throws Exception {
        initConfig();

        CompletableFuture<List<MyTable>> f;
        extracted();
        var rr=Configure.getSessionAutoClose().query(MyTable.class).toList();
        List<MyTable> list = new ArrayList<>();
        f = Configure.getSessionAutoClose().query(MyTable.class).toListAsync();

        f.handleAsync((myTables, throwable) -> {
            if(throwable!=null){
                List<MyTable> list1 = new ArrayList<>();
                return list1;
            }
            return myTables;

        }).thenAcceptAsync(myTables -> {
            for (MyTable myTable : myTables) {
                list.add(myTable);
                Log.i("____________",myTable.name);
            }
        });
        assertTrue(list.size() == 0);
        Thread.sleep(2000);
        assertTrue(list.size() == 10);


    }

    @Test
    public void TestCoreSelect() throws Exception {
        initConfig();


        extracted();
        //var rr=Configure.getSessionAutoClose().getListSelect(MyTable.class,"age",null);
        List<String> list = new ArrayList<>();
        CompletableFuture<List<Object>> f;
        f = Configure.getSessionAutoClose().query(MyTable.class).where("age > ?",-1).selectAsync("name");
        f.thenAcceptAsync(objects -> {
            for (Object o : objects) {
                list.add((String) o);
                Log.i("____________",String.valueOf(o));
            }
        });
        assertTrue(list.size() == 0);
        Thread.sleep(2000);
        assertTrue(list.size() == 10);


    }
    @Test
    public void TestCoreSelectExpression() throws Exception {
        initConfig();


        extracted();
        //var rr=Configure.getSessionAutoClose().getListSelect(MyTable.class,"age",null);
        List<Object> list = new ArrayList<>();
        CompletableFuture<List<Object>> f;
        f = Configure.getSessionAutoClose().query(MyTable.class)
                .where("age > ?",-1)
                .orderBy("name")
                .selectExpressionAsync("age*10");

        f.thenAcceptAsync(objects -> {
            for (Object o : objects) {
                list.add( o);
                Log.i("____________",String.valueOf(o));
            }
        });
        assertTrue(list.size() == 0);
        Thread.sleep(2000);
        assertTrue(list.size() == 10);


    }

    @Test
    public void TestCoreGroupBy() throws Exception {
        initConfig();

        CompletableFuture<Map<Object, List<MyTable>>> f;
        extracted();
        //var rr=Configure.getSessionAutoClose().getListSelect(MyTable.class,"age",null);
        AtomicReference<Map<Object, List<MyTable>>> list = new AtomicReference<>(new HashMap<>());
        f = Configure.getSessionAutoClose().query(MyTable.class)
                .where("age > ?",-1)
                .orderBy("name")
                .groupByAsync("name");

        f.thenAcceptAsync(objects -> {
            list.set(objects);
        });
        assertTrue(list.get().size() == 0);
        Thread.sleep(2000);
        assertTrue(list.get().size() == 10);


    }

    @Test
    public void TestGet() throws Exception {
        initConfig();


        extracted();
        MyTable myTable=Configure.getSessionAutoClose().getById(MyTable.class,5);
        assert myTable.id==5;
        Configure.getSessionAutoClose().query(MyTable.class).getByIdAsync(5).thenAcceptAsync(myTable1 -> {
            assert myTable1.id==5;
        });


        Thread.sleep(2000);


    }

    private static void extracted() throws Exception {
        ISession session = Configure.getSession();
        session.dropTableIfExists(MyTable.class);
        session.createTableIfNotExists(MyTable.class);
        for (int i = 0; i < 10; i++) {
            MyTable myTable = new MyTable();
            myTable.age = i;
            myTable.name = "name" + i;
            session.insert(myTable);
        }
    }


}
