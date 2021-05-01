package es.usj.drodriguez.movieapp

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import es.usj.drodriguez.movieapp.database.MovieDatabase
import es.usj.drodriguez.movieapp.databinding.ActivitySplashScreenBinding
import es.usj.drodriguez.movieapp.utils.DatabaseFetcher
import es.usj.drodriguez.movieapp.utils.DatabasePreferences
import es.usj.drodriguez.movieapp.utils.HostDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SplashScreen : AppCompatActivity() {
    private lateinit var binding : ActivitySplashScreenBinding
    private lateinit var loadingAnimation : ObjectAnimator
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = Color.WHITE
        Handler(Looper.getMainLooper()).postDelayed({
        binding.tvSplashscreenInfo.visibility = View.VISIBLE
        binding.ivLoading.visibility = View.VISIBLE
        CoroutineScope(Main).launch {
            fetchDatabase()
        }
        loadingAnimation = ObjectAnimator.ofFloat(binding.ivLoading,"rotation", 0f, 360f)
        loadingAnimation.repeatCount = ObjectAnimator.INFINITE
        loadingAnimation.repeatMode = ObjectAnimator.RESTART
        loadingAnimation.interpolator = AccelerateInterpolator()
        loadingAnimation.start()
        }, 1000)
    }

    private suspend fun fetchDatabase(){
        var host = DatabasePreferences(this).getHost(Context.MODE_PRIVATE)
        var endedFetcher = false
        var fetch : DatabaseFetcher
        val database = MovieDatabase.getDatabase(this)
        var fetchMovies = false; var fetchActors = false; var fetchGenre = false
        while (!endedFetcher) {
            if (host == "" || host == null) {
                host = DatabaseFetcher.popUpDialog(manager = supportFragmentManager)
                endedFetcher = false
            } else {
                if (DatabasePreferences(this).isOnline(Context.MODE_PRIVATE)){
                    fetch = DatabaseFetcher(host, "admin", "admin")
                    binding.tvSplashscreenInfo.text = getString(R.string.tv_splashscreen_info_connecting)
                    if (fetch.ping()){
                        when(val lastUpdate = DatabasePreferences(this).getLastUpdate(Context.MODE_PRIVATE)) {
                            0L -> {
                                binding.tvSplashscreenInfo.text = getString(R.string.tv_splashscreen_info_first_fetch)
                                fetch.downloadAll(this)
                            }
                            else -> {
                                binding.tvSplashscreenInfo.text = getString(R.string.tv_splashscreen_info_fetching)
                                //GetUpdates
                                //Get updateTable
                                //Download elements that changed
                                println("it has been updated before, TIMESTAMP:$lastUpdate")
                            }
                        }
                        endedFetcher = true
                    } else {
                        host = DatabaseFetcher.popUpDialog(host, supportFragmentManager)
                        endedFetcher = false
                    }
                }else{
                    endedFetcher = true
                }
            }
        }
        startActivity(Intent(this, MainActivity::class.java))
        finish()
        println("Host${DatabasePreferences(this).getHost(Context.MODE_PRIVATE)}")
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