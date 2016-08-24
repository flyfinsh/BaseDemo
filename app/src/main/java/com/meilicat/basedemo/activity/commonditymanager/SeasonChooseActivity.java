package com.meilicat.basedemo.activity.commonditymanager;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.meilicat.basedemo.R;
import com.meilicat.basedemo.base.BaseTitleActivity;
import com.meilicat.basedemo.bean.goods.GoodsCategoryBean;
import com.meilicat.basedemo.bean.goods.GoodsCategoryData;
import com.meilicat.basedemo.bean.goods.GoodsSeasonBean;
import com.meilicat.basedemo.bean.goods.GoodsSeasonData;
import com.meilicat.basedemo.conf.Constants;
import com.meilicat.basedemo.event.BusProvider;
import com.meilicat.basedemo.event.SelectFinlaIdEvent;
import com.meilicat.basedemo.event.SelectTypeEvent;
import com.meilicat.basedemo.utils.HttpManager;
import com.meilicat.basedemo.utils.UIUtils;
import com.meilicat.basedemo.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by user on 2016/2/18.
 */
public class SeasonChooseActivity extends BaseTitleActivity {

    private final String KEY_APPEND_NAME = "key_append_name";
    private final String KEY_APPEND_ID = "key_append_id";
    private String mAppendName;
    private String mAppendIds;

    public static final String KEY_IS_TYPE = "key_is_type";
    private boolean isType;

    private final String KEY_DATA = "key_data";
    private List<GoodsCategoryData> mChildList;

    private CategoryAdapter mCategoryAdapter;
    private GoodsCategoryBean mCategoryBean;

    public static final String KEY_SEASON_TEXT = "key_season_text";
    private String mSeasonText;
    private SeasonAdapter mSeasonAdapter;

    @Bind(R.id.single_listview)
    ListView mListview;

//    private boolean isSelFirst = true; //点击第一子列表条目，返回id

    @Override
    public void setContent() {
        super.setContent();
        setContentView(R.layout.app_single_common_listview_layout);
        ButterKnife.bind(this);
    }

    @Override
    public void initView() {
        super.initView();
        setupNavigationBar(R.id.navigation_bar);

        isType = getIntent().getBooleanExtra(KEY_IS_TYPE, false);
        if(isType) {
            setCommonTitle("分类");
            mChildList = getIntent().getExtras().getParcelableArrayList(KEY_DATA);
            mCategoryAdapter = new CategoryAdapter();
            mListview.setAdapter(mCategoryAdapter);

            mAppendName = getIntent().getStringExtra(KEY_APPEND_NAME);
            mAppendIds = getIntent().getStringExtra(KEY_APPEND_ID);

        } else {
            setCommonTitle("季节");

            mSeasonText = getIntent().getExtras().getString(KEY_SEASON_TEXT);
            mSeasonAdapter = new SeasonAdapter();
            mListview.setAdapter(mSeasonAdapter);
        }



        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(isType) {
                    GoodsCategoryData itemData = mCategoryAdapter.getItem(position);
                    ArrayList<GoodsCategoryData> childItems = itemData.getChildren();

                    if (childItems == null || childItems.isEmpty()) {
                        StringBuffer sbName = new StringBuffer();
                        if(TextUtils.isEmpty(mAppendName)) {
                            sbName.append(itemData.getCn());
                        } else {
                            sbName.append(mAppendName);
                            sbName.append(itemData.getCn());
                        }

                        StringBuffer sbId = new StringBuffer();
                        if(TextUtils.isEmpty(mAppendIds)) {
                            sbId.append(itemData.getId());
                        } else {
                            sbId.append(mAppendIds);
                            sbId.append(itemData.getId());
                        }

                        /**
                         * 商品类型最后一层id、选择的所有id
                         */
                        BusProvider.getInstance().post(new SelectFinlaIdEvent(itemData.getId()));
                        BusProvider.getInstance().post(new SelectTypeEvent(sbId.toString(), sbName.toString()));
                        finish();
                    } else {
                        Intent newIntent = new Intent(getApplicationContext(), SeasonChooseActivity.class);
                        newIntent.putParcelableArrayListExtra(KEY_DATA, childItems);
                        newIntent.putExtra(KEY_IS_TYPE, true);

                        StringBuffer sbName = new StringBuffer();
                        if(TextUtils.isEmpty(mAppendName)) {
                            sbName.append(itemData.getCn()+"/");
                        } else {
                            sbName.append(mAppendName);
                            sbName.append(itemData.getCn()+"/");
                        }
                        newIntent.putExtra(KEY_APPEND_NAME, sbName.toString());

                        StringBuffer sbId = new StringBuffer();
                        if(TextUtils.isEmpty(mAppendIds)) {
                            sbId.append(itemData.getId()+",");
                        } else {
                            sbId.append(mAppendIds);
                            sbId.append(itemData.getId()+",");
                        }
                        newIntent.putExtra(KEY_APPEND_ID, sbId.toString());

                        startActivity(newIntent);
                        finish();
                    }

                }else {
                    GoodsSeasonData seasonData = mSeasonAdapter.getItem(position);
                    Intent seasonResult = new Intent();
                    seasonResult.putExtra(KEY_IS_TYPE, seasonData.getId());
                    seasonResult.putExtra(KEY_SEASON_TEXT, seasonData.getSy());
                    setResult(RESULT_OK, seasonResult);
                    finish();
                }
            }
        });

        if(isType) {
            if (mChildList != null && !mChildList.isEmpty()) {
                mCategoryAdapter.setDataChanged(mChildList);
            } else {
                getCategoryList();
            }
        }else {
            getSeasonList();
        }
    }

    private void getSeasonList() {
        showLoadDialog();
        HttpManager httpManager = new HttpManager(UIUtils.getContext()) {
            @Override
            protected void onSuccess(Object obj) {
                super.onSuccess(obj);
                dismissLoadDialog();
                Gson gson = new Gson();
                GoodsSeasonBean seasonBean = null;
                try{
                    seasonBean = gson.fromJson(String.valueOf(obj), GoodsSeasonBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(seasonBean == null) {
                    ViewUtils.showToast(getApplicationContext(), "没有数据");
                    return;
                }

                if(seasonBean.getMsg() == 1) {
                    List<GoodsSeasonData> listDatas = seasonBean.getData();
                    if(listDatas != null && !listDatas.isEmpty()) {
                        mSeasonAdapter.setDataChanged(listDatas);
                    }
                } else {
                    ViewUtils.showToast(getApplicationContext(), "获取失败");
                }
            }

            @Override
            protected void onFail() {
                super.onFail();
                dismissLoadDialog();
                ViewUtils.showToast(getApplicationContext(), "获取失败");
            }

            @Override
            protected void onTimeOut() {
                super.onTimeOut();
                dismissLoadDialog();
                ViewUtils.showToast(getApplicationContext(), "获取失败");
            }
        };

        httpManager.get(Constants.URLS.GOODS_SEASON_LIST);
    }

    private void getCategoryList() {
        showLoadDialog();

        HttpManager httpManager = new HttpManager(UIUtils.getContext()) {
            @Override
            protected void onSuccess(Object obj) {
                super.onSuccess(obj);
                dismissLoadDialog();
                Gson gson = new Gson();
                try{
                    mCategoryBean = gson.fromJson(String.valueOf(obj), GoodsCategoryBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(mCategoryBean == null) {
                    ViewUtils.showToast(getApplicationContext(), "没有数据");
                    return;
                }

                if(mCategoryBean.getMsg() == 1) {
                    List<GoodsCategoryData> listDatas = mCategoryBean.getData();
                    if(listDatas != null && !listDatas.isEmpty()) {
                        GoodsCategoryData rootData = listDatas.get(0);
                        String id = rootData.getId();
                        if("1".equals(id)) {
                            List<GoodsCategoryData> childList = rootData.getChildren();
                            if(childList!= null && !childList.isEmpty()) {
                                mCategoryAdapter.setDataChanged(childList);
                            }
                        } else {
                            mCategoryAdapter.setDataChanged(listDatas);
                        }

                    }
                } else {
                    ViewUtils.showToast(getApplicationContext(), "获取失败");
                }
            }

            @Override
            protected void onFail() {
                super.onFail();
                dismissLoadDialog();
                ViewUtils.showToast(getApplicationContext(), "获取失败");
            }

            @Override
            protected void onTimeOut() {
                super.onTimeOut();
                dismissLoadDialog();
                ViewUtils.showToast(getApplicationContext(), "获取失败");
            }
        };

        httpManager.get(Constants.URLS.GOODS_CATEGORY_LIST);
    }

    class CategoryAdapter extends BaseAdapter {

        private List<GoodsCategoryData> mItems;

        public void setDataChanged(List<GoodsCategoryData> listData) {
            this.mItems = listData;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if(mItems == null || mItems.isEmpty()) {
                return 0;
            }
            return mItems.size();
        }

        @Override
        public GoodsCategoryData getItem(int position) {
            if(mItems == null || mItems.isEmpty()) {
                return null;
            }
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_filter_common_layout, null);
                holder = new ViewHolder();

                holder.nameTv = (TextView) convertView.findViewById(R.id.item_filter_name_tv);
                holder.arrowIv = (ImageView) convertView.findViewById(R.id.item_filter_right_iv);
                holder.selIv = (ImageView) convertView.findViewById(R.id.item_filter_sel_iv);

                holder.arrowIv.setVisibility(View.VISIBLE);
                holder.selIv.setVisibility(View.GONE);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if(mItems != null && !mItems.isEmpty()) {
                GoodsCategoryData itemData = mItems.get(position);
                if(itemData != null) {
                    holder.nameTv.setText(itemData.getCn());

                    List<GoodsCategoryData> childListData = itemData.getChildren();
                    if(childListData == null || childListData.isEmpty()) {
                        holder.arrowIv.setVisibility(View.GONE);
                    } else {
                        holder.arrowIv.setVisibility(View.VISIBLE);
                    }
                }
            }

            return convertView;
        }

        class ViewHolder {
            TextView nameTv;
            ImageView arrowIv;
            ImageView selIv;
        }
    }

    class SeasonAdapter extends BaseAdapter {

        private List<GoodsSeasonData> mItems;

        public void setDataChanged(List<GoodsSeasonData> listData) {
            this.mItems = listData;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if(mItems == null || mItems.isEmpty()) {
                return 0;
            }
            return mItems.size();
        }

        @Override
        public GoodsSeasonData getItem(int position) {
            if(mItems == null || mItems.isEmpty()) {
                return null;
            }
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_filter_common_layout, null);
                holder = new ViewHolder();

                holder.nameTv = (TextView) convertView.findViewById(R.id.item_filter_name_tv);
                holder.arrowIv = (ImageView) convertView.findViewById(R.id.item_filter_right_iv);
                holder.selIv = (ImageView) convertView.findViewById(R.id.item_filter_sel_iv);

                holder.arrowIv.setVisibility(View.GONE);
                holder.selIv.setVisibility(View.GONE);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if(mItems != null && !mItems.isEmpty()) {
                GoodsSeasonData itemData = mItems.get(position);
                if(itemData != null) {

                    String syText = itemData.getSy();
                    if(syText.equals(mSeasonText)) {
                        holder.selIv.setVisibility(View.VISIBLE);
                    } else {
                        holder.selIv.setVisibility(View.GONE);
                    }

                    holder.nameTv.setText(syText);
                }
            }

            return convertView;
        }

        class ViewHolder {
            TextView nameTv;
            ImageView arrowIv;
            ImageView selIv;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
