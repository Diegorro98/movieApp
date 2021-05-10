package es.usj.drodriguez.movieapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MainViewPagerAdapter (fa: FragmentActivity) : FragmentStateAdapter(fa){
    companion object{
        private  const val ARG_OBJECT = "object"
    }
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        /*val fragment = Prueba()
        fragment.arguments = Bundle().apply {
            putInt(ARG_OBJECT, position+1)
        }
        return  fragment*/
        return when(position){
            0 -> MovieList()
            1 -> MovieList()//genre list
            2 -> MovieList()//actor_list
            else -> MovieList()
        }
    }
}