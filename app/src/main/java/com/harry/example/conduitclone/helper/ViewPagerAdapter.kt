package com.harry.example.conduitclone.helper


import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.harry.example.conduitclone.ui.fragments.GlobalFeed
import com.harry.example.conduitclone.ui.fragments.UserFeed


class ViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    private val globalFeed = GlobalFeed()
    private val userFeed = UserFeed()
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> globalFeed
            else -> userFeed
            }
        }
    }
