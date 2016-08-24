package com.meilicat.basedemo.activity.buyeractivity;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meilicat.basedemo.R;
import com.meilicat.basedemo.base.BaseActivity;
import com.meilicat.basedemo.utils.UIUtils;

import butterknife.Bind;
import butterknife.ButterKnife;


public class BuyerSupplierSelect extends BaseActivity {

    @Bind(R.id.buyer_supplier_title_back)
    RelativeLayout mBuyerSupplierTitleBack;
    @Bind(R.id.buyer_supplier_lv)
    ListView mBuyerSupplierLv;
    private String[] mSupplierses;

    @Override
    public void setContent() {
        setContentView(R.layout.activity_supplier_select);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        mSupplierses = intent.getStringArrayExtra("suppliers");

    }

    @Override
    public void initTitle() {
        mBuyerSupplierTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    int currentPosition = -1;


    @Override
    public void initView() {
        mAdapter = new SupplierAdapter();

        mBuyerSupplierLv.setAdapter(mAdapter);
        mBuyerSupplierLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                currentPosition = position;
                mAdapter.notifyDataSetChanged();
                Intent intent = getIntent();
                intent.putExtra("currentPosition",position);
                setResult(1, intent);
                finish();
            }
        });
    }

    SupplierAdapter mAdapter;
    class SupplierAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            if (mSupplierses != null){
                return mSupplierses.length;
            }

            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SupplierHolder holder;

            if (convertView == null){
                convertView = LayoutInflater.from(UIUtils.getContext()).inflate(R.layout.item_supplier,null);
                holder = new SupplierHolder();

                holder.mConfirm = (ImageView) convertView.findViewById(R.id.item_supplier_confirm);
                holder.mName = (TextView) convertView.findViewById(R.id.item_supplier_name);
                convertView.setTag(holder);
            }else {
                holder = (SupplierHolder) convertView.getTag();

            }

            if (currentPosition == position){
                holder.mConfirm.setVisibility(View.VISIBLE);
            }else {
                holder.mConfirm.setVisibility(View.GONE);
            }
            holder.mName.setText(mSupplierses[position]);

            return convertView;
        }
    }

    class SupplierHolder{
        ImageView mConfirm;
        TextView mName;
    }

}
