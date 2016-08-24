package com.meilicat.basedemo.activity.moneyactivity;

import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
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
import com.meilicat.basedemo.base.BaseApplication;
import com.meilicat.basedemo.bean.SupplierDetailBean;
import com.meilicat.basedemo.bean.SupplierInAccountDetail;
import com.meilicat.basedemo.bean.SupplierMoneyDetail;
import com.meilicat.basedemo.bean.SupplierOutAccountDetail;
import com.meilicat.basedemo.bean.money.OutMoneyBean;
import com.meilicat.basedemo.conf.Constants;
import com.meilicat.basedemo.utils.HttpManager;
import com.meilicat.basedemo.utils.IntentUtil;
import com.meilicat.basedemo.utils.LogUtils;
import com.meilicat.basedemo.utils.T;
import com.meilicat.basedemo.utils.UIUtils;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.meilicat.basedemo.bean.SupplierMoneyDetail.DataEntity;

/**
 * Created by cj on 2016/2/17.
 * 这是余额模块的账户余额的activity
 */
public class MoneyMangerBlance extends BaseActivity {

    @Bind(R.id.money_manger_blance_title_back)
    RelativeLayout mMoneyMangerBlanceTitleBack;
    @Bind(R.id.money_manger_blance_title_atm)
    TextView mMoneyMangerBlanceTitleAtm;
    @Bind(R.id.money_manger_blance_rg)
    RadioGroup mMoneyMangerBlanceRg;
    @Bind(R.id.money_manger_blance_lv)
    ListView mMoneyMangerBlanceLv;
    @Bind(R.id.error_btn_retry)
    Button mErrorBtnRetry;
    @Bind(R.id.money_manger_blance_error)
    LinearLayout mMoneyMangerBlanceError;
    @Bind(R.id.money_manger_blance_empty)
    LinearLayout mMoneyMangerBlanceEmpty;

    public static final int STATE_EMPTY = 0;
    public static final int STATE_ERROR = 1;
    public static final int STATE_SUCCESS = 2;
    public static final int STATE_LOADING = 3;
    public static final int STATE_NONE = -1;
    @Bind(R.id.order_commont_refresh)
    MaterialRefreshLayout mOrderCommontRefresh;
    @Bind(R.id.money_manger_blance_loading)
    ProgressWheel mMoneyMangerBlanceLoading;

    private int currentState = STATE_NONE;
    List<DataEntity.SupplierListEntity> mData;
    List<OutMoneyBean.DataEntity.SupplierMoneyLogListEntity> mOutMoney;

    @Override
    public void setContent() {
        setContentView(R.layout.activity_money_blance);
        ButterKnife.bind(this);
        initData();
    }

    /**
     * 加载数据
     */

    private void initData() {
        mData = new ArrayList<>();
        currentAdapter = BASE_ADAPTER;
        HttpManager manger = new HttpManager(UIUtils.getContext()) {
            @Override
            protected void onFail() {
                currentState = STATE_ERROR;
                refeshUI(currentState, 0);
            }

            @Override
            protected void onSuccess(Object obj) {
                LogUtils.i("就加载成功-----------");
                if (obj != null) {
                    LogUtils.i("obj-----------"+obj);

                    Gson gson = new Gson();
                    try {
                        SupplierMoneyDetail supplierMoneyDetail = gson.fromJson((String) obj, SupplierMoneyDetail.class);

                        if (supplierMoneyDetail.msg == 1) {
                            if (supplierMoneyDetail.data.supplierList != null && supplierMoneyDetail.data.supplierList.size() != 0) {
                                mData = supplierMoneyDetail.data.supplierList;

                                currentState = STATE_SUCCESS;

                            } else {
                                currentState = STATE_EMPTY;
                                LogUtils.i("数据数据为空--------");
                            }
                        } else {
                            currentState = STATE_ERROR;
                            T.showShort(UIUtils.getContext(), "请求数据失败，" + supplierMoneyDetail.msgbox);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        LogUtils.i("加载数据失败--------");
                        currentState = STATE_ERROR;
                    }
                }else {
                    LogUtils.i("数据数据为空--------");
                    currentState = STATE_EMPTY;
                }
                refeshUI(currentState, 0);
            }

            @Override
            protected void onTimeOut() {
                currentState = STATE_ERROR;
                refeshUI(currentState, 0);
            }
        };

        manger.get(Constants.URLS.SUPPLIER_MONEY_DETAIL);//请求全部的数据


    }

    @Override
    public void initTitle() {
        mMoneyMangerBlanceTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mMoneyMangerBlanceTitleAtm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击提现按钮
                SupplierDetailBean mSupplier = BaseApplication.mSupplier;
                if (mSupplier != null) {
                    String bankName = mSupplier.data.supplier.bankName;
                    String bankNumber = mSupplier.data.supplier.bankNumber;
                    if (TextUtils.isEmpty(bankName) || TextUtils.isEmpty(bankNumber)) {
                        T.showShort(UIUtils.getContext(), "未绑定银行卡，无法进行提现操作");
                        return;
                    }
                    IntentUtil.startActivity(MoneyMangerBlance.this, MoneyMangerBlanceChange.class);
                }

            }
        });
    }

    @Override
    public void initView() {
        if (mData != null) {
            mAdapter = new BlanceAdapter(mData);
            mMoneyMangerBlanceLv.setAdapter(mAdapter);
        }
        initEvent();
        //初始化条目的点击事件

    }

    private int currentAdapter = -1;
    public static final int BASE_ADAPTER = 1;
    public static final int OUT_ADAPTER = 2;

    private void initEvent() {
        mMoneyMangerBlanceLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentAdapter == BASE_ADAPTER) {
                    DataEntity.SupplierListEntity entity = mData.get(position);
                    LogUtils.i("你点击的是收入--------");
                    seeDetail(1, entity.moneyLogId, entity.colorName, entity.sizeAbbr, entity.productNumber);
                } else if (currentAdapter == OUT_ADAPTER) {
                    //                    LogUtils.i("seeDetail------"+mOutMoney.get(position).moneyLogId);
                    LogUtils.i("你点击的是提现--------" + mOutMoney.get(position).id);
                    seeDetail(0, mOutMoney.get(position).id, null, null, null);
                }

            }
        });

        mMoneyMangerBlanceRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                changeFragment(checkedId);

            }
        });
        mErrorBtnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(mMoneyMangerBlanceRg.getCheckedRadioButtonId());//根据当前选中的button，去重新请求数据
            }
        });

        mOrderCommontRefresh.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                changeFragment(mMoneyMangerBlanceRg.getCheckedRadioButtonId());
                mOrderCommontRefresh.finishRefresh();
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                String url = "";
                int type = -1;
                currentPage = currentPage + 1;
                switch (mMoneyMangerBlanceRg.getCheckedRadioButtonId()) {
                    case R.id.money_manger_blance_rg_1:
                        url = Constants.URLS.SUPPLIER_MONEY_DETAIL + "?page=" + currentPage;
                        type = 1;
                        break;
                    case R.id.money_manger_blance_rg_2:
                        url = Constants.URLS.SUPPLIER_MONEY_DETAIL + "?type=" + 1 + "&page=" + currentPage;
                        type = 1;
                        break;
                    case R.id.money_manger_blance_rg_3:
                        url = Constants.URLS.SUPPLIER_MONEY_OUTMONEY + "?page=" + currentPage;
                        type = 0;
                        break;
                    default:
                        break;
                }
                loadMore(url, type);
            }
        });

    }

    private void seeDetail(final int type, String moneyId, String colorName, String sizeAbbr, String productNumber) {
        String url = "";
        switch (type) {
            case 0:
                //提现的订单
                url = Constants.URLS.SUPPLIER_MONEY_OUTACCOUNT + "?moneyLogId=" + moneyId;

                break;
            case 1:
                url = Constants.URLS.SUPPLIER_MONEY_INACCOUNT + "?moneyLogId=" + moneyId + "&colorName='" + colorName + "'&sizeAbbr='" + sizeAbbr + "'&productNumber='" + productNumber + "'";
                LogUtils.i("url------------" + url);
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
                dismissLoadDialog();
                if (obj != null) {
                    Gson gson = new Gson();
                    if (type == 0) {
                        SupplierOutAccountDetail outAccountDetail = gson.fromJson(obj + "", SupplierOutAccountDetail.class);
                        if (outAccountDetail.msg == 0) {
                            T.showShort(UIUtils.getContext(), "查看订单详情失败");
                        } else {
                            SupplierOutAccountDetail.DataEntity.SupplierEntity supplier = outAccountDetail.data.supplier;

                            Intent intent = new Intent();
                            if (supplier == null) {
                                T.showShort(UIUtils.getContext(), "获取订单详情失败");
                                return;
                            }
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
                                return;
                            }
                            Intent intent = new Intent();
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
                T.showShort(UIUtils.getContext(), "查看订单超时");
                dismissLoadDialog();
            }
        };
        showLoadDialog();
        LogUtils.i("url-------" + url);
        manger.get(url);
    }

    /**
     * 该方法用于控制视图的显示
     */
    private void refeshUI(int type, int style) {
        LogUtils.i("type----"+type);
        mMoneyMangerBlanceError.setVisibility((type == STATE_ERROR) || (type == STATE_NONE) ? View.VISIBLE : View.GONE);
        mMoneyMangerBlanceEmpty.setVisibility((type == STATE_EMPTY) || (type == STATE_NONE) ? View.VISIBLE : View.GONE);
        mMoneyMangerBlanceLv.setVisibility((type == STATE_SUCCESS) || (type == STATE_NONE) ? View.VISIBLE : View.GONE);
        mMoneyMangerBlanceLoading.setVisibility((type == STATE_LOADING) || (type == STATE_NONE) ? View.VISIBLE : View.GONE);

        initView();
        if (style == 0) {

            mAdapter = new BlanceAdapter(mData);
            mMoneyMangerBlanceLv.setAdapter(mAdapter);
        } else {

            outMoneyAdapter = new OutMoneyAdapter(mOutMoney);
            mMoneyMangerBlanceLv.setAdapter(outMoneyAdapter);
        }
    }

    private void loadMore(String url, final int type) {
        HttpManager manger = new HttpManager(UIUtils.getContext()) {
            @Override
            protected void onFail() {
                T.showShort(UIUtils.getContext(), "加载更多失败");
                mOrderCommontRefresh.finishRefreshLoadMore();
                if (currentPage >= 1) {
                    currentPage = currentPage - 1;
                }
            }

            @Override
            protected void onSuccess(Object obj) {
                mOrderCommontRefresh.finishRefreshLoadMore();
                LogUtils.i("obj--------"+obj);
                if (type == 0) {
                    //说明是提现的列表
                    Gson gson = new Gson();
                    OutMoneyBean outAccout = gson.fromJson(obj + "", OutMoneyBean.class);
                    if (outAccout.msg == 1) {
                        if (outAccout.data != null && outAccout.data.supplierMoneyLogList != null && outAccout.data.supplierMoneyLogList.size() > 0) {
                            mOutMoney.addAll(outAccout.data.supplierMoneyLogList);
                        } else {
                            if (currentPage >= 1) {
                                currentPage = currentPage - 1;
                            }
                            T.showShort(UIUtils.getContext(), "没有更多数据了");
                        }
                    } else {
                        if (currentPage >= 1) {
                            currentPage = currentPage - 1;
                        }
                        T.showShort(UIUtils.getContext(), "加载更多失败");
                    }
                    outMoneyAdapter.notifyDataSetChanged();
                } else {
                    //说明是其他列表
                    Gson gson = new Gson();
                    SupplierMoneyDetail moneyDetail = gson.fromJson(obj + "", SupplierMoneyDetail.class);
                    if (moneyDetail.msg == 1) {

                        if (moneyDetail.data != null && moneyDetail.data.supplierList != null && moneyDetail.data.supplierList.size() > 0) {
                            mData.addAll(moneyDetail.data.supplierList);
                        } else {
                            if (currentPage >= 1) {
                                currentPage = currentPage - 1;
                            }
                            T.showShort(UIUtils.getContext(), "没有更多数据了");
                        }
                    } else {
                        if (currentPage >= 1) {
                            currentPage = currentPage - 1;
                        }
                        T.showShort(UIUtils.getContext(), "加载更多失败");
                    }
                    mAdapter.notifyDataSetChanged();

                }
            }

            @Override
            protected void onTimeOut() {
                T.showShort(UIUtils.getContext(), "加载更多超时");
                mOrderCommontRefresh.finishRefreshLoadMore();
                if (currentPage >= 1) {
                    currentPage = currentPage - 1;
                }
            }
        };

        manger.get(url);
    }


    /**
     * 根据点击的按钮不同，给ListView加载不同的数据集
     */
    int currentPage = 0;

    private void changeFragment(int checkedId) {

        switch (checkedId) {
            case R.id.money_manger_blance_rg_1:
                initData(Constants.URLS.SUPPLIER_MONEY_DETAIL, 0);
                currentAdapter = BASE_ADAPTER;
                break;
            case R.id.money_manger_blance_rg_2:
                initData(Constants.URLS.SUPPLIER_MONEY_DETAIL + "?type=1", 0);
                currentAdapter = BASE_ADAPTER;
                break;
            case R.id.money_manger_blance_rg_3:
                initData(Constants.URLS.SUPPLIER_MONEY_OUTMONEY, 1);
                currentAdapter = OUT_ADAPTER;
                break;
            default:
                break;
        }

    }

    /**
     * 写一个方法，根据传入的url不同，返回不同的数据，并且刷新视图
     */
    private void initData(String url, final int type) {
        currentPage = 0;

        HttpManager manger = new HttpManager(UIUtils.getContext()) {
            @Override
            protected void onFail() {
                currentState = STATE_ERROR;
                T.showShort(UIUtils.getContext(), "请求数据失败");
                refeshUI(currentState, type);
            }

            @Override
            protected void onSuccess(Object obj) {
                //                T.showShort(UIUtils.getContext(), "请求数据成功");
                                LogUtils.i("obj-----------"+obj);
                if (obj != null) {
                    Gson gson = new Gson();
                    if (type == 0) {
                        SupplierMoneyDetail supplierMoneyDetail = gson.fromJson((String) obj, SupplierMoneyDetail.class);

                        if (supplierMoneyDetail.msg == 1) {
                            if (supplierMoneyDetail.data.supplierList != null && supplierMoneyDetail.data.supplierList.size() != 0) {
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
                        //TODO 这里数据解析有问题
                        LogUtils.i("response--------" + obj);
                        OutMoneyBean outMoney = gson.fromJson(obj + "", OutMoneyBean.class);
                        if (outMoney != null) {
                            if (outMoney.msg == 1) {
                                //返回数据成功

                                if (outMoney.data.supplierMoneyLogList == null) {
                                    LogUtils.i("data是空的");
                                }
                                if (outMoney.data.supplierMoneyLogList != null && outMoney.data.supplierMoneyLogList.size() != 0) {
                                    //说明请求成功
                                    mOutMoney = outMoney.data.supplierMoneyLogList;
                                    currentState = STATE_SUCCESS;
                                    LogUtils.i("currentState--------"+currentState);
                                } else {
                                    //请求为空
                                    currentState = STATE_EMPTY;
                                }
                            } else {
                                //返回数据失败
                                currentState = STATE_ERROR;
                                T.showShort(UIUtils.getContext(), "请求数据失败，" + outMoney.msgbox);
                            }
                        } else {
                            currentState = STATE_EMPTY;
                        }

                    }

                } else {
                    T.showShort(UIUtils.getContext(), "请求数据为空");
                    currentState = STATE_EMPTY;
                }
                refeshUI(currentState, type);
            }
        };
        LogUtils.i("url---blance-----"+url);
        manger.get(url);//请求全部的数据

    }

    /**
     * 这是提现列表特有的adapter
     */

    OutMoneyAdapter outMoneyAdapter;


    class OutMoneyAdapter extends BaseAdapter {
        List<OutMoneyBean.DataEntity.SupplierMoneyLogListEntity> mData;


        public OutMoneyAdapter(List<OutMoneyBean.DataEntity.SupplierMoneyLogListEntity> data) {
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
                holder.blanceMoney = (TextView) convertView.findViewById(R.id.order_blance);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            OutMoneyBean.DataEntity.SupplierMoneyLogListEntity entity = mData.get(position);

            holder.name.setText(entity.comment);
            holder.date.setText(entity.tradeTime);
            holder.changeMoney.setText("-" + entity.money);
            holder.changeMoney.setTextColor(Color.BLACK);
            holder.blanceMoney.setText("余额：" + entity.balance);


            return convertView;
        }
    }


    BlanceAdapter mAdapter;

    class BlanceAdapter extends BaseAdapter {
        List<DataEntity.SupplierListEntity> mData;

        public BlanceAdapter(List<DataEntity.SupplierListEntity> data) {
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
                holder.blanceMoney = (TextView) convertView.findViewById(R.id.order_blance);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            DataEntity.SupplierListEntity moneyBean = mData.get(position);
            holder.name.setText(moneyBean.productName_cn + " " + moneyBean.sizeAbbr + " " + moneyBean.colorName);
            if (moneyBean.type == 0) {
                //说明是提现
                holder.date.setText(moneyBean.TradeTime);
                holder.changeMoney.setTextColor(Color.BLACK);
                holder.changeMoney.setText("-" + moneyBean.moneyDetail);
                if (!TextUtils.isEmpty(moneyBean.money)) {
                    holder.blanceMoney.setText(moneyBean.money);
                } else {
                    holder.blanceMoney.setText("没有余额数据返回");
                }

            } else {
                holder.date.setText(moneyBean.createTime1);
                holder.changeMoney.setTextColor(Color.parseColor("#FF0099"));
                holder.changeMoney.setText("+" + moneyBean.moneyDetail);
                if (!TextUtils.isEmpty(moneyBean.money)) {
                    holder.blanceMoney.setText(moneyBean.money);
                } else {
                    holder.blanceMoney.setText("没有余额数据返回");
                }
            }


            return convertView;
        }
    }

    class ViewHolder {
        TextView name;
        TextView date;
        TextView changeMoney;
        TextView blanceMoney;

    }


}
