package es.usj.drodriguez.movieapp

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MainViewPagerAdapter (fa: FragmentActivity) : FragmentStateAdapter(fa){
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> Lists.newInstance(Lists.MOVIES)
            1 -> Lists.newInstance(Lists.MOVIES)//genre list
            2 -> Lists.newInstance(Lists.ACTORS)//actor_list
            else -> Lists()
        }
    }
}