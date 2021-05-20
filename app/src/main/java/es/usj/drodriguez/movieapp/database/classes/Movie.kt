package es.usj.drodriguez.movieapp.database.classes

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.annotations.SerializedName
import java.io.File
import java.io.Serializable

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
        @ColumnInfo(name = POSTER) var posterPath: File?,
        @ColumnInfo(name = FAVORITE) var favorite: Boolean): Serializable{
    companion object{
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
    }
}
class MovieFetcher(
    @SerializedName(Movie.ID) val id: Long,
    @SerializedName(Movie.TITLE) var title: String,
    @SerializedName(Movie.DESCRIPTION) var description: String,
    @SerializedName(Movie.DIRECTOR) var director: String,
    @SerializedName(Movie.YEAR) var year: Int,
    @SerializedName(Movie.RUNTIME) var runtime: Int,
    @SerializedName(Movie.RATING) var rating: Float,
    @SerializedName(Movie.VOTES) var votes: Int,
    @SerializedName(Movie.REVENUE) var revenue: Float,
    @SerializedName(Movie.GENRES) var genres: List<Long>,
    @SerializedName(Movie.ACTORS)  var actors: List<Long>,
){
    constructor(normalMovie: Movie, genresOfMovie: List<MovieGenre>, actorsOfMovie: List<MovieActor>) : this(
        normalMovie.id,
        normalMovie.title,
        normalMovie.description,
        normalMovie.director,
        normalMovie.year,
        normalMovie.runtime,
        normalMovie.rating,
        normalMovie.votes,
        normalMovie.revenue,
        List(genresOfMovie.size) {
            genresOfMovie[it].genreID
        },
        List(actorsOfMovie.size) {
            actorsOfMovie[it].actorID
        }
    )
    fun asNormalMovie(movieToUpdate : Movie? =  null) = Movie(
        id,
        title,
        description,
        director,
        year,
        runtime,
        rating,
        votes,
        revenue,
        movieToUpdate?.posterPath,
        movieToUpdate?.favorite ?: false
    )
    fun asMovieGenres() = List(genres.size) {
        MovieGenre(id, genres[it])
    }
    fun asMovieActors() = List(genres.size) {
        MovieActor(id, actors[it])
    }

}
class FileConverter{
    @TypeConverter
    fun stringToFilePath(filePath: String?):File?{
        return filePath?.let{File(it)}
    }

    @TypeConverter
    fun filePathToString(Object: File?): String {
        return Object.let {it.toString()}
    }
}