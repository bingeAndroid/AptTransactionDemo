package com.apt.transaction.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import com.apt.transaction.R
import com.apt.transaction.adapter.MainAdapter
import com.apt.transaction.databinding.ActivityMainBinding
import com.apt.transaction.dialog.CenterEditNameDialog
import com.apt.transaction.extend.toast
import com.apt.transaction.utils.LogUtils
import com.apt.transaction.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * @author Jiabin Lin
 * @date 2022/10/13
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mAdapter: MainAdapter
    private val mViewModel: MainViewModel by viewModels()
    private var mCenterEditNameDialog: CenterEditNameDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        mBinding.lifecycleOwner = this
        initView()
        LogUtils.i(msg = "viewModel is $mViewModel")
        lifecycleScope.launch {
            mViewModel.getPager(context = this@MainActivity).collectLatest {
                mAdapter.submitData(it)
            }
        }
        initObserve()
    }

    private fun initObserve() {
        // listen update name success or not
        mViewModel.mUpdateNamePositionLiveData.observe(this) {
            mCenterEditNameDialog?.dismiss()
            if (it > 0) {
                LogUtils.i(msg = "Edit name success,refresh position is $it")
                "Edit name success".toast()
                mAdapter.notifyItemChanged(it)
            } else {
                LogUtils.i(msg = "Edit name fail")
                "Edit name fail".toast()
            }
        }
    }

    private fun initView() {
        mAdapter = MainAdapter()
        mBinding.rvMain.setHasFixedSize(true)
        (mBinding.rvMain.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
        mBinding.rvMain.adapter = mAdapter
        //edit name listener
        mAdapter.setOnEditNameClickListener { position,transaction ->
            LogUtils.i(msg = "edit name transaction is = $transaction")
            if (mCenterEditNameDialog == null) {
                mCenterEditNameDialog = CenterEditNameDialog()
            }
            if (mCenterEditNameDialog?.isAdded == false) {
                mCenterEditNameDialog?.show(supportFragmentManager, "EDIT_NAME")
                mCenterEditNameDialog?.setOnEditNameSaveClickListener { newName ->
                    LogUtils.i(msg = "save new name is $newName")
                    transaction.name = newName
                    //update new name
                    mViewModel.updateTransactionByName(this@MainActivity, transaction,position)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // dismiss dialog prevent memory leaks
        mCenterEditNameDialog?.dismiss()
        mCenterEditNameDialog = null
    }
}