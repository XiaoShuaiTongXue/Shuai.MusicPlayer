package com.shuai.musicplayer2.domain;

import java.util.List;

public class MusicListInfo {

    @Override
    public String toString() {
        return "MusicListInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", mvid=" + mvid +
                ", fee=" + fee +
                ", albumName='" + albumName + '\'' +
                ", artistsName=" + artistsName +
                ", picUrl='" + picUrl + '\'' +
                ", url='" + url + '\'' +
                ", alia=" + alia +
                '}';
    }

    public void setMusicInfo(MusicList.ResultBean.SongsBean songsBean){
        this.id = Integer.toString(songsBean.getId());
        this.name = songsBean.getName();
        this.mvid = songsBean.getMvid();
        this.fee = songsBean.getFee();
        this.albumName = songsBean.getAlbum().getName();
        this.artistsName = songsBean.getArtists();
        this.alia = songsBean.getAlias();
    }

    public void setPicUrl(String picUrl){
        this.picUrl = picUrl;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    //音乐ID
    private String id;
    //音乐名
    private String name;
    //mvID
    private int mvid;
    //是否付费
    private int fee;
    //专辑名
    private String albumName;
    //作家名
    private List<MusicList.ResultBean.SongsBean.ArtistsBean> artistsName;

    public String getAlbumName() {
        return albumName;
    }

    public List<MusicList.ResultBean.SongsBean.ArtistsBean> getArtistsName() {
        return artistsName;
    }

    //音乐图片地址
    private String picUrl;
    //音乐播放地址
    private String url;
    //音乐附件信息
    private List<String> alia;

    public List<String> getAlia() {
        return alia;
    }

    public String getId() {
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


    public String getPicUrl() {
        return picUrl;
    }


    public String getUrl() {
        return url;
    }
}
