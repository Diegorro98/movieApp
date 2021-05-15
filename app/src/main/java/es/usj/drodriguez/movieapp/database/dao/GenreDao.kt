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

    @Query("UPDATE ${Genre.TABLE_NAME} SET ${Genre.FAVORITE} = :favorite WHERE ${Genre.ID} = :id")
    suspend fun setFavorite(id: Int, favorite: Boolean)

    @Query ("SELECT * FROM ${Genre.TABLE_NAME} ORDER BY ${Genre.NAME} ASC")
    fun getAll(): Flow<List<Genre>>
    @Query("SELECT * FROM ${Genre.TABLE_NAME} WHERE ${Genre.ID} = :id")
    fun getByID(id: Int): Genre
}