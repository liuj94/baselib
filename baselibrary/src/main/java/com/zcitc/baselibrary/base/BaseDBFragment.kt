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
        //setUserVisibleHint()有可能在fragment的生命周期外被调用
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
     * 设置是否使用 view 的复用，默认开启
     * view 的复用是指，ViewPager 在销毁和重建 Fragment 时会不断调用 onCreateView() -> onDestroyView()
     * 之间的生命函数，这样可能会出现重复创建 view 的情况，导致界面上显示多个相同的 Fragment
     * view 的复用其实就是指保存第一次创建的 view，后面再 onCreateView() 时直接返回第一次创建的 view
     *
     * @param isReuse
     */
    protected open fun reuseView(isReuse: Boolean) {
        isReuseView = isReuse
    }

    /**
     * 去除setUserVisibleHint()多余的回调场景，保证只有当fragment可见状态发生变化时才回调
     * 回调时机在view创建完后，所以支持ui操作，解决在setUserVisibleHint()里进行ui操作有可能报null异常的问题
     *
     * 可在该回调方法里进行一些ui显示与隐藏，比如加载框的显示和隐藏
     *
     * @param isVisible true  不可见 -> 可见
     * false 可见  -> 不可见
     */
    protected open fun onFragmentVisibleChange(isVisible: Boolean) {}

    /**
     * 在fragment首次可见时回调，可在这里进行加载数据，保证只在第一次打开Fragment时才会加载数据，
     * 这样就可以防止每次进入都重复加载数据
     * 该方法会在 onFragmentVisibleChange() 之前调用，所以第一次打开时，可以用一个全局变量表示数据下载状态，
     * 然后在该方法内将状态设置为下载状态，接着去执行下载的任务
     * 最后在 onFragmentVisibleChange() 里根据数据下载状态来控制下载进度ui控件的显示与隐藏
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