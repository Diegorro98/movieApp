package es.usj.drodriguez.movieapp.database.dao

import androidx.room.*
import es.usj.drodriguez.movieapp.database.classes.Genre
import kotlinx.coroutines.flow.Flow

@Dao
interface GenreDao {
    @Query("INSERT INTO ${Genre.TABLE_NAME} (${Genre.NAME}, ${Genre.FAVORITE}) VALUES ('',0)")
    suspend fun insertNew(): Long
    @Transaction
    suspend fun getNew() = getByIDNoFlow(insertNew())

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(genre: Genre): Long
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMultiple(genres: List<Genre>)

    @Update
    suspend fun update(genre: Genre)
    @Update
    suspend fun updateAll(genres: List<Genre>)

    @Delete
    suspend fun delete(genre: Genre)

    @Query("UPDATE ${Genre.TABLE_NAME} SET ${Genre.FAVORITE} = :favorite WHERE ${Genre.ID} = :id")
    suspend fun setFavorite(id: Long, favorite: Boolean)

    @Query ("SELECT * FROM ${Genre.TABLE_NAME} ORDER BY ${Genre.NAME} ASC")
    fun getAll(): Flow<List<Genre>>
    @Query ("SELECT * FROM ${Genre.TABLE_NAME} WHERE ${Genre.FAVORITE} = 1 ORDER BY ${Genre.NAME} ASC")
    fun getFavorites(): Flow<List<Genre>>
    @Query("SELECT * FROM ${Genre.TABLE_NAME} WHERE ${Genre.ID} = :ID")
    fun getByID(ID: Long): Flow<Genre>
    @Query("SELECT * FROM ${Genre.TABLE_NAME} WHERE ${Genre.ID} in (:IDs)")
    fun getByID(IDs: List<Long>): Flow<List<Genre>>
    @Query("SELECT * FROM ${Genre.TABLE_NAME} WHERE ${Genre.ID} = :ID")
    suspend fun getByIDNoFlow(ID: Long): Genre
    @Query("SELECT * FROM ${Genre.TABLE_NAME} WHERE ${Genre.NAME} = :name")
    suspend fun getByNameNoFlow(name: String): Genre
}