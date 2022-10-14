package com.apt.transaction.adapter

import android.view.View
import androidx.databinding.BindingAdapter

/**
 * @author Jiabin Lin
 * @date 2022/10/13
 */
@BindingAdapter(value = ["visible"])
fun isVisible(view: View, visible: Boolean = true) {
    view.visibility = if (visible) View.VISIBLE else View.GONE
}