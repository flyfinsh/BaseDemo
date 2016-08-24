package com.meilicat.basedemo.activity.commonditymanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.meilicat.basedemo.R;
import com.meilicat.basedemo.adapter.CommonAdapter;
import com.meilicat.basedemo.base.BaseApplication;
import com.meilicat.basedemo.utils.ViewUtils;
import com.meilicat.basedemo.view.GridItemView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2015/5/21.
 */
public class PictureAdapter extends CommonAdapter<String> {
    /**
     * 用户选择的图片，存储为图片的完整路径
     */
    private List<String> mSelectedImage = new LinkedList<String>();
    private CheckChangedListener mListener;
    private int maxCount = Integer.MAX_VALUE;
    private Context context;

    private DisplayImageOptions tempOptions;

    public PictureAdapter(Context ctx, int maxCount, CheckChangedListener mListener)
    {
        super(ctx, R.layout.goods_pic_choose_item);
        this.context = ctx;
        this.mListener = mListener;
        this.maxCount = maxCount;

        tempOptions = new DisplayImageOptions.Builder()
                .showStubImage(R.mipmap.default_error)// 加载等待 时显示的图片
                .showImageForEmptyUri(R.mipmap.default_error)// 加载数据为空时显示的图片
                .showImageOnFail(R.mipmap.default_error)// 加载失败时显示的图片
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory()//在內存中做緩存
                .build();
    }

    @Override
    protected void convert(ViewHolderEntity entity, final String itemData, int itemViewType) {
        // 图片
        GridItemView view = (GridItemView) entity.getConvertView();
        view.setWidthHeightScale(4, 4);

        final ImageView srcImg = entity.getView(R.id.src_img);
//        srcImg.setImageResource(R.mipmap.default_error);
        String newitemUrl = "file://"+itemData;
        Log.e("lzm", "newitemUrl="+newitemUrl);
        BaseApplication.imageLoader.displayImage(newitemUrl, srcImg, tempOptions);
        final ImageView chooseImg = entity.getView(R.id.img_edit_choose);
        chooseImg.setColorFilter(null);
        // 设置ImageView的点击事件
        srcImg.setOnClickListener(new View.OnClickListener()
        {

            // 选择，则将图片变暗，反之则反之
            @Override
            public void onClick(View v) {
                // 已经选择过该图片
                if (mSelectedImage.contains(itemData)) {
                    mSelectedImage.remove(itemData);
                    setItemStatus(srcImg, chooseImg, false);
                    // 未选择该图片
                } else {
                    if (mSelectedImage.size() < maxCount) {
                        mSelectedImage.add(itemData);
                        setItemStatus(srcImg, chooseImg, true);
                    } else {
                        ViewUtils.showToast(mContext.getApplicationContext(), "最多可选择"+maxCount+"个文件");

                        return;
                    }
                }
                if (mListener != null) {
                    mListener.onCheckChanged(mSelectedImage);
                }
            }
        });

        /**
         * 已经选择过的图片，显示出选择过的效果
         */
        if (mSelectedImage.contains(itemData)) {
            setItemStatus(srcImg, chooseImg, true);
        } else {
            setItemStatus(srcImg, chooseImg, false);
        }
    }

    public void setCheckChangedListener(CheckChangedListener listener) {
        this.mListener = listener;
    }

    private void setItemStatus(ImageView srcImage, ImageView chooseImage, boolean checked) {
        //
        if (checked) {
            chooseImage.setImageResource(R.mipmap.goods_pic_sel);
            srcImage.setColorFilter(Color.parseColor("#77000000"));
        } else {
            chooseImage.setImageResource(R.mipmap.goods_pic_unsel);
            srcImage.setColorFilter(null);
        }
    }

    //
    public List<String> getChoosedItems() {
        return mSelectedImage;
    }

    public interface CheckChangedListener {
        void onCheckChanged(List<String> selectedImage);
    }
}
