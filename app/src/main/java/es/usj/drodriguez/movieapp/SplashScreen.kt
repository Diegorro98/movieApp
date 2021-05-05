package es.usj.drodriguez.movieapp

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import es.usj.drodriguez.movieapp.database.MovieDatabase
import es.usj.drodriguez.movieapp.databinding.ActivitySplashScreenBinding
import es.usj.drodriguez.movieapp.utils.DatabaseFetcher
import es.usj.drodriguez.movieapp.utils.DatabasePreferences
import es.usj.drodriguez.movieapp.utils.HostDialogFragment
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main


class SplashScreen : AppCompatActivity() {
    private lateinit var binding : ActivitySplashScreenBinding
    private lateinit var loadingAnimation : ObjectAnimator
    private  lateinit var fetcherJob: CompletableJob
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

            //initJob()
            fetcherJob = Job()
            fetcherJob.invokeOnCompletion {
                it?.message.let{it_msg ->
                    var msg = it_msg
                    if(msg.isNullOrBlank()){
                        msg = "Unknown cancellation error."
                    }
                    Log.e("JOB", "${fetcherJob} was cancelled. Reason: ${msg}")
                    GlobalScope.launch(Main){
                        Toast.makeText(this@SplashScreen, msg, Toast.LENGTH_SHORT).show()
                    }
                }

            }
            CoroutineScope(IO + fetcherJob).launch {
                fetchDatabase(fetcherJob)
            }
        }, 1000)//A little bit of time to show the splash screen
    }

    private suspend fun fetchDatabase(job: CompletableJob){
        DatabasePreferences(this).setOnline(true, Context.MODE_PRIVATE)
        var host = DatabasePreferences(this).getHost(Context.MODE_PRIVATE)
        var endedFetcher = false
        var fetch : DatabaseFetcher
        while (!endedFetcher) {
            if (DatabasePreferences(this).isOnline(Context.MODE_PRIVATE)){
                fetch = DatabaseFetcher(host, "admin", "admin")
                GlobalScope.launch(Main){
                    binding.tvSplashscreenInfo.text = getString(R.string.tv_splashscreen_info_connecting)
                }

                if (fetch.ping()){
                    val lastUpdate = DatabasePreferences(this).getLastUpdate(Context.MODE_PRIVATE)
                    GlobalScope.launch(Main) {
                        when (lastUpdate) {
                            0L -> binding.tvSplashscreenInfo.text = getString(R.string.tv_splashscreen_info_first_fetch)
                            else -> binding.tvSplashscreenInfo.text = getString(R.string.tv_splashscreen_info_fetching)
                        }
                    }
                    fetch.updateDatabase(lastUpdate,job,this)
                    endedFetcher = true
                } else {
                    host = DatabaseFetcher.popUpDialog(host, supportFragmentManager)
                    endedFetcher = false
                }
            }else{
                endedFetcher = true
            }
        }
        GlobalScope.launch(Main){
            binding.tvSplashscreenInfo.text = "Update complete"
            startActivity(Intent(this@SplashScreen, MainActivity::class.java))
            finish()
        }
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
        fetcherJob.cancel()
        loadingAnimation.end() //TODO ESTA CORRECTAMENTE APLICADO?
    }
}