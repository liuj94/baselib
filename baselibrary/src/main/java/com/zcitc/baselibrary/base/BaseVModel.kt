package com.zcitc.baselibrary.base

import android.annotation.SuppressLint
import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.*

open class BaseVModel : ViewModel() {
    lateinit var mContext : Activity

    var isShowLoading: MutableLiveData<Boolean> = MutableLiveData()
    fun isShowLoadingLiveData(): LiveData<Boolean> {
        return isShowLoading
    }

    fun killMyself(){
        if(mContext!=null){
            mContext.finish()
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getDataString() : String {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        val date = Date(System.currentTimeMillis())
        return simpleDateFormat.format(date)
    }
}