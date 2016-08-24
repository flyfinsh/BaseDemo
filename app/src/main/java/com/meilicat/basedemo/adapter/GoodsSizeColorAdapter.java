package com.meilicat.basedemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.meilicat.basedemo.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by lizhiming on 2016/1/26.
 */
public class GoodsSizeColorAdapter extends BaseAdapter {

    private ArrayList<HashMap<String, String>> mTestData;

    private LayoutInflater mInflater;

    public GoodsSizeColorAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public void setData(ArrayList<HashMap<String, String>> datas) {
        this.mTestData = datas;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mTestData == null || mTestData.isEmpty()) {
            return 0;
        }
        return mTestData.size();
    }

    @Override
    public HashMap<String, String> getItem(int position) {
        if (mTestData == null || mTestData.isEmpty()) {
            return null;
        }
        return mTestData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.size_color_item, null);
            holder = new ViewHolder();
            holder.colorTv = (TextView) convertView.findViewById(R.id.size_color_left);
            holder.sizeTv = (TextView) convertView.findViewById(R.id.size_color_right);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        setItemData(holder, position);

        return convertView;
    }

    private void setItemData(ViewHolder holder, int position) {
        if (mTestData == null || mTestData.isEmpty()) {
            return;
        }

        HashMap<String, String> tagMap = mTestData.get(position);
        Iterator iter = tagMap.entrySet().iterator();
        if (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            String val = (String) entry.getValue();

            holder.colorTv.setText(key);
            holder.sizeTv.setText(val);
        }
    }

    class ViewHolder {
        TextView colorTv;
        TextView sizeTv;
    }
}
