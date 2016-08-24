package com.meilicat.basedemo.factory;


public class ThreadPoolProxyFactory {

	static ThreadPoolProxy	mNormalThreadPoolProxy;	// 只需创建一次即可


	/**
	 * 返回普通线程池的代理
	 * 双重检查加锁,保证只有第一次实例化的时候才启用同步机制,提高效率
	 * @return
	 */
	public static ThreadPoolProxy createNormalThreadPoolProxy() {
		if (mNormalThreadPoolProxy == null) {
			synchronized (ThreadPoolProxyFactory.class) {
				if (mNormalThreadPoolProxy == null) {
					mNormalThreadPoolProxy = new ThreadPoolProxy(3, 3, 3000);
				}
			}
		}
		return mNormalThreadPoolProxy;
	}


}
