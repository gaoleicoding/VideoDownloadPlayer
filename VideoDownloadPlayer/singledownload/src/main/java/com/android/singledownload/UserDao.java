package com.android.singledownload;

import androidx.room.*;

import java.util.List;

/**
 * Created by Allen on 2018/4/16/016.
 */

@Dao
public interface UserDao {
    //所有的CURD根据primary key进行匹配
    //------------------------query------------------------
    @Query("SELECT * FROM download")
    List<DownloadInfo> getAll();

    @Query("SELECT * FROM download WHERE url IN (:urls)")
    List<DownloadInfo> loadAllByIds(String[] urls);

    @Query("SELECT * FROM download WHERE url = :url")
    DownloadInfo findByUid(String url);

    //-----------------------insert----------------------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(DownloadInfo download);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertAll(DownloadInfo... infos);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertAll(List<DownloadInfo> infos);

    //---------------------update------------------------
    @Update()
    int update(DownloadInfo download);

    @Update()
    int updateAll(DownloadInfo... download);

    @Update()
    int updateAll(List<DownloadInfo> download);

    //-------------------delete-------------------
    @Delete
    int delete(DownloadInfo download);

    @Delete
    int deleteAll(List<DownloadInfo> users);

    @Delete
    int deleteAll(DownloadInfo... users);
}
