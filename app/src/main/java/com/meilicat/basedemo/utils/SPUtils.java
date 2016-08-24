package com.meilicat.basedemo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.Set;


public class SPUtils {
	private SharedPreferences	mSp;
	private Editor				mEditor;

	public SPUtils(Context context) {
		mSp = context.getSharedPreferences("meilicat", Context.MODE_PRIVATE);// TODO: 2016/1/20 确定sp的文件名
		mEditor = mSp.edit();
	}
	public SPUtils(Context context,String name) {
		mSp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
		mEditor = mSp.edit();
	}

	/**取出string*/
	public String getString(String key, String defValue) {
		return mSp.getString(key, defValue);
	}

	/**取出int*/
	public int getInt(String key, int defValue) {
		return mSp.getInt(key, defValue);
	}

	/**取出boolean*/
	public boolean getBoolean(String key, boolean defValue) {
		return mSp.getBoolean(key, defValue);
	}

	/**存入string*/
	public void putString(String key, String value) {
		mEditor.putString(key, value);
		mEditor.commit();
	}

	/**存入int*/
	public void putInt(String key, int value) {
		mEditor.putInt(key, value);
		mEditor.commit();
	}

	/**存入boolean*/
	public void putBoolean(String key, boolean value) {
		mEditor.putBoolean(key, value);
		mEditor.commit();
	}

	/**存入set集合*/
	public void putStringSet(String key, Set<String> set) {
		mEditor.putStringSet(key, set);
		mEditor.commit();
	}

	/**取出Hierarchy Class*/
	public Set<String> getStringSet(String key,Set<String> defult) {
		return mSp.getStringSet(key,defult);
	}


}
