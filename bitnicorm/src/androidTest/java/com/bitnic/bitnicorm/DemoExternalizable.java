package com.bitnic.bitnicorm;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class DemoExternalizable implements Externalizable {

    public int id= 100;

    public   String name = "name";

    @Override
    public void readExternal(ObjectInput in) throws ClassNotFoundException, IOException {
        id = (int) in.readObject();
        name = (String) in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(this.id);
        out.writeObject(this.name);
    }
}
