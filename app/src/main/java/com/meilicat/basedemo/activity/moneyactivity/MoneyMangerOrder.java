package com.meilicat.basedemo.activity.moneyactivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.google.gson.Gson;
import com.meilicat.basedemo.R;
import com.meilicat.basedemo.base.BaseActivity;
import com.meilicat.basedemo.bean.SupplierInAccountDetail;
import com.meilicat.basedemo.bean.SupplierMoneyDetail;
import com.meilicat.basedemo.bean.SupplierOutAccountDetail;
import com.meilicat.basedemo.conf.Constants;
import com.meilicat.basedemo.utils.HttpManager;
import com.meilicat.basedemo.utils.LogUtils;
import com.meilicat.basedemo.utils.T;
import com.meilicat.basedemo.utils.UIUtils;
import com.meilicat.basedemo.view.DoubleDatePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by cj on 2016/2/17.
 * 这是余额模块的账单界面
 */

public class MoneyMangerOrder extends BaseActivity {
    @Bind(R.id.money_manger_order_title_back)
    RelativeLayout mMoneyMangerOrderTitleBack;
    @Bind(R.id.money_manger_order_date)
    TextView mMoneyMangerOrderDate;
    @Bind(R.id.money_manger_order_date_change)
    LinearLayout mMoneyMangerOrderDateChange;
    @Bind(R.id.money_manger_order_inner)
    TextView mMoneyMangerOrderInner;
    @Bind(R.id.money_manger_order_outer)
    TextView mMoneyMangerOrderOuter;
    @Bind(R.id.money_manger_order_lv)
    ListView mMoneyMangerOrderLv;
    @Bind(R.id.error_btn_retry)
    Button mErrorBtnRetry;
    @Bind(R.id.money_manger_blance_error)
    LinearLayout mMoneyMangerBlanceError;
    @Bind(R.id.money_manger_blance_empty)
    LinearLayout mMoneyMangerBlanceEmpty;


    public static final int STATE_EMPTY = 0;
    public static final int STATE_ERROR = 1;
    public static final int STATE_SUCCESS = 2;
    public static final int STATE_NONE = -1;

    @Bind(R.id.order_commont_refresh)
    MaterialRefreshLayout mOrderCommontRefresh;

    private int currentState = STATE_NONE;
    List<SupplierMoneyDetail.DataEntity.SupplierListEntity> mData;
    private String mCurrentDate;

    @Override
    public void setContent() {
        setContentView(R.layout.activity_money_order);
        ButterKnife.bind(this);
        initEvent();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Date datee = new Date(System.currentTimeMillis());
        mCurrentDate = sdf.format(datee);
        String[] dates = mCurrentDate.split("-");
        String year = dates[0];
        String month = dates[1];
        String datess = year+"年"+month+"月";
        mMoneyMangerOrderDate.setText(datess);

        LogUtils.i("当前的 时间-----" + mCurrentDate);
        initData(mCurrentDate);



    }

    /**
     * 初始化数据
     */
    private String inMoney = "0.0";
    private String outMoney = "0.0";


    private int currentPager = 1;//分页查询的

    private void initData(String date)  {
        showLoadDialog();
        mData = new ArrayList<>();

        HttpManager manger = new HttpManager(UIUtils.getContext()) {
            @Override
            protected void onFail() {
                currentState = STATE_ERROR;
                T.showShort(UIUtils.getContext(), "请求数据失败");
                mOrderCommontRefresh.finishRefresh();
                dismissLoadDialog();
                refeshUI(currentState);
            }

            @Override
            protected void onSuccess(Object obj) {

                if (obj != null) {
                    LogUtils.i("obj-------"+obj);
                    Gson gson = new Gson();
                    SupplierMoneyDetail supplierMoneyDetail = null;
                    try {
                        supplierMoneyDetail = gson.fromJson((String) obj, SupplierMoneyDetail.class);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    if (supplierMoneyDetail == null){
                        currentState = STATE_ERROR;
                        refeshUI(currentState);
                        return;
                    }

                    if (supplierMoneyDetail.msg == 1) {
                        if (supplierMoneyDetail.data.supplierList != null && supplierMoneyDetail.data.supplierList.size() != 0) {
                            if (supplierMoneyDetail.data.supplierCountMoney != null) {
                                inMoney = supplierMoneyDetail.data.supplierCountMoney.countInMoney;
                                outMoney = supplierMoneyDetail.data.supplierCountMoney.countOutMoney;
                                LogUtils.i("inmoney--------"+inMoney+"----outmoney-----"+outMoney);
                            }
                            mData = supplierMoneyDetail.data.supplierList;
                            currentState = STATE_SUCCESS;
                        } else {
                            currentState = STATE_EMPTY;
                        }
                    } else {
                        currentState = STATE_ERROR;
                        T.showShort(UIUtils.getContext(), "请求数据失败，" + supplierMoneyDetail.msgbox);
                    }
                } else {
                    T.showShort(UIUtils.getContext(), "请求数据为空");
                    currentState = STATE_EMPTY;
                }
                refeshUI(currentState);
                mOrderCommontRefresh.finishRefresh();
                dismissLoadDialog();
            }

            @Override
            protected void onTimeOut() {
                dismissLoadDialog();
                currentState = STATE_ERROR;
                mOrderCommontRefresh.finishRefresh();
                refeshUI(currentState);
            }
        };
        String url = "";

        if (!TextUtils.isEmpty(date)) {

            url = Constants.URLS.SUPPLIER_GET_DETAIL_BY_TIME + "?tradeTime=" + date+"-00";
        } else {
            url = Constants.URLS.SUPPLIER_GET_DETAIL_BY_TIME;
        }
        LogUtils.i("url-----"+url);
        manger.get(url);

    }

    private void loadmore(String date) throws ParseException {
        HttpManager manager = new HttpManager(UIUtils.getContext()){
            @Override
            protected void onFail() {
                if (currentPager >= 1){
                    currentPager = currentPager -1;
                }

                T.showShort(UIUtils.getContext(),"加载更多失败");
                mOrderCommontRefresh.finishRefreshLoadMore();
            }

            @Override
            protected void onSuccess(Object obj) {
                if (obj != null) {
                    Gson gson = new Gson();
                    SupplierMoneyDetail supplierMoneyDetail = null;
                    try {
                        supplierMoneyDetail = gson.fromJson((String) obj, SupplierMoneyDetail.class);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    if (supplierMoneyDetail == null){
                        if (currentPager >= 1){
                            currentPager = currentPager -1;
                        }
                        T.showShort(UIUtils.getContext(),"加载更多数据失败");
                        return;
                    }


                    if (supplierMoneyDetail.msg == 1) {
                        if (supplierMoneyDetail.data.supplierList != null && supplierMoneyDetail.data.supplierList.size() != 0) {
                            if (supplierMoneyDetail.data.supplierCountMoney != null) {
                                inMoney = supplierMoneyDetail.data.supplierCountMoney.countInMoney;
                                outMoney = supplierMoneyDetail.data.supplierCountMoney.countOutMoney;
                                LogUtils.i("inmoney--------"+inMoney+"----outmoney-----"+outMoney);
                            }
                            mData.addAll(supplierMoneyDetail.data.supplierList);

                        } else {
                            if (currentPager >= 1){
                                currentPager = currentPager -1;
                            }
                            T.showShort(UIUtils.getContext(), "没有更多数据");
                        }
                    } else {
                        if (currentPager >= 1){
                            currentPager = currentPager -1;
                        }
                        T.showShort(UIUtils.getContext(),"加载更多数据失败");
                    }
                } else {
                    if (currentPager >= 1){
                        currentPager = currentPager -1;
                    }
                    T.showShort(UIUtils.getContext(), "没有更多数据");
                }
                mOrderCommontRefresh.finishRefreshLoadMore();
                initView();
                dismissLoadDialog();
            }

            @Override
            protected void onTimeOut() {
                if (currentPager >= 1){
                    currentPager = currentPager -1;
                }
                T.showShort(UIUtils.getContext(),"加载更多超时");
                mOrderCommontRefresh.finishRefreshLoadMore();
            }
        };
        String url = "";

        url = Constants.URLS.SUPPLIER_GET_DETAIL_BY_TIME + "?tradeTime=" + date + "&page=" + currentPager;//TODO 数据有问题 这里需要加上日期

        manager.get(url);
    }


    private void refeshUI(int type) {
        mMoneyMangerBlanceError.setVisibility((type == STATE_ERROR) || (type == STATE_NONE) ? View.VISIBLE : View.GONE);
        mMoneyMangerBlanceEmpty.setVisibility((type == STATE_EMPTY) || (type == STATE_NONE) ? View.VISIBLE : View.GONE);
        mMoneyMangerOrderLv.setVisibility((type == STATE_SUCCESS) || (type == STATE_NONE) ? View.VISIBLE : View.GONE);
        initView();

    }


    /**
     * 初始化事件
     */
    private void initEvent() {
        mMoneyMangerOrderDateChange.setOnClickListener(new View.OnClickListener() {
            Calendar c = Calendar.getInstance();

            @Override
            public void onClick(View v) {

                //选择日期的dialog
                new DoubleDatePickerDialog(MoneyMangerOrder.this, AlertDialog.THEME_HOLO_LIGHT, new DoubleDatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker startDatePicker, int startYear, int startMonthOfYear,
                                          int startDayOfMonth, DatePicker endDatePicker, int endYear, int endMonthOfYear,
                                          int endDayOfMonth) {

                        String text = startYear + "年" + (startMonthOfYear + 1) + "月";
                        String date ="";
                        if ((startMonthOfYear+1) < 10){
                            date = startYear + "-0" + (startMonthOfYear + 1);
                        }else {
                            date = startYear + "-" + (startMonthOfYear + 1);
                        }

                        mCurrentDate = date;
                        mData.clear();

                        initData(date);

                        mMoneyMangerOrderDate.setText(text);
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), false).show();
            }
        });


        mOrderCommontRefresh.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {

                    initData(mCurrentDate);


            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                try {
                    currentPager = currentPager + 1;
                    loadmore(mCurrentDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                    T.showShort(UIUtils.getContext(), "加载更多失败");
                    mOrderCommontRefresh.finishRefreshLoadMore();
                }
            }
        });
        mErrorBtnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData(mCurrentDate);
            }
        });
    }

    @Override
    public void initTitle() {
        mMoneyMangerOrderTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void initView() {
        mMoneyMangerOrderInner.setText(inMoney);

        LogUtils.i("outMoney---" + outMoney);
        mMoneyMangerOrderOuter.setText(outMoney);
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        } else {
            if (mData != null){
                mAdapter = new OrderAdapter(mData);
                mMoneyMangerOrderLv.setAdapter(mAdapter);
            }
        }

        mMoneyMangerOrderLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                seeDetail(mData.get(position).type,
                        mData.get(position).moneyLogId, mData.get(position).colorName,
                        mData.get(position).sizeAbbr, mData.get(position).productNumber);
            }
        });
    }


    /**
     * 查看详情的方法
     */
    private void seeDetail(final int type, String moneyId, String colorName, String sizeArr, String productNum) {
        String url = "";
        switch (type) {
            case 0:
                //提现的订单
                url = Constants.URLS.SUPPLIER_MONEY_OUTACCOUNT + "moneyLogId=" + moneyId;

                break;
            case 1:
                url = Constants.URLS.SUPPLIER_MONEY_INACCOUNT + "moneyLogId=" + 39 + "&colorName=" + colorName + "&sizeAbbr=" + sizeArr + "productNumber=" + productNum;
                LogUtils.i("url-------" + url);
                break;
            default:
                break;
        }
        HttpManager manger = new HttpManager(UIUtils.getContext()) {
            @Override
            protected void onFail() {
                dismissLoadDialog();
                T.showShort(UIUtils.getContext(), "查看订单详情失败");
            }

            @Override
            protected void onSuccess(Object obj) {
                //请求数据成功
                if (obj != null) {
                    Gson gson = new Gson();
                    if (type == 0) {
                        SupplierOutAccountDetail outAccountDetail = gson.fromJson(obj + "", SupplierOutAccountDetail.class);
                        if (outAccountDetail.msg == 0) {
                            T.showShort(UIUtils.getContext(), "查看订单详情失败");
                        } else {
                            SupplierOutAccountDetail.DataEntity.SupplierEntity supplier = outAccountDetail.data.supplier;
                            T.showShort(UIUtils.getContext(), "请求数据成功");
                            if (supplier == null) {
                                T.showShort(UIUtils.getContext(), "获取订单详情失败");
                                return;
                            }
                            Intent intent = new Intent();
                            intent.putExtra("detailss", supplier);
                            intent.putExtra("type", type);

                            intent.setClass(UIUtils.getContext(), MoneyMangerOrderInner.class);
                            startActivity(intent);
                        }
                    } else {
                        SupplierInAccountDetail inAccoutDetail = gson.fromJson(obj + "", SupplierInAccountDetail.class);
                        if (inAccoutDetail.msg == 0) {
                            T.showShort(UIUtils.getContext(), "查看订单详情失败");
                        } else {
                            SupplierInAccountDetail.DataEntity.SupplierEntity supplier = inAccoutDetail.data.supplier;
                            //                            T.showShort(UIUtils.getContext(), "请求数据成功");
                            if (supplier == null) {
                                T.showShort(UIUtils.getContext(), "获取订单详情失败");
                                dismissLoadDialog();
                                return;
                            }
                            Intent intent = new Intent();
                            LogUtils.i(supplier.toString());
                            intent.putExtra("detailss", supplier);
                            intent.putExtra("type", type);
                            intent.setClass(UIUtils.getContext(), MoneyMangerOrderInner.class);
                            startActivity(intent);
                        }
                    }
                } else {
                    T.showShort(UIUtils.getContext(), "请求数据失败");
                }

            }

            @Override
            protected void onTimeOut() {
                dismissLoadDialog();
                T.showShort(UIUtils.getContext(), "查看订单详情失败");
            }
        };

        showLoadDialog();
        manger.get(url);
    }

    OrderAdapter mAdapter;



    class OrderAdapter extends BaseAdapter {
        List<SupplierMoneyDetail.DataEntity.SupplierListEntity> mData;

        public OrderAdapter(List<SupplierMoneyDetail.DataEntity.SupplierListEntity> data) {
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
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(UIUtils.getContext()).inflate(R.layout.item_money_blance_order, null);
                holder = new ViewHolder();

                holder.name = (TextView) convertView.findViewById(R.id.order_name);
                holder.date = (TextView) convertView.findViewById(R.id.order_date);
                holder.changeMoney = (TextView) convertView.findViewById(R.id.order_money);
                holder.type = (TextView) convertView.findViewById(R.id.order_blance);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            SupplierMoneyDetail.DataEntity.SupplierListEntity moneyBean = mData.get(position);

            if (moneyBean.type == 0) {
                holder.date.setText(moneyBean.TradeTime);
                holder.name.setText("提现");
                holder.changeMoney.setTextColor(Color.BLACK);
                holder.changeMoney.setText("-" + moneyBean.moneyDetail);
                holder.type.setText("提现出账");
            } else {
                holder.name.setText(moneyBean.productName_cn + " " + moneyBean.productNumber + "" + moneyBean.colorName);
                holder.date.setText(moneyBean.createTime1);
                holder.changeMoney.setTextColor(Color.parseColor("#FF0099"));
                holder.changeMoney.setText("+" + moneyBean.moneyDetail);
                holder.type.setText("结算入账");
            }


            return convertView;
        }
    }

    class ViewHolder {
        TextView name;
        TextView date;
        TextView changeMoney;
        TextView type;

    }


}
