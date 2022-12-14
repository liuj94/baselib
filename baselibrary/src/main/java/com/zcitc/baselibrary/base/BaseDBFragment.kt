package com.zcitc.baselibrary.base

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.zcitc.baselibrary.StatusBarUtil
import com.zcitc.baselibrary.obtainViewModel


open abstract class BaseDBFragment <T : BaseVModel ,VB : ViewBinding>(
private val inflate: (LayoutInflater, ViewGroup?, Boolean) -> VB
) :  Fragment(), IBaseFragment{
    private var isFragmentVisible = false
    private var isReuseView = false
    private var isFirstVisible = false
    private var rootView: View? = null

    var mProgressDialog: ProgressDialog? = null

    val mViewModel : T by lazy {
        obtainViewModel(this,getViewModel())
    }
    lateinit var mBinding: VB
   fun bindingisInitialized():Boolean = this::mBinding.isInitialized


    abstract fun getViewModel(): Class<T>
   var myContainer: ViewGroup? = null
   var myInflater: LayoutInflater? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mProgressDialog = ProgressDialog(requireActivity())
        mViewModel.mContext = requireActivity()

        this.myContainer = container
        this.myInflater = inflater
        mBinding = inflate(inflater, container, false)

        return mBinding.root
//        return initView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        if (rootView == null) {
            rootView = view
            if (userVisibleHint) {
                if (isFirstVisible) {
                    onFragmentFirstVisible()
                    isFirstVisible = false
                }
                onFragmentVisibleChange(true)
                isFragmentVisible = true
            }
        }
        super.onViewCreated((if (isReuseView) rootView!! else view), savedInstanceState)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initData(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        initVariable();
    }
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        //setUserVisibleHint()????????????fragment???????????????????????????
        if (rootView == null) {
            return
        }
        if (isFirstVisible && isVisibleToUser) {
            onFragmentFirstVisible()
            isFirstVisible = false
        }
        if (isVisibleToUser) {
            onFragmentVisibleChange(true)
            isFragmentVisible = true
            return
        }
        if (isFragmentVisible) {
            isFragmentVisible = false
            onFragmentVisibleChange(false)
        }
    }
    private  fun initVariable() {
        isFirstVisible = true
        isFragmentVisible = false
        rootView = null
        isReuseView = true
    }

    /**
     * ?????????????????? view ????????????????????????
     * view ??????????????????ViewPager ?????????????????? Fragment ?????????????????? onCreateView() -> onDestroyView()
     * ????????????????????????????????????????????????????????? view ???????????????????????????????????????????????? Fragment
     * view ???????????????????????????????????????????????? view???????????? onCreateView() ????????????????????????????????? view
     *
     * @param isReuse
     */
    protected open fun reuseView(isReuse: Boolean) {
        isReuseView = isReuse
    }

    /**
     * ??????setUserVisibleHint()???????????????????????????????????????fragment????????????????????????????????????
     * ???????????????view???????????????????????????ui??????????????????setUserVisibleHint()?????????ui??????????????????null???????????????
     *
     * ????????????????????????????????????ui???????????????????????????????????????????????????
     *
     * @param isVisible true  ????????? -> ??????
     * false ??????  -> ?????????
     */
    protected open fun onFragmentVisibleChange(isVisible: Boolean) {}

    /**
     * ???fragment????????????????????????????????????????????????????????????????????????????????????Fragment????????????????????????
     * ??????????????????????????????????????????????????????
     * ??????????????? onFragmentVisibleChange() ????????????????????????????????????????????????????????????????????????????????????????????????
     * ????????????????????????????????????????????????????????????????????????????????????
     * ????????? onFragmentVisibleChange() ????????????????????????????????????????????????ui????????????????????????
     */
    protected open fun onFragmentFirstVisible() {}

    protected open fun isFragmentVisible(): Boolean {
        return isFragmentVisible
    }

     fun setStatusBarHeight( toolbarView : View) {
        var height = StatusBarUtil.getStatusBarHeight(activity)
        val display = requireActivity().windowManager.defaultDisplay
        val layoutParams = toolbarView.layoutParams
        layoutParams.height = height
        layoutParams.width = display.width
        toolbarView.layoutParams = layoutParams

    }

}