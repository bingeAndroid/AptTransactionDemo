package com.apt.transaction.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.apt.transaction.DEFAULT_USD_AMOUNT

@Entity(tableName = "apt_transaction")
data class AptTransaction(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var name: String,
    val cnyAmount: Double,
    var usdAmount: Double = DEFAULT_USD_AMOUNT
)
