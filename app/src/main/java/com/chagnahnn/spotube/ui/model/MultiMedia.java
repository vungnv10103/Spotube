package com.chagnahnn.spotube.ui.model;

import androidx.annotation.NonNull;

import java.util.List;

public class MultiMedia {
    private String id;
    private String title;
    private String artist;
    private List<Lyric> lyrics;
    private String compose;
    private String artworkUrl;
    private String audioUrl;
    private String videoUrl;
    private Long likesCount;
    private Long dislikesCount;
    private Long viewsCount;
    private boolean enableShowComment;
    private boolean enableShowDislike;
    private String createdAt;
    private String updatedAt;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompose() {
        return compose;
    }

    public void setCompose(String compose) {
        this.compose = compose;
    }

    public String getArtworkUrl() {
        return artworkUrl;
    }

    public void setArtworkUrl(String artworkUrl) {
        this.artworkUrl = artworkUrl;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Long getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(Long likesCount) {
        this.likesCount = likesCount;
    }

    public Long getDislikesCount() {
        return dislikesCount;
    }

    public void setDislikesCount(Long dislikesCount) {
        this.dislikesCount = dislikesCount;
    }

    public Long getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(Long viewsCount) {
        this.viewsCount = viewsCount;
    }

    public boolean isEnableShowComment() {
        return enableShowComment;
    }

    public void setEnableShowComment(boolean enableShowComment) {
        this.enableShowComment = enableShowComment;
    }

    public boolean isEnableShowDislike() {
        return enableShowDislike;
    }

    public void setEnableShowDislike(boolean enableShowDislike) {
        this.enableShowDislike = enableShowDislike;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Lyric> getLyrics() {
        return lyrics;
    }

    public void setLyrics(List<Lyric> lyrics) {
        this.lyrics = lyrics;
    }

    @NonNull
    @Override
    public String toString() {
        return "MultiMedia{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", lyrics=" + lyrics +
                ", compose='" + compose + '\'' +
                ", artworkUrl='" + artworkUrl + '\'' +
                ", audioUrl='" + audioUrl + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", likesCount=" + likesCount +
                ", dislikesCount=" + dislikesCount +
                ", viewsCount=" + viewsCount +
                ", enableShowComment=" + enableShowComment +
                ", enableShowDislike=" + enableShowDislike +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}
