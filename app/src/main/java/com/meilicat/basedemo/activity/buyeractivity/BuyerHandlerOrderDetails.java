package com.meilicat.basedemo.activity.buyeractivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.meilicat.basedemo.R;
import com.meilicat.basedemo.autolayout.AutoLayoutActivity;
import com.meilicat.basedemo.bean.purchaser.PurchaserHanlderDetail;
import com.meilicat.basedemo.conf.Constants;
import com.meilicat.basedemo.utils.HttpManager;
import com.meilicat.basedemo.utils.ImageLoaderUtil;
import com.meilicat.basedemo.utils.LogUtils;
import com.meilicat.basedemo.utils.UIUtils;
import com.pnikosis.materialishprogress.ProgressWheel;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by cj on 2016/2/23.
 */
public class BuyerHandlerOrderDetails extends AutoLayoutActivity {

    @Bind(R.id.buyer_order_details_title_back)
    RelativeLayout mBuyerOrderDetailsTitleBack;
    @Bind(R.id.buyer_order_details_iamge)
    ImageView mBuyerOrderDetailsIamge;
    @Bind(R.id.buyer_order_details_ordernum)
    TextView mBuyerOrderDetailsOrdernum;
    @Bind(R.id.buyer_order_details_paytime)
    TextView mBuyerOrderDetailsPaytime;
    @Bind(R.id.buyer_order_details_goodsname)
    TextView mBuyerOrderDetailsGoodsname;
    @Bind(R.id.buyer_order_details_goodsnum)
    TextView mBuyerOrderDetailsGoodsnum;
    @Bind(R.id.buyer_order_details_goodscolor)
    TextView mBuyerOrderDetailsGoodscolor;
    @Bind(R.id.buyer_order_details_goodssize)
    TextView mBuyerOrderDetailsGoodssize;
    @Bind(R.id.buyer_order_details_number)
    TextView mBuyerOrderDetailsNumber;
    @Bind(R.id.buyer_order_details_price)
    TextView mBuyerOrderDetailsPrice;
    @Bind(R.id.buyer_order_details_marktime)
    TextView mBuyerOrderDetailsMarktime;
    @Bind(R.id.buyer_order_details_signinfo)
    TextView mBuyerOrderDetailsSigninfo;
    @Bind(R.id.buyer_order_details_signtime)
    TextView mBuyerOrderDetailsSigntime;
    @Bind(R.id.buyer_order_details_signname)
    TextView mBuyerOrderDetailsSignname;
    @Bind(R.id.buyer_order_details_remark)
    TextView mBuyerOrderDetailsRemark;
    @Bind(R.id.buyer_handler_loading)
    ProgressWheel mBuyerHandlerLoading;
    @Bind(R.id.error_btn_retry)
    Button mErrorBtnRetry;
    @Bind(R.id.buyer_handler_error)
    LinearLayout mBuyerHandlerError;
    @Bind(R.id.buyer_handler_empty)
    LinearLayout mBuyerHandlerEmpty;
    @Bind(R.id.buyer_handler_orderdetails_sc)
    ScrollView mBuyerHandlerOrderdetailsSc;


    public static final int STATE_EMPTY = 0;
    public static final int STATE_ERROR = 1;
    public static final int STATE_SUCCESS = 2;
    public static final int STATE_NONE = -1;
    public static final int STATE_LOADING = 3;

    private int currentState = STATE_NONE;
    private PurchaserHanlderDetail mDetail;
    private String mId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_handler_orderdetails);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mId = intent.getStringExtra("id");

        initData(Integer.parseInt(mId));

        initEvent();


    }



    private void initData(int id) {
        String url = Constants.URLS.BUYER_HANDLER_DETAIL + "?id=" + id;

        HttpManager manger = new HttpManager(UIUtils.getContext()) {
            @Override
            protected void onSuccess(Object obj) {
                LogUtils.i("obhj---------"+obj);
                if (obj != null){
                    Gson gson = new Gson();
                    mDetail = gson.fromJson(obj + "", PurchaserHanlderDetail.class);
                    if (mDetail !=null){
                        if (mDetail.data != null){
                            if (mDetail.data.supplierPurchaseDetailShow != null){
                                currentState = STATE_SUCCESS;

                            }else {
                                currentState = STATE_EMPTY;
                            }
                        }else {
                            currentState = STATE_EMPTY;
                        }
                    }else {
                        currentState = STATE_EMPTY;
                    }

                }else {
                    currentState = STATE_ERROR;
                }

                refeshUI(currentState);
            }

            @Override
            protected void onFail() {
                currentState = STATE_ERROR;
                refeshUI(currentState);
            }

            @Override
            protected void onTimeOut() {
                currentState = STATE_ERROR;
                refeshUI(currentState);
            }
        };

        manger.get(url);

    }
    public void refeshUI(int type) {
        mBuyerHandlerError.setVisibility((type == STATE_ERROR) || (type == STATE_NONE) ? View.VISIBLE : View.GONE);
        mBuyerHandlerEmpty.setVisibility((type == STATE_EMPTY) || (type == STATE_NONE) ? View.VISIBLE : View.GONE);
        mBuyerHandlerOrderdetailsSc.setVisibility((type == STATE_SUCCESS) || (type == STATE_NONE) ? View.VISIBLE : View.GONE);
        mBuyerHandlerLoading.setVisibility((type == STATE_LOADING) || (type == STATE_NONE) ? View.VISIBLE : View.GONE);
        if (type == STATE_SUCCESS){
            initView();
        }

    }

    private void initEvent() {
        mBuyerOrderDetailsTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mErrorBtnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData(Integer.parseInt(mId));
            }
        });
    }

    public void initView() {
        if (mDetail== null){
            return;
        }
        if (mDetail.data == null){
            return;
        }

        if (mDetail.data.supplierPurchaseDetailShow != null){
            PurchaserHanlderDetail.DataEntity.SupplierPurchaseDetailShowEntity detailShow = mDetail.data.supplierPurchaseDetailShow;

            ImageLoaderUtil.getInstance(UIUtils.getContext()).displayImage(detailShow.mobileImageURL_1, mBuyerOrderDetailsIamge);
            mBuyerOrderDetailsOrdernum.setText(detailShow.orderNo);
            mBuyerOrderDetailsPaytime.setText(detailShow.payTime);
            String productName = detailShow.productName_cn.replaceAll(" ","");

            mBuyerOrderDetailsGoodsname.setText(productName);
            mBuyerOrderDetailsGoodsnum.setText(detailShow.productNumber);

            mBuyerOrderDetailsGoodscolor.setText(detailShow.colorName);
            mBuyerOrderDetailsGoodssize.setText(detailShow.sizeAbbr);
            mBuyerOrderDetailsNumber.setText(detailShow.purchaseNum+"件");

            mBuyerOrderDetailsPrice.setText("￥ "+detailShow.totalPurchasedPrice);

            mBuyerOrderDetailsSigninfo.setText(detailShow.appComment);
            String THDate = detailShow.THDate;

            if (TextUtils.isEmpty(THDate)){
                THDate = "1111-11-11";
            }else {
                THDate = THDate.substring(0,10);
            }

            mBuyerOrderDetailsMarktime.setText("已标记 商品于"+THDate+"备齐");

            String actualTHDate = detailShow.actualTHDate.substring(0,10);
            mBuyerOrderDetailsSigntime.setText(actualTHDate);

            mBuyerOrderDetailsSignname.setText(detailShow.fullName);
            if (TextUtils.isEmpty(detailShow.comment)){
                mBuyerOrderDetailsRemark.setText("无签收备注");
            }else {
                mBuyerOrderDetailsRemark.setText(detailShow.comment);
            }
        }
    }


}
