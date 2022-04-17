package com.example.contactstest;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TakePhotoActivity extends AppCompatActivity {

    private static final int TAKE_PHOTO = 1;

    private Uri imageUri;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);
        Button button=findViewById(R.id.take_photo);
        imageView = findViewById(R.id.picture);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //创建File文件，用于存储拍照后的照片
                //getExternalCacheDir()关联缓存目录（专门存放缓存的目录）
                File outputImage=new File(getExternalCacheDir(),"output_image.jpg");

                //判断输出文件是否存在，存在则删除，删除后再创建
                try {
                    if (outputImage.exists()){
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (Build.VERSION.SDK_INT>=24){
                    imageUri= FileProvider.getUriForFile(TakePhotoActivity.this,
                            "com.example.contactstest.fileprovider",outputImage);
                }
                else {
                    imageUri= Uri.fromFile(outputImage);
                }

                //启动相机
                Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent,TAKE_PHOTO);//启动相机程序
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case TAKE_PHOTO:
                if (requestCode==RESULT_OK){
                    try {
                        Bitmap bitmap = BitmapFactory.
                                decodeStream(getContentResolver().openInputStream(imageUri));
                        imageView.setImageBitmap(bitmap);//将照片显示出来
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }
}