package es.usj.drodriguez.movieapp.database.classes

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(tableName = MovieGenre.TABLE_NAME,
    primaryKeys = [MovieGenre.MOVIE_ID, MovieGenre.GENRE_ID],
    foreignKeys =
    [ForeignKey(entity = Movie::class,
        parentColumns = [Movie.ID],
        childColumns = [MovieGenre.MOVIE_ID],
        onDelete = ForeignKey.CASCADE
    ),
        ForeignKey(entity = Genre::class,
            parentColumns = [Genre.ID],
            childColumns = [MovieGenre.GENRE_ID],
            onDelete = ForeignKey.CASCADE
        )])
data class MovieGenre(
    @ColumnInfo(name = MOVIE_ID, index = true) var movieID: Long,
    @ColumnInfo(name = GENRE_ID, index = true) var genreID: Long
) {
    companion object {
        const val TABLE_NAME = "movies_genres"
        const val MOVIE_ID = "movie_id"
        const val GENRE_ID = "genre_id"
    }
}