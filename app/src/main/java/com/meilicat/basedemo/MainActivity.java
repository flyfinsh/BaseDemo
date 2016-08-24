package com.meilicat.basedemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.meilicat.basedemo.autolayout.AutoLayoutActivity;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * 这是一个基础框架
 * 使用okhttp进行网络请求
 * 使用imageloader进行图片加载
 * 使用ButterKnife进行视图绑定
 * 对ListView进行封装
 * ListView的下拉刷新和上滑加载更多操作
 * 集成了zhy的屏幕的百分比适配，若要使用百分比适配，请将activity集成AutoLayoutActivity
 */
public class MainActivity extends AutoLayoutActivity {


    @Bind(R.id.btn)
    Button mBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


    }

    public void change(View v) {
        Intent intnet = new Intent(this, SecondActivity.class);

        startActivity(intnet);
    }


}
