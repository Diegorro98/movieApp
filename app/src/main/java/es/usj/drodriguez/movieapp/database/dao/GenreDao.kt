package es.usj.drodriguez.movieapp.database.dao

import androidx.room.*
import es.usj.drodriguez.movieapp.database.classes.Genre
import kotlinx.coroutines.flow.Flow

@Dao
interface GenreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(genre: Genre)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMultiple(genres: List<Genre>)
    @Update
    suspend fun update(genre: Genre)
    @Update
    suspend fun updateAll(genres: List<Genre>)
    @Delete
    suspend fun delete(genre: Genre)

    @Query ("SELECT * FROM ${Genre.TABLE_NAME} ORDER BY ${Genre.ID} ASC")
    fun getAll(): Flow<List<Genre>>

    @Query("SELECT * FROM ${Genre.TABLE_NAME} WHERE ${Genre.ID} = :id")
    fun getByID(id: Int): Flow<Genre>
}