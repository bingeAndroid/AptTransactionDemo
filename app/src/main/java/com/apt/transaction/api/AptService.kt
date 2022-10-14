package com.apt.transaction.api

import com.apt.transaction.base.BaseResponse
import com.apt.transaction.base.BaseService
import com.apt.transaction.entity.UsdConverterToCny
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @author Jiabin Lin
 * @date 2022/10/13
 */
interface AptService : BaseService {

    @GET("onebox/exchange/currency")
    suspend fun UsdConverterToCny(
        @Query("from") from: String,
        @Query("to") to: String
    ): BaseResponse<List<UsdConverterToCny>>

}