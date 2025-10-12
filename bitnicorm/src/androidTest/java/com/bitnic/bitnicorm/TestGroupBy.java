package com.bitnic.bitnicorm;

import static org.junit.Assert.assertEquals;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class TestGroupBy extends BaseTestClass{

    static class TableSerialize implements Serializable {
        public String name="ion100";
        public int age = 64;
    }

    static class TableSerializeJson  {
        public String name="ion100";
        public int age = 64;
    }

    @MapTableName("oreder_by")
     static class TableGroupBy {

        @MapPrimaryKey
         public int id;

        @MapColumn
         public String key;


        @MapColumn
         public String value;


        @MapColumn
        public TableSerialize tableSerialize;
        @MapColumn
        @MapJsonColumn
        public TableSerializeJson tableSerializeJson;

     }
    private void preInit() {

        initConfig();
        Configure.getSession().deleteRows(MyTable.class);
    }

    @Test
    public void TestGroupByCore(){
        preInit();
        ISession session= Configure.getSession();
        try {
            session.createTableIfNotExists(TableGroupBy.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        session.deleteRows(TableGroupBy.class);
        for (int i = 0; i < 3; i++) {
            var t=new TableGroupBy();
            t.key="name1";
            t.value="name1";

            session.insert(t);

        }
        for (int i = 0; i < 3; i++) {
            var t=new TableGroupBy();
            t.key="name2";
            t.value="name2";

            session.insert(t);

        }
        for (int i = 0; i < 3; i++) {
            var t=new TableGroupBy();
            t.key=null;
            t.value="name3";

            session.insert(t);

        }
        var listRaw=session.getList(TableGroupBy.class);
        var map= session.groupBy(TableGroupBy.class,"key",null);
        assertEquals(3,map.size());
        map.forEach((o, list) -> {
            assertEquals(3,list.size());
        });
    }

    @Test
    public void TestDistinctByCore(){
        preInit();
        ISession session= Configure.getSession();
        try {
            session.createTableIfNotExists(TableGroupBy.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        session.deleteRows(TableGroupBy.class);
        for (int i = 0; i < 3; i++) {
            var t=new TableGroupBy();
            t.key="name1";
            t.value="name1";
            session.insert(t);

        }
        for (int i = 0; i < 3; i++) {
            var t=new TableGroupBy();
            t.key="name2";
            t.value="name2";
            session.insert(t);

        }
        for (int i = 0; i < 3; i++) {
            var t=new TableGroupBy();
            t.key=null;
            t.value="name3";
            session.insert(t);

        }
        var list= session.distinctBy(TableGroupBy.class,"key",null);
        assertEquals(3,list.size());
        list= session.distinctBy(TableGroupBy.class,"value","value  <> ?","50");
        assertEquals(3,list.size());

    }

    @Test
    public void TestSerialize(){
        preInit();
        ISession session= Configure.getSession();
        try {
            session.createTableIfNotExists(TableGroupBy.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        session.deleteRows(TableGroupBy.class);
        var t=new TableGroupBy();
        t.key="name2";
        t.value="name2";
        t.tableSerialize=new TableSerialize();
        t.tableSerializeJson=new TableSerializeJson();
        session.insert(t);
        var o=session.firstOrDefault(TableGroupBy.class,null);
        assertEquals(64,o.tableSerializeJson.age);
        assertEquals("ion100",o.tableSerializeJson.name);
        assertEquals(64,o.tableSerialize.age);
        assertEquals("ion100",o.tableSerialize.name);

    }

    @MapTableName("t_bytes")
    static class TableBytes{
        @MapPrimaryKey
        public long id;

        @MapColumn
        public byte[] bytes;
    }


    @Test
    public void TestBytes(){

        preInit();
        ISession session= Configure.getSession();
        try {
            session.createTableIfNotExists(TableBytes.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //SELECT HEX(X'0123456789ABCDEF');
        session.deleteRows(TableBytes.class);
        TableBytes tableBytes = new TableBytes();
        tableBytes.bytes= UtilsHelper.serializeByte(new TableSerialize());
        session.insert(tableBytes);
        var o=session.firstOrDefault(TableBytes.class,null);
        assertEquals(1,1);
    }

    @Test
    public void TestBytesInsertBulk(){

        preInit();
        ISession session= Configure.getSession();
        try {
            session.createTableIfNotExists(TableGroupBy.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        session.deleteRows(TableGroupBy.class);
        //SELECT HEX(X'0123456789ABCDEF');
        var t=new TableGroupBy();
        t.key="name2";
        t.value="name2";
        t.tableSerialize=new TableSerialize();
        t.tableSerializeJson=new TableSerializeJson();
        session.insertBulk(t);
        var o=session.firstOrDefault(TableGroupBy.class,null);
        assertEquals(64,o.tableSerializeJson.age);
        assertEquals("ion100",o.tableSerializeJson.name);
        assertEquals(64,o.tableSerialize.age);
        assertEquals("ion100",o.tableSerialize.name);
    }

    @Test
    public void TestBytesTestTransaction(){

        preInit();
        ISession session= Configure.getSession();
        try {
            session.createTableIfNotExists(TableGroupBy.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        session.deleteRows(TableGroupBy.class);
        session.beginTransaction();
        var t=new TableGroupBy();
        t.key="name2";
        t.value="name2";
        t.tableSerialize=new TableSerialize();
        t.tableSerializeJson=new TableSerializeJson();
        session.insert(t);

        var t2=new TableGroupBy();
        t2.key="name2";
        t2.value="name2";
        t2.tableSerialize=new TableSerialize();
        t2.tableSerializeJson=new TableSerializeJson();
        session.insert(t2);
        session.commitTransaction();
        session.endTransaction();

        var t21=new TableGroupBy();
        t21.key="name2";
        t21.value="name2";
        t21.tableSerialize=new TableSerialize();
        t21.tableSerializeJson=new TableSerializeJson();
        session.insert(t21);
        var o=session.getList(TableGroupBy.class,null);
        assertEquals(3,o.size());


    }

    @Test
    public void TestSerialize2(){
        preInit();
        ISession session= Configure.getSession();
        try {
            session.createTableIfNotExists(TableGroupBy.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        session.deleteRows(TableGroupBy.class);
        var t=new TableGroupBy();
        t.key="name2";
        t.value="name2";
        t.tableSerialize=new TableSerialize();
        t.tableSerializeJson=new TableSerializeJson();
        session.insert(t);
        var o=session.firstOrDefault(TableGroupBy.class,null);
        assertEquals(64,o.tableSerializeJson.age);
        assertEquals("ion100",o.tableSerializeJson.name);
        assertEquals(64,o.tableSerialize.age);
        assertEquals("ion100",o.tableSerialize.name);

        o.tableSerialize=new TableSerialize();
        o.tableSerialize.age=100;
        o.tableSerialize.name="100";

        o.tableSerializeJson=new TableSerializeJson();
        o.tableSerializeJson.age=100;
        o.tableSerializeJson.name="100";
        session.update(o);
        o=session.firstOrDefault(TableGroupBy.class,null);
        assertEquals(100,o.tableSerializeJson.age);
        assertEquals("100",o.tableSerializeJson.name);
        assertEquals(100,o.tableSerialize.age);
        assertEquals("100",o.tableSerialize.name);

        var tByte=new TableSerialize();
        tByte.name="11";
        tByte.age=11;

        var tByte1=new TableSerializeJson();
        tByte1.name="11";
        tByte1.age=11;

        session.updateRows(TableGroupBy.class,new PairColumnValue()
                        .put("tableSerialize",tByte)
                        .put("tableSerializeJson",tByte1)
                ,null);
        o=session.firstOrDefault(TableGroupBy.class,null);
        assertEquals(11,o.tableSerializeJson.age);
        assertEquals("11",o.tableSerializeJson.name);
        assertEquals(11,o.tableSerialize.age);
        assertEquals("11",o.tableSerialize.name);


    }

    @Test
    public void TestSerialize3() {
        preInit();
        try (ISession session = Configure.getSession()) {
            try {
                session.createTableIfNotExists(TableGroupBy.class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            session.deleteRows(TableGroupBy.class);
            List<TableGroupBy> list = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                var t = new TableGroupBy();
                t.key = "name2";
                t.value = "name2";
                t.tableSerialize = new TableSerialize();
                t.tableSerializeJson = new TableSerializeJson();
                list.add(t);
            }
            session.insertBulk(list);
            list = session.getList(TableGroupBy.class);

            list.forEach(o -> {
                assertEquals(64,o.tableSerializeJson.age);
                assertEquals("ion100",o.tableSerializeJson.name);
                assertEquals(64,o.tableSerialize.age);
                assertEquals("ion100",o.tableSerialize.name);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
