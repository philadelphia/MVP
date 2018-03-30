package com.meiliwu.installer.base;

/**
 * Author:  ZhangTao
 * Date: 2018/3/29.
 */
public class BaseModel<T> {
    private T t;

    public BaseModel(T t){
        this.t = t;
    }

    public T getApiService(){
        return t;
    }

    public T getService() {
        return t;
    }
}
