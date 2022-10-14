package com.apt.transaction.base

/**
 * @author Jiabin Lin
 * @date 2022/10/13
 */
data class BaseResponse<T>(val reason: String, val result: T, val error_code: Int = -1)