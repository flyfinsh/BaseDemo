package com.meilicat.basedemo.activity.commonditymanager;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.meilicat.basedemo.R;
import com.meilicat.basedemo.base.BaseApplication;
import com.meilicat.basedemo.base.BaseTitleActivity;
import com.meilicat.basedemo.bean.AppSimpleBean;
import com.meilicat.basedemo.bean.ClientColorSizeBean;
import com.meilicat.basedemo.bean.GoodsDetailBean;
import com.meilicat.basedemo.bean.GoodsProductSkuListEntity;
import com.meilicat.basedemo.bean.GoodsRowsEntity;
import com.meilicat.basedemo.conf.Constants;
import com.meilicat.basedemo.event.BusProvider;
import com.meilicat.basedemo.event.FinishActivityEvent;
import com.meilicat.basedemo.event.NotifyUpdateEvent;
import com.meilicat.basedemo.event.SelectColorSizeEvent;
import com.meilicat.basedemo.fragment.commodity.GoodsDeatailsStatuFragment;
import com.meilicat.basedemo.utils.DeviceConfiger;
import com.meilicat.basedemo.utils.HttpManager;
import com.meilicat.basedemo.utils.RadioTabManager;
import com.meilicat.basedemo.utils.UIUtils;
import com.meilicat.basedemo.utils.ViewUtils;
import com.meilicat.basedemo.view.FlowLayout;
import com.meilicat.basedemo.view.NoToggleCheckBox;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by lizhiming on 2016/1/23.
 */
public class CommondityDetailsActivity extends BaseTitleActivity implements RadioTabManager.BuildViewFactory, View.OnClickListener {

    public static final String KEY_TYPE = "key_type";
    public static final String KEY_DATA = "key_data";

    private boolean isCheckType;

    private String mGoodsId;
    private GoodsRowsEntity mData;

    @Bind(R.id.goods_detail_head_layout)
    RelativeLayout mHeadLayout;

    @Bind(R.id.goods_deatails_iv)
    ImageView mGoodsIv;
    @Bind(R.id.goods_deatails_name_tv)
    TextView mGoodsNameTv;
    @Bind(R.id.goods_deatails_num_tv)
    TextView mGoodsNumTv;
    @Bind(R.id.goods_deatails_color_tv)
    TextView mGoodsColorTv;
    @Bind(R.id.goods_deatails_size_tv)
    TextView mGoodsSizeTv;
    @Bind(R.id.goods_deatails_price_tv)
    TextView mGoodsPriceTv;

    @Bind(R.id.goods_norm_down_layout)
    LinearLayout mNormDownLayout;
    @Bind(R.id.goods_norm_down_iv)
    ImageView mNormDownIv;

    @Bind(R.id.all_norms_layout)
    LinearLayout mAllNormsLayout;
    @Bind(R.id.goods_deatails_cs_all_tv)
    TextView mAllNormsTv;

    @Bind(R.id.goods_deatails_check_layout)
    RelativeLayout mCheckLayout;
    @Bind(R.id.check_statu_tv)
    TextView mCheckStatuTv;
    @Bind(R.id.check_time_tv)
    TextView mCheckTimeTv;
    @Bind(R.id.check_statu_desc_tv)
    TextView mCheckDescTv;
    @Bind(R.id.goods_deatails_modify_tv)
    TextView mModifyTv;
    @Bind(R.id.goods_deatails_del_tv)
    TextView mDelTv;

    @Bind(R.id.goods_deatails_order_listlayout)
    RelativeLayout mOrderListLayout;
    @Bind(R.id.goods_deatails_tab_contain)
    RadioGroup mTabContain;

    private RadioTabManager mManager;

    private List<String> mFilterColorList = new ArrayList<>();
    private List<String> mFilterSizeList = new ArrayList<>();

    //显示新规格UI组装数据
    ArrayList<ClientColorSizeBean> mTempCSDatas = new ArrayList<>();

    @Override
    public void setContent() {
        super.setContent();
        setContentView(R.layout.activity_commondity_details_layout);
        ButterKnife.bind(this);

        BusProvider.getInstance().register(this);

        setupNavigationBar(R.id.navigation_bar);
        Bundle extra = getIntent().getExtras();

        if (extra != null) {
            isCheckType = extra.getBoolean(KEY_TYPE, false);
            mGoodsId = extra.getString(KEY_DATA);
        }
    }

    @Override
    public void initView() {
        super.initView();
//        if (mData != null) {  不传对象过来了
//            refreshUI();
//        }

        mHeadLayout.setOnClickListener(this);
        mModifyTv.setOnClickListener(this);
        mDelTv.setOnClickListener(this);
        mNormDownLayout.setOnClickListener(this);

        if (!TextUtils.isEmpty(mGoodsId)) {
            getDetailData(false);
        }
    }

    /**
     * 更新详情信息
     */
    private void refreshUI() {
        String imageUrl = mData.getImageURL_1();
        if (!TextUtils.isEmpty(imageUrl)) {
            BaseApplication.imageLoader.displayImage(imageUrl, mGoodsIv, BaseApplication.options);
        }

        mGoodsNameTv.setText(mData.getCn());
        mGoodsNumTv.setText(mData.getProductNumber());
        mGoodsPriceTv.setText(mData.getPrice() + "");

        List<GoodsProductSkuListEntity> childList = mData.getSupplierProductSkuList();
        if (childList != null && !childList.isEmpty()) {
            if (!mFilterColorList.isEmpty()) {
                mFilterColorList.clear();
            }
            if (!mFilterSizeList.isEmpty()) {
                mFilterSizeList.clear();
            }

            mTempCSDatas.clear();
            for (GoodsProductSkuListEntity childItem : childList) {
                if (!isHaveColor(childItem.getColorName())) {
                    mFilterColorList.add(childItem.getColorName());
                }

                if (!isHaveSize(childItem.getSizeName())) {
                    mFilterSizeList.add(childItem.getSizeName());
                }

                if (GoodsModelUtils.isHaveColor(mTempCSDatas, childItem)) {
                    mTempCSDatas = GoodsModelUtils.appendSizeData(mTempCSDatas, childItem);
                } else {
                    ClientColorSizeBean otherBean = new ClientColorSizeBean();
                    otherBean.colorName = childItem.getColorName();
                    otherBean.sizeName = childItem.getSizeName();
                    mTempCSDatas.add(otherBean);
                }
            }

            if (mTempCSDatas.size() == 1) {
                mNormDownLayout.setVisibility(View.GONE);
                mGoodsColorTv.setText(mTempCSDatas.get(0).colorName + "：" + mTempCSDatas.get(0).sizeName);
                mGoodsSizeTv.setVisibility(View.GONE);
            } else if (mTempCSDatas.size() == 2) {
                mNormDownLayout.setVisibility(View.GONE);
                mGoodsSizeTv.setVisibility(View.VISIBLE);
                mGoodsColorTv.setText(mTempCSDatas.get(0).colorName + "：" + mTempCSDatas.get(0).sizeName);
                mGoodsSizeTv.setText(mTempCSDatas.get(1).colorName + "：" + mTempCSDatas.get(1).sizeName);
            } else if (mTempCSDatas.size() > 2) {
                mNormDownLayout.setVisibility(View.VISIBLE);
                mGoodsSizeTv.setVisibility(View.VISIBLE);
                mGoodsColorTv.setText(mTempCSDatas.get(0).colorName + "：" + mTempCSDatas.get(0).sizeName);
                mGoodsSizeTv.setText(mTempCSDatas.get(1).colorName + "：" + mTempCSDatas.get(1).sizeName);

                StringBuffer sbNorms = new StringBuffer();
                for (int i = 0; i < mTempCSDatas.size(); i++) {
                    ClientColorSizeBean csItem = mTempCSDatas.get(i);
                    if (i == mTempCSDatas.size() - 1) {
                        sbNorms.append(csItem.colorName + "：" + csItem.sizeName);
                    } else {
                        sbNorms.append(csItem.colorName + "：" + csItem.sizeName + "\n");
                    }
                }
                mAllNormsTv.setText(sbNorms.toString());
            }
        }

        int statu = mData.getStatus();
        if (statu == 0) { //待审核
            mCheckStatuTv.setText("待审核");
            mCheckStatuTv.setTextColor(Color.parseColor("#333333"));
            mCheckDescTv.setText("商品资料已上传成功，美丽猫工作人员会尽快为您审核，请您耐心等候。");
            mModifyTv.setVisibility(View.GONE);
            mDelTv.setVisibility(View.GONE);
        } else if (statu == 1) { //已通过
            mCheckStatuTv.setText("审核通过");
            mCheckStatuTv.setTextColor(Color.parseColor("#333333"));
            mCheckDescTv.setText("恭喜您，您的商品已通过审核，请将商品邮寄或送至美丽猫，我们会尽快帮您上架。");
            mModifyTv.setVisibility(View.GONE);
            mDelTv.setVisibility(View.GONE);
        } else if (statu == 2) { //已拒绝
            mCheckStatuTv.setText("已拒绝");
            mCheckStatuTv.setTextColor(Color.parseColor("#ff0099"));
            mCheckDescTv.setText(mData.getRefuseReason());
            mModifyTv.setVisibility(View.VISIBLE);
            mDelTv.setVisibility(View.VISIBLE);
        }
        mCheckTimeTv.setText(mData.getApproveTime());


        if (isCheckType) {
            setCommonTitle("审核详情");
        } else {
            setCommonTitle("商品详情");
            addRightButtonText("筛选", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!choosedColorTags.isEmpty()) {
                        choosedColorTags.clear();
                    }
                    if (!choosedSizeTags.isEmpty()) {
                        choosedSizeTags.clear();
                    }

                    showFilterDialog();
                }
            });
        }
        if (isCheckType) {
            mCheckLayout.setVisibility(View.VISIBLE);
            mOrderListLayout.setVisibility(View.GONE);
        } else {
            mCheckLayout.setVisibility(View.GONE);
            mOrderListLayout.setVisibility(View.VISIBLE);
            if (mManager == null) {
                initTabLayout();
            }
        }
    }

    private boolean isHaveSize(String sizeName) {
        if (mFilterSizeList.isEmpty()) {
            return false;
        }

        for (String size : mFilterSizeList) {
            if (size.equals(sizeName)) {
                return true;
            }
        }
        return false;
    }

    private boolean isHaveColor(String colorName) {
        if (mFilterColorList.isEmpty()) {
            return false;
        }

        for (String color : mFilterColorList) {
            if (color.equals(colorName)) {
                return true;
            }
        }

        return false;
    }

    private void initTabLayout() {
        mManager = new RadioTabManager(this, getSupportFragmentManager(), mTabContain, R.id.goods_deatails_stub_layout, this);
        Bundle args = new Bundle();
        args.putString(GoodsDeatailsStatuFragment.REQUEST_TYPE, GoodsDeatailsStatuFragment.TYPE_SALE_ALREADY);
        args.putString(GoodsDeatailsStatuFragment.REQUEST_ID, mGoodsId);
        mManager.addTab(GoodsDeatailsStatuFragment.TYPE_SALE_ALREADY, GoodsDeatailsStatuFragment.class, args);
        args = new Bundle();
        args.putString(GoodsDeatailsStatuFragment.REQUEST_TYPE, GoodsDeatailsStatuFragment.TYPE_AWAY_ON);
        args.putString(GoodsDeatailsStatuFragment.REQUEST_ID, mGoodsId);
        mManager.addTab(GoodsDeatailsStatuFragment.TYPE_AWAY_ON, GoodsDeatailsStatuFragment.class, args);
//        args = new Bundle();
//        args.putString(GoodsDeatailsStatuFragment.REQUEST_TYPE,
//                GoodsDeatailsStatuFragment.TYPE_HAVED);
//        mManager.addTab(GoodsDeatailsStatuFragment.TYPE_HAVED, GoodsDeatailsStatuFragment.class,
//                args);
        mManager.setCurrFragmentByTag(GoodsDeatailsStatuFragment.TYPE_SALE_ALREADY);
    }

    @Override
    public View tabView(String tag, ViewGroup parent) {
        RadioButton tabView = newRadioButton();
        if (GoodsDeatailsStatuFragment.TYPE_SALE_ALREADY.equals(tag)) {
            if (mData != null) {
                tabView.setText(mData.getStatCount() + "件");
            } else {
                tabView.setText("0件");
            }
        } else if (GoodsDeatailsStatuFragment.TYPE_AWAY_ON.equals(tag)) {
            if (mData != null) {
                tabView.setText(mData.getTransitCount() + "件");
            } else {
                tabView.setText("0件");
            }
        }
        return tabView;
    }

    private RadioButton newRadioButton() {
        RadioButton btn = new RadioButton(this);
        btn.setButtonDrawable(new BitmapDrawable());
        btn.setBackgroundDrawable(ViewUtils.createTabItemDividerBg(Color.parseColor("#ff0099")));
        btn.setGravity(Gravity.CENTER_HORIZONTAL);
        btn.setPadding(0, DeviceConfiger.dp2px(5), 0, 0);
        btn.setTextColor(ViewUtils.createTabItemTextColor(Color.parseColor("#333333"), Color.parseColor("#ff0099")));
        return btn;
    }

    private FlowLayout mColorLayout;
    private FlowLayout mSizeLayout;

    private void showFilterDialog() {

        final Dialog dialog = new Dialog(this, R.style.common_dialog_theme);
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogContentView = inflater.inflate(R.layout.dialog_goods_filter, null);

        mColorLayout = (FlowLayout) dialogContentView.findViewById(R.id.goods_filter_color_layout);
        addCanSelectTagView(mColorLayout, mFilterColorList);

        mSizeLayout = (FlowLayout) dialogContentView.findViewById(R.id.goods_filter_size_layout);
        addFilterSizeTagView(mSizeLayout, mFilterSizeList);

        TextView confirmTv = (TextView) dialogContentView.findViewById(R.id.goods_filter_confirm_tv);
        confirmTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                if (choosedColorTags.isEmpty() || choosedSizeTags.isEmpty()) {
                    ViewUtils.showToast(getApplicationContext(), "请选择好颜色和尺码");
                    return;
                }
                getDetailData(true);
            }
        });

        TextView cancleTv = (TextView) dialogContentView.findViewById(R.id.goods_filter_cancel_tv);
        cancleTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setContentView(dialogContentView);

        int dialogWidth = DeviceConfiger.dp2px(300);

        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = dialogWidth;
        dialogWindow.setAttributes(lp);

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /**
     * 筛选数据
     */
    private void getDetailData(final boolean isFilter) {
        showLoadDialog();
        HttpManager httpManager = new HttpManager(UIUtils.getContext()) {
            @Override
            protected void onSuccess(Object obj) {
                super.onSuccess(obj);
                dismissLoadDialog();
                Gson gson = new Gson();
                GoodsDetailBean bean = null;
                try {
                    bean = gson.fromJson(String.valueOf(obj), GoodsDetailBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (bean == null || bean.getData() == null) {
                    ViewUtils.showToast(getApplicationContext(), "没有详情数据");
                    return;
                }

                if (bean.getMsg() == 1) {
                    mData = bean.getData();
                    if (mData != null) {
                        refreshUI();
                        if (isFilter) {
                            BusProvider.getInstance().post(new SelectColorSizeEvent(choosedColorTags.get(0), choosedSizeTags.get(0)));
                        }

                        if (!isCheckType && mManager != null) {
                            mManager.updateDetailCount(mData.getStatCount() + "件", mData.getTransitCount() + "件");
                        }

                    }
                } else {
                    ViewUtils.showToast(getApplicationContext(), "获取详情失败");
                }
            }

            @Override
            protected void onFail() {
                super.onFail();
                dismissLoadDialog();
                ViewUtils.showToast(getApplicationContext(), "获取详情失败");
            }

            @Override
            protected void onTimeOut() {
                super.onTimeOut();
                dismissLoadDialog();
                ViewUtils.showToast(getApplicationContext(), "获取详情失败");
            }
        };

        if (isFilter) {
            httpManager.get(Constants.URLS.GOODS_DETAIL_GET + "id=" + mData.getId() + "&searchColorName=" + choosedColorTags.get(0) + "&searchSizeName=" + choosedSizeTags.get(0));
        } else {
            httpManager.get(Constants.URLS.GOODS_DETAIL_GET + "id=" + mGoodsId);
        }

    }

    private void addFilterSizeTagView(FlowLayout sizeLayout, List<String> testSize) {
        sizeLayout.removeAllViews();
        if (testSize != null) {
            for (int i = 0; i < testSize.size(); i++) {
                String tag = testSize.get(i);
                Context context = sizeLayout.getContext();
                View itemView = createSizeTagView(context, tag);
                sizeLayout.addView(itemView);
            }
        }
    }

    private ArrayList<String> choosedSizeTags = new ArrayList<>();

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private View createSizeTagView(Context context, final String tag) {

        View tagView = LayoutInflater.from(context).inflate(R.layout.filter_size_item, null);

        TextView sizeTv = (TextView) tagView.findViewById(R.id.filter_size_tv);
        sizeTv.setText(tag);

        final NoToggleCheckBox itemView = (NoToggleCheckBox) tagView.findViewById(R.id.filter_size_box);
        itemView.setGravity(Gravity.CENTER);
        if (choosedSizeTags.contains(tag)) {
            itemView.setChecked(true);
        } else {
            itemView.setChecked(false);
        }

        int paddingLR = DeviceConfiger.dp2px(10);
        int padding = DeviceConfiger.dp2px(5);
        itemView.setPadding(paddingLR, padding, paddingLR, padding);

        itemView.setTextColor(Color.parseColor("#666666"));
        int size = DeviceConfiger.dp2sp(14);
        itemView.setTextSize(size);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mSizeLayout != null) {
                    int childCount = mSizeLayout.getChildCount();
                    if (childCount > 0) {
                        for (int i = 0; i < childCount; i++) {
                            View itemCheckView = mSizeLayout.getChildAt(i);
                            NoToggleCheckBox itemCheck = (NoToggleCheckBox) itemCheckView.findViewById(R.id.filter_size_box);
                            itemCheck.setChecked(false);
                        }

                        if (!choosedSizeTags.isEmpty()) {
                            choosedSizeTags.clear();
                        }
                    }
                }

//                if (choosedSizeTags.contains(tag)) {
//                    choosedSizeTags.remove(tag);
//                    itemView.setChecked(false);
//                } else {
                choosedSizeTags.add(tag);
                itemView.setChecked(true);
//                }
            }
        });


        return tagView;
    }

    private void addCanSelectTagView(FlowLayout containLayout, List<String> requireTagList) {
        containLayout.removeAllViews();
        if (requireTagList != null) {
            for (int i = 0; i < requireTagList.size(); i++) {
                String tag = requireTagList.get(i);
                Context context = containLayout.getContext();
                View itemView = createCanSeclectTagView(context, tag, i);
                containLayout.addView(itemView);
            }
        }
    }

    private ArrayList<String> choosedColorTags = new ArrayList<>();

    /**
     * 创建带选择效果的标签
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private View createCanSeclectTagView(Context context, final String tag, int position) {

        View tagView = LayoutInflater.from(context).inflate(R.layout.filter_color_item, null);

        TextView colorV = (TextView) tagView.findViewById(R.id.filter_color_v);
        colorV.setBackgroundColor(Color.parseColor("#32c980"));

        TextView colorTv = (TextView) tagView.findViewById(R.id.filter_color_tv);
        colorTv.setText(tag);
        final NoToggleCheckBox itemView = (NoToggleCheckBox) tagView.findViewById(R.id.filter_color_box);
        itemView.setGravity(Gravity.CENTER);
        if (choosedColorTags.contains(tag)) {
            itemView.setChecked(true);
        } else {
            itemView.setChecked(false);
        }

        int paddingLR = DeviceConfiger.dp2px(10);
        int padding = DeviceConfiger.dp2px(5);
        itemView.setPadding(paddingLR, padding, paddingLR, padding);

        itemView.setTextColor(Color.parseColor("#666666"));
        int size = DeviceConfiger.dp2sp(14);
        itemView.setTextSize(size);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mColorLayout != null) {
                    int childCount = mColorLayout.getChildCount();
                    if (childCount > 0) {
                        for (int i = 0; i < childCount; i++) {
                            View itemCheckView = mColorLayout.getChildAt(i);
                            NoToggleCheckBox itemCheck = (NoToggleCheckBox) itemCheckView.findViewById(R.id.filter_color_box);
                            itemCheck.setChecked(false);
                        }

                        if (!choosedColorTags.isEmpty()) {
                            choosedColorTags.clear();
                        }
                    }
                }


//                if (choosedColorTags.contains(tag)) {
//                    choosedColorTags.remove(tag);
//                    itemView.setChecked(false);
//                } else {
                choosedColorTags.add(tag);
                itemView.setChecked(true);
//                }
            }
        });

        return tagView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.goods_detail_head_layout:
                if (mData == null) {
                    return;
                }
                Intent lookIntent = new Intent(this, CommodityPublishActivity.class);
                lookIntent.putExtra(CommodityPublishActivity.KEY_EDIT_DATA, mData);
                lookIntent.putParcelableArrayListExtra(CommodityPublishActivity.KEY_NORMS_SHOW_DATA, mTempCSDatas);
                lookIntent.putExtra(CommodityPublishActivity.KEY_IS_LOOK, true);
                startActivity(lookIntent);
                break;
            case R.id.goods_deatails_modify_tv:
                if (mData == null) {
                    return;
                }
                Intent editIntent = new Intent(this, CommodityPublishActivity.class);
                editIntent.putExtra(CommodityPublishActivity.KEY_EDIT_DATA, mData);
                editIntent.putExtra(CommodityPublishActivity.KEY_IS_LOOK, false);
                startActivity(editIntent);
                break;
            case R.id.goods_deatails_del_tv:
                deleteGoods();
                break;
            case R.id.goods_norm_down_layout:
                if (isExpanded) {
                    isExpanded = false;
                    mAllNormsLayout.setVisibility(View.GONE);
                    mNormDownIv.setImageResource(R.mipmap.item_ex_list_down);
                } else {
                    isExpanded = true;
                    mAllNormsLayout.setVisibility(View.VISIBLE);
                    mNormDownIv.setImageResource(R.mipmap.item_ex_list_up);
                }

                break;
        }
    }

    private boolean isExpanded;

    /**
     * 删除商品
     */
    private void deleteGoods() {
        if (TextUtils.isEmpty(mGoodsId)) {
            return;
        }

        showLoadDialog();
        HttpManager httpManager = new HttpManager(UIUtils.getContext()) {
            @Override
            protected void onSuccess(Object obj) {
                super.onSuccess(obj);
                dismissLoadDialog();
                Gson gson = new Gson();
                AppSimpleBean bean = gson.fromJson(String.valueOf(obj), AppSimpleBean.class);
                if (bean == null) {
                    ViewUtils.showToast(getApplicationContext(), "删除失败");
                    return;
                }
                if (bean.getMsg() == 1) {
                    ViewUtils.showToast(getApplicationContext(), "删除成功");
                    BusProvider.getInstance().post(new NotifyUpdateEvent());
                    finish();
                } else {
                    ViewUtils.showToast(getApplicationContext(), "删除失败");
                }
            }

            @Override
            protected void onFail() {
                super.onFail();
                dismissLoadDialog();
                ViewUtils.showToast(getApplicationContext(), "删除失败");
            }

            @Override
            protected void onTimeOut() {
                super.onTimeOut();
                dismissLoadDialog();
                ViewUtils.showToast(getApplicationContext(), "删除失败");
            }
        };

        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("id", mGoodsId);
        httpManager.post(Constants.URLS.GOODS_DELETE, paramsMap);
    }

    @Subscribe
    public void onEventFinish(FinishActivityEvent event) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        BusProvider.getInstance().unregister(this);
    }

}
