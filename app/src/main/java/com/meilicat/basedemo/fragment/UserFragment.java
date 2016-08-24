package com.meilicat.basedemo.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.meilicat.basedemo.R;
import com.meilicat.basedemo.activity.usercenter.AccountActivity;
import com.meilicat.basedemo.activity.usercenter.BankListActivity;
import com.meilicat.basedemo.activity.usercenter.MsgListActivity;
import com.meilicat.basedemo.activity.usercenter.PswModifyActivity;
import com.meilicat.basedemo.activity.usercenter.QuestionBackActivity;
import com.meilicat.basedemo.activity.usercenter.SettingActivity;
import com.meilicat.basedemo.base.BaseActivity;
import com.meilicat.basedemo.base.BaseApplication;
import com.meilicat.basedemo.bean.UserInfoBean;
import com.meilicat.basedemo.bean.UserInfoSupplierEntity;
import com.meilicat.basedemo.conf.Constants;
import com.meilicat.basedemo.utils.HttpManager;
import com.meilicat.basedemo.utils.UIUtils;
import com.meilicat.basedemo.utils.ViewUtils;
import com.meilicat.basedemo.view.UIPerfectlyItem;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by cj on 2016/1/23.
 */
@SuppressWarnings("deprecation")
public class UserFragment extends Fragment implements View.OnClickListener {

    @Bind(R.id.user_account_item)
    RelativeLayout mAccountItem;
    @Bind(R.id.user_msg_item)
    UIPerfectlyItem mMsgItem;
    @Bind(R.id.user_modify_item)
    UIPerfectlyItem mModifyItem;
    @Bind(R.id.user_bank_item)
    UIPerfectlyItem mBankItem;
    @Bind(R.id.user_question_item)
    UIPerfectlyItem mQuestionItem;
    @Bind(R.id.user_setting_item)
    UIPerfectlyItem mSettingsItem;

    @Bind(R.id.user_head_iv)
    CircleImageView mHeadIv;
    @Bind(R.id.user_name_tv)
    TextView mUserNameTv;
    @Bind(R.id.user_qian_tv)
    TextView mQianTv;
    @Bind(R.id.user_account_tv)
    TextView mUserAccountTv;

    UserInfoSupplierEntity mUserEntityData;
    private int mMsgCount;

    private BaseActivity mBaseAct;

    private boolean isFirst = true;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof BaseActivity) {
            mBaseAct = (BaseActivity) activity;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        initEvent();
    }

    private void initEvent() {
        mAccountItem.setOnClickListener(this);
        mMsgItem.setOnClickListener(this);
        mModifyItem.setOnClickListener(this);
        mBankItem.setOnClickListener(this);
        mQuestionItem.setOnClickListener(this);
        mSettingsItem.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
            getUpdateData();
    }

    private void getUpdateData() {
        if(isFirst) {
            mBaseAct.showLoadDialog();
        }

        HttpManager manger = new HttpManager(UIUtils.getContext()) {
            @Override
            protected void onSuccess(Object obj) {
                super.onSuccess(obj);
                mBaseAct.dismissLoadDialog();

                Gson gson = new Gson();
                UserInfoBean userBean = null;
                try {
                    userBean = gson.fromJson((String) obj, UserInfoBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (userBean == null) {
                    return;
                }

                if (userBean.getMsg() == 1) {
                    if (userBean.getData() != null) {
                        mMsgCount = userBean.getData().getAnnounceCount();
                        mUserEntityData = userBean.getData().getSupplier();
                        updateUIView();
                        isFirst = false;
                    }
                }
            }

            @Override
            protected void onFail() {
                super.onFail();
                mBaseAct.dismissLoadDialog();
            }

            @Override
            protected void onTimeOut() {
                super.onTimeOut();
                mBaseAct.dismissLoadDialog();
            }
        };


        manger.post(Constants.URLS.SUPPLIER_USERINFO, new HashMap());
    }

    private void updateUIView() {
        if (mUserEntityData == null) {
            return;
        }

        String headUrl = mUserEntityData.getAvatarImageURL();
        if (!TextUtils.isEmpty(headUrl)) {
            BaseApplication.imageLoader.displayImage(headUrl, mHeadIv, BaseApplication.options);
        }

        mUserNameTv.setText(mUserEntityData.getName());
        mUserAccountTv.setText(mUserEntityData.getSupAccount());
        mMsgItem.getRightTextView().setText(mMsgCount + "");
        mMsgItem.getRightTextView().setTextColor(Color.parseColor("#ff0099"));

        int isSignCode = mUserEntityData.getIsSign();
        if (isSignCode == 1) {
            mQianTv.setVisibility(View.VISIBLE);
        } else {
            mQianTv.setVisibility(View.GONE);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_account_item:
                Intent accountIntent = new Intent(getActivity(), AccountActivity.class);
                accountIntent.putExtra(AccountActivity.KEY_ACCOUNT_DATA, mUserEntityData);
                startActivity(accountIntent);
                break;
            case R.id.user_msg_item:
                Intent msgIntent = new Intent(getActivity(), MsgListActivity.class);
                startActivity(msgIntent);
                break;
            case R.id.user_modify_item:
                Intent pswIntent = new Intent(getActivity(), PswModifyActivity.class);
                startActivity(pswIntent);
                break;
            case R.id.user_bank_item:
                String bankName = mUserEntityData.getBankName();
                String bankNumber = mUserEntityData.getBankNumber();
                if (TextUtils.isEmpty(bankName) && TextUtils.isEmpty(bankNumber)) {
                    ViewUtils.showToast(UIUtils.getContext(), "还没有绑定的银行卡哦");
                    return;
                }

                Intent bankIntent = new Intent(getActivity(), BankListActivity.class);
                bankIntent.putExtra(BankListActivity.KEY_NAME, bankName);
                bankIntent.putExtra(BankListActivity.KEY_NUM, bankNumber);
                startActivity(bankIntent);
                break;
            case R.id.user_question_item:
                Intent questionIntent = new Intent(getActivity(), QuestionBackActivity.class);
                startActivity(questionIntent);
                break;
            case R.id.user_setting_item:
                Intent settingIntent = new Intent(getActivity(), SettingActivity.class);
                startActivity(settingIntent);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isFirst = true;
    }
}
