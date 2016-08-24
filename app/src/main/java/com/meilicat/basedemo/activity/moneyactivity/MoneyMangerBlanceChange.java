package com.meilicat.basedemo.activity.moneyactivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.meilicat.basedemo.R;
import com.meilicat.basedemo.base.BaseActivity;
import com.meilicat.basedemo.base.BaseApplication;
import com.meilicat.basedemo.bean.MoneyChangeBean;
import com.meilicat.basedemo.bean.SupplierDetailBean;
import com.meilicat.basedemo.bean.SupplierOutAccountDetail;
import com.meilicat.basedemo.conf.Constants;
import com.meilicat.basedemo.utils.DeviceConfiger;
import com.meilicat.basedemo.utils.HttpManager;
import com.meilicat.basedemo.utils.T;
import com.meilicat.basedemo.utils.UIUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by cj on 2016/2/17.
 * 这是提现界面
 */
public class MoneyMangerBlanceChange extends BaseActivity {

    @Bind(R.id.money_blance_change_title_back)
    RelativeLayout mMoneyBlanceChangeTitleBack;
    @Bind(R.id.money_blance_change_blank)
    LinearLayout mMoneyBlanceChangeBlank;
    @Bind(R.id.money_blance_change_edit)
    LinearLayout mMoneyBlanceChangeEdit;
    @Bind(R.id.money_blance_change_canuse)
    TextView mMoneyBlanceChangeCanuse;
    @Bind(R.id.money_blance_change_comfirm)
    Button mMoneyBlanceChangeComfirm;
    @Bind(R.id.money_blance_change_num)
    EditText mMoneyBlanceChangeNum;
    @Bind(R.id.bank_and_number)
    TextView mBankAndNumber;

    private double usefulMoney = 0;
    private SupplierDetailBean mSupplier;

    @Override
    public void setContent() {
        setContentView(R.layout.activity_money_blance_change);
        ButterKnife.bind(this);
        initData();
        initEvent();
    }

    private void initData() {
        mSupplier = BaseApplication.getInstance().mSupplier;
        usefulMoney =  mSupplier.data.supplier.money;

    }

    /**
     * 初始化事件
     */
    private void initEvent() {
      /*  mMoneyBlanceChangeBlank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBank();
            }
        });*/
        mMoneyBlanceChangeComfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = mMoneyBlanceChangeNum.getText().toString().trim();
                if (!TextUtils.isEmpty(number)) {
                    //说明不是空的
                    int outMoney = Integer.parseInt(number);
                    if (outMoney <= 0){
                        T.showShort(UIUtils.getContext(), "金额不能小于等于0");
                    }else {
                        if (outMoney > usefulMoney) {
                            T.showShort(UIUtils.getContext(), "您账户的余额不足");
                        } else {
                            //可以转账
                            outMoney(outMoney);
                        }
                    }


                } else {
                    T.showShort(UIUtils.getContext(), "金额不能为空");
                }
            }
        });
    }

    /**
     * 调用接口，转账
     */
    private void outMoney(int outMoney) {
        HttpManager manager = new HttpManager(UIUtils.getContext()) {
            @Override
            protected void onFail() {
                dismissLoadDialog();
                T.showShort(UIUtils.getContext(), "提现失败");
            }

            @Override
            protected void onSuccess(Object obj) {
                dismissLoadDialog();
                Gson gson = new Gson();
                MoneyChangeBean bean = gson.fromJson(obj + "", MoneyChangeBean.class);
                if (bean != null){
                    seeDetail(bean.data.moneyLogId);
                }
            }

            @Override
            protected void onTimeOut() {
                dismissLoadDialog();
//                T.showShort(UIUtils.getContext(), "网络请求超时");
            }

        };
        manager.get(Constants.URLS.SUPPLIER_MONEY_WITHDRAWALS +"?moneyDetail=" + outMoney);
        showLoadDialog();
    }

    private void seeDetail(String moneyLogId) {

        String url =  Constants.URLS.SUPPLIER_MONEY_OUTACCOUNT+"?moneyLogId="+moneyLogId;

        HttpManager manger = new HttpManager(UIUtils.getContext()){
            @Override
            protected void onFail() {
                dismissLoadDialog();
                T.showShort(UIUtils.getContext(),"查看订单详情失败");
                finish();
            }

            @Override
            protected void onSuccess(Object obj) {
                //请求数据成功
                if (obj != null){
                    Gson gson = new Gson();
                    SupplierOutAccountDetail outAccountDetail = gson.fromJson(obj + "", SupplierOutAccountDetail.class);
                    if (outAccountDetail.msg == 0){
                        T.showShort(UIUtils.getContext(),"查看订单详情失败");
                    }else {
                        SupplierOutAccountDetail.DataEntity.SupplierEntity supplier = outAccountDetail.data.supplier;
                        T.showShort(UIUtils.getContext(), "请求数据成功");
                        if (supplier == null){
                            T.showShort(UIUtils.getContext(), "查看订单详情失败");
                            finish();
                            return;
                        }
                        Intent intent = new Intent();
                        intent.putExtra("detailss", supplier);
                        intent.putExtra("type", 0);
                        intent.setClass(UIUtils.getContext(), MoneyMangerOrderInner.class);
                        startActivity(intent);
                    }
                }else {
                    T.showShort(UIUtils.getContext(), "请求数据失败");
                }
                dismissLoadDialog();
                finish();
            }

            @Override
            protected void onTimeOut() {
                dismissLoadDialog();
                finish();
            }
        };
        showLoadDialog();
        manger.get(url);
    }

    /**
     * 弹出一个选择银行卡的dialog
     */
    private void showBank() {
        final Dialog dialog = new Dialog(MoneyMangerBlanceChange.this, R.style.common_dialog_theme);

        View view = LayoutInflater.from(UIUtils.getContext()).inflate(R.layout.custom_dialog_change_bank, null);

        ListView lv = (ListView) view.findViewById(R.id.change_bank_lv);

        mAdapter = new ChangeBankAdapter();
        lv.setDividerHeight(UIUtils.dp2Px(10));
        lv.setDivider(new ColorDrawable(Color.GRAY));
        lv.setAdapter(mAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //这里写
                T.showShort(UIUtils.getContext(), "你点击了---" + position);
                dialog.dismiss();
            }
        });
        dialog.setContentView(view);

        int dialogWidth = DeviceConfiger.dp2px(360);

        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = dialogWidth;
        dialogWindow.setAttributes(lp);

        dialog.setCanceledOnTouchOutside(true);

        dialog.show();

    }

    ChangeBankAdapter mAdapter;



    class ChangeBankAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(UIUtils.getContext()).inflate(R.layout.item_change_bank_lv, null);
                holder.mName = (TextView) convertView.findViewById(R.id.item_change_bank_name);
                holder.mType = (TextView) convertView.findViewById(R.id.item_change_bank_type);
                holder.mFour = (TextView) convertView.findViewById(R.id.item_change_bank_four);
                holder.mDivi = (RelativeLayout) convertView.findViewById(R.id.item_bank_change_divi);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position == 0) {
                holder.mDivi.setVisibility(View.GONE);
            } else {
                holder.mDivi.setVisibility(View.VISIBLE);
            }


            return convertView;
        }
    }

    class ViewHolder {
        TextView mName;
        TextView mType;
        TextView mFour;
        RelativeLayout mDivi;
    }


    @Override
    public void initTitle() {
        mMoneyBlanceChangeTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void initView() {
        if (mSupplier != null){
            mBankAndNumber.setText(mSupplier.data.supplier.bankName+"("+mSupplier.data.supplier.bankNumber+")");
            mMoneyBlanceChangeCanuse.setText("可用余额：￥ "+mSupplier.data.supplier.money);

        }

    }

}
