package es.usj.drodriguez.movieapp.utils

import android.content.Context
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentManager
import com.google.gson.Gson
import es.usj.drodriguez.movieapp.database.*
import es.usj.drodriguez.movieapp.database.classes.*
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
    fun movies(id: Int? = null) : List<Movie>{
        var url = "http://$hostname:$port/user/getMovies.php?user=$user&pass=$password"
        if (id != null) {
            url += "&id=$id"
        }
        val response = getResponse(URL(url))
        return if(response != null){
            gson.fromJson(response, Array<Movie>::class.java).toList()
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
    suspend fun updateDatabase(lastUpdate : Long, job: CompletableJob, context: Context){
        val database = MovieDatabase.getDatabase(context)
        var fetchMovies = false; var fetchActors = false; var fetchGenre = false
        when(lastUpdate){
            0L->{
                CoroutineScope(IO + job ).launch {
                    val ret = movies()
                    database?.movieDao()?.insertAll(ret)
                    fetchMovies = true
                }
                CoroutineScope(IO + job ).launch {
                    val ret = actors()
                    database?.actorDao()?.insertAll(ret)
                    fetchActors = true
                }
                CoroutineScope(IO + job ).launch {
                    val ret = genres()
                    database?.genreDao()?.insertAll(ret)
                    fetchGenre = true
                }
                while (!fetchMovies && !fetchActors && !fetchGenre) {
                    delay(100)
                }
            }
            else -> {
                //
                //TODO
                //GetUpdates
                //Get updateTable
                //Download elements that changed
            }
        }

    }
    companion object{
        suspend fun fetch(@NonNull context: Context, @NonNull job: CompletableJob, @NonNull manager: FragmentManager, onPing: ( () -> Unit)? = null, onDownloadAll: ( () -> Unit)? = null, onUpdate: (() -> Unit)? = null, onFinish: ( () -> Unit)? = null){
            DatabasePreferences(context).setOnline(true, Context.MODE_PRIVATE)

            var host = DatabasePreferences(context).getHost(Context.MODE_PRIVATE)
            var endedFetcher = false
            var fetch : DatabaseFetcher
            var lastUpdate : Long

            while (!endedFetcher) {
                if (DatabasePreferences(context).isOnline(Context.MODE_PRIVATE)){
                    fetch = DatabaseFetcher(host, "admin", "admin")
                    onPing?.invoke()
                    if (fetch.ping()){
                        lastUpdate =DatabasePreferences(context).getLastUpdate(Context.MODE_PRIVATE)
                        when (lastUpdate) {
                            0L -> onDownloadAll?.invoke()
                            else -> onUpdate?.invoke()
                        }
                        fetch.updateDatabase(lastUpdate, job, context)
                        endedFetcher = true
                    } else {
                        host = popUpDialog(host, manager)
                        endedFetcher = false
                    }
                }else{
                    endedFetcher = true
                }
            }
            onFinish?.invoke()
        }
        private suspend fun popUpDialog(hostName: String?=null, @NonNull manager:FragmentManager): String?{
            val dialog = HostDialogFragment(hostName)
            dialog.show(manager, "host_input")
            while (!dialog.end) { //wait for the dialog to end
                delay(100) //to avoid thread blocking
            }
            println(dialog.hostName)
            return dialog.hostName
        }
    }
}