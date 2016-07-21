package com.mrpeng.chatdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //        new Thread(new Runnable() {
//            @Override
//            public void run()
//            {
//                //处理表情数据，使之分页封装完毕
//                FaceConversionUtils.getIntance().getFileText(getApplication());
//            }
//        }).start();

        new Thread(){
            @Override
            public void run()
            {
                super.run();
            }
        }.start();

        FaceConversionUtils.getIntance().getFileText(getApplication());

        findViewById(R.id.btn_chat).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });
    }
}
