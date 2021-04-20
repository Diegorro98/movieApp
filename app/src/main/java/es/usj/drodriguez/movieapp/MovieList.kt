package es.usj.drodriguez.movieapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_movie_list.*

class MovieList : AppCompatActivity() {
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_bar_menu, menu);
        return super.onCreateOptionsMenu(menu)
    }
    fun contactClick(mi : MenuItem){
        startActivity(Intent(this, Contact::class.java))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_list)
        btn_addMovie.shrink()
        btn_addMovie.setOnClickListener {
            //startActivity(Intent(this, AddMovie::class.java))
        }
        btn_addMovie.setOnLongClickListener {
            btn_addMovie.extend()
            Handler(Looper.getMainLooper()).postDelayed({
                btn_addMovie.shrink()
            }, 1500)
            return@setOnLongClickListener true
        }
    }
}