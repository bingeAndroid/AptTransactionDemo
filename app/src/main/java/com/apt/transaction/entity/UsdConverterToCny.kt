package com.apt.transaction.entity

data class UsdConverterToCny(
    val currencyF: String,
    val currencyFD: String,
    val currencyF_Name: String,
    val currencyT: String,
    val currencyT_Name: String,
    val exchange: String,
    val result: String,
    val updateTime: String
)