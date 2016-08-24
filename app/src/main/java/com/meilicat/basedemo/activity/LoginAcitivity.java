package com.meilicat.basedemo.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.meilicat.basedemo.R;
import com.meilicat.basedemo.base.BaseApplication;
import com.meilicat.basedemo.base.BaseAutoActivity;
import com.meilicat.basedemo.bean.LoginBean;
import com.meilicat.basedemo.bean.UserInfoBean;
import com.meilicat.basedemo.conf.Constants;
import com.meilicat.basedemo.factory.ThreadPoolProxyFactory;
import com.meilicat.basedemo.utils.HttpManager;
import com.meilicat.basedemo.utils.IntentUtil;
import com.meilicat.basedemo.utils.LogUtils;
import com.meilicat.basedemo.utils.SPUtils;
import com.meilicat.basedemo.utils.T;
import com.meilicat.basedemo.utils.UIUtils;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Response;

/**
 * Created by cj on 2016/1/21.
 */
public class LoginAcitivity extends BaseAutoActivity {

    @Bind(R.id.login_userlogo)
    CircleImageView mLoginUserlogo;
    @Bind(R.id.login_username_delete)
    ImageView mLoginUsernameDelete;
    @Bind(R.id.login_username)
    EditText mLoginUsername;
    @Bind(R.id.login_password)
    EditText mLoginPassword;
    @Bind(R.id.login_btn)
    Button mLoginBtn;

    private SPUtils mSpUtils;

    @Override
    public void setContent() {
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mSpUtils = new SPUtils(UIUtils.getContext());
        mSpUtils.putString("cookie","");//清空cookie

        initWindow();
        initData();

    }

    private SystemBarTintManager tintManager;

    @TargetApi(19)
    private void initWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintColor(getResources().getColor(R.color.login_bg_color));
            tintManager.setStatusBarTintEnabled(true);
        }

    }

    private void initData() {
        mSpUtils = new SPUtils(UIUtils.getContext());

    }

    private void readUserName() {
        String lastUserName = mSpUtils.getString("lastUserName", "");
        if (!TextUtils.isEmpty(lastUserName)){
            mLoginUsername.setText(lastUserName);
        }

    }

    private void initEvent() {

        readUserName();

        mLoginUsernameDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoginUsername.setText("");
            }
        });
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = mLoginUsername.getText().toString().trim().toUpperCase();
                final String password = mLoginPassword.getText().toString().trim();


                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    T.showShort(UIUtils.getContext(), "账号或密码不能为空");
                    return;
                }
                //TODO 这里进行账号的登录
                showLoadDialog();
                ThreadPoolProxyFactory.createNormalThreadPoolProxy().execute(new Runnable() {
                    @Override
                    public void run() {
                        HttpManager manger = new HttpManager(UIUtils.getContext());

                        Map<String, String> map = new HashMap<>();
                        map.put("suppOrPurcName", username);
                        map.put("password", password);
                        map.put("loginType",3+"");

                        LogUtils.i("username-----"+username+"------pwd----"+password);
                        try {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mLoginBtn.setText("登录中");
                                    mLoginBtn.setClickable(false);
                                }
                            });

                            Response response = manger.postLogin(Constants.URLS.LOGIN, map);
                            LogUtils.i("respone----" + response);
                            if (response != null) {
                                dismissLoadDialog();
                                String obj = response.body().string();


                                Gson gson = new Gson();
                                LoginBean bean = null;
                                try{
                                    bean = gson.fromJson(obj + "", LoginBean.class);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                 if (bean == null){
                                     runOnUiThread(new Runnable() {
                                         @Override
                                         public void run() {
                                             T.showShort(UIUtils.getContext(), "登录失败");
                                             mLoginBtn.setText("登录");
                                             mLoginBtn.setClickable(true);
                                         }
                                     });

                                     return;
                                 }


                                if (bean.msg == 0) {

                                    final LoginBean finalBean = bean;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mLoginBtn.setText("登录");
                                            mLoginBtn.setClickable(true);
                                            T.showShort(UIUtils.getContext(),  finalBean.msgbox);
                                        }
                                    });

                                    LogUtils.i( "登录失败--状态码-" + bean.msg);

                                } else {

                                    String cookies  = response.header("Set-Cookie");
                                    mSpUtils.putString("cookie", cookies);

                                    rememberUsername(username);

                                    String type = bean.data;
                                    if (type.equals("1")){
                                        //获取
                                        getUserInfo();

                                    }else{
                                        IntentUtil.startActivity(LoginAcitivity.this, BuyerHomeActivity.class);
                                        finish();
                                    }
                                    LogUtils.i("登录成功--状态码-" + bean.msg);
                                }
                                LogUtils.i( "登录结果---"+obj );

                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mLoginBtn.setText("登录");
                                        mLoginBtn.setClickable(true);
                                        T.showShort(UIUtils.getContext(), "手机网络未开启");
                                    }
                                });

                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mLoginBtn.setText("登录");
                                    dismissLoadDialog();
                                    mLoginBtn.setClickable(true);
                                    T.showShort(UIUtils.getContext(), "登录失败，网络异常");
                                }
                            });
                        }
                    }
                });

            }
        });
        mLoginUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    //如果是空的
                    mLoginUsernameDelete.setVisibility(View.GONE);
                } else {
                    mLoginUsernameDelete.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    //获取供应商的详情
    private void getUserInfo() {
        HttpManager manger = new HttpManager(UIUtils.getContext());

        try {
            String response = manger.postSync(Constants.URLS.SUPPLIER_USERINFO, "");
            Gson gson = new Gson();
            UserInfoBean userBean = null;
            try {
                userBean = gson.fromJson(response, UserInfoBean.class);
            } catch (Exception e) {
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {

                        T.showShort(UIUtils.getContext(), "获取账号信息失败，请重新登录");
                    }
                });

                e.printStackTrace();
            }
            if (userBean == null){
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        T.showShort(UIUtils.getContext(), "获取账号信息失败，请重新登录");
                    }
                });
                return;
            }
            IntentUtil.startActivity(LoginAcitivity.this, HomeActivity2.class);
            BaseApplication.getInstance().setUserInfo(userBean);

            finish();
        }catch (Exception e){

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    T.showShort(UIUtils.getContext(), "登录失败，服务器异常");
                }
            });

            e.printStackTrace();
        }

    }

    /**
     * 这是将登录成功的账户名记录下来
     * */
    private void rememberUsername(String username) {
        mSpUtils.putString("lastUserName",username);

    }


    @Override
    public void initTitle() {

    }


    @Override
    public void initView() {
        mLoginUsernameDelete.setVisibility(View.GONE);
        initEvent();
    }


}
