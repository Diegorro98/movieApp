package es.usj.drodriguez.movieapp.database

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.google.gson.Gson
import es.usj.drodriguez.movieapp.database.*
import es.usj.drodriguez.movieapp.database.classes.*
import es.usj.drodriguez.movieapp.database.viewmodels.*
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
import java.nio.charset.StandardCharsets
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
    fun getMovies(id: Int? = null) : List<MovieFetcher>{
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
    fun getActors(id: Int? = null) : List<Actor>{
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
    fun getGenres(id: Int? = null) : List<Genre>{
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
    fun add(movie: MovieFetcher) = post(URL("http://$hostname:$port/user/addMovie.php?user=$user&pass=$password"), gson.toJson(movie))
    fun add(genre: Genre) = post(URL("http://$hostname:$port/user/addGenre.php?user=$user&pass=$password"), gson.toJson(genre))
    fun add(actor: Actor) = post(URL("http://$hostname:$port/user/addActor.php?user=$user&pass=$password"), gson.toJson(actor))

    fun update(movie: MovieFetcher) = post(URL("http://$hostname:$port/user/updateMovie.php?user=$user&pass=$password"), gson.toJson(movie))
    fun update(genre: Genre) = post(URL("http://$hostname:$port/user/updateGenre.php?user=$user&pass=$password"), gson.toJson(genre))
    fun update(actor: Actor) = post(URL("http://$hostname:$port/user/updateActor.php?user=$user&pass=$password"), gson.toJson(actor))

    fun delete(movie: Movie) = post(URL("http://$hostname:$port/user/deleteMovie.php?user=$user&pass=$password"), "{\"id\":\"${movie.id}}")
    fun delete(genre: Genre) = post(URL("http://$hostname:$port/user/deleteGenre.php?user=$user&pass=$password"), "{\"id\":\"${genre.id}}")
    fun delete(actor: Actor) = post(URL("http://$hostname:$port/user/deleteActor.php?user=$user&pass=$password"), "{\"id\":\"${actor.id}}")

    private fun post(url: URL, json:String):Boolean{
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "application/json; utf-8")
        connection.setRequestProperty("Accept", "application/json")
        val writer = DataOutputStream(connection.outputStream)
        writer.write(json.toByteArray(StandardCharsets.UTF_8))
        writer.flush()
        BufferedReader(InputStreamReader(connection.inputStream, "utf-8")).use { br ->
            val response = java.lang.StringBuilder()
            var responseLine: String?
            while (br.readLine().also { responseLine = it } != null) {
                response.append(responseLine!!.trim { it <= ' ' })
            }
            Log.i("response", response.toString())
        }
        writer.close()
        return true
    }
    suspend fun speakWithAPI(job: CompletableJob, application: Application){
        val repository = (application as DatabaseApp).repository
        withContext(IO + job){
            listOf(//delete
                launch {
                       deleted.movies.forEach {
                           delete(it)
                           deleted.movies.remove(it)
                       }
                },
                launch {
                    deleted.genres.forEach {
                        delete(it)
                        deleted.genres.remove(it)
                    }
                },
                launch {
                    deleted.actors.forEach {
                        delete(it)
                        deleted.actors.remove(it)
                    }
                }
            ).joinAll()
            listOf(//update
                launch {
                    updated.movies.forEach {
                        val genresIDs = mutableListOf<Long>()
                        repository.getMovieGenresNoFlow(it.id).forEach { genreID ->
                            genresIDs.add(genreID)
                        }
                        val actorsIDs = mutableListOf<Long>()
                        repository.getMovieActorsNoFlow(it.id).forEach { actorID ->
                            actorsIDs.add(actorID)
                        }
                        val movie = MovieFetcher(it,genresIDs.toList(),actorsIDs)
                        update(movie)
                        updated.movies.remove(it)
                    }
                },
                launch {
                    updated.genres.forEach {
                        update(it)
                        updated.genres.remove(it)
                    }
                },
                launch {
                    updated.actors.forEach {
                        update(it)
                        updated.actors.remove(it)
                    }
                },
            ).joinAll()
            listOf(//add
                launch {
                    added.movies.forEach {
                        val genresIDs = mutableListOf<Long>()
                        repository.getMovieGenresNoFlow(it.id).forEach { genreID ->
                            genresIDs.add(genreID)
                        }
                        val actorsIDs = mutableListOf<Long>()
                        repository.getMovieActorsNoFlow(it.id).forEach { actorID ->
                            actorsIDs.add(actorID)
                        }
                        val movie = MovieFetcher(it,genresIDs.toList(),actorsIDs)
                        add(movie)
                        added.movies.remove(it)
                    }
                },
                launch {
                    added.genres.forEach {
                        add(it)
                        added.genres.remove(it)
                    }
                },
                launch {
                    added.actors.forEach {
                        add(it)
                        added.actors.remove(it)
                    }
                },
            ).joinAll()
            var rawMovies = emptyList<MovieFetcher>()
            listOf(//Download
                launch{
                    rawMovies = getMovies()
                    val storedMovies = repository.getAllMovies()
                    downloaded.movies = MutableList(rawMovies.size){
                        rawMovies[it].asNormalMovie()
                    }
                    storedMovies.forEach { storedMovie ->
                        if (storedMovie.favorite || storedMovie.posterURL != null) {
                            downloaded.movies.forEach { downloadedMovie ->
                                if (storedMovie.year == downloadedMovie.year && storedMovie.title == downloadedMovie.title){
                                    downloadedMovie.favorite = storedMovie.favorite
                                    downloadedMovie.posterURL = storedMovie.posterURL
                                }
                            }
                        }

                    }
                },
                launch {
                    val storedActors = repository.getAllActors()
                    downloaded.actors = getActors().toMutableList()
                    storedActors.forEach { storedActor ->
                        if (storedActor.favorite) {
                            downloaded.actors.forEach { downloadedActor ->
                                if (storedActor.name == downloadedActor.name){
                                    downloadedActor.favorite = true
                                }
                            }
                        }
                    }
                },
                launch {
                    val storedGenres = repository.getAllGenres()
                    downloaded.genres = getGenres().toMutableList()
                    storedGenres.forEach { storedGenre ->
                        if (storedGenre.favorite) {
                            downloaded.genres.forEach { downloadedGenre ->
                                if (storedGenre.name == downloadedGenre.name){
                                    downloadedGenre.favorite = true
                                }
                            }
                        }
                    }
                }
            ).joinAll()
            repository.nuke()
            listOf(//insertProcessed
                launch {
                    repository.insertMovies(downloaded.movies)
                },/*
                launch{
                    rawMovies.forEach { movie ->
                        movie.asMovieActors().forEach {
                            repository.insertMovieActor(it)
                        }
                        movie.asMovieGenres().forEach {
                            repository.insertMovieGenre(it)
                        }
                    }
                },*/
                launch {
                    repository.insertActors(downloaded.actors)
                },
                launch {
                    repository.insertGenres(downloaded.genres)
                }
            ).joinAll()
            rawMovies.forEach { movie ->
                listOf(//insertProcessed
                    launch {
                        movie.asMovieActors().forEach {
                            repository.insertMovieActor(it)
                        }
                    },
                    launch {
                        movie.asMovieGenres().forEach {
                            repository.insertMovieGenre(it)
                        }
                    }
                )
            }
            println("All ended")
        }
    }
    class FetchClass {
        var actors = mutableListOf<Actor>()
        var genres = mutableListOf<Genre>()
        var movies = mutableListOf<Movie>()
    }
    companion object{
        val deleted = FetchClass()
        val updated = FetchClass()
        val added = FetchClass()
        val downloaded = FetchClass()
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
            val manager : FragmentManager = (context as FragmentActivity).supportFragmentManager
            CoroutineScope(IO + job).launch {
                while (!endedFetcher) {
                    if (DatabasePreferences(context).isOnline(Context.MODE_PRIVATE)) {
                        fetcher = DatabaseFetcher(host, "admin", "admin")
                        onPing?.invoke()
                        if (fetcher.ping()) {
                            onUpdate?.invoke()
                            fetcher.speakWithAPI(job, application)
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
        fun getResponse(url: URL): String? {
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            return try {
                val input: InputStream
                input = BufferedInputStream(urlConnection.inputStream)
                readStream(input)
            } catch (e: Exception) {
                println(e.printStackTrace().toString())
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
        const val OMDbAPIKey = "81d0dc32"
    }
}