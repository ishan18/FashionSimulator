package com.example.android.fashionsimulator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        categoryList=(RecyclerView)findViewById(R.id.category_list);
        RecyclerView.LayoutManager gridLayoutManager=new GridLayoutManager(this,2);
        categoryList.setLayoutManager(gridLayoutManager);

        ArrayList<Category> categories=new ArrayList<>();

        categories.add(new Category(BitmapFactory.decodeResource(getResources(),R.drawable.ankleboot),"Ankle Boots",9));
        categories.add(new Category(BitmapFactory.decodeResource(getResources(),R.drawable.bag),"Bags",8));
        categories.add(new Category(BitmapFactory.decodeResource(getResources(),R.drawable.coat),"Coats",4));
        categories.add(new Category(BitmapFactory.decodeResource(getResources(),R.drawable.dress),"Dresses",3));
        categories.add(new Category(BitmapFactory.decodeResource(getResources(),R.drawable.pullover),"Pullovers",2));
        categories.add(new Category(BitmapFactory.decodeResource(getResources(),R.drawable.sandal),"Sandals",5));
        categories.add(new Category(BitmapFactory.decodeResource(getResources(),R.drawable.shirt),"Shirts",6));
        categories.add(new Category(BitmapFactory.decodeResource(getResources(),R.drawable.sneakers),"Sneakers",7));
        categories.add(new Category(BitmapFactory.decodeResource(getResources(),R.drawable.trousers),"Trousers",1));
        categories.add(new Category(BitmapFactory.decodeResource(getResources(),R.drawable.tshirt),"T-Shirts/Top",0));

        DataAdapter dataAdapter=new DataAdapter(this,categories);
        categoryList.setAdapter(dataAdapter);
    }
}