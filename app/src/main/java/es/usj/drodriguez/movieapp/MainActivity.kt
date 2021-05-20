package es.usj.drodriguez.movieapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import es.usj.drodriguez.movieapp.databinding.ActivityMainBinding
import es.usj.drodriguez.movieapp.editors.*
import es.usj.drodriguez.movieapp.utils.DatabaseApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.util.*


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

    private val adapter by lazy { MainViewPagerAdapter(this) }

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.pager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
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
        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                contextualToolbar?.finish()
                tabPosition = when(tab.position){
                    0 -> Lists.MOVIES
                    1 -> Lists.GENRES
                    2 -> Lists.ACTORS
                    else -> ""
                }
                println(tab.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        setSupportActionBar(binding.toolbarMovieList)
        binding.toolbarMovieList.showOverflowMenu()

        binding.btnAddMovie.shrink()
        binding.btnAddMovie.setOnClickListener {
            CoroutineScope(IO).launch{
                startActivity(when(tabPosition){
                    Lists.MOVIES -> TODO() /*Intent(this@MainActivity, MovieEditor::class.java)
                        .putExtra(MovieEditor.OBJECT, (application as DatabaseApp).repository.getNewMovie())
                        .putExtra(MovieEditor.NEW, true)*/
                    Lists.GENRES -> Intent(this@MainActivity, ActorGenreEditor::class.java)
                        .putExtra(ActorGenreEditor.CLASS, ActorGenreEditor.GENRE)
                        .putExtra(ActorGenreEditor.OBJECT, (application as DatabaseApp).repository.getNewGenre())
                    Lists.ACTORS -> Intent(this@MainActivity, ActorGenreEditor::class.java)
                        .putExtra(ActorGenreEditor.CLASS, ActorGenreEditor.ACTOR)
                        .putExtra(ActorGenreEditor.OBJECT, (application as DatabaseApp).repository.getNewActor())
                    else -> null
                })
            }
        }
        binding.btnAddMovie.setOnLongClickListener {
            binding.btnAddMovie.extend()
            Handler(Looper.getMainLooper()).postDelayed({
                binding.btnAddMovie.shrink()
            }, 2000)
            return@setOnLongClickListener true
        }
    }
    companion object{
        var contextualToolbar: ActionMode? = null
        var tabPosition: String = Lists.MOVIES
    }
}