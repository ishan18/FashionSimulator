package com.example.android.fashionsimulator;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Category> categoryArrayList;

    DataAdapter(Context context,ArrayList<Category> categories){
        this.context=context;
        this.categoryArrayList=categories;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageButton imageButton;
        TextView label;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageButton=(ImageButton)itemView.findViewById(R.id.image);
            label=(TextView)itemView.findViewById(R.id.label);
        }

        public ImageButton getImageButton() {
            return imageButton;
        }

        public TextView getLabel() {
            return label;
        }
    }

    @NonNull
    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final ViewHolder viewHolder=new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item,parent,false));

        viewHolder.getImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Category category=categoryArrayList.get(viewHolder.getAdapterPosition());
                if(category.getCode()>=0){
                    Intent intent=new Intent(context,GeneratorActivity.class);
                    intent.putExtra("Code",category.getCode());
                    context.startActivity(intent);
                }
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DataAdapter.ViewHolder holder, int position) {

        Category category=categoryArrayList.get(holder.getAdapterPosition());

        Bitmap bitmap=category.getPicture();
        if(category.getCode()>=0){
            bitmap= Bitmap.createScaledBitmap(bitmap,300,500,true);
        }
        holder.getImageButton().setImageBitmap(bitmap);
        holder.getLabel().setText(category.getName());
    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }
}
