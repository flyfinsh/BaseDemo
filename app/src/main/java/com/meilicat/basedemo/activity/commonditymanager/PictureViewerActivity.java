package com.meilicat.basedemo.activity.commonditymanager;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.TextView;

import com.meilicat.basedemo.R;
import com.meilicat.basedemo.adapter.PicViewerAdapter;
import com.meilicat.basedemo.base.BaseApplication;
import com.meilicat.basedemo.base.BaseTitleActivity;
import com.meilicat.basedemo.view.photoview.PhotoView;
import com.meilicat.basedemo.view.photoview.PhotoViewAttacher;

import java.util.List;

public class PictureViewerActivity extends BaseTitleActivity {

    public static final String EXTRA_KEY_MEDIA_URLS = "list_media_url";
    public static final String EXTRA_KEY_DEFULT_INDEX = "defult_index";

    private ViewPager mViewPager;
    private TextView mPageNumTv;
    private View[] mImageViews;
    private int defultIndex = 0;
    private List<String> urlList;
    private PicViewerAdapter mAdapter;

    static final int PICTURE = 0;


    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.picture_look_layout);
        mViewPager = (ViewPager) findViewById(R.id.goods_pic_viewpager);
        mPageNumTv = (TextView) findViewById(R.id.picture_page_num_tv);

        mViewPager.setOnPageChangeListener(new OnPageChangeListener()
        {

            @Override
            public void onPageSelected(int arg0) {
                currentIndex = arg0;
                setPageNumInfo(currentIndex);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        setViewData();
    }

    private void setPageNumInfo(int index) {
        mPageNumTv.setText((index + 1) + "/" + mImageViews.length);
    }

    private void setViewData() {
        Bundle bundle = getIntent().getExtras();
        defultIndex = bundle.getInt(EXTRA_KEY_DEFULT_INDEX);
        currentIndex = defultIndex;
        urlList = bundle.getStringArrayList(EXTRA_KEY_MEDIA_URLS);

        mImageViews = new View[urlList.size()];

        for (int i = 0; i < mImageViews.length; i++) {
            int type = getItemType("");
            mImageViews[i] = getItemView(i, urlList.get(i), type);
        }

        mAdapter = new PicViewerAdapter(mImageViews);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(defultIndex);
        setPageNumInfo(currentIndex);
    }

    private int getItemType(String mediaType) {
            return PICTURE;
    }

    private View getItemView(final int index, String path, int type) {
        PhotoView view = new PhotoView(this);
        switch (type) {
            case PICTURE:
                BaseApplication.imageLoader.displayImage(path, view, BaseApplication.options);
                 view.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {

                     @Override
                     public void onPhotoTap(View view, float x, float y) {
                         finish();
                     }
                 });

                view.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
                    @Override
                    public void onViewTap(View view, float x, float y) {
                        finish();
                    }
                });
                break;
        }

        return view;
    }
}
