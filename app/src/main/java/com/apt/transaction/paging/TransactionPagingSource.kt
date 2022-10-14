package com.apt.transaction.paging

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apt.transaction.DEFAULT_MAX_PAGE_SIZE
import com.apt.transaction.DEFAULT_USD_AMOUNT
import com.apt.transaction.api.AptService
import com.apt.transaction.api.RetrofitManager
import com.apt.transaction.entity.AptTransaction
import com.apt.transaction.extend.retainTwoDecimalPlaces
import com.apt.transaction.utils.APT_EXCHANGE_KEY
import com.apt.transaction.utils.DataUtils
import com.apt.transaction.utils.DbUtils
import com.apt.transaction.utils.LogUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * @author Jiabin Lin
 * @date 2022/10/13
 */
const val MAX_PAGE_INDEX = 5
class TransactionPagingSource(private val context: Context, private val pageSize: Int = DEFAULT_MAX_PAGE_SIZE) :
    PagingSource<Int, AptTransaction>() {

    override fun getRefreshKey(state: PagingState<Int, AptTransaction>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AptTransaction> {
        val start = params.key ?: 1
        LogUtils.i(msg = "request data page index is $start")
        return LoadResult.Page(
            data = mockResponseTransactionList(start),
            prevKey = if (start > 1) start - 1 else null,
            nextKey = if (start > MAX_PAGE_INDEX) null else start + 1
        )
    }

    private suspend fun mockResponseTransactionList(pageIndex: Int): List<AptTransaction> {
        val transactionListFromDb =
            DbUtils.getInstance(context).getTransactionListFromDb(pageIndex, pageSize)
        LogUtils.i(msg = "data form db = $transactionListFromDb")
        var mockTransactionList = transactionListFromDb
        if (mockTransactionList.isEmpty()) {
            mockTransactionList = randomMockTransactionList(pageIndex)
        }
        val usdConverterToCny =
            RetrofitManager.getService(serviceClass = AptService::class.java)
                .UsdConverterToCny("USD", "CNY")
        if (usdConverterToCny.error_code == 0 && usdConverterToCny.result.isNotEmpty()) {
            var exchange = usdConverterToCny.result[0].exchange
            for (transaction in mockTransactionList) {
                if (exchange.isBlank()) {
                    //if exchange is blank,get it from dataStore,default value is 7.0
                    exchange = DataUtils.getInstance().getString(APT_EXCHANGE_KEY, "7.0")
                }
                LogUtils.i(msg = "exchange is == $exchange")
                if (transaction.usdAmount != DEFAULT_USD_AMOUNT) {
                    //save exchange into dataStore prevent request api fail
                    DataUtils.getInstance().putString(APT_EXCHANGE_KEY, exchange)
                    transaction.usdAmount =
                        (exchange.toDouble() * transaction.cnyAmount).retainTwoDecimalPlaces
                }
            }
        }
        return mockTransactionList
    }

    private suspend fun randomMockTransactionList(pageIndex: Int): List<AptTransaction> {
        LogUtils.i(msg = "request pageIndex is $pageIndex")
        val transactionList = mutableListOf<AptTransaction>()
        for (i in ((pageIndex - 1) * pageSize + 1)..((pageIndex - 1) * pageSize + DEFAULT_MAX_PAGE_SIZE)) {
            transactionList.add(
                AptTransaction(
                    name = "Transaction $i",
                    cnyAmount = (Math.random() * 100).retainTwoDecimalPlaces,
                    usdAmount = if (i % 3 == 0) (Math.random() * 1000).retainTwoDecimalPlaces else DEFAULT_USD_AMOUNT
                )
            )
        }
        DbUtils.getInstance(context).saveTransactionListIntoDb(transactionList)
        return transactionList
    }

}