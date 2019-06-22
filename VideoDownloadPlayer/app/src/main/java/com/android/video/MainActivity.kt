package com.android.video

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var videoList: MutableList<VideoEntity>
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
        videoList = ArrayList()
        for (index in 0..9) {
            var entity = VideoEntity()
            entity.videoUrl = videos[index]
            entity.videoName = videosName[index]
            entity.videoImg = images[index]
            videoList.add(entity)
        }
        initRecyclerView()

    }

    private fun initRecyclerView() {
        var videoAdapter= VideoAdapter(this@MainActivity, videoList)
        videoRecyclerView.adapter = videoAdapter
        videoAdapter.setOnItemClickListener(object : VideoAdapter.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                Toast.makeText(this@MainActivity, "position：" + position, Toast.LENGTH_LONG).show()
            }

        })
    }

}
