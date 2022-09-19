package com.zcitc.baselibrary.base


import android.app.ProgressDialog
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.text.method.Touch.onTouchEvent
import android.view.InflateException
import android.view.LayoutInflater
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import com.zcitc.baselibrary.CheckSysUtils
import com.zcitc.baselibrary.R
import com.zcitc.baselibrary.SignCheck
import com.zcitc.baselibrary.StatusBarUtil
import com.zcitc.baselibrary.hideSoftInput
import com.zcitc.baselibrary.obtainViewModel


import kotlin.system.exitProcess

 open abstract class BaseActivity<T : BaseVModel, VB : ViewBinding>(private val inflate: (LayoutInflater) -> VB) : AppCompatActivity(), IBaseActivity {
    private var mProgressDialog: ProgressDialog? = null

     var mContext: FragmentActivity = this@BaseActivity


    val mBinding: VB by lazy {
        inflate(layoutInflater)
    }
    val mViewModel: T by lazy {
        obtainViewModel(this, getViewModel())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.left_in, R.anim.left_out)
        super.onCreate(savedInstanceState)
        mContext = this@BaseActivity
        val signCheck: SignCheck = SignCheck(this)
        // 第2步查看签名指纹的SHA1
        if (!signCheck.check()) {
            // 签名异常
            exitProcess(0)
//                signCheck.showCheckErrorTips()
            return
        }
        //判断设备是否被Root，如果已被Root，则直接退出应用；
        if (CheckSysUtils.isDeviceRooted()) {
            signCheck.showCheckRootTips()
            return
        }
        StatusBarUtil.setTranslucentStatus(this)
        try {
            val layoutResID = initView(savedInstanceState)
            if (layoutResID != 0) {
//                mBinding = inflate(layoutInflater)
                setContentView(mBinding.root)

            }
        } catch (e: Exception) {
            if (e is InflateException) throw e
            e.printStackTrace()
        }

        mProgressDialog = ProgressDialog(this)
        mViewModel.mContext = this
        initData(savedInstanceState)
        mViewModel.isShowLoadingLiveData().observe(this, Observer {
            if (it) {
                showLoading()
            } else {
                hideLoading()
            }
        })
    }

    abstract fun getViewModel(): Class<T>


    fun showLoading() {
        if (mProgressDialog?.isShowing == false)
            mProgressDialog?.show()
    }

    fun hideLoading() {
        if (mProgressDialog?.isShowing == true)
            mProgressDialog?.dismiss()
    }


    //隐藏软键盘
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            hideSoftInput()
            return super.dispatchTouchEvent(ev)
        }
        return if (window.superDispatchTouchEvent(ev)) {
            true
        } else onTouchEvent(ev)
    }



    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.right_in, R.anim.right_out)
    }

    override fun getResources(): Resources? {
        val res: Resources = super.getResources()
        val config = Configuration()
        config.setToDefaults()
        res.updateConfiguration(config, res.displayMetrics)
        return res
    }




}