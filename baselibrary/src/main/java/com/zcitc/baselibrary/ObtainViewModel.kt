package com.zcitc.baselibrary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

fun <T : ViewModel> obtainViewModel(owner: ViewModelStoreOwner, viewModelClass: Class<T>) =
    ViewModelProvider(owner, ViewModelProvider.NewInstanceFactory()).get(viewModelClass)