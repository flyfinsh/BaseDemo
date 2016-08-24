package com.meilicat.basedemo.activity.usercenter;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.meilicat.basedemo.R;
import com.meilicat.basedemo.adapter.CommonAdapter;
import com.meilicat.basedemo.base.BaseTitleActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2016/2/22.
 */
public class BankListActivity extends BaseTitleActivity {

    public static final String KEY_NAME = "key_name";
    public static final String KEY_NUM = "key_num";

    private CommonAdapter mAdapter;

    private String mBankName;
    private String mBankNumber;

    @Override
    public void setContent() {
        super.setContent();
        setContentView(R.layout.activity_banklist_layout);
        setupNavigationBar(R.id.navigation_bar);
        setCommonTitle("提现银行卡");

        Bundle bundle = getIntent().getExtras();
        mBankName = bundle.getString(KEY_NAME);
        mBankNumber = bundle.getString(KEY_NUM);
    }

    @Override
    public void initView() {
        super.initView();
        ListView listview = (ListView) findViewById(R.id.bank_listview);
        mAdapter  = new CommonAdapter<String>(this, R.layout.item_bank_list) {

            @Override
            protected void convert(ViewHolderEntity entity, String itemData, int itemViewType) {
                TextView nameTv = entity.getView(R.id.bank_name_tv);
                TextView numTv = entity.getView(R.id.bank_num_tv);
                if("1".equals(itemData)) {
                    nameTv.setText(mBankName);
                    numTv.setText(mBankNumber);
                }
            }
        };

        listview.setAdapter(mAdapter);
        List<String> testList = new ArrayList<>();
        testList.add("1");
        mAdapter.setData(testList);
    }
}
