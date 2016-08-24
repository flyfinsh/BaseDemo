package com.meilicat.basedemo.activity.commonditymanager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.meilicat.basedemo.R;
import com.meilicat.basedemo.adapter.GoodsPubPhotoAdapter;
import com.meilicat.basedemo.base.BaseTitleActivity;
import com.meilicat.basedemo.bean.AppSimpleBean;
import com.meilicat.basedemo.bean.ClientColorSizeBean;
import com.meilicat.basedemo.bean.GoodsProductSkuListEntity;
import com.meilicat.basedemo.bean.GoodsRowsEntity;
import com.meilicat.basedemo.bean.NormSelItemData;
import com.meilicat.basedemo.bean.UploadImageBean;
import com.meilicat.basedemo.conf.Constants;
import com.meilicat.basedemo.event.BusProvider;
import com.meilicat.basedemo.event.FinishActivityEvent;
import com.meilicat.basedemo.event.NotifyUpdateEvent;
import com.meilicat.basedemo.event.SelectFinlaIdEvent;
import com.meilicat.basedemo.event.SelectTypeEvent;
import com.meilicat.basedemo.utils.AppFilePath;
import com.meilicat.basedemo.utils.BitmapPickUtils;
import com.meilicat.basedemo.utils.HttpManager;
import com.meilicat.basedemo.utils.UIUtils;
import com.meilicat.basedemo.utils.ViewUtils;
import com.meilicat.basedemo.view.ActionSheet;
import com.meilicat.basedemo.view.ScrollGridView;
import com.meilicat.basedemo.view.UIItem;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by lizhiming on 2016/2/1.
 */
public class CommodityPublishActivity extends BaseTitleActivity implements View.OnClickListener, ActionSheet.ActionSheetListener {

    public static final int REQUEST_CODE_CAMERA = 1; // 拍照
    public static final int REQUEST_CODE_GALLERY = 2; // 相册

    public static final int CAMERA_PERMISSION_REQUEST_CODE = 5; // 拍照权限
    public static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 6; // 相册权限

    private static final int REQUEST_CODE_SEASON = 8;
    private static final int REQUEST_CODE_NORMS = 9;

    @Bind(R.id.goods_pic_gridview)
    ScrollGridView picGridview;
    @Bind(R.id.goods_pic_emptyp_layout)
    RelativeLayout mEmptyPicLayout;

    @Bind(R.id.goods_pic_title_edit)
    EditText mGoodsNameEdit;
    @Bind(R.id.goods_pic_num_edit)
    EditText mGoodsNumEdit;
    @Bind(R.id.goods_pic_money_edit)
    EditText mMoneyEdit;
    @Bind(R.id.goods_pic_title_ct)
    TextView mGoodsNameTv;
    @Bind(R.id.goods_pic_num_ct)
    TextView mGoodsNumTv;
    @Bind(R.id.goods_pic_money_ct)
    TextView mMoneyTv;

    @Bind(R.id.goods_pic_type_item)
    UIItem mTypeItem;
    @Bind(R.id.goods_pic_season_item)
    UIItem mSeasonItem;
    @Bind(R.id.goods_pic_norms_item)
    UIItem mNormsItem;

    @Bind(R.id.goods_pic_norms_look_item)
    LinearLayout mNormLookItem;
    @Bind(R.id.goods_pic_norms_look_cs)
    TextView mNormLookCSTv;

    ArrayList<String> mSelUrls = new ArrayList<>();
    private GoodsPubPhotoAdapter mPubAdapter;

    private ExecutorService mSingleThreadExecutor = Executors.newSingleThreadExecutor();

    public static final String KEY_EDIT_DATA = "key_edit_data";
    private GoodsRowsEntity mEditData;
    public static final String KEY_IS_LOOK = "key_is_look";
    private boolean isLookType;

    public static final String KEY_NORMS_SHOW_DATA = "key_norms_show_data";
    private ArrayList<ClientColorSizeBean> mShowCSDatas;

    private ArrayList<NormSelItemData> mSelNormList;//已选规格、用于跳转规格界面显示已选

    @Override
    public void setContent() {
        super.setContent();
        setContentView(R.layout.activity_goods_publish_layout);
        ButterKnife.bind(this);

        BusProvider.getInstance().register(this);

        setupNavigationBar(R.id.navigation_bar);
        setCommonTitle("商品信息");

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            mEditData = extra.getParcelable(KEY_EDIT_DATA);
            isLookType = extra.getBoolean(KEY_IS_LOOK);

            mShowCSDatas = extra.getParcelableArrayList(KEY_NORMS_SHOW_DATA);
        }
        if (!isLookType) {
            addRightButtonText("提交", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    submitGoods();
                }
            });
        }
    }

    private boolean isDeautType;
    private int usePosition;

    private boolean isEditType; //是否是编辑修改模式

    @Override
    public void initView() {
        super.initView();

        mPubAdapter = new GoodsPubPhotoAdapter(this, mSelUrls);
        picGridview.setAdapter(mPubAdapter);
        picGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isLookType) {
                    return;
                }
                String itemText = mSelUrls.get(position);
                if ("default".equals(itemText)) {
                    isDeautType = true;
                    showActionSheet("拍摄", "从相册选择");
                } else {
                    usePosition = position;
                    isDeautType = false;
                    showActionSheet("查看原图", "删除");
                }
            }
        });

        mEmptyPicLayout.setOnClickListener(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            //申请CAMERA权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
        }

        mTypeItem.setOnClickListener(this);
        mSeasonItem.setOnClickListener(this);
        mNormsItem.setOnClickListener(this);

        if (mEditData != null) {
            addSelUrl(mEditData.getImageURL_1());
            addSelUrl(mEditData.getImageURL_2());
            addSelUrl(mEditData.getImageURL_3());
            addSelUrl(mEditData.getImageURL_4());
            addSelUrl(mEditData.getImageURL_5());
            addSelUrl(mEditData.getImageURL_6());

            if (isLookType) {
                mGoodsNameEdit.setVisibility(View.GONE);
                mGoodsNumEdit.setVisibility(View.GONE);
                mMoneyEdit.setVisibility(View.GONE);
                mGoodsNameTv.setVisibility(View.VISIBLE);
                mGoodsNumTv.setVisibility(View.VISIBLE);
                mMoneyTv.setVisibility(View.VISIBLE);

                mGoodsNameTv.setText(mEditData.getCn());
                String productNum = mEditData.getProductNumber();
                if(!TextUtils.isEmpty(productNum) && productNum.length() > 4) {
                    int startIndex = productNum.length() - 4;
                    productNum = productNum.substring(startIndex);
                }
                mGoodsNumTv.setText(productNum);
                mMoneyTv.setText(mEditData.getPrice() + "");

                mTypeItem.getRightTextView().setText(mEditData.getCategoryName());
                mTypeItem.getArrowIv().setVisibility(View.GONE);
                mSeasonItem.getRightTextView().setText(mEditData.getSeasonName());
                mSeasonItem.getArrowIv().setVisibility(View.GONE);
                mTypeItem.setClickable(false);
                mSeasonItem.setClickable(false);
                mNormsItem.setVisibility(View.GONE);
                mNormLookItem.setVisibility(View.VISIBLE);

                if (mShowCSDatas != null && !mShowCSDatas.isEmpty()) {
                    StringBuffer sbNorms = new StringBuffer();
                    for (int i = 0; i < mShowCSDatas.size(); i++) {
                        ClientColorSizeBean csItem = mShowCSDatas.get(i);
                        if (i == mShowCSDatas.size() - 1) {
                            sbNorms.append(csItem.colorName + "：" + csItem.sizeName);
                        } else {
                            sbNorms.append(csItem.colorName + "：" + csItem.sizeName + "\n");
                        }
                    }
                    mNormLookCSTv.setText(sbNorms.toString());
                }

            } else {
                isEditType = true;
                mGoodsNameEdit.setVisibility(View.VISIBLE);
                mGoodsNumEdit.setVisibility(View.VISIBLE);
                mMoneyEdit.setVisibility(View.VISIBLE);
                mGoodsNameTv.setVisibility(View.GONE);
                mGoodsNumTv.setVisibility(View.GONE);
                mMoneyTv.setVisibility(View.GONE);

                mGoodsNameEdit.setText(mEditData.getCn());
                mGoodsNumEdit.setText(mEditData.getProductNumber());
                mTypeItem.getRightTextView().setText(mEditData.getCategoryName());
                mCategoryId = mEditData.getCategoryId() + "";
                mCategoryIds = mEditData.getCategoryIdStr();
                mSeasonItem.getRightTextView().setText(mEditData.getSeasonName());
                mMoneyEdit.setText(mEditData.getPrice() + "");
                mSeasonId = mEditData.getSeasonId() + "";
                mNormsItem.getRightTextView().setText("已编辑");

                List<GoodsProductSkuListEntity> tempSkuList = mEditData.getSupplierProductSkuList();
                if (tempSkuList != null && !tempSkuList.isEmpty()) {
                    getEditNormJson(mEditData.getSupplierProductSkuList());
                }
            }
        }
    }

    private void updatePicView() {
        if (mSelUrls.isEmpty()) {
            mEmptyPicLayout.setVisibility(View.VISIBLE);
            picGridview.setVisibility(View.GONE);
        } else {
            mEmptyPicLayout.setVisibility(View.GONE);
            picGridview.setVisibility(View.VISIBLE);
        }
    }

    public void showActionSheet(String... str) {
        setTheme(R.style.ActionSheetStyleIOS7);
        ActionSheet.Builder builder = ActionSheet.createBuilder(this, getSupportFragmentManager())
                .setCancelButtonTitle(getResources().getString(R.string.app_cancel_text)).setCancelableOnTouchOutside(true);
        builder.setOtherButtonTitles(str);
        builder.setListener(this).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.goods_pic_emptyp_layout:
                if (isLookType) {
                    return;
                }
                isDeautType = true;
                showActionSheet("拍摄", "从相册选择");
                break;
            case R.id.goods_pic_type_item:
                Intent typeIntent = new Intent(this, SeasonChooseActivity.class);
                typeIntent.putExtra(SeasonChooseActivity.KEY_IS_TYPE, true);
                startActivity(typeIntent);
                break;
            case R.id.goods_pic_season_item:
                Intent seasonIntent = new Intent(this, SeasonChooseActivity.class);
                seasonIntent.putExtra(SeasonChooseActivity.KEY_SEASON_TEXT, mSeasonItem.getRightTextView().getText().toString());
                startActivityForResult(seasonIntent, REQUEST_CODE_SEASON);
                break;
            case R.id.goods_pic_norms_item:
                Intent normsIntent = new Intent(this, GoodsNormsActivity.class);
                normsIntent.putParcelableArrayListExtra(GoodsNormsActivity.KEY_SEL_NORM_LIST, mSelNormList);
                startActivityForResult(normsIntent, REQUEST_CODE_NORMS);
                break;
            default:
                break;
        }
    }


    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
    }

    private File pictureOut;

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        switch (index) {
            case 0:
                if (isDeautType) {
                    pictureOut = AppFilePath.getPictureFile(BitmapPickUtils.createCameraPictureName());
                    BitmapPickUtils.startSystemCamera(this, REQUEST_CODE_CAMERA, pictureOut);
                } else {
                    String tempUrl = mSelUrls.get(usePosition);
                    ArrayList<String> tempList = new ArrayList<>();
                    tempList.add(tempUrl);
                    toScanMedia(tempList, 0);
                }
                break;
            case 1:
                if (isDeautType) {
                    Intent intent = new Intent(this, PictureChooseActivity.class);
                    intent.putExtra(PictureChooseActivity.KEY_MAX_CHOOSE_COUNT, getCanSelCount());
                    startActivityForResult(intent, REQUEST_CODE_GALLERY);
                } else {
                    mSelUrls.remove(usePosition);
                    if(!isHaveDefaultType(mSelUrls)) {
                        mSelUrls.add("default");
                    }
                    mPubAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    private boolean isHaveDefaultType(ArrayList<String> mSelUrls) {
        for(String text : mSelUrls) {
            if("default".equals(text)) {
                return true;
            }
        }
        return false;
    }

    private int getCanSelCount() {
        if (!mSelUrls.isEmpty()) {
            int size = mSelUrls.size();

            for (String path : mSelUrls) {
                if ("default".equals(path)) {
                    size = size - 1;
                }
            }
            return 6 - size;
        }
        return 6;
    }

    private void toScanMedia(ArrayList<String> urls, int index) {
        Intent intent = new Intent(this, PictureViewerActivity.class);
        intent.putStringArrayListExtra(PictureViewerActivity.EXTRA_KEY_MEDIA_URLS, urls);
        intent.putExtra(PictureViewerActivity.EXTRA_KEY_DEFULT_INDEX, index);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doNextMethod(requestCode, grantResults);
    }

    private void doNextMethod(int requestCode, int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                requestPicPermission();
            } else {
                // Permission Denied
            }
        } else if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
//                ViewUtils.showToast(getApplicationContext(), "申请权限结束");
            } else {

                // Permission Denied
            }
        }
    }

    private void requestPicPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_CAMERA:
                if (resultCode == RESULT_OK) {
                    // 设置文件保存路径这里放在跟目录下
                    if (pictureOut != null && pictureOut.exists()) {
//                        Log.e("lzm", "pictureOut.getAbsolutePath()=" + pictureOut.getAbsolutePath());
//                        String ableUrl = "file://"+pictureOut.getAbsolutePath();
//                        addSelUrl(ableUrl);

                        Log.e("lzm", "拍照——length=" + pictureOut.length());

                        List<String> cameraList = new ArrayList<>();
                        cameraList.add(pictureOut.getAbsolutePath());
                        uploadImage(cameraList);
                    }
                }
                break;
            case REQUEST_CODE_GALLERY:
                if (resultCode == RESULT_OK) {
                    // String filePath =
                    // BitmapPickUtils.pickResultFromGalleryImage(this, data);
                    // 读取相册缩放图片
                    Bundle extras = data.getExtras();
                    String[] choosedItems = extras.getStringArray(PictureChooseActivity.KEY_RESULT_DATA);

                    List<String> galleryList = new ArrayList<>();
                    for (String path : choosedItems) {
//                        addSelUrl("file://" + path);
                        galleryList.add(path);
                    }
                    uploadImage(galleryList);
                }
                break;
            case REQUEST_CODE_SEASON:
                if (resultCode == RESULT_OK) {
                    mSeasonId = data.getStringExtra(SeasonChooseActivity.KEY_IS_TYPE);
                    String seasonText = data.getStringExtra(SeasonChooseActivity.KEY_SEASON_TEXT);
                    if (!TextUtils.isEmpty(seasonText)) {
                        mSeasonItem.getRightTextView().setText(seasonText);
                    }
                }
                break;
            case REQUEST_CODE_NORMS:
                if (resultCode == RESULT_OK) {
                    mNormsItem.getRightTextView().setText("已编辑");
                    mSelNormList = data.getParcelableArrayListExtra(GoodsNormsActivity.KEY_SEL_NORM_LIST);
                    if (mSelNormList != null && !mSelNormList.isEmpty()) {
                        getNormJson(mSelNormList);
                    }
                }
                break;
            default:
                break;
        }
    }

    private int threadIndex;

    private void uploadImage(final List<String> urlList) {

        showLoadDialog();

        final HttpManager httpManager = new HttpManager(UIUtils.getContext()) {
            @Override
            protected void onSuccess(Object obj) {
                super.onSuccess(obj);

                if (threadIndex == urlList.size() - 1) {
                    dismissLoadDialog();
                    threadIndex = 0;
                } else {
                    threadIndex++;
                }

                Gson gson = new Gson();
                UploadImageBean bean = gson.fromJson(String.valueOf(obj), UploadImageBean.class);
                if (bean == null) {
                    ViewUtils.showToast(getApplicationContext(), "上传失败");
                    return;
                }
                String serverUrl = bean.getData();
                if (!TextUtils.isEmpty(serverUrl)) {
                    addSelUrl(serverUrl);
                }
            }

            @Override
            protected void onFail() {
                super.onFail();
                if (threadIndex == urlList.size() - 1) {
                    dismissLoadDialog();
                    threadIndex = 0;
                } else {
                    threadIndex++;
                }
                ViewUtils.showToast(getApplicationContext(), "上传失败");
            }

            @Override
            protected void onTimeOut() {
                super.onTimeOut();
                if (threadIndex == urlList.size() - 1) {
                    dismissLoadDialog();
                    threadIndex = 0;
                } else {
                    threadIndex++;
                }
                ViewUtils.showToast(getApplicationContext(), "上传失败");
            }
        };

        for (int i = 0; i < urlList.size(); i++) {
            final int index = i;
            mSingleThreadExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.e("lzm", "index=" + index);
                        httpManager.uploadImage(Constants.URLS.GOODS_IMAGE_UPLOAD, urlList.get(index));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

    private void addSelUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }

        if (!isLookType) {
            if (!mSelUrls.isEmpty()) {
                int index = mSelUrls.size() - 1;
                mSelUrls.remove(index);
            }
        }

        if (mSelUrls.size() < 6) {
            mSelUrls.add(url);
        }

        if (!isLookType) {
            if (mSelUrls.size() < 6) {
                mSelUrls.add("default");
            }
        }

        updatePicView();
        mPubAdapter.notifyDataSetChanged();
    }

    private String mCategoryId;
    private String mCategoryIds;
    private String mSeasonId;
    private String mNormJsonText;

    /**
     * 提交商品信息
     */
    private void submitGoods() {

        String tilteText = mGoodsNameEdit.getText().toString();
        String goodsNum = mGoodsNumEdit.getText().toString();
        String goodsPrice = mMoneyEdit.getText().toString();

        if (TextUtils.isEmpty(tilteText) || TextUtils.isEmpty(goodsNum) || TextUtils.isEmpty(goodsPrice) || TextUtils.isEmpty(mSeasonId)
                || TextUtils.isEmpty(mCategoryIds) || TextUtils.isEmpty(mCategoryId) || TextUtils.isEmpty(mNormJsonText)) {
            ViewUtils.showToast(getApplicationContext(), "请完善信息");
            return;
        }

        if (mSelUrls == null || mSelUrls.isEmpty()) {
            ViewUtils.showToast(getApplicationContext(), "请至少上传一张图片");
            return;
        }

        showLoadDialog();
        HttpManager httpManager = new HttpManager(UIUtils.getContext()) {
            @Override
            protected void onSuccess(Object obj) {
                super.onSuccess(obj);
                dismissLoadDialog();
                Gson gson = new Gson();
                AppSimpleBean appSimpleBean = null;
                try {
                    appSimpleBean = gson.fromJson(String.valueOf(obj), AppSimpleBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (appSimpleBean == null) {
                    ViewUtils.showToast(getApplicationContext(), "提交失败");
                    return;
                }

                if (appSimpleBean.getMsg() == 1) {
                    ViewUtils.showToast(getApplicationContext(), "提交成功");
                    BusProvider.getInstance().post(new NotifyUpdateEvent());
                    if(isEditType) {
                        BusProvider.getInstance().post(new FinishActivityEvent());
                    }
                    finish();
                } else {
                    ViewUtils.showToast(getApplicationContext(), appSimpleBean.getMsgbox());
                }
            }

            @Override
            protected void onFail() {
                super.onFail();
                dismissLoadDialog();
                ViewUtils.showToast(getApplicationContext(), "提交失败");
            }

            @Override
            protected void onTimeOut() {
                super.onTimeOut();
                dismissLoadDialog();
                ViewUtils.showToast(getApplicationContext(), "提交失败");
            }
        };

        HashMap<String, String> paramsMap = new HashMap<>();
        if (isEditType) {
            paramsMap.put("id", mEditData.getId());
        }
        paramsMap.put("cn", tilteText);
        paramsMap.put("categoryId", mCategoryId);
        paramsMap.put("categoryIdStr", mCategoryIds);
        paramsMap.put("seasonId", mSeasonId);
        paramsMap.put("productNumber", goodsNum);
        paramsMap.put("price", goodsPrice);
        ArrayList<String> urls = new ArrayList<>();
        urls.addAll(mSelUrls);
        if (urls.size() < 6) {
            urls.remove(urls.size() - 1);
        }

        for (int i = 0; i < urls.size(); i++) {
            paramsMap.put("imageURL_" + (i + 1), urls.get(i));
        }
        paramsMap.put("skus", mNormJsonText);

        if (isEditType) {
            httpManager.post(Constants.URLS.GOODS_EDIT, paramsMap);
        } else {
            httpManager.post(Constants.URLS.GOODS_ADD, paramsMap);
        }
    }

    /**
     * 拼接编辑状态规格json串
     */
    private void getEditNormJson(List<GoodsProductSkuListEntity> normList) {
        if (mSelNormList == null) {
            mSelNormList = new ArrayList<>();
        }
        JSONArray jsonArray = new JSONArray();
        for (GoodsProductSkuListEntity itemData : normList) {
            NormSelItemData selData = new NormSelItemData();
            String selText = itemData.getColorName() + " " + itemData.getSizeName();
            selData.filterName = selText;
            selData.colorName = itemData.getColorName();
            selData.colorAbbrName = itemData.getColorAbbr();
            selData.sizeName = itemData.getSizeName();
            selData.sizeAbbrName = itemData.getSizeAbbr();
            mSelNormList.add(selData);

            JSONObject object = new JSONObject();
            try {
                object.put("colorName", itemData.getColorName());
                object.put("colorAbbr", itemData.getColorAbbr());
                object.put("sizeName", itemData.getSizeName());
                object.put("sizeAbbr", itemData.getSizeAbbr());
                jsonArray.put(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        mNormJsonText = jsonArray.toString();
    }

    /**
     * 拼接规格json串
     *
     * @param selNormList
     */
    private void getNormJson(ArrayList<NormSelItemData> selNormList) {
        JSONArray jsonArray = new JSONArray();
        for (NormSelItemData itemData : selNormList) {
            JSONObject object = new JSONObject();
            try {
                object.put("colorName", itemData.colorName);
                object.put("colorAbbr", itemData.colorAbbrName);
                object.put("sizeName", itemData.sizeName);
                object.put("sizeAbbr", itemData.sizeAbbrName);
                jsonArray.put(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        mNormJsonText = jsonArray.toString();
    }

    @Subscribe
    public void onEventSelectFinalId(SelectFinlaIdEvent event) {
        mCategoryId = event.mCategoryId;
    }

    @Subscribe
    public void onEventSelectIds(SelectTypeEvent event) {
        mCategoryIds = event.mIds;
        mTypeItem.getRightTextView().setText(event.mTypeText);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);

        BusProvider.getInstance().unregister(this);
        mSingleThreadExecutor.shutdown();
        mSingleThreadExecutor = null;
    }
}
