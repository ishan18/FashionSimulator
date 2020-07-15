package com.example.android.fashionsimulator;

import android.graphics.Bitmap;

public class Category {
    Bitmap picture;
    String name;
    int code;

    Category(Bitmap bitmap,String string,int c){
        this.picture=bitmap;
        this.name=string;
        this.code=c;
    }
    Category(Bitmap bitmap){
        this.picture=bitmap;
        name="";
        code=-5;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
