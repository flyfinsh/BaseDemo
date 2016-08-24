package com.meilicat.basedemo.utils;

import android.content.Context;
import android.widget.Toast;

public class T {
	
	private T(){
		throw new UnsupportedOperationException("cannot be instantiated");
	}
	
	 public static boolean isShow = true;

	private static Toast mToast;
	  
	    /** 
	     * 短时间显示Toast 
	     *  
	     * @param context 上下文对象
	     * @param message 需要显示的字符串信息
	     */  
	    public static void showShort(Context context, CharSequence message)  
	    {  
	        if (isShow)  {
				if (mToast == null){
					mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
				}else {
					mToast.setText(message);
					mToast.setDuration(Toast.LENGTH_SHORT);
				}
				mToast.show();
			}
	    }
	  
	    /** 
	     * 短时间显示Toast 
	     *  
	     * @param context 上下文对象
	     * @param message 资源ID
	     */  
	    public static void showShort(Context context, int message)  
	    {  
	        if (isShow)  {
				if (mToast == null){
					mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
				}else {
					mToast.setText(message);
					mToast.setDuration(Toast.LENGTH_SHORT);
				}
				mToast.show();
			}

	    }  
	  
	    /** 
	     * 长时间显示Toast 
	     *  
	     * @param context 上下文对象
	     * @param message 需要显示的字符串信息
	     */  
	    public static void showLong(Context context, CharSequence message)  
	    {  
	        if (isShow)  
	            Toast.makeText(context, message, Toast.LENGTH_LONG).show();  
	    }  
	  
	    /** 
	     * 长时间显示Toast 
	     *  
	     * @param context 上下文对象
	     * @param message 资源ID
	     */  
	    public static void showLong(Context context, int message)  
	    {  
	        if (isShow) {
				if (mToast == null){
					mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
				}else {
					mToast.setText(message);
					mToast.setDuration(Toast.LENGTH_SHORT);
				}
				mToast.show();
			}
	    }
	  
	    /** 
	     * 自定义显示Toast时间 
	     *  
	     * @param context 上下文对象
	     * @param message 需要显示的字符串信息
	     * @param duration 需要显示的时间，单位毫秒
	     */  
	    public static void show(Context context, CharSequence message, int duration)  
	    {  
	        if (isShow)  {
				if (mToast == null){
					mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
				}else {
					mToast.setText(message);
					mToast.setDuration(Toast.LENGTH_SHORT);
				}
				mToast.show();
			}
	    }
	  
	    /** 
	     * 自定义显示Toast时间 
	     *  
	     * @param context 上下文对象
	     * @param message 资源ID
	     * @param duration 需要显示的时间，单位毫秒
	     */  
	    public static void show(Context context, int message, int duration)  
	    {  
	        if (isShow)  {
				if (mToast == null){
					mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
				}else {
					mToast.setText(message);
					mToast.setDuration(Toast.LENGTH_SHORT);
				}
				mToast.show();
			}
	    }

	public static void cancelToast() {
		if (mToast != null) {
			mToast.cancel();
		}
	}
}
