package com.meilicat.basedemo.fragment.commodity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.google.gson.Gson;
import com.meilicat.basedemo.R;
import com.meilicat.basedemo.activity.commonditymanager.CommondityDetailsActivity;
import com.meilicat.basedemo.adapter.CommodityManagerAdapter;
import com.meilicat.basedemo.base.BaseActivity;
import com.meilicat.basedemo.bean.GoodsDataEntity;
import com.meilicat.basedemo.bean.GoodsManagerListBean;
import com.meilicat.basedemo.bean.GoodsRowsEntity;
import com.meilicat.basedemo.conf.Constants;
import com.meilicat.basedemo.event.BusProvider;
import com.meilicat.basedemo.event.NotifyUpdateEvent;
import com.meilicat.basedemo.event.NotifyUpdateHomeEvent;
import com.meilicat.basedemo.utils.HttpManager;
import com.meilicat.basedemo.utils.UIUtils;
import com.meilicat.basedemo.utils.ViewUtils;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * Created by lizhiming on 2016/1/25.
 */
@SuppressWarnings("deprecation")
public class CommodityManagerFragment extends Fragment {

    private BaseActivity mBaseAct;

    public static final String REQUEST_TYPE = "request_type";
    public static final String TYPE_PUTAWAY_ALREADY = "1"; // 已上架
    public static final String TYPE_PUTAWAY_WAIT = "2"; // 待上架
    public static final String TYPE_SOLD_OUT = "3"; // 已下架

    private String mRequestType = TYPE_PUTAWAY_ALREADY;
    private String onlineStatu = "1";

    private MaterialRefreshLayout mRefreshLayout;
    private GridView mGridView;

    private TextView mEmptyTv;
    private String mEmptyText;

    private CommodityManagerAdapter mAdapter;

    private boolean isWaitStatu;

    private int mCurrentPage = 1;

    private boolean isHaveRegister;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCurrentPage = 1;
        if (activity instanceof BaseActivity) {
            mBaseAct = (BaseActivity) activity;
        }

        if (!isHaveRegister) {
            BusProvider.getInstance().register(this);
            isHaveRegister = true;
        }

        mRequestType = getArguments().getString(REQUEST_TYPE);
        if (TYPE_PUTAWAY_WAIT.equals(mRequestType)) {
            isWaitStatu = true;
            mEmptyText = "暂时无待上架商品";
        } else {
            isWaitStatu = false;
            if (TYPE_PUTAWAY_ALREADY.equals(mRequestType)) {
                onlineStatu = "1";
                mEmptyText = "暂时无商品上架";
            } else {
                onlineStatu = "2"; //已下架
                mEmptyText = "暂时无商品下架";
            }
        }

        mAdapter = new CommodityManagerAdapter(getActivity(), R.layout.commodity_list_item, mRequestType);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.commodity_manager_fragment_layout, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        getListData(false);
    }

    /**
     * 获取列表数据
     * supplierId=40&
     * supplierId=42&
     * supplierId=39&
     */
    private void getListData(boolean isLoadMore) {
        if (!isLoadMore) {
            mCurrentPage = 1;
            mBaseAct.showLoadDialog();
        }

        String requestUrl;
        if (isWaitStatu) {
            requestUrl = Constants.URLS.GOODS_WAIT_ONLINE + "page=" + mCurrentPage;
        } else {
            if ("1".equals(onlineStatu)) {
                requestUrl = Constants.URLS.GOODS_UP_DOWN_ONLINE + "status=" + onlineStatu + "&page=" + mCurrentPage;
            } else {
                requestUrl = Constants.URLS.GOODS_UP_DOWN_ONLINE + "status=" + onlineStatu + "&page=" + mCurrentPage;
            }
        }

        HttpManager httpManager = new HttpManager(UIUtils.getContext()) {
            @Override
            protected void onSuccess(Object obj) {
                super.onSuccess(obj);
                mBaseAct.dismissLoadDialog();
                mRefreshLayout.finishRefresh();
                mRefreshLayout.finishRefreshLoadMore();

                Gson gson = new Gson();
                GoodsManagerListBean bean = null;
                try{
                    bean = gson.fromJson(String.valueOf(obj), GoodsManagerListBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (bean == null || bean.getData() == null) {
                    if(mCurrentPage == 1) {
                        mEmptyTv.setVisibility(View.VISIBLE);
                        mGridView.setVisibility(View.GONE);
                    }
                    ViewUtils.showToast(UIUtils.getContext(), "获取列表数据失败");
                    return;
                }

                if (bean.getMsg() == 1) {
                    updateUI(bean.getData());
                } else {
                    if(mCurrentPage == 1) {
                        mEmptyTv.setVisibility(View.VISIBLE);
                        mGridView.setVisibility(View.GONE);
                    }
                    ViewUtils.showToast(UIUtils.getContext(), "获取列表数据失败");
                }
            }

            @Override
            protected void onFail() {
                super.onFail();
                if(mCurrentPage == 1) {
                    mEmptyTv.setVisibility(View.VISIBLE);
                    mGridView.setVisibility(View.GONE);
                }
                mBaseAct.dismissLoadDialog();
                mRefreshLayout.finishRefresh();
                mRefreshLayout.finishRefreshLoadMore();
                ViewUtils.showToast(UIUtils.getContext(), "获取列表数据失败");
            }

            @Override
            protected void onTimeOut() {
                super.onTimeOut();
                if(mCurrentPage == 1) {
                    mEmptyTv.setVisibility(View.VISIBLE);
                    mGridView.setVisibility(View.GONE);
                }
                mBaseAct.dismissLoadDialog();
                mRefreshLayout.finishRefresh();
                mRefreshLayout.finishRefreshLoadMore();
                ViewUtils.showToast(UIUtils.getContext(), "获取列表数据失败");
            }
        };

        httpManager.get(requestUrl);
    }

    /**
     * 更新UI信息
     *
     * @param data
     */
    private void updateUI(GoodsDataEntity data) {
        List<GoodsRowsEntity> listRows = data.getRows();
        if (listRows == null || listRows.isEmpty()) {
            if(mCurrentPage == 1) {
                mEmptyTv.setVisibility(View.VISIBLE);
                mGridView.setVisibility(View.GONE);
            }
            return;
        }

        mEmptyTv.setVisibility(View.GONE);
        mGridView.setVisibility(View.VISIBLE);
        if (listRows.size() < 10) {
            mRefreshLayout.setLoadMore(false);
        } else {
            mCurrentPage++;
            mRefreshLayout.setLoadMore(true);
        }

        if (mCurrentPage == 1) {
            mAdapter.setData(listRows);
        } else {
            mAdapter.addAll(listRows);
        }
    }

    private void initView() {
        mRefreshLayout = (MaterialRefreshLayout) getView().findViewById(R.id.commodity_refresh_layout);
        mRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {
                getListData(true);
                BusProvider.getInstance().post(new NotifyUpdateHomeEvent());
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                getListData(true);
            }
        });

        mGridView = (GridView) getView().findViewById(R.id.commodity_gridview);
        mGridView.setAdapter(mAdapter);
        mEmptyTv = (TextView) getView().findViewById(R.id.goods_list_empty_tv);
        mEmptyTv.setText(mEmptyText);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GoodsRowsEntity itemData = mAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), CommondityDetailsActivity.class);
                intent.putExtra(CommondityDetailsActivity.KEY_TYPE, isWaitStatu);
                intent.putExtra(CommondityDetailsActivity.KEY_DATA, itemData.getId());
                startActivity(intent);
            }
        });

    }

    @Subscribe
    public void onEventNotify(NotifyUpdateEvent event) {
        getListData(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
        isHaveRegister = false;
    }
}
