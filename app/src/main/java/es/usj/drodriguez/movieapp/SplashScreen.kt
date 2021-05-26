package es.usj.drodriguez.movieapp

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import es.usj.drodriguez.movieapp.database.DatabaseFetcher
import es.usj.drodriguez.movieapp.databinding.ActivitySplashScreenBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main


class SplashScreen : AppCompatActivity() {
    private lateinit var binding : ActivitySplashScreenBinding
    private lateinit var loadingAnimation : ObjectAnimator
    private lateinit var fetcherJob: CompletableJob
    @DelicateCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = binding.root.solidColor
        Handler(Looper.getMainLooper()).postDelayed({
            binding.tvSplashscreenInfo.visibility = View.VISIBLE
            binding.ivLoading.visibility = View.VISIBLE
            loadingAnimation = ObjectAnimator.ofFloat(binding.ivLoading,"rotation", 0f, 360f)
            loadingAnimation.repeatCount = ObjectAnimator.INFINITE
            loadingAnimation.repeatMode = ObjectAnimator.RESTART
            loadingAnimation.interpolator = AccelerateInterpolator()
            loadingAnimation.start()

            //initJob()
            fetcherJob = Job()

            DatabaseFetcher.fetch(this@SplashScreen, fetcherJob, this.application,
                onPing = {
                    GlobalScope.launch(Main){
                        binding.tvSplashscreenInfo.text = getString(R.string.tv_splashscreen_info_connecting)
                    }
                },
                onDownloadAll = {
                    GlobalScope.launch(Main) {
                        binding.tvSplashscreenInfo.text = getString(R.string.tv_splashscreen_info_first_fetch)
                    }
                },
                onUpdate = {
                    GlobalScope.launch(Main) {
                        binding.tvSplashscreenInfo.text = getString(R.string.tv_splashscreen_info_fetching)
                    }
                },
                onFinish = { online ->
                    GlobalScope.launch(Main){
                        binding.tvSplashscreenInfo.text = if (online)getString(R.string.tv_splashscreen_info_update_complete) else getString(R.string.tv_splashscreen_info_offline_mode)
                        startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                        finish()
                    }
                })
        }, 1000 )//A little bit of time to show the splash screen
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
        loadingAnimation.end()
    }
}