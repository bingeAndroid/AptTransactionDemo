package com.apt.transaction.extend

import android.widget.Toast
import com.apt.transaction.AptApp
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * @author Jiabin Lin
 * @date 2022/10/13
 */
val Double.retainTwoDecimalPlaces
    get() = BigDecimal(this).setScale(2, RoundingMode.HALF_UP).toDouble()

fun String?.toast() = takeIf { it.isNullOrBlank().not() }?.also {
    Toast.makeText(AptApp.mContext, it, Toast.LENGTH_SHORT).show()
}