package com.meilicat.basedemo.activity.usercenter;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.meilicat.basedemo.R;
import com.meilicat.basedemo.activity.LoginAcitivity;
import com.meilicat.basedemo.base.BaseApplication;
import com.meilicat.basedemo.base.BaseTitleActivity;
import com.meilicat.basedemo.bean.LoginBean;
import com.meilicat.basedemo.bean.UserInfoSupplierEntity;
import com.meilicat.basedemo.conf.Constants;
import com.meilicat.basedemo.utils.BitmapPickUtils;
import com.meilicat.basedemo.utils.HttpManager;
import com.meilicat.basedemo.utils.IntentUtil;
import com.meilicat.basedemo.utils.SPUtils;
import com.meilicat.basedemo.utils.T;
import com.meilicat.basedemo.utils.UIUtils;
import com.meilicat.basedemo.view.UIItem;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by user on 2016/2/22.
 */
public class AccountActivity extends BaseTitleActivity implements View.OnClickListener {

    public static final String KEY_ACCOUNT_DATA = "key_account_data";

    public static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 6; // 相册权限

    public static final int PHOTOZOOM = 2; // 缩放
    public static final String IMAGE_UNSPECIFIED = "image/*";
    public static final int PHOTORESOULT = 3;// 结果

    //    @Bind(R.id.account_pic_item)
//    RelativeLayout mPicItem;
    @Bind(R.id.account_head_iv)
    CircleImageView mPicIv;

    @Bind(R.id.account_name_tv)
    TextView mNameTv;
    @Bind(R.id.account_account_tv)
    TextView mAccountTv;
    @Bind(R.id.account_factory_tv)
    TextView mFactoryTv;
    @Bind(R.id.account_market_tv)
    TextView mMarketTv;
    @Bind(R.id.account_addr_item)
    UIItem mAddrItem;
    @Bind(R.id.account_contacter_item)
    UIItem mContacterItem;
    @Bind(R.id.account_phone_item)
    UIItem mPhoneItem;

    private final int CODE_ADDR = 14;
    private final int CODE_CONTACTER = 15;
    private final int CODE_PHONE = 16;

    private UserInfoSupplierEntity mData;
    private SPUtils mSPUtils;

    @Override
    public void setContent() {
        super.setContent();
        setContentView(R.layout.activity_account_layout);
        ButterKnife.bind(this);
        setupNavigationBar(R.id.navigation_bar);
        setCommonTitle("账号设置");
        addRightButtonText("退出登录", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //退出登录
                HttpManager manger = new HttpManager(UIUtils.getContext()) {
                    @Override
                    protected void onFail() {
                        T.showShort(UIUtils.getContext(), "退出登录失败");
                    }

                    @Override
                    protected void onSuccess(Object obj) {
                        Gson gson = new Gson();
                        LoginBean bean = gson.fromJson(obj + "", LoginBean.class);
                        if (bean.msg == 0) {
                            T.showShort(UIUtils.getContext(), "退出登录失败");
                        } else {
                            T.showShort(UIUtils.getContext(), "退出登录成功");
                            mSPUtils = new SPUtils(UIUtils.getContext());
                            mSPUtils.putString("cookie", "");

                            IntentUtil.startActivity(AccountActivity.this, LoginAcitivity.class);
                            finishAll();
                        }
//                        LogUtils.i(obj+"");
                    }
                };
                Map<String, String> map = new HashMap<>();

                manger.post(Constants.URLS.OUTLOGIN, map);

            }
        });

        mData = getIntent().getExtras().getParcelable(KEY_ACCOUNT_DATA);
    }

    @Override
    public void initView() {
        super.initView();
//        mPicItem.setOnClickListener(this);
        mAddrItem.setOnClickListener(this);
        mContacterItem.setOnClickListener(this);
        mPhoneItem.setOnClickListener(this);

//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
        //申请WRITE_EXTERNAL_STORAGE权限
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                    WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
//        }

        if (mData != null) {
            refrshUI();
        }
    }

    private void refrshUI() {
        String headUrl = mData.getAvatarImageURL();
        if (!TextUtils.isEmpty(headUrl)) {
            BaseApplication.imageLoader.displayImage(headUrl, mPicIv, BaseApplication.options);
        }

        mNameTv.setText(mData.getName());
        mAccountTv.setText(mData.getSupAccount());
        mFactoryTv.setText(mData.getIndustryBeltName_cn());
        mMarketTv.setText(mData.getMarketName_cn());
        String addrText = mData.getAddress();

        if (!TextUtils.isEmpty(addrText) && addrText.length() > 15) {
            addrText = addrText.substring(0, 12);
            addrText += "...";
        }

        mAddrItem.getRightTextView().setText(addrText);
        mContacterItem.getRightTextView().setText(mData.getContractPerson());
        mPhoneItem.getRightTextView().setText(mData.getMobilePhoneNum());
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        doNextMethod(requestCode, grantResults);
//    }

    private void doNextMethod(int requestCode, int[] grantResults) {
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
//                ViewUtils.showToast(getApplicationContext(), "申请权限结束");
            } else {

                // Permission Denied
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.account_pic_item:
//                Intent intent1 = new Intent(Intent.ACTION_PICK, null);
//                intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
//                startActivityForResult(intent1, PHOTOZOOM);
//                break;
            case R.id.account_addr_item:
                String addrConent = null;
                if (mData != null) {
                    addrConent = mData.getAddress();
                }
                Intent addrIntent = new Intent(AccountActivity.this, CommonModifyActivity.class);
                addrIntent.putExtra(CommonModifyActivity.KEY_TITLE, "地址");
                addrIntent.putExtra(CommonModifyActivity.KEY_CONTENT, addrConent);
                addrIntent.putExtra(CommonModifyActivity.KEY_PARAMS, "address");
                startActivityForResult(addrIntent, CODE_ADDR);
                break;
            case R.id.account_contacter_item:
                String contacterConent = null;
                if (mData != null) {
                    contacterConent = mData.getContractPerson();
                }

                Intent contacterIntent = new Intent(AccountActivity.this, CommonModifyActivity.class);
                contacterIntent.putExtra(CommonModifyActivity.KEY_TITLE, "联系人");
                contacterIntent.putExtra(CommonModifyActivity.KEY_CONTENT, contacterConent);
                contacterIntent.putExtra(CommonModifyActivity.KEY_PARAMS, "contractPerson");
                startActivityForResult(contacterIntent, CODE_CONTACTER);
                break;
            case R.id.account_phone_item:
                String phoneConent = null;
                if (mData != null) {
                    phoneConent = mData.getMobilePhoneNum();
                }

                Intent phoneIntent = new Intent(AccountActivity.this, CommonModifyActivity.class);
                phoneIntent.putExtra(CommonModifyActivity.KEY_TITLE, "联系电话");
                phoneIntent.putExtra(CommonModifyActivity.KEY_CONTENT, phoneConent);
                phoneIntent.putExtra(CommonModifyActivity.KEY_PARAMS, "mobilePhoneNum");
                startActivityForResult(phoneIntent, CODE_PHONE);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }

        if (requestCode == PHOTOZOOM) {
            // 读取相册缩放图片
            String bitmapPath = BitmapPickUtils.pickResultFromGalleryImage(this, data);
            if (!TextUtils.isEmpty(bitmapPath)) {
                BitmapPickUtils.startPhotoClip(this, bitmapPath, PHOTORESOULT);
            }
        }
        if (requestCode == PHOTORESOULT) {
            String path = data.getStringExtra("imagePath");
            Log.e("lzm", "path=" + path);

            if (TextUtils.isEmpty(path)) {
                return;
            }

            File pathFile = new File(path);
            String localFileUri = "file://" + pathFile.getAbsolutePath();
            BaseApplication.imageLoader.displayImage(localFileUri, mPicIv, BaseApplication.options);
        }

        switch (requestCode) {
            case CODE_ADDR:
                String addr = data.getStringExtra(CommonModifyActivity.KEY_CONTENT);
                if (!TextUtils.isEmpty(addr)) {
                    mAddrItem.getRightTextView().setText(addr);
                }
                break;
            case CODE_CONTACTER:
                String contacter = data.getStringExtra(CommonModifyActivity.KEY_CONTENT);
                if (!TextUtils.isEmpty(contacter)) {
                    mContacterItem.getRightTextView().setText(contacter);
                }
                break;
            case CODE_PHONE:
                String phone = data.getStringExtra(CommonModifyActivity.KEY_CONTENT);
                if (!TextUtils.isEmpty(phone)) {
                    mPhoneItem.getRightTextView().setText(phone);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
