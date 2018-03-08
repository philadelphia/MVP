package com.meiliwu.installer.service;

import android.app.DownloadManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Author:  ZhangTao
 * Date: 2018/3/7.
 */

public class DownloadService extends IntentService {
    private String TAG = "DownloadService";
    public static final String BROADCAST_ACTION =
            "com.meiliwu.installer.service.BROADCAST";
    public static final String EXTENDED_DATA_STATUS =
            "com.meiliwu.installer.service.STATUS";
    public static final String FILE_NAME = "fileName";

    private LocalBroadcastManager mLocalBroadcastManager;

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //获取下载地址
        String url = intent.getDataString();
        String fileName = intent.getStringExtra(FILE_NAME);
        Log.i(TAG, url);
        //获取DownloadManager对象
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

        //指定APK缓存路径和应用名称，可在SD卡/Android/data/包名/file/Download文件夹中查看
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        //设置网络下载环境为wifi
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        //设置下载文件的mineType。因为下载管理Ui中点击某个已下载完成文件及下载完成点击通知栏提示都会根据mimeType去打开文件，所以我们可以利用这个属性。
        request.setMimeType("application/vnd.android.package-archive");
        //设置显示通知栏，下载完成后通知栏自动消失
        // request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        //设置通知栏标题
        request.setTitle(fileName);
        request.setDescription("应用正在下载");
        request.setAllowedOverRoaming(false);
        //获得唯一下载id
        long requestId = downloadManager.enqueue(request);
        //将id放进Intent
        Intent localIntent = new Intent(BROADCAST_ACTION);
        localIntent.putExtra(EXTENDED_DATA_STATUS, requestId);
        localIntent.putExtra(DownloadManager.EXTRA_DOWNLOAD_ID, requestId);
        localIntent.putExtra(FILE_NAME, fileName);
        //查询下载信息
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(requestId);
        try {
            boolean isGoging = true;
            while (isGoging) {
                Cursor cursor = downloadManager.query(query);
                if (cursor != null && cursor.moveToFirst()) {
                    int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    switch (status) {
                        //如果下载状态为成功
                        case DownloadManager.STATUS_SUCCESSFUL:
                            isGoging = false;
                            //调用LocalBroadcastManager.sendBroadcast将intent传递回去
                            mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
                            mLocalBroadcastManager.sendBroadcast(localIntent);
                            break;
                    }
                }

                if (cursor != null) {
                    cursor.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
