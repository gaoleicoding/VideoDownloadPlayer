package com.android.download

import android.app.Application
import androidx.room.Room
import com.android.singledownload.AppDatabase

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "downloadinfo.db"
        )
            //添加数据库的变动迁移支持(当前状态从version1到version2的变动处理)
            //主要在user里面加入了age字段,大家可以git reset --hard <commit> 到第一个版本
            //然后debug 手动生成一些数据。然后debug 该版本查看数据库升级体现。
            .addMigrations(AppDatabase.MIGRATION_1_2)
            //下面注释表示允许主线程进行数据库操作，但是不推荐这样做。
            //他可能造成主线程lock以及anr
            //                .allowMainThreadQueries()
            .build()
    }
}