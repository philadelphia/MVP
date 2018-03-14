package com.meiliwu.installer.rx;

import rx.Subscriber;

/**
 * @description :通过继承该观察者，实现错误交给RxErrorHandler进行处理。
 */
public abstract class RxErrorHandlerSubscriber<T> extends Subscriber<T> {
    private RxErrorHandler rxErrorHandler;

    public RxErrorHandlerSubscriber(RxErrorHandler rxErrorHandler) {
        this.rxErrorHandler = rxErrorHandler;
    }

    @Override
    public void onStart() {
        //可以加载loading
//        if (!NetWorkUtil.isNetConnected()) {
//            Toast.makeText(rxErrorHandler.getContext(), "当前网络不可用，请检查网络情况", Toast.LENGTH_SHORT).show();
//        }


        // 显示进度条
        super.onStart();
    }

    @Override
    public void onCompleted() {

        //结束loading
    }


    @Override
    public void onError(Throwable e) {
        rxErrorHandler.handleError(e);
    }
}
