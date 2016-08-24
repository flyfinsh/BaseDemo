package com.meilicat.basedemo.activity.usercenter;

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
 * Created by user on 2016/2/19.
 */
public class QuestionBackActivity extends BaseTitleActivity {

    private EditText backEdit;

    @Override
    public void setContent() {
        super.setContent();
        setContentView(R.layout.question_back_layout);
        setupNavigationBar(R.id.navigation_bar);
        setCommonTitle("问题反馈");

        addRightButtonText("提交", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String backText = backEdit.getText().toString();
                if(TextUtils.isEmpty(backText)) {
                    ViewUtils.showToast(getApplicationContext(), "反馈内容不能为空");
                    return;
                }

                if(backText.length() > 250) {
                    ViewUtils.showToast(getApplicationContext(), "不能超过250个字符");
                    return;
                }

                commitFeedBack(backText);
            }
        });
    }

    private void commitFeedBack(String backText) {
        showLoadDialog();

        HttpManager httpManager = new HttpManager(UIUtils.getContext()){
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
                if (bean.getMsg() == 1) {
                    ViewUtils.showToast(getApplicationContext(), "提交成功");
                    finish();
                } else {
                    ViewUtils.showToast(getApplicationContext(), bean.getMsgbox());
                }
            }

            @Override
            protected void onFail() {
                super.onFail();
                dismissLoadDialog();
                ViewUtils.showToast(getApplicationContext(), "提交反馈失败");
            }

            @Override
            protected void onTimeOut() {
                super.onTimeOut();
                dismissLoadDialog();
                ViewUtils.showToast(getApplicationContext(), "提交反馈失败");
            }
        };

        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("content", backText);
        httpManager.post(Constants.URLS.FEEDBACK, paramsMap);
    }

    @Override
    public void initView() {
        super.initView();
        backEdit = (EditText) findViewById(R.id.feedback_edit);
    }
}
