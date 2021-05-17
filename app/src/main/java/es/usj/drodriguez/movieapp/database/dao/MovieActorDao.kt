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

    @Query("SELECT * FROM ${MovieActor.TABLE_NAME} WHERE ${MovieActor.ACTOR_ID} = :actorID ORDER BY ${MovieActor.ACTOR_ID} ASC")
    fun getMovies(actorID: Int): Flow<List<MovieActor>>
    @Query("SELECT * FROM ${MovieActor.TABLE_NAME} WHERE ${MovieActor.MOVIE_ID} = :movieID ORDER BY ${MovieActor.MOVIE_ID} ASC")
    fun getActors(movieID: Int): Flow<List<MovieActor>>

    @Query ("SELECT * FROM ${MovieActor.TABLE_NAME}")
    fun getAll(): Flow<List<MovieActor>>
}