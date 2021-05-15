package es.usj.drodriguez.movieapp.database.dao

import androidx.room.*
import es.usj.drodriguez.movieapp.database.classes.Actor
import kotlinx.coroutines.flow.Flow

@Dao
interface ActorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(actor: Actor)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMultiple(actors: List<Actor>)
    
    @Update
    suspend fun update(actor: Actor)
    @Update
    suspend fun updateAll(actors: List<Actor>)

    @Delete
    suspend fun delete(actor: Actor)

    @Query("UPDATE ${Actor.TABLE_NAME} SET ${Actor.FAVORITE} = :favorite WHERE ${Actor.ID} = :id")
    suspend fun setFavorite(id: Int, favorite: Boolean)

    @Query ("SELECT * FROM ${Actor.TABLE_NAME} ORDER BY ${Actor.NAME} ASC")
    fun getAll(): Flow<List<Actor>>
    @Query("SELECT * FROM ${Actor.TABLE_NAME} WHERE ${Actor.ID} = :id")
    fun getByID(id: Int): Actor
}