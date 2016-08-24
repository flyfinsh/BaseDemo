package com.meilicat.basedemo.activity.commonditymanager;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.meilicat.basedemo.R;
import com.meilicat.basedemo.base.BaseTitleActivity;
import com.meilicat.basedemo.utils.MonitoredUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/5/21.
 */
public class PictureChooseActivity extends BaseTitleActivity implements PictureAdapter.CheckChangedListener,
        View.OnClickListener {
    public static final String KEY_MAX_CHOOSE_COUNT = "max_count";
    public static final String KEY_RESULT_DATA = "result_data";

    private GridView mGridView;
    private TextView mPerviewBtn;
    private TextView mConfirmBtn;
    private TextView mChossedCountText;
    private PictureAdapter mAdapter;

    private int maxChoosedCount = Integer.MAX_VALUE;



    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        maxChoosedCount = getIntent().getIntExtra(KEY_MAX_CHOOSE_COUNT, Integer.MAX_VALUE);
        mAdapter = new PictureAdapter(PictureChooseActivity.this, maxChoosedCount, this);
        setContentView(R.layout.choose_picture_layout);
        setupNavigationBar(R.id.navigation_bar);
        setCommonTitle("Camera Roll");

        addRightButtonText("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initViews();
        mGridView.setAdapter(mAdapter);
        loadPictures();


    }

    private Handler mMainHandler = new Handler()
    {

        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            if (msg.what == 0x12) {
                List<String> allPictrues = (List<String>) msg.obj;
                mAdapter.setData(allPictrues);
            }
        }
    };

    private void loadPictures() {
        MonitoredUtil.startBackgroundJob(this, getResources().getString(R.string.pic_choose_load_tips), true,
                new Runnable() {
                    public void run() {
                        List<String> allPictrues = getAllPictrues();
                        Message msg = mMainHandler.obtainMessage(0x12, allPictrues);
                        msg.sendToTarget();
                    }

                }, mMainHandler);
    }

    private void initViews() {
        mGridView = (GridView) findViewById(R.id.id_gridView);
        mPerviewBtn = (TextView) findViewById(R.id.id_choose_perview);
        mConfirmBtn = (TextView) findViewById(R.id.id_total_confirm);
        mPerviewBtn.setOnClickListener(this);
        mConfirmBtn.setOnClickListener(this);
        mChossedCountText = (TextView) findViewById(R.id.tv_number);
        changeEditStatus(null);
    }

    private void changeEditStatus(List<String> choosedItems) {
        if (choosedItems != null && !choosedItems.isEmpty()) {
            mPerviewBtn.setEnabled(true);
            mConfirmBtn.setEnabled(true);
            mPerviewBtn.setTextColor(Color.parseColor("#333333"));
            mConfirmBtn.setTextColor(Color.parseColor("#ff0099"));
            mChossedCountText.setVisibility(View.VISIBLE);
            mChossedCountText.setText(Integer.toString(choosedItems.size()));
        } else {
            mPerviewBtn.setEnabled(false);
            mConfirmBtn.setEnabled(false);
            mChossedCountText.setVisibility(View.GONE);
            mPerviewBtn.setTextColor(Color.parseColor("#999999"));
            mConfirmBtn.setTextColor(Color.parseColor("#999999"));
        }
    }

    private List<String> getAllPictrues() {
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        List<String> allPicture = new ArrayList<>();
        ContentResolver contentResolver = getContentResolver();

        //// 只查询jpeg和png的图片
        Cursor mCursor = contentResolver.query(mImageUri, null, MediaStore.Images.Media.MIME_TYPE + "=? or "
                + MediaStore.Images.Media.MIME_TYPE + "=?", new String[] { "image/jpeg", "image/png" },
                MediaStore.Images.Media.DATE_MODIFIED);

        if (mCursor != null) {
            while (mCursor.moveToNext()) {
                // 获取图片的路径
                String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                if (path.endsWith(".jpg") || path.endsWith(".png") || path.endsWith(".jpeg")) {
                    allPicture.add(path);
                } else {
                    Log.e("lzm", "path = " + path);
                }
            }
            //
            mCursor.close();
        }
        return allPicture;
    }

    @Override
    public void onCheckChanged(List<String> selectedImage) {
        changeEditStatus(selectedImage);
    }

    @Override
    public void onClick(View view) {
        if (view == mPerviewBtn) {
            List<String> choosedItems = mAdapter.getChoosedItems();
            if (choosedItems != null && !choosedItems.isEmpty()) {
                ArrayList<String> list = new ArrayList<String>(choosedItems.size());
                for (String str : choosedItems) {
                    list.add("file://" + str);
                }
                toScanMedia(list, 0);
            }

        } else if (view == mConfirmBtn) {
            List<String> choosedItems = mAdapter.getChoosedItems();

            String[] tempChoosedItemArr = choosedItems.toArray(new String[0]);
            Intent reslut = new Intent();
            reslut.putExtra(KEY_RESULT_DATA, tempChoosedItemArr);
            setResult(RESULT_OK, reslut);
            finish();
        }
    }

    private void toScanMedia(ArrayList<String> urls, int index) {
        Intent intent = new Intent(this, PictureViewerActivity.class);
        intent.putStringArrayListExtra(PictureViewerActivity.EXTRA_KEY_MEDIA_URLS, urls);
        intent.putExtra(PictureViewerActivity.EXTRA_KEY_DEFULT_INDEX, index);
        startActivity(intent);
    }
}
