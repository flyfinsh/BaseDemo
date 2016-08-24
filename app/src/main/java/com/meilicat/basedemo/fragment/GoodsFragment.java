package com.meilicat.basedemo.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.meilicat.basedemo.R;
import com.meilicat.basedemo.bean.GoodsCountBean;
import com.meilicat.basedemo.conf.Constants;
import com.meilicat.basedemo.event.BusProvider;
import com.meilicat.basedemo.event.NotifyUpdateHomeEvent;
import com.meilicat.basedemo.fragment.commodity.CommodityManagerFragment;
import com.meilicat.basedemo.utils.DeviceConfiger;
import com.meilicat.basedemo.utils.HttpManager;
import com.meilicat.basedemo.utils.RadioTabManager;
import com.meilicat.basedemo.utils.UIUtils;
import com.meilicat.basedemo.utils.ViewUtils;

import org.greenrobot.eventbus.Subscribe;

/**
 * Created by cj on 2016/1/23.
 */
@SuppressWarnings("deprecation")
public class GoodsFragment extends Fragment implements RadioTabManager.BuildViewFactory {

    private RadioTabManager mManager;
    private RadioGroup mTabContain;

    private TextView mAlCountTv;
    private TextView mWaitCountTv;
    private TextView mDownCountTv;

    private boolean isHaveRegister;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!isHaveRegister) {
            BusProvider.getInstance().register(this);
            isHaveRegister = true;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_goods, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initView();

        mAlCountTv = (TextView) getView().findViewById(R.id.goods_al_count_tv);
        mWaitCountTv = (TextView) getView().findViewById(R.id.goods_wait_count_tv);
        mDownCountTv = (TextView) getView().findViewById(R.id.goods_down_count_tv);

        getCategoryCount();
    }

    /**
     * 获取不同列表的数目
     */
    private void getCategoryCount() {
        HttpManager httpManager = new HttpManager(UIUtils.getContext()) {
            @Override
            protected void onSuccess(Object obj) {
                super.onSuccess(obj);
                Gson gson = new Gson();
                GoodsCountBean bean = null;
                try {
                    bean = gson.fromJson(String.valueOf(obj), GoodsCountBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (bean == null || bean.getData() == null) {
                    return;
                }

                GoodsCountBean.DataEntity countData = bean.getData();
                mAlCountTv.setText(countData.getUpOnlineCount() + "");
                mWaitCountTv.setText(countData.getWaitOnlineCount() + "");
                mDownCountTv.setText(countData.getDownOnlineCount() + "");
            }

            @Override
            protected void onFail() {
                super.onFail();
            }
        };

        httpManager.get(Constants.URLS.GOODS_MANAGER_COUNT);
    }

    private void initView() {
        mTabContain = (RadioGroup) getView().findViewById(R.id.commodity_tab_contain);
        mManager = new RadioTabManager(getActivity(), getChildFragmentManager(), mTabContain, R.id.commodity_stub_layout, this);
        Bundle args = new Bundle();
        args.putString(CommodityManagerFragment.REQUEST_TYPE, CommodityManagerFragment.TYPE_PUTAWAY_ALREADY);
        mManager.addTab(CommodityManagerFragment.TYPE_PUTAWAY_ALREADY, CommodityManagerFragment.class, args);
        args = new Bundle();
        args.putString(CommodityManagerFragment.REQUEST_TYPE, CommodityManagerFragment.TYPE_PUTAWAY_WAIT);
        mManager.addTab(CommodityManagerFragment.TYPE_PUTAWAY_WAIT, CommodityManagerFragment.class, args);
        args = new Bundle();
        args.putString(CommodityManagerFragment.REQUEST_TYPE,
                CommodityManagerFragment.TYPE_SOLD_OUT);
        mManager.addTab(CommodityManagerFragment.TYPE_SOLD_OUT, CommodityManagerFragment.class,
                args);
        mManager.setCurrFragmentByTag(CommodityManagerFragment.TYPE_PUTAWAY_ALREADY);
    }

    @Override
    public View tabView(String tag, ViewGroup parent) {
        RadioButton tabView = newRadioButton();
        if (CommodityManagerFragment.TYPE_PUTAWAY_ALREADY.equals(tag)) {
            tabView.setText("已上架");
        } else if (CommodityManagerFragment.TYPE_PUTAWAY_WAIT.equals(tag)) {
            tabView.setText("待上架");
//            appendDividerView(parent);
        } else if (CommodityManagerFragment.TYPE_SOLD_OUT.equals(tag)) {
            tabView.setText("已下架");
//            appendDividerView(parent);
        }
        return tabView;
    }

    private RadioButton newRadioButton() {
        RadioButton btn = new RadioButton(getActivity());
        btn.setButtonDrawable(new BitmapDrawable());
        btn.setBackgroundDrawable(ViewUtils.createTabItemDividerBg(Color.parseColor("#ff0099")));
        btn.setGravity(Gravity.CENTER_HORIZONTAL);
        btn.setPadding(0, DeviceConfiger.dp2px(5), 0, 0);
        btn.setTextColor(ViewUtils.createTabItemTextColor(Color.parseColor("#333333"), Color.parseColor("#ff0099")));
        return btn;
    }

    private void appendDividerView(ViewGroup parent) {
        View div = new View(getActivity());
        div.setBackgroundResource(R.color.gray_line);
        parent.addView(div, 2, getResources().getDimensionPixelSize(R.dimen.tab_div_h));
    }

    @Subscribe
    public void onEventHomeNotify(NotifyUpdateHomeEvent event) {
        getCategoryCount();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
        isHaveRegister = false;
    }
}
