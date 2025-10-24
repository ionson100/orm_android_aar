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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@RunWith(AndroidJUnit4.class)
public class TestQuryable extends BaseTestClass {

    @MapTable
    static class Master {
        @MapPrimaryKey
        public int id;

        @MapColumn
        public int age=10;

        @MapColumn
        public String name="name";

        @MapColumnName("myDate")
        public LocalDateTime date = LocalDateTime.now().minusDays(1);
    }

    @Test
    public void TestWhere() throws Exception {
        initConfig();


        try (ISession session = Configure.getSession()) {


            session.query(Master.class).dropTableIfExists();
            session.query(Master.class).createTable();

            {
                Master date= new Master();
                date.date = LocalDateTime.now().minusDays(-1);
                session.insert(date);
            }
            String s=session.query(Master.class)
                    .where("age = ?",10)
                    .where("name = ?","name")
                    .orderBy("name").orderBy("age").limit(10)
                    .toString();


            List<Master> tt = session.query(Master.class)
                    .where("age = ?",10)
                    .where("name = ?","name")
                    .orderBy("name").orderBy("age").limit(10)
                    .getList();


        }


    }
}
