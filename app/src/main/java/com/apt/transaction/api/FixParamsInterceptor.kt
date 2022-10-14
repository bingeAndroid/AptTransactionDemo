package com.apt.transaction.api

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * @author Jiabin Lin
 * @date 2022/10/13
 */
class FixParamsInterceptor : Interceptor {

    companion object {
        private const val KEY = "key"
        private const val VALUE = "0eaed736b78346fde63467701ed941f2"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val builder: Request.Builder = request.newBuilder()
        val urlWithKey = request.url.newBuilder()
            .addQueryParameter(KEY, VALUE)
            .build()
        val newRequest: Request = builder.url(urlWithKey).build()
        return chain.proceed(newRequest)
    }
}