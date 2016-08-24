package com.meilicat.basedemo.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.meilicat.basedemo.protocol.BaseProtocol;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by cj on 2016/1/20.
 *
 */
public class HttpManager<F> {
    private Context mContext;
    private OkHttpClient client;
    private Call call;

    private static final int SUCCESS = 1;
    private static final int FAIL = -1;

    private static final int TIME_OUT_CODE = 500;
    private static final int TIME_OUT = 5;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png; charset=utf-8");
    private final SPUtils mSpUtils ;

    public  String cookie = "";
    public HttpManager(Context context){
        mContext = context;
        mSpUtils  = new SPUtils(UIUtils.getContext());

        cookie = mSpUtils.getString("cookie","");
        if (client == null){

            client = new OkHttpClient();
            int cacheSize = 3*1024*1024;
            File cacheDirectory = new File(UIUtils.getContext().getCacheDir().getAbsolutePath(),"json");
            if (!cacheDirectory.exists()){
               cacheDirectory.mkdirs();
            }
            Cache cache = new Cache(cacheDirectory,cacheSize);

            client.newBuilder()
                    .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .cache(cache);

        }

    }



    private void post(String url,String json){
        if (!checkNetwork()){
            loadDataFromCache(url);
            return;
        }

        RequestBody body = RequestBody.create(JSON, json);


        if (TextUtils.isEmpty(cookie)){
            return;
        }
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendFailMessage();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    sendSuccessMessage(response.body().string());
                } else if (response.code() == TIME_OUT_CODE) {
                    sendTimeOutMessage();
                }
            }
        });
    }

    public String postSync(String url,String json) throws IOException {

        RequestBody body = RequestBody.create(JSON, json);


        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("cookie", cookie)
                .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    public Response postLogin(String url,Map map) throws IOException {
        if (!checkNetwork()){
            UIUtils.postTaskSafely(new Runnable() {
                @Override
                public void run() {
                    T.showShort(mContext, "检测到手机网络未开启");
                }
            });
            return null;
        }
        FormBody.Builder builder = new FormBody.Builder();

        Set<Object> keySet = map.keySet();
        for (Object obj : keySet) {
            try {
                builder.add(obj.toString(), URLDecoder.decode(String.valueOf(map.get(obj)), "UTF-8"));

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        FormBody formBody = builder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        call = client.newCall(request);

        Response response = call.execute();

        String cookies  = response.header("Set-Cookie");//这个地方有问题，获取到的cookie不完整

      /*  Headers headers = response.networkResponse().headers();
        List<Cookie> cookies1 = Cookie.parseAll(request.url(), headers);

        StringBuilder buffer = new StringBuilder();

        for (int i = 0; i < cookies1.size(); i++) {
            if (i == cookies1.size()-1){
                buffer.append(cookies1.get(i).toString());

            }else {
                String value = cookies1.get(i).value();
                if (TextUtils.isEmpty(value)){
                    LogUtils.i("跳过了-----"+value);
                    continue;
                }
                buffer.append(cookies1.get(i).toString() + ",");
            }
        }*/

       /* if (response.isSuccessful()){

            mSpUtils.putString("cookie", cookies);
            cookie = mSpUtils.getString("cookie","");
//            LogUtils.i("cookie--------------------" + cookie);

        }*/

        return response;
    }

    /**
     * 异步map的post请求
     * */
    public void post(String url, Map map) {
        if (!checkNetwork()){
            String s = mapToString(map);
            loadDataFromCache(url+s);
            T.showShort(mContext,"检测到手机网络未开启");
            return;
        }

        FormBody.Builder builder = new FormBody.Builder();
        Set<Object> keySet = map.keySet();
        for (Object obj : keySet) {
            try {
                builder.add(obj.toString(), URLDecoder.decode(String.valueOf(map.get(obj)), "UTF-8"));

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        FormBody formBody = builder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .addHeader("cookie", cookie)
                .build();

        call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendFailMessage();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    sendSuccessMessage(response.body().string());
                } else if (response.code() == TIME_OUT_CODE) {
                    sendTimeOutMessage();
                }
            }

        });
    }



    public void get(final String url) {
        if (!checkNetwork()) {
            T.showShort(mContext,"检测到手机网络未开启");
            loadDataFromCache(url);
            return;
        }

        cookie = mSpUtils.getString("cookie","");
        Request request = new Request.Builder()
                .url(url)
                .addHeader("cookie",cookie)
                .build();
        call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                sendFailMessage();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtils.i("order-------"+response);
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    saveDataOnCache(url, result);
                    sendSuccessMessage(result);
                } else if (response.code() == TIME_OUT_CODE) {
                    sendTimeOutMessage();
                }
            }
        });
    }

    private String loadDataFromCache(String url) {
        String data = BaseProtocol.getInstance().loadData(url);
        if (!TextUtils.isEmpty(data)){
            onSuccess(data);
            return data;
        }else {
            onFail();
            return "";
        }

    }

    private void saveDataOnCache(String url,String result){
//        BaseProtocol.getInstance().saveData(url,result);
    }

    public String run(String url) throws IOException {

        if (!checkNetwork()) {
            T.showShort(mContext,"检测到手机网络未开启");
            String s = loadDataFromCache(url);
            if (!TextUtils.isEmpty(s)){
                return s;
            }
            return null;
        }
        cookie = mSpUtils.getString("cookie","");
        LogUtils.i("--------------"+cookie);
        Request request = new Request.Builder().url(url).addHeader("cookie", cookie).get().build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful()){
            String result = response.body().string();

            saveDataOnCache(url,result);
            return result;
        }else{
            throw new IOException("Unexpected code " + response);
        }
    }
    public String run(String url,Map map) throws IOException {

        if (!checkNetwork()) {
            String s = mapToString(map);
            String s1 = loadDataFromCache(url + s);
            if (!TextUtils.isEmpty(s1)){
                return s1;
            }
            T.showShort(mContext,"检测到手机网络未开启");
            return null;
        }

        url = url + "?";
        TreeMap<String, String> signMap = new TreeMap<>();

        signMap.put("method", "get");

        Iterator it = map.keySet().iterator();

        while (it.hasNext()) {
            String key = (String) it.next();
            String value = map.get(key).toString();
            signMap.put(key, value);
        }
        Map paramMap = new HashMap();

        StringBuffer param = new StringBuffer();
        Iterator it1 = paramMap.keySet().iterator();
        while (it1.hasNext()) {
            String key = (String) it1.next();
            String value = paramMap.get(key).toString();
            param.append(key).append("=").append(value).append("&");
        }
        param.deleteCharAt(param.length() - 1);

        Request request = new Request.Builder().url(url + param).addHeader("cookie", cookie).get().build();

        call = client.newCall(request);

        Response response = call.execute();
        if (response.isSuccessful()){
            String result = response.body().string();
            saveDataOnCache(url,result);
            return response.body().string();
        }else{
            throw new IOException("Unexpected code " + response);
        }

    }

    public void get(String url, Map map) {
        if (!checkNetwork()) {
            T.showShort(mContext,"检测到手机网络未开启");
            String s = mapToString(map);
            loadDataFromCache(url + s);
            return;
        }

        final  String url1 = url + "?";
        TreeMap<String, String> signMap = new TreeMap<>();

        signMap.put("method", "get");

        Iterator it = map.keySet().iterator();

        while (it.hasNext()) {
            String key = (String) it.next();
            String value = map.get(key).toString();
            signMap.put(key, value);
        }

        StringBuffer param = new StringBuffer();
        Iterator it1 = signMap.keySet().iterator();
        while (it1.hasNext()) {
            String key = (String) it1.next();
            String value = signMap.get(key).toString();
            param.append(key).append("=").append(value).append("&");
        }
        param.deleteCharAt(param.length() - 1);

        Request request = new Request.Builder().header("Cache-Control", "max-age=3600").addHeader("cookie", cookie).url(url1 + param).build();

        call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendFailMessage();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    saveDataOnCache(url1,result);
                    sendSuccessMessage(result);
                } else if (response.code() == TIME_OUT_CODE) {
                    sendTimeOutMessage();
                }
            }

        });
    }


    public void uploadImage(String url, String imagePath) throws Exception {
        if (!checkNetwork()) {
            T.showShort(mContext, "检测到手机网络未开启");
            return;
        }

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);//设置为表单提前

     //    //    MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);
//        Set<Object> keySet = paramMap.keySet();
//        for (Object obj : keySet) {
//            builder.addFormDataPart(obj.toString(), URLDecoder.decode(String.valueOf(paramMap.get(obj))));
//        }
//        builder.addPart(
//                Headers.of("Content-Disposition", "form-data; name=\"" + imageKey + "\";filename=\"" + imagePath + "\""),
//                RequestBody.create(MEDIA_TYPE_PNG, new File(imagePath)));

        File uploadFile = PhotoUtil.scal(imagePath);

        builder.addPart(
                Headers.of("Content-Disposition", "form-data; name=file;filename=\"" + imagePath + "\""),
                RequestBody.create(MEDIA_TYPE_PNG, uploadFile));

//       builder.addFormDataPart("file", null, RequestBody.create(MEDIA_TYPE_PNG, new File(imagePath)));
        Request request = new Request.Builder()
                .url(url)
                .addHeader("cookie",cookie)
                .post(builder.build())
                .build();

        call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendFailMessage();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    sendSuccessMessage(response.body().string());
                }
            }
        });
    }



    private void sendTimeOutMessage() {
        Message msg = Message.obtain();
        msg.what = TIME_OUT;
        handler.sendMessage(msg);

    }
    private void sendSuccessMessage(String response) {
        Message msg = Message.obtain();
        msg.what = SUCCESS;
        msg.obj = response;
        handler.sendMessage(msg);

    }

    private void sendFailMessage() {
        Message msg = Message.obtain();
        msg.what = FAIL;
        handler.sendMessage(msg);
    }

    public void cancel() {
        if(call != null){
            call.cancel();
        }
    }


    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == SUCCESS) {
                if (msg.obj != null) {
                    //把数据解析了？再弄？
                    onSuccess(msg.obj);
                } else {
                    T.showShort(mContext, "服务器存在异常");
                    // if (multiStateView != null && !isShow)
                    //multiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
                    return;
                }
            } else if (msg.what == FAIL) {
                //ToastManager.getInstance(context).shortToast("请求失败");

                onFail();

                // if (multiStateView != null && !isShow)
                // multiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);

            } else if (msg.what == TIME_OUT) {
                onTimeOut();
                // if (dialog != null) dialog.dismiss();
                // if (multiStateView != null && !isShow)
                //multiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
            }
        }
    };

    protected  void onFail(){

    }
    protected  void onSuccess(Object obj){

    }

    protected void onTimeOut(){

    }


    private boolean checkNetwork() {
        ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info == null || !info.isConnected()) {
            return false;
        }
        return true;
    }

    private String mapToString(Map<String,String> map){
        StringBuffer buf = new StringBuffer();
        if (map == null)
            return "";
        for (HashMap.Entry<String,String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            buf.append(key + "=" + value);
            buf.append("&");
        }

        String result = buf.toString();
        if (result.endsWith("&")){
            result = result.substring(0, result.lastIndexOf("&"));
        }
       return result;
    }

}
