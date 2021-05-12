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

    @Query ("SELECT * FROM ${Movie.TABLE_NAME} ORDER BY ${Movie.ID} ASC")
    fun getAll(): Flow<List<Movie>>

    @Query("SELECT * FROM ${Movie.TABLE_NAME} WHERE ${Movie.ID} = :id")
    fun getByID(id: Int): Movie

    @Query("SELECT * FROM ${Movie.TABLE_NAME} WHERE ${Movie.GENRES} LIKE :id")
    fun getActorsMovies(id: String): List<Movie>
}