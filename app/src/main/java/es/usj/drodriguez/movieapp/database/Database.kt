package es.usj.drodriguez.movieapp.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import es.usj.drodriguez.movieapp.database.classes.*
import es.usj.drodriguez.movieapp.database.dao.*


@androidx.room.Database(entities = [Movie::class, Actor::class, Genre::class, MovieActor::class, MovieGenre::class], version = 1, exportSchema = false)
@TypeConverters(FileConverter::class)
abstract class Database : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun actorDao(): ActorDao
    abstract fun genreDao(): GenreDao
    abstract fun movieActorDao(): MovieActorDao
    abstract fun movieGenreDao(): MovieGenreDao
    companion object {
        @Volatile
        private var INSTANCE: Database? = null

        fun getDatabase(context: Context): Database {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        Database::class.java,
                        "movie_database"
                    ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}