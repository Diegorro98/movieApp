package es.usj.drodriguez.movieapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.widget.CheckBox
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_main, menu)

        val searchMovieList =  menu?.findItem(R.id.btn_search)?.actionView as SearchView
        searchMovieList.queryHint = getString(R.string.searchMovie)

        val fav = menu.findItem(R.id.btn_fav)?.actionView as CheckBox
        fav.buttonDrawable  =  ContextCompat.getDrawable(this,R.drawable.outline_favorite_border_24)
        fav.isChecked = false
        return super.onCreateOptionsMenu(menu)
       }

    override fun onOptionsItemSelected(item: MenuItem) :Boolean{
        when (item.itemId) {
            R.id.btn_fav -> {
                val fav = item as CheckBox
                if (fav.isChecked) {
                    fav.buttonDrawable = ContextCompat.getDrawable(this, R.drawable.outline_favorite_border_24)
                } else {
                    fav.buttonDrawable = ContextCompat.getDrawable(this, R.drawable.outline_favorite_24)
                }
                fav.isChecked = !fav.isChecked
                return true
            }
            R.id.btn_contact -> {
                startActivity(Intent(this, Contact::class.java))
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }
    private val adapter by lazy { ViewPagerAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pager.adapter = adapter
        TabLayoutMediator(tabLayout, pager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = getString(R.string.movieList_title)
                }
                1 -> {
                    tab.text = getString(R.string.movieList_genre)
                }
                2 -> {
                    tab.text = getString(R.string.movieList_actors)
                }
            }
        }.attach()

        setSupportActionBar(toolbar_movieList)
        toolbar_movieList.showOverflowMenu()

        btn_addMovie.shrink()
        btn_addMovie.setOnClickListener {
            //startActivity(Intent(this, AddMovie::class.java))
        }
        btn_addMovie.setOnLongClickListener {
            btn_addMovie.extend()
            Handler(Looper.getMainLooper()).postDelayed({
                btn_addMovie.shrink()
            }, 2000)
            return@setOnLongClickListener true
        }
    }
}