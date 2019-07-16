package com.android.download

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.android.singledownload.DownLoadObserver
import com.android.singledownload.DownloadInfo
import com.android.singledownload.DownloadManager
import com.android.singledownload.DownloadStatus
import com.android.singledownload.db.DatabaseManager

class VideoAdapter(private val context: Context, private val fileList: List<DownloadInfo>) :
    RecyclerView.Adapter<VideoAdapter.ViewHolder>() {
    var isStartAll: Boolean = false
    var isStopAll: Boolean = false
    var isEdit: Boolean = false
    var isSelectAll: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false)
        return ViewHolder(view)
    }

    fun notifyStartOrPauseAll(isStartAll: Boolean, isStopAll: Boolean) {
        this.isStartAll = isStartAll
        this.isStopAll = isStopAll
        notifyDataSetChanged()
    }


    fun notifyEdit(isEdit: Boolean) {
        this.isEdit = isEdit
        notifyDataSetChanged()
    }

    fun selectAll(isSelectAll: Boolean) {
        this.isSelectAll = isSelectAll
        notifyItemRangeChanged(0,fileList.size)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var downloadInfo: DownloadInfo = fileList[position]
        holder.name.text = downloadInfo.fileShowName
        holder.img.setImageResource(downloadInfo.fileImg)
        holder.progress.setMax(downloadInfo.totalLength.toInt())
        var mDownloadLength: Long = downloadInfo.downloadLength
        holder.progress.setProgress(mDownloadLength.toInt())
        if (isStartAll) {
            startDownload(downloadInfo, holder)
            if (downloadInfo.downloadStatus != DownloadStatus.statusComplete)
                downloadInfo.downloadStatus == DownloadStatus.statusDownloading
        }
        if (isStopAll) {
            pauseDownload(downloadInfo, holder)
            if (downloadInfo.downloadStatus != DownloadStatus.statusComplete)
                downloadInfo.downloadStatus == DownloadStatus.statusPause
        }
        if (isEdit) {
            holder.cb_select.visibility = View.VISIBLE
        } else {
            holder.cb_select.visibility = View.GONE
            holder.cb_select.isChecked = false
        }


        if (isSelectAll) {
            holder.cb_select.isChecked = true
//            onCheckListtener?.onChecked(position, downloadInfo.url, true)
        } else {
            holder.cb_select.isChecked = false
//            onCheckListtener?.onChecked(position, downloadInfo.url, false)
        }
        holder.cb_select.setOnCheckedChangeListener { buttonView, isChecked ->
                onCheckListtener?.onChecked(position, downloadInfo, isChecked)
        }
        if (downloadInfo.downloadStatus == DownloadStatus.statusStart)
            holder.tv_download.setText("下载")
        if (downloadInfo.downloadStatus == DownloadStatus.statusPause)
            holder.tv_download.setText("继续")
        if (downloadInfo.downloadStatus == DownloadStatus.statusDownloading)
            holder.tv_download.setText("暂停")
        if (downloadInfo.downloadStatus == DownloadStatus.statusComplete) {
            holder.tv_download.setText("完成")
            holder.progress.setProgress(downloadInfo.totalLength.toInt())
        }

        holder.tv_download.setOnClickListener {
            if (downloadInfo.downloadStatus == DownloadStatus.statusPause || downloadInfo.downloadStatus == DownloadStatus.statusStart) {
                startDownload(downloadInfo, holder)
            } else if (downloadInfo.downloadStatus == DownloadStatus.statusDownloading) {
                pauseDownload(downloadInfo, holder)
            } else if (downloadInfo.downloadStatus == DownloadStatus.statusComplete) {
                var toast: Toast = Toast.makeText(context, "已经下载完成", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 100)
                toast.show()
            }

        }

    }

    fun startDownload(info: DownloadInfo, holder: ViewHolder) {

        DownloadManager.getInstance().download(info, object : DownLoadObserver() {
            override fun onNext(value: DownloadInfo) {
                super.onNext(value)
                info.downloadLength = value.downloadLength
                Log.d("gaolei", "downloadLength:" + value.downloadLength)
                holder.progress.setProgress(value.downloadLength.toInt())
                if (value.downloadLength == value.totalLength) {
                    holder.tv_download.setText("完成")
                    downloadInfo.downloadStatus = DownloadStatus.statusComplete
                    updateDownloadInfo(downloadInfo)
                }
            }

            override fun onComplete() {
                downloadInfo.downloadStatus = DownloadStatus.statusComplete
                updateDownloadInfo(downloadInfo)

                if (downloadInfo != null) {
                    Toast.makeText(
                        context,
                        downloadInfo.getFileShowName() + " 下载完成",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
        info.downloadStatus = DownloadStatus.statusDownloading
        updateDownloadInfo(info)
        holder.tv_download.setText("暂停")
    }

    fun pauseDownload(info: DownloadInfo, holder: ViewHolder) {
        info.downloadStatus = DownloadStatus.statusPause
        updateDownloadInfo(info)

        DownloadManager.getInstance().cancel(info.url)
        holder.tv_download.setText("继续")
    }

    fun updateDownloadInfo(downloadInfo: DownloadInfo) {
        downloadInfo.downloadLength = downloadInfo.downloadLength
        downloadInfo.downloadStatus = downloadInfo.downloadStatus
        DatabaseManager.getInstance().db.downloadDao().update(downloadInfo)
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
        var cb_select: CheckBox

        init {
            name = itemView.findViewById(R.id.name)
            tv_speed = itemView.findViewById(R.id.tv_speed)
            tv_download = itemView.findViewById(R.id.tv_download)
            img = itemView.findViewById(R.id.img)
            progress = itemView.findViewById(R.id.progress)
            cb_select = itemView.findViewById(R.id.cb_select)

        }
    }

    interface OnCheckListtener {
        fun onChecked(position: Int, downloadInfo: DownloadInfo, isChecked: Boolean)
    }

    var onCheckListtener: OnCheckListtener? = null
    fun setOnCheckListener(onCheckListtener: OnCheckListtener) {
        this.onCheckListtener = onCheckListtener
    }

}