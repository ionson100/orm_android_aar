package com.bitnic.bitnicorm;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import java.util.ArrayList;
import java.util.List;

public  class BaseTestClass {

    static boolean useListTable=true;
    static boolean isInit=false;

    protected void initConfig(){
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        if(!isInit){
            if(!useListTable){

                new Configure("assa",3,appContext,true);
                ISession session=Configure.getSession();
                try {
                    session.createTableIfNotExists(MyTable.class);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }else{
                List<Class> classList=new ArrayList<>();
                classList.add(MyTable.class);
                new Configure("assa",3,appContext,classList,true);

            }
            isInit=true;
        }

    }
}
