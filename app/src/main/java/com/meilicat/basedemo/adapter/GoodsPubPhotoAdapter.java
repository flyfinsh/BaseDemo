package com.meilicat.basedemo.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.meilicat.basedemo.R;
import com.meilicat.basedemo.base.BaseApplication;

import java.util.List;

public class GoodsPubPhotoAdapter extends CommonAdapter<String> {


    public GoodsPubPhotoAdapter(Context ctx, List<String> lists) {

        super(ctx, lists, new int[]{0, 1}, new int[]{R.layout.goods_pic_normal_item, R.layout.goods_pic_default_item});
    }

    @Override
    protected void convert(ViewHolderEntity arg0, String arg1, int arg2) {

        String imgUrl = arg1;
        if (arg2 == 1) {
//            TextView tv = (TextView)arg0.getView(R.id.tvMediaNumber);
//            tv.setText(getInfoStr());
        } else {
            ImageView srcImg = arg0.getView(R.id.pic_src_iv);
            if (!TextUtils.isEmpty(imgUrl)) {
                BaseApplication.imageLoader.displayImage(imgUrl, srcImg, BaseApplication.headOptions);
            } else {
                srcImg.setImageResource(R.mipmap.head_logo);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        String type = getItem(position);
        if ("default".equals(type)) {
            return 1;
        } else {
            return 0;
        }
    }

    private String getInfoStr() {
        String str = "";
        int num = getData().size() - 1;
        str = num + "/6张";
        return str;
    }

}
