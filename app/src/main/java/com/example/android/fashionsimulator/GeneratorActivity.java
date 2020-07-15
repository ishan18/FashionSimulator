package com.example.android.fashionsimulator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Random;

public class GeneratorActivity extends AppCompatActivity {

    RecyclerView imageList;
    int code;
    ArrayList<Category> categories;
    DataAdapter dataAdapter;
    Interpreter interpreter;
    String label="Shoes";
    final int STORAGE_PERMISSION_REQUEST=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generator);

        imageList=(RecyclerView)findViewById(R.id.image_list);
        RecyclerView.LayoutManager gridLM=new GridLayoutManager(this,4);
        imageList.setLayoutManager(gridLM);

        try {
            interpreter = new Interpreter(loadModel());
        } catch (IOException e) {
            e.printStackTrace();
        }

        code=getIntent().getIntExtra("Code",0);
        switch (code){
            case 0:
                label="T-shirt/Top";
                break;
            case 1:
                label="Trousers";
                break;
            case 2:
                label="Pullovers";
                break;
            case 3:
                label="Dresses";
                break;
            case 4:
                label="Coats";
                break;
            case 5:
                label="Sandals";
                break;
            case 6:
                label="Shirts";
                break;
            case 7:
                label="Sneakers";
                break;
            case 8:
                label="Bags";
                break;
            case 9:
                label="Ankle boots";
                break;
        }
        try {
            getSupportActionBar().setTitle(label);
        }catch (NullPointerException e){}

        categories=new ArrayList<>();
        dataAdapter=new DataAdapter(this,categories);

        imageList.setAdapter(dataAdapter);

        new AsyncLoadImage().execute();
    }
    private class AsyncLoadImage extends AsyncTask<Void,Void, Category>{
        @Override
        protected Category doInBackground(Void... voids) {
            ByteBuffer input=ByteBuffer.allocateDirect(110*4).order(ByteOrder.nativeOrder());
            FloatBuffer fb=input.asFloatBuffer();
            Random rd=new Random();
            float[] inputf=new float[110];
            for(int i=0;i<100;i++)
                inputf[i]= (float) ((rd.nextFloat()-0.5)*6);
            for(int i=100;i<110;i++)
                inputf[i]=0;
            inputf[100+code]=1;
            fb.put(inputf);
            ByteBuffer modelOutput=ByteBuffer.allocateDirect(28*28*java.lang.Float.SIZE / java.lang.Byte.SIZE).order(ByteOrder.nativeOrder());
            interpreter.run(input,modelOutput);

            modelOutput.rewind();
            FloatBuffer pixels=modelOutput.asFloatBuffer();
            int[] output=new int[pixels.capacity()];
            Bitmap bitmap=Bitmap.createBitmap(28,28,Bitmap.Config.RGB_565);

            for(int i=0;i<pixels.capacity();i++){
                output[i]= (int) (pixels.get(i)*255);
                int x=Math.abs(output[i]);
                x=255-x;
                bitmap.setPixel(i%28,i/28,Color.rgb(x,x,x));
            }

            bitmap=Bitmap.createScaledBitmap(bitmap,200,200,true);

            Category image=new Category(bitmap);
            return image;
        }

        @Override
        protected void onPostExecute(Category image) {
            super.onPostExecute(image);
            categories.add(image);
            dataAdapter.notifyItemInserted(dataAdapter.getItemCount()-1);
        }
    }

    MappedByteBuffer loadModel() throws IOException {
        AssetFileDescriptor fileDescriptor = getAssets().openFd("fashion_mnist_gan.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();

        long startOFFSet = fileDescriptor.getStartOffset(), declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOFFSet, declaredLength);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.images_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        switch (id){
            case R.id.insert:
                new AsyncLoadImage().execute();
                break;
            case R.id.save:
                if(ContextCompat.checkSelfPermission(GeneratorActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED){
                    save_images();
                }else {
                    ActivityCompat.requestPermissions(GeneratorActivity.this,new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_PERMISSION_REQUEST);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==STORAGE_PERMISSION_REQUEST){
            if(permissions.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                save_images();
        }
    }

    private void save_images() {
        String path= Environment.getExternalStorageDirectory().toString();
        File myDir=new File(path+"/Fashion");
        myDir.mkdirs();
        File catFile=new File(myDir,label);
        catFile.mkdirs();
        boolean check=true;
        for(int i=0;i<categories.size();i++){
            Category category=categories.get(i);

            Random rd=new Random();
            String fname=String.valueOf(rd.nextInt())+" "+(i+1)+".jpg";
            Bitmap bitmap=category.getPicture();
            File fileName=new File(catFile,fname);
            if(fileName.exists())
                fileName.delete();
            try {
                FileOutputStream out=new FileOutputStream(fileName);
                bitmap.compress(Bitmap.CompressFormat.JPEG,90,out);
                out.flush();
                out.close();

                MediaStore.Images.Media.insertImage(getContentResolver(),fileName.getAbsolutePath(),fileName.getName(),fileName.getName());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                check=false;
            } catch (IOException e) {
                e.printStackTrace();
                check=false;
            }
        }
        if(check)
            Toast.makeText(GeneratorActivity.this,"Saved at "+path,Toast.LENGTH_SHORT).show();
    }
}