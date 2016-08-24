package com.meilicat.basedemo.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meilicat.basedemo.R;
import com.meilicat.basedemo.activity.commonditymanager.GoodsModelUtils;
import com.meilicat.basedemo.base.BaseApplication;
import com.meilicat.basedemo.bean.ClientColorSizeBean;
import com.meilicat.basedemo.bean.GoodsProductSkuListEntity;
import com.meilicat.basedemo.bean.GoodsRowsEntity;
import com.meilicat.basedemo.fragment.commodity.CommodityManagerFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lizhiming on 2016/1/26.
 */
public class CommodityManagerAdapter extends CommonAdapter<GoodsRowsEntity> {

//    private GoodsSizeColorAdapter sizeAdapter;

    private boolean isWaitPage;

    public CommodityManagerAdapter(Context ctx, int layoutId, String requestType) {
        super(ctx, layoutId);

        if (CommodityManagerFragment.TYPE_PUTAWAY_WAIT.equals(requestType)) {
            isWaitPage = true;
        } else {
            isWaitPage = false;
        }
    }

    @Override
    protected void convert(ViewHolderEntity vh, GoodsRowsEntity itemData, int itemViewType) {
//        ScrollGridView sizeGridview;
        ImageView goodsIv = vh.getView(R.id.item_commodity_iv);
        ImageView checkIv = vh.getView(R.id.item_commodity_check_iv);
        LinearLayout ivBottomLayout = vh.getView(R.id.item_commodity_iv_bottom);
        ivBottomLayout.getBackground().setAlpha(175);
        TextView goodsNumTv = vh.getView(R.id.item_commodity_num_tv);
//            holder.sizeGridview = (ScrollGridView) convertView.findViewById(R.id.item_commodity_size_gridview);
//            sizeAdapter = new GoodsSizeColorAdapter(mContext);
//            holder.sizeGridview.setAdapter(sizeAdapter);
        TextView colorTv = vh.getView(R.id.item_commodity_color_tv);
        TextView sizeTv = vh.getView(R.id.item_commodity_size_tv);
        TextView csMoreTv = vh.getView(R.id.item_commodity_cs_more_tv);

        TextView priceTv = vh.getView(R.id.item_commodity_price_tv);

        LinearLayout saleLayout = vh.getView(R.id.sale_layout);
        TextView saleTv = vh.getView(R.id.item_commodity_sale_tv);
        LinearLayout wayLayout = vh.getView(R.id.way_layout);
        TextView wayTv = vh.getView(R.id.item_commodity_way_tv);


        String imageUrl = itemData.getImageURL_1();
        if (!TextUtils.isEmpty(imageUrl)) {
            BaseApplication.imageLoader.displayImage(imageUrl, goodsIv, BaseApplication.options);
        }

        goodsNumTv.setText(itemData.getProductNumber());

        List<GoodsProductSkuListEntity> childList = itemData.getSupplierProductSkuList();
        if (childList != null && !childList.isEmpty()) {
            ArrayList<ClientColorSizeBean> tempCSDatas = new ArrayList<>();

            for (GoodsProductSkuListEntity childItem : childList) {
                if (GoodsModelUtils.isHaveColor(tempCSDatas, childItem)) {
                    tempCSDatas = GoodsModelUtils.appendSizeData(tempCSDatas, childItem);
                } else {
                    ClientColorSizeBean otherBean = new ClientColorSizeBean();
                    otherBean.colorName = childItem.getColorName();
                    otherBean.sizeName = childItem.getSizeName();
                    tempCSDatas.add(otherBean);
                }
            }

            if (tempCSDatas.size() == 1) {
                csMoreTv.setVisibility(View.INVISIBLE);
                colorTv.setText(tempCSDatas.get(0).colorName + "：" + tempCSDatas.get(0).sizeName);
                sizeTv.setVisibility(View.INVISIBLE);
            } else if (tempCSDatas.size() == 2) {
                csMoreTv.setVisibility(View.INVISIBLE);
                sizeTv.setVisibility(View.VISIBLE);
                colorTv.setText(tempCSDatas.get(0).colorName + "：" + tempCSDatas.get(0).sizeName);
                sizeTv.setText(tempCSDatas.get(1).colorName + "：" + tempCSDatas.get(1).sizeName);
            } else if (tempCSDatas.size() > 2) {
                csMoreTv.setVisibility(View.VISIBLE);
                sizeTv.setVisibility(View.VISIBLE);
                colorTv.setText(tempCSDatas.get(0).colorName + "：" + tempCSDatas.get(0).sizeName);
                sizeTv.setText(tempCSDatas.get(1).colorName + "：" + tempCSDatas.get(1).sizeName);
            }

        }

//        colorTv.setText(colorText);
//        sizeTv.setText(sizeText);

//        ArrayList<HashMap<String, String>> colorSizeList = itemData.colorSizeList;
//        if (colorSizeList == null || colorSizeList.isEmpty()) {
//            holder.sizeGridview.setVisibility(View.GONE);
//        } else {
//            holder.sizeGridview.setVisibility(View.VISIBLE);
//            sizeAdapter.setData(colorSizeList);
//        }

        priceTv.setText(itemData.getPrice() + "");

        saleTv.setText(itemData.getStatCount() + "");
        wayTv.setText(itemData.getTransitCount() + "");

        if (isWaitPage) {
            saleLayout.setVisibility(View.GONE);
            wayLayout.setVisibility(View.GONE);

            checkIv.setVisibility(View.VISIBLE);
            int checkStatu = itemData.getStatus();

            if (checkStatu == 1) {
                checkIv.setImageResource(R.mipmap.check_pass);
            } else if (checkStatu == 2) {
                checkIv.setImageResource(R.mipmap.check_refused);
            } else { //0待审核 审核中
                checkIv.setImageResource(R.mipmap.check_ing);
            }
        } else {
            saleLayout.setVisibility(View.VISIBLE);
            wayLayout.setVisibility(View.VISIBLE);
            checkIv.setVisibility(View.GONE);
        }
    }

}
