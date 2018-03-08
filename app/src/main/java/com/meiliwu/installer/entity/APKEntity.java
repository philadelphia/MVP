package com.meiliwu.installer.entity;

import com.google.auto.value.AutoValue;

import java.util.List;

/**
 * Author:  ZhangTao
 * Date: 2018/3/6.
 */

public class APKEntity extends PackageEntity {
    /**
     * id : 461
     * version_name : 3.3.0
     * application_id : 2
     * application_name : 美丽屋C端
     * system_name : android
     * version_describe : 优化价格筛选自定义使用体验
     * download_url : http://cdn.mse.mlwplus.com/meiliwu/applications/2018/02/01/17/05/rent_customer_alibaba_release_3.3.0.apk
     * plist_url :
     * version_type : 正式
     * if_deleted : 0
     * status : 0
     * create_time : 1517475979
     * update_time : 1517475979
     * update_type : 0
     * uid : 5410123e44e8afbe40558c8c
     * icon_url : http://cdn.mse.mlwplus.com/meiliwu/applications/2018/03/05/18/23/meiliwu.png
     */

    private String version_name;
    private String application_id;
    private String system_name;
    private String version_describe;
    private String download_url;
    private String plist_url;
    private String version_type;
    private String status;
    private String update_type;
    private String uid;


    public String getVersion_name() {
        return version_name;
    }

    public void setVersion_name(String version_name) {
        this.version_name = version_name;
    }

    public String getApplication_id() {
        return application_id;
    }

    public void setApplication_id(String application_id) {
        this.application_id = application_id;
    }

    public String getSystem_name() {
        return system_name;
    }

    public void setSystem_name(String system_name) {
        this.system_name = system_name;
    }

    public String getVersion_describe() {
        return version_describe;
    }

    public void setVersion_describe(String version_describe) {
        this.version_describe = version_describe;
    }

    public String getDownload_url() {
        return download_url;
    }

    public void setDownload_url(String download_url) {
        this.download_url = download_url;
    }

    public String getPlist_url() {
        return plist_url;
    }

    public void setPlist_url(String plist_url) {
        this.plist_url = plist_url;
    }

    public String getVersion_type() {
        return version_type;
    }

    public void setVersion_type(String version_type) {
        this.version_type = version_type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUpdate_type() {
        return update_type;
    }

    public void setUpdate_type(String update_type) {
        this.update_type = update_type;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


}
