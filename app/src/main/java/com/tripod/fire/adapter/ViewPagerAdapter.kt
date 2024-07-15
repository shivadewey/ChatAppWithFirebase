package com.tripod.fire.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tripod.fire.fragments.TabContainFragment

class ViewPagerAdapter(
    fragmentActivity: TabContainFragment,
    private var list:ArrayList<Fragment>,
    private var title:ArrayList<String>
):FragmentStateAdapter(fragmentActivity){
    override fun getItemCount(): Int {
        return list.size
    }

    override fun createFragment(position: Int): Fragment {
        return  list[position]
    }

    fun getPageTitle(position: Int): CharSequence? {
        return title.getOrNull(position)
    }
}