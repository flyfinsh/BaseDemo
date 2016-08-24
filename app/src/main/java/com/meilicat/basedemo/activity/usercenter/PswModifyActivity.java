package com.meilicat.basedemo.activity.usercenter;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.meilicat.basedemo.R;
import com.meilicat.basedemo.activity.LoginAcitivity;
import com.meilicat.basedemo.base.BaseTitleActivity;
import com.meilicat.basedemo.bean.AppSimpleBean;
import com.meilicat.basedemo.bean.LoginBean;
import com.meilicat.basedemo.conf.Constants;
import com.meilicat.basedemo.utils.HttpManager;
import com.meilicat.basedemo.utils.IntentUtil;
import com.meilicat.basedemo.utils.T;
import com.meilicat.basedemo.utils.UIUtils;
import com.meilicat.basedemo.utils.ViewUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by user on 2016/2/22.
 */
public class PswModifyActivity extends BaseTitleActivity {

    @Bind(R.id.psw_sourch_edit)
    EditText mSourceEdit;
    @Bind(R.id.psw_new_edit)
    EditText mNewEdit;
    @Bind(R.id.psw_agin_edit)
    EditText mNewAginEdit;

    @Override
    public void setContent() {
        super.setContent();
        setContentView(R.layout.activity_modify_psw);
        ButterKnife.bind(this);
        setupNavigationBar(R.id.navigation_bar);
        setCommonTitle("修改密码");
        addRightButtonText("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sourcePsw = mSourceEdit.getText().toString();
                String newPsw = mNewEdit.getText().toString();
                String newAginPsw = mNewAginEdit.getText().toString();

                if (TextUtils.isEmpty(sourcePsw) || TextUtils.isEmpty(newPsw) || TextUtils.isEmpty(newAginPsw)) {
                    ViewUtils.showToast(getApplicationContext(), "请完善输入");
                    return;
                }

                if(sourcePsw.length() < 6 || sourcePsw.length() > 18) {
                    ViewUtils.showToast(getApplicationContext(), "密码长度应该是6-18位的数字、大小写字母组合");
                    return;
                }

                if(newPsw.length() < 6 || newPsw.length() > 18) {
                    ViewUtils.showToast(getApplicationContext(), "密码长度应该是6-18位的数字、大小写字母组合");
                    return;
                }

                if (!newPsw.equals(newAginPsw)) {
                    ViewUtils.showToast(getApplicationContext(), "两次输入的密码不一致");
                    return;
                }

                modifyPsw(sourcePsw, newPsw);
            }
        });
    }

    private void modifyPsw(String sourcePsw, String newPsw) {
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
                if (bean.getMsg() == 1) {
                    ViewUtils.showToast(getApplicationContext(), "修改成功");
                    loginOut();
                } else {
                    ViewUtils.showToast(getApplicationContext(), bean.getMsgbox());
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
        paramsMap.put("oldpassword", sourcePsw);
        paramsMap.put("password", newPsw);
        httpManager.post(Constants.URLS.PSW_MODIFY, paramsMap);
    }

    private void loginOut() {
        //退出登录
        HttpManager manger = new HttpManager(UIUtils.getContext()){
            @Override
            protected void onFail() {
                T.showShort(UIUtils.getContext(), "退出登录失败");
            }

            @Override
            protected void onSuccess(Object obj) {
                Gson gson = new Gson();
                LoginBean bean = null;
                try{
                    bean = gson.fromJson(String.valueOf(obj), LoginBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (bean.msg == 0){
                    T.showShort(UIUtils.getContext(),"退出登录失败");
                }else {
                    T.showShort(UIUtils.getContext(),"退出登录成功");
                    IntentUtil.startActivity(PswModifyActivity.this, LoginAcitivity.class);
                    finishAll();
                }
//                        LogUtils.i(obj+"");
            }
        };
        Map<String,String> map = new HashMap<>();
        manger.post(Constants.URLS.OUTLOGIN, map);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
