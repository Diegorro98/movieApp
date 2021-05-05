package es.usj.drodriguez.movieapp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import es.usj.drodriguez.movieapp.database.classes.Genre

@Dao
interface GenreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(genre: Genre)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(genres: List<Genre>)
    @Update
    fun update(genre: Genre)
    @Update
    fun updateAll(genres: List<Genre>)
    @Delete
    fun delete(genre: Genre)

    @Query ("SELECT * FROM ${Genre.TABLE_NAME} ORDER BY ${Genre.ID} ASC")
    fun getAll(): List<Genre>
    @Query ("SELECT * FROM ${Genre.TABLE_NAME} ORDER BY ${Genre.ID} ASC")
    fun getAllWithLiveData(): LiveData<List<Genre>>

    @Query("SELECT * FROM ${Genre.TABLE_NAME} WHERE ${Genre.ID} = :id")
    fun getByID(id: Int): Genre
}