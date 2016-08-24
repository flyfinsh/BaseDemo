package com.meilicat.basedemo.base;

import android.os.Bundle;
import android.view.View;

import com.meilicat.basedemo.utils.NavigationBarController;
import com.meilicat.basedemo.view.NavigationBar;

/**
 * Created by lizhiming on 2016/1/23.
 */
public class BaseTitleActivity extends BaseActivity implements NavigationBar.IProvideNavigationBar {
    protected NavigationBarController mNvaigationBarMange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContent() {

    }

    @Override
    public void initTitle() {

    }

    @Override
    public void initView() {

    }

    @Override
    public NavigationBar getNavigationBar() {
        if (mNvaigationBarMange == null || mNvaigationBarMange.getNavigationBar() == null) {
            throw new RuntimeException("you may have forgotten to call setupNavigationBar!!");
        }
        return mNvaigationBarMange.getNavigationBar();
    }

    @Override
    public void setupNavigationBar(int resId) {
        NavigationBar mNavigationBar = (NavigationBar) findViewById(resId);
        if (mNavigationBar == null) {
            throw new RuntimeException("R.id.navigation_bar_ex resouce not found!!");
        }
        mNvaigationBarMange = new NavigationBarController(this, mNavigationBar);
    }

    @Override
    public void setCommonTitle(int titleId) {
        mNvaigationBarMange.setTitle(titleId);
    }

    @Override
    public void setCommonTitle(CharSequence title) {
        mNvaigationBarMange.setTitle(title);
    }

    @Override
    public View addRightButtonText(String text, View.OnClickListener listener) {
        return mNvaigationBarMange.addRightButtonText(text, listener);
    }

    @Override
    public View addRightButtonText(int resId, View.OnClickListener listener) {
        return mNvaigationBarMange.addRightButtonText(resId, listener);
    }
}
