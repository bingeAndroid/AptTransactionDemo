package com.apt.transaction.utils

import android.annotation.SuppressLint
import android.content.Context
import com.apt.transaction.DEFAULT_MAX_PAGE_SIZE
import com.apt.transaction.db.TransactionDatabase
import com.apt.transaction.entity.AptTransaction

/**
 * @author Jiabin Lin
 * @date 2022/10/13
 */
class DbUtils {

    companion object {

        @SuppressLint("StaticFieldLeak")
        private var instance: DbUtils? = null

        @SuppressLint("StaticFieldLeak")
        private lateinit var mContext: Context
        private val mDao by lazy(LazyThreadSafetyMode.NONE) {
            TransactionDatabase.getInstance(
                mContext
            ).transactionDao()
        }

        fun getInstance(context: Context): DbUtils {
            mContext = context
            return instance ?: synchronized(DbUtils::class.java) {
                instance ?: DbUtils()
            }
        }
    }

    suspend fun getTransactionListFromDb(
        pageIndex: Int = 1,
        pageSize: Int = DEFAULT_MAX_PAGE_SIZE
    ): List<AptTransaction> = mDao.queryTransactionByPage(pageIndex, pageSize)

    suspend fun saveTransactionListIntoDb(transactionList: List<AptTransaction>) {
        mDao.insertTransaction(transactionList)
    }

    suspend fun updateTransactionByName(transactionList: AptTransaction) =
        mDao.updateTransaction(transactionList)

}