package com.android.video

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VideoAdapter(private val context: Context, private val videoList: List<VideoEntity>) :
    RecyclerView.Adapter<VideoAdapter.ViewHolder>() {
    lateinit var clickListener:OnItemClickListener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var videoEntity:VideoEntity=videoList[position]
        holder.name.text = videoEntity.videoName
        holder.img.setImageResource(videoEntity.videoImg)

        holder.itemView.setOnClickListener {
            clickListener.onItemClick(holder.itemView,position)
        }

    }

    override fun getItemCount(): Int {
        return videoList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

         var name: TextView
         var speed: TextView
         var img: ImageView

        init {
            name = itemView.findViewById(R.id.name)
            speed = itemView.findViewById(R.id.speed)
            img = itemView.findViewById(R.id.img)

        }
    }

    interface OnItemClickListener{
       fun onItemClick(view:View,position: Int)
    }

    fun setOnItemClickListener(clickListener:OnItemClickListener){
        this.clickListener=clickListener
    }
}