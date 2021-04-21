package es.usj.drodriguez.movieapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MotionEvent
import androidx.appcompat.widget.SearchView
import kotlinx.android.synthetic.main.activity_movie_list.*

class MovieList : AppCompatActivity() {
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        val searchMovieList =  menu?.findItem(R.id.btn_search)?.actionView as SearchView
        searchMovieList.queryHint = "Search for a movie"
        return super.onCreateOptionsMenu(menu)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_list)

        setSupportActionBar(toolbar_movieList)
        toolbar_movieList.showOverflowMenu()
        toolbar_movieList.setOnMenuItemClickListener {
            when (it.itemId){
                R.id.btn_contact -> {
                    startActivity(Intent(this, Contact::class.java))
                    true
                }
                else -> false
            }
        }

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