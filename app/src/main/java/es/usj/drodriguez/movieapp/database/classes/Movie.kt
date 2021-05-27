package es.usj.drodriguez.movieapp.database.classes

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import es.usj.drodriguez.movieapp.database.DatabaseFetcher
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Job
import java.io.Serializable
import java.net.URL


@Entity(tableName = Movie.TABLE_NAME)
data class Movie(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = ID) @SerializedName(ID) val id: Long,
    @ColumnInfo(name = TITLE) var title: String,
    @ColumnInfo(name = DESCRIPTION) var description: String,
    @ColumnInfo(name = DIRECTOR) var director: String,
    @ColumnInfo(name = YEAR) var year: Int,
    @ColumnInfo(name = RUNTIME) var runtime: Int,
    @ColumnInfo(name = RATING) var rating: Float,
    @ColumnInfo(name = VOTES) var votes: Int,
    @ColumnInfo(name = REVENUE) var revenue: Float,
    @ColumnInfo(name = POSTER) var posterURL: String?,
    @ColumnInfo(name = FAVORITE) var favorite: Boolean): Serializable{

    fun getPosterURLFromOMDbAPI() = askAPI()?.poster

    @Suppress("BlockingMethodInNonBlockingContext")
    fun askAPI(): OMDbMovie? {
        val response = DatabaseFetcher.getResponse(URL("http://www.omdbapi.com/?apikey=${DatabaseFetcher.OMDbAPIKey}&type=movie&plot=full&r=json&t=${title.replace(' ','+')}"))
        return gson.fromJson(response, OMDbMovie::class.java)
    }

    class OMDbMovie(
        @SerializedName("Title") var title: String,
        @SerializedName("Plot") var plot: String,
        @SerializedName("Director") var director: String,
        @SerializedName("Year") var year: Int,
        @SerializedName("Runtime") var runtime: String,
        @SerializedName("imdbRating") var rating: String,
        @SerializedName("imdbVotes") var votes: String,
        @SerializedName("Genre") var genres: String,
        @SerializedName("BoxOffice") var boxOffice: String,
        @SerializedName("Actors")  var actors: String,
        @SerializedName("Poster")  var poster: String,
    )

    companion object{
        private val gson = Gson()
        const val ID = "id"
        const val TITLE = "title"
        const val DESCRIPTION ="description"
        const val DIRECTOR ="director"
        const val YEAR = "year"
        const val RUNTIME = "runtime"
        const val RATING = "rating"
        const val VOTES = "votes"
        const val REVENUE = "revenue"
        const val GENRES = "genres"
        const val ACTORS = "actors"
        const val TABLE_NAME = "movies"
        const val POSTER = "poster"
        const val FAVORITE = "favorite"
        const val MOVIE_NOT_FOUND = "movie not found"
        const val MIN_YEAR = 1900
        val posterFetcherJob: CompletableJob = Job()
    }
}
class MovieFetcher(
    @SerializedName(Movie.ID) val id: Long,
    @SerializedName(Movie.TITLE) var title: String,
    @SerializedName(Movie.DESCRIPTION) var plot: String,
    @SerializedName(Movie.DIRECTOR) var director: String,
    @SerializedName(Movie.YEAR) var year: Int,
    @SerializedName(Movie.RUNTIME) var runtime: Int,
    @SerializedName(Movie.RATING) var rating: Float,
    @SerializedName(Movie.VOTES) var votes: Int,
    @SerializedName(Movie.REVENUE) var revenue: Float,
    @SerializedName(Movie.GENRES) var genres: List<Long>,
    @SerializedName(Movie.ACTORS)  var actors: List<Long>,
){
    constructor(normalMovie: Movie, genresOfMovie: List<Long>, actorsOfMovie: List<Long>) : this(
        normalMovie.id,
        normalMovie.title,
        normalMovie.description,
        normalMovie.director,
        normalMovie.year,
        normalMovie.runtime,
        normalMovie.rating,
        normalMovie.votes,
        normalMovie.revenue,
        genresOfMovie,
        actorsOfMovie
    )
    fun asNormalMovie(movieToUpdate : Movie? =  null) = Movie(
        id,
        title,
        plot,
        director,
        year,
        runtime,
        rating,
        votes,
        revenue,
        movieToUpdate?.posterURL,
        movieToUpdate?.favorite ?: false
    )
    fun asMovieGenres() = if(genres != null )List(genres.size) {
        MovieGenre(id, genres[it])
    } else emptyList()
    fun asMovieActors() = if(actors != null) List(actors.size) {
        MovieActor(id, actors[it])
    } else emptyList()

}