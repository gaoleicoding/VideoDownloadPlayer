package com.android.singledownload;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Created by Allen on 2018/4/16/016.
 */

@Database(entities = {DownloadInfo.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DownloadDao downloadDao();

    //数据库变动添加Migration
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE download "
                    + " ADD COLUMN age INTEGER NOT NULL DEFAULT 0");
        }
    };

}
