package com.android.singledownload.db;


import androidx.room.*;
import com.android.singledownload.DownloadInfo;

import java.util.List;

/**
 * Created by Allen on 2018/4/16/016.
 */

@Dao
public interface DownloadDao {

    //所有的CURD根据primary key进行匹配
    //------------------------query------------------------
    @Query("SELECT * FROM download")
    List<DownloadInfo> getAll();

    @Query("SELECT * FROM download WHERE file_name IN (:fileNames)")
    List<DownloadInfo> loadAllByFileNames(int[] fileNames);

    @Query("SELECT * FROM download WHERE file_name = :fileName")
    DownloadInfo findByFileName(String fileName);

    //-----------------------insert----------------------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(DownloadInfo info);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertAll(DownloadInfo... infos);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertAll(List<DownloadInfo> infos);

    //---------------------update------------------------

    @Query("UPDATE download SET download_length =:downloadLength,download_status=:downloadStatus WHERE file_name = :fileName")
    int updateFile(String fileName, long downloadLength, int downloadStatus);

    @Update()
    int update(DownloadInfo info);

    @Update()
    int updateAll(DownloadInfo... infos);

    @Update()
    int updateAll(List<DownloadInfo> infos);

    //-------------------delete-------------------
    @Delete
    int delete(DownloadInfo info);

    @Delete
    int deleteAll(List<DownloadInfo> infos);

    @Delete
    int deleteAll(DownloadInfo... infos);
}
