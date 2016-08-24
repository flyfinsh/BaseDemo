package com.meilicat.basedemo.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

public class PicViewerAdapter extends PagerAdapter {

	private View[] mImageviews;
	
	public PicViewerAdapter(View[] tempViews) {
		this.mImageviews = tempViews;
	}
	
	@Override
	public int getCount() {
		return mImageviews.length;
	}

	@Override
	public boolean isViewFromObject(View view, Object obj) {
		return view == obj;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
//		super.destroyItem(container, position, object);
		((ViewPager)container).removeView(mImageviews[position % mImageviews.length]);  
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		((ViewPager)container).addView(mImageviews[position % mImageviews.length], 0);  
        return mImageviews[position % mImageviews.length];  
	}
	
	

}
