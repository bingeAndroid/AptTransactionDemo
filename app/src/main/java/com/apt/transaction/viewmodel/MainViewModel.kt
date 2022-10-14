package com.apt.transaction.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.apt.transaction.DEFAULT_MAX_PAGE_SIZE
import com.apt.transaction.base.BaseViewModel
import com.apt.transaction.entity.AptTransaction
import com.apt.transaction.paging.TransactionPagingSource
import com.apt.transaction.utils.DbUtils
import kotlinx.coroutines.launch

/**
 * @author Jiabin Lin
 * @date 2022/10/13
 */
class MainViewModel: BaseViewModel() {

    /**
     * update name success position
     */
    val mUpdateNamePositionLiveData: MutableLiveData<Int> = MutableLiveData()

    companion object {
        const val PAGE_SIZE = DEFAULT_MAX_PAGE_SIZE
        const val PREFETCH_DISTANCE = 3
    }

    fun getPager(context: Context) = Pager(
        config = PagingConfig(
            pageSize = PAGE_SIZE,
            enablePlaceholders = false,
            prefetchDistance = PREFETCH_DISTANCE,
        ),
        pagingSourceFactory = { TransactionPagingSource(context, PAGE_SIZE) })
        .flow
        .cachedIn(viewModelScope)

    fun updateTransactionByName(context: Context, transaction: AptTransaction, position: Int) {
        viewModelScope.launch {
            val updateRowCount = DbUtils.getInstance(context).updateTransactionByName(transaction)
            mUpdateNamePositionLiveData.value = if (updateRowCount > 0) position else -1
        }
    }

}