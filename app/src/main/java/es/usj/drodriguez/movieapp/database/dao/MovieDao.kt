package es.usj.drodriguez.movieapp.database.dao

import androidx.room.*
import es.usj.drodriguez.movieapp.database.classes.Movie
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: Movie)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMultiple(movies: List<Movie>)
    @Update
    suspend fun update(movie: Movie)
    @Update
    suspend fun updateAll(movies: List<Movie>)
    @Delete
    suspend fun delete(movie: Movie)
    @Query("UPDATE ${Movie.TABLE_NAME} SET ${Movie.FAVORITE} = :favorite WHERE ${Movie.ID} = :id")
    suspend fun setFavorite(id: Int, favorite: Boolean)

    @Query ("SELECT * FROM ${Movie.TABLE_NAME} ORDER BY ${Movie.TITLE} ASC")
    fun getAll(): Flow<List<Movie>>

    @Query("SELECT * FROM ${Movie.TABLE_NAME} WHERE ${Movie.ID} = :id")
    fun getByID(id: Int): Movie

    @Query("SELECT * FROM ${Movie.TABLE_NAME} WHERE ${Movie.ACTORS} LIKE :actorID ORDER BY ${Movie.TITLE}")
    fun getActorMovies(actorID: String): List<Movie>
    @Query("SELECT * FROM ${Movie.TABLE_NAME} WHERE ${Movie.GENRES} LIKE :genreID ORDER BY ${Movie.TITLE}")
    fun getGenreMovies(genreID: String): List<Movie>
}