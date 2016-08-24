package com.meilicat.basedemo.activity.buyeractivity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.google.gson.Gson;
import com.meilicat.basedemo.R;
import com.meilicat.basedemo.base.BaseActivity;
import com.meilicat.basedemo.bean.purchaser.PurchaserUnhandler;
import com.meilicat.basedemo.conf.Constants;
import com.meilicat.basedemo.utils.DeviceConfiger;
import com.meilicat.basedemo.utils.HttpManager;
import com.meilicat.basedemo.utils.LogUtils;
import com.meilicat.basedemo.utils.T;
import com.meilicat.basedemo.utils.UIUtils;
import com.meilicat.basedemo.view.AnimatedExpandableListView;
import com.meilicat.basedemo.view.CustomBuyerBrandView;
import com.meilicat.basedemo.view.CustomBuyerCommontView;
import com.meilicat.basedemo.view.CustomSureDialog;
import com.meilicat.basedemo.view.CutomStateInfoView;
import com.meilicat.basedemo.view.SubListView;
import com.meilicat.basedemo.view.calendar.CalendarActivity;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;


public class BuyerUnhandlerActivityOther extends BaseActivity {

    @Bind(R.id.buyer_unhandler_title_back)
    RelativeLayout mBuyerUnhandlerTitleBack;
    @Bind(R.id.buyer_unhandler_title_name)
    TextView mBuyerUnhandlerTitleName;
    @Bind(R.id.buyer_unhandler_tabcontainer)
    RadioGroup mBuyerUnhandlerTabcontainer;
    @Bind(R.id.buyer_unhandler_today_num)
    TextView mBuyerUnhandlerTodayNum;
    @Bind(R.id.buyer_unhandler_week_num)
    TextView mBuyerUnhandlerWeekNum;
    @Bind(R.id.buyer_unhandler_history_num)
    TextView mBuyerUnhandlerHistoryNum;
    @Bind(R.id.buyer_unhandler_lv)
    AnimatedExpandableListView mBuyerUnhandlerLv;
    @Bind(R.id.order_commont_refresh)
    MaterialRefreshLayout mOrderCommontRefresh;
    @Bind(R.id.error_btn_retry)
    Button mErrorBtnRetry;
    @Bind(R.id.buyer_unhandler_error)
    LinearLayout mBuyerUnhandlerError;
    @Bind(R.id.buyer_unhandler_empty)
    LinearLayout mBuyerUnhandlerEmpty;
    @Bind(R.id.buyer_unhandler_loading)
    ProgressWheel mBuyerUnhandlerLoading;

    public static final int STATE_EMPTY = 0;
    public static final int STATE_ERROR = 1;
    public static final int STATE_SUCCESS = 2;
    public static final int STATE_NONE = -1;
    public static final int STATE_LOADING = 3;

    private int currentState = STATE_NONE;

    public static final int TYPE_FIRST = 1;
    public static final int TYPE_REFRESH = 2;

    int mTodaySum;
    int mSevenSum;
    int mAllSum;
    private Map<String, Map<String, List<PurchaserUnhandler.DataEntity.DTHListEntity>>> mDateMap;
    private Map<String, Map<String, List<PurchaserUnhandler.DataEntity.DTHListEntity>>> mOldDateMap;


    private List<String> mSuuplierId;
    private List<String> mSuuplierName;
    private List<String> mDateList;
    private Map<String, PurchaserUnhandler.DataEntity.DTHListEntity> mSupplierDetail;
    private List<String> mDateAndIdList;
//    private Dialog mComfirmDialog;
    int currentPage = 1;
    private CustomSureDialog mComfirmDialog;
    private int mCurrentPosition;

    @Override
    public void setContent() {
        setContentView(R.layout.activity_buyer_unhandler);
        ButterKnife.bind(this);
        initData(Constants.URLS.BUYER_UNHANDLER + "?timeMark=0",TYPE_FIRST);
    }


    private void initData(String url,int type) {

        if (type == TYPE_FIRST){
            currentState = STATE_LOADING;
            currentDate = COMMONT_TYPE;
            mCurrentPosition = -1;
            refeshUI(currentState);
        }
        currentPage = 1;
        mCurrentPosition = -1;

        mDateMap = null;
        mTodaySum = 0;
        mSevenSum = 0;
        mAllSum = 0;
        mSuuplierId = new ArrayList<>();
        mDateList = new ArrayList<>();
        mSupplierDetail = new HashMap<>();
        mSuuplierName = new ArrayList<>();
        mDateAndIdList = new ArrayList<>();

        HttpManager manager = new HttpManager(UIUtils.getContext()) {
            @Override
            protected void onFail() {
                currentState = STATE_ERROR;
                refeshUI(currentState);
                mOrderCommontRefresh.finishRefresh();
            }

            @Override
            protected void onTimeOut() {
                currentState = STATE_ERROR;
                refeshUI(currentState);
                mOrderCommontRefresh.finishRefresh();
            }

            @Override
            protected void onSuccess(Object obj) {
                Gson gson = new Gson();
                PurchaserUnhandler unhandler = null;

                LogUtils.i("obj-----"+obj);

                try {
                    unhandler = gson.fromJson(obj + "", PurchaserUnhandler.class);
                }catch (Exception e){
                    e.printStackTrace();
                    T.showShort(UIUtils.getContext(),"加载数据失败");
                }

                if (unhandler == null){
                    currentState = STATE_ERROR;
                    refeshUI(currentState);
                    mOrderCommontRefresh.finishRefresh();
                    return;
                }

                if (unhandler != null) {
                    if (unhandler.msg == 1) {
                        if (unhandler.data.headCount != null) {
                            mTodaySum = unhandler.data.headCount.notTHInToday;
                            mSevenSum = unhandler.data.headCount.notTHIn30day;
                            mAllSum = unhandler.data.headCount.notTHAll;
                        }

                        if (unhandler.data != null) {
                            if (unhandler.data.dTHList != null && unhandler.data.dTHList.size() != 0){
                                    mDateMap = new HashMap<>();
                                    mOldDateMap = new HashMap<>();
                                    List<PurchaserUnhandler.DataEntity.DTHListEntity> list = unhandler.data.dTHList;

                                    for (int i = 0; i < list.size(); i++) {
                                        PurchaserUnhandler.DataEntity.DTHListEntity listEntity = list.get(i);

                                        String date = listEntity.THDate;
                                        date = date.substring(0, 10);
                                        String dateAndID = date+"#"+listEntity.supplierId;

                                        mDateAndIdList.add(dateAndID);

                                        Map<String, List<PurchaserUnhandler.DataEntity.DTHListEntity>> supplierMap = mDateMap.get(date);

                                        if (supplierMap == null) {
                                            Map<String, List<PurchaserUnhandler.DataEntity.DTHListEntity>> mMap = new HashMap<>();
                                            String supplierId = listEntity.supplierId;
                                            List<PurchaserUnhandler.DataEntity.DTHListEntity> lastList = mMap.get(supplierId);
                                            if (lastList == null) {
                                                List<PurchaserUnhandler.DataEntity.DTHListEntity> newList = new ArrayList<>();

                                                mSuuplierId.add(supplierId);
                                                mSuuplierName.add(listEntity.supplierName);

                                                newList.add(listEntity);
                                                mMap.put(supplierId, newList);
                                                mSupplierDetail.put(supplierId, listEntity);

                                            } else {
                                                lastList.add(listEntity);
                                            }

                                            mDateMap.put(date, mMap);
                                            mDateList.add(date);
                                        } else {
                                            List<PurchaserUnhandler.DataEntity.DTHListEntity> suppList = supplierMap.get(listEntity.supplierId);
                                            if (suppList == null) {
                                                List<PurchaserUnhandler.DataEntity.DTHListEntity> newList = new ArrayList<>();
                                                newList.add(listEntity);
                                                supplierMap.put(listEntity.supplierId, newList);

                                                mSupplierDetail.put(listEntity.supplierId, listEntity);

                                                mSuuplierId.add(listEntity.supplierId);
                                                mSuuplierName.add(listEntity.supplierName);
                                            } else {
                                                suppList.add(listEntity);
                                            }
                                        }
                                    }
                                    mDateAndIdList = removeDuplicateWithOrder(mDateAndIdList);
                                    mSuuplierName = removeDuplicateWithOrder(mSuuplierName);
                                    mSuuplierId = removeDuplicateWithOrder(mSuuplierId);

                                    mOldDateMap.putAll(mDateMap);


                                    currentState = STATE_SUCCESS;
                            }else {
                                currentState = STATE_EMPTY;
                            }
                        } else {
                            currentState = STATE_EMPTY;
                        }
                    } else {
                        currentState = STATE_ERROR;
                    }
                } else {
                    currentState = STATE_EMPTY;
                }
                mOrderCommontRefresh.finishRefresh();
                refeshUI(currentState);
            }
        };

        manager.get(url);
    }

    public static final int SELECT_TYPE = 1;
    public static final int COMMONT_TYPE = 2;

    private int currentDate = -1;

    public void loadMore(String url){

        LogUtils.i("url-----------"+url);
        HttpManager manger = new HttpManager(UIUtils.getContext()){
            @Override
            protected void onSuccess(Object obj) {
                if (obj != null){
                    Gson gson = new Gson();
                    PurchaserUnhandler unhandler = null;

                    try {
                        unhandler = gson.fromJson(obj + "", PurchaserUnhandler.class);
                    }catch (Exception e){
                        if (currentPage >= 1){
                            currentPage = currentPage-1;
                        }
                        e.printStackTrace();

                    }
                    if (unhandler == null){
                        if (currentPage >= 1){
                            currentPage = currentPage-1;
                        }
                        mOrderCommontRefresh.finishRefreshLoadMore();
                        return;
                    }

                    if (unhandler.msg == 1) {
                        if (unhandler.data.dTHList != null && unhandler.data.dTHList.size() != 0){

                            List<PurchaserUnhandler.DataEntity.DTHListEntity> list = unhandler.data.dTHList;
                            for (int i = 0; i < list.size(); i++) {
                                PurchaserUnhandler.DataEntity.DTHListEntity listEntity = list.get(i);
                                String date = listEntity.THDate;
                                date = date.substring(0, 10);
                                String dateAndID = date+"#"+listEntity.supplierId;

                                mDateAndIdList.add(dateAndID);
                                Map<String, List<PurchaserUnhandler.DataEntity.DTHListEntity>> supplierMap = mDateMap.get(date);
                                if (supplierMap == null) {
                                    Map<String, List<PurchaserUnhandler.DataEntity.DTHListEntity>> mMap = new HashMap<>();
                                    String supplierId = listEntity.supplierId;
                                    List<PurchaserUnhandler.DataEntity.DTHListEntity> lastList = mMap.get(supplierId);
                                    if (lastList == null) {
                                        List<PurchaserUnhandler.DataEntity.DTHListEntity> newList = new ArrayList<>();

                                        mSuuplierId.add(supplierId);
                                        mSuuplierName.add(listEntity.supplierName);

                                        newList.add(listEntity);
                                        mMap.put(supplierId, newList);
                                        mSupplierDetail.put(supplierId, listEntity);

                                    } else {
                                        lastList.add(listEntity);
                                    }
                                    mDateMap.put(date, mMap);
                                    mDateList.add(date);
                                } else {
                                    List<PurchaserUnhandler.DataEntity.DTHListEntity> suppList = supplierMap.get(listEntity.supplierId);
                                    if (suppList == null) {
                                        List<PurchaserUnhandler.DataEntity.DTHListEntity> newList = new ArrayList<>();
                                        newList.add(listEntity);
                                        supplierMap.put(listEntity.supplierId, newList);

                                        mSupplierDetail.put(listEntity.supplierId, listEntity);

                                        mSuuplierId.add(listEntity.supplierId);
                                        mSuuplierName.add(listEntity.supplierName);
                                    } else {
                                        suppList.add(listEntity);
                                    }
                                }
                                mDateAndIdList = removeDuplicateWithOrder(mDateAndIdList);

                                mSuuplierName = removeDuplicateWithOrder(mSuuplierName);
                                mSuuplierId = removeDuplicateWithOrder(mSuuplierId);

                                mOldDateMap.clear();
                                mOldDateMap.putAll(mDateMap);
                                T.showShort(UIUtils.getContext(), "加载更多成功");
                            }
                        }else {
                            T.showShort(UIUtils.getContext(),"没有更多数据");
                            if (currentPage >= 1){
                                currentPage = currentPage-1;
                            }

                        }
                    }else {
                        T.showShort(UIUtils.getContext(),"加载更多失败");
                        if (currentPage >= 1){
                            currentPage = currentPage-1;
                        }
                    }
                }else {
                    T.showShort(UIUtils.getContext(),"加载更多失败");
                    if (currentPage >= 1){
                        currentPage = currentPage-1;
                    }
                }
                mOrderCommontRefresh.finishRefreshLoadMore();
                mAnimAdapter.notifyDataSetChanged();
            }

            @Override
            protected void onFail() {
                if (currentPage >= 1){
                    currentPage = currentPage-1;
                }
                T.showShort(UIUtils.getContext(),"加载更多失败");
                mOrderCommontRefresh.finishRefreshLoadMore();
            }

            @Override
            protected void onTimeOut() {
                if (currentPage >= 1){
                    currentPage = currentPage-1;
                }
                T.showShort(UIUtils.getContext(),"加载更多超时");
                mOrderCommontRefresh.finishRefreshLoadMore();
            }
        };

        manger.get(url);

    }


    public static List<String> removeDuplicateWithOrder(List<String> list) {
        HashSet<String> hashSet = new HashSet<>();
        List<String> newlist = new ArrayList<>();
        for (Iterator iterator = list.iterator(); iterator.hasNext(); ) {
            String element = (String) iterator.next();
            if (hashSet.add(element)) {
                newlist.add(element);
            }
        }
        list.clear();
        list.addAll(newlist);
        return list;
    }

    public void refeshUI(int type) {
        mBuyerUnhandlerError.setVisibility((type == STATE_ERROR) || (type == STATE_NONE) ? View.VISIBLE : View.GONE);
        mBuyerUnhandlerEmpty.setVisibility((type == STATE_EMPTY) || (type == STATE_NONE) ? View.VISIBLE : View.GONE);
        mBuyerUnhandlerLv.setVisibility((type == STATE_SUCCESS) || (type == STATE_NONE) ? View.VISIBLE : View.GONE);
        mBuyerUnhandlerLoading.setVisibility((type == STATE_LOADING) || (type == STATE_NONE) ? View.VISIBLE : View.GONE);
        initView();
    }

    @Override
    public void initView() {
        mBuyerUnhandlerTodayNum.setText(mTodaySum + "");
        mBuyerUnhandlerWeekNum.setText(mSevenSum + "");
        mBuyerUnhandlerHistoryNum.setText(mAllSum + "");

        if (mDateMap != null) {

            if (mAnimAdapter != null) {
                mAnimAdapter.notifyDataSetChanged();
            } else {
                mAnimAdapter = new AnimAdapter();
                mBuyerUnhandlerLv.setAdapter(mAnimAdapter);
                mBuyerUnhandlerLv.setGroupIndicator(null);

                mBuyerUnhandlerLv.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                    @Override
                    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                        if (mBuyerUnhandlerLv.isGroupExpanded(groupPosition)) {
                            mBuyerUnhandlerLv.collapseGroupWithAnimation(groupPosition);
                        } else {
                            mBuyerUnhandlerLv.expandGroupWithAnimation(groupPosition);
                        }
                        return true;
                    }
                });
            }
        }

        initEvent();
    }
    AnimAdapter mAnimAdapter;
    class AnimAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter{

        @Override
        public View getRealChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            final ChildItemHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(UIUtils.getContext()).inflate(R.layout.item_unhandler_child_lv, null);
                holder = new ChildItemHolder();
                holder.mItem = (CustomBuyerCommontView) convertView.findViewById(R.id.cutom_order_view);
                holder.mStateInfo = (CutomStateInfoView) convertView.findViewById(R.id.cutom_state_info_view);

                convertView.setTag(holder);
            } else {
                holder = (ChildItemHolder) convertView.getTag();
            }

            String dateAndId = mDateAndIdList.get(groupPosition);
            String[] dates = dateAndId.split("#");
            final String date = dates[0];
            final String id = dates[1];
            final PurchaserUnhandler.DataEntity.DTHListEntity entity = mDateMap.get(date).get(id).get(childPosition);


            holder.mItem.setGoodsColor(entity.colorName);
            holder.mItem.setGoodsName(entity.productName_cn);
            holder.mItem.setGoodsNumber(entity.productNumber);
            holder.mItem.setGoodPrice(entity.purchasedPrice);
            holder.mItem.setGoodsSize(entity.sizeAbbr);


            holder.mItem.setOrderNumber(entity.orderNo + "");

            if (TextUtils.isEmpty(entity.payTime)){
                holder.mItem.setPayTime(entity.createTime);
            }else {
                holder.mItem.setPayTime(entity.payTime);
            }

            holder.mItem.setSumNum(entity.purchaseNum);
            holder.mItem.setOrderPrice(entity.totalMoneyOne);

            holder.mItem.setGoodsImage(entity.mobileImageURL_1);

            if (entity.con == 0) {
                holder.mItem.changeGoodsType(true);
            } else {
                holder.mItem.changeGoodsType(false);
            }

            holder.mItem.hindArrow();

            if (entity.isAccept) {
                holder.mStateInfo.isAccept(true);
                if (!TextUtils.isEmpty(entity.remark)) {
                    holder.mStateInfo.setAcceptInfo(true, entity.remark);
                } else {
                    holder.mStateInfo.setAcceptInfo(false, null);
                }
            } else {
                holder.mStateInfo.isAccept(false);
            }

            if (TextUtils.isEmpty(entity.appComment)){
                holder.mStateInfo.setSignInfo(false, "");
            }else {
                holder.mStateInfo.setSignInfo(true, entity.appComment);
            }

            String date1 = entity.THDate;
            date1 = date1.substring(0,19);

            holder.mStateInfo.setReadyTime(date1);

            holder.mStateInfo.setConfirmClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    T.showShort(UIUtils.getContext(), "你点击了确认签收");
                    signOrder(entity.id, date, id, childPosition);
//                    signOrderOther(entity.id, date, id, childPosition);
                }
            });


            return convertView;
        }

        @Override
        public int getRealChildrenCount(int groupPosition) {
            String dateAndId = mDateAndIdList.get(groupPosition);
            String[] dates = dateAndId.split("#");
            String date = dates[0];
            String id = dates[1];
            List<PurchaserUnhandler.DataEntity.DTHListEntity> itemData = mDateMap.get(date).get(id);

            return itemData.size();
        }

        @Override
        public int getGroupCount() {
            int count = 0;
            if (mDateList != null) {
                for (int i = 0; i < mDateList.size(); i++) {

                    Map<String, List<PurchaserUnhandler.DataEntity.DTHListEntity>> map = mDateMap.get(mDateList.get(i));
                    if (map != null) {
                        count = count + map.size();
                    }
                }
            }
            return count;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {

                holder = new ViewHolder();
                convertView = LayoutInflater.from(UIUtils.getContext()).inflate(R.layout.item_buyer_unhandler_today, null);
                holder.mBrandView = (CustomBuyerBrandView) convertView.findViewById(R.id.item_buyer_unhandler_today_commont);
                holder.mListView = (SubListView) convertView.findViewById(R.id.order_unhandler_item_lv);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            String dateAndId = mDateAndIdList.get(groupPosition);
            String[] dates = dateAndId.split("#");
            String date = dates[0];
            String id = dates[1];
            PurchaserUnhandler.DataEntity.DTHListEntity supplier = mSupplierDetail.get(id);

            if (TextUtils.isEmpty(supplier.address)){
                holder.mBrandView.setBrandAddress("暂无地址");
            }else {
                holder.mBrandView.setBrandAddress(supplier.address);
            }

            String suoolierName = supplier.supplierName.replaceAll(" ", "");
            holder.mBrandView.setBrandName(suoolierName);
            holder.mBrandView.setBrandNumber(supplier.mobilePhoneNum);
            holder.mBrandView.setBrandSaler(supplier.contractPerson);
            holder.mBrandView.hintCheckBox();

            holder.mBrandView.changeImageView(isExpanded);

            if (supplier.isSign == 0){
                holder.mBrandView.setIsSign(false);
            }else {
                holder.mBrandView.setIsSign(true);
            }
            holder.mBrandView.setBrandLogo(supplier.avatarImageURL);

            holder.mBrandView.setBrandTime(date);

            int sumNumber = 0;
            for (int i = 0; i < mDateMap.get(date).get(id).size(); i++) {
                sumNumber = sumNumber + mDateMap.get(date).get(id).get(i).purchaseNum;
            }
            holder.mBrandView.setBrandNum(sumNumber);

            if (groupPosition == 0) {
                holder.mBrandView.hintDivi(true);
            } else {
                holder.mBrandView.hintDivi(false);
            }


            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }

    private void signOrder(int id, String date, String supplierId, int position) {
        LogUtils.i("确认签收 的 id---------"+id);
        Intent intent = new Intent();
        intent.putExtra("detail", id);
        intent.putExtra("position", position);
        intent.putExtra("date", date);
        intent.putExtra("supplierId", supplierId);
        intent.setClass(this, BuyerUnhandlerConfirmActivity.class);

        startActivityForResult(intent, 1);

    }
    public void calendar(String id) {
        Intent intent = new Intent(this, CalendarActivity.class);

        intent.putExtra("id", id);
        intent.putExtra("type",1);

        startActivityForResult(intent, 1);
    }

    private void signOrderOther(final int id,String date,String supplierId,int position){


        mComfirmDialog = new CustomSureDialog(this, R.style.common_dialog_theme);

        mComfirmDialog.setCancelOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mComfirmDialog.dismiss();
                mComfirmDialog = null;
            }
        });

        mComfirmDialog.setComfirmOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = mComfirmDialog.getNum();

            }
        });

        mComfirmDialog.setTimeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar(id+"");
            }
        });


        int topMargin = DeviceConfiger.dp2px(150);
        int width = DeviceConfiger.dp2px(300);
        Window dialogWindow = mComfirmDialog.getWindow();
        dialogWindow.setGravity(Gravity.TOP);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = topMargin;
        lp.width = width;
        dialogWindow.setAttributes(lp);

        mComfirmDialog.setCanceledOnTouchOutside(false);
        mComfirmDialog.show();

    }



    class ChildItemHolder {
        CustomBuyerCommontView mItem;
        CutomStateInfoView mStateInfo;
    }


    class ViewHolder {
        CustomBuyerBrandView mBrandView;
        SubListView mListView;
    }

    @Override
    public void initTitle() {
        mBuyerUnhandlerTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //返回值
        if (resultCode == 1) {
            int currentPosition = data.getIntExtra("currentPosition", -1);
            if (currentPosition != -1) {
                mCurrentPosition = currentPosition;
                selectSupplier(currentPosition, 1);//刷新


            }
        } else if (resultCode == 2) {

            String remark = data.getStringExtra("remark");
            int position = data.getIntExtra("position", -1);
            String date = data.getStringExtra("date");
            String supplierId = data.getStringExtra("supplierId");

            if (position == -1) {
                return;
            }
            Map<String, List<PurchaserUnhandler.DataEntity.DTHListEntity>> map = mDateMap.get(date);
            List<PurchaserUnhandler.DataEntity.DTHListEntity> dthListEntities = map.get(supplierId);
            PurchaserUnhandler.DataEntity.DTHListEntity entity = dthListEntities.get(position);
            if (TextUtils.isEmpty(remark)) {

                entity.isAccept = true;

            } else {
                entity.isAccept = true;
                entity.remark = remark;
            }
             mAnimAdapter.notifyDataSetChanged();

        }
    }

    //从服务器获取数据，并且重新显示数据
    private void selectSupplier(int currentPosition, final int type) {

        String supplierId = mSuuplierId.get(currentPosition);
        String url = "";
        if (type == 1) {
            showLoadDialog();
            currentPage = 1;
            switch (mBuyerUnhandlerTabcontainer.getCheckedRadioButtonId()) {

                case R.id.buyer_unhandler_tabcontainer_r1:
                    url = Constants.URLS.BUYER_UNHANDLER + "?timeMark=0&supplierId=" + supplierId;
                    break;
                case R.id.buyer_unhandler_tabcontainer_r2:
                    url = Constants.URLS.BUYER_UNHANDLER + "?timeMark=1&supplierId=" + supplierId;
                    break;
                case R.id.buyer_unhandler_tabcontainer_r3:
                    url = Constants.URLS.BUYER_UNHANDLER + "?supplierId=" + supplierId;
                    break;
            }
        }else {
            switch (mBuyerUnhandlerTabcontainer.getCheckedRadioButtonId()) {

                case R.id.buyer_unhandler_tabcontainer_r1:
                    url = Constants.URLS.BUYER_UNHANDLER + "?timeMark=0&supplierId=" + supplierId+"&page="+currentPage;
                    break;
                case R.id.buyer_unhandler_tabcontainer_r2:
                    url = Constants.URLS.BUYER_UNHANDLER + "?timeMark=1&supplierId=" + supplierId+"&page="+currentPage;
                    break;
                case R.id.buyer_unhandler_tabcontainer_r3:
                    url = Constants.URLS.BUYER_UNHANDLER + "?supplierId=" + supplierId+"&page="+currentPage;
                    break;
            }
        }

        HttpManager manager = new HttpManager(UIUtils.getContext()){
            @Override
            protected void onSuccess(Object obj) {
                dismissLoadDialog();

                LogUtils.i("obj-------"+obj);
                if ( type == 1){
                    currentPage = 1;

                    mDateMap = null;
                    mTodaySum = 0;
                    mSevenSum = 0;
                    mAllSum = 0;
                    mSuuplierId = new ArrayList<>();
                    mDateList = new ArrayList<>();
                    mSupplierDetail = new HashMap<>();
                    mSuuplierName = new ArrayList<>();
                    mDateAndIdList = new ArrayList<>();

                    if (obj != null){
                        Gson gson = new Gson();
                        PurchaserUnhandler unhandler = null;

                        try {
                            unhandler = gson.fromJson(obj + "", PurchaserUnhandler.class);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        if (unhandler == null){
                            T.showShort(UIUtils.getContext(),"供应商筛选失败");
                            return;
                        }

                        if (unhandler != null) {
                            if (unhandler.msg == 1) {
                                if (unhandler.data.headCount != null) {
                                    mTodaySum = unhandler.data.headCount.notTHInToday;
                                    mSevenSum = unhandler.data.headCount.notTHIn30day;
                                    mAllSum = unhandler.data.headCount.notTHAll;
                                }

                                if (unhandler.data != null) {
                                    if (unhandler.data.dTHList != null && unhandler.data.dTHList.size() != 0){
                                        mDateMap = new HashMap<>();
                                        mOldDateMap = new HashMap<>();
                                        List<PurchaserUnhandler.DataEntity.DTHListEntity> list = unhandler.data.dTHList;

                                        for (int i = 0; i < list.size(); i++) {
                                            PurchaserUnhandler.DataEntity.DTHListEntity listEntity = list.get(i);

                                            String date = listEntity.THDate;
                                            date = date.substring(0, 10);
                                            String dateAndID = date+"#"+listEntity.supplierId;

                                            mDateAndIdList.add(dateAndID);

                                            Map<String, List<PurchaserUnhandler.DataEntity.DTHListEntity>> supplierMap = mDateMap.get(date);

                                            if (supplierMap == null) {
                                                Map<String, List<PurchaserUnhandler.DataEntity.DTHListEntity>> mMap = new HashMap<>();
                                                String supplierId = listEntity.supplierId;
                                                List<PurchaserUnhandler.DataEntity.DTHListEntity> lastList = mMap.get(supplierId);
                                                if (lastList == null) {
                                                    List<PurchaserUnhandler.DataEntity.DTHListEntity> newList = new ArrayList<>();

                                                    mSuuplierId.add(supplierId);
                                                    mSuuplierName.add(listEntity.supplierName);

                                                    newList.add(listEntity);
                                                    mMap.put(supplierId, newList);
                                                    mSupplierDetail.put(supplierId, listEntity);

                                                } else {
                                                    lastList.add(listEntity);
                                                }

                                                mDateMap.put(date, mMap);
                                                mDateList.add(date);
                                            } else {
                                                List<PurchaserUnhandler.DataEntity.DTHListEntity> suppList = supplierMap.get(listEntity.supplierId);
                                                if (suppList == null) {
                                                    List<PurchaserUnhandler.DataEntity.DTHListEntity> newList = new ArrayList<>();
                                                    newList.add(listEntity);
                                                    supplierMap.put(listEntity.supplierId, newList);

                                                    mSupplierDetail.put(listEntity.supplierId, listEntity);

                                                    mSuuplierId.add(listEntity.supplierId);
                                                    mSuuplierName.add(listEntity.supplierName);
                                                } else {
                                                    suppList.add(listEntity);
                                                }
                                            }
                                        }
                                        mDateAndIdList = removeDuplicateWithOrder(mDateAndIdList);
                                        mSuuplierName = removeDuplicateWithOrder(mSuuplierName);
                                        mSuuplierId = removeDuplicateWithOrder(mSuuplierId);

                                        mOldDateMap.putAll(mDateMap);

                                        currentState = STATE_SUCCESS;

                                        currentDate = SELECT_TYPE;

                                        refeshUI(currentState);
                                    }else {
                                        T.showShort(UIUtils.getContext(),"该供应商没有数据");
                                    }
                                } else {
                                    T.showShort(UIUtils.getContext(),"该供应商没有数据");
                                }
                            } else {
                                T.showShort(UIUtils.getContext(),"供应商筛选失败");
                            }
                        } else {
                            T.showShort(UIUtils.getContext(),"该供应商没有数据");
                        }


                    }
                }else {
                    LogUtils.i("加载数据之后---------");
                    if (obj != null){
                        Gson gson = new Gson();
                        PurchaserUnhandler unhandler = null;

                        try {
                            unhandler = gson.fromJson(obj + "", PurchaserUnhandler.class);
                        }catch (Exception e){
                            if (currentPage >= 1){
                                currentPage = currentPage-1;
                            }
                            e.printStackTrace();
                        }
                        if (unhandler == null){
                            if (currentPage >= 1){
                                currentPage = currentPage-1;
                            }
                            mOrderCommontRefresh.finishRefreshLoadMore();
                            T.showShort(UIUtils.getContext(),"加载更多失败");
                            return;
                        }

                        if (unhandler.msg == 1) {
                            if (unhandler.data.dTHList != null && unhandler.data.dTHList.size() != 0){

                                List<PurchaserUnhandler.DataEntity.DTHListEntity> list = unhandler.data.dTHList;
                                for (int i = 0; i < list.size(); i++) {
                                    PurchaserUnhandler.DataEntity.DTHListEntity listEntity = list.get(i);
                                    String date = listEntity.THDate;
                                    date = date.substring(0, 10);
                                    String dateAndID = date+"#"+listEntity.supplierId;

                                    mDateAndIdList.add(dateAndID);
                                    Map<String, List<PurchaserUnhandler.DataEntity.DTHListEntity>> supplierMap = mDateMap.get(date);
                                    if (supplierMap == null) {
                                        Map<String, List<PurchaserUnhandler.DataEntity.DTHListEntity>> mMap = new HashMap<>();
                                        String supplierId = listEntity.supplierId;
                                        List<PurchaserUnhandler.DataEntity.DTHListEntity> lastList = mMap.get(supplierId);
                                        if (lastList == null) {
                                            List<PurchaserUnhandler.DataEntity.DTHListEntity> newList = new ArrayList<>();

                                            mSuuplierId.add(supplierId);
                                            mSuuplierName.add(listEntity.supplierName);

                                            newList.add(listEntity);
                                            mMap.put(supplierId, newList);
                                            mSupplierDetail.put(supplierId, listEntity);

                                        } else {
                                            lastList.add(listEntity);
                                        }
                                        mDateMap.put(date, mMap);
                                        mDateList.add(date);
                                    } else {
                                        List<PurchaserUnhandler.DataEntity.DTHListEntity> suppList = supplierMap.get(listEntity.supplierId);
                                        if (suppList == null) {
                                            List<PurchaserUnhandler.DataEntity.DTHListEntity> newList = new ArrayList<>();
                                            newList.add(listEntity);
                                            supplierMap.put(listEntity.supplierId, newList);

                                            mSupplierDetail.put(listEntity.supplierId, listEntity);

                                            mSuuplierId.add(listEntity.supplierId);
                                            mSuuplierName.add(listEntity.supplierName);
                                        } else {
                                            suppList.add(listEntity);
                                        }
                                    }
                                    mDateAndIdList = removeDuplicateWithOrder(mDateAndIdList);

                                    mSuuplierName = removeDuplicateWithOrder(mSuuplierName);
                                    mSuuplierId = removeDuplicateWithOrder(mSuuplierId);

                                    mOldDateMap.clear();
                                    mOldDateMap.putAll(mDateMap);
                                    T.showShort(UIUtils.getContext(), "加载更多成功");
                                    currentDate = SELECT_TYPE;
                                }
                            }else {
                                T.showShort(UIUtils.getContext(),"没有更多数据");
                                if (currentPage >= 1){
                                    currentPage = currentPage-1;
                                }
                            }
                        }else {
                            T.showShort(UIUtils.getContext(),"加载更多失败");
                            if (currentPage >= 1){
                                currentPage = currentPage-1;
                            }
                        }
                    }else {
                        T.showShort(UIUtils.getContext(),"加载更多失败");
                        if (currentPage >= 1){
                            currentPage = currentPage-1;
                        }
                    }
                    mOrderCommontRefresh.finishRefreshLoadMore();
                    mAnimAdapter.notifyDataSetChanged();
                }


            }

            @Override
            protected void onFail() {
                if ( type == 1){
                    T.showShort(UIUtils.getContext(),"供应商筛选失败");
                }else {
                    if (currentPage >= 1){
                        currentPage = currentPage-1;
                    }
                    T.showShort(UIUtils.getContext(),"加载更多失败");
                    mOrderCommontRefresh.finishRefreshLoadMore();
                }
                dismissLoadDialog();

            }

            @Override
            protected void onTimeOut() {
                if (type == 1){
                    T.showShort(UIUtils.getContext(),"供应商筛选超时");
                }else {
                    if (currentPage >= 1){
                        currentPage = currentPage-1;
                    }
                    T.showShort(UIUtils.getContext(),"加载更多失败");
                    mOrderCommontRefresh.finishRefreshLoadMore();
                }
                dismissLoadDialog();
            }
        };
        LogUtils.i("url-------"+url);
        manager.get(url);

    }

    private void sortData(int position) {
        Map<String, Map<String, List<PurchaserUnhandler.DataEntity.DTHListEntity>>> newData = new HashMap<>();
        List<String> dateAndId = new ArrayList<>();
        for (int i = 0; i < mDateList.size(); i++) {
            String date = mDateList.get(i);

            Map<String, List<PurchaserUnhandler.DataEntity.DTHListEntity>> map = mOldDateMap.get(date);

            List<PurchaserUnhandler.DataEntity.DTHListEntity> list = map.get(mSuuplierId.get(position));
            if (list != null){
                Map<String, List<PurchaserUnhandler.DataEntity.DTHListEntity>> newDateMap = new HashMap<>();
                newDateMap.put(mSuuplierId.get(position), list);

                dateAndId.add(date + "#" + (mSuuplierId.get(position)));
                newData.put(date,newDateMap);
            }
        }
        mDateMap = newData;
        mDateAndIdList = dateAndId;
        mAnimAdapter.notifyDataSetChanged();
        currentDate = SELECT_TYPE;
    }


    private void initEvent() {
        mBuyerUnhandlerTitleName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if (mSuuplierName.size() != 0){
                    String[] suppliers = new String[mSuuplierName.size()];
                    for (int i = 0; i < mSuuplierName.size(); i++) {
                        suppliers[i] = mSuuplierName.get(i);
                    }
                    intent.putExtra("suppliers",suppliers);
                    LogUtils.i("供应商数组的长度-------"+suppliers.length);

                    intent.setClass(BuyerUnhandlerActivityOther.this, BuyerSupplierSelect.class);
                    startActivityForResult(intent, 1);
                }else {
                    T.showShort(UIUtils.getContext(),"无可筛选的供应商");
                }

            }
        });

        mBuyerUnhandlerTabcontainer.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                currentPage = 1;

                changeFragment(checkedId,TYPE_FIRST,1);
            }
        });
        mErrorBtnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(mBuyerUnhandlerTabcontainer.getCheckedRadioButtonId(),TYPE_FIRST,1);//根据当前选中的button，去重新请求数据
            }
        });

        mOrderCommontRefresh.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                currentPage = 1;
                changeFragment(mBuyerUnhandlerTabcontainer.getCheckedRadioButtonId(),TYPE_REFRESH,0);

            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                currentPage = currentPage+1;
                LogUtils.i("加载更多---------"+currentPage);
                if (currentDate == COMMONT_TYPE){
                    String url = "";
                    switch (mBuyerUnhandlerTabcontainer.getCheckedRadioButtonId()){
                        case R.id.buyer_unhandler_tabcontainer_r1:
                            url = Constants.URLS.BUYER_UNHANDLER  + "?timeMark=0"+"&page="+currentPage;
                            break;
                        case R.id.buyer_unhandler_tabcontainer_r2:
                            url = Constants.URLS.BUYER_UNHANDLER  + "?timeMark=1"+"&page="+currentPage;
                            break;
                        case R.id.buyer_unhandler_tabcontainer_r3:
                            url = Constants.URLS.BUYER_UNHANDLER  +"?page="+currentPage;
                            break;
                        default:
                            break;
                    }
                    loadMore(url);
                }else {
                    //选择之后的加载更多
                    LogUtils.i("筛选后，加载更多---------");
                    if (mCurrentPosition != -1){
                        selectSupplier(mCurrentPosition,2);
                    }

                }
            }
        });
    }

    private void changeFragment(int checkedId,int type,int where) {
        if (where == 1){
            mOrderCommontRefresh.finishRefresh();
            mOrderCommontRefresh.finishRefreshLoadMore();
        }else {

        }
        currentDate = COMMONT_TYPE;

        switch (checkedId) {

            case R.id.buyer_unhandler_tabcontainer_r1:
                initData(Constants.URLS.BUYER_UNHANDLER  + "?timeMark=0",type);
                break;
            case R.id.buyer_unhandler_tabcontainer_r2:
                initData(Constants.URLS.BUYER_UNHANDLER + "?timeMark=1",type);
                break;
            case R.id.buyer_unhandler_tabcontainer_r3:
                initData(Constants.URLS.BUYER_UNHANDLER ,type);
                break;
        }


    }

}
