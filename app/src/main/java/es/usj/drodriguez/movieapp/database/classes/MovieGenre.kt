package es.usj.drodriguez.movieapp.database.classes

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = MovieGenre.TABLE_NAME, primaryKeys = [MovieGenre.MOVIE_ID, MovieGenre.GENRE_ID])
data class MovieGenre(
    @ColumnInfo(name = MOVIE_ID) var movieID: Int,
    @ColumnInfo(name = GENRE_ID) var genreID: Int
) {
    companion object {
        const val TABLE_NAME = "movies_genres"
        const val MOVIE_ID = "movie_id"
        const val GENRE_ID = "genre_id"
    }
}