package com.meilicat.basedemo.activity.usercenter;

import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.google.gson.Gson;
import com.meilicat.basedemo.R;
import com.meilicat.basedemo.adapter.CommonAdapter;
import com.meilicat.basedemo.base.BaseTitleActivity;
import com.meilicat.basedemo.bean.MsgDetailsBean;
import com.meilicat.basedemo.bean.MsgListBean;
import com.meilicat.basedemo.conf.Constants;
import com.meilicat.basedemo.utils.HttpManager;
import com.meilicat.basedemo.utils.UIUtils;
import com.meilicat.basedemo.utils.ViewUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by user on 2016/2/22.
 */
public class MsgListActivity extends BaseTitleActivity {

    private CommonAdapter<MsgListBean.DataEntity> mAdapter;

    @Bind(R.id.msg_refresh_layout)
    MaterialRefreshLayout mRefreshLayout;
    @Bind(R.id.msg_listview)
    ListView listview;

    @Bind(R.id.msg_empty_layout)
    RelativeLayout mEmptyLayout;

    private int mCurrentPage = 0;
    private HttpManager httpListManager;
    private MsgListBean mMsgListBean;
    private HttpManager httpDetailsManager;

    private View mFooterView;

    @Override
    public void setContent() {
        super.setContent();
        setContentView(R.layout.activity_msg_list_layout);
        ButterKnife.bind(this);

        setupNavigationBar(R.id.navigation_bar);
        setCommonTitle("消息");

    }

    @Override
    public void initView() {
        super.initView();

        mRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {
                //下拉刷新...
                mCurrentPage = 0;
                listview.removeFooterView(mFooterView);
                getListDta(true);
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                //上拉刷新...
                getListDta(true);
            }
        });

        mAdapter = new CommonAdapter<MsgListBean.DataEntity>(this, R.layout.item_msg_list) {

            @Override
            protected void convert(ViewHolderEntity entity, MsgListBean.DataEntity itemData, int itemViewType) {
                TextView typeTv = entity.getView(R.id.msg_item_type_tv);
                TextView titleTv = entity.getView(R.id.msg_item_title_tv);
                TextView contentTv = entity.getView(R.id.msg_item_content_tv);
                TextView timeTv = entity.getView(R.id.msg_item_time_tv);

                int msgType = itemData.getAnnounceType();
                String typeText;
                if (msgType == 1) {
                    typeText = "【政策公告】";
                } else if (msgType == 2) {
                    typeText = "【营销公告】";
                } else { //0系统公告
                    typeText = "【系统公告】";
                }
                typeTv.setText(typeText);

                titleTv.setText(itemData.getSubject());

                if (itemData.getStatus() == null) {
                    typeTv.setTextColor(Color.parseColor("#333333"));
                    titleTv.setTextColor(Color.parseColor("#333333"));
                } else {
                    typeTv.setTextColor(Color.parseColor("#999999"));
                    titleTv.setTextColor(Color.parseColor("#999999"));
                }

                String msgContent = itemData.getContent();
                if (!TextUtils.isEmpty(msgContent)) {
                    contentTv.setText(Html.fromHtml(msgContent));
                }
                timeTv.setText(itemData.getPublishTime());
            }
        };

        mFooterView =  LayoutInflater.from(this).inflate(R.layout.common_load_finish_footer, null);
        listview.addFooterView(mFooterView);
        listview.setAdapter(mAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MsgListBean.DataEntity itemData = mAdapter.getItem(position);
                if (itemData == null) {
                    return;
                }
                itemData.setStatus("0");
                mAdapter.notifyDataSetChanged();
                getDetailData(itemData.getId());
            }
        });

        listview.removeFooterView(mFooterView);

        getListDta(false);

    }

    private void getDetailData(String id) {
        if (TextUtils.isEmpty(id)) {
            return;
        }
        showLoadDialog();

        if (httpDetailsManager == null) {
            httpDetailsManager = new HttpManager(UIUtils.getContext()) {
                @Override
                protected void onSuccess(Object obj) {
                    super.onSuccess(obj);
                    dismissLoadDialog();

                    Gson gson = new Gson();
                    MsgDetailsBean msgDetailsData = null;
                    try {
                        msgDetailsData = gson.fromJson((String) obj, MsgDetailsBean.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (msgDetailsData == null) {
                        ViewUtils.showToast(getApplicationContext(), "获取详情失败");
                        return;
                    }

                    if (mMsgListBean.getMsg() == 1) {
                        Intent intent = new Intent(MsgListActivity.this, WebviewActivity.class);
                        intent.putExtra(WebviewActivity.KEY_TITLE, "消息详情");

                        String tempTitle = "<h3>"+msgDetailsData.getData().getSubject()+"</h3>";
                        StringBuffer sb = new StringBuffer(tempTitle);
                        sb.append("<p style=\"color: #999999; size: 10px\">"+msgDetailsData.getData().getPublishTime()+"</p>");
                        sb.append(msgDetailsData.getData().getContent());


                        String newHtmlText = fitContentInWebView(sb.toString());
                        intent.putExtra(WebviewActivity.KEY_URL, newHtmlText);
                        startActivity(intent);
                    } else {
                        ViewUtils.showToast(getApplicationContext(), "获取详情失败");
                    }

                }

                @Override
                protected void onFail() {
                    super.onFail();
                    dismissLoadDialog();
                }

                @Override
                protected void onTimeOut() {
                    super.onTimeOut();
                    dismissLoadDialog();
                }
            };
        }

        httpDetailsManager.get(Constants.URLS.MSG_DETAIL + "id=" + id);
    }


    private static final String REGULAR_EXPRESSION_IMAGE_TAG="<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>";

    /**
     * 调整webview中文字和图片的大小
     * */
    public static String fitContentInWebView(String content){
        return "<body style=\"word-break:break-all;\">"+ fitImageInWebView(content)+"</body>";
    }

    /**
     * 调整webView中图片的大小
     * @param  content 内容
     * */
    public static String fitImageInWebView(String content){
        if(TextUtils.isEmpty(content)){
            return content;
        }
        Pattern imagePattern = Pattern.compile(REGULAR_EXPRESSION_IMAGE_TAG);
        Matcher imageMatcher = imagePattern.matcher(content);
        while(imageMatcher.find()){
            StringBuilder buf=new StringBuilder(imageMatcher.group());
            int styleIndex=imageMatcher.group().indexOf("img")+4;
            buf.insert(styleIndex, "style=\"max-width:100%;\" ");
            content=content.replace(imageMatcher.group(), buf.toString());
        }
        return content;
    }

    private void refreshUIData() {
        if (mMsgListBean == null) {
            if (mCurrentPage == 0) {
                mEmptyLayout.setVisibility(View.VISIBLE);
            }
            return;
        }

        List<MsgListBean.DataEntity> listItems = mMsgListBean.getData();
        if (listItems == null || listItems.isEmpty()) {
            if (mCurrentPage == 0) {
                mEmptyLayout.setVisibility(View.VISIBLE);
            }
            return;
        }

        if (listItems.size() < 20) {
            listview.addFooterView(mFooterView);
            mRefreshLayout.setLoadMore(false);
        } else {
            listview.removeFooterView(mFooterView);
            mCurrentPage++;
            mRefreshLayout.setLoadMore(true);
        }

        if (mCurrentPage == 0) {
            mAdapter.setData(listItems);
        } else {
            mAdapter.addAll(listItems);
        }
    }

    private void getListDta(boolean isLoadMore) {
        if (!isLoadMore) {
            showLoadDialog();
        }

        if (httpListManager == null) {
            httpListManager = new HttpManager(UIUtils.getContext()) {
                @Override
                protected void onSuccess(Object obj) {
                    super.onSuccess(obj);
                    dismissLoadDialog();
                    // 结束下拉刷新...
                    mRefreshLayout.finishRefresh();
                    // 结束上拉刷新...
                    mRefreshLayout.finishRefreshLoadMore();
                    Gson gson = new Gson();
                    try{
                        mMsgListBean = gson.fromJson(String.valueOf(obj), MsgListBean.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (mMsgListBean == null) {
                        if (mCurrentPage == 0) {
                            mEmptyLayout.setVisibility(View.VISIBLE);
                        }
                        ViewUtils.showToast(getApplicationContext(), "获取列表数据失败");
                        return;
                    }

                    if (mMsgListBean.getMsg() == 1) {
                        refreshUIData();
                    } else {
                        if (mCurrentPage == 0) {
                            mEmptyLayout.setVisibility(View.VISIBLE);
                        }
                        ViewUtils.showToast(getApplicationContext(), "获取列表数据失败");
                    }
                }

                @Override
                protected void onFail() {
                    super.onFail();
                    dismissLoadDialog();

                    if (mCurrentPage == 0) {
                        mEmptyLayout.setVisibility(View.VISIBLE);
                    }

                    // 结束下拉刷新...
                    mRefreshLayout.finishRefresh();
                    // 结束上拉刷新...
                    mRefreshLayout.finishRefreshLoadMore();
                    ViewUtils.showToast(getApplicationContext(), "获取列表数据失败");
                }

                @Override
                protected void onTimeOut() {
                    super.onTimeOut();
                    dismissLoadDialog();

                    if (mCurrentPage == 0) {
                        mEmptyLayout.setVisibility(View.VISIBLE);
                    }

                    // 结束下拉刷新...
                    mRefreshLayout.finishRefresh();
                    // 结束上拉刷新...
                    mRefreshLayout.finishRefreshLoadMore();
                    ViewUtils.showToast(getApplicationContext(), "获取列表数据失败");
                }
            };
        }

        httpListManager.get(Constants.URLS.MSG_LIST + "page=" + mCurrentPage);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
