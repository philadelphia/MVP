package com.meiliwu.installer.http;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public interface GlobalHttpHandler {
    Response onHttpResultResponse(String httpResult, Interceptor.Chain chain, Response response);

    Request onHttpRequestBefore(Interceptor.Chain chain, Request request);
}