package com.example.contactstest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ArrayAdapter<String> adapter;
    private List<String> contactList=new ArrayList<>();
    Cursor cursor=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView= (ListView)findViewById(R.id.contact_list);
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,contactList);//加载适配器
        listView.setAdapter(adapter);//加载数据
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)!=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_CONTACTS},1);
        }else{
            readContacts();
        }

        //点击联系人跳转到发送信息页面
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String number=contactList.get(i);//获取列表数据
                Intent intent=new Intent(MainActivity.this,SendMessageActivity.class);
                intent.putExtra("phone",number);
                startActivity(intent);
            }
        });
    }

    //定义一个获取联系人姓名和电话号码的方法
    private void readContacts() {
        //查询联系人数据
        Cursor cursor=null;
        cursor=getContentResolver().query(ContactsContract.CommonDataKinds.Phone
                .CONTENT_URI,null,null,null,null);
        if (cursor!=null){
            while (cursor.moveToNext()){
                //获取联系人的名字
                @SuppressLint("Range") String displayName =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                //获取电话号码
                @SuppressLint("Range") String number =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                contactList.add(displayName+"\n"+number);//将拼接后的数据添加到ListView数据源
            }
            adapter.notifyDataSetChanged();//刷新

        }

        if (cursor!=null){
            cursor.close();//关闭cursor对象
        }
}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    readContacts();
                }
                else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
            break;
            default:
        }
    }
}