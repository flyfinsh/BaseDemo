package com.meilicat.basedemo.activity.usercenter;

import android.content.Intent;
import android.view.View;

import com.google.gson.Gson;
import com.meilicat.basedemo.R;
import com.meilicat.basedemo.base.BaseTitleActivity;
import com.meilicat.basedemo.bean.AppSimpleBean;
import com.meilicat.basedemo.conf.Constants;
import com.meilicat.basedemo.utils.DialogUtils;
import com.meilicat.basedemo.utils.HttpManager;
import com.meilicat.basedemo.utils.UIUtils;
import com.meilicat.basedemo.utils.ViewUtils;
import com.meilicat.basedemo.view.UIItem;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by user on 2016/2/22.
 */
public class SettingActivity extends BaseTitleActivity implements View.OnClickListener {

    private String mCacheSize;

    @Bind(R.id.settings_clear_cache_item)
    UIItem mClearItem;
    @Bind(R.id.settings_check_version_item)
    UIItem mCheckItem;
    @Bind(R.id.settings_about_item)
    UIItem mAboutItem;

    private String mNewVersionUrl;

    @Override
    public void setContent() {
        super.setContent();
        setContentView(R.layout.user_settings_layout);
        ButterKnife.bind(this);

        setupNavigationBar(R.id.navigation_bar);
        setCommonTitle("设置");
    }

    @Override
    public void initView() {
        super.initView();
        try {
            mCacheSize = DataCleanManager.getTotalCacheSize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mClearItem.setRightText(mCacheSize);
        mClearItem.setOnClickListener(this);
        mCheckItem.setOnClickListener(this);
        mAboutItem.setOnClickListener(this);

        checkVersion();
    }

    private void checkVersion() {
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
                    mCheckItem.getRightTextView().setText("可升级至最新版");
                    mNewVersionUrl = bean.getData();
                } else {
                    mCheckItem.getRightTextView().setText("无更新版本");
                }
            }

            @Override
            protected void onFail() {
                super.onFail();
                dismissLoadDialog();
                ViewUtils.showToast(UIUtils.getContext(), "获取版本信息失败");
            }

            @Override
            protected void onTimeOut() {
                super.onTimeOut();
                dismissLoadDialog();
                ViewUtils.showToast(UIUtils.getContext(), "获取版本信息失败");
            }
        };

        httpManager.get(Constants.URLS.CHECK_VERSION + "version=" + ViewUtils.getVersionName());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.settings_clear_cache_item:
                if ("0KB".equals(mCacheSize)) {
                    ViewUtils.showToast(getApplicationContext(), "手机清洁如新，无需再次清理哦");
                    return;
                }

                DataCleanManager.clearAllCache(this);
                ViewUtils.showToast(getApplicationContext(), "成功清理" + mCacheSize + "缓存");
                mClearItem.setRightText("0KB");
                mCacheSize = "0KB";
                break;
            case R.id.settings_check_version_item:
                DialogUtils.showCheckVersionDialog(this, mNewVersionUrl);
                break;
            case R.id.settings_about_item:
                Intent aboutIntent = new Intent(this, WebviewActivity.class);
                aboutIntent.putExtra(WebviewActivity.KEY_TITLE, "关于美丽猫");
                aboutIntent.putExtra(WebviewActivity.KEY_URL, "http://www.baidu.com");
                startActivity(aboutIntent);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
