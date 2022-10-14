package com.apt.transaction.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.apt.transaction.DEFAULT_MAX_PAGE_SIZE
import com.apt.transaction.entity.AptTransaction

/**
 * @author Jiabin Lin
 * @date 2022/10/13
 */
@Dao
interface TransactionDao {

    @Insert
    suspend fun insertTransaction(transactionList: List<AptTransaction>)

    @Query("SELECT * FROM apt_transaction LIMIT :pageSize OFFSET (:pageIndex-1)*10")
    suspend fun queryTransactionByPage(pageIndex: Int = 1, pageSize: Int = DEFAULT_MAX_PAGE_SIZE): List<AptTransaction>

    @Update
    suspend fun updateTransaction(transaction: AptTransaction): Int

}