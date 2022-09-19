package com.zcitc.baselibrary.base

import android.content.Intent

/**
 * ================================================
 * 框架要求框架中的每个 View 都需要实现此类,以满足规范
 *
 * @see [View wiki 官方文档](https://github.com/JessYanCoding/MVPArms/wiki.2.4.2)
 * Created by JessYan on 4/22/2016
 * [Contact me](mailto:jess.yan.effort@gmail.com)
 * [Follow me](https://github.com/JessYanCoding)
 * ================================================
 */
interface IView {

    /**
     * 显示加载
     */
    fun showLoading()

    /**
     * 隐藏加载
     */
    fun hideLoading()

    /**
     * 显示信息
     *
     * @param message 消息内容, 不能为 `null`
     */
    fun showMessage(message: String)

    /**
     * 跳转
     *
     * @param intent `intent` 不能为 `null`
     */
    fun launchActivity(intent: Intent)

    /**
     * 杀死自己
     */
    fun killMyself()
}
