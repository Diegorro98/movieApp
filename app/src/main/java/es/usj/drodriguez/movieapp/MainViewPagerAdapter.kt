package es.usj.drodriguez.movieapp

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MainViewPagerAdapter (fa: FragmentActivity) : FragmentStateAdapter(fa){
    override fun getItemCount(): Int = 3
    override fun createFragment(position: Int): Fragment {
        return Lists.newInstance(when(position){
            0 -> Lists.MOVIES
            1 -> Lists.GENRES
            2 -> Lists.ACTORS
            else -> ""
        })
    }
}