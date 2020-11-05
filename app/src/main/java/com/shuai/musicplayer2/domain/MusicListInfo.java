package com.shuai.musicplayer2.domain;

import java.util.List;

public class MusicListInfo {

    public void setMusicListInfo(MusicInfo.SongsBean songsBean){
        this.id = songsBean.getId();
        this.name = songsBean.getName();
        this.mvid = songsBean.getMv();
        this.fee = songsBean.getFee();
        this.albumName = songsBean.getAl().getName();
        this.artistsName = songsBean.getAr().get(0).getName();
        this.picUrl = songsBean.getAl().getPicUrl();
        this.alia = songsBean.getAlia();
    }

    //音乐ID
    private int id;
    //音乐名
    private String name;
    //mvID
    private int mvid;
    //是否付费
    private int fee;
    //专辑名
    private String albumName;
    //作家名
    private String artistsName;
    //音乐图片地址
    private String picUrl;
    //音乐播放地址
    private String url;
    //音乐附件信息
    private List<String> alia;

    public List<String> getAlia() {
        return alia;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getMvid() {
        return mvid;
    }

    public int getFee() {
        return fee;
    }

    public String getalbumName() {
        return albumName;
    }

    public String getartistsName() {
        return artistsName;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
