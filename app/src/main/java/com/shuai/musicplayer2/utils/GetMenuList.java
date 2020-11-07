package com.shuai.musicplayer2.utils;

import android.os.Message;
import android.util.Log;

import com.shuai.musicplayer2.api.Api;
import com.shuai.musicplayer2.control.Result;
import com.shuai.musicplayer2.domain.MusicInfo;
import com.shuai.musicplayer2.domain.MusicList;
import com.shuai.musicplayer2.domain.MusicUrl;
import com.shuai.musicplayer2.domain.TopMusicList;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class GetMenuList {

    public static List<com.shuai.musicplayer2.domain.MusicListInfo> sMusicListInfo;
    public static int sCount;
    private  Retrofit mRetrofit;
    private  Api mApi;
    private  Call<MusicList> mMusicListTask;
    private static final String TAG = "GetMusicListInfo";
    private Call<MusicInfo> mMusicInfoTask;
    private Call<MusicUrl> mMusicUrlTask;
    private List<String> mSongsId;
    private int mNum;
    private Call<TopMusicList> mTopMusicListTask;

    /**
     * 初始化播放菜单的音乐
     * @param count 菜单音乐的个数
     */
    public GetMenuList(int count){
        sCount = 10;
        mRetrofit = RetrofitManager.getRetrofit();
        mApi = mRetrofit.create(Api.class);
    }

    /**
     * 根据关键词获得音乐列表，并将ID信息存放到数组中
     * @param keyWord 关键词
     */
    public void getMusicList(String keyWord){
        mMusicListTask = mApi.getMusicList(keyWord);
        mMusicListTask.enqueue(new Callback<MusicList>() {
            @Override
            public void onResponse(Call<MusicList> call, Response<MusicList> response) {
                if(response.code()== HttpURLConnection.HTTP_OK){
                    List<MusicList.ResultBean.SongsBean> songs = response.body().getResult().getSongs();
                    //将取出的id存放到列表中，将具体信息查询独立出来，方便本地数据库的查询
                    saveMusicListId(songs);
                    //根据列表的信息更新每一条信息的地址
                    updateInfo();
                }
            }

            @Override
            public void onFailure(Call<MusicList> call, Throwable t) {
                Log.i(TAG,t.toString());
            }
        });
    }

    /**
     * 根据模块ID，并将模块中音乐ID存放到数组中
     * @param topListID 模块ID
     */
    public void getTopList(String topListID){
        mTopMusicListTask = mApi.getTopMusicList(topListID);
        mTopMusicListTask.enqueue(new Callback<TopMusicList>() {
            @Override
            public void onResponse(Call<TopMusicList> call, Response<TopMusicList> response) {
                if(response.code()== HttpURLConnection.HTTP_OK){
                    List<TopMusicList.PlaylistBean.TrackIdsBean> trackIds = response.body().getPlaylist().getTrackIds();
                    saveTopListId(trackIds);
                    updateInfo();
                }
            }

            @Override
            public void onFailure(Call<TopMusicList> call, Throwable t) {

            }
        });
    }

    public void getLikeList(int count){

    }

    private void saveTopListId(List<TopMusicList.PlaylistBean.TrackIdsBean> trackIds) {
        //将存放数据的数组清空，防止内存泄露
        if (mSongsId != null){
            mSongsId.clear();
            mSongsId = null;
        }
        mSongsId = new ArrayList<String>();
        for(TopMusicList.PlaylistBean.TrackIdsBean trackIdsBean :trackIds){
            mSongsId.add(trackIdsBean.getId());
        }
        trackIds.clear();
        trackIds = null;
    }

    private void saveMusicListId(List<MusicList.ResultBean.SongsBean> songs) {
        //将存放数据的数组清空，防止内存泄露
        if (mSongsId != null){
            mSongsId.clear();
            mSongsId = null;
        }
        mSongsId = new ArrayList<String>();
        for (int i = 0;i < 30;i++){
            mSongsId.add(songs.get(i).getId());
        }
        songs.clear();
        songs = null;
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
        mNum = 0;
        for (int i = 0; i < sCount; i++) {
            com.shuai.musicplayer2.domain.MusicListInfo mMusicListInfo = new com.shuai.musicplayer2.domain.MusicListInfo();
            String musicId = mSongsId.get(i);
            mMusicInfoTask = mApi.getMusicInfo(musicId);
            mMusicInfoTask.enqueue(new Callback<MusicInfo>() {
               @Override
               public void onResponse(Call<MusicInfo> call, Response<MusicInfo> response) {
                   if (response.code() == HttpURLConnection.HTTP_OK){
                       mMusicListInfo.setMusicInfo(response.body().getSongs().get(0));
                       mNum +=1;
                       if(mNum == sCount){
                           Message message = Message.obtain();
                           message.what = 100;
                           Result.mHandler.sendMessage(message);
                       }
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
