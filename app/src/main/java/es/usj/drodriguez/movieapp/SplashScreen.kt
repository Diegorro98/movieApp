package es.usj.drodriguez.movieapp

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import es.usj.drodriguez.movieapp.database.MovieDatabase
import es.usj.drodriguez.movieapp.databinding.ActivitySplashScreenBinding
import es.usj.drodriguez.movieapp.utils.DatabaseFetcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch


class SplashScreen : AppCompatActivity() {
    private lateinit var binding : ActivitySplashScreenBinding
    private lateinit var loadingAnimation : ObjectAnimator
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = Color.WHITE
        /*
        Load what it's need and then...
        startActivity(Intent(this, MovieList::class.java))
        finish()*/
        binding.tvSplashscreenInfo.text = "Loading whatever" //TODO
        binding.tvSplashscreenInfo.visibility = View.VISIBLE
        binding.ivLoading.visibility = View.VISIBLE
        fetchDatabase()
        loadingAnimation = ObjectAnimator.ofFloat(binding.ivLoading,"rotation", 0f, 360f)
        loadingAnimation.repeatCount = ObjectAnimator.INFINITE
        loadingAnimation.repeatMode = ObjectAnimator.RESTART
        loadingAnimation.interpolator = AccelerateInterpolator()
        loadingAnimation.start()

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 6000)
    }

    private fun fetchDatabase(){
        val fetch = DatabaseFetcher()
        val database = MovieDatabase.getDatabase(this)
        val databasePreferences = getSharedPreferences("databaseInfo", Context.MODE_PRIVATE)
        val databasePreferencesEditor = databasePreferences.edit()
        var fetchMovies = false
        var fetchActors = false
        var fetchGenre = false
        when(val lastUpdate = databasePreferences.getLong("last_update", 0)){
            0L ->{
                CoroutineScope(IO).launch {
                    val ret = fetch.movies()
                    database?.movieDao()?.insertAll(ret)
                    fetchMovies = true
                }
                CoroutineScope(IO).launch {
                    val ret = fetch.actors()
                    database?.actorDao()?.insertAll(ret)
                    fetchActors = true
                }
                CoroutineScope(IO).launch {
                    val ret = fetch.genres()
                    database?.genreDao()?.insertAll(ret)
                    fetchGenre = true
                }
                //Get last update number and record it in database Preferences
                /*CoroutineScope(IO).launch {
                    while(!fetchMovies && !fetchActors && !fetchGenre ){}
                    databasePreferencesEditor.putLong("last_update", System.currentTimeMillis()/1000)
                }*/
                //delete up there when it is correctly get the timestamp
            }
            else -> {
                //GetUpdates
                //Get updateTable
                //Download elements that changed
                println("it has been updated before, TIMESTAMP:$lastUpdate")
            }
        }
        databasePreferencesEditor.apply()

    }

    override fun onPause() {
        super.onPause()
        loadingAnimation.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        loadingAnimation.end() //TODO ESTA CORRECTAMENTE APLICADO?
    }
}