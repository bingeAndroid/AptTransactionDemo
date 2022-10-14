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
import com.apt.transaction.api.AptService
import com.apt.transaction.api.RetrofitManager
import com.apt.transaction.entity.AptTransaction
import com.apt.transaction.extend.retainTwoDecimalPlaces
import com.apt.transaction.utils.DbUtils
import com.apt.transaction.utils.LogUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * @author Jiabin Lin
 * @date 2022/10/13
 */

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class TransactionPagingSource(private val context: Context, private val pageSize: Int = 10) :
    PagingSource<Int, AptTransaction>() {

    override fun getRefreshKey(state: PagingState<Int, AptTransaction>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AptTransaction> {
        val start = params.key ?: 1
        LogUtils.i(msg = "request data page index is $start")
        return LoadResult.Page(
            data = mockResponseTransactionList(start),
            prevKey = if (start > 1) start - 1 else null,
            nextKey = if (start > 5) null else start + 1
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
                val EXCHANGE_KEY = stringPreferencesKey("exchange_key")
                if (exchange.isBlank()) {
                    //if exchange is blank,get it from dataStore,default value is 7.0
                    exchange = context.dataStore.data.map { preferences ->
                        preferences[EXCHANGE_KEY] ?: "7.0"
                    }.first()
                }
                LogUtils.i(msg = "exchange is == $exchange")
                if (transaction.usdAmount != -1.0) {
                    //save exchange into dataStore prevent request api fail
                    context.dataStore.edit { settings ->
                        settings[EXCHANGE_KEY] = exchange
                    }
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
        for (i in ((pageIndex - 1) * pageSize + 1)..((pageIndex - 1) * pageSize + 10)) {
            transactionList.add(
                AptTransaction(
                    name = "Transaction $i",
                    cnyAmount = (Math.random() * 100).retainTwoDecimalPlaces,
                    usdAmount = if (i % 3 == 0) (Math.random() * 1000).retainTwoDecimalPlaces else -1.0
                )
            )
        }
        DbUtils.getInstance(context).saveTransactionListIntoDb(transactionList)
        return transactionList
    }

}