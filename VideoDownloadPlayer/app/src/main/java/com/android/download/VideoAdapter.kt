package com.android.download

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.singledownload.DownLoadObserver
import com.android.singledownload.DownloadInfo
import com.android.singledownload.DownloadManager
import com.android.singledownload.DownloadStatus
import com.android.singledownload.db.DatabaseManager

class VideoAdapter(private val context: Context, private val fileList: List<DownloadInfo>) :
    RecyclerView.Adapter<VideoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var downloadInfo: DownloadInfo = fileList[position]
        holder.name.text = downloadInfo.fileShowName
        holder.img.setImageResource(downloadInfo.fileImg)
        if (downloadInfo.downloadStatus == DownloadStatus.statusStart)
            holder.tv_download.setText("下载")
        if (downloadInfo.downloadStatus == DownloadStatus.statusPause)
            holder.tv_download.setText("继续")
        if (downloadInfo.downloadStatus == DownloadStatus.statusDownloading)
            holder.tv_download.setText("暂停")

        var mDownloadLength: Long = 0
        holder.tv_download.setOnClickListener {
            if (downloadInfo.downloadStatus == DownloadStatus.statusPause || downloadInfo.downloadStatus == DownloadStatus.statusStart) {
                DownloadManager.getInstance().download(downloadInfo, object : DownLoadObserver() {
                    override fun onNext(value: DownloadInfo) {
                        super.onNext(value)
                        holder.progress.setMax(value.totalLength.toInt())
                        mDownloadLength = value.downloadLength
                        holder.progress.setProgress(mDownloadLength.toInt())
                    }

                    override fun onComplete() {
                        if (downloadInfo != null) {
                            Toast.makeText(
                                context,
                                downloadInfo.getFileName() + "-下载完成",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
                downloadInfo.downloadStatus = DownloadStatus.statusDownloading
                holder.tv_download.setText("暂停")
            } else {
                DownloadManager.getInstance().cancel(downloadInfo.url)
                downloadInfo.downloadStatus = DownloadStatus.statusPause
                holder.tv_download.setText("继续")
            }
            var downloadInfo: DownloadInfo =
                DatabaseManager.getInstance().db.downloadDao().findByFileUrl(downloadInfo.url)
            downloadInfo.downloadStatus = DownloadStatus.statusDownloading
            downloadInfo.downloadLength = mDownloadLength
            DatabaseManager.getInstance().db.downloadDao().update(downloadInfo)
        }

    }

    override fun getItemCount(): Int {
        return fileList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var name: TextView
        var tv_speed: TextView
        var tv_download: TextView
        var img: ImageView
        var progress: ProgressBar

        init {
            name = itemView.findViewById(R.id.name)
            tv_speed = itemView.findViewById(R.id.tv_speed)
            tv_download = itemView.findViewById(R.id.tv_download)
            img = itemView.findViewById(R.id.img)
            progress = itemView.findViewById(R.id.progress)

        }
    }


}