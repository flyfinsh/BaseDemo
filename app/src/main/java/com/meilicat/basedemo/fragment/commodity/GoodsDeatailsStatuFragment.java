package com.meilicat.basedemo.fragment.commodity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.google.gson.Gson;
import com.meilicat.basedemo.R;
import com.meilicat.basedemo.base.BaseActivity;
import com.meilicat.basedemo.bean.goods.GoodsDetailListBean;
import com.meilicat.basedemo.conf.Constants;
import com.meilicat.basedemo.event.BusProvider;
import com.meilicat.basedemo.event.SelectColorSizeEvent;
import com.meilicat.basedemo.utils.HttpManager;
import com.meilicat.basedemo.utils.UIUtils;
import com.meilicat.basedemo.utils.ViewUtils;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2016/1/28.
 */
public class GoodsDeatailsStatuFragment extends Fragment {

    private BaseActivity mBaseAct;

    public static final String REQUEST_TYPE = "request_type";
    public static final String TYPE_SALE_ALREADY = "1"; // 已卖出
    public static final String TYPE_AWAY_ON = "2"; // 在途中
//    public static final String TYPE_HAVED = "3"; // 现库存

    private String mRequestType = TYPE_SALE_ALREADY;

    private LayoutInflater mInflater;

    private List<GoodsDetailListBean.DataEntity.RowsEntity> mGroupList = new ArrayList<>();
    private List<List<GoodsDetailListBean.DataEntity.RowsEntity.ChildItemEntity>> mItemList = new ArrayList<>();
    private List<List<GoodsDetailListBean.DataEntity.RowsEntity.ChildItemEntity>> mTempItemList = new ArrayList<>();

    private String mType = "0";
    public static final String REQUEST_ID = "request_id";
    private String mGoodsId;

    private MyExpandableListViewAdapter mAdapter;
    private boolean isAlreadySale;

    private boolean isHaveRegister;

    private int mCurrentPage = 1;

    private TextView mEmptyTv;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof BaseActivity) {
            mBaseAct = (BaseActivity) activity;
        }
        mInflater = LayoutInflater.from(activity);

        if (!isHaveRegister) {
            BusProvider.getInstance().register(this);
            isHaveRegister = true;
        }

        mRequestType = getArguments().getString(REQUEST_TYPE);
        mGoodsId = getArguments().getString(REQUEST_ID);
        if (TYPE_SALE_ALREADY.equals(mRequestType)) {
            mType = "0";
            isAlreadySale = true;
        } else {
            mType = "1";
            isAlreadySale = false;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.goods_deatails_statu_layout, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        getStatuData(false);
    }

    private void getStatuData(boolean isLoadMore) {
        if (!isLoadMore) {
            mBaseAct.showLoadDialog();
        }
        HttpManager httpManager = new HttpManager(UIUtils.getContext()) {
            @Override
            protected void onSuccess(Object obj) {
                super.onSuccess(obj);
                mBaseAct.dismissLoadDialog();
                mRefreshLayout.finishRefreshLoadMore();
                Gson gson = new Gson();

                GoodsDetailListBean bean = null;
                try {
                    bean = gson.fromJson(String.valueOf(obj), GoodsDetailListBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (bean == null) {
                    if (mCurrentPage == 1) {
                        mGroupList.clear();
                        mItemList.clear();
                        mAdapter.notifyDataSetChanged();
                        mEmptyTv.setVisibility(View.VISIBLE);
                    }
                    ViewUtils.showToast(UIUtils.getContext(), "没有数据");
                    return;
                }

                if (bean.getMsg() == 1) {
                    updateListData(bean);
                } else {
                    if (mCurrentPage == 1) {
                        mGroupList.clear();
                        mItemList.clear();
                        mAdapter.notifyDataSetChanged();
                        mEmptyTv.setVisibility(View.VISIBLE);
                    }
                    ViewUtils.showToast(UIUtils.getContext(), "获取状态失败");
                }
            }

            @Override
            protected void onFail() {
                super.onFail();
                if (mCurrentPage == 1) {
                    mGroupList.clear();
                    mItemList.clear();
                    mAdapter.notifyDataSetChanged();
                    mEmptyTv.setVisibility(View.VISIBLE);
                }
                mBaseAct.dismissLoadDialog();
                mRefreshLayout.finishRefreshLoadMore();
                ViewUtils.showToast(UIUtils.getContext(), "获取状态失败");
            }
        };

        if (isFilter) {
            httpManager.get(Constants.URLS.GOODS_STATU_LIST + "id=" + mGoodsId + "&type=" + mType + "&searchColorName=" + selColor + "&searchSizeName=" + selSize + "&page=" + mCurrentPage);
        } else {
            httpManager.get(Constants.URLS.GOODS_STATU_LIST + "id=" + mGoodsId + "&type=" + mType + "&page=" + mCurrentPage);
        }
    }

    private void updateListData(GoodsDetailListBean bean) {
        GoodsDetailListBean.DataEntity statuData = bean.getData();
        if (statuData == null) {
            if (mCurrentPage == 1) {
                mGroupList.clear();
                mItemList.clear();
                mAdapter.notifyDataSetChanged();
                mEmptyTv.setVisibility(View.VISIBLE);
            }
            return;
        }

        List<GoodsDetailListBean.DataEntity.RowsEntity> tempGroupList = statuData.getRows();
        if (tempGroupList == null || tempGroupList.isEmpty()) {
            if (mCurrentPage == 1) {
                mGroupList.clear();
                mItemList.clear();
                mAdapter.notifyDataSetChanged();
                mEmptyTv.setVisibility(View.VISIBLE);
            }
            return;
        }

        mEmptyTv.setVisibility(View.GONE);

        mTempItemList.clear();
        for (GoodsDetailListBean.DataEntity.RowsEntity entity : tempGroupList) {
            GoodsDetailListBean.DataEntity.RowsEntity.ChildItemEntity processItem = entity.getProcessedMap();
            GoodsDetailListBean.DataEntity.RowsEntity.ChildItemEntity signItem = entity.getSignedMap();
            GoodsDetailListBean.DataEntity.RowsEntity.ChildItemEntity statItem = entity.getStatMap();

            List<GoodsDetailListBean.DataEntity.RowsEntity.ChildItemEntity> itemList = new ArrayList<>();

            if (processItem != null) {
                processItem.setTitle("已处理");
                processItem.isHandled = true;
                itemList.add(processItem);
            }
            if (signItem != null) {
                signItem.setTitle("已签收");
                itemList.add(signItem);
            }
            if (statItem != null) {
                statItem.setTitle("已结算");
                statItem.isMoneyed = true;
                itemList.add(statItem);
            }

            mTempItemList.add(itemList);
        }

        if (tempGroupList.size() < 10) {
            mRefreshLayout.setLoadMore(false);
        } else {
            mCurrentPage++;
            mRefreshLayout.setLoadMore(true);
        }

        if (mCurrentPage == 1) {
            mGroupList = tempGroupList;
            mItemList = mTempItemList;
        } else {
            mGroupList.addAll(tempGroupList);
            mItemList.addAll(mTempItemList);
        }

        mAdapter.notifyDataSetChanged();
    }

    private MaterialRefreshLayout mRefreshLayout;

    private void initView() {
        mRefreshLayout = (MaterialRefreshLayout) getView().findViewById(R.id.goods_details_exp_refresh_layout);
        mRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {
                //下拉刷新...
                mRefreshLayout.finishRefresh();
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                //上拉刷新...
                getStatuData(true);
            }
        });

        ExpandableListView expListview = (ExpandableListView) getView().findViewById(R.id.goods_details_exp_listview);
        expListview.setGroupIndicator(null);
        mAdapter = new MyExpandableListViewAdapter();
        expListview.setAdapter(mAdapter);

        mEmptyTv = (TextView) getView().findViewById(R.id.statu_empty_tv);
    }

    class MyExpandableListViewAdapter extends BaseExpandableListAdapter {

        public MyExpandableListViewAdapter() {
        }

        @Override
        public int getGroupCount() {
            if (mGroupList == null || mGroupList.isEmpty()) {
                return 0;
            }

            return mGroupList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            if (mItemList.isEmpty()) {
                return 0;
            }

            return mItemList.get(groupPosition).size();
        }

        @Override
        public GoodsDetailListBean.DataEntity.RowsEntity getGroup(int groupPosition) {
            if (mGroupList == null || mGroupList.isEmpty()) {
                return null;
            }
            return mGroupList.get(groupPosition);
        }

        @Override
        public GoodsDetailListBean.DataEntity.RowsEntity.ChildItemEntity getChild(int groupPosition, int childPosition) {
            if (mItemList.isEmpty()) {
                return null;
            }
            return mItemList.get(groupPosition).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            GroupHolder groupHolder;
            if (convertView == null) {
                convertView = mInflater.inflate(
                        R.layout.goods_statu_item_parent, null);
                groupHolder = new GroupHolder();

                groupHolder.orderNumTv = (TextView) convertView.findViewById(R.id.item_num_tv);
                groupHolder.payTimeTv = (TextView) convertView.findViewById(R.id.item_time_tv);
                groupHolder.cscTv = (TextView) convertView.findViewById(R.id.item_csc_tv);
                groupHolder.priceLayout = (LinearLayout) convertView.findViewById(R.id.item_money_l);
                groupHolder.priceTv = (TextView) convertView.findViewById(R.id.item_js_price_tv);
                groupHolder.statuIv = (ImageView) convertView.findViewById(R.id.goods_statu_iv);
                convertView.setTag(groupHolder);
            } else {
                groupHolder = (GroupHolder) convertView.getTag();
            }

            if (mGroupList != null && !mGroupList.isEmpty()) {
                GoodsDetailListBean.DataEntity.RowsEntity itemData = mGroupList.get(groupPosition);
                if (itemData != null) {
                    groupHolder.orderNumTv.setText("订单号：" + itemData.getOrderNo());
                    groupHolder.payTimeTv.setText(itemData.getPayTime());
                    if (isAlreadySale) {
                        groupHolder.cscTv.setText(getString(R.string.goods_statu_csc_text, itemData.getColorName(), itemData.getSizeName(), itemData.getQty() + ""));
                    } else {
                        groupHolder.cscTv.setText(getString(R.string.goods_statu_csc_text, itemData.getColorName(), itemData.getSizeAbbr(), itemData.getPurchaseNum2() + ""));
                    }

                    double totalPrice = itemData.getTotalPrice();
                    if (totalPrice > 0) {
                        groupHolder.priceLayout.setVisibility(View.VISIBLE);
                        groupHolder.priceTv.setText("￥" + totalPrice);
                    } else {
                        groupHolder.priceLayout.setVisibility(View.INVISIBLE);
                    }
                }
            }

            if (mItemList == null || mItemList.isEmpty()) {
                groupHolder.statuIv.setVisibility(View.INVISIBLE);
            } else {

                if (isChildListEmpty(groupPosition)) {
                    groupHolder.statuIv.setVisibility(View.INVISIBLE);
                } else {
                    groupHolder.statuIv.setVisibility(View.VISIBLE);
                    if (isExpanded) {
                        groupHolder.statuIv.setImageResource(R.mipmap.item_ex_list_up);
                    } else {
                        groupHolder.statuIv.setImageResource(R.mipmap.item_ex_list_down);
                    }
                }
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            ItemHolder itemHolder;
            if (convertView == null) {
                convertView = mInflater.inflate(
                        R.layout.goods_statu_item_child, null);
                itemHolder = new ItemHolder();
                itemHolder.topFirstLine = convertView.findViewById(R.id.top_one_line);
                itemHolder.firtEmptyView = convertView.findViewById(R.id.top_one_empty);
                itemHolder.bottomLine = convertView.findViewById(R.id.down_line);

                itemHolder.titleTv = (TextView) convertView.findViewById(R.id.child_state_title_tv);
                itemHolder.titleRightTv = (TextView) convertView.findViewById(R.id.child_state_title_right_tv);
                itemHolder.descTv = (TextView) convertView.findViewById(R.id.child_state_desc_tv);
                convertView.setTag(itemHolder);
            } else {
                itemHolder = (ItemHolder) convertView.getTag();
            }

            if (childPosition == 0) {
                itemHolder.topFirstLine.setVisibility(View.VISIBLE);
                itemHolder.firtEmptyView.setVisibility(View.VISIBLE);
            } else {
                itemHolder.topFirstLine.setVisibility(View.GONE);
                itemHolder.firtEmptyView.setVisibility(View.GONE);
            }

            if (childPosition == mItemList.get(groupPosition).size() - 1) {
                itemHolder.bottomLine.setVisibility(View.GONE);
            } else {
                itemHolder.bottomLine.setVisibility(View.VISIBLE);
            }

            if (mItemList != null && !mItemList.isEmpty()) {
                GoodsDetailListBean.DataEntity.RowsEntity.ChildItemEntity childItemData = mItemList.get(groupPosition).get(childPosition);
                if (childItemData != null) {
                    itemHolder.titleTv.setText(childItemData.getTitle());

                    String optTime = childItemData.getOpTime();

                    if (childItemData.isHandled) {
                        itemHolder.descTv.setVisibility(View.INVISIBLE);
                        itemHolder.titleRightTv.setText(optTime);
                    } else if (childItemData.isMoneyed) {
                        StringBuffer moneyBuffer = new StringBuffer("时间：" + optTime);
                        String totalMoney = childItemData.getTotalMoney();
                        if (!TextUtils.isEmpty(totalMoney)) {
                            moneyBuffer.append("   金额：" + totalMoney);
                        }

                        itemHolder.descTv.setVisibility(View.INVISIBLE);
                        itemHolder.titleRightTv.setText(moneyBuffer.toString());
                    } else {
                        itemHolder.descTv.setVisibility(View.VISIBLE);

                        String signName = childItemData.getSignName();
                        StringBuffer descBuffer = new StringBuffer("签收人：" + signName);
                        if (!TextUtils.isEmpty(optTime)) {
                            descBuffer.append("   签收时间：" + optTime);
                        }
                        String comment = childItemData.getComment();
                        if (!TextUtils.isEmpty(comment)) {
                            descBuffer.append("\n签收备注：" + comment);
                        }

                        String signMsg = childItemData.getSignMsg();
                        itemHolder.titleRightTv.setText(signMsg);
                        itemHolder.descTv.setText(descBuffer.toString());
                    }

                }
            }

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

    }

    private boolean isChildListEmpty(int groupPosition) {
        List<GoodsDetailListBean.DataEntity.RowsEntity.ChildItemEntity> checkItemList = mItemList.get(groupPosition);
        if(checkItemList == null || checkItemList.isEmpty()) {
            return true;
        }
        return false;
    }

    private String selColor;
    private String selSize;

    private boolean isFilter;

    @Subscribe
    public void onEventSelectColorSize(SelectColorSizeEvent event) {
        selColor = event.mColor;
        selSize = event.mSize;
        isFilter = true;
        mCurrentPage = 1;
        getStatuData(false);
    }

    class GroupHolder {
        public TextView orderNumTv;
        public TextView payTimeTv;
        public TextView cscTv;
        public LinearLayout priceLayout;
        public TextView priceTv;

        public ImageView statuIv;
    }

    class ItemHolder {
        public TextView titleTv;
        public TextView titleRightTv;
        public TextView descTv;

        View topFirstLine;
        View bottomLine;
        View firtEmptyView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isHaveRegister = false;
        BusProvider.getInstance().unregister(this);
    }
}
