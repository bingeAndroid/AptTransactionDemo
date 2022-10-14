package com.apt.transaction.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.apt.transaction.databinding.MainItemLayoutBinding
import com.apt.transaction.entity.AptTransaction

/**
 * @author Jiabin Lin
 * @date 2022/10/13
 */
class MainAdapter : PagingDataAdapter<AptTransaction, MainAdapter.ViewHolder>(MAIN_COMPARATOR),
    View.OnClickListener {

    private var mEditNameClickListener: ((position: Int, transaction: AptTransaction) -> Unit)? =
        null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MainItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            transaction = getItem(position)
            lifecycleOwner = root.context as LifecycleOwner
            btnMainItemEdit.tag = position
            btnMainItemEdit.setOnClickListener(this@MainAdapter)
        }
    }

    class ViewHolder(var binding: MainItemLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {

        private val MAIN_COMPARATOR = object : DiffUtil.ItemCallback<AptTransaction>() {
            override fun areItemsTheSame(oldItem: AptTransaction, newItem: AptTransaction) =
                oldItem == newItem

            override fun areContentsTheSame(
                oldItem: AptTransaction,
                newItem: AptTransaction
            ) = oldItem.id == newItem.id
        }
    }

    fun setOnEditNameClickListener(block: (position: Int, transaction: AptTransaction) -> Unit) {
        this.mEditNameClickListener = block
    }

    override fun onClick(v: View?) {
        val position = v?.tag as Int
        val transaction = getItem(position)
        if (transaction != null) {
            mEditNameClickListener?.invoke(position, transaction)
        }
    }
}