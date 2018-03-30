package com.meiliwu.installer.utils;

import com.meiliwu.installer.api.ApiService;

/**
 * Author:  ZhangTao
 * Date: 2018/3/30.
 */
public class ApiServiceUtil {
    public static ApiService getApiService(){
        return RetrofitUtil.getInstance().getRetrofit().create(ApiService.class);
    }
}
