package es.usj.drodriguez.movieapp.database.dao

import androidx.room.*
import es.usj.drodriguez.movieapp.database.classes.Movie
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query("INSERT INTO ${Movie.TABLE_NAME}  (${Movie.TITLE}, ${Movie.DESCRIPTION}, ${Movie.DIRECTOR}, ${Movie.YEAR}, ${Movie.RUNTIME}, ${Movie.RATING}, ${Movie.VOTES}, ${Movie.REVENUE}, ${Movie.FAVORITE})VALUES ('','','',-1,-1,-1,-1,-1,0)")
    suspend fun insertNew(): Long
    @Transaction
    suspend fun getNew() = getByIDNoFlow(insertNew())

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
    suspend fun setFavorite(id: Long, favorite: Boolean)
    @Query("UPDATE ${Movie.TABLE_NAME} SET ${Movie.POSTER} = :url WHERE ${Movie.ID} = :id")
    suspend fun setPosterURL(id: Long, url: String)

    @Query ("SELECT * FROM ${Movie.TABLE_NAME} ORDER BY ${Movie.TITLE} ASC")
    fun getAll(): Flow<List<Movie>>
    @Query ("SELECT * FROM ${Movie.TABLE_NAME} WHERE ${Movie.FAVORITE} = 1 ORDER BY ${Movie.TITLE} ASC")
    fun getFavorites(): Flow<List<Movie>>
    @Query("SELECT * FROM ${Movie.TABLE_NAME} WHERE ${Movie.ID} = :ID")
    fun getByID(ID: Long): Flow<Movie>
    @Query("SELECT * FROM ${Movie.TABLE_NAME} WHERE ${Movie.ID} in (:IDs)")
    fun getByID(IDs: List<Long>): Flow<List<Movie>>
    @Query("SELECT * FROM ${Movie.TABLE_NAME} WHERE ${Movie.ID} = :ID")
    suspend fun getByIDNoFlow(ID: Long): Movie
}