package com.meiliwu.installer.entity;


import com.google.auto.value.AutoValue;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Author:  ZhangTao
 * Date: 2018/3/5.
 */

public class PackageEntity {

    /**
     * id : 12
     * application_name : 南京麦风
     * application_describe : null
     * icon_url : http://cdn.mse.mlwplus.com/meiliwu/applications/2017/11/30/09/50/Spotlight.png
     * bundle_id : com.eallcn.chowRentAgent
     * testing_bundle_id : com.eallcn.chowRentAgentDev
     * if_deleted : 0
     * create_time : 1512006618
     * update_time : 1512006618
     */

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

    private String id;
    private String application_name;
    private Object application_describe;
    private String icon_url;
    private String bundle_id;
    private String testing_bundle_id;
    private String if_deleted;
    private String create_time;
    private String update_time;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApplication_name() {
        return application_name;
    }

    public void setApplication_name(String application_name) {
        this.application_name = application_name;
    }

    public Object getApplication_describe() {
        return application_describe;
    }

    public void setApplication_describe(Object application_describe) {
        this.application_describe = application_describe;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }

    public String getBundle_id() {
        return bundle_id;
    }

    public void setBundle_id(String bundle_id) {
        this.bundle_id = bundle_id;
    }

    public String getTesting_bundle_id() {
        return testing_bundle_id;
    }

    public void setTesting_bundle_id(String testing_bundle_id) {
        this.testing_bundle_id = testing_bundle_id;
    }

    public String getIf_deleted() {
        return if_deleted;
    }

    public void setIf_deleted(String if_deleted) {
        this.if_deleted = if_deleted;
    }

    public String getCreate_time() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
        String format = simpleDateFormat.format(Long.valueOf(create_time) * 1000);
        return format;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    @Override
    public String toString() {
        return "PackageEntity{" +
                "id='" + id + '\'' +
                ", application_name='" + application_name + '\'' +
                ", application_describe=" + application_describe +
                ", icon_url='" + icon_url + '\'' +
                ", bundle_id='" + bundle_id + '\'' +
                ", testing_bundle_id='" + testing_bundle_id + '\'' +
                ", if_deleted='" + if_deleted + '\'' +
                ", create_time='" + create_time + '\'' +
                ", update_time='" + update_time + '\'' +
                '}';
    }
}
