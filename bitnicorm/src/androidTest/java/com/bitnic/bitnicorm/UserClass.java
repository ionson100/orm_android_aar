package com.bitnic.bitnicorm;


import com.google.gson.Gson;

/**
 * The type User class.
 */
public class UserClass implements IUserType
{

    /**
     * The Name.
     */
    public String name;
    /**
     * The Age.
     */
    public int age;
    @Override
    public void initBody(String str) {
        Gson gson=new Gson();
        UserClass inner= gson.fromJson(str, UserClass.class);
        name=inner.name;
        age=inner.age;
    }

    @Override
    public String getString() {
        Gson gson=new Gson();
       return gson.toJson(this);

    }
}
