package es.usj.drodriguez.movieapp.database

import android.app.Application
import android.content.Context
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.google.gson.Gson
import es.usj.drodriguez.movieapp.database.*
import es.usj.drodriguez.movieapp.database.classes.*
import es.usj.drodriguez.movieapp.utils.DatabaseApp
import es.usj.drodriguez.movieapp.utils.DatabasePreferences
import es.usj.drodriguez.movieapp.utils.HostDialogFragment
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import java.io.*
import java.net.HttpURLConnection
import java.net.InetSocketAddress
import java.net.Socket
import java.net.URL
import java.util.*


class DatabaseFetcher(
    private val hostname: String? = "localhost",
    private val user: String,
    private val password: String
){
    private val gson = Gson()
    private val port = 8888
    fun ping(): Boolean{
        return try {
            val socket =InetSocketAddress(hostname, port)

            // Create an unbound socket
            val sock = Socket()

            // This method will block no more than timeoutMs.
            // If the timeout occurs, SocketTimeoutException is thrown.
            val timeoutMs = 2000 // 2 seconds
            sock.connect(socket, timeoutMs)
            true
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            false
        }
    }
    fun movies(id: Int? = null) : List<MovieFetcher>{
        var url = "http://$hostname:$port/user/getMovies.php?user=$user&pass=$password"
        if (id != null) {
            url += "&id=$id"
        }
        val response = getResponse(URL(url))
        return if(response != null){
            gson.fromJson(response, Array<MovieFetcher>::class.java).toList()
        }else{
            Collections.emptyList()
        }
    }
    fun actors(id: Int? = null) : List<Actor>{
        var url ="http://$hostname:$port/user/getActors.php?user=$user&pass=$password"
        if (id != null) {
            url += "&id=$id"
        }
        val response = getResponse(URL(url))
        return if(response != null){
            gson.fromJson(response, Array<Actor>::class.java).toList()
        }else{
            Collections.emptyList()
        }
    }
    fun genres(id: Int? = null) : List<Genre>{
        var url = "http://$hostname:$port/user/getGenres.php?user=$user&pass=$password"
        if (id != null) {
            url += "&id=$id"
        }
        val response = getResponse(URL(url))
        return if(response != null){
            gson.fromJson(response, Array<Genre>::class.java).toList()
        }else{
            Collections.emptyList()
        }

    }

    private fun getResponse(url: URL): String? {
        val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
        return try {
            val input: InputStream
            input = BufferedInputStream(urlConnection.inputStream)
            readStream(input)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            urlConnection.disconnect()
        }
    }
    private fun readStream(inputStream: InputStream) : String {
        val br = BufferedReader(InputStreamReader(inputStream))
        val total = StringBuilder()
        while (true) {
            val line = br.readLine() ?: break
            total.append(line).append('\n')
        }
        return total.toString()
    }
    suspend fun updateDatabase(lastUpdate : Long, job: CompletableJob, application: Application){
        val repository = (application as DatabaseApp).repository
        when(lastUpdate){
            0L ->  {
                withContext(IO + job){
                    listOf(
                            launch{
                                val rawMovies = movies()
                                repository.insertMovies(List(rawMovies.size){
                                    rawMovies[it].asNormalMovie()
                                })
                                rawMovies.forEach { movie ->
                                    movie.asMovieActors().forEach {
                                        repository.insertMovieActor(it)
                                    }
                                }
                                rawMovies.forEach { movie ->
                                    movie.asMovieGenres().forEach {
                                        repository.insertMovieGenre(it)
                                    }
                                }
                            },
                            launch {
                                //database.insertActors(actors())
                                repository.insertActors(actors())
                            },
                            launch {
                                repository.insertGenres(genres())
                            }
                    ).joinAll()
                    println("All ended")
                }
            }
            else -> {
                //TODO
                //GetUpdates
                //Get updateTable
                //Download elements that changed
            }
        }

    }
    companion object{
        /**
         * Allows to do fetch the database by taking the necessary info from shared preferences and stores the data in the database. The anonymous functions are run in a coroutine,
         * so if you want to do things in Main thread, you should call `GlobalScope.launch(Main){}`
         * @param context Context for the sharedPreferences and the supportFragmentManager
         * @param job Job to get its own environment in coroutines resulting in a CoroutineScope(IO + job)
         * @param onPing Set of actions to do when the fetcher is going to do when pings the server
         * @param onDownloadAll Set of actions to do when the fetcher is going to do when downloads all the database from the server because it never had an first download
         * @param onUpdate Set of actions to do when the fetcher is going to do when updates the database
         * @param onFinish Set of actions to do when the fetcher is going to do when finishes its job
         */
        fun fetch(@NonNull context: Context, @NonNull job: CompletableJob = Job(), application: Application, onPing: ( () -> Unit)? = null, onDownloadAll: ( () -> Unit)? = null, onUpdate: (() -> Unit)? = null, onFinish: ( (Boolean) -> Unit)? = null){
            DatabasePreferences(context).setOnline(true, Context.MODE_PRIVATE)

            var host = DatabasePreferences(context).getHost(Context.MODE_PRIVATE)
            var endedFetcher = false
            var fetcher: DatabaseFetcher
            val lastUpdate = DatabasePreferences(context).getLastUpdate(Context.MODE_PRIVATE)
            val manager : FragmentManager = (context as FragmentActivity).supportFragmentManager //TODO is save to do this cast? It works, but...
            CoroutineScope(IO + job).launch {
                while (!endedFetcher) {
                    if (DatabasePreferences(context).isOnline(Context.MODE_PRIVATE)) {
                        fetcher = DatabaseFetcher(host, "admin", "admin")
                        onPing?.invoke()
                        if (fetcher.ping()) {
                            when (lastUpdate) {
                                0L -> onDownloadAll?.invoke()
                                else -> onUpdate?.invoke()
                            }
                            fetcher.updateDatabase(lastUpdate, job, application)
                            endedFetcher = true
                        } else {
                            host = popUpDialog(host, manager)
                            endedFetcher = false
                        }
                    } else {
                        endedFetcher = true
                    }
                }
                onFinish?.invoke(DatabasePreferences(context).isOnline(Context.MODE_PRIVATE))
                job.complete()
            }
        }
        private suspend fun popUpDialog(hostName: String?=null, @NonNull manager:FragmentManager): String?{
            val dialog = HostDialogFragment(hostName)
            dialog.show(manager, "host_input")
            while (!dialog.end) { //wait for the dialog to end
                delay(100) //to avoid thread blocking
            }
            return dialog.hostName
        }
    }
}