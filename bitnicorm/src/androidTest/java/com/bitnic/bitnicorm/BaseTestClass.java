package com.bitnic.bitnicorm;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Base test class.
 */
public  class BaseTestClass {

    /**
     * The Use list table.
     */
    static boolean useListTable=true;
    /**
     * The Is init.
     */
    static boolean isInit=false;

    /**
     * Init config.
     */
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
