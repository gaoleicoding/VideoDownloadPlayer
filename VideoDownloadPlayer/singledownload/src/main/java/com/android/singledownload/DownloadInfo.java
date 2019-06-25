package com.android.singledownload;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "download", indices = {})
public class DownloadInfo {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;
    @ColumnInfo(name = "url")
    private String url;
    @ColumnInfo(name = "total_length")
    private long totalLength;
    @ColumnInfo(name = "progress")
    private long progress;
    @ColumnInfo(name = "file_name")
    private String fileName;
    @ColumnInfo(name = "file_img")
    private int fileImg;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTotalLength() {
        return totalLength;
    }

    public void setTotalLength(long totalLength) {
        this.totalLength = totalLength;
    }

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
