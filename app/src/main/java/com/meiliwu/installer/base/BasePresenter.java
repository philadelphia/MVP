package com.meiliwu.installer.base;

import android.util.Log;

import com.meiliwu.installer.ui.packageList.mvp.PackageListContract;

import java.lang.ref.WeakReference;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Author:  ZhangTao
 * Date: 2018/3/29.
 */
public class BasePresenter<V extends IView> {
    private static final String TAG = "BasePresenter";
    private WeakReference<V> mViewWeakReference;

    private CompositeSubscription compositeSubscription;

    public BasePresenter(){

        compositeSubscription = new CompositeSubscription();
    }

    public V getView() {
        return ((V) mViewWeakReference.get());
    }


    protected void onAttach(V v){
        Log.i(TAG, "onAttach: ");
        mViewWeakReference = new WeakReference<>(v);
    }
    public void onDetach(){
        Log.i(TAG, "onDetach: ");
        if (mViewWeakReference != null){
            mViewWeakReference.clear();
        }

        if (compositeSubscription != null) {
            compositeSubscription.unsubscribe();
        }
    }

    public void addSubscription(Subscription subscription){
        compositeSubscription.add(subscription);
    }

}
