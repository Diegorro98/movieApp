package es.usj.drodriguez.movieapp.database.dao

import androidx.room.*
import es.usj.drodriguez.movieapp.database.classes.MovieActor
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieActorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movieActor: MovieActor)
    @Delete
    suspend fun delete(movieActor: MovieActor)
    @Query("DELETE FROM ${MovieActor.TABLE_NAME}")
    suspend fun nukeTable()

    @Query("SELECT ${MovieActor.MOVIE_ID} FROM ${MovieActor.TABLE_NAME} WHERE ${MovieActor.ACTOR_ID} = :actorID ORDER BY ${MovieActor.ACTOR_ID} ASC")
    fun getMovies(actorID: Long): Flow<List<Long>>
    @Query("SELECT ${MovieActor.ACTOR_ID} FROM ${MovieActor.TABLE_NAME} WHERE ${MovieActor.MOVIE_ID} = :movieID ORDER BY ${MovieActor.MOVIE_ID} ASC")
    fun getActors(movieID: Long): Flow<List<Long>>
    @Query("SELECT ${MovieActor.ACTOR_ID} FROM ${MovieActor.TABLE_NAME} WHERE ${MovieActor.MOVIE_ID} = :movieID ORDER BY ${MovieActor.MOVIE_ID} ASC")
    suspend fun getActorsNoFlow(movieID: Long): List<Long>

    @Query ("SELECT * FROM ${MovieActor.TABLE_NAME}")
    fun getAll(): Flow<List<MovieActor>>
    @Query ("SELECT * FROM ${MovieActor.TABLE_NAME}")
    suspend fun getAllNoFlow(): List<MovieActor>
}