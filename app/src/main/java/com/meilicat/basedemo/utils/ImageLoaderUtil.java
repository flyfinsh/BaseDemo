package com.meilicat.basedemo.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.View;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;

import com.meilicat.basedemo.R;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ImageLoaderUtil{
	public DisplayImageOptions options;	// DisplayImageOptions是用于设置图片显示的类
	public AnimateFirstDisplayListener animateFirstListener = new AnimateFirstDisplayListener();;
	public ImageLoader imageLoader = ImageLoader.getInstance();
	
	private static ImageLoaderUtil instance;
	public PauseOnScrollListener scrollListener ;
	
	public PauseOnScrollListener getOnScrollListener(OnScrollListener onScrollListener) {
		scrollListener = new PauseOnScrollListener(imageLoader, true, false, onScrollListener);
		return scrollListener;
	}

	public PauseOnScrollListener getOnScrollListener() {
		scrollListener = new PauseOnScrollListener(imageLoader, true, false);
		return scrollListener;
	}

	public ImageLoaderUtil(Context context) {
		initImageLoader(context);
		setDisplayImageOptions(R.mipmap.head_logo, R.mipmap.head_logo, R.mipmap.head_logo);
		scrollListener = new PauseOnScrollListener(imageLoader, true, false);
	}
	
	public static ImageLoaderUtil getInstance(Context context) {
		if(instance == null){
			instance = new ImageLoaderUtil(context);
		}
		return instance;
	}
	
	/**
	 * 加载图片并展示到界面上
	 * @param imagePath
	 * @param imgView
	 */
	public void displayImage(String imagePath, ImageView imgView){
		imageLoader.displayImage(imagePath, imgView, options, animateFirstListener);
	}
	
	public ImageLoader getImageLoader() {
		return imageLoader;
	}

	@SuppressLint("NewApi")
	private void initImageLoader(Context context) {
		File cacheFile;
		cacheFile = new File(Environment.getExternalStorageDirectory(), "/BearBaby/ImgCache");
		if(!cacheFile.exists()){
			cacheFile.mkdirs();
		}
        int maxMemory = (int) (Runtime.getRuntime().maxMemory());
        int cacheSize = maxMemory / 4;
        //应用程序已获得内存  
        //long totalMemory = ((int) Runtime.getRuntime().totalMemory());  
        //应用程序已获得内存中未使用内存  
        //long freeMemory = ((int) Runtime.getRuntime().freeMemory());  
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
		.threadPoolSize(3)
		.threadPriority(Thread.NORM_PRIORITY - 2)
		.denyCacheImageMultipleSizesInMemory()
		.discCacheSize(300 * 1024 * 1024)
		.discCacheFileNameGenerator(new Md5FileNameGenerator())
		.tasksProcessingOrder(QueueProcessingType.LIFO)
		.memoryCache(new LruMemoryCache(cacheSize))
		//.memoryCacheSize(cacheSize)
		.discCache(new UnlimitedDiscCache(cacheFile) )
		//.writeDebugLogs() // Remove for release app
		.build();
		
		imageLoader.init(config);
	}
	
	/** 
	 * @param stubImage        设置图片下载期间显示的图片
	 * @param imageForEmptyUri 设置图片Uri为空或是错误的时候显示的图片 
	 * @param imageOnFail	        设置图片加载或解码过程中发生错误显示的图片	
	 */
	public void setDisplayImageOptions(int stubImage, int imageForEmptyUri, int imageOnFail) {
		options = new DisplayImageOptions.Builder()
			.imageScaleType(ImageScaleType.EXACTLY)
			.bitmapConfig(Bitmap.Config.ARGB_8888)
			//.delayBeforeLoading(100)
			.resetViewBeforeLoading()// 设置图片下载期间显示的图片
			.showImageForEmptyUri(imageForEmptyUri)	// 设置图片Uri为空或是错误的时候显示的图片
			.showImageOnFail(imageOnFail)		// 设置图片加载或解码过程中发生错误显示的图片
			.cacheInMemory()						// 设置下载的图片是否缓存在内存中
			.cacheOnDisc()	// 设置下载的图片是否缓存在SD卡中
			.build();
	}
	
	/**
	 * 图片加载第一次显示监听器
	 * @author Administrator
	 *
	 */
	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {
		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());
		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				// 是否第一次显示
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					// 图片淡入效果
					FadeInBitmapDisplayer.animate(imageView, 600);
					displayedImages.add(imageUri);
				}
			}
		}
	}
}
