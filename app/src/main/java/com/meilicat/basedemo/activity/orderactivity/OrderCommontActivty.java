package com.meilicat.basedemo.activity.orderactivity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.google.gson.Gson;
import com.meilicat.basedemo.R;
import com.meilicat.basedemo.base.BaseActivity;
import com.meilicat.basedemo.bean.order.OrderMain;
import com.meilicat.basedemo.bean.order.OrderUnhandler;
import com.meilicat.basedemo.conf.Constants;
import com.meilicat.basedemo.utils.HttpManager;
import com.meilicat.basedemo.utils.LogUtils;
import com.meilicat.basedemo.utils.T;
import com.meilicat.basedemo.utils.UIUtils;
import com.meilicat.basedemo.view.AnimatedExpandableListView;
import com.meilicat.basedemo.view.CustomChildStateReturn;
import com.meilicat.basedemo.view.CustomPayView;
import com.meilicat.basedemo.view.CustomSignView;
import com.meilicat.basedemo.view.CutomCommontOrderItem;
import com.meilicat.basedemo.view.calendar.CalendarActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by cj on 2016/1/29.
 * 这是订单界面共用的activity
 */
public class OrderCommontActivty extends BaseActivity {
    @Bind(R.id.order_commont_lv)
    AnimatedExpandableListView mOrderCommontLv;
    @Bind(R.id.order_commont_refresh)
    MaterialRefreshLayout mOrderCommontRefresh;
    @Bind(R.id.order_commont_title_back)
    RelativeLayout mOrderCommontTitleBack;
    @Bind(R.id.order_commont_title_name)
    TextView mOrderCommontTitleName;
    @Bind(R.id.order_commont_title_pop)
    ImageView mOrderCommontTitlePop;
    @Bind(R.id.load_error)
    LinearLayout mLoadError;
    @Bind(R.id.load_loading)
    LinearLayout mLoading;
    @Bind(R.id.load_empty)
    LinearLayout mLoadEmpty;
    @Bind(R.id.load_success)
    LinearLayout mLoadSuccess;
    @Bind(R.id.order_commont_date_rg_1)
    RadioButton mOrderCommontDateRg1;
    @Bind(R.id.order_commont_date_rg_2)
    RadioButton mOrderCommontDateRg2;
    @Bind(R.id.order_commont_date_rg_3)
    RadioButton mOrderCommontDateRg3;
    @Bind(R.id.order_commont_date_rg_4)
    RadioButton mOrderCommontDateRg4;

    @Bind(R.id.order_commont_date_rg)
    RadioGroup mOrderCommontDateRg;

    @Bind(R.id.order_commont_select)
    FrameLayout mOrderCommontSelect;
    @Bind(R.id.error_btn_retry)
    Button mErrorBtnRetry;


    private LayoutInflater mInflater;
    private int mState;
    private int mWhich;
    public static final int DATA_STATE_NONE = -1;
    private static final int DATA_STATE_EMPTY = 1;
    private static final int DATA_STATE_SUCCESS = 2;
    private static final int DATA_STATE_FAIL = 3;
    private static final int DATA_STATE_LOADING = 4;
    private int mType;
    private int startPage = 1;

    public static final int LOAD_MORE = 1;
    public static final int COMMONT_DATE = 2;

    private static final int STATE_NOTAB = 1;//未标记
    private static final int STATE_NOTAB_RETURN = 2;//未标记，已退款
    private static final int STATE_TAB_NOSIGN = 3;//已标记，未签收
    private static final int STATE_TAB_NOSIGN_RETURN = 4;//已标记，未签收，已退款
    private static final int STATE_TAB_SIGN_NOSEND = 5;//已标记，已签收，未发货
    private static final int STATE_TAB_SIGN_NOSEND_RETURN = 6;//已标记，已签收，未发货，已退款
    private static final int STATE_TAB_SIGN_NOSEND_RETURN_ACCOUNT = 11;//已标记，已签收，未发货，已退款

    private static final int STATE_TAB_SIGN_SEND_NOACCOUNT = 7;//已标记，已签收，已发货，未结算
    private static final int STATE_TAB_SIGN_SEND_NOACCOUT_RETURN = 8;//已标记，已签收，已发货，未结算，已退款
    private static final int STATE_TAB_SIGN_SEND_ACCOUT = 9;//已标记，已签收，已发货，已结算
    private static final int STATE_TAB_SIGN_SEND_ACCOUT_RETURN = 10;//已标记，已签收，已发货，已结算,已退款


    @Override
    public void setContent() {
        Intent intent = getIntent();
        mState = intent.getIntExtra("state", 1);
        mWhich = intent.getIntExtra("which", -1);
        mType = intent.getIntExtra("type", -1);

        setContentView(R.layout.activity_order_commont);
        ButterKnife.bind(this);

        mInflater = LayoutInflater.from(OrderCommontActivty.this);


        mCurState = DATA_STATE_LOADING;
        refreshUIByState();

        mData = new ArrayList<>();
        if (mType == 4){
            initData(mType,0+"",startPage,COMMONT_DATE);
        }else if(mType == 5){
            initData(mType,"1",startPage,COMMONT_DATE);
        }else if (mType == 6) {
            initData(mType, "1", startPage, COMMONT_DATE);
        }else {
            initData(mType,"",startPage,COMMONT_DATE);
        }

        //再刷新视图
        initEvent();
    }
    private OrderMain.DataEntity mOrderMain;
    @Override
    public void initTitle() {

        initTitle2();

        switch (mState) {
            case 1:
                mOrderCommontTitlePop.setVisibility(View.GONE);
                mOrderCommontSelect.setVisibility(View.GONE);

                break;
            case 2:
                mOrderCommontDateRg1.setText("未处理");
                mOrderCommontDateRg2.setText("已处理");
                mOrderCommontDateRg3.setText("全部");

                mOrderCommontDateRg4.setVisibility(View.GONE);

                break;
            case 3:
                mOrderCommontDateRg1.setText("今日");
                mOrderCommontDateRg2.setText("本周");
                mOrderCommontDateRg3.setText("本月");
                mOrderCommontDateRg4.setText("全部");

                mOrderCommontDateRg4.setVisibility(View.VISIBLE);


                break;

            default:
                break;
        }


    }

    private void initTitle2() {
        HttpManager manger = new HttpManager(UIUtils.getContext()){
            @Override
            protected void onFail() {
                T.showShort(UIUtils.getContext(),"初始化标题失败");
            }
            @Override
            protected void onTimeOut() {
                T.showShort(UIUtils.getContext(),"初始化标题超时");
            }
            @Override
            protected void onSuccess(Object obj) {
                Gson mGson = new Gson();
                if (obj != null){
                    OrderMain main = null;
                    try{
                        main = mGson.fromJson(obj + "", OrderMain.class);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    if (main == null){
                        T.showShort(UIUtils.getContext(),"初始化标题失败");
                        return;
                    }
                    if (main.msg == 0){
                        T.showShort(UIUtils.getContext(),"初始化标题失败");
                    }else {
                        if (main.data == null){
                            T.showShort(UIUtils.getContext(),"初始化标题失败");
                            return ;
                        }else {
                            mOrderMain = main.data;
                            refreshTitle();
                        }
                    }
                }else {
                    T.showShort(UIUtils.getContext(),"初始化标题失败");
                }
            }
        };

        manger.get(Constants.URLS.ORDER_MAIN);


    }

    private void refreshTitle(){
        String tilte = "";
        if (mOrderMain != null){
             switch (mType){
                 case 1:
                     tilte = "未处理订单("+mOrderMain.waitOrderCount+")";
                     break;
                 case 2:
                     tilte = "待备货订单("+mOrderMain.waitBeReadyCount+")";
                     break;
                 case 3:
                     tilte = "仓库待发货订单("+mOrderMain.waitGoodsCount+")";
                     break;
                 case 4:
                     tilte = "今日新增订单("+mOrderMain.nowAddOrderCount+")";
                     break;
                 case 5:
                     tilte = "今日结算订单("+mOrderMain.nowStatOrderCount+")";
                     break;
                 case 6:
                     tilte = "今日退款订单("+mOrderMain.nowReturnOrderCount+")";
                     break;

                 default:
                     break;
             }
             mOrderCommontTitleName.setText(tilte);
        }
    }

    private int mCurState = DATA_STATE_NONE;

    private void refreshUIByState() {
        mLoading.setVisibility((mCurState == DATA_STATE_LOADING) || (mCurState == DATA_STATE_NONE) ? View.VISIBLE : View.GONE);

        mLoadEmpty.setVisibility((mCurState == DATA_STATE_EMPTY ) || (mCurState == DATA_STATE_NONE) ? View.VISIBLE : View.GONE);

        mLoadError.setVisibility((mCurState == DATA_STATE_FAIL ) || (mCurState == DATA_STATE_NONE) ? View.VISIBLE : View.GONE);

        mLoadSuccess.setVisibility((mCurState == DATA_STATE_SUCCESS) || (mCurState == DATA_STATE_NONE) ? View.VISIBLE : View.GONE);
        LogUtils.i("currentType-------" + mCurState);
        mOrderCommontRefresh.finishRefresh();
        mOrderCommontRefresh.finishRefreshLoadMore();

        initView2();
    }

    @Override
    public void initView() {
    }
    public void  initView2(){
        if (mData != null && mData.size() != 0){
            LogUtils.i("mData-----不是null的--");
            if (mAdapter != null){
                mAdapter.notifyDataSetChanged();
            }else {
                mAdapter = new ExpandAdapter();
                mOrderCommontLv.setAdapter(mAdapter);
                mOrderCommontLv.setGroupIndicator(null);

                mOrderCommontLv.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                    @Override
                    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                        if (mOrderCommontLv.isGroupExpanded(groupPosition)) {
                            mOrderCommontLv.collapseGroupWithAnimation(groupPosition);

                        } else {
                            mOrderCommontLv.expandGroupWithAnimation(groupPosition);
                        }
                        return true;
                    }
                });
            }
        }
    }


    /**
     * 初始化事件
     */
    private void initEvent() {
        //初始化下拉刷新控件的事件
        mOrderCommontRefresh.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                startPage = 1;
                checkedInitData(mOrderCommontDateRg.getCheckedRadioButtonId(),startPage,COMMONT_DATE);

            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                startPage = startPage +1;
                checkedInitData(mOrderCommontDateRg.getCheckedRadioButtonId(), startPage, LOAD_MORE);
            }

        });
        mOrderCommontTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mErrorBtnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPage = 1;
                checkedInitData(mOrderCommontDateRg.getCheckedRadioButtonId(),startPage,COMMONT_DATE);
            }
        });
        mOrderCommontDateRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mOrderCommontRefresh.finishRefreshLoadMore();
                mOrderCommontRefresh.finishRefresh();

                startPage = 1;
                checkedInitData(checkedId,1,COMMONT_DATE);
            }
        });



    }
    private void checkedInitData(int checkedId,int page,int which){
        switch (mType) {
            case 1:
                initData(mType,"",page,which);
                break;
            case 2:
                initData(mType,"",page,which);
                break;
            case 3:
                initData(mType,"",page,which);
                break;
            case 4:
                switch (checkedId) {
                    case R.id.order_commont_date_rg_1:
                        initData(mType, 0 + "",page,which);
                        break;
                    case R.id.order_commont_date_rg_2:
                        initData(mType, 1 + "",page,which);
                        break;
                    case R.id.order_commont_date_rg_3:
                        initData(mType, "",page,which);
                        break;
                    default:
                        break;
                }
                break;
            case 5:
                switch (checkedId) {
                    case R.id.order_commont_date_rg_1:
                        initData(mType, 1 + "",page,which);
                        break;
                    case R.id.order_commont_date_rg_2:
                        initData(mType, 2 + "",page,which);
                        break;
                    case R.id.order_commont_date_rg_3:
                        initData(mType, 3 + "",page,which);
                        break;
                    case R.id.order_commont_date_rg_4:
                        initData(mType, "",page,which);
                        break;
                    default:
                        break;
                }
                break;
            case 6:
                switch (checkedId) {
                    case R.id.order_commont_date_rg_1:
                        initData(mType, 1 + "",page,which);
                        break;
                    case R.id.order_commont_date_rg_2:
                        initData(mType, 2 + "",page,which);
                        break;
                    case R.id.order_commont_date_rg_3:
                        initData(mType, 3 + "",page,which);
                        break;
                    case R.id.order_commont_date_rg_4:
                        initData(mType, "",page,which);
                        break;
                    default:
                        break;
                }

                break;
            default:
                break;
        }
    }

    /**
     * 初始化数据
     */
    private List<OrderUnhandler.DataEntity.RowsEntity>  mData;
    private List<OrderUnhandler.DataEntity.RowsEntity>  mNewData;

    private void initData(final int type,String isSent,int page,int which) {
        String url = "";

        LogUtils.i("当前的page------"+startPage);
         switch (type){
              case 1:
                 url = Constants.URLS.ORDER_UNHANDLER+"?page="+page;
                  break;
              case 2:
                  url = Constants.URLS.ORDER_WAITREADY+"?page="+page;
                  break;
              case 3:
                  url = Constants.URLS.ORDER_WAITDELIVERY+"?page="+page;
                  break;
              case 4:
                  if (TextUtils.isEmpty(isSent)){
                      url = Constants.URLS.ORDER_TODAYADD+"?page="+page;
                  }else {
                      url = Constants.URLS.ORDER_TODAYADD+"?isSent="+isSent+"&page="+page;
                  }

                  break;
              case 5:
                  if (TextUtils.isEmpty(isSent)){
                      url = Constants.URLS.ORDER_FINISH+"?page="+page;
                  }else {
                      url = Constants.URLS.ORDER_FINISH+"?type="+isSent+"&page="+page;
                  }
                  break;
              case 6:
                  if (TextUtils.isEmpty(isSent)){
                      url =  Constants.URLS.ORDER_RETURN+"?page="+page;
                  }else {
                      url =  Constants.URLS.ORDER_RETURN+"?type="+isSent+"&page="+page;
                  }

                  break;
              default:
                  break;
          }
        LogUtils.i("url---------"+url);
        if (which == COMMONT_DATE){
            initTitle2();
            mData = new ArrayList<>();
            HttpManager manger = new HttpManager(UIUtils.getContext()){
                @Override
                protected void onSuccess(Object obj) {
                    LogUtils.i("obj-------"+obj);
                    if (obj != null){
                        Gson gson = new Gson();
                        OrderUnhandler orderUnhandler = null;
                        try {
                            orderUnhandler = gson.fromJson(obj + "", OrderUnhandler.class);
                        }catch (Exception e){
                            e.printStackTrace();
                            T.showShort(UIUtils.getContext(),"刷新失败");
                        }
                        if (orderUnhandler == null){
                            mCurState = DATA_STATE_EMPTY;
                            refreshUIByState();
                            return;
                        }

                        if (orderUnhandler.msg ==1){
                            LogUtils.i("解析了数据-------"+mCurState);
                            if (orderUnhandler.data != null){
                                if (orderUnhandler.data.rows != null && orderUnhandler.data.rows.size() != 0){
                                    mData = orderUnhandler.data.rows;
                                    mCurState = DATA_STATE_SUCCESS;
                                    LogUtils.i("获取数据成功-------"+mCurState);
                                }else {
                                    mCurState = DATA_STATE_EMPTY;
                                }
                            }else {
                                mCurState = DATA_STATE_EMPTY;
                            }
                        }else {
                            mCurState = DATA_STATE_FAIL;
                        }
                    }else {
                        mCurState = DATA_STATE_EMPTY;
                    }
                    LogUtils.i("开始刷新数据-------"+mCurState);
                    refreshUIByState();
                }

                @Override
                protected void onFail() {
                    mCurState = DATA_STATE_FAIL;
                    refreshUIByState();
                }
            };

            manger.get(url);
            mCurState = DATA_STATE_LOADING;
        }else {
            mNewData = new ArrayList<>();
            HttpManager manger = new HttpManager(UIUtils.getContext()){
                @Override
                protected void onSuccess(Object obj) {
                    mOrderCommontRefresh.finishRefreshLoadMore();

                    if (obj != null){
                        LogUtils.i("返回了数据-------"+obj);
                        Gson gson = new Gson();
                        OrderUnhandler orderUnhandler = gson.fromJson(obj + "", OrderUnhandler.class);
                        if (orderUnhandler.msg ==1){
                            LogUtils.i("解析了数据-------"+mCurState);
                            if (orderUnhandler.data != null){
                                if (orderUnhandler.data.rows != null && orderUnhandler.data.rows.size() != 0){
                                    mNewData = orderUnhandler.data.rows;
                                    mData.addAll(mNewData);
                                    mCurState = DATA_STATE_SUCCESS;
                                    refreshUIByState();
                                }else {
                                    startPage = startPage-1;
                                    T.showShort(UIUtils.getContext(),"没有更多数据了");
                                }
                            }else {
                                startPage = startPage-1;
                                T.showShort(UIUtils.getContext(),"没有更多数据了");
                            }
                        }else {
                            startPage = startPage-1;
                            T.showShort(UIUtils.getContext(),"加载更多失败");
                        }
                    }else {
                        startPage = startPage-1;
                        T.showShort(UIUtils.getContext(),"没有更多数据了");
                    }
                    LogUtils.i("开始刷新数据-------"+mCurState);
                }

                @Override
                protected void onFail() {
                    startPage = startPage-1;
                    mOrderCommontRefresh.finishRefreshLoadMore();
                    T.showShort(UIUtils.getContext(),"加载更多失败");
                }

                @Override
                protected void onTimeOut() {
                    startPage = startPage-1;
                    mOrderCommontRefresh.finishRefreshLoadMore();
                    T.showShort(UIUtils.getContext(), "加载更多超时");
                }
            };

            manger.get(url);
        }

    }



    ExpandAdapter mAdapter;

    class ExpandAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public View getRealChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.goods_statu_item_child_other, null);
                mItemHolder = new ItemHolder();
                mItemHolder.topFirstLine = convertView.findViewById(R.id.top_one_line);
                mItemHolder.firtEmptyView = convertView.findViewById(R.id.top_one_empty);
                mItemHolder.bottomLine = convertView.findViewById(R.id.down_line);
                mItemHolder.state = (TextView) convertView.findViewById(R.id.child_state);
                mItemHolder.txt = (TextView) convertView.findViewById(R.id.child_test_tv);
                mItemHolder.mFinishTime = (Button) convertView.findViewById(R.id.sign_finish_time);
                mItemHolder.bottomView = convertView.findViewById(R.id.bottom_view);
                mItemHolder.mSignView = (CustomSignView) convertView.findViewById(R.id.sign_view);
                mItemHolder.mother = (LinearLayout) convertView.findViewById(R.id.child_state_mother);
                mItemHolder.mPayView = (CustomPayView) convertView.findViewById(R.id.pay_view);

                convertView.setTag(mItemHolder);
            } else {
                mItemHolder = (ItemHolder) convertView.getTag();
            }

            if (childPosition == 0) {
                mItemHolder.topFirstLine.setVisibility(View.VISIBLE);
                mItemHolder.firtEmptyView.setVisibility(View.VISIBLE);
            } else {
                mItemHolder.firtEmptyView.setVisibility(View.GONE);
            }
             switch (getStateNumber(mData.get(groupPosition))){
                 case STATE_NOTAB:
                     mItemHolder.bottomLine.setVisibility(View.GONE);
                     mItemHolder.bottomView.setVisibility(View.VISIBLE);
                     break;
                 case STATE_NOTAB_RETURN:
                 case STATE_TAB_NOSIGN:
                     if (childPosition == 1){
                         mItemHolder.bottomLine.setVisibility(View.GONE);
                         mItemHolder.bottomView.setVisibility(View.VISIBLE);
                     }else {
                         mItemHolder.bottomLine.setVisibility(View.VISIBLE);
                         mItemHolder.bottomView.setVisibility(View.GONE);
                     }

                     break;
                 case STATE_TAB_NOSIGN_RETURN:
                 case STATE_TAB_SIGN_NOSEND:
                     if (childPosition == 2){
                         mItemHolder.bottomLine.setVisibility(View.GONE);
                         mItemHolder.bottomView.setVisibility(View.VISIBLE);
                     }else {
                         mItemHolder.bottomLine.setVisibility(View.VISIBLE);
                         mItemHolder.bottomView.setVisibility(View.GONE);
                     }
                     break;
                 case STATE_TAB_SIGN_NOSEND_RETURN:
                 case STATE_TAB_SIGN_SEND_NOACCOUNT:
                     if (childPosition == 3){
                         mItemHolder.bottomLine.setVisibility(View.GONE);
                         mItemHolder.bottomView.setVisibility(View.VISIBLE);
                     }else {
                         mItemHolder.bottomLine.setVisibility(View.VISIBLE);
                         mItemHolder.bottomView.setVisibility(View.GONE);
                     }
                     break;
                 case STATE_TAB_SIGN_SEND_NOACCOUT_RETURN:
                 case STATE_TAB_SIGN_SEND_ACCOUT_RETURN:
                 case STATE_TAB_SIGN_NOSEND_RETURN_ACCOUNT:
                     if (childPosition == 4){
                         mItemHolder.bottomLine.setVisibility(View.GONE);
                         mItemHolder.bottomView.setVisibility(View.VISIBLE);
                     }else {
                         mItemHolder.bottomLine.setVisibility(View.VISIBLE);
                         mItemHolder.bottomView.setVisibility(View.GONE);
                     }
                     break;
                 case STATE_TAB_SIGN_SEND_ACCOUT:
                     if (childPosition == 3){
                         mItemHolder.bottomLine.setVisibility(View.GONE);
                         mItemHolder.bottomView.setVisibility(View.VISIBLE);
                     }else {
                         mItemHolder.bottomLine.setVisibility(View.VISIBLE);
                         mItemHolder.bottomView.setVisibility(View.GONE);
                     }
                     break;

                 case 0:
                     LogUtils.i("数据错乱。。。什么鬼都没有-----");
                     break;
                 default:
                     break;
              }

            mItemHolder.txt.setPadding(5, 5, 5, 5);
            //写具体的显示逻辑 TODO


             switch (getStateNumber(mData.get(groupPosition))){
                 case STATE_NOTAB:
                     mItemHolder.mPayView.setVisibility(View.GONE);
                     mItemHolder.mother.setVisibility(View.GONE);
                     mItemHolder.mSignView.setVisibility(View.GONE);

                     if (mData.get(groupPosition).isHandler){
                         //说明是已经处理过的
                         mItemHolder.state.setText("已标记");
                         mItemHolder.txt.setText(mData.get(groupPosition).finishTime);
                         mItemHolder.mFinishTime.setVisibility(View.GONE);
                     }else {
                         mItemHolder.mFinishTime.setVisibility(View.VISIBLE);
                         mItemHolder.state.setText("未处理");
                         mItemHolder.txt.setText("");
                         mItemHolder.mFinishTime.setOnClickListener(new View.OnClickListener() {
                             @Override
                             public void onClick(View v) {
                                 current = groupPosition;
                                 calendar(mData.get(groupPosition).id);
                             }
                         });

                     }
                     break;
                 case STATE_NOTAB_RETURN:
                     if (childPosition == 0){
                         mItemHolder.mPayView.setVisibility(View.GONE);
                         mItemHolder.mother.setVisibility(View.GONE);
                         mItemHolder.mSignView.setVisibility(View.GONE);
                         mItemHolder.txt.setVisibility(View.VISIBLE);

                         if (mData.get(groupPosition).isHandler){
                             //说明是已经处理过的
                             mItemHolder.state.setText("已标记");
                             mItemHolder.txt.setText(mData.get(groupPosition).finishTime);
                             mItemHolder.mFinishTime.setVisibility(View.GONE);
                         }else {
                             mItemHolder.mFinishTime.setVisibility(View.VISIBLE);
                             mItemHolder.state.setText("未处理");
                             mItemHolder.txt.setText("");
                             mItemHolder.mFinishTime.setOnClickListener(new View.OnClickListener() {
                                 @Override
                                 public void onClick(View v) {
                                     current = groupPosition;
                                     calendar(mData.get(groupPosition).id);
                                 }
                             });
                         }
                     }else {
                         mItemHolder.mother.setVisibility(View.VISIBLE);
                         mItemHolder.mSignView.setVisibility(View.GONE);
                         mItemHolder.mFinishTime.setVisibility(View.GONE);
                         mItemHolder.mPayView.setVisibility(View.GONE);
                         mItemHolder.txt.setVisibility(View.GONE);
                         mItemHolder.mother.removeAllViews();

                         mItemHolder.state.setText("已退款");
                         List<OrderUnhandler.DataEntity.RowsEntity.ReturnEntity> retrunList = mData.get(groupPosition).returnOrderMapList;

                         for (int i = 0; i < retrunList.size(); i++) {
                             CustomChildStateReturn returnView = new CustomChildStateReturn(UIUtils.getContext());
                             returnView.setReturnDate(retrunList.get(i).returnTime);
                             returnView.setReturnMoney(retrunList.get(i).returnMoney);
                             returnView.setReturnNumber(retrunList.get(i).returnQty);
                             returnView.setReturnReason(retrunList.get(i).returnReason);
                             mItemHolder.mother.addView(returnView);
                         }
                     }
                     break;
                 case STATE_TAB_NOSIGN:
                     if (childPosition == 0){
                         mItemHolder.mFinishTime.setVisibility(View.GONE);
                         mItemHolder.mother.setVisibility(View.GONE);
                         mItemHolder.mSignView.setVisibility(View.GONE);
                         mItemHolder.txt.setVisibility(View.VISIBLE);
                         mItemHolder.mPayView.setVisibility(View.GONE);

                         mItemHolder.state.setText("已标记");
                         mItemHolder.txt.setText(mData.get(groupPosition).processedMap.opTime);

                     }else if (childPosition == 1){
                         mItemHolder.mFinishTime.setVisibility(View.GONE);
                         mItemHolder.mother.setVisibility(View.GONE);
                         mItemHolder.mSignView.setVisibility(View.GONE);
                         mItemHolder.txt.setVisibility(View.GONE);
                         mItemHolder.mPayView.setVisibility(View.GONE);

                         mItemHolder.state.setText("未签收");
                     }
                     break;
                 case STATE_TAB_NOSIGN_RETURN:
                     if (childPosition == 0 ){
                         mItemHolder.mFinishTime.setVisibility(View.GONE);
                         mItemHolder.mother.setVisibility(View.GONE);
                         mItemHolder.mSignView.setVisibility(View.GONE);
                         mItemHolder.txt.setVisibility(View.VISIBLE);
                         mItemHolder.mPayView.setVisibility(View.GONE);

                         mItemHolder.state.setText("已标记");
                         mItemHolder.txt.setText(mData.get(groupPosition).processedMap.opTime);

                     }else if (childPosition == 1){
                         mItemHolder.mFinishTime.setVisibility(View.GONE);
                         mItemHolder.mother.setVisibility(View.GONE);
                         mItemHolder.mSignView.setVisibility(View.GONE);
                         mItemHolder.txt.setVisibility(View.GONE);
                         mItemHolder.mPayView.setVisibility(View.GONE);

                         mItemHolder.state.setText("未签收");

                     }else if (childPosition == 2){
                         mItemHolder.mother.setVisibility(View.VISIBLE);
                         mItemHolder.mSignView.setVisibility(View.GONE);
                         mItemHolder.mFinishTime.setVisibility(View.GONE);
                         mItemHolder.mPayView.setVisibility(View.GONE);
                         mItemHolder.txt.setVisibility(View.GONE);
                         mItemHolder.mother.removeAllViews();

                         mItemHolder.state.setText("已退款");
                         List<OrderUnhandler.DataEntity.RowsEntity.ReturnEntity> retrunList = mData.get(groupPosition).returnOrderMapList;

                         for (int i = 0; i < retrunList.size(); i++) {
                             CustomChildStateReturn returnView = new CustomChildStateReturn(UIUtils.getContext());
                             returnView.setReturnDate(retrunList.get(i).returnTime);
                             returnView.setReturnMoney(retrunList.get(i).returnMoney);
                             returnView.setReturnNumber(retrunList.get(i).returnQty);
                             returnView.setReturnReason(retrunList.get(i).returnReason);
                             mItemHolder.mother.addView(returnView);
                         }
                     }
                     break;
                 case STATE_TAB_SIGN_NOSEND:
                     if (childPosition == 0){
                         mItemHolder.mFinishTime.setVisibility(View.GONE);
                         mItemHolder.mother.setVisibility(View.GONE);
                         mItemHolder.mSignView.setVisibility(View.GONE);
                         mItemHolder.txt.setVisibility(View.VISIBLE);
                         mItemHolder.mPayView.setVisibility(View.GONE);

                         mItemHolder.state.setText("已标记");
                         mItemHolder.txt.setText(mData.get(groupPosition).processedMap.opTime);

                     }else if (childPosition == 1){
                         mItemHolder.mFinishTime.setVisibility(View.GONE);
                         mItemHolder.mother.setVisibility(View.GONE);
                         mItemHolder.mSignView.setVisibility(View.VISIBLE);
                         mItemHolder.txt.setVisibility(View.VISIBLE);
                         mItemHolder.mPayView.setVisibility(View.GONE);

                         mItemHolder.state.setText("已签收");

                         OrderUnhandler.DataEntity.RowsEntity.SignedMap signedMap = mData.get(groupPosition).signedMap;
                         String comment = "暂无签收备注";
                         LogUtils.i("comment--------" + signedMap.comment);
                         if (!TextUtils.isEmpty(signedMap.comment)){
                             comment = signedMap.comment;
                         }
                         mItemHolder.txt.setText(signedMap.signMsg);
                         mItemHolder.mSignView.setSignName(signedMap.signName);
                         mItemHolder.mSignView.setSignTime(signedMap.opTime);
                         mItemHolder.mSignView.setSignViewInfo(comment);
                     }else if (childPosition == 2){
                         mItemHolder.mFinishTime.setVisibility(View.GONE);
                         mItemHolder.mother.setVisibility(View.GONE);
                         mItemHolder.mSignView.setVisibility(View.GONE);
                         mItemHolder.txt.setVisibility(View.GONE);
                         mItemHolder.mPayView.setVisibility(View.GONE);

                         mItemHolder.state.setText("未发货");
                     }

                     break;
                 case STATE_TAB_SIGN_NOSEND_RETURN:
                     if (childPosition == 0){
                         mItemHolder.mFinishTime.setVisibility(View.GONE);
                         mItemHolder.mother.setVisibility(View.GONE);
                         mItemHolder.mSignView.setVisibility(View.GONE);
                         mItemHolder.txt.setVisibility(View.VISIBLE);
                         mItemHolder.mPayView.setVisibility(View.GONE);

                         mItemHolder.state.setText("已标记");
                         mItemHolder.txt.setText(mData.get(groupPosition).processedMap.opTime);

                     }else if (childPosition == 1){
                         mItemHolder.mFinishTime.setVisibility(View.GONE);
                         mItemHolder.mother.setVisibility(View.GONE);
                         mItemHolder.mSignView.setVisibility(View.VISIBLE);
                         mItemHolder.txt.setVisibility(View.VISIBLE);
                         mItemHolder.mPayView.setVisibility(View.GONE);

                         mItemHolder.state.setText("已签收");

                         OrderUnhandler.DataEntity.RowsEntity.SignedMap signedMap = mData.get(groupPosition).signedMap;
                         String comment = "暂无签收备注";
                         LogUtils.i("comment--------" + signedMap.comment);
                         if (!TextUtils.isEmpty(signedMap.comment)){
                             comment = signedMap.comment;
                         }
                         mItemHolder.txt.setText(signedMap.signMsg);
                         mItemHolder.mSignView.setSignName(signedMap.signName);
                         mItemHolder.mSignView.setSignTime(signedMap.opTime);
                         mItemHolder.mSignView.setSignViewInfo(comment);
                     }else if (childPosition == 2){
                         mItemHolder.mFinishTime.setVisibility(View.GONE);
                         mItemHolder.mother.setVisibility(View.GONE);
                         mItemHolder.mSignView.setVisibility(View.GONE);
                         mItemHolder.txt.setVisibility(View.GONE);
                         mItemHolder.mPayView.setVisibility(View.GONE);

                         mItemHolder.state.setText("未发货");
                     }else if (childPosition == 3){
                         mItemHolder.mother.setVisibility(View.VISIBLE);
                         mItemHolder.mSignView.setVisibility(View.GONE);
                         mItemHolder.mFinishTime.setVisibility(View.GONE);
                         mItemHolder.mPayView.setVisibility(View.GONE);
                         mItemHolder.txt.setVisibility(View.GONE);
                         mItemHolder.mother.removeAllViews();

                         mItemHolder.state.setText("已退款");
                         List<OrderUnhandler.DataEntity.RowsEntity.ReturnEntity> retrunList = mData.get(groupPosition).returnOrderMapList;

                         for (int i = 0; i < retrunList.size(); i++) {
                             CustomChildStateReturn returnView = new CustomChildStateReturn(UIUtils.getContext());
                             returnView.setReturnDate(retrunList.get(i).returnTime);
                             returnView.setReturnMoney(retrunList.get(i).returnMoney);
                             returnView.setReturnNumber(retrunList.get(i).returnQty);
                             returnView.setReturnReason(retrunList.get(i).returnReason);
                             mItemHolder.mother.addView(returnView);
                         }
                     }
                     break;
                 case STATE_TAB_SIGN_NOSEND_RETURN_ACCOUNT:
                     if (childPosition == 0){
                         mItemHolder.mFinishTime.setVisibility(View.GONE);
                         mItemHolder.mother.setVisibility(View.GONE);
                         mItemHolder.mSignView.setVisibility(View.GONE);
                         mItemHolder.txt.setVisibility(View.VISIBLE);
                         mItemHolder.mPayView.setVisibility(View.GONE);

                         mItemHolder.state.setText("已标记");
                         mItemHolder.txt.setText(mData.get(groupPosition).processedMap.opTime);

                     }else if (childPosition == 1){
                         mItemHolder.mFinishTime.setVisibility(View.GONE);
                         mItemHolder.mother.setVisibility(View.GONE);
                         mItemHolder.mSignView.setVisibility(View.VISIBLE);
                         mItemHolder.txt.setVisibility(View.VISIBLE);
                         mItemHolder.mPayView.setVisibility(View.GONE);

                         mItemHolder.state.setText("已签收");

                         OrderUnhandler.DataEntity.RowsEntity.SignedMap signedMap = mData.get(groupPosition).signedMap;
                         String comment = "暂无签收备注";
                         LogUtils.i("comment--------" + signedMap.comment);
                         if (!TextUtils.isEmpty(signedMap.comment)){
                             comment = signedMap.comment;
                         }
                         mItemHolder.txt.setText(signedMap.signMsg);
                         mItemHolder.mSignView.setSignName(signedMap.signName);
                         mItemHolder.mSignView.setSignTime(signedMap.opTime);
                         mItemHolder.mSignView.setSignViewInfo(comment);
                     }else if (childPosition == 2) {
                         mItemHolder.mFinishTime.setVisibility(View.GONE);
                         mItemHolder.mother.setVisibility(View.GONE);
                         mItemHolder.mSignView.setVisibility(View.GONE);
                         mItemHolder.txt.setVisibility(View.GONE);
                         mItemHolder.mPayView.setVisibility(View.GONE);

                         mItemHolder.state.setText("未发货");
                     }else if (childPosition == 3){
                         mItemHolder.mFinishTime.setVisibility(View.GONE);
                         mItemHolder.mother.setVisibility(View.GONE);
                         mItemHolder.mSignView.setVisibility(View.GONE);
                         mItemHolder.mPayView.setVisibility(View.VISIBLE);
                         mItemHolder.txt.setVisibility(View.GONE);

                         mItemHolder.state.setText("已结算");
                         mItemHolder.mPayView.setPayDate(mData.get(groupPosition).statMap.opTime);
                         mItemHolder.mPayView.setPayNum(mData.get(groupPosition).statMap.totalMoney);

                     }else if (childPosition == 4){
                         mItemHolder.mother.setVisibility(View.VISIBLE);
                         mItemHolder.mSignView.setVisibility(View.GONE);
                         mItemHolder.mFinishTime.setVisibility(View.GONE);
                         mItemHolder.mPayView.setVisibility(View.GONE);
                         mItemHolder.txt.setVisibility(View.GONE);
                         mItemHolder.mother.removeAllViews();

                         mItemHolder.state.setText("已退款");
                         List<OrderUnhandler.DataEntity.RowsEntity.ReturnEntity> retrunList = mData.get(groupPosition).returnOrderMapList;

                         for (int i = 0; i < retrunList.size(); i++) {
                             CustomChildStateReturn returnView = new CustomChildStateReturn(UIUtils.getContext());
                             returnView.setReturnDate(retrunList.get(i).returnTime);
                             returnView.setReturnMoney(retrunList.get(i).returnMoney);
                             returnView.setReturnNumber(retrunList.get(i).returnQty);
                             returnView.setReturnReason(retrunList.get(i).returnReason);
                             mItemHolder.mother.addView(returnView);
                         }
                     }
                     break;
                 case STATE_TAB_SIGN_SEND_NOACCOUNT:
                     if (childPosition == 0){
                         mItemHolder.mFinishTime.setVisibility(View.GONE);
                         mItemHolder.mother.setVisibility(View.GONE);
                         mItemHolder.mSignView.setVisibility(View.GONE);
                         mItemHolder.txt.setVisibility(View.VISIBLE);
                         mItemHolder.mPayView.setVisibility(View.GONE);

                         mItemHolder.state.setText("已标记");
                         mItemHolder.txt.setText(mData.get(groupPosition).processedMap.opTime);
                     }else if (childPosition == 1){
                         mItemHolder.mFinishTime.setVisibility(View.GONE);
                         mItemHolder.mother.setVisibility(View.GONE);
                         mItemHolder.mSignView.setVisibility(View.VISIBLE);
                         mItemHolder.txt.setVisibility(View.VISIBLE);
                         mItemHolder.mPayView.setVisibility(View.GONE);

                         mItemHolder.state.setText("已签收");

                         OrderUnhandler.DataEntity.RowsEntity.SignedMap signedMap = mData.get(groupPosition).signedMap;
                         String comment = "暂无签收备注";
                         LogUtils.i("comment--------" + signedMap.comment);
                         if (!TextUtils.isEmpty(signedMap.comment)){
                             comment = signedMap.comment;
                         }
                         mItemHolder.txt.setText(signedMap.signMsg);
                         mItemHolder.mSignView.setSignName(signedMap.signName);
                         mItemHolder.mSignView.setSignTime(signedMap.opTime);
                         mItemHolder.mSignView.setSignViewInfo(comment);
                     }else if (childPosition == 2){
                         mItemHolder.mFinishTime.setVisibility(View.GONE);
                         mItemHolder.mother.setVisibility(View.GONE);
                         mItemHolder.mSignView.setVisibility(View.GONE);
                         mItemHolder.mPayView.setVisibility(View.GONE);
                         mItemHolder.txt.setVisibility(View.VISIBLE);

                         mItemHolder.state.setText("已发货");
                         mItemHolder.txt.setText("发货时间："+mData.get(groupPosition).goodsShippedMap.opTime);
                     }else if (childPosition == 3){
                         mItemHolder.mFinishTime.setVisibility(View.GONE);
                         mItemHolder.mother.setVisibility(View.GONE);
                         mItemHolder.mSignView.setVisibility(View.GONE);
                         mItemHolder.mPayView.setVisibility(View.GONE);

                         mItemHolder.state.setText("未结算");
                         mItemHolder.txt.setVisibility(View.GONE);
                     }
                     break;
                 case STATE_TAB_SIGN_SEND_NOACCOUT_RETURN:
                     if (childPosition == 0){
                         mItemHolder.mFinishTime.setVisibility(View.GONE);
                         mItemHolder.mother.setVisibility(View.GONE);
                         mItemHolder.mSignView.setVisibility(View.GONE);
                         mItemHolder.txt.setVisibility(View.VISIBLE);
                         mItemHolder.mPayView.setVisibility(View.GONE);

                         mItemHolder.state.setText("已标记");
                         mItemHolder.txt.setText(mData.get(groupPosition).processedMap.opTime);
                     }else if (childPosition == 1){
                         mItemHolder.mFinishTime.setVisibility(View.GONE);
                         mItemHolder.mother.setVisibility(View.GONE);
                         mItemHolder.mSignView.setVisibility(View.VISIBLE);
                         mItemHolder.txt.setVisibility(View.VISIBLE);
                         mItemHolder.mPayView.setVisibility(View.GONE);

                         mItemHolder.state.setText("已签收");

                         OrderUnhandler.DataEntity.RowsEntity.SignedMap signedMap = mData.get(groupPosition).signedMap;
                         String comment = "暂无签收备注";
                         LogUtils.i("comment--------" + signedMap.comment);
                         if (!TextUtils.isEmpty(signedMap.comment)){
                             comment = signedMap.comment;
                         }
                         mItemHolder.txt.setText(signedMap.signMsg);
                         mItemHolder.mSignView.setSignName(signedMap.signName);
                         mItemHolder.mSignView.setSignTime(signedMap.opTime);
                         mItemHolder.mSignView.setSignViewInfo(comment);
                     }else if (childPosition == 2){
                         mItemHolder.mFinishTime.setVisibility(View.GONE);
                         mItemHolder.mother.setVisibility(View.GONE);
                         mItemHolder.mSignView.setVisibility(View.GONE);
                         mItemHolder.mPayView.setVisibility(View.GONE);
                         mItemHolder.txt.setVisibility(View.VISIBLE);

                         mItemHolder.state.setText("已发货");
                         mItemHolder.txt.setText("发货时间："+mData.get(groupPosition).goodsShippedMap.opTime);
                     }else if (childPosition == 3){
                         mItemHolder.mFinishTime.setVisibility(View.GONE);
                         mItemHolder.mother.setVisibility(View.GONE);
                         mItemHolder.mSignView.setVisibility(View.GONE);
                         mItemHolder.mPayView.setVisibility(View.GONE);

                         mItemHolder.state.setText("未结算");
                         mItemHolder.txt.setVisibility(View.GONE);
                     }else if (childPosition == 4){
                         mItemHolder.mother.setVisibility(View.VISIBLE);
                         mItemHolder.mSignView.setVisibility(View.GONE);
                         mItemHolder.mFinishTime.setVisibility(View.GONE);
                         mItemHolder.mPayView.setVisibility(View.GONE);
                         mItemHolder.txt.setVisibility(View.GONE);
                         mItemHolder.mother.removeAllViews();

                         mItemHolder.state.setText("已退款");
                         List<OrderUnhandler.DataEntity.RowsEntity.ReturnEntity> retrunList = mData.get(groupPosition).returnOrderMapList;

                         for (int i = 0; i < retrunList.size(); i++) {
                             CustomChildStateReturn returnView = new CustomChildStateReturn(UIUtils.getContext());
                             returnView.setReturnDate(retrunList.get(i).returnTime);
                             returnView.setReturnMoney(retrunList.get(i).returnMoney);
                             returnView.setReturnNumber(retrunList.get(i).returnQty);
                             returnView.setReturnReason(retrunList.get(i).returnReason);
                             mItemHolder.mother.addView(returnView);
                         }
                     }
                     break;
                 case STATE_TAB_SIGN_SEND_ACCOUT:
                     if (childPosition == 0){
                         mItemHolder.mFinishTime.setVisibility(View.GONE);
                         mItemHolder.mother.setVisibility(View.GONE);
                         mItemHolder.mSignView.setVisibility(View.GONE);
                         mItemHolder.txt.setVisibility(View.VISIBLE);
                         mItemHolder.mPayView.setVisibility(View.GONE);

                         mItemHolder.state.setText("已标记");
                         mItemHolder.txt.setText(mData.get(groupPosition).processedMap.opTime);
                     }else if (childPosition == 1){
                         mItemHolder.mFinishTime.setVisibility(View.GONE);
                         mItemHolder.mother.setVisibility(View.GONE);
                         mItemHolder.mSignView.setVisibility(View.VISIBLE);
                         mItemHolder.txt.setVisibility(View.VISIBLE);
                         mItemHolder.mPayView.setVisibility(View.GONE);

                         mItemHolder.state.setText("已签收");

                         OrderUnhandler.DataEntity.RowsEntity.SignedMap signedMap = mData.get(groupPosition).signedMap;
                         String comment = "暂无签收备注";
                         LogUtils.i("comment--------" + signedMap.comment);
                         if (!TextUtils.isEmpty(signedMap.comment)){
                             comment = signedMap.comment;
                         }
                         mItemHolder.txt.setText(signedMap.signMsg);
                         mItemHolder.mSignView.setSignName(signedMap.signName);
                         mItemHolder.mSignView.setSignTime(signedMap.opTime);
                         mItemHolder.mSignView.setSignViewInfo(comment);
                     }else if (childPosition == 2){
                         mItemHolder.mFinishTime.setVisibility(View.GONE);
                         mItemHolder.mother.setVisibility(View.GONE);
                         mItemHolder.mSignView.setVisibility(View.GONE);
                         mItemHolder.mPayView.setVisibility(View.GONE);
                         mItemHolder.txt.setVisibility(View.VISIBLE);

                         mItemHolder.state.setText("已发货");
                         mItemHolder.txt.setText("发货时间："+mData.get(groupPosition).goodsShippedMap.opTime);
                     }else if (childPosition == 3){
                         mItemHolder.mFinishTime.setVisibility(View.GONE);
                         mItemHolder.mother.setVisibility(View.GONE);
                         mItemHolder.mSignView.setVisibility(View.GONE);
                         mItemHolder.mPayView.setVisibility(View.VISIBLE);
                         mItemHolder.txt.setVisibility(View.GONE);

                         mItemHolder.state.setText("已结算");
                         mItemHolder.mPayView.setPayDate(mData.get(groupPosition).statMap.opTime);
                         mItemHolder.mPayView.setPayNum(mData.get(groupPosition).statMap.totalMoney);
                     }
                     break;
                 case STATE_TAB_SIGN_SEND_ACCOUT_RETURN:
                     if (childPosition == 0){
                         mItemHolder.mFinishTime.setVisibility(View.GONE);
                         mItemHolder.mother.setVisibility(View.GONE);
                         mItemHolder.mSignView.setVisibility(View.GONE);
                         mItemHolder.txt.setVisibility(View.VISIBLE);
                         mItemHolder.mPayView.setVisibility(View.GONE);

                         mItemHolder.state.setText("已标记");
                         mItemHolder.txt.setText(mData.get(groupPosition).processedMap.opTime);
                     }else if (childPosition == 1){
                         mItemHolder.mFinishTime.setVisibility(View.GONE);
                         mItemHolder.mother.setVisibility(View.GONE);
                         mItemHolder.mSignView.setVisibility(View.VISIBLE);
                         mItemHolder.txt.setVisibility(View.VISIBLE);
                         mItemHolder.mPayView.setVisibility(View.GONE);

                         mItemHolder.state.setText("已签收");

                         OrderUnhandler.DataEntity.RowsEntity.SignedMap signedMap = mData.get(groupPosition).signedMap;
                         String comment = "暂无签收备注";
                         LogUtils.i("comment--------" + signedMap.comment);
                         if (!TextUtils.isEmpty(signedMap.comment)){
                             comment = signedMap.comment;
                         }
                         mItemHolder.txt.setText(signedMap.signMsg);
                         mItemHolder.mSignView.setSignName(signedMap.signName);
                         mItemHolder.mSignView.setSignTime(signedMap.opTime);
                         mItemHolder.mSignView.setSignViewInfo(comment);
                     }else if (childPosition == 2){
                         mItemHolder.mFinishTime.setVisibility(View.GONE);
                         mItemHolder.mother.setVisibility(View.GONE);
                         mItemHolder.mSignView.setVisibility(View.GONE);
                         mItemHolder.mPayView.setVisibility(View.GONE);
                         mItemHolder.txt.setVisibility(View.VISIBLE);

                         mItemHolder.state.setText("已发货");
                         mItemHolder.txt.setText("发货时间："+mData.get(groupPosition).goodsShippedMap.opTime);
                     }else if (childPosition == 3){
                         mItemHolder.mFinishTime.setVisibility(View.GONE);
                         mItemHolder.mother.setVisibility(View.GONE);
                         mItemHolder.mSignView.setVisibility(View.GONE);
                         mItemHolder.mPayView.setVisibility(View.VISIBLE);
                         mItemHolder.txt.setVisibility(View.GONE);

                         mItemHolder.state.setText("已结算");
                         mItemHolder.mPayView.setPayDate(mData.get(groupPosition).statMap.opTime);
                         mItemHolder.mPayView.setPayNum(mData.get(groupPosition).statMap.totalMoney);
                     }else if (childPosition == 4){
                         mItemHolder.mother.setVisibility(View.VISIBLE);
                         mItemHolder.mSignView.setVisibility(View.GONE);
                         mItemHolder.mFinishTime.setVisibility(View.GONE);
                         mItemHolder.mPayView.setVisibility(View.GONE);
                         mItemHolder.txt.setVisibility(View.GONE);
                         mItemHolder.mother.removeAllViews();

                         mItemHolder.state.setText("已退款");
                         List<OrderUnhandler.DataEntity.RowsEntity.ReturnEntity> retrunList = mData.get(groupPosition).returnOrderMapList;

                         for (int i = 0; i < retrunList.size(); i++) {
                             CustomChildStateReturn returnView = new CustomChildStateReturn(UIUtils.getContext());
                             returnView.setReturnDate(retrunList.get(i).returnTime);
                             returnView.setReturnMoney(retrunList.get(i).returnMoney);
                             returnView.setReturnNumber(retrunList.get(i).returnQty);
                             returnView.setReturnReason(retrunList.get(i).returnReason);
                             mItemHolder.mother.addView(returnView);
                         }
                     }
                     break;
                 case 0:
                     break;
                 default:
                     break;
              }

            return convertView;
        }

        @Override
        public int getRealChildrenCount(int groupPosition) {
            int childNum = 0;
             switch (getStateNumber(mData.get(groupPosition))){
                  case STATE_NOTAB:
                      childNum = 1;
                  break;
                 case STATE_NOTAB_RETURN:
                     childNum = 2;
                     break;
                 case STATE_TAB_NOSIGN:
                     childNum = 2;
                     break;
                 case STATE_TAB_NOSIGN_RETURN:
                     childNum = 3;
                     break;
                 case STATE_TAB_SIGN_NOSEND:
                     childNum = 3;
                     break;
                 case STATE_TAB_SIGN_NOSEND_RETURN:
                     childNum = 4;
                     break;
                 case STATE_TAB_SIGN_NOSEND_RETURN_ACCOUNT:
                     childNum = 5;
                     break;
                 case STATE_TAB_SIGN_SEND_NOACCOUNT:
                     childNum = 4;
                     break;
                 case STATE_TAB_SIGN_SEND_NOACCOUT_RETURN:
                     childNum = 5;
                     break;
                 case STATE_TAB_SIGN_SEND_ACCOUT:
                     childNum = 4;
                     break;
                 case STATE_TAB_SIGN_SEND_ACCOUT_RETURN:
                     childNum = 5;
                     break;
                 case 0:
                     break;
                  default:
                  break;
              }

            LogUtils.i("childnumber------"+childNum);
            return childNum;
        }

        @Override
        public int getGroupCount() {
            if (mData != null){
                return mData.size();
            }
            return 0;

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
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_order_unhandler, null);
                holder = new ViewHolder();
                holder.showView = (CutomCommontOrderItem) convertView.findViewById(R.id.order_unhandler_item_view);
                holder.mLv = (ListView) convertView.findViewById(R.id.order_unhandler_item_lv);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            OrderUnhandler.DataEntity.RowsEntity entity = mData.get(groupPosition);
            holder.showView.setGoodsColor(entity.colorName);
            holder.showView.setGoodsName(entity.productName_cn);
            holder.showView.setGoodsNumber(entity.productNumber);
            holder.showView.setGoodPrice(entity.purchasedPrice);
            holder.showView.setGoodsSize(entity.sizeAbbr);
            //
            holder.showView.setOrderNumber(entity.orderNo);

            holder.showView.setPayTime(entity.payTime);
            holder.showView.setSumNum(entity.purchaseNum2);
            holder.showView.setOrderPrice(entity.totalMoney);
            holder.showView.setGoodsImage(entity.imageURL_1);
            holder.showView.hintGoodsType();
            holder.showView.hindArrow();

            if (isExpanded) {
                holder.showView.changeImageView(true);
            } else {
                holder.showView.changeImageView(false);
            }
            if (groupPosition == 0) {
                holder.showView.isVisiable(false);
            } else {
                holder.showView.isVisiable(true);
            }


            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }


    ViewHolder holder;
    ItemHolder mItemHolder;

    class ItemHolder {
        public TextView txt;
        public TextView state;

        View topFirstLine;
        View bottomLine;
        View firtEmptyView;
        View bottomView;

        Button mFinishTime;
        CustomSignView mSignView;
        LinearLayout mother;
        CustomPayView mPayView;
    }


    private class ViewHolder {
        CutomCommontOrderItem showView;
        ListView mLv;
    }

    private int getStateNumber(OrderUnhandler.DataEntity.RowsEntity entity){

        if (entity.flowCount == 0 && entity.processedMap == null && (entity.returnOrderMapList == null || entity.returnOrderMapList.size() == 0)){
            return STATE_NOTAB;
        }else if(entity.flowCount == 1 && entity.processedMap == null && entity.returnOrderMapList != null && entity.returnOrderMapList.size() > 0){
            return STATE_NOTAB_RETURN;
        }else if(entity.flowCount == 1 && entity.processedMap!= null && entity.signedMap == null && (entity.returnOrderMapList == null || entity.returnOrderMapList.size() <= 0)){
            return STATE_TAB_NOSIGN;
        }else if (entity.flowCount == 2 && entity.processedMap != null && entity.signedMap == null && entity.returnOrderMapList != null && entity.returnOrderMapList.size() >0){
            return STATE_TAB_NOSIGN_RETURN;
        }else if (entity.flowCount == 2 && entity.processedMap != null && entity.signedMap != null && entity.goodsShippedMap == null && (entity.returnOrderMapList == null || entity.returnOrderMapList.size() <=0)){
            return STATE_TAB_SIGN_NOSEND;
        }else if (entity.flowCount == 3 && entity.processedMap != null && entity.signedMap != null && entity.goodsShippedMap == null && entity.returnOrderMapList != null && entity.returnOrderMapList.size() > 0 &&  entity.statMap == null){
            return STATE_TAB_SIGN_NOSEND_RETURN;
        }else if (entity.flowCount == 3 && entity.processedMap != null && entity.signedMap != null && entity.goodsShippedMap == null && entity.returnOrderMapList != null && entity.returnOrderMapList.size() > 0 && entity.statMap != null){
            return STATE_TAB_SIGN_NOSEND_RETURN_ACCOUNT;
        }else if (entity.flowCount == 3 && entity.processedMap != null && entity.signedMap != null && entity.goodsShippedMap != null && entity.statMap == null && (entity.returnOrderMapList == null || entity.returnOrderMapList.size() <= 0)){
            return STATE_TAB_SIGN_SEND_NOACCOUNT;
        }else if (entity.flowCount == 4 && entity.processedMap != null && entity.signedMap != null && entity.goodsShippedMap != null && entity.statMap == null && entity.returnOrderMapList != null && entity.returnOrderMapList.size() > 0){
            return STATE_TAB_SIGN_SEND_NOACCOUT_RETURN;
        }else if (entity.flowCount == 4 && entity.processedMap != null && entity.signedMap != null && entity.goodsShippedMap != null && entity.statMap != null && (entity.returnOrderMapList == null || entity.returnOrderMapList.size() <= 0)){
            return STATE_TAB_SIGN_SEND_ACCOUT;
        }else if (entity.flowCount == 5 && entity.processedMap != null && entity.signedMap != null && entity.goodsShippedMap != null && entity.statMap != null && entity.returnOrderMapList != null && entity.returnOrderMapList.size() > 0){
            return STATE_TAB_SIGN_SEND_ACCOUT_RETURN;
        }else if(entity.flowCount == 2 && entity.returnOrderMapList != null && entity.returnOrderMapList.size() > 0 && entity.statMap != null){
            return STATE_NOTAB_RETURN;
        }

        return 0;
    }


    private int current = -1;

    public void calendar(String id) {
        Intent intent = new Intent(OrderCommontActivty.this, CalendarActivity.class);

        intent.putExtra("id",id);

        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.i("从日历返回了---------");
        if (resultCode == 1) {
            final String selectDate = data.getExtras().getString("result");
            if ("notPru".equals(selectDate)) {
                mData.get(current).finishTime = "商品暂不生产";
                mData.get(current).isHandler = true;
            } else {
                T.showShort(UIUtils.getContext(), "选择的日期是--" + selectDate);

                mData.get(current).finishTime = "商品需要在" + selectDate + "之前备齐";
                mData.get(current).isHandler = true;
            }

            mAdapter.notifyDataSetChanged();

        }
    }


}
