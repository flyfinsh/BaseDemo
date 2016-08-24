package com.meilicat.basedemo.activity.usercenter;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.meilicat.basedemo.R;
import com.meilicat.basedemo.base.BaseTitleActivity;
import com.meilicat.basedemo.utils.ViewUtils;


/**
 * Created by lizhiming on 2015/10/15.
 */
public class WebviewActivity extends BaseTitleActivity {
    public static final String KEY_TITLE = "key_title";
    public static final String KEY_URL = "url";

    private String mUrl = "";
    private WebView mWebview;

    private boolean isMsgDetails; //是否是消息详情、消息详情加载html代码

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_webview_layout);
        setupNavigationBar(R.id.navigation_bar);

        Bundle bundle = getIntent().getExtras();

        String title = bundle.getString(KEY_TITLE);
        if("消息详情".equals(title)) {
            isMsgDetails = true;
        }
        if (TextUtils.isEmpty(title)) {
            title = "美丽猫";
        }
        setCommonTitle(title);

        mUrl = bundle.getString(KEY_URL);
        init();
    }

    private void init() {
        mWebview = (WebView) findViewById(R.id.app_webview);

        if (TextUtils.isEmpty(mUrl)) {
            ViewUtils.showToast(getApplicationContext(), "URL为空");
            return;
        }

        WebSettings settings = mWebview.getSettings();
        settings.setJavaScriptEnabled(true);

        // 设置可以访问文件
        settings.setAllowFileAccess(true);
//        //如果访问的页面中有Javascript，则webview必须设置支持Javascript
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setAppCacheEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);

        if(isMsgDetails) {
            Log.e("lzm", "mUrl="+mUrl);

            mWebview.loadDataWithBaseURL(null, mUrl, "text/html", "utf-8", null);
        } else {
            mWebview.loadUrl(mUrl);
        }


        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                showLoadDialog();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                dismissLoadDialog();
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissLoadDialog();
    }
}
