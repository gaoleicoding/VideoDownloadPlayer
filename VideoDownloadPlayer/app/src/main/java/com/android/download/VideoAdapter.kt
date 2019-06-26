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
import com.android.singledownload.DownloadManager
import com.android.singledownload.DownloadInfo

class VideoAdapter(private val context: Context, private val fileList: List<DownloadInfo>) :
    RecyclerView.Adapter<VideoAdapter.ViewHolder>() {

//    lateinit var clickListener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var downloadInfo: DownloadInfo = fileList[position]
        holder.name.text = downloadInfo.fileName
        holder.img.setImageResource(downloadInfo.fileImg)

        holder.itemView.setOnClickListener {
//            clickListener.onItemClick(holder.itemView, position,downloadInfo)

            if (!downloadInfo.isStop) {
                DownloadManager.getInstance().download(downloadInfo.url, object : DownLoadObserver() {
                    override fun onNext(value: DownloadInfo) {
                        super.onNext(value)
                        holder.progress.setMax(value.totalLength.toInt())
                        holder.progress.setProgress(value.getProgress().toInt())
                    }

                    override fun onComplete() {
                        if (downloadInfo != null) {
                            Toast.makeText(
                                context,
                                downloadInfo.getFileName() + "-DownloadComplete",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
                downloadInfo.isStop = true
            } else {
                DownloadManager.getInstance().cancel(downloadInfo.url)
                downloadInfo.isStop = false
            }
        }

    }

    override fun getItemCount(): Int {
        return fileList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var name: TextView
        var speed: TextView
        var img: ImageView
         var progress: ProgressBar

        init {
            name = itemView.findViewById(R.id.name)
            speed = itemView.findViewById(R.id.speed)
            img = itemView.findViewById(R.id.img)
            progress = itemView.findViewById(R.id.progress)

        }
    }

//    interface OnItemClickListener {
//        fun onItemClick(view: View, position: Int,downloadInfo: DownloadInfo)
//    }
//
//    fun setOnItemClickListener(clickListener: OnItemClickListener) {
//        this.clickListener = clickListener
//    }
}