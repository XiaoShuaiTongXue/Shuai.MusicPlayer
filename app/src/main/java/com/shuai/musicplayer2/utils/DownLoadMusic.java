package com.shuai.musicplayer2.utils;

import android.util.Log;

import com.shuai.musicplayer2.domain.MusicListInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class DownLoadMusic {

    private static final String TAG = "DownLoadMusic";
    private final String mUrl;
    private final String mFileName;

    public DownLoadMusic(int position){
        MusicListInfo info = MenuList.sMusicListInfo.get(position);
        mUrl = info.getUrl();
        mFileName = info.getName();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(3000, TimeUnit.MILLISECONDS)
                .build();
        Request request = new Request.Builder()
                .get()
                .url(mUrl)
                .build();
        //用浏览器创建任务
        Call task = client.newCall(request);
        task.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG,e.toString());
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                if(response.code()== HttpURLConnection.HTTP_OK){
                    downLoad(response.body());
                }
            }
        });

    }

    private void downLoad(ResponseBody body) {
        InputStream mInputStream = body.byteStream();
        try {
            // TODO: 2020/11/8 添加外部存储卡的地址
            File mFile = new File("/data/data/com.shuai.retrofit/images/"+mFileName);
            Log.i(TAG,mFile.getPath());
            FileOutputStream fos = new FileOutputStream(mFile);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = mInputStream.read(buffer))!=-1){
                fos.write(buffer,0,len);
            }
            fos.close();
            // TODO: 2020/11/8 将主界面的视图控制器传递过来
            Log.i(TAG,"下载成功");
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG,e.toString());
        }
    }
}
