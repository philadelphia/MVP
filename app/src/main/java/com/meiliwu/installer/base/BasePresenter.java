package com.meiliwu.installer.base;

import java.lang.ref.WeakReference;

/**
 * Author:  ZhangTao
 * Date: 2018/3/29.
 */
public class BasePresenter<V extends IView, M  extends IModel> {
    private WeakReference<IView> mViewWeakReference;
    private WeakReference<IModel> mModelWeakReference ;
    private V mView;
    private M mModel;

    public BasePresenter(V view, M model){
        this.mView = view;
        this.mModel = model;
        mModelWeakReference = new WeakReference<IModel>(mModel);
        mViewWeakReference = new WeakReference<IView>(mView);
    }

    public V getView() {
        return mView;
    }

    public M getModel() {
        return mModel;
    }

    public void onDetach(){
        if (mViewWeakReference != null){
            mViewWeakReference.clear();
        }

        if (mModelWeakReference != null){
            mModelWeakReference.clear();
        }
    }
}
