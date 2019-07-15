package com.android.singledownload;

import android.os.Environment;
import android.util.Log;
import com.android.singledownload.db.DatabaseManager;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;


public class DownloadManager {

    private static final AtomicReference<DownloadManager> INSTANCE = new AtomicReference<>();
    private HashMap<String, Call> downCalls;//用来存放各个下载的请求
    private OkHttpClient mClient;//OKHttpClient;

    public String downloadPath;

    //获得一个单例类
    public static DownloadManager getInstance() {
        DownloadManager current = INSTANCE.get();
        if (current != null) {
            return current;
        }
        current = new DownloadManager();
        if (INSTANCE.compareAndSet(null, current)) {
            return current;
        }
        return null;
    }

    private DownloadManager() {
        downCalls = new HashMap<>();
        mClient = new OkHttpClient.Builder().build();
        downloadPath = Environment.getExternalStorageDirectory().getPath() + "/downloadPlayer";
        File file = new File(downloadPath);
        if (!file.exists()) {
            //找到了文件,代表已经下载过,则获取其长度
            new File(downloadPath).mkdirs();
        }
    }

    /**
     * 开始下载
     *
     * @param
     * @param downLoadObserver 用来回调的接口
     */
    public void download(DownloadInfo info, DownLoadObserver downLoadObserver) {
        Observable.just(info.getUrl())
                .filter(s -> !downCalls.containsKey(s))//call的map已经有了,就证明正在下载,则这次不下载
                .flatMap(s -> Observable.just(info))
                .map(this::getRealFileName)//检测本地文件夹,生成新的文件名
                .flatMap(downloadInfo -> Observable.create(new DownloadSubscribe(downloadInfo)))//下载
                .subscribeOn(Schedulers.io())//在子线程执行
                .observeOn(AndroidSchedulers.mainThread())//在主线程回调
                .subscribe(downLoadObserver);//添加观察者

    }

    public void cancel(String url) {
        Call call = downCalls.get(url);
        if (call != null) {
            call.cancel();//取消
        }
        downCalls.remove(url);
    }

    private DownloadInfo getRealFileName(DownloadInfo downloadInfo) {
        String fileName = downloadInfo.getFileName();
        long downloadLength = 0, contentLength = downloadInfo.getTotalLength();
        File file = new File(downloadPath, fileName);
        if (file.exists()) {
            //找到了文件,代表已经下载过,则获取其长度
            downloadLength = file.length();
        }
        if (downloadLength >= contentLength) {
            downloadInfo.setDownloadStatus(DownloadStatus.statusComplete);
            DatabaseManager.getInstance().db.downloadDao().update(downloadInfo);
            return downloadInfo;
        }
        downloadInfo.setDownloadLength(downloadLength);
        downloadInfo.setFileName(file.getName());
        return downloadInfo;
    }

    private class DownloadSubscribe implements ObservableOnSubscribe<DownloadInfo> {
        private DownloadInfo downloadInfo;

        public DownloadSubscribe(DownloadInfo downloadInfo) {
            this.downloadInfo = downloadInfo;
        }

        @Override
        public void subscribe(ObservableEmitter<DownloadInfo> e) throws Exception {
            String url = downloadInfo.getUrl();
            long downloadLength = downloadInfo.getDownloadLength();//已经下载好的长度
//            long contentLength = downloadInfo.getTotalLength();//文件的总长度
            //初始进度信息
            e.onNext(downloadInfo);

            Request request = new Request.Builder()
                    //确定下载的范围,添加此头,则服务器就可以跳过已经下载好的部分
                    .addHeader("RANGE", "bytes=" + downloadLength + "-" + downloadInfo.getTotalLength())
                    .url(url)
                    .build();
            Call call = mClient.newCall(request);
            downCalls.put(url, call);//把这个添加到call里,方便取消
            Response response = call.execute();
            ResponseBody responseBody = response.body();
//            Log.d("gaolei","responseStr.length()："+responseStr.length());
            File file = new File(downloadPath, downloadInfo.getFileName());
            InputStream is = null;
            FileOutputStream fileOutputStream = null;
            try {
                is = responseBody.byteStream();
                fileOutputStream = new FileOutputStream(file, true);
                byte[] buffer = new byte[2048];//缓冲数组2kB
                int len;
                while ((len = is.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, len);
                    downloadLength += len;
                    downloadInfo.setDownloadLength(downloadLength);
                    e.onNext(downloadInfo);
                }
                if (downloadLength == downloadInfo.getTotalLength())
                    e.onComplete();//完成
                fileOutputStream.flush();
                downCalls.remove(url);
            } catch (Exception ex) {
                String exception = ex.toString();
                Log.e("gaolei", "exception：" + exception);
            } finally {
                //关闭IO流
                IOUtil.closeAll(is, fileOutputStream);

            }

        }
    }

}
