package com.meilicat.basedemo.activity.usercenter;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.meilicat.basedemo.R;
import com.meilicat.basedemo.base.BaseTitleActivity;
import com.meilicat.basedemo.bean.AppSimpleBean;
import com.meilicat.basedemo.conf.Constants;
import com.meilicat.basedemo.utils.HttpManager;
import com.meilicat.basedemo.utils.UIUtils;
import com.meilicat.basedemo.utils.ViewUtils;

import java.util.HashMap;

/**
 * Created by user on 2016/2/22.
 */
public class CommonModifyActivity extends BaseTitleActivity {

    public static final String KEY_TITLE = "key_title";
    public static final String KEY_CONTENT = "key_content";
    public static final String KEY_PARAMS = "key_params";

    private EditText modifyEdit;

    private String contentText;
    private String mParams;

    @Override
    public void setContent() {
        super.setContent();
        setContentView(R.layout.activity_common_modify_text_layout);
        setupNavigationBar(R.id.navigation_bar);

        Bundle bundle = getIntent().getExtras();
        String title = bundle.getString(KEY_TITLE);
        setCommonTitle("修改"+title);

        addRightButtonText("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String modifyContent = modifyEdit.getText().toString();
                if (TextUtils.isEmpty(modifyContent)) {
                    ViewUtils.showToast(getApplicationContext(), "修改内容为空");
                    return;
                }

                modifyAccount(modifyContent);
            }
        });

        contentText = bundle.getString(KEY_CONTENT);
        mParams = bundle.getString(KEY_PARAMS);
    }

    private void modifyAccount(final String modifyContent) {
        if(TextUtils.isEmpty(mParams)) {
            ViewUtils.showToast(getApplicationContext(), "请求参数异常");
            return;
        }

        showLoadDialog();
        HttpManager httpManager = new HttpManager(UIUtils.getContext()) {
            @Override
            protected void onSuccess(Object obj) {
                super.onSuccess(obj);
                dismissLoadDialog();
                Gson gson = new Gson();
                AppSimpleBean bean = null;
                try{
                    bean = gson.fromJson(String.valueOf(obj), AppSimpleBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (bean.getMsg() == 1){
                    ViewUtils.showToast(getApplicationContext(), "修改成功");
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(KEY_CONTENT, modifyContent);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }else {
                    ViewUtils.showToast(getApplicationContext(), "修改失败");
                }
            }

            @Override
            protected void onFail() {
                super.onFail();
                dismissLoadDialog();
                ViewUtils.showToast(getApplicationContext(), "修改失败");
            }

            @Override
            protected void onTimeOut() {
                super.onTimeOut();
                dismissLoadDialog();
                ViewUtils.showToast(getApplicationContext(), "修改失败");
            }
        };

        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put(mParams, modifyContent);
        httpManager.post(Constants.URLS.ACCOUTN_MODIFY, paramsMap);
    }

    @Override
    public void initView() {
        super.initView();
        modifyEdit = (EditText) findViewById(R.id.account_modify_edit);
        if(!TextUtils.isEmpty(contentText)) {
            modifyEdit.setText(contentText);
        }

        if("address".equals(mParams)) {
            modifyEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
        } else {
            modifyEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        }
    }
}
