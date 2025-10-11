package com.bitnic.bitnicorm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest extends BaseTestClass {


    /**
     * Use app context.
     */
    @Test
    public void useAppContext() {
        // Context of the app under test.
        initConfig();
        ISession session=Configure.getSession();
        session.deleteRows(MyTable.class);
        {
            MyTable myTable=new MyTable();
            myTable.name="simple";
            myTable.aByte=8;
            session.insert(myTable);
        }
        {
            MyTable myTable=new MyTable();
            myTable.name="simple";
            myTable.aByte=8;
            session.insert(myTable);

        }
        List<MyTable> list=session.getList(MyTable.class);
        assertEquals(2,list.size());
    }

    /**
     * Test delete.
     */
    @Test
    public void testDelete(){
        initConfig();
        ISession session=Configure.getSession();
        MyTable myTable=new MyTable();
        myTable.name="simple";
        myTable.aByte=8;
        session.insert(myTable);
        session.deleteRows(MyTable.class);
        List<MyTable> list=session.getList(MyTable.class);
        assertEquals(0,list.size());
    }

    /**
     * Test delete where.
     */
    @Test
    public void testDeleteWhere(){
        initConfig();
        ISession session=Configure.getSession();
        MyTable myTable=new MyTable();
        myTable.name="simple";
        myTable.aByte=8;
        session.insert(myTable);
        session.deleteRows(MyTable.class);
        List<MyTable> list=session.getList(MyTable.class);
        assertEquals(0, list.size());
    }


    /**
     * Test insert bulk list.
     */
    @Test
    public void testInsertBulkList(){
        initConfig();
        ISession session=Configure.getSession();
        session.deleteRows(MyTable.class);


        List<MyTable> list=new ArrayList<>();
        int item=400;
        for (int i = 0; i < item ; i++) {
            MyTable myTable=new MyTable();
            myTable.name="simple:"+i;
            myTable.aByte=8;
            myTable.myData= new Date();
            myTable.aFloat=1.2f;
            myTable.aDouble=1.2d;
            myTable.aShort=3;
            myTable.longs=1111111L;
            myTable.inte=1000;
            list.add(myTable);
        }
        //Configure.bulk();

        session.insertBulk(list);
        List<MyTable> listRes=session.getList(MyTable.class);
        assertEquals(item,listRes.size());


    }

    /**
     * Test count.
     */
    @Test
    public void testCount(){
        initConfig();
        ISession session=Configure.getSession();
        session.deleteRows(MyTable.class);


        List<MyTable> list=new ArrayList<>();
        for (int i = 0; i < 12 ; i++) {
            MyTable myTable=new MyTable();
            myTable.name="simple:"+i;
            myTable.aByte=8;
            myTable.myData= new Date();
            list.add(myTable);
        }
        //Configure.bulk();

        session.insertBulk(list);

        int count= session.count(MyTable.class);
        assertEquals(12,count);
        count= session.count(MyTable.class," name = ?","simple:1");
        assertEquals(1,count);
    }

    /**
     * Test any.
     */
    @Test
    public void testAny(){
        initConfig();
        ISession session=Configure.getSession();
        session.deleteRows(MyTable.class);


        List<MyTable> list=new ArrayList<>();
        for (int i = 0; i < 10 ; i++) {
            MyTable myTable=new MyTable();
            myTable.name="simple:"+i;
            myTable.aByte=8;
            myTable.myData= new Date();
            list.add(myTable);
        }
        //Configure.bulk();

        session.insertBulk(list);

        boolean res1=session.any(MyTable.class);
        assertTrue(res1);
        boolean res2=session.any(MyTable.class,"name=?","simple:1");
        assertTrue(res2);
    }


    /**
     * Test first or default update.
     */
    @Test
    public void testFirstOrDefaultUpdate(){
        initConfig();
        ISession session=Configure.getSession();
        session.deleteRows(MyTable.class);

        List<MyTable> list=new ArrayList<>();
        for (int i = 0; i < 10 ; i++) {
            MyTable myTable=new MyTable();
            myTable.name="simple:"+i;
            myTable.aByte=8;
            myTable.myData= new Date();
            list.add(myTable);
        }
        session.insertBulk(list);
        List<MyTable> list1=session.getList(MyTable.class);

        MyTable myTable=session.firstOrDefault(MyTable.class," name = 'simple:1'");
        assertEquals("simple:1",myTable.name);
        myTable.name="222";
        session.update(myTable);


        myTable=session.firstOrDefault(MyTable.class," name = ?","222");
        assertEquals("222",myTable.name);
    }

    /**
     * Test single not ok.
     */
    @Test
    public void testSingleNotOk()  {
        initConfig();
        ISession session=Configure.getSession();
        MyTable myTable=new MyTable();
        myTable.name="simple";
        myTable.aByte=8;
        session.insert(myTable);
        try {
            MyTable  myTable1=session.single(MyTable.class,"id=10");
            assertEquals(2,1);
        } catch (Exception e) {
            assertEquals(1,1);
        }

    }

    /**
     * Test single ok.
     */
    public void testSingleOk() {
        initConfig();
        ISession session=Configure.getSession();
        MyTable myTable=new MyTable();
        myTable.name="simple";
        myTable.aByte=8;
        session.insert(myTable);
        try {
            MyTable  myTable1=session.single(MyTable.class,"id=?",myTable.id);
            assertNotNull(myTable1);
            assertEquals(1,1);
        } catch (Exception e) {
            assertEquals(2,1);
        }

    }

    /**
     * Test single or default ok.
     */
    @Test
    public void testSingleOrDefaultOk(){
        initConfig();
        ISession session=Configure.getSession();
        session.deleteRows(MyTable.class);
        MyTable myTable=new MyTable();
        myTable.name="simple";
        myTable.aByte=8;
        session.insert(myTable);

        MyTable  myTable1=session.singleOrDefault(MyTable.class,"name=?",myTable.name);
        assertNotNull(myTable1);
    }

    /**
     * Test single or default not ok.
     */
    public void testSingleOrDefaultNotOk(){
        initConfig();
        ISession session=Configure.getSession();
        session.deleteRows(MyTable.class);
        MyTable myTable=new MyTable();
        myTable.name="simple";
        myTable.aByte=8;
        session.insert(myTable);

        MyTable  myTable1=session.singleOrDefault(MyTable.class,"id=?",10);
        assertNull(myTable1);
    }

    /**
     * Test first ok.
     */
    @Test
    public void testFirstOk() {
        initConfig();
        ISession session=Configure.getSession();
        session.deleteRows(MyTable.class);
        MyTable myTable=new MyTable();
        myTable.name="simple";
        myTable.aByte=8;
        session.insert(myTable);
        try {
            MyTable  myTable1=session.first(MyTable.class,"name=?",myTable.name);
            assertTrue(myTable1 != null);
        } catch (Exception e) {
            assertEquals(2,1);
        }
    }

    /**
     * Test first not ok.
     */
    @Test
    public void testFirstNotOk() {
        initConfig();
        ISession session=Configure.getSession();
        session.deleteRows(MyTable.class);
        MyTable myTable=new MyTable();
        myTable.name="simple";
        myTable.aByte=8;
        session.insert(myTable);
        try {
            MyTable  myTable1=session.first(MyTable.class,"name='10'");
            fail();
        } catch (Exception e) {
            assertEquals(1,1);
        }
    }

    /**
     * Test select list.
     */
    @Test
    public void testSelectList() {
        initConfig();
        ISession session=Configure.getSession();
        session.deleteRows(MyTable.class);


        List<MyTable> list=new ArrayList<>();
        for (int i = 0; i < 2 ; i++) {
            MyTable myTable=new MyTable();
            myTable.name="simple:"+i;
            myTable.aByte=8;
            myTable.myData= new Date();
            list.add(myTable);
        }
        session.insertBulk(list);
        List<Object> list1=session.getListSelect(MyTable.class,"name",null);
        assertEquals(2,list1.size());
    }

    /**
     * Test user table.
     */
    @Test
    public void testUserTable(){
        initConfig();
        ISession session=Configure.getSession();
        try {
            session.createTableIfNotExists(TableUser.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var item=11;
        for (int i = 0; i < item; i++) {
            TableUser tableUser=new TableUser();
            tableUser.userClass=new UserClass();
            tableUser.userClass.name="111";
            tableUser.address= MessageFormat.format("sity-{0}", i);
            session.insert(tableUser);
        }


        List<UserClass>tableUsers1=session.getListSelect(TableUser.class,"user",null);
        assertEquals(item,tableUsers1.size());
    }


}