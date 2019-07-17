package com.android.download

import android.Manifest.permission
import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import com.android.singledownload.DownloadInfo
import com.android.singledownload.DownloadManager
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
import java.io.File


class MainActivity : AppCompatActivity(), VideoAdapter.OnCheckListtener {

    lateinit var videoUrls: Array<String>
    lateinit var fileList: MutableList<DownloadInfo>
    lateinit var fileOpList: MutableList<DownloadInfo>
    lateinit var videoAdapter: VideoAdapter
    lateinit var selectDownloadList: MutableList<DownloadInfo>
    lateinit var dialog: ProgressDialog
    override fun onChecked(position: Int, downloadInfo: DownloadInfo, isChecked: Boolean) {

        if (isChecked) {
            selectDownloadList.add(downloadInfo)

        } else {
            selectDownloadList.remove(downloadInfo)
        }
        changeDelText(selectDownloadList.size)
    }

    fun changeDelText(size: Int) {
        if (size > 0) {
            tv_delete.setTextColor(getColor(R.color.color_ffa400))
            tv_delete.setText("删除" + "(" + size + ")")
        } else {
            tv_delete.setTextColor(getColor(R.color.black))
            tv_delete.setText("删除")
        }
        if (fileOpList.size > size) {
            isSelectAll = false
            tv_select_all.setText("全选")
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dialog = ProgressDialog(this@MainActivity)
        dialog.setMessage("加载中...")
        dialog.setCancelable(true)

        videoUrls = resources.getStringArray(R.array.videos);
        val videoNames: Array<String> = resources.getStringArray(R.array.videos_name);
        val videoImages: List<Int> = mutableListOf(
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
        fileOpList = mutableListOf()
        selectDownloadList = mutableListOf()

        if (SharedPreferencesUtil.getInstance(this).getSP("isFirstInsertData").equals("")) {
            for (index in 0..9) {
                var downloadInfo = DownloadInfo()
                downloadInfo.url = videoUrls[index]
                downloadInfo.fileShowName = videoNames[index]
                downloadInfo.fileImg = videoImages[index]
                fileList.add(downloadInfo)
            }
            getObservable().subscribe(getObserver());
            dialog.show()
        } else {
            fileOpList = DatabaseManager.getInstance().db.downloadDao().getAll()
            for (index in 0..fileOpList.size - 1) {
                if (fileOpList[index].downloadStatus == DownloadStatus.statusDownloading) {
                    fileOpList[index].downloadStatus = DownloadStatus.statusPause
                    DatabaseManager.getInstance().db.downloadDao().update(fileOpList[index])
                }
            }
        }
        initRecyclerView()
        requestPermission()
        videoAdapter.setOnCheckListener(this)

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
                fileOpList.add(data)
            }

            override fun onComplete() {
                Log.d("gaolei", "onComplete-------------")
                videoAdapter.notifyDataSetChanged()
                DatabaseManager.getInstance().db.downloadDao().insertAll(fileOpList)
                SharedPreferencesUtil.getInstance(this@MainActivity).putSP("isFirstInsertData", "false")
                dialog.dismiss()
            }

            override fun onError(e: Throwable) {
                Log.d("gaolei", "e-------------" + e.message)
            }
        }
        return observer
    }


    private fun initRecyclerView() {
        videoAdapter = VideoAdapter(this@MainActivity, fileOpList)
        videoRecyclerView.adapter = videoAdapter
        videoRecyclerView.itemAnimator = null
    }

    fun startOrPauseAll(view: View) {
        if (tv_all.text.toString() == "全部开始") {
            videoAdapter.notifyStartOrPauseAll(true, false)
            tv_all.text = "全部暂停"
        } else {
            videoAdapter.notifyStartOrPauseAll(false, true)
            tv_all.text = "全部开始"
        }

    }

    var isEdit: Boolean = false
    fun edit(view: View) {
        if (!isEdit) {
            videoAdapter.notifyEdit(true)
            isEdit = true
            cl_edit.visibility = View.VISIBLE
            tv_edit.setText("取消")
        } else {
            cancelEdit()
        }

    }

    fun cancelEdit() {
        videoAdapter.notifyEdit(false)
        videoAdapter.selectAll(false)
        isEdit = false
        isSelectAll = false
        cl_edit.visibility = View.GONE
        tv_edit.setText("编辑")
        tv_select_all.setText("全选")
        changeDelText(0)
        selectDownloadList.clear()
    }

    var isSelectAll: Boolean = false
    fun selectAll(view: View) {
        if (!isSelectAll) {
            isSelectAll = true
            videoAdapter.selectAll(true)
            tv_select_all.setText("取消全选")
            selectDownloadList.clear()
            for (index in 0..fileOpList.size - 1) {
                selectDownloadList.add(fileOpList[index])
            }
            changeDelText(selectDownloadList.size)
        } else {
            videoAdapter.selectAll(false)
            isSelectAll = false
            tv_select_all.setText("全选")
            changeDelText(0)
            selectDownloadList.clear()
        }
    }

    fun delete(view: View) {
        if (selectDownloadList.size == 0) return
        fileOpList.removeAll(selectDownloadList)

        changeDelText(0)
        for (downloadInfo in selectDownloadList) {
            val fileName = downloadInfo.getFileName()
            val file = File(DownloadManager.getInstance().downloadPath, fileName)
            if (file.exists()) {
                //找到了文件,删除
                file.delete()
            }
        }
        DatabaseManager.getInstance().db.downloadDao().deleteAll(selectDownloadList)
        cancelEdit()

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
        } catch (e: Throwable) {
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
