package es.usj.drodriguez.movieapp.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(movie: Movie)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(movies: List<Movie>)
    @Update
    fun update(movie: Movie)
    @Update
    fun updateAll(movies: List<Movie>)
    @Delete
    fun delete(movie: Movie)

    @Query ("SELECT * FROM ${Movie.TABLE_NAME} ORDER BY ${Movie.ID} ASC")
    fun getAll(): List<Movie>
    @Query ("SELECT * FROM ${Movie.TABLE_NAME} ORDER BY ${Movie.ID} ASC")
    fun getAllWithLiveData(): LiveData<List<Movie>>

    @Query("SELECT * FROM ${Movie.TABLE_NAME} WHERE ${Movie.ID} = :id")
    fun getByID(id: Int): List<Movie>
}