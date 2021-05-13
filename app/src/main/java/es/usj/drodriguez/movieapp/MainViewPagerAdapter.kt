package es.usj.drodriguez.movieapp

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MainViewPagerAdapter (fa: FragmentActivity) : FragmentStateAdapter(fa){
    override fun getItemCount(): Int = 3
    override fun createFragment(position: Int): Fragment {
        when(position){
            0 -> page = Lists.MOVIES
            1 -> page = Lists.GENRES
            2 -> page = Lists.ACTORS
        }
        return Lists.newInstance(page)
    }
    companion object{
        var page: String = ""
    }
}