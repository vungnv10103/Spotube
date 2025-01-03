package com.chagnahnn.spotube.ui.model;

public class Comment {
    private String id;
    private String userId;
    private String multiMediaId; // TODO REMOVE
    private String content;
    private long likeCount;
    private long dislikeCount;
    private long replyCount;
    public boolean isShowDisLikeCount;
    private boolean isPinned;
    private boolean isParentCmt;
    private boolean isParentLike;

    private String createdAt;
    private String updatedAt;

    private boolean isExpanded;

    public Comment() {

    }

    public Comment(String id, String userId, String multiMediaId, String content, long likeCount, long dislikeCount, long replyCount, boolean isShowDisLikeCount, boolean isPinned, boolean isParentCmt, boolean isParentLike, String createdAt, String updatedAt, boolean isExpanded) {
        this.id = id;
        this.userId = userId;
        this.multiMediaId = multiMediaId;
        this.content = content;
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
        this.replyCount = replyCount;
        this.isShowDisLikeCount = isShowDisLikeCount;
        this.isPinned = isPinned;
        this.isParentCmt = isParentCmt;
        this.isParentLike = isParentLike;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isExpanded = isExpanded;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMultiMediaId() {
        return multiMediaId;
    }

    public void setMultiMediaId(String multiMediaId) {
        this.multiMediaId = multiMediaId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(long likeCount) {
        this.likeCount = likeCount;
    }

    public long getDislikeCount() {
        return dislikeCount;
    }

    public void setDislikeCount(long dislikeCount) {
        this.dislikeCount = dislikeCount;
    }

    public long getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(long replyCount) {
        this.replyCount = replyCount;
    }

    public boolean isShowDisLikeCount() {
        return isShowDisLikeCount;
    }

    public void setShowDisLikeCount(boolean showDisLikeCount) {
        isShowDisLikeCount = showDisLikeCount;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }

    public boolean isParentLike() {
        return isParentLike;
    }

    public void setParentLike(boolean parentLike) {
        isParentLike = parentLike;
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

    public boolean isParentCmt() {
        return isParentCmt;
    }

    public void setParentCmt(boolean parentCmt) {
        isParentCmt = parentCmt;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
}
