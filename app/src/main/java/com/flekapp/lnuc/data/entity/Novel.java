package com.flekapp.lnuc.data.entity;

import java.util.Date;

public class Novel {
    public enum Status {
        ONGOING(0), COMPLETED(1);

        private int code;

        Status(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public static Status getByCode(int code) {
            switch (code) {
                case 1:
                    return Status.COMPLETED;
                default:
                    return Status.ONGOING;
            }
        }
    }

    private Integer id;
    private String name;
    private String shortName;
    private Source source;
    private String url;
    private String imageUrl;
    private Status status;
    private String lastChapterNumber;
    private String lastChapterTitle;
    private Date lastUpdate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getLastChapterNumber() {
        return lastChapterNumber;
    }

    public void setLastChapterNumber(String lastChapterNumber) {
        this.lastChapterNumber = lastChapterNumber;
    }

    public String getLastChapterTitle() {
        return lastChapterTitle;
    }

    public void setLastChapterTitle(String lastChapterTitle) {
        this.lastChapterTitle = lastChapterTitle;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
