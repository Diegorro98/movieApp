package es.usj.drodriguez.movieapp.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ActorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(actor: Actor)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(actors: List<Actor>)
    @Update
    fun update(actor: Actor)
    @Update
    fun updateAll(actors: List<Actor>)

    @Delete
    fun delete(actor: Actor)

    @Query ("SELECT * FROM ${Actor.TABLE_NAME} ORDER BY ${Actor.ID} ASC")
    fun getAll(): List<Actor>
    @Query ("SELECT * FROM ${Actor.TABLE_NAME} ORDER BY ${Actor.ID} ASC")
    fun getAllWithLiveData(): LiveData<List<Actor>>

    @Query("SELECT * FROM ${Actor.TABLE_NAME} WHERE ${Actor.ID} = :id")
    fun getByID(id: Int): Actor

}