package com.apt.transaction.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.apt.transaction.entity.AptTransaction

/**
 * @author Jiabin Lin
 * @date 2022/10/13
 */
@Database(entities = [AptTransaction::class], version = 1)
abstract class TransactionDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao

    companion object {
        private var instance: TransactionDatabase? = null
        fun getInstance(context: Context) =
            instance ?: synchronized(TransactionDatabase::class.java) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    TransactionDatabase::class.java,
                    "apt"
                ).build().also {
                    instance = it
                }
            }
    }


}