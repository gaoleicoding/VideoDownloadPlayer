package com.android.video

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

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
        val videoList: MutableList<VideoEntity> =  ArrayList()
        for (index in 1..10){
           var entity =VideoEntity ()
            entity.videoUrl=videos[index]
            entity.videoName=videosName[index]
            entity.videoImg=images[index]
            videoList.add(entity)
        }


    }
}
