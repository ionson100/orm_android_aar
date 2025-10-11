package com.bitnic.bitnicorm;

import android.os.Parcel;
import android.os.Parcelable;

public class MyObject implements Parcelable   {
    public int id;



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
    }

    protected MyObject(Parcel in) {
        this.id = in.readInt();
    }

    public static final Creator<MyObject> CREATOR = new Creator<MyObject>() {
        @Override
        public MyObject createFromParcel(Parcel source) {
            return new MyObject(source);
        }

        @Override
        public MyObject[] newArray(int size) {
            return new MyObject[size];
        }
    };
}
