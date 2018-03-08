package com.meiliwu.installer.api;

import com.meiliwu.installer.entity.APKEntity;
import com.meiliwu.installer.entity.PackageEntity;
import com.meiliwu.installer.entity.Result;


import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Author:  ZhangTao
 * Date: 2018/3/5.
 */

public interface ApiService {
    /*获取所有APK列表*/
    @GET("/applicationClient/getApplications")
    Observable<Result<APKEntity>> getPackageList();

    /*获取指定平台的特定APK(release/debug)列表*/
    @GET("/applicationClient/getApplicationVersions")
    Observable<Result<APKEntity>> getSpecifiedAPKVersionList(@Query("system_name") String system_name, @Query("application_id") String application_id, @Query("version_type") String version_type);
}
