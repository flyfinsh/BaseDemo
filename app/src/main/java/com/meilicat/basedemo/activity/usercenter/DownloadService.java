package com.meilicat.basedemo.activity.usercenter;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 后台服务，专用于下载。
 *
 * @author lenovo
 *
 */
public class DownloadService extends Service {
    // 要下载的文件链接
    private String urlString;
    public static final String FILENAME = "recruit.apk";
    private int totalSize;// 文件总长度
    private int downloadedSize;// 已下载的文件长度
    private int ncount = 0;
    private int count = 0;
    private Intent intent;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent myintent, int startId) {
        super.onStart(myintent, startId);
        if (myintent != null)
            urlString = myintent.getStringExtra("url");
        this.intent = new Intent("com.dataprogress.doService");
        new Thread()
        {
            @Override
            public void run() {
                downloadFile();
            }
        }.start();
    }

    public void downloadFile() {
        try {

            Log.e("--download", "begin down load file!!");
            URL url = new URL(urlString);
            // 创建一个下载通道
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            // sdcard
            String sdcardRoot = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
            // 创建要保存的文件,保存在根目录
            File file = new File(sdcardRoot, FILENAME);

            // 用来将下载的数据写入file中
            FileOutputStream fileOutput = new FileOutputStream(sdcardRoot + FILENAME);
            // 用来读取来自网络的数据
            InputStream inputStream = urlConnection.getInputStream();

            totalSize = urlConnection.getContentLength();// 将要下载的文件的总长度

            byte[] buffer = new byte[1024];// 每次读取的长度，此处为1k
            int bufferLength = 0;// 用来临时储存buffer的长度
            sendMessage(0);
            // 通过读取输入流中的数据，用输出流写到本地文件中
            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fileOutput.write(buffer, 0, bufferLength);//
                downloadedSize += bufferLength;// 已下载的文件的长度
                // 此处发送消息给handler以改变进度条
                sendMessage(1);
            }
            sendMessage(2);
            fileOutput.close();
            inputStream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 当收到线程中发来的消息时，handler开始执行
    private Handler handler = new Handler()
    {
        public void handleMessage(Message msg) {

            if (!Thread.currentThread().isInterrupted()) {
                Log.e("--download", "thread is not interrupted");
                switch (msg.what) {
                case 0:// 初始化
                    count = 0;
                    ncount = 0;
                    downloadedSize = 0;
                    intent.putExtra("msg", msg.what);
                    intent.putExtra("downloadedSize", downloadedSize);
                    intent.putExtra("totalSize", totalSize);
                    sendBroadcast(intent);
                    break;
                case 1:
                    count = downloadedSize * 100 / totalSize;
                    while (count - ncount >= 5) {// 当每增加5%时，改变通知栏的进度条(太频繁的通知将会很卡)
                        ncount = count;
                        intent.putExtra("msg", msg.what);
                        intent.putExtra("totalSize", totalSize);
                        intent.putExtra("downloadedSize", downloadedSize);
                        sendBroadcast(intent);
                    }
                    break;
                case 2:
                    intent.putExtra("msg", msg.what);// 通知给通知栏
                    sendBroadcast(intent);
                    break;
                }
            }
        };
    };

    public void sendMessage(int flag) {// 发消息给handler的方法
        Message msg = new Message();
        msg.what = flag;
        handler.sendMessage(msg);
    }

}
