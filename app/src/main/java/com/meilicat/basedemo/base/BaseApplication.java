package com.meilicat.basedemo.base;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.util.LruCache;

import com.meilicat.basedemo.R;
import com.meilicat.basedemo.bean.SupplierDetailBean;
import com.meilicat.basedemo.bean.UserInfoBean;
import com.meilicat.basedemo.utils.DiskLruCache;
import com.meilicat.basedemo.utils.LogUtils;
import com.meilicat.basedemo.utils.UIUtils;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.litepal.LitePalApplication;

import java.io.File;
import java.io.IOException;

/**
 * @author cj
 * @描述 这是全局唯一的application 初始化了imageloader的一些配置信息
 *
 * */

public class BaseApplication extends LitePalApplication {
	public static ImageLoader imageLoader = ImageLoader.getInstance();
	/**使用universal-image-loader时的一些默认配置这个配置为广告的配置 这个是普通图片的，为正方形*/
	public static DisplayImageOptions options;
	
	/** 会员头像模式值 */
	public static DisplayImageOptions headOptions;
	
	/** 广告模式值 */
	public static DisplayImageOptions advOptions;
	private static Context mContext;
	private static Handler mHandler;
	private static long mMainThreadId;

	private MeiliCatSettings mSettings;

	private static BaseApplication instance;

	private static String cookie = "";

	public static SupplierDetailBean mSupplier;

	private DiskLruCache mDiskLruCache;
	private UserInfoBean userInfo;

	@Override
	public void onCreate() {

		instance = this;
		mSettings = new MeiliCatSettings(this);

		/********** 异步下载图片缓存类 初始化 */
		initImageLoader(getApplicationContext());
		
		options = optionsInit(0);
		
		headOptions = optionsInit(1);
		
		advOptions = optionsInit(2);

		mContext = getApplicationContext();
		LogUtils.i("hero","获取了context");

		// 2.主线程的Handler
		mHandler = new Handler();

		// 3.得到主线程的id
		mMainThreadId = android.os.Process.myTid();

		mDiskLruCache = getDiskLruCache();
		super.onCreate();
	}
	public static Context getContext() {
		return mContext;
	}

	public static Handler getHandler() {
		return mHandler;
	}

	public static long getMainThreadId() {
		return mMainThreadId;
	}

	public static BaseApplication getInstance() {
		return instance;
	}


	/**
	 * 初始化options
	 * @param flag	根据不同的flag返回不同的option，主要是默认图片不同
	 * @return
	 */
	private DisplayImageOptions optionsInit(int flag){
		int sourceId = R.mipmap.head_logo;
		
		if (flag==0) {
			// TODO: 2016/1/20 更改默认图片
			sourceId = R.mipmap.head_logo;
		} else if (flag==1) {
			sourceId = R.mipmap.default_error;
		}
		/*else if (flag==2) {
			sourceId = R.mipmap.adv_img2;
		}*/
		
		return new DisplayImageOptions.Builder()
		.showStubImage(sourceId)// 加载等待 时显示的图片
		.showImageForEmptyUri(sourceId)// 加载数据为空时显示的图片
		.showImageOnFail(sourceId)// 加载失败时显示的图片
		.bitmapConfig(Bitmap.Config.RGB_565)
		.imageScaleType(ImageScaleType.EXACTLY)
		.cacheInMemory()//在內存中做緩存
		.cacheOnDisc() //在磁盤進行緩
		.build();
		
	}
	
	
	/** 图片加载库初始化*/
	private void initImageLoader(Context context) {

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.threadPoolSize(3)//内部最多三个线程加载数据
				.denyCacheImageMultipleSizesInMemory()
				.memoryCache(new WeakMemoryCache())
				.memoryCacheSize(3 * 1024 * 1024)//内存缓存的大小为3M
				.discCacheSize(30 * 1024 * 1024)//设置磁盘缓存的大小为30M
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO).enableLogging() // Not
				.build();
//		ImageLoader.getInstance().init(config);
		imageLoader.init(config);
	}

	public static MeiliCatSettings getSettings(Activity activity) {
		if (activity.getApplication() instanceof BaseApplication) {
			return ((BaseApplication) activity.getApplication()).getSettings();
		}
		return new MeiliCatSettings(activity);
	}

	public MeiliCatSettings getSettings() {
		return mSettings;
	}

	public void setSupplierDetail(SupplierDetailBean bean){
		mSupplier = bean;
	}


	//用于协议缓存的东西
	private LruCache<String, String> mProtocolMap = new LruCache<>(2*1024*1024);

	public LruCache<String, String> getProtocolMap() {
		return mProtocolMap;
	}

	private DiskLruCache getDiskLruCache() {
		DiskLruCache mDiskLruCache = null;
		try {
			File cacheDir = getDiskCacheDir(UIUtils.getContext(), "json");
			if (!cacheDir.exists()) {
				cacheDir.mkdirs();
			}
			mDiskLruCache = DiskLruCache.open(cacheDir, getAppVersion(UIUtils.getContext()), 1, 3 * 1024 * 1024);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mDiskLruCache;
	}

	private File getDiskCacheDir(Context context, String uniqueName) {
		String cachePath;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
				|| !Environment.isExternalStorageRemovable()) {
			cachePath = context.getExternalCacheDir().getPath();
		} else {
			cachePath = context.getCacheDir().getPath();
		}
		return new File(cachePath + File.separator + uniqueName);
	}

	private int getAppVersion(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return info.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return 1;
	}

	public DiskLruCache getDiskCache(){
		return mDiskLruCache;
	}

	public void setUserInfo(UserInfoBean bean){
		userInfo  = bean;
	}

	public UserInfoBean getUserInfo(){
		if (userInfo != null){
			return userInfo;
		}
		return null;
	}

}
