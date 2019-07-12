package com.android.download

import android.Manifest.permission
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.room.Room
import com.android.singledownload.DownloadInfo
import com.android.singledownload.db.AppDatabase
import com.android.singledownload.db.DatabaseManager
import com.android.singledownload.db.DownloadDao
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    lateinit var fileList: MutableList<DownloadInfo>

    var a = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val videos: Array<String> = resources.getStringArray(R.array.videos);
        val videosName: Array<String> = resources.getStringArray(R.array.videos_name);
        val images: List<Int> = mutableListOf(
            R.mipmap.img1,
            R.mipmap.img2,
            R.mipmap.img3,
            R.mipmap.img4,
            R.mipmap.img5,
            R.mipmap.img6,
            R.mipmap.img7,
            R.mipmap.img8,
            R.mipmap.img9,
            R.mipmap.img10
        )
        fileList = mutableListOf()
        for (index in 0..9) {
            var entity = DownloadInfo()
            entity.url = videos[index]
            entity.fileName = videosName[index]
            entity.fileImg = images[index]
            fileList.add(entity)
        }
        DatabaseManager.getInstance().initDatabase();
        initRecyclerView()
        requestPermission()
    }



    private fun initRecyclerView() {
        var videoAdapter = VideoAdapter(this@MainActivity, fileList)
        videoRecyclerView.adapter = videoAdapter

    }

    fun requestPermission() {
        // checkSelfPermission 判断是否已经申请了此权限
        if (ContextCompat.checkSelfPermission(
                this, permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //如果应用之前请求过此权限但用户拒绝了请求，shouldShowRequestPermissionRationale将返回 true。
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission.WRITE_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this, arrayOf(permission.WRITE_EXTERNAL_STORAGE), 1)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            for (i in permissions.indices) {
                if (grantResults[i] == PERMISSION_GRANTED) {
//                    Toast.makeText(this, "" + "权限" + permissions[i] + "申请成功", Toast.LENGTH_SHORT).show()
                } else {
//                    Toast.makeText(this, "" + "权限" + permissions[i] + "申请失败", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
