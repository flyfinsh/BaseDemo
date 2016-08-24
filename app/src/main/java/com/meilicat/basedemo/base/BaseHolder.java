package com.meilicat.basedemo.base;

import android.view.View;

/**
 *这是一个控制基类
 */
public abstract class BaseHolder<HOLDERBEANTYPE> {

    public View mRootView;
    public HOLDERBEANTYPE mData;

    public BaseHolder() {
        mRootView = initHolderView();


        mRootView.setTag(this);
    }

    /**
     * 接收数据,然后进行数据和视图的绑定
     */
    public void setDataAndRefreshHolderView(HOLDERBEANTYPE data) {
        // 保存传递过来的数据到成员变量
        mData = data;

        refreshHolderView(data);

    }


    /**
     * 初始化持有的视图
     * 在BaseHolder里面不知道如何具体的初始化持有的视图
     * 必须实现但是不知道具体实现,定义成为抽象方法,交给子类具体实现
     *
     * @return
     */
    public abstract View initHolderView();

    /**
     * 进行数据和视图的绑定
     * 在BaseHolder里面不知道如何具体的初始化持有的视图
     * 必须实现但是不知道具体实现,定义成为抽象方法,交给子类具体实现
     *
     * @param data
     */
    public abstract void refreshHolderView(HOLDERBEANTYPE data);


    public void initView() {
    }

    public void initView(int position) {
        initView();
    }
}
