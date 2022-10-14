package com.apt.transaction.dialog

import android.R
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.apt.transaction.databinding.DialogEditNameLayoutBinding
import com.apt.transaction.extend.toast

/**
 * @author Jiabin Lin
 * @date 2022/10/13
 */
class CenterEditNameDialog : DialogFragment() {

    private lateinit var mEditNameDialogBinding: DialogEditNameLayoutBinding
    private var mEditNameSaveClickListener: ((newName: String) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.requestFeature(Window.FEATURE_NO_TITLE)
            window?.setBackgroundDrawableResource(R.color.transparent)
            setCanceledOnTouchOutside(false)
            setCancelable(false)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mEditNameDialogBinding = DialogEditNameLayoutBinding.inflate(inflater)
        return mEditNameDialogBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mEditNameDialogBinding.btnDialogEditNameSave.setOnClickListener {
            val newName = mEditNameDialogBinding.etDialogEditName.text.trim().toString()
            if (newName.isBlank()) {
                "Please input new name first!".toast()
                return@setOnClickListener
            }
            mEditNameSaveClickListener?.invoke(newName)
            clearEditText()
        }
        mEditNameDialogBinding.btnDialogEditNameCancel.setOnClickListener {
            dismiss()
            clearEditText()
        }
    }

    private fun clearEditText() {
        mEditNameDialogBinding.etDialogEditName.setText("")
    }

    fun setOnEditNameSaveClickListener(block: (newName: String) -> Unit) {
        this.mEditNameSaveClickListener = block
    }

    override fun onStart() {
        super.onStart()
        dialog?.run {
            with(window) {
                val lp = window?.attributes
                context.also {
                    lp?.width = (it.resources.displayMetrics.widthPixels * 0.8f).toInt()
                    window?.attributes = lp
                }
            }
        }
    }
}