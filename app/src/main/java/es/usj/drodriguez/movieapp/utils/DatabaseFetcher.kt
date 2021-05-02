package es.usj.drodriguez.movieapp.utils

import android.content.Context
import android.content.Intent
import androidx.annotation.NonNull
import com.google.gson.Gson
import es.usj.drodriguez.movieapp.MainActivity
import es.usj.drodriguez.movieapp.R
import es.usj.drodriguez.movieapp.database.*
import kotlinx.coroutines.*
import java.io.*
import java.lang.IllegalArgumentException
import java.net.HttpURLConnection
import java.net.InetSocketAddress
import java.net.Socket
import java.net.URL
import java.nio.file.Files.exists
import java.util.*


class DatabaseFetcher(
    private val hostname: String? = "localhost",
    private val user: String,
    private val password: String
){
    private val gson = Gson()
    private val port = 8888
    suspend fun ping(): Boolean{
        try {
            val socket =InetSocketAddress(hostname, port)

            // Create an unbound socket
            val sock = Socket()

            // This method will block no more than timeoutMs.
            // If the timeout occurs, SocketTimeoutException is thrown.
            val timeoutMs = 2000 // 2 seconds
            withContext(Dispatchers.IO) {
                sock.connect(socket, timeoutMs)
            }
            return true
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            return false
        }
    }
    suspend fun movies(id: Int? = null) : List<Movie>{
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
    suspend fun actors(id: Int? = null) : List<Actor>{
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
    suspend fun genres(id: Int? = null) : List<Genre>{
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

    private suspend fun getResponse(url: URL): String? {
        val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
        return try {
            val input: InputStream
            withContext(Dispatchers.IO) {
                 input = BufferedInputStream(urlConnection.inputStream)
            }
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
    suspend fun updateDatabase(lastUpdate : Long, context: Context){
        val database = MovieDatabase.getDatabase(context)
        var fetchMovies = false; var fetchActors = false; var fetchGenre = false
        when(lastUpdate){
            0L->{
                CoroutineScope(Dispatchers.IO).launch {
                    val ret = movies()
                    database?.movieDao()?.insertAll(ret)
                    fetchMovies = true
                }
                CoroutineScope(Dispatchers.IO).launch {
                    val ret = actors()
                    database?.actorDao()?.insertAll(ret)
                    fetchActors = true
                }
                CoroutineScope(Dispatchers.IO).launch {
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
        suspend fun fetchDatabase(context: Context, @NonNull manager:androidx.fragment.app.FragmentManager){
            DatabasePreferences(context).setOnline(true, Context.MODE_PRIVATE)
            var host = DatabasePreferences(context).getHost(Context.MODE_PRIVATE)
            var endedFetcher = false
            var fetch : DatabaseFetcher
            while (!endedFetcher) {
                if (DatabasePreferences(context).isOnline(Context.MODE_PRIVATE)){
                    fetch = DatabaseFetcher(host, "admin", "admin")
                    if (fetch.ping()){
                        fetch.updateDatabase(DatabasePreferences(context).getLastUpdate(Context.MODE_PRIVATE),context)
                        endedFetcher = true
                    } else {
                        host = popUpDialog(host, manager)
                        endedFetcher = false
                    }
                }else{
                    endedFetcher = true
                }
            }
        }
        suspend fun popUpDialog(hostName: String?=null, @NonNull manager:androidx.fragment.app.FragmentManager): String?{
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