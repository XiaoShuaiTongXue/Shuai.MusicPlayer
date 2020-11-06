package com.shuai.musicplayer2.utils;

import android.os.Message;
import android.util.Log;

import com.shuai.musicplayer2.api.Api;
import com.shuai.musicplayer2.control.Main;
import com.shuai.musicplayer2.domain.MusicInfo;
import com.shuai.musicplayer2.domain.MusicList;
import com.shuai.musicplayer2.domain.MusicListInfo;
import com.shuai.musicplayer2.domain.MusicUrl;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class GetMenuList {

    public static List<com.shuai.musicplayer2.domain.MusicListInfo> sMusicListInfo;
    public static int count;
    private final Retrofit mRetrofit;
    private final Api mApi;
    private final Call<MusicList> mMusicListTask;
    private MusicList mMusicList;
    private static final String TAG = "GetMusicListInfo";
    private Call<MusicInfo> mMusicInfoTask;
    private Call<MusicUrl> mMusicUrlTask;
    private static String mKeyword;
    private List<String> mSongsId;

    /**
     * 根据关键词获得音乐列表，并将ID信息存放到数组中
     * @param keyWord 关键词
     */
    public GetMenuList(String keyWord){
        if (!(mKeyword!=null&&mKeyword == keyWord)){
            count = 10;
            mRetrofit = RetrofitManager.getRetrofit();
            mApi = mRetrofit.create(Api.class);
            mMusicListTask = mApi.getMusicList(keyWord);
            mMusicListTask.enqueue(new Callback<MusicList>() {
                @Override
                public void onResponse(Call<MusicList> call, Response<MusicList> response) {
                    if(response.code()== HttpURLConnection.HTTP_OK){
                        mMusicList = response.body();
                        List<MusicList.ResultBean.SongsBean> songs = mMusicList.getResult().getSongs();
                        //将取出的id存放到列表中，将具体信息查询独立出来，方便本地数据库的查询
                        saveInList(songs);
                        //根据列表的信息更新每一条信息的地址
                        updateInfo();
                        Message message = Message.obtain();
                        message.what = 100;
                        Main.mHandler.sendMessage(message);
                    }
                }

                @Override
                public void onFailure(Call<MusicList> call, Throwable t) {
                    Log.i(TAG,t.toString());
                }
            });
        }else {
            mRetrofit = null;
            mApi = null;
            mMusicListTask = null;
        }
    }

    private void saveInList(List<MusicList.ResultBean.SongsBean> songs) {
        //将存放数据的数组清空，防止内存泄露
        if (mSongsId != null){
            mSongsId.clear();
            mSongsId = null;
        }
        mSongsId = new ArrayList<String>();
        for (MusicList.ResultBean.SongsBean song : songs){
            mSongsId.add(Integer.toString(song.getId()));
        }
    }

    /**
     * 更新每个列表的pic地址和Url地址
     * 执行此方法必须声明count的值
     */
    private void updateInfo() {
        if (sMusicListInfo != null) {
            sMusicListInfo.clear();
            sMusicListInfo = null;
        }
        sMusicListInfo = new ArrayList<com.shuai.musicplayer2.domain.MusicListInfo>();
        for (int i = 0; i < count ; i++) {
            com.shuai.musicplayer2.domain.MusicListInfo mMusicListInfo = new com.shuai.musicplayer2.domain.MusicListInfo();
            String musicId = mSongsId.get(i);
            mMusicInfoTask = mApi.getMusicInfo(musicId);
            mMusicInfoTask.enqueue(new Callback<MusicInfo>() {
               @Override
               public void onResponse(Call<MusicInfo> call, Response<MusicInfo> response) {
                   if (response.code() == HttpURLConnection.HTTP_OK){
                       mMusicListInfo.setMusicInfo(response.body().getSongs().get(0));
                   }
               }

               @Override
               public void onFailure(Call<MusicInfo> call, Throwable t) {
                   Log.i(TAG,t.toString());
               }
            });
            mMusicUrlTask = mApi.getMusicUrl(musicId);
            mMusicUrlTask.enqueue(new Callback<MusicUrl>() {
               @Override
               public void onResponse(Call<MusicUrl> call, Response<MusicUrl> response) {
                   if (response.code() == HttpURLConnection.HTTP_OK){
                       mMusicListInfo.setUrl(response.body().getData().get(0).getUrl());
                   }
               }

               @Override
               public void onFailure(Call<MusicUrl> call, Throwable t) {
                   Log.i(TAG,t.toString());
               }
            });
            sMusicListInfo.add(mMusicListInfo);
        }
    }
}
