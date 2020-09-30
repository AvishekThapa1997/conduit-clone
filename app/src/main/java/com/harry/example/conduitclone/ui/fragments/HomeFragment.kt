package com.harry.example.conduitclone.ui.fragments


import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.harry.example.conduitclone.R
import com.harry.example.conduitclone.helper.ViewPagerAdapter
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*



class HomeFragment : BaseFragment() {

    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var callback: OnBackPressedCallback
    private lateinit var viewPagerPageCallBack: ViewPager2.OnPageChangeCallback
    private var isNewArticlePosted = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (view_pager.currentItem != 0) {
                view_pager.currentItem = view_pager.currentItem - 1
            } else {
                activity?.finishAfterTransition()
            }
        }
        viewPagerPageCallBack = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == 0 && arguments != null && isNewArticlePosted) {
                    view_pager.currentItem = 1
                    isNewArticlePosted = false
                }
            }
        }
    }

    override val layoutId = R.layout.fragment_home


    override fun onResume() {
        super.onResume()
        view?.view_pager?.registerOnPageChangeCallback(viewPagerPageCallBack)
        callback.isEnabled = true
    }

    override fun onPause() {
        super.onPause()
        callback.isEnabled = false
        view_pager.unregisterOnPageChangeCallback(viewPagerPageCallBack)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewPagerAdapter =
            ViewPagerAdapter(
                childFragmentManager,
                viewLifecycleOwner.lifecycle,
            )
        view_pager.adapter = viewPagerAdapter
        TabLayoutMediator(tab_layout, view_pager) { tab, position ->
            run {
                when (position) {
                    0 -> {
                        tab.text = getTabTitle(R.string.global_feed)
                    }
                    else -> {
                        tab.text = getTabTitle(R.string.your_feed)
                    }
                }
            }
        }.attach()
        view_pager.offscreenPageLimit = 2
    }


    private fun getTabTitle(id: Int): String? {
        return context?.resources?.getString(id)
    }

    override fun onDestroyView() {
        view_pager.adapter = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        callback.remove()
    }

}