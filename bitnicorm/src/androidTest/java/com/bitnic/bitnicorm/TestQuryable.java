package com.bitnic.bitnicorm;


import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

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
        @MapColumn
        public LocalDateTime dateTime = LocalDateTime.now().minusDays(1);
    }
    static class PartialMaster {
        public int id;
        public int age;
    }

    @Test
    public void TestWhere() throws Exception {
        initConfig();


        try (ISession session = Configure.getSession()) {
            session.query(Master.class).dropTableIfExists();
            session.query(Master.class).createTable();

            List<Master>  list=new ArrayList<>(20);
            for (int i = 0; i < 20; i++) {
                Master master=new Master();
                master.age=i;
                master.dateTime=LocalDateTime.now().plusDays(i);
                master.name="name"+i;
                list.add(master);

            }
            session.insertBulk(list);

            List<PartialMaster> listT=session.query(PartialMaster.class).rawSqlSelect("select id age from "+session.getTableName(Master.class)).toList();
            assert listT.size()==20;

            List<Master> listT3=session.query(Master.class).rawSqlSelect("select * from "+session.getTableName(Master.class))
                    .where("name not null").where("age > ?",-1).orderBy("name").toList();
            assert listT3.size()==20;

            String sql= session.query(Master.class)
                    .rawSqlSelect("select * from "+session.getTableName(Master.class))
                    .where("name not null")
                    .where("age > ?",-1)
                    .orderBy("name").toString();
            Log.i("____sql____",sql);

            session.query(PartialMaster.class).rawSqlSelect("select * from "+session.getTableName(Master.class))
                    .where("name not null").orderBy("age")
                    .iterator(master -> Log.i("____age_____",String.valueOf(master.age)));

            List<Integer> integers=new ArrayList<>();
            session.query(Master.class).where(" name not null").iterator(master -> integers.add(master.age));
            assert integers.size()==20;

            int count= session.query(Master.class).where("name not null").where("age > ? ",5).orderBy("mame").count();
            assert count==14;

            var o=session.query(Master.class)
                    .where(" name = ?","name5")
                    .where("age==?",5)
                    .orderBy("name")
                    .orderBy("age")
                    .limit(10).toList();
            assert o.size()==1;

            o=session.query(Master.class).limitOffSet(3,5).toList();
            assert o.size()==3;

            var r=session.query(Master.class).where("dateTime > ?",LocalDateTime.now().plusDays(5)).firstOrDefault();
            assert r!=null;

            r=session.query(Master.class).where("dateTime > ?",LocalDateTime.now().plusDays(50)).firstOrDefault();
            assert r==null;
            r=session.query(Master.class).where("dateTime > ?",LocalDateTime.now().plusDays(5)).singleOrDefault();
            assert r==null;
            //r=session.query(Master.class).where("dateTime > ?",LocalDateTime.now().plusDays(50)).single(); //Error

            var t=session.query(Master.class).groupBy("name");
            assert t.size()==20;

            var names=session.query(Master.class).distinctBy("name");
            assert t.size()==20;

            String tempSql="select * from "+session.getTableName(Master.class);
            var listTemp=session.query(PartialMaster.class).rawSqlSelect(tempSql).where("age > ?",-1).toList();
            assert listTemp.size()==20;

            var any=session.query(Master.class).where("age < 0").any();
            assert any==false;

        }


    }
}
