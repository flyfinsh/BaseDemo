package com.meilicat.basedemo.activity.commonditymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.meilicat.basedemo.R;
import com.meilicat.basedemo.adapter.CommonAdapter;
import com.meilicat.basedemo.base.BaseTitleActivity;
import com.meilicat.basedemo.bean.NormSelItemData;
import com.meilicat.basedemo.bean.goods.GoodsColorBean;
import com.meilicat.basedemo.bean.goods.GoodsSizeBean;
import com.meilicat.basedemo.conf.Constants;
import com.meilicat.basedemo.utils.HttpManager;
import com.meilicat.basedemo.utils.UIUtils;
import com.meilicat.basedemo.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by user on 2016/2/23.
 */
public class GoodsNormsActivity extends BaseTitleActivity {

    public static final String KEY_SEL_NORM_LIST = "key_sel_norm_list";

    @Bind(R.id.norms_color_listview)
    ListView mColorListview;
    @Bind(R.id.norms_size_listview)
    ListView mSizeListview;
    @Bind(R.id.norms_sel_num_tv)
    TextView mSelNumTv;
    @Bind(R.id.norms_sel_listview)
    ListView mSelListview;

    private CommonAdapter<GoodsColorBean.DataEntity> mColorAdapter;
    private List<GoodsColorBean.DataEntity> mColorList;
    private CommonAdapter<GoodsSizeBean.DataEntity> mSizeAdapter;
    private List<GoodsSizeBean.DataEntity> mSizeList;

    private CommonAdapter<NormSelItemData> mSelAdapter;
    private ArrayList<NormSelItemData> mSelDatas;

    private boolean isNeedShowSeled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_norms_layout);
        ButterKnife.bind(this);

        setupNavigationBar(R.id.navigation_bar);
        setCommonTitle("商品规格");
        addRightButtonText("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSelDatas == null || mSelDatas.isEmpty()) {
                    ViewUtils.showToast(getApplicationContext(), "请至少选择一种规格");
                    return;
                }

                Intent normIntent = new Intent();
                normIntent.putParcelableArrayListExtra(KEY_SEL_NORM_LIST, mSelDatas);
                setResult(RESULT_OK, normIntent);
                finish();
            }
        });

        mSelDatas = getIntent().getExtras().getParcelableArrayList(KEY_SEL_NORM_LIST);
        if(mSelDatas == null) {
            isNeedShowSeled = false;
            mSelDatas = new ArrayList<>();
        }
        initCurrentView();

    }

    private void initCurrentView() {
        mColorAdapter = new CommonAdapter<GoodsColorBean.DataEntity>(GoodsNormsActivity.this, R.layout.item_norms_colorsize) {
            @Override
            protected void convert(ViewHolderEntity vh, GoodsColorBean.DataEntity itemData, int itemViewType) {
                TextView nameTv = vh.getView(R.id.norms_colorsize_tv);
                nameTv.setText(itemData.getCn());
                ImageView selIv = vh.getView(R.id.norms_colorsize_sel_iv);

                if (itemData.isSelected) {
                    selIv.setVisibility(View.VISIBLE);
                } else {
                    selIv.setVisibility(View.GONE);
                }
            }
        };
        mColorListview.setAdapter(mColorAdapter);

        mColorListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GoodsColorBean.DataEntity itemData = mColorAdapter.getItem(position);
                if (itemData.isSelected) {
                    itemData.isSelected = false;
                } else {
                    itemData.isSelected = true;
                }
                mColorAdapter.notifyDataSetChanged();
                if (itemData.isSelected) {
                    checkSelState();
                } else {
                    deleteContains(itemData.getCn(), true);
                }
            }
        });

        mSizeAdapter = new CommonAdapter<GoodsSizeBean.DataEntity>(GoodsNormsActivity.this, R.layout.item_norms_colorsize) {
            @Override
            protected void convert(ViewHolderEntity vh, GoodsSizeBean.DataEntity itemData, int itemViewType) {
                View iconIv = vh.getView(R.id.norms_colorsize_icon);
                iconIv.setVisibility(View.GONE);

                TextView nameTv = vh.getView(R.id.norms_colorsize_tv);
                nameTv.setText(itemData.getName());
                ImageView selIv = vh.getView(R.id.norms_colorsize_sel_iv);

                if (itemData.isSelected) {
                    selIv.setVisibility(View.VISIBLE);
                } else {
                    selIv.setVisibility(View.GONE);
                }
            }
        };
        mSizeListview.setAdapter(mSizeAdapter);

        mSizeListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GoodsSizeBean.DataEntity itemData = mSizeAdapter.getItem(position);
                if (itemData.isSelected) {
                    itemData.isSelected = false;
                } else {
                    itemData.isSelected = true;
                }
                mSizeAdapter.notifyDataSetChanged();
                if (itemData.isSelected) {
                    checkSelState();
                } else {
                    deleteContains(itemData.getName(), false);
                }
            }
        });

        mSelAdapter = new CommonAdapter<NormSelItemData>(GoodsNormsActivity.this, R.layout.item_norms_sel) {
            @Override
            protected void convert(ViewHolderEntity vh, final NormSelItemData itemData, int itemViewType) {
                TextView valueTv = vh.getView(R.id.norms_sel_value_tv);
                valueTv.setText(itemData.filterName);

                RelativeLayout delLayout = vh.getView(R.id.norms_sel_right_layout);
                delLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSelDatas.remove(itemData);
                        if (mSelDatas.isEmpty()) {
                            notifyOtherAdapter();
                        }
                        mSelNumTv.setText("已选（" + mSelDatas.size() + "）");
                        notifyDataSetChanged();
                    }
                });
            }
        };
        mSelListview.setAdapter(mSelAdapter);

        getColorList();
    }

    private void getColorList() {
        showLoadDialog();

        HttpManager httpManager = new HttpManager(UIUtils.getContext()) {
            @Override
            protected void onSuccess(Object obj) {
                super.onSuccess(obj);

                Gson gson = new Gson();
                GoodsColorBean colorBean = null;
                try{
                    colorBean = gson.fromJson(String.valueOf(obj), GoodsColorBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(colorBean == null) {
                    dismissLoadDialog();
                    ViewUtils.showToast(getApplicationContext(), "无法获取规格数据");
                    return;
                }

                if(colorBean.getMsg() ==1) {
                    mColorList = colorBean.getData();
                    mColorAdapter.setData(mColorList);
                    getSizeList();
                } else {
                    dismissLoadDialog();
                    ViewUtils.showToast(getApplicationContext(), "无法获取规格数据");
                }
            }

            @Override
            protected void onFail() {
                super.onFail();
                dismissLoadDialog();
                ViewUtils.showToast(getApplicationContext(), "无法获取规格数据");
            }

            @Override
            protected void onTimeOut() {
                super.onTimeOut();
                dismissLoadDialog();
                ViewUtils.showToast(getApplicationContext(), "无法获取规格数据");
            }
        };

        httpManager.get(Constants.URLS.GOODS_COLOR_LIST);
    }

    private void getSizeList() {
        HttpManager httpManager = new HttpManager(UIUtils.getContext()) {
            @Override
            protected void onSuccess(Object obj) {
                super.onSuccess(obj);
                dismissLoadDialog();
                Gson gson = new Gson();
                GoodsSizeBean sizeBean = null;
                try{
                    sizeBean = gson.fromJson(String.valueOf(obj), GoodsSizeBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(sizeBean == null) {
                    dismissLoadDialog();
                    ViewUtils.showToast(getApplicationContext(), "无法获取规格数据");
                    return;
                }

                if(sizeBean.getMsg() ==1) {
                    mSizeList = sizeBean.getData();
                    mSizeAdapter.setData(mSizeList);
                    if(isNeedShowSeled) {
                        updateSelUI();
                    }
                } else {
                    dismissLoadDialog();
                    ViewUtils.showToast(getApplicationContext(), "无法获取规格数据");
                }
            }

            @Override
            protected void onFail() {
                super.onFail();
                dismissLoadDialog();
                ViewUtils.showToast(getApplicationContext(), "无法获取规格数据");
            }

            @Override
            protected void onTimeOut() {
                super.onTimeOut();
                dismissLoadDialog();
                ViewUtils.showToast(getApplicationContext(), "无法获取规格数据");
            }
        };

        httpManager.get(Constants.URLS.GOODS_SIZE_LIST);
    }

    /**
     * 显示已选历史
     */
    private void updateSelUI() {
        if(mSelDatas == null || mSelDatas.isEmpty()) {
            return;
        }
        mSelNumTv.setText("已选（" + mSelDatas.size() + "）");
        mSelAdapter.setData(mSelDatas);

        for(NormSelItemData selItem : mSelDatas) {
            String colorName = selItem.colorName;
            for(GoodsColorBean.DataEntity colorData : mColorList) {
                if (colorName.equals(colorData.getCn())) {
                    colorData.isSelected = true;
                }
            }
        }

        for(NormSelItemData selItem : mSelDatas) {
            String sizeName = selItem.sizeName;
            for(GoodsSizeBean.DataEntity sizeData : mSizeList) {
                if (sizeName.equals(sizeData.getName())) {
                    sizeData.isSelected = true;
                }
            }
        }

        mColorAdapter.notifyDataSetChanged();
        mSizeAdapter.notifyDataSetChanged();
    }

    /**
     * 选择列表清空，颜色、尺码列表选中状态初始化
     */
    private void notifyOtherAdapter() {
        for (GoodsColorBean.DataEntity colorItem : mColorList) {
            colorItem.isSelected = false;
        }
        for (GoodsSizeBean.DataEntity sizeItem : mSizeList) {
            sizeItem.isSelected = false;
        }

        mColorAdapter.notifyDataSetChanged();
        mSizeAdapter.notifyDataSetChanged();
    }



    /**
     * 选中尺码、颜色、更新列表
     */
    private void checkSelState() {
        List<GoodsColorBean.DataEntity> tempColors = mColorAdapter.getData();
        List<GoodsSizeBean.DataEntity> tempSizes = mSizeAdapter.getData();

        for (GoodsColorBean.DataEntity colorData : tempColors) {
            if (colorData.isSelected) { //添加
                for (GoodsSizeBean.DataEntity sizeData : tempSizes) {
                    if (sizeData.isSelected) { //添加
                        String selText = colorData.getCn() + " " + sizeData.getName();
                        if (!isHave(selText)) {
                            NormSelItemData selData = new NormSelItemData();
                            selData.filterName = selText;
                            selData.colorName = colorData.getCn();
                            selData.colorAbbrName = colorData.getAbbr();
                            selData.sizeName = sizeData.getName();
                            selData.sizeAbbrName = sizeData.getAbbr();
                            mSelDatas.add(selData);
                        }
                    }
                }
            }
        }

        mSelNumTv.setText("已选（" + mSelDatas.size() + "）");
        mSelAdapter.setData(mSelDatas);
    }

    /**
     * 删除已有的选择
     *
     * @param filterName
     */
    private void deleteContains(String filterName, boolean isColor) {
        if (!mSelDatas.isEmpty()) {
            List<NormSelItemData> tempSelList = new ArrayList<>();
            tempSelList.addAll(mSelDatas);

            for (NormSelItemData selItemData : tempSelList) {
                if (isColor) {
                    if (selItemData.colorName.equals(filterName)) {
                        mSelDatas.remove(selItemData);
                    }
                } else {
                    if (selItemData.sizeName.contains(filterName)) {
                        mSelDatas.remove(selItemData);
                    }
                }
            }
            tempSelList.clear();
        }

        mSelNumTv.setText("已选（" + mSelDatas.size() + "）");
        mSelAdapter.notifyDataSetChanged();
    }

    private boolean isHave(String selText) {
        for (NormSelItemData selData : mSelDatas) {
            if (selText.equals(selData.filterName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
