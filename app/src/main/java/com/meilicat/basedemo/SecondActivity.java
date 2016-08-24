package com.meilicat.basedemo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by cj on 2016/1/21.
 */
public class SecondActivity extends Activity {

    @Bind(R.id.btn)
    Button mBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
//        https://git.oschina.net/vwei/mapp-supplier-android.git
    }
}
