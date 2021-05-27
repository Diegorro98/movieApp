package es.usj.drodriguez.movieapp.database.dao

import androidx.room.*
import es.usj.drodriguez.movieapp.database.classes.Actor
import kotlinx.coroutines.flow.Flow

@Dao
interface ActorDao {
    @Query("INSERT INTO ${Actor.TABLE_NAME} (${Actor.NAME}, ${Actor.FAVORITE}) VALUES ('',0)")
    suspend fun insertNew(): Long
    @Transaction
    suspend fun getNew() = getByIDNoFlow(insertNew())

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(actor: Actor): Long
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMultiple(actors: List<Actor>)
    
    @Update
    suspend fun update(actor: Actor)
    @Update
    suspend fun updateAll(actors: List<Actor>)

    @Delete
    suspend fun delete(actor: Actor)
    @Query("DELETE FROM ${Actor.TABLE_NAME}")
    suspend fun nukeTable()

    @Query("UPDATE ${Actor.TABLE_NAME} SET ${Actor.FAVORITE} = :favorite WHERE ${Actor.ID} = :id")
    suspend fun setFavorite(id: Long, favorite: Boolean)

    @Query ("SELECT * FROM ${Actor.TABLE_NAME} ORDER BY ${Actor.NAME} ASC")
    fun getAll(): Flow<List<Actor>>
    @Query ("SELECT * FROM ${Actor.TABLE_NAME} ORDER BY ${Actor.NAME} ASC")
    suspend fun getAllNoFlow(): List<Actor>
    @Query ("SELECT * FROM ${Actor.TABLE_NAME} WHERE ${Actor.FAVORITE} = 1 ORDER BY ${Actor.NAME} ASC")
    fun getFavorites(): Flow<List<Actor>>
    @Query("SELECT * FROM ${Actor.TABLE_NAME} WHERE ${Actor.ID} = :ID")
    fun getByID(ID: Long): Flow<Actor>
    @Query("SELECT * FROM ${Actor.TABLE_NAME} WHERE ${Actor.ID} in (:IDs)")
    fun getByID(IDs: List<Long>): Flow<List<Actor>>
    @Query("SELECT * FROM ${Actor.TABLE_NAME} WHERE ${Actor.ID} = :ID")
    suspend fun getByIDNoFlow(ID: Long): Actor
    @Query("SELECT * FROM ${Actor.TABLE_NAME} WHERE ${Actor.NAME} = :name")
    suspend fun getByNameNoFlow(name: String): Actor

}