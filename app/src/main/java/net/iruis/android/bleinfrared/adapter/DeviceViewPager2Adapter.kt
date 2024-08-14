package net.iruis.android.bleinfrared.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import net.iruis.android.bleinfrared.fragment.AirConditionerFragment
import net.iruis.android.bleinfrared.fragment.TvFragment

class DeviceViewPager2Adapter(
    private val fragmentManager: FragmentManager,
    private val lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return AirConditionerFragment.newInstance()
            1 -> return TvFragment.newInstance()
        }
        throw IllegalArgumentException("position")
    }

    fun getPageTitle(position: Int): CharSequence {
        when (position) {
            0 -> return "Whisen"
            1 -> return "TV"
        }
        throw IllegalArgumentException("position")
    }
}