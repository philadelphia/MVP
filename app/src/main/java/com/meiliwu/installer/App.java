package com.meiliwu.installer;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.meiliwu.installer.di.AppComponent;
import com.meiliwu.installer.di.AppModule;
import com.meiliwu.installer.di.DaggerAppComponent;
import com.meiliwu.installer.di.NetModule;
import com.meiliwu.installer.di.ServiceModule;
import com.meiliwu.installer.http.GlobalHttpHandler;
import com.meiliwu.installer.rx.ResponseErrorListener;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Author:  ZhangTao
 * Date: 2018/3/9.
 */

public class App extends Application implements ResponseErrorListener{
    private static App mInstance ;
    private AppComponent appComponent;
    private AppModule appModule;
    private NetModule netModule;
    private ServiceModule serviceModule;
    private static final String TAG = "App";

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        initModules();
        appComponent = DaggerAppComponent.builder().appModule(appModule).netModule(netModule).serviceModule(serviceModule).build();
        appComponent.inject(this);
    }

    private void initModules() {
        appModule = new AppModule(this);
        netModule = new NetModule.Builder()
                .setApiUrl("http://backstage.mlwplus.com/")
                .setErrorListener(this)
                .setHandler(getHttpHandler())
                .setInterceptors(null).build();

        serviceModule = new ServiceModule();
    }

    public static Application getInstance() {
        return mInstance;
    }

    @Override
    public void handlerResponseError(Context context, Exception e) {
        Toast.makeText(context, e.getMessage() + e.getCause(), Toast.LENGTH_LONG).show();
    }

    public AppComponent getAppComponent(){
        return appComponent;
    }

    public static GlobalHttpHandler getHttpHandler() {
        return new GlobalHttpHandler() {
            @Override
            public Response onHttpResultResponse(String httpResult, Interceptor.Chain chain, Response response) {
                //这里可以先客户端一步拿到每一次http请求的结果,可以解析成json,做一些操作,如检测到token过期后
                //重新请求token,并重新执行请求
//                try {
//                    JSONArray array = new JSONArray(httpResult);
//                    JSONObject object = (JSONObject) array.get(0);
//                    String login = object.getString("login");
//                    String avatar_url = object.getString("avatar_url");
//                    Timber.tag(TAG).w("result ------>" + login + "    ||   avatar_url------>" + avatar_url);
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }


//                这里如果发现token过期,可以先请求最新的token,然后在拿新的token去重新请求之前的http请求
//                 create a new request and modify it accordingly using the new token
//                    Request newRequest = chain.request().newBuilder().header("token", newToken)
//                            .build();

                    // retry the request

//                    response.body().close();
//                    try {
//                        return chain.proceed(newRequest);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }

                //如果需要返回新的结果,则直接把response参数返回出去
                return response;
            }

            @Override
            public Request onHttpRequestBefore(Interceptor.Chain chain, Request request) {
                //如果需要再请求服务器之前做一些操作,则重新返回一个做过操作的的requeat如增加header,不做操作则返回request
//
//                return chain.request().newBuilder().header("token", tokenId)
//                        .build();
                return request;
            }
        };
    }

}
