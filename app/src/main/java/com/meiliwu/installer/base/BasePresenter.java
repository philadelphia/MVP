package com.meiliwu.installer.base;

import java.lang.ref.WeakReference;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Author:  ZhangTao
 * Date: 2018/3/29.
 */
public class BasePresenter<V extends IView, M  extends IModel> {
    private WeakReference<IView> mViewWeakReference;
    private WeakReference<IModel> mModelWeakReference ;
    private V mView;
    private M mModel;
    private CompositeSubscription compositeSubscription;

    public BasePresenter(V view, M model){
        this.mView = view;
        this.mModel = model;
        mModelWeakReference = new WeakReference<IModel>(mModel);
        mViewWeakReference = new WeakReference<IView>(mView);
        compositeSubscription = new CompositeSubscription();
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

        if (compositeSubscription != null) {
            compositeSubscription.unsubscribe();
        }
    }

    public void addSubscription(Subscription subscription){
        compositeSubscription.add(subscription);
    }

}
