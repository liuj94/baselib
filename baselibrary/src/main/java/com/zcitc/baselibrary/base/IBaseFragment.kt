package com.zcitc.baselibrary.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


interface IBaseFragment {
    fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View

    fun initData(savedInstanceState: Bundle?)

    fun setData(data: Any?)

}
