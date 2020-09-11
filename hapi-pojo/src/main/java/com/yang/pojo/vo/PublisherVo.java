package com.yang.pojo.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PublisherVo {

    public UsersVo publisher;

    public boolean userLikeVideo;

    public UsersVo getPublisher() {
        return publisher;
    }

    public void setPublisher(UsersVo publisher) {
        this.publisher = publisher;
    }

    public boolean isUserLikeVideo() {
        return userLikeVideo;
    }

    public void setUserLikeVideo(boolean userLikeVideo) {
        this.userLikeVideo = userLikeVideo;
    }
}