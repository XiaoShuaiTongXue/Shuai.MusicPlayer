package com.shuai.musicplayer2.utils;

import android.util.Log;

import com.shuai.musicplayer2.api.Api;
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

public class GetMusicListInfo {

    public static List<MusicListInfo> sMusicListInfo;
    public static int count;
    private final Retrofit mRetrofit;
    private final Api mApi;
    private final Call<MusicList> mMusicListTask;
    private MusicList mMusicList;
    private static final String TAG = "GetMusicListInfo";
    private List<MusicList.ResultBean.SongsBean> mSongs;
    private Call<MusicInfo> mMusicInfoTask;
    private Call<MusicUrl> mMusicUrlTask;
    private static String mKeyword;

    //根据关键词获得十条音乐信息
    public GetMusicListInfo(String keyWord){
        if (!(mKeyword!=null&&mKeyword == keyWord)){
            sMusicListInfo = new ArrayList<MusicListInfo>();
            mRetrofit = RetrofitManager.getRetrofit();
            mApi = mRetrofit.create(Api.class);
            mMusicListTask = mApi.getMusicList(keyWord);
            mMusicListTask.enqueue(new Callback<MusicList>() {
                @Override
                public void onResponse(Call<MusicList> call, Response<MusicList> response) {
                    if(response.code()== HttpURLConnection.HTTP_OK){
                        mMusicList = response.body();
                        mSongs = mMusicList.getResult().getSongs();
                        //根据列表的信息更新每一条信息的地址
                        updateInfo();
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

    //更新每个列表的pic地址和Url地址
    private void updateInfo() {
        for (int i = 0; i < count ; i++) {
           String musicId = Integer.toString(mSongs.get(0).getId()) ;
           MusicListInfo mMusicListInfo = new MusicListInfo();
           mMusicInfoTask = mApi.getMusicInfo(musicId);
           mMusicInfoTask.enqueue(new Callback<MusicInfo>() {


               @Override
               public void onResponse(Call<MusicInfo> call, Response<MusicInfo> response) {
                   if (response.code() == HttpURLConnection.HTTP_OK){
                       mMusicListInfo.setMusicListInfo(response.body().getSongs().get(0));
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
