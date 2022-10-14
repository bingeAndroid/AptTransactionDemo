package com.apt.transaction.api

import com.apt.transaction.BuildConfig
import com.apt.transaction.base.BaseService
import com.apt.transaction.utils.LogUtils
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * @author Jiabin Lin
 * @date 2022/10/13
 */
object RetrofitManager {

    private const val BASE_URL = "http://op.juhe.cn"
    private val servicesMap = ConcurrentHashMap<String, BaseService>()
    private const val TIME_OUT_SECONDS = 10

    private val mLogInterceptor by lazy(LazyThreadSafetyMode.NONE) {
        HttpLoggingInterceptor { LogUtils.d(msg = it) }.setLevel(if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.BASIC)
    }

    private val mFixParamsInterceptor by lazy(LazyThreadSafetyMode.NONE) {
        FixParamsInterceptor()
    }

    private val mClient: OkHttpClient
        get() = OkHttpClient.Builder()
            .addInterceptor(mLogInterceptor)
            .addInterceptor(mFixParamsInterceptor)
            .connectTimeout(TIME_OUT_SECONDS.toLong(), TimeUnit.SECONDS)
            .build()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    fun <T : BaseService> getService(serviceClass: Class<T>, baseUrl: String? = null): T {
        return servicesMap.getOrPut(serviceClass.name) {
            Retrofit.Builder()
                .client(mClient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .baseUrl(baseUrl ?: BASE_URL)
                .build()
                .create(serviceClass)
        } as T
    }
}