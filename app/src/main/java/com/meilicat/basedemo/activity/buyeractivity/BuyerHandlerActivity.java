package com.meilicat.basedemo.activity.buyeractivity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.google.gson.Gson;
import com.meilicat.basedemo.R;
import com.meilicat.basedemo.base.BaseActivity;
import com.meilicat.basedemo.bean.LoginBean;
import com.meilicat.basedemo.bean.purchaser.PurchaserHandler;
import com.meilicat.basedemo.conf.Constants;
import com.meilicat.basedemo.utils.HttpManager;
import com.meilicat.basedemo.utils.ImageLoaderUtil;
import com.meilicat.basedemo.utils.LogUtils;
import com.meilicat.basedemo.utils.T;
import com.meilicat.basedemo.utils.UIUtils;
import com.meilicat.basedemo.view.CustomBuyerBrandView;
import com.meilicat.basedemo.view.SubListView;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;


public class BuyerHandlerActivity extends BaseActivity {
    @Bind(R.id.buyer_handler_title_back)
    RelativeLayout mBuyerHandlerTitleBack;
    @Bind(R.id.buyer_handler_tabcontainer)
    RadioGroup mBuyerHandlerTabcontainer;
    @Bind(R.id.buyer_handler_layout)
    ListView mBuyerHandlerLayout;
    @Bind(R.id.buyer_handler_today_num)
    TextView mBuyerHandlerTodayNum;
    @Bind(R.id.buyer_handler_week_num)
    TextView mBuyerHandlerWeekNum;
    @Bind(R.id.buyer_handler_month_num)
    TextView mBuyerHandlerMonthNum;
    @Bind(R.id.buyer_handler_history_num)
    TextView mBuyerHandlerHistoryNum;
    @Bind(R.id.buyer_handler_title_name)
    TextView mBuyerHandlerTitleName;
    @Bind(R.id.order_commont_refresh)
    MaterialRefreshLayout mOrderCommontRefresh;
    @Bind(R.id.buyer_handler_loading)
    ProgressWheel mBuyerHandlerLoading;
    @Bind(R.id.error_btn_retry)
    Button mErrorBtnRetry;
    @Bind(R.id.buyer_handler_error)
    LinearLayout mBuyerHandlerError;
    @Bind(R.id.buyer_handler_empty)
    LinearLayout mBuyerHandlerEmpty;
    @Bind(R.id.buyer_handler_new_order)
    Button mBuyerHandlerNewOrder;

    public static final int STATE_EMPTY = 0;
    public static final int STATE_ERROR = 1;
    public static final int STATE_SUCCESS = 2;
    public static final int STATE_NONE = -1;
    public static final int STATE_LOADING = 3;

    private int currentState = STATE_NONE;

    public static final int TYPE_FIRST = 1;
    public static final int TYPE_REFRESH = 2;

    private int todayNum = 0;
    private int sevenNum = 0;
    private int monthNum = 0;
    private int allNum = 0;
    private Map<String, Map<String, List<PurchaserHandler.DataEntity.YTHListEntity>>> mDateMap;
    private Map<String, Map<String, List<PurchaserHandler.DataEntity.YTHListEntity>>> mOldDateMap;
    private List<String> mSuuplierId;
    private List<String> mSuuplierName;
    private List<String> mDateList;
    private Map<String, PurchaserHandler.DataEntity.YTHListEntity> mSupplierDetail;
    private List<String> mDateAndIdList;
    private Map<String, Boolean> mCheckMap;
    private int mCurrentPosition;

    @Override
    public void setContent() {

        setContentView(R.layout.activity_buyer_handler);
        ButterKnife.bind(this);
        mBuyerHandlerLoading.setBarColor(UIUtils.getColor(R.color.login_btn_pink));

        initData(Constants.URLS.BUYER_HANDLER + "?timeMark=" + 0, TYPE_FIRST);

    }

    private void initData(String url,int type) {
        if (type == TYPE_FIRST){
            currentState = STATE_LOADING;
            currentDate = COMMONT_TYPE;
            mCurrentPosition = -1;
            refeshUI(currentState);
        }

        mDateMap = null;

        mSuuplierId = new ArrayList<>();
        mDateList = new ArrayList<>();
        mSupplierDetail = new HashMap<>();
        mSuuplierName = new ArrayList<>();
        mDateAndIdList = new ArrayList<>();
        mCheckMap = new HashMap<>();
        mOldDateMap = new HashMap<>();
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

                LogUtils.i("obj-------"+obj);

                if (obj != null) {
                    Gson gson = new Gson();
                    PurchaserHandler handler =null;

                    try {
                        handler   = gson.fromJson(obj + "", PurchaserHandler.class);
                    }catch (Exception e){

                        e.printStackTrace();

                    }
                    if (handler == null){
                        currentState = STATE_ERROR;
                        refeshUI(currentState);
                        mOrderCommontRefresh.finishRefresh();
                        return;
                    }

                    if (handler != null) {
                        if (handler.msg == 1) {
                            if (handler.data != null) {
                                if (handler.data.headCount != null) {
                                    LogUtils.i("订单的数量-------"+todayNum+"--sevenNum---"+sevenNum+"---monthNum--"+monthNum+"---allNum--"+allNum);
                                    todayNum = handler.data.headCount.yTHInToday;
                                    sevenNum = handler.data.headCount.yTH1WeekAgo;
                                    monthNum = handler.data.headCount.yTH30daysAgo;
                                    allNum = handler.data.headCount.yTHAll;
                                }

                                if (handler.data.yTHList != null && handler.data.yTHList.size() != 0) {
                                    mDateMap = new HashMap<>();

                                    List<PurchaserHandler.DataEntity.YTHListEntity> list = handler.data.yTHList;
                                    LogUtils.i("list.size（）---------"+list.size());
                                    for (int i = 0; i < list.size(); i++) {
                                        PurchaserHandler.DataEntity.YTHListEntity listEntity = list.get(i);

                                        String date = listEntity.THDate;
                                        if (TextUtils.isEmpty(date)){
                                            date = "1111-11-11";
                                            listEntity.THDate = "1111-11-11";
                                        }else {
                                            date = date.substring(0, 10);
                                        }

                                        String dateAndID = date + "#" + listEntity.supplierId;
                                        String dateAndIdAndOrder = date + "#" + listEntity.supplierId+"#"+listEntity.orderId;

                                        mCheckMap.put(dateAndIdAndOrder,false);
                                        mDateAndIdList.add(dateAndID);

                                        Map<String, List<PurchaserHandler.DataEntity.YTHListEntity>> supplierMap = mDateMap.get(date);

                                        if (supplierMap == null) {
                                            Map<String, List<PurchaserHandler.DataEntity.YTHListEntity>> mMap = new HashMap<>();
                                            String supplierId = listEntity.supplierId;
                                            List<PurchaserHandler.DataEntity.YTHListEntity> lastList = mMap.get(supplierId);
                                            if (lastList == null) {
                                                List<PurchaserHandler.DataEntity.YTHListEntity> newList = new ArrayList<>();

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
                                            List<PurchaserHandler.DataEntity.YTHListEntity> suppList = supplierMap.get(listEntity.supplierId);
                                            if (suppList == null) {
                                                List<PurchaserHandler.DataEntity.YTHListEntity> newList = new ArrayList<>();
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
                                } else {
                                    currentState = STATE_EMPTY;
                                }
                            } else {
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
                refeshUI(currentState);
                mOrderCommontRefresh.finishRefresh();
            }
        };
        manager.get(url);

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
        mBuyerHandlerError.setVisibility((type == STATE_ERROR) || (type == STATE_NONE) ? View.VISIBLE : View.GONE);
        mBuyerHandlerEmpty.setVisibility((type == STATE_EMPTY) || (type == STATE_NONE) ? View.VISIBLE : View.GONE);
        mBuyerHandlerLayout.setVisibility((type == STATE_SUCCESS) || (type == STATE_NONE) ? View.VISIBLE : View.GONE);
        mBuyerHandlerLoading.setVisibility((type == STATE_LOADING) || (type == STATE_NONE) ? View.VISIBLE : View.GONE);
        initView();
    }

    @Override
    public void initTitle() {
        mBuyerHandlerTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void initView() {
        judgeIsSelect();
        mBuyerHandlerTodayNum.setText(todayNum + "");
        mBuyerHandlerWeekNum.setText(sevenNum + "");
        mBuyerHandlerMonthNum.setText(monthNum + "");
        mBuyerHandlerHistoryNum.setText(allNum + "");


        if (mDateMap != null) {
            if (baseAdapter != null) {
                baseAdapter.notifyDataSetChanged();
            } else {
                baseAdapter = new LvAdapter();
                mBuyerHandlerLayout.setAdapter(baseAdapter);
            }
        }

        initEvent();
    }


    private void initEvent() {
        mBuyerHandlerTabcontainer.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                currentPage = 1;
                changeFragment(checkedId, TYPE_FIRST, 1);
            }
        });
        mBuyerHandlerTitleName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (mSuuplierName.size() != 0) {
                    String[] suppliers = new String[mSuuplierName.size()];
                    Intent intent = new Intent();
                    for (int i = 0; i < mSuuplierName.size(); i++) {
                        suppliers[i] = mSuuplierName.get(i);
                    }
                    intent.putExtra("suppliers", suppliers);

                    intent.setClass(BuyerHandlerActivity.this, BuyerSupplierSelect.class);
                    startActivityForResult(intent, 1);
                } else {
                    T.showShort(UIUtils.getContext(), "没有可以筛选的供应商");
                }

            }
        });
        mOrderCommontRefresh.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                currentPage = 1;

                changeFragment(mBuyerHandlerTabcontainer.getCheckedRadioButtonId(), TYPE_REFRESH, 0);
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                currentPage = currentPage + 1;
                if (currentDate == COMMONT_TYPE) {
                    String url = "";
                    switch (mBuyerHandlerTabcontainer.getCheckedRadioButtonId()) {
                        case R.id.buyer_handler_tabcontainer_r1:
                            url = Constants.URLS.BUYER_HANDLER + "?timeMark=0" + "&page=" + currentPage;
                            break;
                        case R.id.buyer_handler_tabcontainer_r2:
                            url = Constants.URLS.BUYER_HANDLER + "?timeMark=1" + "&page=" + currentPage;
                            break;
                        case R.id.buyer_handler_tabcontainer_r3:
                            url = Constants.URLS.BUYER_HANDLER + "?timeMark=2" + "&page=" + currentPage;
                            break;
                        case R.id.buyer_handler_tabcontainer_r4:
                            url = Constants.URLS.BUYER_HANDLER + "?page=" + currentPage;
                            break;
                        default:
                            break;
                    }
                    loadMore(url);
                } else {
                    if ( mCurrentPosition != -1){
                        selectSupplier(mCurrentPosition,0);
                    }

                }
            }
        });

        mErrorBtnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(mBuyerHandlerTabcontainer.getCheckedRadioButtonId(), TYPE_FIRST, 1);
            }
        });

        mBuyerHandlerNewOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCheckMap != null) {
                    String url = Constants.URLS.BUYER_HANDLER_ORDER;
                    Iterator<Map.Entry<String, Boolean>> iterator = mCheckMap.entrySet().iterator();
                    List<String> select = new ArrayList<>();

                    while (iterator.hasNext()) {
                        Map.Entry<String, Boolean> next = iterator.next();
                        String key = next.getKey();
                        boolean value = next.getValue();
                        if (value) {
                            select.add(key);
                        }
                    }
                    List<PurchaserHandler.DataEntity.YTHListEntity> selectEntity = new ArrayList();
                    for (int i = 0; i < select.size(); i++) {
                        String dateAndId = select.get(i);
                        String[] strings = dateAndId.split("#");
                        String date = strings[0];
                        String id = strings[1];
                        String orderId = strings[2];
                        int selectId = Integer.parseInt(orderId);
                        List<PurchaserHandler.DataEntity.YTHListEntity> list = mDateMap.get(date).get(id);
                        for (int j = 0; j < list.size(); j++) {
                            PurchaserHandler.DataEntity.YTHListEntity entity = list.get(j);
                            int order = entity.orderId;
                            if (order == selectId) {
                                selectEntity.add(entity);
                            }
                        }
                    }


                    JSONArray array = new JSONArray();

                    for (int i = 0; i < selectEntity.size(); i++) {
                        PurchaserHandler.DataEntity.YTHListEntity entity = selectEntity.get(i);
                        Map<String, Object> entityMap = new HashMap<>();

                        String goodsNum = entity.purchaseNum + "";
                        double money = entity.totalMoneyOne;
                        String detailId = entity.id;

                        entityMap.put("purchaseNum", goodsNum);
                        entityMap.put("totalMoneyOne", money);
                        entityMap.put("supplierPurchaseDetailId", detailId);
                        JSONObject object = new JSONObject(entityMap);

                        array.put(object);
                    }
                    String s = array.toString();
                    try {
                        url = url + "?purchaseOrderJson=" + s;
                        LogUtils.i("url----------" + url);

                        HttpManager manger = new HttpManager(UIUtils.getContext()) {
                            @Override
                            protected void onSuccess(Object obj) {
                                dismissLoadDialog();
                                clearMap();
                                if (obj != null) {
                                    Gson gson = new Gson();
                                    LoginBean loginBean = null;
                                    try {
                                        loginBean = gson.fromJson(obj + "", LoginBean.class);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    if (loginBean == null) {
                                        T.showShort(UIUtils.getContext(), "生成采购单失败");
                                        return;
                                    }

                                    if (loginBean.msg == 1) {
                                        T.showShort(UIUtils.getContext(), "生成采购单成功");

                                    } else {
                                        T.showShort(UIUtils.getContext(), "生成采购单失败");
                                    }
                                }
                                mBuyerHandlerNewOrder.setVisibility(View.GONE);

                                baseAdapter.notifyDataSetChanged();
                            }

                            @Override
                            protected void onFail() {
                                dismissLoadDialog();
                                T.showShort(UIUtils.getContext(), "生成采购单失败");
                                mBuyerHandlerNewOrder.setVisibility(View.VISIBLE);
                            }

                            @Override
                            protected void onTimeOut() {
                                dismissLoadDialog();
                                T.showShort(UIUtils.getContext(), "请求超时");
                                mBuyerHandlerNewOrder.setVisibility(View.VISIBLE);
                            }
                        };

                        showLoadDialog();
                        LogUtils.i("url-------" + url);
                        manger.get(url);
                        mBuyerHandlerNewOrder.setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                        T.showShort(UIUtils.getContext(), "生成采购单失败");
                    }
                }
            }
        });
    }

    private void clearMap() {
        Map<String,Boolean> newMap = new HashMap<>();
        Iterator<Map.Entry<String, Boolean>> iterator = mCheckMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, Boolean> next = iterator.next();
            String key = next.getKey();
            Boolean value = next.getValue();
            if (value){
                value = false;
            }
            newMap.put(key,value);
        }
        mCheckMap.clear();
        mCheckMap.putAll(newMap);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //返回值
        if (resultCode == 1) {
            int currentPosition = data.getIntExtra("currentPosition",-1);
            if (currentPosition != -1) {
//                sortData(currentPosition);
                mCurrentPosition = currentPosition;
                selectSupplier(currentPosition,1);
            }
        }
    }

    private void selectSupplier(int currentPosition, final int type) {
        showLoadDialog();
        String url ="";

        String supplierId = mSuuplierId.get(currentPosition);


        if (type == 1){
            //说明是刷新
            currentPage = 1;
            switch (mBuyerHandlerTabcontainer.getCheckedRadioButtonId()) {

                case R.id.buyer_handler_tabcontainer_r1:
                    url = Constants.URLS.BUYER_HANDLER + "?timeMark=0&supplierId=" + supplierId;
                    break;
                case R.id.buyer_handler_tabcontainer_r2:
                    url = Constants.URLS.BUYER_HANDLER + "?timeMark=1&supplierId=" + supplierId;
                    break;
                case R.id.buyer_handler_tabcontainer_r3:
                    url = Constants.URLS.BUYER_HANDLER + "?timeMark=2&supplierId=" + supplierId;
                    break;
                case R.id.buyer_handler_tabcontainer_r4:
                    url = Constants.URLS.BUYER_HANDLER + "?supplierId=" + supplierId;
                    break;
            }
        }else {
            //说明是加载更多
            switch (mBuyerHandlerTabcontainer.getCheckedRadioButtonId()) {

                case R.id.buyer_handler_tabcontainer_r1:
                    url = Constants.URLS.BUYER_HANDLER + "?timeMark=0&supplierId=" + supplierId+"&page="+currentPage;
                    break;
                case R.id.buyer_handler_tabcontainer_r2:
                    url = Constants.URLS.BUYER_HANDLER + "?timeMark=1&supplierId=" + supplierId+"&page="+currentPage;
                    break;
                case R.id.buyer_handler_tabcontainer_r3:
                    url = Constants.URLS.BUYER_HANDLER + "?timeMark=2&supplierId=" + supplierId+"&page="+currentPage;
                    break;
                case R.id.buyer_handler_tabcontainer_r4:
                    url = Constants.URLS.BUYER_HANDLER + "?supplierId=" + supplierId+"&page="+currentPage;
                    break;
            }
        }

        HttpManager manager = new HttpManager(UIUtils.getContext()){
            @Override
            protected void onSuccess(Object obj) {
                dismissLoadDialog();

                if (type == 1){
                    mDateMap = null;

                    mSuuplierId = new ArrayList<>();
                    mDateList = new ArrayList<>();
                    mSupplierDetail = new HashMap<>();
                    mSuuplierName = new ArrayList<>();
                    mDateAndIdList = new ArrayList<>();
                    mCheckMap = new HashMap<>();
                    mOldDateMap = new HashMap<>();
                    if (obj != null) {
                        Gson gson = new Gson();
                        PurchaserHandler handler =null;

                        try {
                            handler   = gson.fromJson(obj + "", PurchaserHandler.class);
                        }catch (Exception e){

                            e.printStackTrace();
                        }
                        if (handler == null){
                            T.showShort(UIUtils.getContext(),"供应商筛选失败");
                            return;
                        }

                        if (handler != null) {
                            if (handler.msg == 1) {
                                if (handler.data != null) {
                                    if (handler.data.headCount != null) {
                                        LogUtils.i("订单的数量-------"+todayNum+"--sevenNum---"+sevenNum+"---monthNum--"+monthNum+"---allNum--"+allNum);
                                        todayNum = handler.data.headCount.yTHInToday;
                                        sevenNum = handler.data.headCount.yTH1WeekAgo;
                                        monthNum = handler.data.headCount.yTH30daysAgo;
                                        allNum = handler.data.headCount.yTHAll;
                                    }

                                    if (handler.data.yTHList != null && handler.data.yTHList.size() != 0) {
                                        mDateMap = new HashMap<>();

                                        List<PurchaserHandler.DataEntity.YTHListEntity> list = handler.data.yTHList;
                                        LogUtils.i("list.size（）---------"+list.size());
                                        for (int i = 0; i < list.size(); i++) {
                                            PurchaserHandler.DataEntity.YTHListEntity listEntity = list.get(i);

                                            String date = listEntity.THDate;
                                            if (TextUtils.isEmpty(date)){
                                                date = "1111-11-11";
                                                listEntity.THDate = "1111-11-11";
                                            }else {
                                                date = date.substring(0, 10);
                                            }

                                            String dateAndID = date + "#" + listEntity.supplierId;
                                            String dateAndIdAndOrder = date + "#" + listEntity.supplierId+"#"+listEntity.orderId;

                                            mCheckMap.put(dateAndIdAndOrder,false);
                                            mDateAndIdList.add(dateAndID);

                                            Map<String, List<PurchaserHandler.DataEntity.YTHListEntity>> supplierMap = mDateMap.get(date);

                                            if (supplierMap == null) {
                                                Map<String, List<PurchaserHandler.DataEntity.YTHListEntity>> mMap = new HashMap<>();
                                                String supplierId = listEntity.supplierId;
                                                List<PurchaserHandler.DataEntity.YTHListEntity> lastList = mMap.get(supplierId);
                                                if (lastList == null) {
                                                    List<PurchaserHandler.DataEntity.YTHListEntity> newList = new ArrayList<>();

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
                                                List<PurchaserHandler.DataEntity.YTHListEntity> suppList = supplierMap.get(listEntity.supplierId);
                                                if (suppList == null) {
                                                    List<PurchaserHandler.DataEntity.YTHListEntity> newList = new ArrayList<>();
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
                                    } else {
                                        T.showShort(UIUtils.getContext(),"该供应商没有数据");
                                    }
                                } else {
                                    T.showShort(UIUtils.getContext(),"该供应商没有数据");
                                }
                            } else {
                                T.showShort(UIUtils.getContext(),"该供应商没有数据");
                            }
                        } else {
                            T.showShort(UIUtils.getContext(),"筛选供应商失败");
                        }
                    } else {
                        T.showShort(UIUtils.getContext(),"该供应商没有数据");
                    }
                }else {
                    if (obj != null){
                        Gson gson = new Gson();

                        PurchaserHandler handler = null;
                        try {
                            handler  = gson.fromJson(obj + "", PurchaserHandler.class);
                        }catch (Exception e){
                            e.printStackTrace();

                        }
                        if (handler == null){
                            if (currentPage >= 1){
                                currentPage = currentPage-1;
                            }

                            T.showShort(UIUtils.getContext(),"加载更多失败");
                            return;
                        }

                        if (handler.msg == 1) {
                            if (handler.data.yTHList != null && handler.data.yTHList.size() != 0){
                                List<PurchaserHandler.DataEntity.YTHListEntity> list = handler.data.yTHList;
                                for (int i = 0; i < list.size(); i++) {
                                    PurchaserHandler.DataEntity.YTHListEntity listEntity = list.get(i);
                                    String date = listEntity.THDate;
                                    if (TextUtils.isEmpty(date)){
                                        listEntity.createTime = "1111-11-11";
                                        date = "1111-11-11";
                                    }else {
                                        date = date.substring(0, 10);
                                    }

                                    String dateAndID = date+"#"+listEntity.supplierId;

                                    mDateAndIdList.add(dateAndID);
                                    Map<String, List<PurchaserHandler.DataEntity.YTHListEntity>> supplierMap = mDateMap.get(date);
                                    if (supplierMap == null) {
                                        Map<String, List<PurchaserHandler.DataEntity.YTHListEntity>> mMap = new HashMap<>();
                                        String supplierId = listEntity.supplierId;
                                        List<PurchaserHandler.DataEntity.YTHListEntity> lastList = mMap.get(supplierId);
                                        if (lastList == null) {
                                            List<PurchaserHandler.DataEntity.YTHListEntity> newList = new ArrayList<>();

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
                                        List<PurchaserHandler.DataEntity.YTHListEntity> suppList = supplierMap.get(listEntity.supplierId);
                                        if (suppList == null) {
                                            List<PurchaserHandler.DataEntity.YTHListEntity> newList = new ArrayList<>();
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
                                    T.showShort(UIUtils.getContext(),"加载更多成功");

                                }
                            }else {
                                if (currentPage >= 1){
                                    currentPage = currentPage-1;
                                }
                                T.showShort(UIUtils.getContext(),"没有更多数据了");
                            }
                        }else {
                            if (currentPage >= 1){
                                currentPage = currentPage-1;
                            }
                            T.showShort(UIUtils.getContext(),"没有更多数据了");
                        }
                    }else {
                        if (currentPage >= 1){
                            currentPage = currentPage-1;
                        }
                        T.showShort(UIUtils.getContext(),"加载更多失败");
                    }
                    mOrderCommontRefresh.finishRefreshLoadMore();
                    baseAdapter.notifyDataSetChanged();
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

        LogUtils.i("supplierurl------"+url);
        manager.get(url);

    }

    private void sortData(int position) {
        Map<String, Map<String, List<PurchaserHandler.DataEntity.YTHListEntity>>> newData = new HashMap<>();
        List<String> dateAndId = new ArrayList<>();
        for (int i = 0; i < mDateList.size(); i++) {
            String date = mDateList.get(i);

            Map<String, List<PurchaserHandler.DataEntity.YTHListEntity>> map = mOldDateMap.get(date);
            List<PurchaserHandler.DataEntity.YTHListEntity> list = map.get(mSuuplierId.get(position));
            if (list != null){
                Map<String, List<PurchaserHandler.DataEntity.YTHListEntity>> newDateMap = new HashMap<>();
                newDateMap.put(mSuuplierId.get(position), list);

                dateAndId.add(date + "#" + (mSuuplierId.get(position)));
                newData.put(date, newDateMap);
            }
        }
        mDateMap = newData;
        mDateAndIdList = dateAndId;
        currentDate = SELECT_TYPE;
        baseAdapter.notifyDataSetChanged();

    }

    private void changeFragment(int checkedId,int type,int which) {
        if (which == 1){
            mOrderCommontRefresh.finishRefresh();
            mOrderCommontRefresh.finishRefreshLoadMore();
        }
        currentDate = COMMONT_TYPE;

        switch (checkedId) {
            case R.id.buyer_handler_tabcontainer_r1:
                initData(Constants.URLS.BUYER_HANDLER  + "?timeMark=" + 0,type);
                break;
            case R.id.buyer_handler_tabcontainer_r2:
                initData(Constants.URLS.BUYER_HANDLER  + "?timeMark=" + 1,type);
                break;
            case R.id.buyer_handler_tabcontainer_r3:
                initData(Constants.URLS.BUYER_HANDLER + "?timeMark=" + 2,type);
                break;
            case R.id.buyer_handler_tabcontainer_r4:
                initData(Constants.URLS.BUYER_HANDLER ,type);
                break;
            default:
                break;
        }
    }

    public static final int SELECT_TYPE = 1;
    public static final int COMMONT_TYPE = 2;

    private int currentDate = -1;
    int currentPage = 1;

    public void loadMore(String url){

        HttpManager manger = new HttpManager(UIUtils.getContext()){
            @Override
            protected void onSuccess(Object obj) {

                LogUtils.i("obj-------"+obj);
                if (obj != null){
                    Gson gson = new Gson();

                    PurchaserHandler unhandler = null;
                    try {
                        unhandler  = gson.fromJson(obj + "", PurchaserHandler.class);
                    }catch (Exception e){
                       e.printStackTrace();
                    }
                    if (unhandler == null){
                        currentPage = currentPage-1;
                        T.showShort(UIUtils.getContext(),"加载更多失败");
                        return;
                    }

                    if (unhandler.msg == 1) {
                        if (unhandler.data.yTHList != null && unhandler.data.yTHList.size() != 0){
                            List<PurchaserHandler.DataEntity.YTHListEntity> list = unhandler.data.yTHList;
                            for (int i = 0; i < list.size(); i++) {
                                PurchaserHandler.DataEntity.YTHListEntity listEntity = list.get(i);
                                String date = listEntity.THDate;
                                if (TextUtils.isEmpty(date)){
                                    listEntity.createTime = "1111-11-11";
                                    date = "1111-11-11";
                                }else {
                                    date = date.substring(0, 10);
                                }

                                String dateAndID = date+"#"+listEntity.supplierId;

                                mDateAndIdList.add(dateAndID);
                                Map<String, List<PurchaserHandler.DataEntity.YTHListEntity>> supplierMap = mDateMap.get(date);
                                if (supplierMap == null) {
                                    Map<String, List<PurchaserHandler.DataEntity.YTHListEntity>> mMap = new HashMap<>();
                                    String supplierId = listEntity.supplierId;
                                    List<PurchaserHandler.DataEntity.YTHListEntity> lastList = mMap.get(supplierId);
                                    if (lastList == null) {
                                        List<PurchaserHandler.DataEntity.YTHListEntity> newList = new ArrayList<>();

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
                                    List<PurchaserHandler.DataEntity.YTHListEntity> suppList = supplierMap.get(listEntity.supplierId);
                                    if (suppList == null) {
                                        List<PurchaserHandler.DataEntity.YTHListEntity> newList = new ArrayList<>();
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
                                T.showShort(UIUtils.getContext(),"加载更多成功");
                            }
                        }else {
                            T.showShort(UIUtils.getContext(),"没有更多数据了");
                            currentPage = currentPage-1;
                        }
                    }else {
                        T.showShort(UIUtils.getContext(),"没有更多数据了");
                        currentPage = currentPage-1;
                    }
                }else {
                    T.showShort(UIUtils.getContext(),"加载更多失败");
                    if (currentPage >= 1){
                        currentPage = currentPage-1;
                    }

                }
                mOrderCommontRefresh.finishRefreshLoadMore();
                baseAdapter.notifyDataSetChanged();
            }

            @Override
            protected void onFail() {
                currentPage = currentPage-1;
                T.showShort(UIUtils.getContext(),"加载更多失败");
                mOrderCommontRefresh.finishRefreshLoadMore();
            }

            @Override
            protected void onTimeOut() {
                currentPage = currentPage-1;
                T.showShort(UIUtils.getContext(),"加载更多超时");
                mOrderCommontRefresh.finishRefreshLoadMore();
            }
        };

        LogUtils.i("loadmore--------"+url);
        manger.get(url);

    }


    LvAdapter baseAdapter;


    class LvAdapter extends BaseAdapter {
        ChildItemAdapter ChildItemAdapter;


        @Override
        public int getCount() {
            int count = 0;
            if (mDateList != null) {
                for (int i = 0; i < mDateList.size(); i++) {

                    Map<String, List<PurchaserHandler.DataEntity.YTHListEntity>> map = mDateMap.get(mDateList.get(i));
                    if (map != null) {
                        count = count + map.size();
                    }
                }
            }
            return count;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
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
            String dateAndId = mDateAndIdList.get(position);
            String[] dates = dateAndId.split("#");
            String date = dates[0];
            String id = dates[1];

            PurchaserHandler.DataEntity.YTHListEntity entity = mSupplierDetail.get(id);
            if (!TextUtils.isEmpty(entity.avatarImageURL)){
                holder.mBrandView.setBrandLogo(entity.avatarImageURL);
            }

            if (TextUtils.isEmpty(entity.address)){
                holder.mBrandView.setBrandAddress("暂无地址");
            }else {
                holder.mBrandView.setBrandAddress(entity.address);
            }

            String suoolierName = entity.supplierName.replaceAll(" ", "");
            holder.mBrandView.setBrandName(suoolierName);

            holder.mBrandView.setBrandNumber(entity.mobilePhoneNum);
            holder.mBrandView.setBrandSaler(entity.contractPerson);
            holder.mBrandView.hintCheckBox();

            holder.mBrandView.setBrandTime(date);
            holder.mBrandView.hintArrow();

            if (entity.isSign == 0){
                holder.mBrandView.setIsSign(false);
            }else {
                holder.mBrandView.setIsSign(true);
            }

            int sumNumber = 0;
            for (int i = 0; i < mDateMap.get(date).get(id).size(); i++) {
                sumNumber = sumNumber + mDateMap.get(date).get(id).get(i).purchaseNum;
            }
            holder.mBrandView.setBrandNum(sumNumber);

            if (position == 0) {
                holder.mBrandView.hintDivi(true);
            } else {
                holder.mBrandView.hintDivi(false);
            }

            ChildItemAdapter = new ChildItemAdapter(mDateMap.get(date).get(id));
            holder.mListView.setAdapter(ChildItemAdapter);

            holder.mListView.setVisibility(View.VISIBLE);

            return convertView;
        }


    }


    class ChildItemAdapter extends BaseAdapter {
        private List<PurchaserHandler.DataEntity.YTHListEntity> mData;


        public ChildItemAdapter(List<PurchaserHandler.DataEntity.YTHListEntity> data) {
            mData = data;
        }

        @Override
        public int getCount() {
            if (mData != null) {
                return mData.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {

            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ChildItemHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(UIUtils.getContext()).inflate(R.layout.buyer_handler_order_layout, null);
                holder = new ChildItemHolder();

                holder.goodsName = (TextView) convertView.findViewById(R.id.buyer_handler_order_name);
                holder.goodsSize = (TextView) convertView.findViewById(R.id.buyer_handler_order_size);
                holder.readyTime = (TextView) convertView.findViewById(R.id.buyer_handler_order_time);
                holder.handlerTime = (TextView) convertView.findViewById(R.id.buyer_handler_order_handler_time);
                holder.handlerPeople = (TextView) convertView.findViewById(R.id.buyer_handler_order_people_name);
                holder.mCheckBox = (CheckBox) convertView.findViewById(R.id.buyer_handler_order_checkbox);
                holder.mFirst = (ImageView) convertView.findViewById(R.id.buyer_handler_goods_first);
                holder.mLogo = (ImageView) convertView.findViewById(R.id.buyer_handler_goods_logo);
                holder.mDetail = (Button) convertView.findViewById(R.id.buyer_handler_order_handler_details);

                convertView.setTag(holder);
            } else {
                holder = (ChildItemHolder) convertView.getTag();
            }
            final PurchaserHandler.DataEntity.YTHListEntity entity = mData.get(position);

            holder.goodsName.setText(entity.productName_cn);
            holder.goodsSize.setText("货号：" + entity.productNumber + " " + entity.colorName + " " + entity.sizeAbbr);
            String tHDate = entity.THDate;
            if (TextUtils.isEmpty(tHDate)){
                tHDate = "1111-11-11";
            }else {
                tHDate = tHDate.substring(0, 10);
            }

            holder.readyTime.setText("货品将于" + tHDate + "备齐");
            String handlerTime = entity.actualTHDate.substring(0, 10);
            holder.handlerTime.setText("签收时间：" + handlerTime);

            ImageLoaderUtil.getInstance(UIUtils.getContext()).displayImage(entity.mobileImageURL_1, holder.mLogo);

            holder.handlerPeople.setText("签收人：" + entity.fullName);
            if (entity.con == 0) {
                holder.mFirst.setImageResource(R.mipmap.order_type_first);
            } else {
                holder.mFirst.setImageResource(R.mipmap.order_type_fan);
            }

            final String finalTHDate = tHDate;
            final  String finalSupplierId = entity.supplierId;
            final  int finalOrderId = entity.orderId;
            holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    String dateAndId = finalTHDate +"#"+finalSupplierId+"#"+finalOrderId;
                    mCheckMap.put(dateAndId, isChecked);
                    judgeIsSelect();
                }
            });

            holder.mDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("id", entity.id);

                    intent.setClass(UIUtils.getContext(), BuyerHandlerOrderDetails.class);
                    startActivity(intent);
                }
            });
            return convertView;
        }
    }

    private void judgeIsSelect() {
        boolean isSelect = false;
        if (mCheckMap != null) {
            Set<Map.Entry<String, Boolean>> entrySet = mCheckMap.entrySet();

            Iterator<Map.Entry<String, Boolean>> iterator = entrySet.iterator();

            while (iterator.hasNext()) {
                Map.Entry<String, Boolean> next = iterator.next();
                Boolean value = next.getValue();
                if (value) {
                    isSelect = true;
                }
            }
        }

        if (isSelect) {
            mBuyerHandlerNewOrder.setVisibility(View.VISIBLE);
        }else {
            mBuyerHandlerNewOrder.setVisibility(View.GONE);
        }
    }

    class ChildItemHolder {
        TextView goodsName;
        TextView goodsSize;
        TextView readyTime;
        TextView handlerTime;
        TextView handlerPeople;
        CheckBox mCheckBox;
        ImageView mLogo;
        ImageView mFirst;
        Button mDetail;
    }


    class ViewHolder {
        CustomBuyerBrandView mBrandView;
        SubListView mListView;
    }

}
