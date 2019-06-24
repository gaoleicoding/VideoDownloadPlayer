package com.android.file

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.singledownload.DownloadInfo
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var fileList: MutableList<DownloadInfo>
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
        fileList = ArrayList()
        for (index in 0..9) {
            var entity = DownloadInfo()
            entity.url = videos[index]
            entity.fileName = videosName[index]
            entity.fileImg = images[index]
            fileList.add(entity)
        }
        initRecyclerView()

    }

    private fun initRecyclerView() {
        var videoAdapter= VideoAdapter(this@MainActivity, fileList)
        videoRecyclerView.adapter = videoAdapter

    }

}
