package com.meilicat.basedemo.activity.usercenter;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.RemoteViews;

import com.meilicat.basedemo.R;
import com.meilicat.basedemo.utils.ViewUtils;

import java.io.File;


/**
 * 广播接收器
 * 用于接收下载数据的进度百分比并显示在notifycation中。
 * @author lenovo
 */
public class ProgressShowReceiver extends BroadcastReceiver {// 继承自BroadcastReceiver的子类
    private static final int DOWNLOAD_NOTIFY_ID = 10;

	@Override
    public void onReceive(Context context, Intent intent) {// 重写onReceive方法
		NotificationManager manger;
		Notification notif;
		int totalSize;// 文件总长度
		int downloadedSize;// 已下载的文件长度
		int msg = intent.getIntExtra("msg", 0);
		totalSize = intent.getIntExtra("totalSize", 0);
		downloadedSize = intent.getIntExtra("downloadedSize", 0);
		manger = (NotificationManager) context
				.getSystemService(context.NOTIFICATION_SERVICE);
		notif = new Notification();
		PendingIntent pIntent = PendingIntent.getActivity(context, 0,
				new Intent(), 0);
		notif.contentView = new RemoteViews(context.getPackageName(),
				R.layout.show_progress);
		switch (msg) {
		case 0:
			Log.e("--download", "progress is 0!");
			notif.contentView.setProgressBar(R.id.progressBar, totalSize, 0,
					false);
			notif.contentView.setTextViewText(R.id.text, "0%");
			notif.icon = R.mipmap.ic_launcher;// 通知图标
			notif.tickerText = "下载提示";// 通知提示
			notif.when = System.currentTimeMillis();

			notif.contentIntent = pIntent; 
			notif.contentView.setProgressBar(R.id.progressBar, totalSize,
					downloadedSize, false);
			manger.notify(DOWNLOAD_NOTIFY_ID, notif);
			break;
		case 1:
			int count = downloadedSize * 100 / totalSize;

			notif.icon = R.mipmap.ic_launcher;// 通知图标
			notif.tickerText = "下载提示";// 通知提示
			notif.when = System.currentTimeMillis();

			notif.contentIntent = pIntent;

			notif.contentView.setTextViewText(R.id.text, count + "%");
			notif.contentView.setProgressBar(R.id.progressBar, totalSize,
					downloadedSize, false);
			Log.e("--download", "progress is " + count + "%");
			manger.notify(DOWNLOAD_NOTIFY_ID, notif);
			break;
		case 2:
			ViewUtils.showToast(context, "文件下载完成!");

			notif.contentView.setTextViewText(R.id.text, "文件下载完成!");
			notif.contentView.setProgressBar(R.id.progressBar, totalSize,
					downloadedSize, false);
			manger.notify(DOWNLOAD_NOTIFY_ID, notif);
			String sdcardRoot = Environment.getExternalStorageDirectory()
					.getAbsolutePath()
					+ File.separator
					+ DownloadService.FILENAME;
			Intent intent2 = new Intent(Intent.ACTION_VIEW);
			intent2.setDataAndType(Uri.fromFile(new File(sdcardRoot)),
					"application/vnd.android.package-archive");
			intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Log.e("--download", "down load is complete!!!");
			context.startActivity(intent2);
			break;
        }
    }
}

