package com.meilicat.basedemo.activity.buyeractivity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.meilicat.basedemo.R;
import com.meilicat.basedemo.base.BaseActivity;
import com.meilicat.basedemo.bean.purchaser.BuyerSignBean;
import com.meilicat.basedemo.conf.Constants;
import com.meilicat.basedemo.factory.ThreadPoolProxyFactory;
import com.meilicat.basedemo.utils.HttpManager;
import com.meilicat.basedemo.utils.LogUtils;
import com.meilicat.basedemo.utils.T;
import com.meilicat.basedemo.utils.UIUtils;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BuyerUnhandlerConfirmActivity extends BaseActivity {
    @Bind(R.id.back_button)
    RelativeLayout mBackButton;
    @Bind(R.id.confirm_button)
    TextView mConfirmButton;
    @Bind(R.id.confirm_text)
    EditText mConfirmText;
    private int mDetail;
    private int mPosition;
    private String mDate;
    private String mSupplierId;

    @Override
    public void setContent() {
        setContentView(R.layout.activity_confirm_handler);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mDetail = intent.getIntExtra("detail", 0);
        mPosition = intent.getIntExtra("position", -1);
        mDate = intent.getStringExtra("date");
        mSupplierId = intent.getStringExtra("supplierId");

    }

    @Override
    public void initTitle() {
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String signInfo = mConfirmText.getText().toString().trim();

                ThreadPoolProxyFactory.createNormalThreadPoolProxy().execute(new Runnable() {
                    @Override
                    public void run() {
                        boolean comfirm = signComfirm(signInfo);

                        if (comfirm){
                            Intent intent =getIntent();
                            intent.putExtra("remark",signInfo );
                            intent.putExtra("position", mPosition);
                            intent.putExtra("date",mDate);
                            intent.putExtra("supplierId",mSupplierId);

                            setResult(2, intent);
                            finish();
                        }else {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    T.showShort(UIUtils.getContext(), "签收失败，请重试");
                                }
                            });

                        }
                    }
                });

            }
        });
    }


    private boolean signComfirm(String info) {

        HttpManager manager = new HttpManager(UIUtils.getContext());
        String url;
        if (TextUtils.isEmpty(info)){
            url = Constants.URLS.BUYER_SIGN+"id="+mDetail;
        }else {
            LogUtils.i("有签收备注-------"+info);
            url = Constants.URLS.BUYER_SIGN+"id="+mDetail+"&comment="+info;
            LogUtils.i("comfirm---url----"+url);
        }

        try {
            String response = manager.run(url);

            if (!TextUtils.isEmpty(response)){
                Gson gson = new Gson();
                BuyerSignBean signBean = null;
                try{
                    signBean = gson.fromJson(response, BuyerSignBean.class);
                }catch (Exception e){
                    e.printStackTrace();
                }
                if (signBean == null){
                    return false;
                }

                if (signBean.msg == 0){
                    return false;
                }else {
                    return true;
                }

            }else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();

            return false;
        }


    }

    @Override
    public void initView() {

    }


}
