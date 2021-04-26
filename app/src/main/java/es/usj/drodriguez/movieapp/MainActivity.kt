package es.usj.drodriguez.movieapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_main, menu)

        //Fav-checkbox configuration (Because it is not possible to configure it on the XML)
        val searchMovieList =  menu?.findItem(R.id.btn_search)?.actionView as SearchView
        searchMovieList.queryHint = getString(R.string.searchMovie)
        val favButton = menu.findItem(R.id.btn_fav)?.actionView as CheckBox
        val favIcon = ContextCompat.getDrawable(this,R.drawable.favorite_check)
        favIcon?.setTint(getColor(R.color.Toolbar_Primary))
        favButton.buttonDrawable  =  favIcon
        //onOptionsItemsSelected no triggered when neither checkbox or fav-checkbox is clicked, so is configured this way
        favButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                Toast.makeText(this,"Favorites on!", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this,"Favorites off!", Toast.LENGTH_SHORT).show()
            }
        }

        return super.onCreateOptionsMenu(menu)
       }
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.btn_contact -> {
            startActivity(Intent(this, Contact::class.java))
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
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