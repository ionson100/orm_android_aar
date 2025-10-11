package com.bitnic.bitnicorm;


import static org.junit.Assert.assertEquals;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.bitnic.bitnicorm.tableinherit.TableMiddle;
import com.bitnic.bitnicorm.tableinherit.TableTop;

import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * The type Test extend.
 */
@RunWith(AndroidJUnit4.class)
public class TestExtend extends BaseTestClass{

    /**
     * Test class.
     */
    @Test
    public void TestClass(){
        initConfig();
        ISession session=Configure.getSession();
        try {
            session.createTableIfNotExists(TableTop.class);
            session.createTableIfNotExists(TableMiddle.class);
            session.deleteRows(TableTop.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < 10; i++) {
            var t=new TableTop("top","ww@mail.com",20);
            session.insert(t);
        }

        var listTop=session.getList(TableTop.class);
        assertEquals(10,listTop.size());
        var listMiddle=session.getList(TableMiddle.class);
        assertEquals(10,listMiddle.size());
        var item=listMiddle.get(0);
        item.age=100;
        var  i=session.update(item);
        item=session.firstOrDefault(TableTop.class,"age = ?",100);
        assertEquals(100,item.age);

    }


}
