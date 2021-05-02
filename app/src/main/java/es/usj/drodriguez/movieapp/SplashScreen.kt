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
            loadingAnimation = ObjectAnimator.ofFloat(binding.ivLoading,"rotation", 0f, 360f)
            loadingAnimation.repeatCount = ObjectAnimator.INFINITE
            loadingAnimation.repeatMode = ObjectAnimator.RESTART
            loadingAnimation.interpolator = AccelerateInterpolator()
            loadingAnimation.start()
            CoroutineScope(Main).launch {
                fetchDatabase()
            }
        }, 1000)//A little bit of time to show the splash screen
    }

    private suspend fun fetchDatabase(){
        DatabasePreferences(this).setOnline(true, Context.MODE_PRIVATE)
        var host = DatabasePreferences(this).getHost(Context.MODE_PRIVATE)
        var endedFetcher = false
        var fetch : DatabaseFetcher
        while (!endedFetcher) {
            if (DatabasePreferences(this).isOnline(Context.MODE_PRIVATE)){
                fetch = DatabaseFetcher(host, "admin", "admin")
                binding.tvSplashscreenInfo.text = getString(R.string.tv_splashscreen_info_connecting)
                if (fetch.ping()){
                    val lastUpdate = DatabasePreferences(this).getLastUpdate(Context.MODE_PRIVATE)
                    when(lastUpdate) {
                        0L -> binding.tvSplashscreenInfo.text = getString(R.string.tv_splashscreen_info_first_fetch)
                        else -> binding.tvSplashscreenInfo.text = getString(R.string.tv_splashscreen_info_fetching)
                    }
                    fetch.updateDatabase(lastUpdate,this)
                    endedFetcher = true
                } else {
                    host = DatabaseFetcher.popUpDialog(host, supportFragmentManager)
                    endedFetcher = false
                }
            }else{
                endedFetcher = true
            }
        }
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
    override fun onPause() {
        super.onPause()
        if (binding.ivLoading.visibility == View.VISIBLE) {
            loadingAnimation.pause()
        }
    }
    override fun onResume() {
        super.onResume()
        if (binding.ivLoading.visibility == View.VISIBLE) {
            loadingAnimation.start()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        loadingAnimation.end() //TODO ESTA CORRECTAMENTE APLICADO?
    }
}