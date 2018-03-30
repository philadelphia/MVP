package com.meiliwu.installer.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Author:  ZhangTao
 * Date: 2018/3/29.
 */
public abstract class BaseActivity<P extends BasePresenter> extends AppCompatActivity {
    private P presenter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutID());

    }

    public P  getPresenter(){
        return presenter;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null){
            presenter.onDetach();
        }
    }

    public abstract int getLayoutID();
}

