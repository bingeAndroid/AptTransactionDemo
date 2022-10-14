package com.apt.transaction.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * @author Jiabin Lin
 * @date 2022/10/13
 */
open class BaseViewModel:ViewModel()

fun BaseViewModel.launch(
    call: suspend CoroutineScope.() -> Unit,
    catch: suspend CoroutineScope.() -> Unit = {},
    finally: suspend CoroutineScope.() -> Unit = {}
) {
    this.viewModelScope.launch {
        try {
            call()
        } catch (e: Exception) {
            catch()
        } finally {
            finally()
        }
    }
}