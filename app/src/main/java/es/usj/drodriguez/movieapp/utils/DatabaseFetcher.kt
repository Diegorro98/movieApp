package es.usj.drodriguez.movieapp.utils

import android.content.Context
import com.google.gson.Gson
import es.usj.drodriguez.movieapp.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class DatabaseFetcher{
    private val gson = Gson()
    private val host = "192.168.0.12"
    private val user = "admin"
    private val password = "admin"
    suspend fun movies(id: Int? = null) : List<Movie>{
        var url = "http://$host:8888//user/getMovies.php?user=$user&pass=$password"
        if (id != null) {
            url += "&id=$id"
        }
        val response = getResponse(URL(url))
        return if(response != null){
            gson.fromJson(response,Array<Movie>::class.java).toList()
        }else{
            Collections.emptyList()
        }
    }
    suspend fun actors(id: Int? = null) : List<Actor>{
        var url ="http://$host:8888/user/getActors.php?user=$user&pass=$password"
        if (id != null) {
            url += "&id=$id"
        }
        val response = getResponse(URL(url))
        return if(response != null){
            gson.fromJson(response,Array<Actor>::class.java).toList()
        }else{
            Collections.emptyList()
        }
    }
    suspend fun genres(id: Int? = null) : List<Genre>{
        var url = "http://$host:8888/user/getGenres.php?user=$user&pass=$password"
        if (id != null) {
            url += "&id=$id"
        }
        val response = getResponse(URL(url))
        return if(response != null){
            gson.fromJson(response,Array<Genre>::class.java).toList()
        }else{
            Collections.emptyList()
        }

    }
    //CoroutineScope(IO)
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

}