package com.android.singledownload;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

public class DownloadInfo {
    private Long id;
    private String url;
    private long total;
    private long progress;
    private String fileName;
    private int fileImg;
    private boolean isStop;
    public String getUrl() {
        return url;
    }

    public int getFileImg() {
        return fileImg;
    }

    public void setFileImg(int fileImg) {
        this.fileImg = fileImg;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getProgress() {
        return progress;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isStop() {
        return isStop;
    }

    public void setStop(boolean stop) {
        isStop = stop;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }
}