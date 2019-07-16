package com.android.download

import android.Manifest.permission
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import com.android.singledownload.DownloadInfo
import com.android.singledownload.DownloadStatus
import com.android.singledownload.SharedPreferencesUtil
import com.android.singledownload.db.DatabaseManager
import com.android.singledownload.net.OkhttpManager
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.observers.ResourceObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.Request
import java.io.IOException


class MainActivity : AppCompatActivity() {

    lateinit var fileList: MutableList<DownloadInfo>
    lateinit var fileList2: MutableList<DownloadInfo>
    lateinit var videoAdapter: VideoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val videosUrl: Array<String> = resources.getStringArray(R.array.videos);
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
        fileList2 = mutableListOf()

        for (index in 0..9) {
            var downloadInfo = DownloadInfo()
            downloadInfo.url = videosUrl[index]
            downloadInfo.fileShowName = videosName[index]
            downloadInfo.fileImg = images[index]
            fileList.add(downloadInfo)
        }
        if (SharedPreferencesUtil.getInstance(this).getSP("isFirstInsertData").equals("")) {
            getObservable().subscribe(getObserver());

        } else {
            fileList2 = DatabaseManager.getInstance().db.downloadDao().getAll()
            for (index in 0..fileList2.size - 1) {
                if (fileList2[index].downloadStatus == DownloadStatus.statusDownloading) {
                    fileList2[index].downloadStatus = DownloadStatus.statusPause
                    DatabaseManager.getInstance().db.downloadDao().update(fileList2[index])
                }
            }
        }
        initRecyclerView()
        requestPermission()
    }

    fun getObservable(): Observable<DownloadInfo> {
        return Observable.fromIterable(fileList)
            .map(object : Function<DownloadInfo, DownloadInfo> {
                override fun apply(t: DownloadInfo): DownloadInfo {
                    Log.d("gaolei", "t.url-------------" + t.url)
                    var downloadInfo = createDownInfo(t.url, t.fileShowName, t.fileImg)
                    return downloadInfo
                }
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }


    fun getObserver(): ResourceObserver<DownloadInfo> {
        val observer = object : ResourceObserver<DownloadInfo>() {
            override fun onNext(data: DownloadInfo) {
                Log.d("gaolei", "onNext-------------")
                fileList2.add(data)
            }

            override fun onComplete() {
                Log.d("gaolei", "onComplete-------------")
                videoAdapter.notifyDataSetChanged()
                DatabaseManager.getInstance().db.downloadDao().insertAll(fileList2)
                SharedPreferencesUtil.getInstance(this@MainActivity).putSP("isFirstInsertData", "false")
            }

            override fun onError(e: Throwable) {
                Log.d("gaolei", "e-------------" + e.message)
            }
        }
        return observer
    }


    private fun initRecyclerView() {
        videoAdapter = VideoAdapter(this@MainActivity, fileList2)
        videoRecyclerView.adapter = videoAdapter
    }

    fun startOrPauseAll(view: View) {
        if (tv_all.text.toString() == "全部开始") {
            videoAdapter.notifyStartOrPauseAll(true, false)
            tv_all.text = "全部暂停"
        }else {
            videoAdapter.notifyStartOrPauseAll(false, true)
            tv_all.text = "全部开始"
        }

    }
    fun edit(view: View) {


    }

    fun stopAll(view: View) {
        videoAdapter.notifyStartOrPauseAll(false, true)
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

    /**
     * 获取下载长度
     *
     * @param downloadUrl
     * @return
     */
    private fun getContentLength(downloadUrl: String): Long {
        val TOTAL_ERROR: Long = -1//获取进度失败
        val request = Request.Builder()
            .url(downloadUrl)
            .build()
        try {
            val response = OkhttpManager.getInstance().mClient.newCall(request).execute()
            if (response != null && response.isSuccessful()) {
                val contentLength = response.body()!!.contentLength()
                response.close()
                return if (contentLength == 0L) TOTAL_ERROR else contentLength
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return TOTAL_ERROR
    }

    /**
     * 创建DownInfo
     *
     * @param url 请求网址
     * @return DownInfo
     */
    private fun createDownInfo(url: String, fileShowName: String, fileImg: Int): DownloadInfo {
        val downloadInfo = DownloadInfo()
        val contentLength = getContentLength(url)//获得文件大小
        downloadInfo.totalLength = contentLength
        val fileName = url.substring(url.lastIndexOf("/") + 1)
        downloadInfo.fileName = fileName
        downloadInfo.url = url
        downloadInfo.fileImg = fileImg
        downloadInfo.fileShowName = fileShowName
        downloadInfo.downloadStatus = DownloadStatus.statusStart
        downloadInfo.url = url
        return downloadInfo
    }
}
