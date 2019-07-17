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
    @ColumnInfo(name = "file_name")
    private String fileName;
    @ColumnInfo(name = "file_show_name")
    private String fileShowName;

    @ColumnInfo(name = "file_img")
    private int fileImg;

    @ColumnInfo(name = "download_status")
    private int downloadStatus = DownloadStatus.statusPause;

    @ColumnInfo(name = "download_length")
    private long downloadLength;

    @ColumnInfo(name = "total_length")
    private long totalLength;
    public String getFileShowName() {
        return fileShowName;
    }

    public void setFileShowName(String fileShowName) {
        this.fileShowName = fileShowName;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getTotalLength() {
        return totalLength;
    }

    public void setTotalLength(long totalLength) {
        this.totalLength = totalLength;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public DownloadInfo(String fileName, int downloadStatus, long downloadLength, long totalLength) {
        this.fileName = fileName;
        this.downloadStatus = downloadStatus;
        this.downloadLength = downloadLength;
        this.totalLength = totalLength;
    }

    public DownloadInfo() {

    }

    public DownloadInfo(String fileName) {
        this.fileName = fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(int downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public long getDownloadLength() {
        return downloadLength;
    }

    public void setDownloadLength(long downloadLength) {
        this.downloadLength = downloadLength;
    }


}
