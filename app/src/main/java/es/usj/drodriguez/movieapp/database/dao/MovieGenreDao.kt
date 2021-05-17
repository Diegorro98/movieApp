package es.usj.drodriguez.movieapp.database.dao

import androidx.room.*
import es.usj.drodriguez.movieapp.database.classes.MovieGenre
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieGenreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movieGenre: MovieGenre)
    @Delete
    suspend fun delete(movieGenre: MovieGenre)

    @Query("SELECT * FROM ${MovieGenre.TABLE_NAME} WHERE ${MovieGenre.GENRE_ID} = :genreID ORDER BY ${MovieGenre.GENRE_ID}")
    fun getMovies(genreID: Int): Flow<List<MovieGenre>>
    @Query("SELECT * FROM ${MovieGenre.TABLE_NAME} WHERE ${MovieGenre.MOVIE_ID} = :movieID ORDER BY ${MovieGenre.MOVIE_ID}")
    fun getGenres(movieID: Int): Flow<List<MovieGenre>>

    @Query ("SELECT * FROM ${MovieGenre.TABLE_NAME}")
    fun getAll(): Flow<List<MovieGenre>>
}