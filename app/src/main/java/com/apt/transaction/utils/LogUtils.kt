package com.apt.transaction.utils

import android.util.Log
import com.apt.transaction.BuildConfig

/**
 * @author Jiabin Lin
 * @date 2022/10/13
 */
object LogUtils {

    private const val DEFAULT_TAG = "Apt"

    private val isDebug
        get() = BuildConfig.DEBUG

    fun v(tag: String = DEFAULT_TAG, msg: String) {
        if (isDebug) {
            Log.v(tag, msg)
        }
    }

    fun d(tag: String = DEFAULT_TAG, msg: String) {
        if (isDebug) {
            Log.d(tag, msg)
        }
    }

    fun i(tag: String = DEFAULT_TAG, msg: String) {
        if (isDebug) {
            Log.i(tag, msg)
        }
    }

    fun w(tag: String = DEFAULT_TAG, msg: String) {
        if (isDebug) {
            Log.w(tag, msg)
        }
    }

    fun e(tag: String = DEFAULT_TAG, msg: String) {
        if (isDebug) {
            Log.e(tag, msg)
        }
    }

    fun e(tag: String = DEFAULT_TAG, msg: String = "", exception: Throwable) {
        if (isDebug) {
            Log.e(tag, msg, exception)
        }
    }
}