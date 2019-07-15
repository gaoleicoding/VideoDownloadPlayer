package com.android.singledownload.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.android.singledownload.DownloadInfo;

@Database(entities = {DownloadInfo.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {

    public abstract DownloadDao downloadDao();

    public AppDatabase() {
    }

    //数据库变动添加Migration
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE download "
                    + " ADD COLUMN age INTEGER NOT NULL DEFAULT 0");
        }
    };

}
