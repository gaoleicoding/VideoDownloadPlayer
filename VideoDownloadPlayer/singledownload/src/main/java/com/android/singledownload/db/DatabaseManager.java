package com.android.singledownload.db;

import androidx.room.Room;
import com.android.singledownload.ContextUtil;

/**
 * author：gaolei
 * date：2019-07-12
 * des：
 */
public class DatabaseManager {

    public static AppDatabase db;

    private DatabaseManager() {
    }

    private static class SingletonHolder {
        private static DatabaseManager instance = new DatabaseManager();
    }

    public static DatabaseManager getInstance() {
        return SingletonHolder.instance;
    }

    public void initDatabase() {
        db = Room.databaseBuilder(
                ContextUtil.getAppContext(),
                AppDatabase.class, "download_info.db"
        )
                //添加数据库的变动迁移支持(当前状态从version1到version2的变动处理)
                //主要在user里面加入了age字段,大家可以git reset --hard <commit> 到第一个版本
                //然后debug 手动生成一些数据。然后debug 该版本查看数据库升级体现。
                .addMigrations(AppDatabase.MIGRATION_1_2)
                //下面注释表示允许主线程进行数据库操作，但是不推荐这样做。
                //他可能造成主线程lock以及anr
                .allowMainThreadQueries()
                .build();

    }
}
