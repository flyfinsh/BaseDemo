package com.meilicat.basedemo.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.meilicat.basedemo.utils.UIUtils;

/**
 * Created by Administrator on 2015/12/17.
 * 这是三个Fragment的基页面
 */
public abstract class BaseFragment<ACTIVITY_TYPE> extends Fragment {

    protected ACTIVITY_TYPE mContext;
    public View mRootView;
    public LoadingPager mLoadingPager;



    /**
     * 这是重写的Fragment的创建View的方法
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        mContext = (ACTIVITY_TYPE) getActivity();

        if (mLoadingPager == null) {

            mLoadingPager = new LoadingPager(UIUtils.getContext()) {
                @Override
                public LoadedResult initData() {
                    return BaseFragment.this.initData();
                }

                @Override
                public View initSuccessView() {
                    return BaseFragment.this.initSuccessView();
                }
            };

        }
        mLoadingPager.triggerLoadData();

        return mLoadingPager;
    }

    protected void refreshLoadingPagerUi(LoadingPager.LoadedResult state) {
        mLoadingPager.mCurState = state.getState();
        mLoadingPager.refreshUIByState();
    }


    /**
     * 这是初始化成功视图的方法
     */
    public abstract View initSuccessView();


    /**
     * 这是初始化数据的方法,子类必须实现
     */
    public abstract LoadingPager.LoadedResult initData();


}
