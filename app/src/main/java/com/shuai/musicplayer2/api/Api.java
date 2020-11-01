package com.shuai.musicplayer2.api;


import com.shuai.musicplayer2.domain.MusicList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Api {
    @GET("/search")
    Call<MusicList> getMusicList(@Query("keywords") String keywords);
}
