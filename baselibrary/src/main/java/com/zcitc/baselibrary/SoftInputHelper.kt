package com.zcitc.baselibrary

import android.app.Activity
import android.view.inputmethod.InputMethodManager

import android.content.Context.INPUT_METHOD_SERVICE

fun Activity.showSoftInput() = currentFocus?.let { (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(it, 0) }

fun Activity.hideSoftInput() = currentFocus?.let { (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(it.windowToken, 0) }

